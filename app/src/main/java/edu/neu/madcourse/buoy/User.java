package edu.neu.madcourse.buoy;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import edu.neu.madcourse.buoy.ItemCard;

import edu.neu.madcourse.buoy.listobjects.AchievementCategory;
import edu.neu.madcourse.buoy.listobjects.Task;
import edu.neu.madcourse.buoy.listobjects.TaskList;

/**
 * Custom User class: to write attributes to database, please remember to write getters/setters for it.
 */
public class User {
    static final String PLACEHOLDERITEMCARD = "itemCard placeHolder";
    String userName;
    String firstName;
    String lastName;
    String email;
    //ArrayList<ItemCard> itemCardArrayList; //*
    String uid; //no setters/getters for this-database does this, likely shouldn't change.
    Map <String, Integer> stickerList; //key: string name for sticker, value: #times received for each sticker
    int totalStickers;
    String token;
    List<TaskList> taskLists;
    List<TaskList> completedLists;
    int buoysSent;

    // Friends' usernames stored instead of the entire User reference. Hopefully will keep
    // recursive BS from happening...
    List<String> friends;

    Task dueSoonestTask;

    public HashMap<String, Integer> getAchievementCounts() {
        return achievementCounts;
    }

    public void setAchievementCounts(HashMap<String, Integer> achievementCounts) {
        this.achievementCounts = achievementCounts;
    }

    HashMap<String, Integer> achievementCounts;
    DatabaseReference mdataBase;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    public User(String uid, String userName, String firstName, String lastName, String email, String token) {
        this.uid = uid;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.stickerList = new HashMap<>();
        //initialize sticker map with sticker names/values
        stickerList.put("doIt", 0);
        stickerList.put("keepUp", 0);
        stickerList.put("goodVibes", 0);
        this.totalStickers = 0;
        this.email = email;
        this.token = token;
//        this.achievementCounts = new HashMap<>();

        //Placeholder TaskList so attribute doesn't disappear from firebase.
        List<TaskList> taskLists = new ArrayList<>();
        TaskList defaultList = new TaskList("My First List!");
        taskLists.add(defaultList);
        this.taskLists = taskLists;

        List<TaskList> completedList = new ArrayList<>();
        TaskList defList = new TaskList(PLACEHOLDERITEMCARD);
        completedList.add(defList);
        this.completedLists = completedList;

        this.friends = new ArrayList<>();
        this.friends.add("default");

        //set a default due soonest task as new user has no new tasks.
        LocalDateTime now = LocalDateTime.now();
        this.dueSoonestTask = new Task("PlaceHolder Task", null, null, now.getYear(),
                now.getMonthValue(), now.getDayOfMonth(), now.getHour(), now.getMinute());

        this.buoysSent = 0;

        achievementCounts = new HashMap<>();

        mdataBase = FirebaseDatabase.getInstance().getReference().child("AchievementCategories");
        mdataBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public synchronized void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    String category = child.getKey();
                    achievementCounts.put(category, 0);
                }

                FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("AchievementCounts").setValue(achievementCounts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken(){return token; }

    public void setToken(String token){this.token = token;}

    public int getTotalStickers() {return totalStickers;}

    public void setTotalStickers(int totalStickers) {
        this.totalStickers = totalStickers;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Map<String, Integer> getStickerList() {
        return stickerList;
    }

    public void setStickerList(Map<String, Integer> stickerList) {
        this.stickerList = stickerList;
    }

    public List<TaskList> getTaskLists() { return taskLists; }

    public void setTaskLists(List<TaskList> taskLists) { this.taskLists = taskLists; }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public void addFriend(User other) {
        this.friends.add(other.userName);
        other.friends.add(this.userName);
    }

    public void removeFriend(User other) {
        this.friends.remove(other.userName);
        other.friends.remove(this.userName);
    }

    public List<TaskList> getCompletedLists() {
        return completedLists;
    }

    public void setCompletedLists(List<TaskList> completedLists) {
        this.completedLists = completedLists;
    }

    public Task getDueSoonestTask() {
        return dueSoonestTask;
    }

    public void setDueSoonestTask(Task dueSoonestTask) {
        this.dueSoonestTask = dueSoonestTask;
    }

    /**
     * call this method to find the soonest task due. use setSoonestTask to set it for user object.
     * @return
     */
    public Task findSoonestTask() {
        LocalDateTime minTime = LocalDateTime.now().minusYears(100);
        Task current = new Task("PlaceHolder Task", null, null,
                minTime.getYear(), minTime.getMonthValue(), minTime.getDayOfMonth(),
                minTime.getHour(), minTime.getMinute());
        for(TaskList each : this.taskLists){
            for(Task eachTask : each.getTaskList()){
                if(!eachTask.isCompleted()) { //if the task isn't completed
                    if (current.isOtherTaskDueSooner(eachTask)) { //check if each task is due sooner than current
                        current = eachTask; //if each task is due sooner than current, each task is set to current.
                    }
                }
            }
        }
        return current;
    }

    public int getBuoysSent() {
        return buoysSent;
    }

    public void setBuoysSent(int buoysSent) {
        this.buoysSent = buoysSent;
    }


    // TODO make sticker send show only friends rather than all users
    // TODO add buttons to add/remove friend and text entry fields to input username of friend to remove
}


