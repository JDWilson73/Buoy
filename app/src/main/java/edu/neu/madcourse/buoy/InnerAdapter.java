package edu.neu.madcourse.buoy;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * Class for expanded items of parent list--> i.e. below the header. Can be tasks or buoy's of tasks
 */
public class InnerAdapter extends RecyclerView.Adapter<InnerAdapter.InnerViewHolder> {
    private ArrayList<InnerItemCard> innerListItems;
    private InnerItemClickListener listener;

    //Put in methods for when clicking the item card.
    public interface InnerItemClickListener {
        void onItemClick(int pos);
        void onCheckClick(int pos);
        void onDeleteClick(int pos);
    }

    public void setOnInnerClickListener(InnerItemClickListener listener) {
        this.listener = listener;
    }

    public static class InnerViewHolder extends RecyclerView.ViewHolder{
        public CheckBox checkItem;
        public TextView text;
        public FloatingActionButton delete;

        public InnerViewHolder(@NonNull View itemView, final InnerItemClickListener listener) {
            super(itemView);
            checkItem = itemView.findViewById(R.id.mini_header);
            text = itemView.findViewById(R.id.more_details);
            delete = itemView.findViewById(R.id.task_delete_button);

            itemView.setOnClickListener(v -> {
                if(listener != null){
                    int position = getBindingAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        listener.onItemClick(position);
                    }
                }
            });

            checkItem.setOnClickListener(v -> {
                if(listener != null){
                    int position = getBindingAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        listener.onCheckClick(position);
                    }
                }
            });

            delete.setOnClickListener(v -> {
                if(listener != null){
                    int position = getBindingAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        listener.onDeleteClick(position);
                    }
                }
            });
        }
    }

    public InnerAdapter(ArrayList<InnerItemCard> innerList){
        this.innerListItems = innerList;
    }

    @NonNull
    @Override
    public InnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_expanded, parent, false);
        return new InnerViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerViewHolder holder, int position) {
        InnerItemCard current = innerListItems.get(position);
        holder.checkItem.setText(current.getHeader());
        holder.text.setText(current.getDate());
        holder.checkItem.setChecked(current.isChecked());
    }

    @Override
    public int getItemCount() {
        return innerListItems.size();
    }
}
