package edu.neu.madcourse.buoy.seebuoys;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.neu.madcourse.buoy.R;
import edu.neu.madcourse.buoy.listobjects.Buoys;

public class ViewBuoyAdapter extends RecyclerView.Adapter<ViewBuoyAdapter.ViewBuoyViewHolder> {
    private ArrayList<Buoys> buoyList;

    public ViewBuoyAdapter(ArrayList<Buoys> buoyList) {
        this.buoyList = buoyList;
    }

    @NonNull
    @Override
    public ViewBuoyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.buoy_item_card, parent, false);
        return new ViewBuoyAdapter.ViewBuoyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewBuoyViewHolder holder, int position) {
        Buoys current = buoyList.get(position);
        StringBuilder friendString = new StringBuilder();
        friendString.append(current.getFriend());
        holder.friend.setText(friendString.toString());
        holder.buoy.setText(current.getComment());
        holder.buoyDate.setText(current.getCommentDate());
    }

    @Override
    public int getItemCount() {
        return this.buoyList.size();
    }


    public static class ViewBuoyViewHolder extends RecyclerView.ViewHolder {
        TextView friend;
        TextView buoy;
        TextView buoyDate;

        public ViewBuoyViewHolder(@NonNull View itemView) {
            super(itemView);

            friend = itemView.findViewById(R.id.friendName);
            buoy = itemView.findViewById(R.id.buoy_comment);
            buoyDate = itemView.findViewById(R.id.buoy_date);
        }
    }

}
