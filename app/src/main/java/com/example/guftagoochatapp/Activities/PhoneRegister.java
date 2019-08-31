package com.example.guftagoochatapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guftagoochatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PhoneRegister extends AppCompatActivity {

    String verificationCode = "";
    TextInputLayout otpSent;

    TextInputLayout username;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_register);

        auth = FirebaseAuth.getInstance();

        String phoneNumber = getIntent().getStringExtra("phonenumber");
        sendVerificationCode(phoneNumber);

        otpSent = findViewById(R.id.otpSent);
        username = findViewById(R.id.username);
        TextView dummy = findViewById(R.id.dummy);

        dummy.requestFocus();
        dummy.setFocusableInTouchMode(true);

        findViewById(R.id.btn_phone_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = Objects.requireNonNull(otpSent.getEditText()).getText().toString();
                if(otp.isEmpty() || otp.length()<6){
                    Toast.makeText(PhoneRegister.this, "Enter valid OTP to continue", Toast.LENGTH_SHORT).show();
                }
                verifyCode(otp);
            }
        });
    }

    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {

        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            assert firebaseUser != null;
                            String userid = firebaseUser.getUid();

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                            String user = Objects.requireNonNull(username.getEditText()).getText().toString();

                            HashMap<String,String> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username", user);
                            hashMap.put("imageURL", "default");
                            hashMap.put("status", "offline");
                            hashMap.put("search", user.toLowerCase());

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Intent i = new Intent(PhoneRegister.this,MainActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        finish();
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(PhoneRegister.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void sendVerificationCode(String number){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallback
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if(code != null){
                Toast.makeText(PhoneRegister.this, "Verification Completed", Toast.LENGTH_SHORT).show();
                Objects.requireNonNull(otpSent.getEditText()).setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(PhoneRegister.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationCode = s;
        }
    };
}
