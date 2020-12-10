package edu.neu.madcourse.buoy;

import android.os.Parcel;
import android.os.Parcelable;

public class FriendItemCard implements Parcelable {
    private String userName;
    private String firstName;
    private String lastName;
    private String userID;
    private String soonestDueTask;
    private String dueDate;

    public FriendItemCard(String userName, String firstName, String lastName, String userID, String soonestDueTask, String dueDate){
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userID = userID;
        this.soonestDueTask = soonestDueTask;
        this.dueDate = dueDate;
    }

    protected FriendItemCard(Parcel in){
        userName = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        soonestDueTask = in.readString();
        dueDate = in.readString();
        userID = in.readString();
    }

    public static final Creator<FriendItemCard> CREATOR = new Creator<FriendItemCard>(){
        @Override
        public FriendItemCard createFromParcel(Parcel source) {
            return new FriendItemCard(source);
        }

        @Override
        public FriendItemCard[] newArray(int size) {
            return new FriendItemCard[size];
        }


    };

    public String getUserName() {
        return userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserID(){
        return userID;
    }

    public String getSoonestDueTask() {
        return soonestDueTask;
    }

    public String getDueDate() {
        return dueDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(userID);
        dest.writeString(soonestDueTask);
        dest.writeString(dueDate);
    }

}
