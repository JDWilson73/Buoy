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
    TextView usernameInput;
    DatabaseReference mdataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
    }

    public void executeAddFriend(View view) {
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
        });
    }

    public void executeRemoveFriend(View view) {
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
        });
    }

    protected void addFriend(final String friend1, final String friend2) {

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.keepSynced(true);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean addedToOtherList = false;
                boolean alreadyFriended = true;
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
                    assert friends != null;
                    if(friend1.equals(userName)) {
                        if (!friends.contains(friend2)) {
                            alreadyFriended = false;
                            friends.add(friend2);
                        }
                        ref.child(friendUID).child("friends").setValue(friends);
                    }
                    if(friend2.equals(userName)) {
                        addedToOtherList = true;
                        if (!friends.contains(friend1)) {
                            friends.add(friend1);
                        }
                        ref.child(friendUID).child("friends").setValue(friends);
                    }
                }
                // Just in case the username input by the user doesn't actually exist, we'll undo it
                if (!addedToOtherList) {
                    removeFriend(friend1, friend2, true);

                    Toast.makeText(FriendActivity.this,"User not found",Toast.LENGTH_LONG).show();
                }
                else if (alreadyFriended) {
                    Toast.makeText(FriendActivity.this,"Already friends with this user",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(FriendActivity.this,"Friend added!",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    protected void removeFriend(final String friend1, final String friend2) {
        removeFriend(friend1, friend2, false);
    }

    protected void removeFriend(final String friend1, final String friend2, final boolean noToast) {

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.keepSynced(true);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                boolean wasFriend = false;
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
                    assert friends != null;
                    if(friend1.equals(userName)) {
                        if (friends.contains(friend2)) {
                            wasFriend = true;
                            friends.remove(friend2);
                        }
                        ref.child(friendUID).child("friends").setValue(friends);
                    }
                    if(friend2.equals(userName)) {
                        friends.remove(friend1);
                        ref.child(friendUID).child("friends").setValue(friends);
                    }
                }

                if (wasFriend && !noToast) {
                    Toast.makeText(FriendActivity.this,"Friend removed",Toast.LENGTH_LONG).show();
                }
                else if (!noToast) {
                    Toast.makeText(FriendActivity.this,"No such friend to remove",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}