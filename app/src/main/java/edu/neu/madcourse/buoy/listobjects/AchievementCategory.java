package edu.neu.madcourse.buoy.listobjects;

import java.util.ArrayList;
/*
Fitness:
    Cardio
    Strength
    Flexibility
    Sports
Household Chores:
    Cooking
    Cleaning
    Laundry
    Organizing
    Pet Care
Outdoor Chores:
    Lawn Care
    Gardening
    Vehicle Care
Productivity:
    Projects
    Time management
    Notes
    Studying
    Assignments
Hobbies:
    Arts and Crafts
    Culinary
    Music
    Performing Arts
Other:
*/
public class AchievementCategory {
    String categoryTitle;
    ArrayList<String> subCategories;
    String color;
    int tasksCompleted;

    public AchievementCategory(String title, String color, ArrayList<String> subCategories){
        this.categoryTitle = title;
        this.color = color;
        this.subCategories = subCategories;
        this.tasksCompleted = 0;
    }

    public void addCompleteTask(){
        this.tasksCompleted ++;
    }

    public void addSubCategory(String subCategory){
        this.subCategories.add(subCategory);
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public ArrayList<String> getSubCategories() {
        return subCategories;
    }

    public String getColor() {
        return color;
    }

    public int getTasksCompleted() {
        return tasksCompleted;
    }

}
