package com.example.scanqrapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.scanqrapp.databinding.ActivityForgotPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {
    private AppCompatEditText emailEditText;
    private ProgressBar progressBar;

    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.scanqrapp.databinding.ActivityForgotPasswordBinding binding = ActivityForgotPasswordBinding
                .inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //add nav graph/ nav host to default view
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_graph);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
        }

        emailEditText = (AppCompatEditText) findViewById(R.id.reset_password_email);
        AppCompatButton resetPasswordButton = (AppCompatButton) findViewById(R.id.reset_passwordBtn);
        progressBar = (ProgressBar) findViewById(R.id.indeterminateBar);

        auth = FirebaseAuth.getInstance();

        binding.viewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
            }
        });

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }

            private void resetPassword() {
                String reset_password_email = Objects.requireNonNull(emailEditText.getText()).toString().trim();

                if(reset_password_email.isEmpty()){
                    emailEditText.setError("email field is empty");
                    emailEditText.requestFocus();
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(reset_password_email).matches()){
                    emailEditText.setError("valid email is required");
                    emailEditText.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                auth.sendPasswordResetEmail(reset_password_email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ForgotPasswordActivity.this, "check your email to reset your password", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this, "Try again, something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


    }





}