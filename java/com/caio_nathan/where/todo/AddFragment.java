package com.caio_nathan.where.todo;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.caio_nathan.where.todo.model.Task;

import java.io.IOException;
import java.util.List;


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
                        EditText address = (EditText) view.findViewById(R.id.address);

                        Geocoder geoCoder = new Geocoder(AddFragment.this.getActivity());
                        List<Address> addresses = null;
                        try {
                            addresses = geoCoder.getFromLocationName(address.getText().toString(), 5);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if ((addresses != null ? addresses.size() : 0) > 0) {
                            //GeoPoint p = new GeoPoint((int) (addresses.get(0).getLatitude() * 1E6),
                            //        (int) (addresses.get(0).getLongitude() * 1E6));

                            Task task = new Task(title.getText().toString(),
                                    description.getText().toString(), address.getText().toString(),
                                    addresses.get(0).getLatitude(), addresses.get(0).getLongitude());

                            if (AddFragment.this.getActivity() instanceof ListActivity) {
                                ((ListActivity) AddFragment.this.getActivity())
                                        .getTasks().add(task);
                                ((ListActivity) AddFragment.this.getActivity())
                                        .arrayAdapter.notifyDataSetChanged();
                            } else if (AddFragment.this.getActivity() instanceof MapsActivity) {
                                ((MapsActivity) AddFragment.this.getActivity())
                                        .getTasks().add(task);
                                ((MapsActivity) AddFragment.this.getActivity())
                                        .refreshMap();
                            }
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