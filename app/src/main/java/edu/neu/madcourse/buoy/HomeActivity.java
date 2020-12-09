package edu.neu.madcourse.buoy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;

import edu.neu.madcourse.buoy.listobjects.Task;

// TODO: Navigation drawer or menu, rotation handling

public class HomeActivity extends AppCompatActivity {
    Button btnLogout;
    Button btnStickerGoTo;
    Button btnUserSettings;
    Button btnUserLists;
    FirebaseAuth mFirebaseAuth;
    DatabaseReference mdataBase;
    String username;
    private DatabaseReference mDatabase;
    TextView doItCount;
    TextView keepUpCount;
    TextView goodVibesCount;
    TextView stickerCount;

    Button friendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btnLogout = findViewById(R.id.logout);
        btnStickerGoTo = findViewById(R.id.sendStickerButton);
        btnUserSettings = findViewById(R.id.userSettings);
        btnUserLists = findViewById(R.id.home_goto_user_list);

        friendButton = findViewById(R.id.friendButton);

        doItCount = findViewById(R.id.do_it_counter);
        keepUpCount = findViewById(R.id.keep_up_counter);
        goodVibesCount = findViewById(R.id.good_vibes_counter);
        stickerCount = findViewById(R.id.sent_counter);

        final TextView user_welcome = findViewById(R.id.textView2);
        final String uid = mFirebaseAuth.getInstance().getCurrentUser().getUid();
        mdataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        mdataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public synchronized void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                user_welcome.setText("Welcome, " + user.firstName + "!");
                //update counters for stickers
                doItCount.setText(String.valueOf(user.stickerList.get("doIt")));
                keepUpCount.setText(String.valueOf(user.stickerList.get("keepUp")));
                goodVibesCount.setText(String.valueOf(user.stickerList.get("goodVibes")));
                stickerCount.setText("Number of Stickers Sent: " + String.valueOf(user.totalStickers));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        btnStickerGoTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intoSticker = new Intent(HomeActivity.this,
                        StickerSendActivity.class);
                startActivity(intoSticker);
            }
        });
        btnUserLists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, userList.class);
                startActivity(i);
            }
        });



        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intoMain = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intoMain);
            }
        });

        btnUserSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userSettingsGoTo = new Intent(HomeActivity.this,
                        UserSettingsActivity.class);
                startActivity(userSettingsGoTo);
            }
        });

        friendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent friendsGoTo = new Intent(HomeActivity.this,
                        FriendActivity.class);
                startActivity(friendsGoTo);
            }
        });

        BottomNavigationView navi = (BottomNavigationView) findViewById(R.id.navigation);
        navi.setItemIconTintList(null);
        navi.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.Home:
                        break;
                    case R.id.Profile:
                        Intent profile = new Intent(HomeActivity.this, Profile.class);
                        startActivity(profile);
                        break;
                    case R.id.Friends:
                        Intent friends = new Intent(HomeActivity.this, FriendActivity.class);
                        startActivity(friends);
                        break;
                    case R.id.Settings:
                        Intent settings = new Intent(HomeActivity.this, UserSettingsActivity.class);
                        startActivity(settings);
                        break;
                    case R.id.Lists:
                        Intent lists = new Intent(HomeActivity.this, userList.class);
                        startActivity(lists);
                        break;
                }

                return false;
            }

        });



    }
}