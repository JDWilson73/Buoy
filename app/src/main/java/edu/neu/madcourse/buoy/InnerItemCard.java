package edu.neu.madcourse.buoy;

import android.os.Parcel;
import android.os.Parcelable;

public class InnerItemCard{

    private String header;
    private boolean checked; //false = false, true = true;
    public InnerItemCard(){
        this.header = "";
        this.checked = false;
    }
    public InnerItemCard(String header){
        this.header = header;
        this.checked = false;
    }

    public String getHeader() {
        return header;
    }

    public void setChecked() {
        if (this.checked == false){
            this.checked = true;
        }
        else {
            this.checked = false;
        }
    }

    public boolean isChecked(){
        return this.checked != false;
    }

}
