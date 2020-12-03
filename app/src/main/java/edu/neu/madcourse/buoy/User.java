package edu.neu.madcourse.buoy;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import edu.neu.madcourse.buoy.ItemCard;

import edu.neu.madcourse.buoy.listobjects.Task;
import edu.neu.madcourse.buoy.listobjects.TaskList;

/**
 * Custom User class: to write attributes to database, please remember to write getters/setters for it.
 */
public class User {
    String userName;
    String firstName;
    String lastName;
    String email;
    ArrayList<ItemCard> itemCardArrayList;
    String uid; //no setters/getters for this-database does this, likely shouldn't change.
    Map <String, Integer> stickerList; //key: string name for sticker, value: #times received for each sticker
    int totalStickers;
    String token;
    List<TaskList> taskLists;

    // Friends' usernames stored instead of the entire User reference. Hopefully will keep
    // recursive BS from happening...
    List<String> friends;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public String getUid() {
        return uid;
    }

    public void setItemCardArrayList(ArrayList<ItemCard> itemCardArrayList) {
        this.itemCardArrayList = itemCardArrayList;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setStickerList(Map<String, Integer> stickerList) {
        this.stickerList = stickerList;
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
        this.itemCardArrayList = new ArrayList<>();
        ArrayList<InnerItemCard> list1 = new ArrayList<>();
        list1.add(new InnerItemCard("Make my first List"));

        itemCardArrayList.add(new ItemCard("Welcome to Buoy!", list1));

        List<TaskList> taskLists = new ArrayList<>();
        TaskList defaultList = new TaskList("testingList");

        edu.neu.madcourse.buoy.listobjects.Task defaultTask = new edu.neu.madcourse.buoy.listobjects.Task("testing", null, null, 2020,
                12, 1, 5, 12);

        defaultList.addTask(defaultTask);
        taskLists.add(defaultList);

        this.taskLists = taskLists;

        this.friends = new ArrayList<>();
        this.friends.add("default");
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

    public Map<String, Integer> getStickerList() {
        return stickerList;
    }

    public void setSticker_list(Map<String, Integer> sticker_list) {
        this.stickerList = sticker_list;
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

    public List<TaskList> getTaskLists() { return taskLists; }

    public ArrayList<ItemCard> getItemCardArrayList() {
        return itemCardArrayList;
    }


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


    // TODO make sticker send show only friends rather than all users
    // TODO add buttons to add/remove friend and text entry fields to input username of friend to remove
}


