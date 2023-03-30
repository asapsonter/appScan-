package com.example.scanqrapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.scanqrapp.databinding.ActivitySignUpBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {


    private AppCompatButton user_regBtn;
    private EditText editTextfullName;
    private EditText editEmail;
    private EditText editPassword;
    private ProgressBar progressBar;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.scanqrapp.databinding.ActivitySignUpBinding binding = ActivitySignUpBinding
                .inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //add nav graph/ nav host to default view
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_graph);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
        }

        mAuth = FirebaseAuth.getInstance();

        //implement user reg btn
        user_regBtn = (AppCompatButton) findViewById(R.id.user_regBtn);
        user_regBtn.setOnClickListener(this);


        editTextfullName = (EditText) findViewById(R.id.input_name);
        editEmail = (EditText) findViewById(R.id.reg_email);
        editPassword = (EditText) findViewById(R.id.reg_pass);

        progressBar = (ProgressBar) findViewById(R.id.indeterminateBar2);

        binding.viewBack.setOnClickListener(view -> startActivity(new Intent(SignUpActivity.this, LoginActivity.class)));
    }


   @Override
    public void onClick(View view) {
       try{
            if (view.getId() == R.id.user_regBtn) {
                registerUser();
                startActivity(new Intent(this, LoginActivity.class));
            }else {
                // Toast if user is unable to register account
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SignUpActivity.this,"Try again Later",Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
           e.printStackTrace();

           Log.d("debug", "onClick: ");
           // startActivity(new Intent(SignUpActivity.this, SignUpActivity.class));

        }

    }

    private void registerUser() {
        String input_name = Objects.requireNonNull(editTextfullName.getText()).toString().trim();
        String reg_pass = Objects.requireNonNull(editPassword.getText()).toString().trim();
        String reg_email = Objects.requireNonNull(editEmail.getText()).toString().trim();

        //if empty name field
        if(input_name.isEmpty()) {
            editTextfullName.setError("full name is required! ");
            editTextfullName.requestFocus();

            return;
        }

        //if empty password
        if(reg_pass.isEmpty()){
            editPassword.setError("password is required! ");
            editPassword.requestFocus();

            return;
        }

        //less than 6 digits password

        if (reg_pass.length() < 6){
            editPassword.setError("pls input not less than 6 digits! ");
            editPassword.requestFocus();

            return;

        }
//if email feild is empty
        if (reg_email.isEmpty()){
            editEmail.setError("email is required! ");
            editEmail.requestFocus();

            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(reg_email).matches()) {
            editEmail.setError("pls provide valid email");
            editEmail.requestFocus();
            return;
        }
        try {
            progressBar.setVisibility(View.GONE);
            mAuth.createUserWithEmailAndPassword(reg_email, reg_pass)
                    .addOnCompleteListener(task -> {


                        if (task.isSuccessful()) {

                            User user = new User(input_name, reg_email);




                            // FirebaseDatabase.getInstance("https://scanqrapp-1db9d-default-rtdb.firebaseio.com")
                            FirebaseDatabase.getInstance(BuildConfig.firebaseAPI)
                                    .getReference("Users")
                                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                    .setValue(user)
                                    .addOnCompleteListener(task1 -> {

                                        if (task1.isSuccessful()) {
                                            Toast.makeText(SignUpActivity.this, "user has been registered! ", Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(View.VISIBLE);
                                            Log.d("Error", Objects.requireNonNull(task1.getException()).toString());

                                        } else {
                                            Toast.makeText(SignUpActivity.this, "registration failed, check your info and try again! ", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(this, LoginActivity.class));
                                            progressBar.setVisibility(View.GONE);

                                        }

                                    });

                        } else {
                            Toast.makeText(SignUpActivity.this, "failed to register", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            Log.d("Error", Objects.requireNonNull(task.getException()).toString());

                        }

                    });
        }catch (IllegalStateException e){
            Toast.makeText(SignUpActivity.this,"Please Wait", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));

        }
    }
}