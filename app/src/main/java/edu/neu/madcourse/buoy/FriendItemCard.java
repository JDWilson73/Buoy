package edu.neu.madcourse.buoy;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

public class FriendItemCard implements Parcelable {
    private String userName;
    private String firstName;
    private String lastName;
    private String userID;

    public FriendItemCard(String userName, String firstName, String lastName, String userID){
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userID = userID;
    }

    protected FriendItemCard(Parcel in){
        userName = in.readString();
        firstName = in.readString();
        lastName = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(firstName);
        dest.writeString(lastName);
    }



//    @Override
//    public void onClick(View view){
//        switch (view.getId()){
//            case R.id.do_it:
//                break;
//            case R.id.good_vibes:
//                break;
//            case R.id.keep_it_up:
//                break;
//
//        }
//    }
}
