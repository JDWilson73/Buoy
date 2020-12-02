package edu.neu.madcourse.buoy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class FirstSignInActivity extends AppCompatActivity {
    String TAG = "user info: ";
    private DatabaseReference mDatabase;
    TextView testing;
    Button mGetStartedButton;
    String email, pwd, token,uid;
    FirebaseAuth mFirebaseAuth;
    public EditText firstName, lastName, userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_sign_in);
        // get data from email/password sign up page
        Bundle extras = getIntent().getExtras();
        if (extras != null){
             email = extras.getString("email");
             pwd = extras.getString("password");
        }
        firstName = findViewById(R.id.editTextTextPersonFirstName);
        lastName = findViewById(R.id.editTextTextPersonLastName);
        userName = findViewById(R.id.editTextTextPersonUserName);

        mGetStartedButton = findViewById(R.id.buttonGetStarted);



        mGetStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String firstNameString = firstName.getText().toString();
                final String lastNameString = lastName.getText().toString();
                final String userNameString = userName.getText().toString();
                if (firstNameString.isEmpty()){
                    firstName.setError("first name can't be empty");
                }
                else if (lastNameString.isEmpty()){
                    lastName.setError("last name can't be empty");
                }
                else if (userNameString.isEmpty()){
                    userName.setError("username can't be empty");
                }
                else{
                    uid = mFirebaseAuth.getInstance().getCurrentUser().getUid().toString();

                    //get token code
                    FirebaseMessaging.getInstance().getToken()
                            .addOnCompleteListener(new OnCompleteListener<String>() {

                                @Override
                                public void onComplete(@NonNull Task<String> task) {
                                    if (!task.isSuccessful()) {
                                        //failed to get token log
                                        Log.w("Token", "Fetching FCM registration token failed", task.getException());
                                        return;
                                    }
                                    token = task.getResult().toString();
                                    //create new user object
                                    writeNewUser(uid, userNameString, firstNameString, lastNameString, email, token);
                                    Intent goHome = new Intent(FirstSignInActivity.this, HomeActivity.class);
                                    goHome.putExtra(userNameString, "username");
                                    //Toast.makeText(FirstSignInActivity.this, token, Toast.LENGTH_LONG).show();
                                    startActivity(goHome);
                                }
                            });

                }
            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference();

    }
    private void writeNewUser(String uid, String userName, String firstName, String lastName, String email, String token) {
        User user = new User(uid, userName, firstName, lastName, email, token);
        mDatabase.child("Users").child(uid).setValue(user);
    }

}