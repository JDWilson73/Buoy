package edu.neu.madcourse.buoy.listobjects;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class TaskList implements Parcelable {

    String listTitle;
    List<Task> taskList;
    boolean isFinished;
    String location;


    public TaskList() {
        this.listTitle = "default";
        this.taskList = new ArrayList<>();
        this.isFinished = false;
    }

    public TaskList(String listTitle) {
        this.listTitle = listTitle;
        this.taskList = new ArrayList<>();
        this.isFinished = false;
    }

    protected TaskList(Parcel in) {
        listTitle = in.readString();
        isFinished = in.readByte() != 0;
        location = in.readString();

    }

    public static final Creator<TaskList> CREATOR = new Creator<TaskList>() {
        @Override
        public TaskList createFromParcel(Parcel in) {
            return new TaskList(in);
        }

        @Override
        public TaskList[] newArray(int size) {
            return new TaskList[size];
        }
    };

    public void addTask(Task task) {
        this.taskList.add(task);
    }

    public String getListTitle() {
        return listTitle;
    }

    public void setListTitle(String listTitle) {
        this.listTitle = listTitle;
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    public boolean isFinished(){ return isFinished; }

    public void setFinished(boolean finished) {
            if (this.isFinished != finished) {
                for(Task task : this.taskList){
                    task.setCompleted(true);
                }
            this.isFinished = finished;
        }
    }

    public String getLocation(){
        return location;
    }

    public void setLocation(String newLocation){
        this.location = newLocation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(listTitle);
        dest.writeByte((byte) (isFinished ? 1 : 0));
        dest.writeString(location);
    }
}
