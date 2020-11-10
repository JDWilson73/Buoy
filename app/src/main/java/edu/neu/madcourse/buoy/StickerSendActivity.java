package edu.neu.madcourse.buoy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
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
    private DatabaseReference mdataBase;
    private FirebaseAuth mFirebaseAuth;
    private ArrayList<FriendItemCard> friendList;
    private RecyclerView recyclerView;
    private StickerAdapter stickerAdapter;
    private String thisUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_send);

        friendList = new ArrayList<>();
        createRecyclerView();

        //friendsTesting = findViewById(R.id.textView3);
        final String uid = mFirebaseAuth.getInstance().getCurrentUser().getUid();
        mdataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        mdataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                thisUsername = user.userName;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        getUsers();

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

//    @Override
//    public void onClick(View view){
//        switch (view.getId()){
//            case R.id.do_it:
//                break;
//            case R.id.good_vibes:
//                break;
//            case R.id.keep_it_up:
//                break;
//
//        }
//    }

    public void getUsers(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.keepSynced(true);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot child: snapshot.getChildren()) {
                    User user = child.getValue(User.class);
                    if(!user.getUserName().equals(thisUsername)) {
                        friendList.add(new FriendItemCard(user.userName, user.firstName, user.lastName, child.getKey()));
                    }
                }

                stickerAdapter.notifyDataSetChanged();
                }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}