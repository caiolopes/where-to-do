package com.caio_nathan.where.todo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        mDbHelper = new TasksDbHelper(this);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            this.task = extras.getParcelable("TASK");
        }

        setTaskInfo();
    }

    public void setTaskInfo() {
        TextView title = (TextView) findViewById(R.id.title);
        TextView description = (TextView) findViewById(R.id.description);
        title.setText(this.task.getTitle());
        description.setText(this.task.getDescription());
    }

    public void removeTask(View view) {
        if (this.mDbHelper.removeTask(this.task) > 0) {
            Toast.makeText(this, "Removed!",
                    Toast.LENGTH_SHORT).show();
        }
        Intent returnIntent = new Intent();
        returnIntent.putExtra("TASK_TITLE", this.task.getTitle());
        setResult(1, returnIntent);
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
        Intent returnIntent = new Intent();
        returnIntent.putExtra("OLD_TASK_TITLE", this.task.getTitle());

        this.task.setTitle(title.getText().toString());
        this.task.setDescription(description.getText().toString());
        setContentView(R.layout.activity_task);
        setTaskInfo();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        returnIntent.putExtra("NEW_TASK_TITLE", this.task.getTitle());
        returnIntent.putExtra("TASK_DESC", this.task.getDescription());
        setResult(2, returnIntent);
        Log.v("TASK_ID", "Task ID: " + this.task.getId());
        if (this.mDbHelper.updateTask(this.task) > 0) {
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
                AddFragment addFragment = AddFragment.newInstance(0);
                addFragment.show(getSupportFragmentManager(), "Add task");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
