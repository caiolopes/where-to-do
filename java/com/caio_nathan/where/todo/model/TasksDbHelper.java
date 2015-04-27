package com.caio_nathan.where.todo.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by caiolopes on 4/26/15.
 */
public class TasksDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "WhereToDo.db";

    private SQLiteDatabase db;

    // Constructor to simplify Business logic access to the repository
    public TasksDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // Android will look for the database defined by DATABASE_NAME
        // And if not found will invoke your onCreate method
        this.db = this.getWritableDatabase();
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TaskSchema.FeedEntry.SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(TaskSchema.FeedEntry.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public long addTask(Task task) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(TaskSchema.FeedEntry.COLUMN_NAME_TITLE, task.getTitle());
        values.put(TaskSchema.FeedEntry.COLUMN_NAME_DESCRIPTION, task.getDescription());
        values.put(TaskSchema.FeedEntry.COLUMN_NAME_LATITUDE, Double.toString(task.getLat()));
        values.put(TaskSchema.FeedEntry.COLUMN_NAME_LONGITUDE, Double.toString(task.getLng()));
        values.put(TaskSchema.FeedEntry.COLUMN_NAME_ADDRESS, task.getAddress());
        values.put(TaskSchema.FeedEntry.COLUMN_NAME_SHOWED, String.valueOf(task.isShowed()));

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = this.db.insert(
                TaskSchema.FeedEntry.TABLE_NAME,
                null,
                values);

        return newRowId;
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> taskArray = new ArrayList<>();
        Cursor cursor;

        cursor = this.db.query(TaskSchema.FeedEntry.TABLE_NAME, new String[]{
                        TaskSchema.FeedEntry._ID,
                        TaskSchema.FeedEntry.COLUMN_NAME_TITLE,
                        TaskSchema.FeedEntry.COLUMN_NAME_DESCRIPTION,
                        TaskSchema.FeedEntry.COLUMN_NAME_LATITUDE,
                        TaskSchema.FeedEntry.COLUMN_NAME_LONGITUDE,
                        TaskSchema.FeedEntry.COLUMN_NAME_ADDRESS,
                        TaskSchema.FeedEntry.COLUMN_NAME_SHOWED},
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            Task task = new Task();
            Log.v("TASK_ID", "Adding ID: " + cursor.getString(0));
            task.setId(Long.parseLong(cursor.getString(0)));
            task.setTitle(cursor.getString(1));
            task.setDescription(cursor.getString(2));
            task.setLat(Double.parseDouble(cursor.getString(3)));
            task.setLng(Double.parseDouble(cursor.getString(4)));
            task.setAddress(cursor.getString(5));
            task.setShowed(Boolean.parseBoolean(cursor.getString(6)));
            taskArray.add(task);
        }
        cursor.close();

        return taskArray;
    }

    public int updateTask(Task task) {
        // New value for one column
        ContentValues values = new ContentValues();
        values.put(TaskSchema.FeedEntry.COLUMN_NAME_TITLE, task.getTitle());
        values.put(TaskSchema.FeedEntry.COLUMN_NAME_DESCRIPTION, task.getDescription());
        values.put(TaskSchema.FeedEntry.COLUMN_NAME_LATITUDE, Double.toString(task.getLat()));
        values.put(TaskSchema.FeedEntry.COLUMN_NAME_LONGITUDE, Double.toString(task.getLng()));
        values.put(TaskSchema.FeedEntry.COLUMN_NAME_ADDRESS, task.getAddress());
        values.put(TaskSchema.FeedEntry.COLUMN_NAME_SHOWED, String.valueOf(task.isShowed()));

        // Which row to update, based on the ID
        String selection = TaskSchema.FeedEntry._ID + " LIKE ? LIMIT 1";
        String[] selectionArgs = { String.valueOf(task.getId()) };

        return db.update(
                TaskSchema.FeedEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    public int removeTask(Task task) {
        // Define 'where' part of query.
        String selection = TaskSchema.FeedEntry._ID + " = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {String.valueOf(task.getId())};

        // Issue SQL statement.
        return this.db.delete(
                TaskSchema.FeedEntry.TABLE_NAME,
                selection,
                selectionArgs);
    }
}