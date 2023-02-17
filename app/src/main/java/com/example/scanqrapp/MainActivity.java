package com.example.scanqrapp;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.scanqrapp.databinding.ActivityMainBinding;
import com.example.scanqrapp.ui.main.MainFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // FragmentTransaction fragmentTransaction;
    private TextView register, forgot_password;
    private EditText editEmail, editPassword;
    private AppCompatButton loginBtn;

    private FirebaseAuth mAuth;

    private ProgressBar indeterminateBar;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

       // overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
       // fragmentTransaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left);


        com.example.scanqrapp.databinding.ActivityMainBinding binding = ActivityMainBinding
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

               //add nav graph/ nav host to default view
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.mainFragment);


        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
        }
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        hideNavBar();
    }
    @Override
    public void onClick(View v) {
        //remember it is a better practice to use if statement
        switch (v.getId()){
            case R.id.register:
                //onclick sent to RegisterActivicty
                startActivity(new Intent(this, RegisterActivity.class));
                break;

            case R.id.loginBtn:
                userLogin();
                break;

            case R.id.forgot_password:
                startActivity(new Intent(this, ForgotPassword.class));

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
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user.isEmailVerified()){
                                Fragment fragment = new MainFragment();
                                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.container,fragment).commit();
                               /* Navigation.findNavController(MainActivity.this, R.id.mainFragment);
                                PendingIntent pendingIntent = new NavDeepLinkBuilder(MainActivity.this)
                                        .setGraph(R.navigation.nav_graph)
                                        .setDestination(R.id.mainFragment)
                                        .createPendingIntent();
                                try {
                                    pendingIntent.send();
                                } catch (PendingIntent.CanceledException e) {
                                    e.printStackTrace();
                                }*/


                            } else {
                                user.sendEmailVerification();
                                Toast.makeText(MainActivity.this, "check your email to verify this account", Toast.LENGTH_SHORT).show();
                            }

                        }else {
                            Toast.makeText(MainActivity.this, "Login failed! ", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public void hideNavBar() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            WindowInsetsControllerCompat windowInsetsController =
                    new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());

            windowInsetsController.setSystemBarsBehavior(
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            );
            windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars());
        }


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavBar();
    }


}