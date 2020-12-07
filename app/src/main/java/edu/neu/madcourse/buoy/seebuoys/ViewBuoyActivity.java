package edu.neu.madcourse.buoy.seebuoys;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import edu.neu.madcourse.buoy.R;
import edu.neu.madcourse.buoy.User;
import edu.neu.madcourse.buoy.listobjects.Task;
import edu.neu.madcourse.buoy.listobjects.TaskList;

public class ViewBuoyActivity extends AppCompatActivity {
    private Task currentTask;
    private String uid;
    private DatabaseReference mdataBase;
    private RecyclerView recyclerView;
    private ViewBuoyAdapter viewBuoyAdapter;
    private TextView taskName;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setTitle("Buoys");
        setContentView(R.layout.view_buoy_acty);
        taskName = findViewById(R.id.task_name_for_buoys);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            uid = extras.getString("uid");
            int indexForTaskList = extras.getInt("taskListforBuoy");
            int indexForTaskInTaskList = extras.getInt("taskinListforBuoy");
            mdataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
            mdataBase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    List<TaskList> listOfLists = user.getTaskLists();
                    TaskList taskList = listOfLists.get(indexForTaskList);
                    currentTask = taskList.getTaskList().get(indexForTaskInTaskList);
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

    }

    private void createRecyclerView(){
        recyclerView = findViewById(R.id.buoy_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager rLayoutManager = new LinearLayoutManager(this);
        viewBuoyAdapter = new ViewBuoyAdapter(this.currentTask.getBuoys());
        String taskHeader = currentTask.getTaskTitle();
        if(this.currentTask.getBuoys().isEmpty()){
            String text = "Sorry, there are no buoys for " + taskHeader + ".\nYou got this, friend!";
            taskName.setText(text);
        } else {
            taskName.setText(taskHeader);
        }
        recyclerView.setAdapter(viewBuoyAdapter);
        recyclerView.setLayoutManager(rLayoutManager);
    }
}
