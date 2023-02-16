package com.example.scanqrapp.ui.auth_login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.scanqrapp.MainActivity;
import com.example.scanqrapp.R;
import com.example.scanqrapp.databinding.FragmentRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterFragment extends Fragment implements View.OnClickListener {

    private RegisterViewModel mViewModel;
    private FragmentRegisterBinding binding;

    private TextView big_header;
    private AppCompatButton user_regBtn;
    private EditText editAge;
    private EditText editTextfullName;
    private EditText editEmail;
    private EditText editPassword;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;   //firebase auth instance

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(getLayoutInflater());

        mAuth = FirebaseAuth.getInstance();
        return binding.getRoot();


    }
    @Override
    //implement onlclick headers and icons
   public void onClick(View v){
        // TODO: do login header messages with switch statements
        switch (v.getId()){
            case R.id.big_header:
                //navigate to login page
                startActivity(new Intent(getActivity(), MainActivity.class));

            case R.id.user_regBtn:
                // get register activity
                registerUser();
                break;
        }
   }
    private void registerUser() {
        String input_name = Objects.requireNonNull(editTextfullName.getText()).toString().trim(); // user input name
        String reg_pass = Objects.requireNonNull(editPassword.getText()).toString().trim(); // user input password
        String reg_email = Objects.requireNonNull(editEmail.getText()).toString().trim();  // user input email

        //condition for empty email
        if (input_name.isEmpty()) {
            editTextfullName.setError(" Name is required! ");
            editTextfullName.requestFocus();

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

        // verify email
        if (!Patterns.EMAIL_ADDRESS.matcher(reg_email).matches()) {
            editEmail.setError("pls provide valid email");
            editEmail.requestFocus();
            return;
        }
        progressBar.setVisibility(View.GONE);
        //create user password/ email instance @ firebase
        mAuth.createUserWithEmailAndPassword(reg_email, reg_pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            // call userID CLASS
                            UserIdAuth userIdAuth =new UserIdAuth(input_name, reg_email);
                           FirebaseDatabase.getInstance("https://scanqrapp-1db9d-default-rtdb.firebaseio.com") //Firebase instance url
                                    .getReference("Users")
                                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                   .setValue(userIdAuth)
                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                           if (task.isSuccessful()){
                                               Toast.makeText(getContext(),"User has been registered!", Toast.LENGTH_LONG)
                                               .show();
                                           } else {
                                               Toast.makeText(getContext(), "registration failed, check your info and try again! ", Toast.LENGTH_LONG).show();
                                               progressBar.setVisibility(View.GONE);
                                           }

                                       }
                                   });
                        } else {
                            Toast.makeText(getContext(), "failed to register", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            Log.d("Error", Objects.requireNonNull(task.getException()).toString());
                        }
                    }
                });
    }

}