package edu.neu.madcourse.buoy.listobjects;

/*
Documentation:
LocalDateTime
https://docs.oracle.com/javase/8/docs/api/java/time/LocalDateTime.html
 */
import android.os.Parcel;
import android.os.Parcelable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

/**
 * This represents a task object. A task is part of list and has a category (badge purposes), title,
 * due date, completion status, and buoys associated with it.
 */
public class Task implements Parcelable {
    String taskTitle;
    String achievementCategory;
    String subCategory;
    boolean completed;
    String dueDate;
    ArrayList<Buoys> buoys;
    int likes;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy hh:mm a");

    public Task() {
        this.taskTitle = "default";
        this.completed = false;
        this.buoys = new ArrayList<>();
        this.likes = 0;
        this.dueDate = LocalDateTime.now().plusDays(14).format(formatter);
    }

    public Task(String title, String achievementCategory, String subCategory, int year,
                int month, int day, int hour, int min){
        this.taskTitle = title;
        LocalDateTime dateTime = LocalDateTime.of(year, month, day, hour, min);
        this.dueDate = dateTime.format(formatter);
        this.achievementCategory = achievementCategory;
        this.subCategory = subCategory;
        this.completed = false;
        this.buoys = new ArrayList<>();
        this.likes = 0;

//        Buoys testBuoy = new Buoys("You can Do it!",
//                new User("UID", "userName", "Test", "User", "email", "123"));
//        this.buoys.add(testBuoy);
//        this.buoys.add(testBuoy);
//        this.buoys.add(testBuoy);
    }

    protected Task(Parcel in) {
        taskTitle = in.readString();
        achievementCategory = in.readString();
        subCategory = in.readString();
        completed = in.readByte() != 0;
        dueDate = in.readString();
        likes = in.readInt();
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {

        if (this.completed != completed){
            //if completed = true, add 1 task completed to parent category, else category -1 task.
        }
        this.completed = completed;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public ArrayList<Buoys> getBuoys() {
        return buoys;
    }

    public void addBuoy(Buoys buoy){
        this.buoys.add(buoy);
        //this.buoys.sort(Comparable by date);
    }

    public void addLike(){
        this.likes ++;
    }

    public String getAchievementCategory() {
        return achievementCategory;
    }

    public void setAchievementCategory(String achievementCategory) {
        this.achievementCategory = achievementCategory;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    //if isOtherTaskDueSooner is true, then other task is due later.
    public boolean isOtherTaskDueSooner(Task other){
        LocalDateTime thisDue = LocalDateTime.parse(this.getDueDate(), formatter);
        LocalDateTime otherDue = LocalDateTime.parse(other.getDueDate(), formatter);
        //isBefore = true means that this is older than other
        return thisDue.isBefore(otherDue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return completed == task.completed &&
                likes == task.likes &&
                Objects.equals(taskTitle, task.taskTitle) &&
                Objects.equals(achievementCategory, task.achievementCategory) &&
                Objects.equals(subCategory, task.subCategory) &&
                Objects.equals(dueDate, task.dueDate) &&
                Objects.equals(buoys, task.buoys) &&
                Objects.equals(formatter, task.formatter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskTitle, achievementCategory, subCategory, completed, dueDate, buoys, likes, formatter);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(taskTitle);
        dest.writeString(achievementCategory);
        dest.writeString(subCategory);
        dest.writeByte((byte) (completed ? 1 : 0));
        dest.writeString(dueDate);
        dest.writeInt(likes);
    }
}
