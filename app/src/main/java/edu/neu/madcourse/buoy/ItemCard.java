package edu.neu.madcourse.buoy;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ItemCard {
    private String header;
    private ArrayList<InnerItemCard> headerList;
    private int expanded; //1 true, 0 false;

    public ItemCard(String header, ArrayList<InnerItemCard> headerList){
        this.header = header;
        this.headerList = headerList;
        this.expanded = 0;
    }

    public String getHeader() {
        return header;
    }

    public ArrayList<InnerItemCard> getHeaderList() {
        return headerList;
    }

    public boolean isExpanded(){
        return this.expanded != 0;
    }

    public void setExpanded(){
        if (this.expanded == 0){
            this.expanded = 1; //if false, set to true
        } else {
            this.expanded = 0;
        }
    }


}
