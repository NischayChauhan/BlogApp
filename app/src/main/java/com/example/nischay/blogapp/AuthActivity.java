package com.example.nischay.blogapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class AuthActivity extends AppCompatActivity {

    EditText m_user_input_phone,m_user_input_code;
    Button m_go_btn;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks m_callbacks;
    String verification_code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        FirebaseApp.initializeApp(this);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            userIsLoggedIn();
        }

        init_variables();
        Log.e("Auth Acitivity","Start sign in auth");

        m_go_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_user_input_code.getText().toString().isEmpty())
                    startPhoneVerification();
                else
                    verifyCred();
            }
        });

        m_callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                // Because kuch phone number aise hote h jo google pe phle se verified hote h
                signInWithPhoneAuthCred(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(getApplicationContext(),"FAILED",Toast.LENGTH_SHORT).show();
                Log.e("Auth Activity",e.getMessage());
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verification_code = s;
                m_go_btn.setText("Verify");
            }
        };



    }

    private void verifyCred() {
        Log.e("Verification Code",verification_code);
        Log.e("User Code",m_user_input_code.getText().toString());

        PhoneAuthCredential cred = PhoneAuthProvider.getCredential(verification_code,m_user_input_code.getText().toString());
        signInWithPhoneAuthCred(cred);
    }

    private void signInWithPhoneAuthCred(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    userIsLoggedIn();
                }else{
                    Toast.makeText(getApplicationContext(),"Login Unsuccessfull "+verification_code+" , "+m_user_input_code.getText().toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void userIsLoggedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
            return;
        }
    }

    private void startPhoneVerification() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(m_user_input_phone.getText().toString(),60, TimeUnit.SECONDS,this,m_callbacks);
    }

    private void init_variables() {
        m_user_input_phone = (EditText) findViewById(R.id.user_input_phone);
        m_user_input_code = (EditText) findViewById(R.id.user_input_code);
        m_go_btn = (Button) findViewById(R.id.go_btn);
    }


}
