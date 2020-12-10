
package edu.neu.madcourse.buoy.newtask;
/*
Sources
https://guides.codepath.com/android/using-dialogfragment#passing-data-to-activity
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.buoy.ItemAdapter;
import edu.neu.madcourse.buoy.ItemCard;
import edu.neu.madcourse.buoy.R;
import edu.neu.madcourse.buoy.User;


public class AddTaskDialogFragment extends DialogFragment {
    private String toDo;
    private String date;
    private String time;
    private ItemAdapter parentAdapter;
    private int parentIndex;
    private ItemCard parentCard;
    AddTaskDialogListener listener;

    static final int REQUEST_CODE = 121;
    static final String PARENTADAPTER = "Parent Adapter";
    static final String PARENTCARD = "Parent Card";
    static final String PARENTINDEX = "Parent Index";

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd yyyy");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

    Spinner achievementSpinner;

    String spinnerSelection;

    public AddTaskDialogFragment(){

    }

    public static AddTaskDialogFragment newInstance(ItemAdapter parentAdapter, int parentIndex, ItemCard parentCard){
        AddTaskDialogFragment fragment = new AddTaskDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(PARENTADAPTER, parentAdapter);
        args.putParcelable(PARENTCARD, parentCard);
        args.putInt(PARENTINDEX, parentIndex);
        fragment.setArguments(args);
        return fragment;
    }

    public interface AddTaskDialogListener{
        void onPositiveClick(AddTaskDialogFragment dialog);
    }

    List<String> categories;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        this.parentAdapter = getArguments().getParcelable(PARENTADAPTER);
        this.parentCard = getArguments().getParcelable(PARENTCARD);
        this.parentIndex = getArguments().getInt(PARENTINDEX);

        LocalDateTime localDateTime = LocalDateTime.now().plusDays(14);
        this.date = localDateTime.format(dateFormatter);
        this.time = localDateTime.format(timeFormatter);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.new_task_dialog_layout, null);
        final EditText todoText = (EditText)dialogView.findViewById(R.id.to_do_task_name);
        final Button setDate = (Button)dialogView.findViewById(R.id.set_date);
        final Button setTime = (Button)dialogView.findViewById(R.id.set_time);
        achievementSpinner = (Spinner)dialogView.findViewById(R.id.achievementSpinner);

        setCategories();

        final FragmentManager fm = ((AppCompatActivity)getActivity()).getSupportFragmentManager();
        setDate.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                DialogFragment dateFragment = new DatePickerFragment();
                dateFragment.setTargetFragment(AddTaskDialogFragment.this, REQUEST_CODE);
                dateFragment.show(fm, "datePicker");
            }
        });
        setTime.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                DialogFragment timeFragment = new TimePickerFragment();
                timeFragment.setTargetFragment(AddTaskDialogFragment.this, REQUEST_CODE);
                timeFragment.show(fm, "timePicker");
            }
        });

        builder.setView(dialogView)
                .setTitle("Add a new Task to List" + this.parentCard.getHeader())
                .setPositiveButton("Add Task", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input = todoText.getText().toString();
                        if(!input.isEmpty()){

                            spinnerSelection = achievementSpinner.getSelectedItem().toString();

                            toDo = todoText.getText().toString();
                            listener.onPositiveClick(AddTaskDialogFragment.this);
                        }else {
                            todoText.setError("Task cannot be empty, please try again.");
                        }
                    }
                })
                .setNegativeButton("Cancel Add Task", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddTaskDialogFragment.this.getDialog().dismiss();
                    }
                });

        return builder.create();

    }


    private synchronized void setCategories() {
        DatabaseReference mdataBase = FirebaseDatabase.getInstance().getReference("AchievementCategories");
        mdataBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categories = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    categories.add(child.getValue().toString());
                }

                ArrayAdapter<CharSequence> catAdapt = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, categories.toArray());
                catAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                achievementSpinner.setAdapter(catAdapt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            listener = (AddTaskDialogListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException("Activity must implement AddTaskDialogListener");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data.getStringExtra("selectedDate") != null){
            this.date = data.getStringExtra("selectedDate");
        }

        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data.getStringExtra("selectedTime") != null){
            this.time = data.getStringExtra("selectedTime");
        }
    }

    public String getToDo() {
        return toDo;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public ItemAdapter getParentAdapter() {
        return parentAdapter;
    }

    public int getParentIndex() {
        return parentIndex;
    }

    public ItemCard getParentCard() {
        return parentCard;
    }

    public String getSpinnerSelection() {
        return spinnerSelection;
    }

}