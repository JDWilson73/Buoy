package edu.neu.madcourse.buoy;

import android.os.Parcel;
import android.os.Parcelable;

public class InnerItemCard{

    private String header;
    private int checked; //0 = false, 1 = true;

    public InnerItemCard(String header){
        this.header = header;
        this.checked = 0;
    }

    public String getHeader() {
        return header;
    }

    public void setChecked() {
        if (this.checked == 0){
            this.checked = 1;
        }
        else {
            this.checked = 0;
        }
    }

    public boolean isChecked(){
        return this.checked != 0;
    }

}
