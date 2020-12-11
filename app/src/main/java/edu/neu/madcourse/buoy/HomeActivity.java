package edu.neu.madcourse.buoy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.neu.madcourse.buoy.listobjects.Buoys;
import edu.neu.madcourse.buoy.listobjects.Task;
import edu.neu.madcourse.buoy.listobjects.TaskList;

public class HomeActivity extends AppCompatActivity implements AddBuoyDialogBox.AddBuoyDialogListener{
    private DatabaseReference mdataBase;
    private FirebaseAuth mFirebaseAuth;
    private ArrayList<FriendItemCard> friendList;
    private RecyclerView recyclerView;
    private StickerAdapter stickerAdapter;
    private String thisUsername;
    static final String FRIEND_CARD_LIST = "friendCardList";
    private String friendToken;
    private String uid;//this user's id
    //private User user; //this user.
    private TextView noFriendsSry;

    private List<String> userFriends;

    private String SERVER_KEY = "key=AAAAhIS5lRU:APA91bHS8Kx0LjSRHt-O7zX4KxDsYX2yMFf0daJn3Z6g_fIxM81-h9GDSxNt2WNB22fwOfQiM_27R02nzggKOFaOKpmjGJJnAKo7U-3hOzq1qQf7NdL6TQZGRWrD1IGSsJzQbolP3qNH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_send);
        friendList = new ArrayList<>();
        noFriendsSry = findViewById(R.id.no_friends);
        createRecyclerView();

        uid = mFirebaseAuth.getInstance().getCurrentUser().getUid();
        mdataBase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = mdataBase.child("Users").child(uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public synchronized void onDataChange(@NonNull DataSnapshot snapshot) {
                //user = snapshot.getValue(User.class);
                thisUsername =(String)snapshot.child("userName").getValue();
                setTitle("Welcome, " + thisUsername);

                userFriends = (List<String>)snapshot.child("friends").getValue();
                getUsers();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

    private void launchNewBuoyDialogBox(int pos){
        FragmentManager fm = getSupportFragmentManager();
        DialogFragment newDialog = AddBuoyDialogBox.newInstance(pos);
        newDialog.show(fm, "New Buoy");
    }

    @Override
    public void onPositiveClick(AddBuoyDialogBox dialog){
        createRecyclerView();
        dialog.dismiss();
        sendBuoy(dialog.getPos(), dialog.getBuoyCommentString());
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(FRIEND_CARD_LIST, this.friendList);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        friendList = savedInstanceState.getParcelableArrayList(FRIEND_CARD_LIST);
        createRecyclerView();
    }



    private void createRecyclerView() {
        recyclerView = findViewById(R.id.recyclerViewFriends);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager rLayoutManager = new LinearLayoutManager(this);
        stickerAdapter = new StickerAdapter(this.friendList);
        recyclerView.setAdapter(stickerAdapter);
        recyclerView.setLayoutManager(rLayoutManager);
    }


    public void getUsers() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.keepSynced(true);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot child : snapshot.getChildren()) {
                    //User user = child.getValue(User.class);
                    String friendUID = child.getKey();
                    assert friendUID != null;
                    String userName = child.child("userName").getValue().toString();
                    if (userFriends.contains(userName)) {
                        String firstName = child.child("firstName").getValue().toString();
                        String lastName = child.child("lastName").getValue().toString();
                        Task topTask = child.child("dueSoonestTask").getValue(Task.class);
                        String topTaskName = topTask.getTaskTitle();
                        String topTaskDueDate = topTask.getDueDate();
                        if (topTaskName.equals("PlaceHolder Task")){
                            topTaskName = "This friend doesn't have any tasks.";
                            topTaskDueDate = "";
                        }
                        friendList.add(new FriendItemCard(userName, firstName, lastName, friendUID, topTaskName, topTaskDueDate));
                    }
                }
                stickerAdapter.notifyDataSetChanged();

                if(friendList.isEmpty()){
                    noFriendsSry.setVisibility(View.VISIBLE);
                }

                stickerAdapter.setOnItemClickListener(new StickerAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(int pos) {
                        //something happens when item card is clicked
                    }


                    @Override
                    public void onBuoyClick(int pos) {
                        FriendItemCard friend = friendList.get(pos);
                        if(!friend.getSoonestDueTask().equals("This friend doesn't have any tasks.")){
                            launchNewBuoyDialogBox(pos);
                        } else {
                            Toast.makeText(HomeActivity.this, "This user has no current task to send a buoy to.", Toast.LENGTH_SHORT).show();
                        }

                    }

                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //On send: should write to associated user's task and send a notification.
    private void sendBuoy(int pos, String buoyComment) {
        FriendItemCard friend = this.friendList.get(pos);
        final String id = friend.getUserID();
        //get token of friend
        final DatabaseReference ref = mdataBase.child("Users").child(id);
        ref.keepSynced(true);
        Thread sendMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User friendUser = snapshot.getValue(User.class);
                        friendToken = friendUser.token;
                        final String friendName = thisUsername;
                        List<TaskList> listList = friendUser.getTaskLists();
                        for (TaskList eachList : listList) {
                            for (Task eachTask : eachList.getTaskList()) {
                                if (eachTask.getTaskTitle().equals(friendUser.getDueSoonestTask().getTaskTitle())
                                        && eachTask.getDueDate().equals(friendUser.getDueSoonestTask().getDueDate())) {
                                    eachTask.addBuoy(new Buoys(buoyComment, thisUsername));
                                    break;
                                }
                            }
                        }
                        ref.child("taskLists").setValue(listList);
                        updateUserBuoyCount();
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

    private void sendToDevice(String friendToken, String friendUsername) {
        JSONObject payload = new JSONObject();
        JSONObject notification = new JSONObject();
        JSONObject data = new JSONObject();

        try {
            notification.put("title", "Buoy");
            notification.put("body", friendUsername + " floated you a buoy!");
            notification.put("sound", "default");
            notification.put("badge", "1");

            data.put("title", "Buoy");
            data.put("content", "Buoy");

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
                    Toast.makeText(HomeActivity.this, resp, Toast.LENGTH_LONG).show();
                }
            });

        } catch (JSONException | IOException e) {
            e.printStackTrace();
            Log.e("TAG", "sticker threw error", e);
        }
    }

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }

    private void updateUserBuoyCount() {
        mdataBase.child("Users").child(this.uid).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                User currentUser = currentData.getValue(User.class);
                if (currentUser == null) {
                    return Transaction.success(currentData);
                }

                currentUser.buoysSent = currentUser.buoysSent + 1; //increment sticker count by 1
                currentData.setValue(currentUser);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                Log.d("Buoy count Error", "buoy count post activity:onComplete" + error);
            }
        });
    }



}