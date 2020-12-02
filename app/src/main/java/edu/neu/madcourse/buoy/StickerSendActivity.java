package edu.neu.madcourse.buoy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import edu.neu.madcourse.buoy.listobjects.Task;

public class StickerSendActivity extends AppCompatActivity {
    private DatabaseReference mdataBase;
    private FirebaseAuth mFirebaseAuth;
    private ArrayList<FriendItemCard> friendList;
    private RecyclerView recyclerView;
    private StickerAdapter stickerAdapter;
    private String thisUsername;
    static final String FRIEND_CARD_LIST = "friendCardList";
    private String friendToken;
    private String uid;//this user's id

    private String SERVER_KEY = "key=AAAAhIS5lRU:APA91bHS8Kx0LjSRHt-O7zX4KxDsYX2yMFf0daJn3Z6g_fIxM81-h9GDSxNt2WNB22fwOfQiM_27R02nzggKOFaOKpmjGJJnAKo7U-3hOzq1qQf7NdL6TQZGRWrD1IGSsJzQbolP3qNH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_send);
        setTitle("Friends");

        friendList = new ArrayList<>();
        createRecyclerView();

        //friendsTesting = findViewById(R.id.textView3);
        uid = mFirebaseAuth.getInstance().getCurrentUser().getUid();
        mdataBase = FirebaseDatabase.getInstance().getReference();

        mdataBase.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
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

        stickerAdapter.setOnItemClickListener(new StickerAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                //something happens when item card is clicked
            }

            @Override
            public void onVibesClick(int pos) {
                setTitle("Good Vibes!");
                sendSticker(pos, "goodVibes");
            }

            @Override
            public void onKeepClick(int pos) {
                setTitle("Keep it Up!");
                sendSticker(pos, "keepUp");
            }

            @Override
            public void onDoItClick(int pos) {
                setTitle("You can Do it!");
                sendSticker(pos, "doIt");
            }
        });

        //consider on swipe to delete

    }

    public void subscribeStickerNotification(View view){

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(FRIEND_CARD_LIST, this.friendList);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        friendList = savedInstanceState.getParcelableArrayList(FRIEND_CARD_LIST);
        createRecyclerView();
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
                    //User user = child.getValue(User.class);
                    String friendUID = child.getKey();
                    assert friendUID != null;
                    String firstName = child.child("firstName").getValue().toString();
                    String lastName = child.child("lastName").getValue().toString();
                    String userName = child.child("userName").getValue().toString();
                    List<Task> list = (List<Task>)child.child("taskLists").getValue();
                    if(!friendUID.equals(uid)) {
                        friendList.add(new FriendItemCard(userName, firstName, lastName, friendUID));
                    }
                }
                stickerAdapter.notifyDataSetChanged();
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //On send: update friends' sticker list and current users sticker sent count.
    private void sendSticker(int pos, final String stickerType){
        FriendItemCard friend = this.friendList.get(pos);
        final String id = friend.getUserID();
        //get token of friend
        final DatabaseReference ref = mdataBase.child("Users").child(id);
        ref.keepSynced(true);
        Thread sendMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                //DataSnapshot temp = new DataSnapshot;
                //String friendToken = ref.getDatabase().

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        friendToken = user.token;
                        final String friendName = user.userName;
                        writeSticker(stickerType, id);
                        updateStickerCount();
                        Thread sendDeviceThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                sendToDevice(friendToken, friendName);
                            }
                        });
                        sendDeviceThread.start();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

        sendMessage.start();

    }

    private void sendToDevice(String friendToken, String friendUsername){
        JSONObject payload = new JSONObject();
        JSONObject notification = new JSONObject();
        JSONObject data = new JSONObject();

        try{
            notification.put("title", "Sticker");
            notification.put("body",  friendUsername + " sent you a sticker!");
            notification.put("sound", "default");
            notification.put("badge", "1");

            data.put("title", "Sticker");
            data.put("content", "sticker");

            payload.put("to", friendToken);
            payload.put("priority", "high");
            payload.put("notification", notification);
            payload.put("data", data);

            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", SERVER_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(payload.toString().getBytes());
            outputStream.close();

            // Read FCM response. ERROR IS HERE
            InputStream inputStream = conn.getInputStream();
            final String resp = convertStreamToString(inputStream);

            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    Log.e("TAG", "run: " + resp);
                    Toast.makeText(StickerSendActivity.this,resp,Toast.LENGTH_LONG).show();
                }
            });

        }catch (JSONException | IOException e){
            e.printStackTrace();
            Log.e("TAG","sticker threw error",e);
        }
    }

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }

    private void writeSticker(final String stickerType, String friendId){
        mdataBase.child("Users").child(friendId).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                User friend = currentData.getValue(User.class);
                if (friend == null){
                    return Transaction.success(currentData);
                }

                Map<String, Integer> stickerList = friend.stickerList;

                if (stickerList.containsKey(stickerType)){
                    stickerList.put(stickerType, stickerList.get(stickerType) + 1); //increment sticker type count
                }
                currentData.setValue(friend);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                Log.d("StickerActivity", "friend sticker count post activity:onComplete" + error);
            }
        });
    }

    private void updateStickerCount(){
        mdataBase.child("Users").child(this.uid).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                User currentUser = currentData.getValue(User.class);
                if (currentUser==null){
                    return Transaction.success(currentData);
                }

                currentUser.totalStickers = currentUser.totalStickers + 1; //increment sticker count by 1
                currentData.setValue(currentUser);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                Log.d("StickerActivity", "sticker count post activity:onComplete" + error);
            }
        });
    }

}