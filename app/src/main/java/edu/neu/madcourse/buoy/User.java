package edu.neu.madcourse.buoy;

import java.util.HashMap;
import java.util.Map;

public class User {
    String userName;
    String firstName;
    String lastName;
    String email;
    String uid;
    Map <String, Integer> stickerList; //key: string name for sticker, value: #times received for each sticker
    int totalStickers;
    //String test;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }



    public User(String uid, String userName, String firstName, String lastName, String email) {
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
        this.email = email; //this is token. bc its broken.
        //this.test = test;


    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getTotalStickers() {
        return totalStickers;
    }

    public Map<String, Integer> getSticker_list() {
        return stickerList;
    }

    public String getUserName() {
        return userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setSticker_list(Map<String, Integer> sticker_list) {
        this.stickerList = sticker_list;
    }

    public void setTotalStickers(int totalStickers) {
        this.totalStickers = totalStickers;
    }

    //public void setToken(String test){this.test = test;}


}


