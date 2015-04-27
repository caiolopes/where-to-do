package com.caio_nathan.where.todo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.caio_nathan.where.todo.model.Task;
import com.caio_nathan.where.todo.model.TasksDbHelper;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by caiolopes on 4/23/15.
 */
public class ListActivity extends FragmentActivity {
    final String TAG = this.getClass().getSimpleName();
    private ArrayList<Task> taskArray;
    public ArrayList<String> titleArray;
    public ArrayAdapter<String> arrayAdapter;
    public TasksDbHelper mDbHelper;
    private double userLat;
    private double userLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mDbHelper = new TasksDbHelper(this);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            this.taskArray = extras.getParcelableArrayList("TASK_ARRAY");
            this.userLat = extras.getDouble("USER_LAT");
            this.userLng = extras.getDouble("USER_LNG");
        } else {
            this.taskArray = mDbHelper.getTasks();
        }

        ListView lv = (ListView) findViewById(R.id.task_list);

        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.

        Iterator i = this.taskArray.iterator();
        titleArray = new ArrayList<>();
        while(i.hasNext()) {
            Task t = (Task) i.next();
            titleArray.add(t.getTitle());
        }

        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                titleArray);

        lv.setAdapter(arrayAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
                //String value = (String)adapter.getItemAtPosition(position);
                // assuming string and if you want to get the value on click of list item
                // do what you intend to do on click of listview row
                Intent intent = new Intent(ListActivity.this, TaskActivity.class);
                intent.putExtra("TASK", ListActivity.this.getTasks().get(position));
                intent.putExtra("USER_LAT", userLat);
                intent.putExtra("USER_LNG", userLng);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == 1) {
                Task removedTask = data.getParcelableExtra("REMOVED_TASK");
                int i = 0;
                for (Task t : taskArray) {
                    if (t.getId() == removedTask.getId()) {
                        this.taskArray.remove(i);
                        this.titleArray.remove(i);
                    }
                    i++;
                }
                this.arrayAdapter.notifyDataSetChanged();
            } else if (resultCode == 2) {
                Task editedTask  = data.getParcelableExtra("EDITED_TASK");
                int i = 0;
                for (Task t : taskArray) {
                    if (t.getId() == editedTask.getId()) {
                        taskArray.set(i, editedTask);
                        titleArray.set(i, editedTask.getTitle());
                    }
                    i++;
                }
                this.arrayAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        if (TAG.equals("ListActivity")) {
            MenuItem item = menu.findItem(R.id.action_listview);
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
            case R.id.action_map:
                Intent i = new Intent(this, MapsActivity.class);
                i.putParcelableArrayListExtra("TASK_ARRAY", this.taskArray);
                finish();
                startActivity(i);
            case R.id.action_add_task:
                AddFragment addFragment = AddFragment.newInstance(0, this.userLat, this.userLng);
                addFragment.show(getSupportFragmentManager(), "Add task");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Getters and Setters
    public ArrayList<Task> getTasks() {
        return taskArray;
    }
}