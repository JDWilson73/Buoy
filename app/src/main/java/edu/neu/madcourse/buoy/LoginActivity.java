package edu.neu.madcourse.buoy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    public EditText emailId, password;
    Button btnSignIn;
    TextView tvSignUp;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.editTextEmailAddress);
        password = findViewById(R.id.editTextPassword);
        tvSignUp = findViewById(R.id.signUpText);
        btnSignIn = findViewById(R.id.signInButton);
        final VideoView videoview = (VideoView) findViewById(R.id.videoView);

        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.background_waves);
        videoview.setVideoURI(uri);
        videoview.start();
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoview.start();
            }
        });
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if (mFirebaseUser != null){
                    Toast.makeText(LoginActivity.this, "You are logged in",
                            Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(i);
                }
                else{
                    Toast.makeText(LoginActivity.this, "Please Log in",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intSignUp = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intSignUp);

            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = emailId.getText().toString();
                String pwd = password.getText().toString();
                if(Email.isEmpty()){
                    emailId.setError("please enter email address");
                    emailId.requestFocus();
                } else if (pwd.isEmpty()) {
                    password.setError("please enter your password");
                    password.requestFocus();
                }
                if (!(pwd.isEmpty() && Email.isEmpty())) {
                    mFirebaseAuth.signInWithEmailAndPassword(Email, pwd).addOnCompleteListener(LoginActivity.this,
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(!task.isSuccessful()){
                                        Toast.makeText(LoginActivity.this,
                                                "Login Error. Please Try again", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Intent intToHome = new Intent(LoginActivity.this, HomeActivity.class);
                                        startActivity(intToHome);
                                    }
                                }
                            }
                    );
                }
                else{
                    Toast.makeText(LoginActivity.this,
                            "An Error Occurred",
                            Toast.LENGTH_SHORT).show();
                }

            }

        });
    }
    @Override
    protected void onStart(){
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
}