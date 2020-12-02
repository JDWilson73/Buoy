package edu.neu.madcourse.buoy.listobjects;

import java.time.LocalDateTime;

import edu.neu.madcourse.buoy.User;

public class Buoys {
    User friend;
    String comment;
    String commentDate;

    public Buoys(){

    }

    public Buoys(String comment, User user){
        this.friend = user;
        this.comment = comment;
        //should we set some character limit for a comment?
        this.commentDate = LocalDateTime.now().toString(); //set date to time commented.
    }

    public User getFriend() {
        return friend;
    }

    public void setFriend(User friend) {
        this.friend = friend;
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
