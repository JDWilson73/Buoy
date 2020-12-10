package edu.neu.madcourse.buoy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;


public class AddBuoyDialogBox extends DialogFragment {
    private String buoyCommentString;
    private int pos;
    static final String POSITION = "position";

    AddBuoyDialogListener listener;

    public AddBuoyDialogBox(){

    }

    public static AddBuoyDialogBox newInstance(int pos){
        AddBuoyDialogBox fragment = new AddBuoyDialogBox();
        Bundle args = new Bundle();
        args.putInt(POSITION, pos);
        fragment.setArguments(args);
        return fragment;
    }

    public interface AddBuoyDialogListener{
        void onPositiveClick(AddBuoyDialogBox dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        this.pos = getArguments().getInt(POSITION);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.send_buoy_dialog, null);
        final EditText buoyComment = (EditText)dialogView.findViewById(R.id.add_buoy_comment);

        builder.setView(dialogView).setTitle("Float a buoy!")
                .setPositiveButton("Send buoy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input = buoyComment.getText().toString();
                        if(!input.isEmpty()){
                            buoyCommentString = input;
                            listener.onPositiveClick(AddBuoyDialogBox.this);
                        }else {
                            buoyComment.setError("Comment cannot be empty, please try again.");
                        }
                    }
                })
                .setNegativeButton("Cancel Buoy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddBuoyDialogBox.this.getDialog().dismiss();
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try {
            listener = (AddBuoyDialogListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException("Activity must implement AddBuoyDialogListener");
        }
    }

    public String getBuoyCommentString() {
        return buoyCommentString;
    }


    public int getPos() {
        return pos;
    }

}
