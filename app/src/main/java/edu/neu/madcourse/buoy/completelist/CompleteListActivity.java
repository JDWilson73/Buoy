package edu.neu.madcourse.buoy.completelist;
/*
https://stackoverflow.com/questions/22323974/how-to-get-city-name-by-latitude-longitude-in-android
 */


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import edu.neu.madcourse.buoy.R;
import edu.neu.madcourse.buoy.User;
import edu.neu.madcourse.buoy.listobjects.Task;
import edu.neu.madcourse.buoy.listobjects.TaskList;

public class CompleteListActivity extends AppCompatActivity {
    static final String PLACEHOLDERITEMCARD = "itemCard placeHolder";
    private DatabaseReference mdataBase;
    private String uid;
    private List<TaskList> listOfLists;
    private TaskList taskList;
    private String locationName;
    private CompleteListAdapter adapter;
    private RecyclerView recyclerView;
    private Button cancelButton;
    private Button completeButton;
    private CheckBox shareLocation;
    private int listIndex;
    private List<TaskList> completedLists;
    private User user;
    private Map<String, Long> achievementCounts;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private int locationRequestCode = 12;
    static final String LOCATIONNAME = "location name";
    static final String TASKLIST = "task list";
    static final String UID = "uid";
    static final String NOPERMISSIONS = "Location permission not granted";
    static final String LOCERROR = "Unable to get Location.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Complete a List");
        setContentView(R.layout.complete_list_activity);
        this.cancelButton = findViewById(R.id.cancel_complete);
        this.completeButton = findViewById(R.id.complete_this_list_button);
        this.shareLocation = findViewById(R.id.share_location_box);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            this.uid = extras.getString("uid");
            this.listIndex = extras.getInt("taskListComplete");
            mdataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
            mdataBase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user = snapshot.getValue(User.class);
                    listOfLists = user.getTaskLists();
                    taskList = listOfLists.get(listIndex);
                    achievementCounts = (HashMap<String, Long>) snapshot.child("AchievementCounts").getValue();

                    completedLists = user.getCompletedLists();
                    for(int i = 0; i < completedLists.size(); i++) {
                        if(completedLists.get(i).getListTitle().equals(PLACEHOLDERITEMCARD)){
                            completedLists.remove(i);
                            break;
                        }

                    }
                    createRecyclerView();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    finish();
                }
            });

        } else {
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    locationRequestCode);
        } else {
            new Thread(new getLocationTask()).start();
        }

        this.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED, returnIntent);
                finish(); //cancel should close this activity
            }
        });

        this.completeButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(shareLocation.isChecked() && !locationName.equals(LOCERROR)
                        && !locationName.equals(LOCATIONNAME)
                        && !locationName.equals(NOPERMISSIONS)){
                    taskList.setLocation(locationName);
                }

                for(Task each : taskList.getTaskList()){
                    if(!each.isCompleted()){
                        each.setCompleted(true);
                        String category = each.getAchievementCategory();
                        achievementCounts.put(category, achievementCounts.get(category) + 1);
                    }
                }

                taskList.setFinished(true);
                listOfLists.remove(taskList);
                if(listOfLists.isEmpty()){
                    listOfLists.add(new TaskList(PLACEHOLDERITEMCARD));
                }
                completedLists.add(taskList);
                mdataBase.child("taskLists").setValue(listOfLists);
                mdataBase.child("completedLists").setValue(completedLists);
                mdataBase.child("dueSoonestTask").setValue(user.findSoonestTask());
                mdataBase.child("AchievementCounts").setValue(achievementCounts);
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    private void createRecyclerView(){
        //recyclerView.findViewById(R.id.task_recycler_view);
        recyclerView = findViewById(R.id.task_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager rLayoutManager = new LinearLayoutManager(this);
        adapter = new CompleteListAdapter(this.taskList.getTaskList());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(rLayoutManager);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(LOCATIONNAME, this.locationName);
        savedInstanceState.putString(UID, this.uid);
        savedInstanceState.putInt(TASKLIST, this.listIndex);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        locationName = savedInstanceState.getString(LOCATIONNAME);
        uid = savedInstanceState.getString(UID);
        taskList = savedInstanceState.getParcelable(TASKLIST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == locationRequestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    return;
                } else {
                    new Thread(new getLocationTask()).start();
                }
            }
        } else {
            locationName = NOPERMISSIONS;
            //TODO add something for completing list.
        }
    }

        private class getLocationTask implements Runnable {

            @Override
            public void run() {
                locationName = LOCERROR;
                if (ActivityCompat.checkSelfPermission(CompleteListActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(CompleteListActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(CompleteListActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        //locationName = "";
                        Geocoder geocoder = new Geocoder(CompleteListActivity.this, Locale.getDefault());
                        try {
                            Address address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0);
                            StringBuilder builder = new StringBuilder();
                            builder.append(address.getLocality()).append(", ").append(address.getAdminArea()).append(", ").append(address.getCountryName());
                            locationName = builder.toString();
                            shareLocation.setText("Share Location: " + locationName);
                        }catch (IOException e){
                            locationName = LOCERROR;
                            Log.e("Location Error", e.getMessage());
                            e.printStackTrace();
                        }

                    }
                });

            }
        }

    }
