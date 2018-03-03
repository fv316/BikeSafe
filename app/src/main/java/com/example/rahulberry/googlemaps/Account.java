package com.example.rahulberry.googlemaps;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

/**
 * Created by rahulberry on 01/03/2018.
 */

public class Account extends AppCompatActivity{

    private static final int CHOOSE_IMAGE = 101;

    TextView textView;
    ImageView imageView;
    EditText displayName2, Email3;
    Button start;
    Uri uriProfileImage;
    ProgressBar progressBar;

    String profileImageUrl;

    FirebaseAuth mAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        displayName2 = (EditText) findViewById(R.id.editTextDisplayName2);
        Email3 = (EditText) findViewById(R.id.editTextEmail_three);
        imageView = (ImageView) findViewById(R.id.profilepicture);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        textView = (TextView) findViewById(R.id.textViewVerified);
        start = (Button) findViewById(R.id.enter_main);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageChooser();
            }
        });
        loadUserInformation();

        findViewById(R.id.buttonSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });
    }

    private void saveUserInformation() {
        String displayName = displayName2.getText().toString();

        if (displayName.isEmpty()) {
            displayName2.setError("Name required");
            displayName2.requestFocus();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null && profileImageUrl != null) {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();

            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(com.example.rahulberry.googlemaps.Account.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void loadUserInformation() {
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(imageView);
            }

            //The problem is here with this set text broooOOooooO - still have to fix this
            if (user.getDisplayName() != null) {
               displayName2.setText(user.getDisplayName());
            }

           if (user.getEmail() != null) {
                Email3.setText(user.getEmail());
            }


            if (user.isEmailVerified()) {
                textView.setText("Email Verified");
            } else {
                textView.setText("Email Not Verified (Click to Verify)");
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(Account.this, "Verification Email Sent", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                imageView.setImageBitmap(bitmap);

                uploadImageToFirebaseStorage();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebaseStorage() {
        StorageReference profileImageRef =
                FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg");

        if (uriProfileImage != null) {
            progressBar.setVisibility(View.VISIBLE);
            profileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.setVisibility(View.GONE);
                            profileImageUrl = taskSnapshot.getDownloadUrl().toString();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(com.example.rahulberry.googlemaps.Account.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE);
    }

}
