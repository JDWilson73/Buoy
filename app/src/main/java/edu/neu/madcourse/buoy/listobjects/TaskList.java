package edu.neu.madcourse.buoy.listobjects;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class TaskList {

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
}
