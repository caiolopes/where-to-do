package com.caio_nathan.where.todo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.caio_nathan.where.todo.model.Task;
import com.caio_nathan.where.todo.model.TasksDbHelper;

/**
 * Created by caiolopes on 4/26/15.
 */
public class TaskActivity extends FragmentActivity {
    final String TAG = this.getClass().getSimpleName();
    private Task task;
    public TasksDbHelper mDbHelper;
    private double userLat;
    private double userLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        mDbHelper = new TasksDbHelper(this);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            this.task = extras.getParcelable("TASK");
            this.userLat = extras.getDouble("USER_LAT");
            this.userLng = extras.getDouble("USER_LNG");
        }

        setTaskInfo();
    }

    public void setTaskInfo() {
        TextView title = (TextView) findViewById(R.id.title);
        TextView description = (TextView) findViewById(R.id.description);
        TextView address = (TextView) findViewById(R.id.address);
        CheckBox notified = (CheckBox) findViewById(R.id.notified);
        title.setText(this.task.getTitle());
        description.setText(this.task.getDescription());
        address.setText(this.task.getAddress());
        if (this.task.isShowed()) {
            notified.setChecked(true);
        } else {
            notified.setChecked(false);
        }

        notified.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                task.setShowed(isChecked);
                if (mDbHelper.updateTask(task) > 0) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("EDITED_TASK", task);
                    setResult(2, returnIntent);

                    Toast.makeText(TaskActivity.this, "Edited!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void removeTask(View view) {
        if (this.mDbHelper.removeTask(this.task) > 0) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("REMOVED_TASK", this.task);
            setResult(1, returnIntent);

            Toast.makeText(this, "Removed!",
                    Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    public void editTask(View view) {
        setContentView(R.layout.activity_task_edit);
        EditText title = (EditText) findViewById(R.id.title);
        EditText description = (EditText) findViewById(R.id.description);
        title.setText(this.task.getTitle());
        description.setText(this.task.getDescription());
    }

    public void doneEditing(View view) {
        EditText title = (EditText) findViewById(R.id.title);
        EditText description = (EditText) findViewById(R.id.description);


        this.task.setTitle(title.getText().toString());
        this.task.setDescription(description.getText().toString());
        setContentView(R.layout.activity_task);
        setTaskInfo();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        if (this.mDbHelper.updateTask(this.task) > 0) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("EDITED_TASK", this.task);
            setResult(2, returnIntent);

            Toast.makeText(this, "Edited!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_listview:
                onBackPressed();
                return true;
            case R.id.action_map:
                Intent i = new Intent(this, MapsActivity.class);
                startActivity(i);
            case R.id.action_add_task:
                AddFragment addFragment = AddFragment.newInstance(0, this.userLat, this.userLng);
                addFragment.show(getSupportFragmentManager(), "Add task");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
