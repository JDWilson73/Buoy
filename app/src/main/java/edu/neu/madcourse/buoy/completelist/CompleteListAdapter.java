package edu.neu.madcourse.buoy.completelist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.buoy.R;
import edu.neu.madcourse.buoy.listobjects.Task;

public class CompleteListAdapter extends RecyclerView.Adapter<CompleteListAdapter.CompleteListViewHolder> {
    private List<Task> taskList;

    public CompleteListAdapter(List<Task> taskList){
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public CompleteListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.complete_list_item_card, parent, false);
        return new CompleteListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompleteListViewHolder holder, int position) {
        Task current = taskList.get(position);
        holder.task.setText(current.getTaskTitle());
        holder.taskDueDate.setText(current.getDueDate());

    }

    @Override
    public int getItemCount() {
        return this.taskList.size();
    }

    public static class CompleteListViewHolder extends RecyclerView.ViewHolder {
        TextView task;
        TextView taskDueDate;

        public CompleteListViewHolder(@NonNull View itemView) {
            super(itemView);
            task = itemView.findViewById(R.id.task_name);
            taskDueDate = itemView.findViewById(R.id.task_due_date_time);
        }
    }
}
