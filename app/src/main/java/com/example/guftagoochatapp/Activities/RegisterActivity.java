package com.example.guftagoochatapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guftagoochatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    TextInputLayout username,email,password;
    Button btn_register,phone_register;

    FirebaseAuth auth;
    DatabaseReference reference;

    TextInputLayout phoneNumber;

    TextView dummy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FirebaseApp.initializeApp(RegisterActivity.this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_register = findViewById(R.id.btn_register);
        //phone_register = findViewById(R.id.phone_register);
        dummy = findViewById(R.id.dummy);
        //phoneNumber = findViewById(R.id.phoneNumber);

        dummy.requestFocus();
        dummy.setFocusableInTouchMode(true);

        auth = FirebaseAuth.getInstance();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text_username = Objects.requireNonNull(username.getEditText()).getText().toString();
                String text_email = Objects.requireNonNull(email.getEditText()).getText().toString();
                String text_password = Objects.requireNonNull(password.getEditText()).getText().toString();

                if(TextUtils.isEmpty(text_username) || TextUtils.isEmpty(text_email) || TextUtils.isEmpty(text_password)){
                    Toast.makeText(RegisterActivity.this,"All fields are required !!!",Toast.LENGTH_SHORT).show();
                } else if(text_password.length() < 6){
                    Toast.makeText(RegisterActivity.this,"Password isn't strong enough. It must be at-least 6 characters long.",Toast.LENGTH_SHORT).show();
                } else{
                    register(text_username,text_email,text_password);
                }
            }
        });

        /*phone_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phonenumber = Objects.requireNonNull(phoneNumber.getEditText()).getText().toString();

                if(phonenumber.isEmpty() || phonenumber.length()<10){
                    Toast.makeText(RegisterActivity.this, "Valid number is required with 10 digits.", Toast.LENGTH_SHORT).show();
                } else{
                    String number = "+91" + phonenumber;
                    Intent intent = new Intent(RegisterActivity.this, PhoneRegister.class);
                    intent.putExtra("phonenumber", number);
                    startActivity(intent);
                }
            }
        });*/
    }

    private void register(final String username, String email, String password){
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            String userid = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                            HashMap<String,String> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username", username);
                            hashMap.put("imageURL", "default");
                            hashMap.put("status", "offline");
                            hashMap.put("search", username.toLowerCase());

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Intent i = new Intent(RegisterActivity.this,MainActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        finish();
                                    }
                                }
                            });
                        }else {
                            Toast.makeText(RegisterActivity.this,"You can't register with this email/password. Try again with something new.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
