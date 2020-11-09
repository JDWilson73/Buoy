package edu.neu.madcourse.buoy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class StickerSendActivity extends AppCompatActivity {
    private TextView friendsTesting;
    private DatabaseReference mdataBase;
    private FirebaseAuth mFirebaseAuth;
    private ArrayList<FriendItemCard> friendList;
    private RecyclerView recyclerView;
    private StickerAdapter stickerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_send);

        friendList = new ArrayList<>();
        createRecyclerView();
        //garbage friends
//        FriendItemCard friend1 = new FriendItemCard("sandy", "Sandy", "Cheeks");
//        FriendItemCard friend2 = new FriendItemCard("star", "Patrick", "Star");
//        friendList.add(friend1);
//        friendList.add(friend2);

        friendsTesting = findViewById(R.id.textView3);
        final String uid = mFirebaseAuth.getInstance().getCurrentUser().getUid();
        mdataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        mdataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                friendsTesting.setText(user.firstName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        getUsers(uid);

        //consider on swipe to delete

    }

    private void createRecyclerView(){
        recyclerView = findViewById(R.id.recyclerViewFriends);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager rLayoutManager = new LinearLayoutManager(this);
        stickerAdapter = new StickerAdapter(this.friendList);
        recyclerView.setAdapter(stickerAdapter);
        recyclerView.setLayoutManager(rLayoutManager);
    }

    public void getUsers(final String uid){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.keepSynced(true);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot child: snapshot.getChildren()) {
                    User user = child.getValue(User.class);
                    friendList.add(new FriendItemCard(user.userName, user.firstName, user.lastName));
                }

                stickerAdapter.notifyDataSetChanged();
//                Iterator<DataSnapshot> dataSnapShots = snapshot.getChildren().iterator();
//
//                while(dataSnapShots.hasNext()){
//
//                    User user = snapshot.getValue(User.class);
//                    if (user == null){
//                        System.out.println("User is NUll!");
//                    }
//                    if(user != null) {
//                        System.out.println(user.firstName);
//                        System.out.println(uid);
//                        if(!user.uid.equals(uid)) {
//                            friendList.add(new FriendItemCard(user.userName, user.firstName, user.lastName));
//                        }
//                    }
                    //DataSnapshot dataSnapshotChild = dataSnapShots.next();
                }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}