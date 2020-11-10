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
        final TextView text = findViewById(R.id.textView);

        stickerAdapter.setOnItemClickListener(new StickerAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                //something happens when item card is clicked
            }

            @Override
            public void onVibesClick(int pos) {
                text.setText("Good Vibes!");
            }

            @Override
            public void onKeepClick(int pos) {
                text.setText("Keep it Up!");
            }

            @Override
            public void onDoItClick(int pos) {
                text.setText("You can Do it!");
            }
        });

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