package edu.neu.madcourse.buoy;

import java.util.HashMap;
import java.util.Map;

public class User {
    String userName;
    String firstName;
    String lastName;
    Map <String, Integer> sticker_list;
    int totalStickers;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userName, String firstName, String lastName){
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.sticker_list = new HashMap<>();
        this.totalStickers = 0;
        }

    public int getTotalStickers() {
        return totalStickers;
    }

    public Map<String, Integer> getSticker_list() {
        return sticker_list;
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
        this.sticker_list = sticker_list;
    }

    public void setTotalStickers(int totalStickers) {
        this.totalStickers = totalStickers;
    }
}


