package com.example.scanqrapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.scanqrapp.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView register, forgot_password;
    private EditText editEmail, editPassword;
    private AppCompatButton loginBtn;

    private FirebaseAuth mAuth;

    private ProgressBar indeterminateBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.scanqrapp.databinding.ActivityLoginBinding binding = ActivityLoginBinding
                .inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //set register on click
        register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(this);

        //set forgot password
        forgot_password = (TextView) findViewById(R.id.forgot_password);
        forgot_password.setOnClickListener(this);

        //set onClick listener for login butter
        loginBtn = (AppCompatButton) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);

        editEmail = (EditText) findViewById(R.id.input_email);
        editPassword = (EditText) findViewById(R.id.input_password);

        indeterminateBar = (ProgressBar) findViewById(R.id.indeterminateBar);

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View view) {
        //remember it is a better practice to use if statement
        switch (view.getId()){
            case R.id.register:
                //onclick sent to RegisterActivicty
                startActivity(new Intent(this, SignUpActivity.class));
                break;

            case R.id.loginBtn:
                userLogin();
                break;

            case R.id.forgot_password:
               // startActivity(new Intent(this, ForgotPassword.class));
                break;
        }

    }

    private void userLogin() {
        String input_email = editEmail.getText().toString().trim();
        String input_password = editPassword.getText().toString().trim();

        if(input_email.isEmpty()){
            editEmail.setError(" email is required! ");
            editEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(input_email).matches()){
            editEmail.setError("input a valid email");
            editEmail.requestFocus();
            return;

        }

        if (input_password.isEmpty()){
            editPassword.setError("password is empty");
            editPassword.requestFocus();
            return;
        }

        if(input_password.length() < 6) {
            editPassword.setError("min password is 6 characters");
            editPassword.requestFocus();
            return;

        }

        indeterminateBar.setVisibility(View.GONE);
        mAuth.signInWithEmailAndPassword(input_email, input_password)
                .addOnCompleteListener(task -> {

                    if(task.isSuccessful()){

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user.isEmailVerified()){
                            // redirect to profile
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        } else {
                            user.sendEmailVerification();
                            Toast.makeText(LoginActivity.this, "check your email to verify this account", Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        Toast.makeText(LoginActivity.this, "Login failed! ", Toast.LENGTH_SHORT).show();
                    }

                });
    }
}