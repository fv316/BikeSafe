package com.example.rahulberry.googlemaps;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class register extends AppCompatActivity implements View.OnClickListener{

    Button register;
    ProgressBar progressBar;
    EditText editTextEmail, editTextPassword, editTextConfEmail, editTextconfirm;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        register = (Button) findViewById(R.id.register_two);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail_two);
        editTextConfEmail = (EditText) findViewById(R.id.editTextConfEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword_two);
        editTextconfirm = (EditText) findViewById(R.id.editTextconfirm_password);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        findViewById(R.id.register_two).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirm_email = editTextConfEmail.getText().toString().trim();
        String confirm_password = editTextconfirm.getText().toString().trim();


        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if(!(confirm_password).equals(password)){
            editTextconfirm.setError("The Passwords do not match");
            editTextconfirm.requestFocus();
            return;
        }

        if (confirm_password.isEmpty()) {
            editTextconfirm.setError("Password confirmation is required");
            editTextconfirm.requestFocus();
            return;
        }

        if(!(confirm_email).equals(email)){
            editTextconfirm.setError("The Emails do not match");
            editTextconfirm.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Minimum length of password should be 6");
            editTextPassword.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);


        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Created Account!", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(register.this, welcome_one.class));
                } else {

                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_two:
                registerUser();
                break;
        }
    }
}
