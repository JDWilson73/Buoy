package edu.neu.madcourse.buoy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserSettingsActivity extends AppCompatActivity {
    TextView firstName, lastName, userName;
    Button updateInfo;
    FirebaseAuth mFirebaseAuth;
    DatabaseReference mdataBase;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setings_activty);

        firstName = findViewById(R.id.userSettingsFirstNameFill);
        lastName = findViewById(R.id.userSettingsLastNameFill);
        userName = findViewById(R.id.userSettingsUserNamefill);
        updateInfo = findViewById(R.id.userSettingsUpdateInfoButton);

        final String uid = mFirebaseAuth.getInstance().getCurrentUser().getUid();
        mdataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        mdataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                firstName.setText(user.firstName);
                lastName.setText(user.lastName);
                userName.setText(user.userName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        updateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserSettingsActivity.this, UserSettingsUpdateActivity.class);
                i.putExtra("firstname", firstName.toString());
                i.putExtra("lastname", lastName.toString());
                i.putExtra("username", userName.toString());
                startActivity(i);

            }
        });
    }
}