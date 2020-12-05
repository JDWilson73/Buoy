package edu.neu.madcourse.buoy;

import android.os.Parcel;
import android.os.Parcelable;

import java.time.LocalDateTime;

import edu.neu.madcourse.buoy.listobjects.Task;

public class InnerItemCard implements Parcelable {

    private String header;
    private boolean checked; //false = false, true = true;
    private String date;

    //Constructor for Firebase
    public InnerItemCard(){
        this.header = "";
        this.checked = false;
        this.date = LocalDateTime.now().toString();
    }

    //Default constructor
    public InnerItemCard(String header){
        this.header = header;
        this.checked = false;
        this.date = LocalDateTime.now().toString();
    }

    //Constructor that turns a Task Object to Inner Item Card Object.
    public InnerItemCard(Task task){
        this.header = task.getTaskTitle();
        this.checked = task.isCompleted();
        this.date = task.getDueDate();
    }

    protected InnerItemCard(Parcel in) {
        header = in.readString();
        checked = in.readByte() != 0;
        date = in.readString();
    }

    public static final Creator<InnerItemCard> CREATOR = new Creator<InnerItemCard>() {
        @Override
        public InnerItemCard createFromParcel(Parcel in) {
            return new InnerItemCard(in);
        }

        @Override
        public InnerItemCard[] newArray(int size) {
            return new InnerItemCard[size];
        }
    };

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {this.header = header;}

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public void setChecked() {
        if (!this.checked){
            this.checked = true;
        }
        else {
            this.checked = false;
        }
    }

    public boolean isChecked(){
        return this.checked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(header);
        dest.writeByte((byte) (checked ? 1 : 0));
        dest.writeString(date);
    }
}
