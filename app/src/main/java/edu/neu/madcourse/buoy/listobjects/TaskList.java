package edu.neu.madcourse.buoy.listobjects;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class TaskList {

    String listTitle;
    List<Task> taskList;

    public TaskList() {
        this.listTitle = "default";
        this.taskList = new ArrayList<>();
    }

    public TaskList(String listTitle) {
        this.listTitle = listTitle;

        this.taskList = new ArrayList<>();
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
}
