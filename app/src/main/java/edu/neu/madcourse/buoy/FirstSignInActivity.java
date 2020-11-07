package edu.neu.madcourse.buoy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirstSignInActivity extends AppCompatActivity {
    String TAG = "user info: ";
    private DatabaseReference mDatabase;
    TextView testing;
    Button mGetStartedButton;
    public EditText firstName, lastName, userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_sign_in);

        // get data from email/password sign up page
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            String email = extras.getString("email");
            String pwd = extras.getString("password");
        }
        testing = findViewById(R.id.textView6);
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
//                    testing.setText(firstNameString+ "\n"+ lastNameString + "\n" + userNameString);
                    writeNewUser(userNameString, firstNameString, lastNameString);
                    startActivity(new Intent(FirstSignInActivity.this, HomeActivity.class));
                }
            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference();



    }
    private void writeNewUser(String userName, String firstName, String lastName) {

        User user = new User(userName, firstName, lastName);

        mDatabase.child("Users").child(userName).setValue(user);
    }
}