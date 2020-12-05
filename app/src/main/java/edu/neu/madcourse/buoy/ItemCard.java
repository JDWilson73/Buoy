package edu.neu.madcourse.buoy;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ItemCard implements Parcelable{
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

    protected ItemCard(Parcel in) {
        header = in.readString();
        headerList = in.createTypedArrayList(InnerItemCard.CREATOR);
        expanded = in.readByte() != 0;
    }

    public static final Creator<ItemCard> CREATOR = new Creator<ItemCard>() {
        @Override
        public ItemCard createFromParcel(Parcel in) {
            return new ItemCard(in);
        }

        @Override
        public ItemCard[] newArray(int size) {
            return new ItemCard[size];
        }
    };

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

    public void addToHeaderList(InnerItemCard addItem){
        this.headerList.add(addItem);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(header);
        dest.writeTypedList(headerList);
        dest.writeByte((byte) (expanded ? 1 : 0));
    }
}
