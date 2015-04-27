package com.caio_nathan.where.todo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.caio_nathan.where.todo.model.Task;
import com.caio_nathan.where.todo.model.TasksDbHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MapsActivity extends FragmentActivity implements LocationListener, OnMarkerDragListener {

    final String TAG = this.getClass().getSimpleName();
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private double userLat = 0;
    private double userLng = 0;
    private LocationManager locationManager;
    private String provider;
    private ArrayList<Task> taskArray;
    public TasksDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mDbHelper = new TasksDbHelper(this);

        // Tasks
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            this.taskArray = extras.getParcelableArrayList("TASK_ARRAY");
        } else {
            this.taskArray = mDbHelper.getTasks();
        }

        // Get the location manager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // check if enabled and if not send user to the GSP settings
        // Better solution would be to display a dialog and suggesting to
        // go to the settings
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            Log.e(TAG, "Error");
        }
        setUpMapIfNeeded(this.userLat, this.userLng);

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                String address = MapsActivity.this.getAddressFromLocation(point.latitude,
                                    point.longitude);
                if (address != null) {
                    Task t = new Task();
                    t.setLat(point.latitude);
                    t.setLng(point.longitude);
                    t.setAddress(address);
                    taskArray.add(t);
                    AddFragment addFragment = AddFragment.newInstance(1);
                    addFragment.show(getSupportFragmentManager(), "Add task");
                }
            }
        });
    }

    public String getAddressFromLocation(double latitude, double longitude) {
        Geocoder geoCoder = new Geocoder(this.getApplicationContext());
        List<Address> addresses = null;
        try {
            addresses = geoCoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if ((addresses != null ? addresses.size() : 0) > 0) {
            String result = addresses.get(0).getFeatureName() + ", "
                            + addresses.get(0).getLocality() + ", "
                            + addresses.get(0).getAdminArea() + ", "
                            + addresses.get(0).getCountryName();
            return result;
        }
        return null;
    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded(userLat, userLng);
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.userLat = location.getLatitude();
        this.userLng = location.getLongitude();

        Iterator i = this.taskArray.iterator();
        while(i.hasNext()) {
            Task t = (Task) i.next();
            if (!t.isShowed()) {
                Location taskLocation = new Location(t.getTitle());
                taskLocation.setLatitude(t.getLat());
                taskLocation.setLongitude(t.getLng());
                double distance = taskLocation.distanceTo(location);
                if (distance < 1000) {
                    Toast.makeText(this, "You are near to task '" + t.getTitle() + "'!",
                            Toast.LENGTH_SHORT).show();
                    t.setShowed(true);
                    //mDbHelper.updateTask(t);
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(this)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle(t.getTitle())
                                    .setContentText(t.getDescription());
                    // Creates an explicit intent for an Activity in your app
                    Intent resultIntent = new Intent(this, TaskActivity.class);
                    resultIntent.putExtra("TASK", t);

                    // The stack builder object will contain an artificial back stack for the
                    // started Activity.
                    // This ensures that navigating backward from the Activity leads out of
                    // your application to the Home screen.
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                    // Adds the back stack for the Intent (but not the Intent itself)
                    stackBuilder.addParentStack(TaskActivity.class);
                    // Adds the Intent that starts the Activity to the top of the stack
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    // mId allows you to update the notification later on.
                    mNotificationManager.notify(((int) t.getId()), mBuilder.build());
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        if (TAG.equals("MapsActivity")) {
            MenuItem item = menu.findItem(R.id.action_map);
            item.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_listview:
                Intent i = new Intent(this, ListActivity.class);
                i.putParcelableArrayListExtra("TASK_ARRAY", this.taskArray);
                finish();
                startActivity(i);
                return true;
            case R.id.action_add_task:
                AddFragment addFragment = AddFragment.newInstance(0);
                addFragment.show(getSupportFragmentManager(), "Add task");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded(double lat, double lng) {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerDragListener(this);
        refreshMap();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(this.userLat, this.userLng), 12.0f));
    }

    public void refreshMap() {
        mMap.clear();
        Iterator i = this.taskArray.iterator();
        while(i.hasNext()) {
            Task t = (Task) i.next();
            mMap.addMarker(new MarkerOptions().position(new LatLng(t.getLat(),
                    t.getLng())).title(t.getTitle()).draggable(true));
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        for(Task t : taskArray) {
            if (t.getTitle().equals(marker.getTitle())) {
                t.setShowed(false);
                t.setLat(marker.getPosition().latitude);
                t.setLng(marker.getPosition().longitude);
                t.setAddress(getAddressFromLocation(marker.getPosition().latitude,
                        marker.getPosition().longitude));
                mDbHelper.updateTask(t);
            }
        }
    }
    // Getters and Setters
    public ArrayList<Task> getTasks() {
        return taskArray;
    }
}