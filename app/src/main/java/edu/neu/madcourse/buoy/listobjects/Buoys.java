package edu.neu.madcourse.buoy.listobjects;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import edu.neu.madcourse.buoy.User;

public class Buoys {
    String friendName;
    String comment;
    String commentDate;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy hh:mm a");

    public Buoys(){

    }

    public Buoys(String comment, String userName){
        this.friendName = userName;
        this.comment = comment;
        //should we set some character limit for a comment?
        this.commentDate = LocalDateTime.now().format(formatter); //set date to time commented.
    }

    public String getFriend() {
        return friendName;
    }

    public void setFriend(String userName) {
        this.friendName = userName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public void String(String commentDate) {
        this.commentDate = commentDate;
    }
}
