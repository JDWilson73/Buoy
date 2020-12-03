package edu.neu.madcourse.buoy;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ItemCard {
    private String header;
    private ArrayList<InnerItemCard> headerList;
    private boolean expanded; //true true, false false;
    public ItemCard(){
        this.header  = "";
        this.headerList = new ArrayList<InnerItemCard>();
        this.expanded = false;
    }
    public ItemCard(String header, ArrayList<InnerItemCard> headerList){
        this.header = header;
        this.headerList = headerList;
        this.expanded = false;
    }

    public String getHeader() {
        return header;
    }

    public ArrayList<InnerItemCard> getHeaderList() {
        return headerList;
    }

    public boolean isExpanded(){
        return this.expanded != false;
    }

    public void setExpanded(){
        if (this.expanded == false){
            this.expanded = true; //if false, set to true
        } else {
            this.expanded = false;
        }
    }


}
