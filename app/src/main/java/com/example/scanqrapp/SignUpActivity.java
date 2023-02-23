package com.example.scanqrapp;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.scanqrapp.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity  {

    private TextView big_header;
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

        mAuth = FirebaseAuth.getInstance();

        //implement user reg btn
       /* user_regBtn = (AppCompatButton) findViewById(R.id.user_regBtn);
        user_regBtn.setOnClickListener(this);

        big_header = (TextView) findViewById(R.id.big_header);
        big_header.setOnClickListener(this);*/

        editTextfullName = (EditText) findViewById(R.id.input_name);
        editEmail = (EditText) findViewById(R.id.reg_email);
        editPassword = (EditText) findViewById(R.id.reg_pass);

        progressBar = (ProgressBar) findViewById(R.id.indeterminateBar2);
    }


  /*  @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.big_header:
                //navigate to login page
                startActivity(new Intent(this, MainActivity.class));

            case R.id.user_regBtn:
                // get register activity
                registerUser();
                break;
        }
    }*/

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

        progressBar.setVisibility(View.GONE);
        mAuth.createUserWithEmailAndPassword(reg_email,reg_pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            User user = new User(input_name,  reg_email);

                            FirebaseDatabase.getInstance("")
                                    .getReference("Users")
                                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                    .setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){
                                                Toast.makeText(SignUpActivity.this, "user has been registered! ", Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.VISIBLE);
                                                Log.d("Error", Objects.requireNonNull(task.getException()).toString());

                                            } else {
                                                Toast.makeText(SignUpActivity.this, "registration failed, check your info and try again! ", Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);

                                            }

                                        }
                                    });

                        } else{
                            Toast.makeText(SignUpActivity.this, "failed to register", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            Log.d("Error", Objects.requireNonNull(task.getException()).toString());

                        }
                    }
                });
    }
}
