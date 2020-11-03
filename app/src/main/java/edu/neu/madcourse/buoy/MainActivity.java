package edu.neu.madcourse.buoy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    public EditText emailId, password;
    Button btnSignUp;
    Button btnSignIn;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.editTextEmailAddress);
        password = findViewById(R.id.editTextPassword);
        btnSignIn = findViewById(R.id.signInButton);
        btnSignUp = findViewById(R.id.signUpButton);
        final VideoView videoview = (VideoView) findViewById(R.id.videoView);
//        ConstraintLayout layout = findViewById()
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.background_waves);
        videoview.setVideoURI(uri);
        videoview.start();
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoview.start();
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
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
                    mFirebaseAuth.createUserWithEmailAndPassword(Email, pwd).
                            addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this,
                                                "Account Creation Failed!",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        startActivity(new Intent(MainActivity.this,
                                                HomeActivity.class));

                                    }
                                }
                            });
                }
                else{
                    Toast.makeText(MainActivity.this,
                            "An Error Occurred",
                            Toast.LENGTH_SHORT).show();
                }



            }
        });
    }
}
