package com.caiolopes.where.todo.model;

import android.provider.BaseColumns;

/**
 * Schema for the task database.
 * @author Caio Lopes
 * @version 1.0
 */
public final class TaskSchema {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public TaskSchema() {}

    /* Inner class that defines the table contents */
    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "tasks";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_ADDRESS = "address";
        public static final String COLUMN_NAME_SHOWED = "showed";

        public static final String TEXT_TYPE = " TEXT";
        public static final String REAL_TYPE = " REAL";
        public static final String COMMA_SEP = ",";
        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                        FeedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        FeedEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                        FeedEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                        FeedEntry.COLUMN_NAME_LATITUDE + REAL_TYPE + COMMA_SEP +
                        FeedEntry.COLUMN_NAME_LONGITUDE + REAL_TYPE + COMMA_SEP +
                        FeedEntry.COLUMN_NAME_ADDRESS + TEXT_TYPE + COMMA_SEP +
                        FeedEntry.COLUMN_NAME_SHOWED + TEXT_TYPE +
                " )";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
    }
}
