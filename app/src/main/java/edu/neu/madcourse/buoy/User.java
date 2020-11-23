package edu.neu.madcourse.buoy;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom User class: to write attributes to database, please remember to write getters/setters for it.
 */
public class User {
    String userName;
    String firstName;
    String lastName;
    String email;
    String uid; //no setters/getters for this-database does this, likely shouldn't change.
    Map <String, Integer> stickerList; //key: string name for sticker, value: #times received for each sticker
    int totalStickers;
    String token;

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







}


