package com.example.scanqrapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.scanqrapp.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText editEmail, editPassword;

    private FirebaseAuth mAuth;

    private ProgressBar indeterminateBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.scanqrapp.databinding.ActivityLoginBinding binding = ActivityLoginBinding
                .inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //add nav graph/ nav host to default view
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_graph);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
        }

        //set register on click
        TextView register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(this);

        //set forgot password
        TextView forgot_password = (TextView) findViewById(R.id.forgot_password);
        forgot_password.setOnClickListener(this);

        //set onClick listener for login butter
        AppCompatButton loginBtn = (AppCompatButton) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);

        editEmail = (EditText) findViewById(R.id.input_email);
        editPassword = (EditText) findViewById(R.id.input_password);

        indeterminateBar = (ProgressBar) findViewById(R.id.indeterminateBar);

        mAuth = FirebaseAuth.getInstance();

        //Navigation.findNavController(findViewById(R.id.view_back)).popBackStack();




    }

    @SuppressLint("NonConstantResourceId")
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
                startActivity(new Intent(this, ForgotPasswordActivity.class));
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
                        assert user != null;
                        if (user.isEmailVerified()){
                            // redirect to profile
                            Log.d(TAG, String.valueOf(user.getMetadata()));
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