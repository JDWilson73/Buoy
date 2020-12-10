package edu.neu.madcourse.buoy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.neu.madcourse.buoy.listobjects.TaskList;

public class Profile extends AppCompatActivity {
    private DatabaseReference mdataBase;
    private FirebaseAuth mFirebaseAuth;
    private List<TaskList> userTaskList;
    private String uid;

    private TextView mTextView;

    String achievements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //mTextView = (TextView) findViewById(R.id.profileUsername);

        //get User's Task List
        mdataBase = FirebaseDatabase.getInstance().getReference();
        uid = mFirebaseAuth.getInstance().getCurrentUser().getUid();
        mdataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        mdataBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                //mTextView.setText(user.firstName + " " + user.lastName);
                userTaskList = user.getTaskLists();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Reading from DB error", error.getMessage());
            }
        });

        BottomNavigationView navi = (BottomNavigationView) findViewById(R.id.navigation);
        navi.setItemIconTintList(null);
        navi.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.Home:
                        Intent home = new Intent(Profile.this, HomeActivity.class);
                        startActivity(home);
                        break;
                    case R.id.Profile:
                        break;
                    case R.id.Friends:
                        Intent friends = new Intent(Profile.this, FriendActivity.class);
                        startActivity(friends);
                        break;
                    case R.id.Settings:
                        Intent settings = new Intent(Profile.this, UserSettingsActivity.class);
                        startActivity(settings);
                        break;
                    case R.id.Lists:
                        Intent lists = new Intent(Profile.this, userList.class);
                        startActivity(lists);
                        break;
                }

                return false;
            }
        });

        calculateAchievements();

    }

    private void calculateAchievements() {
        DatabaseReference ndataBase = FirebaseDatabase.getInstance().getReference();
        ndataBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String cheevos = "";
                Map<String, Long> achievementCounts = (HashMap<String, Long>) snapshot.child("Users").child(uid).child("AchievementCounts").getValue();
                for (String s : achievementCounts.keySet()) {
                    Long threshold = (Long) snapshot.child("AchievementCategories").child(s).child("threshold").getValue();
                    if (achievementCounts.get(s) >= threshold) {
                        cheevos += snapshot.child("AchievementCategories").child(s).child("title").getValue().toString() + "  ";
                    }
                }

                cheevos = cheevos.trim();
                TextView achievementsText = findViewById(R.id.achievements);
                achievementsText.setText(cheevos);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //TODO: Display User's first/last name, username, top x number of tasks due soon.
    //TODO: When adding achievement badges, display those in horizontal scrolling recycler view as well.
}