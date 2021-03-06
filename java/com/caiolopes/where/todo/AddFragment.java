package com.caiolopes.where.todo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.caiolopes.where.todo.model.Task;

import java.io.IOException;
import java.util.List;


/**
 * Dialog Fragment class.
 * @author Caio Lopes
 * @version 1.0
 */
public class AddFragment extends DialogFragment {
    public AddFragment() {
    }

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     * @param type
     * @param userLat
     * @param userLng
     * @return instance of this fragment
     */
    public static AddFragment newInstance(int type, double userLat, double userLng) {
        AddFragment frag = new AddFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putDouble("lat", userLat);
        args.putDouble("lng", userLng);
        frag.setArguments(args);
        return frag;
    }

    /**
     * Handle the all different types of insertion of a task.
     * @param savedInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final int type = getArguments().getInt("type");
        final double userLat = getArguments().getDouble("lat");
        final double userLng = getArguments().getDouble("lng");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View view;
        if (type == 0) {
            view = inflater.inflate(R.layout.fragment_add, null);
        } else {
            view = inflater.inflate(R.layout.fragment_add_without_address, null);
        }

        builder.setView(view)
                .setTitle(R.string.add_task)
                        // Add action buttons
                .setPositiveButton(R.string.action_add_task, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Add  task
                        EditText title = (EditText) view.findViewById(R.id.title);
                        EditText description = (EditText) view.findViewById(R.id.description);
                        if (type == 0) {
                            EditText address = (EditText) view.findViewById(R.id.address);
                            CheckBox currentLocation = (CheckBox) view.findViewById(R.id.current_location);

                            Geocoder geoCoder = new Geocoder(AddFragment.this.getActivity());
                            List<Address> addresses = null;
                            if (!currentLocation.isChecked()) {
                                try {
                                    addresses = geoCoder.getFromLocationName(address.getText().toString(), 5);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    addresses = geoCoder.getFromLocation(userLat, userLng, 1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            if ((addresses != null ? addresses.size() : 0) > 0) {
                                Task task;
                                if (!currentLocation.isChecked()) {
                                    task = new Task(title.getText().toString(),
                                            description.getText().toString(),
                                            address.getText().toString(),
                                            addresses.get(0).getLatitude(),
                                            addresses.get(0).getLongitude());
                                } else {
                                    String addr = addresses.get(0).getFeatureName() + ", "
                                            + addresses.get(0).getLocality() + ", "
                                            + addresses.get(0).getAdminArea() + ", "
                                            + addresses.get(0).getCountryName();
                                    task = new Task(title.getText().toString(),
                                            description.getText().toString(),
                                            addr,
                                            userLat,
                                            userLng);
                                }

                                if (AddFragment.this.getActivity() instanceof ListActivity) {
                                    ListActivity activity = ((ListActivity) AddFragment.this.getActivity());
                                    task.setId(activity.mDbHelper.addTask(task));
                                    activity.getTasks().add(task);
                                    activity.titleArray.add(task.getTitle());
                                    activity.arrayAdapter.notifyDataSetChanged();
                                } else if (AddFragment.this.getActivity() instanceof MapsActivity) {
                                    MapsActivity activity = ((MapsActivity) AddFragment.this.getActivity());
                                    task.setId(activity.mDbHelper.addTask(task));
                                    activity.getTasks().add(task);
                                    activity.refreshMap();
                                }
                            }
                        } else {
                            if (AddFragment.this.getActivity() instanceof MapsActivity) {
                                MapsActivity activity = ((MapsActivity) AddFragment.this.getActivity());
                                int index = activity.getTasks().size() - 1;
                                Task task = activity.getTasks().get(index);
                                task.setTitle(title.getText().toString());
                                task.setId(activity.mDbHelper.addTask(task));
                                task.setDescription(description.getText().toString());
                                activity.refreshMap();
                            }
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (type == 1) {
                            if (AddFragment.this.getActivity() instanceof MapsActivity) {
                                MapsActivity activity = ((MapsActivity) AddFragment.this.getActivity());
                                int index = activity.getTasks().size() - 1;
                                activity.getTasks().remove(index);
                            }
                        }
                        AddFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}