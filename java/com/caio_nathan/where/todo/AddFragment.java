package com.caio_nathan.where.todo;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.caio_nathan.where.todo.model.Task;


/**
 * A simple implements Parcelable {@link Fragment} subclass.
 */
public class AddFragment extends DialogFragment {
    public AddFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View view = inflater.inflate(R.layout.fragment_add, null);
        builder.setView(view)
                .setTitle(R.string.add_task)
                        // Add action buttons
                .setPositiveButton(R.string.action_add_task, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Add  task
                        EditText title = (EditText) view.findViewById(R.id.title);
                        EditText description = (EditText) view.findViewById(R.id.description);

                        Task task = new Task(title.getText().toString(),
                                description.getText().toString(), "");

                        if (AddFragment.this.getActivity() instanceof ListActivity) {
                            ((ListActivity) AddFragment.this.getActivity())
                                    .getTasks().add(task);
                            ((ListActivity) AddFragment.this.getActivity())
                                    .arrayAdapter.notifyDataSetChanged();
                        } else if (AddFragment.this.getActivity() instanceof MapsActivity) {
                            ((MapsActivity) AddFragment.this.getActivity())
                                    .getTasks().add(task);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}