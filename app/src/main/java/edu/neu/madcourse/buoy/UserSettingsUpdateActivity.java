package edu.neu.madcourse.buoy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class UserSettingsUpdateActivity extends AppCompatActivity {
    EditText firstName, lastName;
    Button submitButton;
    FirebaseAuth mFirebaseAuth;
    DatabaseReference mdataBase;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings_update);
        firstName = findViewById(R.id.userSettingsFirstNameFill);
        lastName = findViewById(R.id.userSettingsLastNameFill);
        submitButton = findViewById(R.id.userSettingsUpdateSubmit);

        final String uid = mFirebaseAuth.getInstance().getCurrentUser().getUid();
        mdataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        mdataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                firstName.setText(user.firstName);
                lastName.setText(user.lastName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstNameUpdate = firstName.getText().toString();
                String lastNameUpdate = lastName.getText().toString();

                mdataBase.child("firstName").setValue(firstNameUpdate);
                mdataBase.child("lastName").setValue(lastNameUpdate);
                finish();

            }
        });
    }
}