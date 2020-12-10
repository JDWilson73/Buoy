package edu.neu.madcourse.buoy;
/*
Sources:
https://developersbreach.com/merge-multiple-adapters-with-concatadapter-android/
https://medium.com/swlh/expandable-list-in-android-with-mergeadapter-3a7f0cb56166
https://medium.com/androiddevelopers/merge-adapters-sequentially-with-mergeadapter-294d2942127a
https://developer.android.com/guide/topics/ui/controls/checkbox
https://medium.com/techmacademy/how-to-read-and-write-booleans-in-a-parcelable-class-99e5948db58d
https://stackoverflow.com/questions/22323974/how-to-get-city-name-by-latitude-longitude-in-android
https://medium.com/@douglas.iacovelli/android-send-result-back-through-multiple-activities-finished-or-not-4c2ba9e95112
 */

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import edu.neu.madcourse.buoy.InnerAdapter;
import edu.neu.madcourse.buoy.InnerItemCard;
import edu.neu.madcourse.buoy.ItemAdapter;
import edu.neu.madcourse.buoy.ItemCard;
import edu.neu.madcourse.buoy.R;
import edu.neu.madcourse.buoy.completelist.CompleteListActivity;
import edu.neu.madcourse.buoy.listobjects.Task;
import edu.neu.madcourse.buoy.listobjects.TaskList;
import edu.neu.madcourse.buoy.newtask.AddTaskDialogFragment;
import edu.neu.madcourse.buoy.seebuoys.ViewBuoyActivity;

// TODO: If we add achievements, add category spinners to dialog box.
public class userList extends AppCompatActivity implements AddTaskDialogFragment.AddTaskDialogListener {
    static final int REQUEST_CODE = 173;
    static final String PLACEHOLDERITEMCARD = "itemCard placeHolder";

    private ArrayList<ItemCard> itemCardArrayList = new ArrayList<>(); //item card list
    private ConcatAdapter concatAdapter; //main adapter merges all adapters together.

    private ArrayList<ItemAdapter> parentAdapters; //list of item card adapters
    private HashMap<ItemCard, InnerAdapter> innerAdapters; //list of inner lists mapped to item card as keys

    private RecyclerView recyclerView;
    private Button btnSubmit;
    private EditText newListInput;
    private String uid;//this user's id

    private DatabaseReference mdataBase;
    private FirebaseAuth mFirebaseAuth;
    private List<TaskList> userTaskList; //arrayList
    static final String UID = "userID";
    private User user;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy hh:mm a");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Your Lists");
        setContentView(R.layout.activity_user_list);
        btnSubmit = findViewById(R.id.userListSubmit);
        newListInput = findViewById(R.id.userListInput);

        createRecyclerView();

        uid = mFirebaseAuth.getInstance().getCurrentUser().getUid();
        mdataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        //populate user's list
        mdataBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                //itemCardArrayList = user.getItemCardArrayList();
                userTaskList = user.getTaskLists();
                taskListTranslateToItemCardLists(); //translate user's task lists to item card lists
                parentAdapters = new ArrayList<>();
                //set itemCardArrayList into parent Adapters list
                for (int i = 0; i < itemCardArrayList.size(); i++) {
                    ItemAdapter item = new ItemAdapter(itemCardArrayList.get(i));
                    parentAdapters.add(item);
                }
                setAdapters();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //make new list
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = newListInput.getText().toString();
                if (name.isEmpty()) {
                    newListInput.setError("List must have a name");
                } else if (name.length() > 15) {
                    newListInput.setError("List name is too long");
                } else {
                    newList(name); //add new list to DB
                    newListInput.setText("");

                    //close the keyboard
                    try {
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        // fine nevermind then I didn't want you to have fun anyway
                    }
                }
            }
        });

        BottomNavigationView navi = (BottomNavigationView) findViewById(R.id.navigation);
        navi.setItemIconTintList(null);
        navi.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.Home:
                        Intent home = new Intent(userList.this, HomeActivity.class);
                        startActivity(home);
                        break;
                    case R.id.Profile:
                        Intent profile = new Intent(userList.this, Profile.class);
                        startActivity(profile);
                        break;
                    case R.id.Friends:
                        Intent friends = new Intent(userList.this, FriendActivity.class);
                        startActivity(friends);
                        break;
                    case R.id.Settings:
                        Intent settings = new Intent(userList.this, UserSettingsActivity.class);
                        startActivity(settings);
                        break;
                    case R.id.Lists:
                        break;
                }

                return false;
            }
        });
    }



    /**
     * Sets Listeners for each Parent Adapter and Inner Adapter.
     */
    private void setAdapters() {

        for (int i = 0; i < this.parentAdapters.size(); i++) {
            ItemAdapter item = this.parentAdapters.get(i);
            this.concatAdapter.addAdapter(item);
            int finalI = i;

            item.setOnItemClickListener(new ItemAdapter.ItemClickListener() {
                @Override
                public synchronized void onItemClick() {
                    ItemCard itemCard = itemCardArrayList.get(finalI);
                    if (itemCard.isExpanded()) { //if inner list is already expanded, tap should close list.
                        concatAdapter.removeAdapter(Objects.requireNonNull(innerAdapters.get(itemCard)));
                        itemCard.setExpanded();
                    } else { //else expand list.
                        itemCard.setExpanded(); //set expanded to true
                        concatAdapterSet(); //show concat adapter list
                    }
                    item.notifyDataSetChanged();
                }

                @Override
                public synchronized void onDeletePressed() {
                    ItemCard card = itemCardArrayList.get(finalI);
                    parentAdapters.remove(item);
                    innerAdapters.remove(card);
                    concatAdapter.removeAdapter(item);
                    itemCardArrayList.remove(card);
                    concatAdapterSet();

                    userTaskList.remove(finalI);
                    if (userTaskList.size() == 0) {
                        TaskList defaultList = new TaskList(PLACEHOLDERITEMCARD);
                        userTaskList.add(defaultList);
                    }
                    mdataBase.child("taskLists").setValue(userTaskList);
                    mdataBase.child("dueSoonestTask").setValue(user.findSoonestTask());
                }

                @Override
                public synchronized void onTodoAddPressed() {
                    ItemCard card = itemCardArrayList.get(finalI);
                    launchNewTaskDialogBox(item, finalI, card);
                }

                @Override
                public void onCompletePressed() {
                    if(itemCardArrayList.get(finalI).getHeaderList().isEmpty()){
                        Toast.makeText(userList.this,
                                "There are no tasks in this list. List must have tasks to complete.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Intent completeList = new Intent(userList.this, CompleteListActivity.class);
                        completeList.putExtra("uid", uid);
                        completeList.putExtra("taskListComplete", finalI);
                        startActivityForResult(completeList, REQUEST_CODE);
                    }
                }

            });
        }
        populateInnerAdapterList();
    }

    public void newList(String name) {
        //putting new TaskList into DB
        TaskList newTaskList = new TaskList(name);
        this.userTaskList.add(newTaskList);
        mdataBase.child("taskLists").setValue(userTaskList).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //populating recyclerview with new list
                ArrayList<InnerItemCard> newList = new ArrayList<>(); //new inner arraylist
                ItemCard newCard = new ItemCard(name, newList);
                itemCardArrayList.add(newCard);

                ItemAdapter item = new ItemAdapter(newCard);
                parentAdapters.add(item);

                setAdapters();
                Toast.makeText(userList.this, "New List added Successfully!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                userTaskList.remove(newTaskList);
                Log.e("New List Creation Failure", e.getMessage());
                Toast.makeText(userList.this, "Failed to Add new List", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(UID, this.uid);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.uid = savedInstanceState.getString(UID);
        mdataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
    }

    //Refreshes activity after going to complete list activity.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            createRecyclerView();
            mdataBase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    userTaskList = user.getTaskLists();
                    taskListTranslateToItemCardLists(); //translate user's task lists to item card lists
                    parentAdapters = new ArrayList<>();
                    //set itemCardArrayList into parent Adapters list
                    for (int i = 0; i < itemCardArrayList.size(); i++) {
                        ItemAdapter item = new ItemAdapter(itemCardArrayList.get(i));
                        parentAdapters.add(item);
                    }
                    setAdapters();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Reading from DB error", error.getMessage());
                }
            });
        }
    }

    private void createRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view_nested);
        recyclerView.setHasFixedSize(true);

        this.concatAdapter = new ConcatAdapter();
        recyclerView.setAdapter(this.concatAdapter);

        RecyclerView.LayoutManager rLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(rLayoutManager);
    }

    //populate nested lists and associated inner adapters of parent.
    private void populateInnerAdapterList() {
        this.innerAdapters = new HashMap<>();
        for (int i = 0; i < this.itemCardArrayList.size(); i++) {
            TaskList taskList = this.userTaskList.get(i);
            List<Task> tasks = taskList.getTaskList();
            ArrayList<InnerItemCard> list = this.itemCardArrayList.get(i).getHeaderList();
            InnerAdapter adapter = new InnerAdapter(list);
            this.innerAdapters.put(this.itemCardArrayList.get(i), adapter);
            int finalI = i;

/*            achievementSpinner = findViewById(R.id.achievementSpinner);
            ArrayAdapter<CharSequence> catAdapt = new ArrayAdapter(this, android.R.layout.simple_spinner_item, categories.toArray());
            catAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            achievementSpinner.setAdapter(catAdapt);*/

            adapter.setOnInnerClickListener(new InnerAdapter.InnerItemClickListener() {
                @Override
                public void onItemClick(int pos) {
                    Intent viewBuoy = new Intent(userList.this, ViewBuoyActivity.class);
                    viewBuoy.putExtra("uid", uid);
                    viewBuoy.putExtra("taskListforBuoy", finalI);
                    viewBuoy.putExtra("taskinListforBuoy", pos);
                    startActivity(viewBuoy);
                }

                @Override
                public void onCheckClick(int pos) {
                    tasks.get(pos).setCompleted(!tasks.get(pos).isCompleted());
                    mdataBase.child("taskLists").setValue(userTaskList)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    int delta;
                                    if (tasks.get(pos).isCompleted()) {
                                        delta = 1;
                                    }
                                    else {
                                        delta = -1;
                                    }

                                    mdataBase.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String category = tasks.get(pos).getAchievementCategory();
                                            Map<String, Long> achievementCounts = (HashMap<String, Long>) snapshot.child("AchievementCounts").getValue();
                                            achievementCounts.put(category, achievementCounts.get(category) + delta);

                                            mdataBase.child("AchievementCounts").setValue(achievementCounts);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                                    mdataBase.child("dueSoonestTask").setValue(user.findSoonestTask());
                                    list.get(pos).setChecked();
                                    adapter.notifyDataSetChanged();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //if fail, reset task to opposite of what it is.
                                    tasks.get(pos).setCompleted(!tasks.get(pos).isCompleted());
                                    Toast.makeText(userList.this,
                                            "Complete Task failed.", Toast.LENGTH_SHORT).show();
                                }
                            });

                }

                @Override
                public void onDeleteClick(int pos) {
                    Task removedTask = tasks.get(pos);
                    tasks.remove(pos);
                    user.setTaskLists(userTaskList);
                    mdataBase.child("taskLists").setValue(userTaskList)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mdataBase.child("dueSoonestTask").setValue(user.findSoonestTask());
                                    list.remove(pos);
                                    adapter.notifyDataSetChanged();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //if fail, add task back in
                                    tasks.add(removedTask);
                                    Toast.makeText(userList.this,
                                            "Complete Task failed.", Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            });
        }
    }

    //concat needs to be sequential.
    private void concatAdapterSet() {
        for (Map.Entry<ItemCard, InnerAdapter> each : this.innerAdapters.entrySet()) {
            concatAdapter.removeAdapter(each.getValue());
        }
        int concatIndex = 0;
        for (int i = 0; i < this.parentAdapters.size(); i++) {
            ItemCard currentParent = this.itemCardArrayList.get(i);
            concatIndex++;
            if (currentParent.isExpanded()) {
                concatAdapter.addAdapter(concatIndex, Objects.requireNonNull(this.innerAdapters.get(currentParent)));
                concatIndex++;
            }
        }
    }

    /**
     * Translates a List of TaskList Objects into a List of ItemCard objects. TaskList Objects have
     * a List of Task Objects which is translated into InnerItemCard Objects for each Item Card.
     */
    private void taskListTranslateToItemCardLists() {
        itemCardArrayList = new ArrayList<>();
        for (int k = 0; k < this.userTaskList.size(); k++) {
            if (this.userTaskList.get(k).getListTitle().equals(PLACEHOLDERITEMCARD)) {
                this.userTaskList.remove(k);
                break;
            }
        }

        if (this.userTaskList.isEmpty()) {
            return;
        }

        for (int i = 0; i < this.userTaskList.size(); i++) {
            ArrayList<InnerItemCard> currentInnerList = new ArrayList<>();
            TaskList eachList = this.userTaskList.get(i);
            List<Task> tasks = eachList.getTaskList();
            if (!eachList.getListTitle().equals(PLACEHOLDERITEMCARD)) {
                for (int j = 0; j < tasks.size(); j++) {
                    Task eachTask = tasks.get(j);
                    if (!eachTask.getTaskTitle().equals(PLACEHOLDERITEMCARD)) {
                        currentInnerList.add(new InnerItemCard(eachTask));
                    }
                }
                this.itemCardArrayList.add(new ItemCard(eachList.getListTitle(), currentInnerList));
            }
        }
    }

    //Launch dialog fragment for adding a new task to given parent adapter and index.
    private void launchNewTaskDialogBox(ItemAdapter parentAdapter, int parentIndex, ItemCard parentCard) {
        FragmentManager fm = getSupportFragmentManager();
        DialogFragment newDialog = AddTaskDialogFragment.newInstance(parentAdapter, parentIndex, parentCard);
        newDialog.show(fm, "New Task");
    }


    //Positive Click Method for Dialog Box: creates new task object, writes to firebase and displays in app.
    @Override
    public void onPositiveClick(AddTaskDialogFragment dialog) {
        String todo = dialog.getToDo();
        int finalI = dialog.getParentIndex();
        ItemAdapter item = dialog.getParentAdapter();
        ItemCard card = dialog.getParentCard();
        String date = dialog.getDate();
        String time = dialog.getTime();
        String dateAndTime = date + " " + time;

        String achievementSelection = dialog.getSpinnerSelection();

        InnerItemCard newToDo = new InnerItemCard(todo, dateAndTime); //new inner card
        //Parses string of date and time into LocalDateTime Object

        LocalDateTime thisDate = LocalDateTime.now().plusDays(14);
        if(date != null && time != null){
            thisDate = LocalDateTime.parse(dateAndTime, formatter);
        }
        Task cardTask = new Task(todo, achievementSelection, null,
                thisDate.getYear(), thisDate.getMonthValue(),
                thisDate.getDayOfMonth(), thisDate.getHour(), thisDate.getMinute());

        dialog.dismiss();

        if (card == null || cardTask == null) {
            Toast.makeText(userList.this, "Error making new task.", Toast.LENGTH_SHORT).show();
        } else {
            userTaskList.get(finalI).getTaskList().add(cardTask); //add new task to user task list
            //listeners to make sure task is added successfully before adding to adapters.
            mdataBase.child("taskLists").setValue(userTaskList)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mdataBase.child("dueSoonestTask").setValue(user.findSoonestTask());
                            InnerAdapter innerListAdapter = innerAdapters.get(card);
                            card.addToHeaderList(newToDo);
                            innerListAdapter.notifyDataSetChanged();
                            item.notifyDataSetChanged();
                            Toast.makeText(userList.this, "Task added Successfully!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Write to DB new Task Failure", e.getMessage());
                            Toast.makeText(userList.this, "Error making new task", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}