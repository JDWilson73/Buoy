package edu.neu.madcourse.buoy.listobjects;

import java.time.LocalDateTime;

import edu.neu.madcourse.buoy.User;

public class Buoys {
    User friend;
    String comment;
    LocalDateTime commentDate;

    public Buoys(String comment, User user){
        this.friend = user;
        this.comment = comment;
        //should we set some character limit for a comment?
        this.commentDate = LocalDateTime.now(); //set date to time commented.
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

    public LocalDateTime getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(LocalDateTime commentDate) {
        this.commentDate = commentDate;
    }
}
