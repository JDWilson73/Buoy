package edu.neu.madcourse.buoy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class FriendActivity extends AppCompatActivity {

    FirebaseAuth mFirebaseAuth;
    Button addFriendButton;
    TextView usernameInput;
    DatabaseReference mdataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
    }

    public void executeAddFriend(View view) {
        addFriendButton = findViewById(R.id.addFriendButton);
        usernameInput = findViewById(R.id.usernameInput);

        final String newFriend = usernameInput.getText().toString();
        final String uid = mFirebaseAuth.getInstance().getCurrentUser().getUid();
        mdataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        mdataBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public synchronized void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.child("userName").getValue().toString();

                if (!username.equals(newFriend)) {
                    addFriend(username, newFriend);
                } else {
                    Toast.makeText(FriendActivity.this,"Cannot befriend self",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
            //String username = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("userName");
        });
    }

    public void executeRemoveFriend(View view) {
        addFriendButton = findViewById(R.id.addFriendButton);
        usernameInput = findViewById(R.id.usernameInput);

        final String newFriend = usernameInput.getText().toString();
        final String uid = mFirebaseAuth.getInstance().getCurrentUser().getUid();
        mdataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        mdataBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public synchronized void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.child("userName").getValue().toString();

                if (!username.equals(newFriend)) {
                    removeFriend(username, newFriend);
                } else {
                    Toast.makeText(FriendActivity.this,"Cannot unfriend self",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
            //String username = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("userName");
        });
    }

    protected void addFriend(final String friend1, final String friend2) {

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.keepSynced(true);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean addedToOtherList = false;
                for(DataSnapshot child: snapshot.getChildren()) {
                    String friendUID = child.getKey();
                    assert friendUID != null;
                    String userName;
                    try {
                        userName = child.child("userName").getValue().toString();
                    } catch (NullPointerException e) {
                        userName = "";
                    }
                    List<String> friends = (List<String>)child.child("friends").getValue();
                    if(friend1.equals(userName)) {
                        friends.add(friend2);
                        ref.child(friendUID).child("friends").setValue(friends);
                    }
                    if(friend2.equals(userName)) {
                        addedToOtherList = true;
                        friends.add(friend1);
                        ref.child(friendUID).child("friends").setValue(friends);
                    }
                }
                // Just in case the username input by the user doesn't actually exist, we'll undo it
                if (!addedToOtherList) {
                    removeFriend(friend1, friend2);
                }
                // TODO turn sticker send into friend list viewing activity? friend cards could have add/delete buttons
//
//
//                //User user = child.getValue(User.class);
//                List<String> friendList1 = (List<String>) snapshot.child("Users").child(friend1).child("friends").getValue();
//                List<String> friendList2 = (List<String>) snapshot.child("Users").child(friend2).child("friends").getValue();
//
//                friendList1.add(friend2);
//                friendList2.add(friend1);
//
//                mdataBase.child("Users").child(friend1).child("friends").setValue(friendList1);
//                mdataBase.child("Users").child(friend2).child("friends").setValue(friendList2);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    protected void removeFriend(final String friend1, final String friend2) {

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.keepSynced(true);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot child: snapshot.getChildren()) {
                    //User user = child.getValue(User.class);
                    String friendUID = child.getKey();
                    assert friendUID != null;
                    String userName;
                    try {
                        userName = child.child("userName").getValue().toString();
                    } catch (NullPointerException e) {
                        userName = "";
                    }
                    List<String> friends = (List<String>)child.child("friends").getValue();
                    if(friend1.equals(userName)) {
                        //if (!friends.contains(friend2)) {
                        friends.remove(friend2);
                        //}
                        ref.child(friendUID).child("friends").setValue(friends);
                    }
                    if(friend2.equals(userName)) {
                        //if (!friends.contains(friend1)) {
                        friends.remove(friend1);
                        // }
                        ref.child(friendUID).child("friends").setValue(friends);
                    }
                }
                // TODO removeFriend
                // TODO make friends the only one to appear in sticker send rather than all users
//
//
//                //User user = child.getValue(User.class);
//                List<String> friendList1 = (List<String>) snapshot.child("Users").child(friend1).child("friends").getValue();
//                List<String> friendList2 = (List<String>) snapshot.child("Users").child(friend2).child("friends").getValue();
//
//                friendList1.add(friend2);
//                friendList2.add(friend1);
//
//                mdataBase.child("Users").child(friend1).child("friends").setValue(friendList1);
//                mdataBase.child("Users").child(friend2).child("friends").setValue(friendList2);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}