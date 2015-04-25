package com.caio_nathan.where.todo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.caio_nathan.where.todo.model.Task;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by caiolopes on 4/23/15.
 */
public class ListActivity extends FragmentActivity {
    final String TAG = this.getClass().getSimpleName();
    private ArrayList<Task> taskArray;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            this.taskArray = extras.getParcelableArrayList("TASK_ARRAY");
        } else {
            this.taskArray = new ArrayList<>();
        }

        ListView lv = (ListView) findViewById(R.id.task_list);

        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.

        Iterator i = taskArray.iterator();
        ArrayList<String> array = new ArrayList<>();
        while(i.hasNext()) {
            Task t = (Task) i.next();
            array.add(t.getTitle());
        }

        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                array );

        lv.setAdapter(arrayAdapter);
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
                AddFragment addFragment = new AddFragment();
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