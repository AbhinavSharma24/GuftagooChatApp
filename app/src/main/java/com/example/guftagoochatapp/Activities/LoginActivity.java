package com.example.guftagoochatapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guftagoochatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout email,password;
    Button btn_login;
    TextView dummy;

    TextInputLayout phoneNumber,otpSent;
    Button btn_phone_login;

    FirebaseAuth auth;

    TextView forget_password;

    String verificationCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(LoginActivity.this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_login = findViewById(R.id.btn_login);
        forget_password = findViewById(R.id.forget_password);
        /*btn_phone_login = findViewById(R.id.btn_phone_login);
        phoneNumber = findViewById(R.id.phoneNumber);*/
        otpSent = findViewById(R.id.otpSent);
        dummy = findViewById(R.id.dummy);

        dummy.requestFocus();
        dummy.setFocusableInTouchMode(true);

        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text_email = Objects.requireNonNull(email.getEditText()).getText().toString();
                String text_password = Objects.requireNonNull(password.getEditText()).getText().toString();

                if(text_email.equals("") || text_password.equals("")){
                    Toast.makeText(LoginActivity.this,"All fields are required !!!",Toast.LENGTH_SHORT).show();
                }else {
                    auth.signInWithEmailAndPassword(text_email,text_password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Intent i = new Intent(LoginActivity.this,MainActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        finish();
                                    } else{
                                        Toast.makeText(LoginActivity.this,"Authentication Failed !!!",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        /*btn_phone_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phonenumber = Objects.requireNonNull(phoneNumber.getEditText()).getText().toString();

                if(phonenumber.isEmpty() || phonenumber.length()<10){
                    Toast.makeText(LoginActivity.this, "Valid number is required with 10 digits.", Toast.LENGTH_SHORT).show();
                } else{
                    String number = "+91" + phonenumber;
                    sendVerificationCode(number);
                    *//*Intent intent = new Intent(LoginActivity.this, PhoneLogin.class);
                    intent.putExtra("phonenumber", number);
                    startActivity(intent);*//*
                    otpSent.setVisibility(View.VISIBLE);
                    Objects.requireNonNull(otpSent.getEditText()).setVisibility(View.VISIBLE);

                    btn_phone_login.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String otp = Objects.requireNonNull(otpSent.getEditText()).getText().toString();
                            if(otp.isEmpty() || otp.length()<6){
                                Toast.makeText(LoginActivity.this, "Enter valid OTP to continue", Toast.LENGTH_SHORT).show();
                            }
                            //verifyCode(otp);
                            final PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode,otp);
                            SignInWithCredential(credential);
                        }
                    });
                }
            }
        });*/
    }

    private void SignInWithCredential(PhoneAuthCredential credential) {

        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent i = new Intent(LoginActivity.this,MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();
                        } else{
                            Toast.makeText(LoginActivity.this,"Authentication Failed !!!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    /*private void sendVerificationCode(String number){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallback
        );
    }*/

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                    String code = phoneAuthCredential.getSmsCode();
                    if(code != null){
                        Toast.makeText(LoginActivity.this, "Verification Completed", Toast.LENGTH_SHORT).show();
                        Objects.requireNonNull(otpSent.getEditText()).setText(code);
                        //verifyCode(code);
                        SignInWithCredential(phoneAuthCredential);
                    }
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    verificationCode = s;
                    Toast.makeText(LoginActivity.this, "OTP Sent", Toast.LENGTH_SHORT).show();
                }
            };
}
