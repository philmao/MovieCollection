package com.example.moviescollection.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.moviescollection.data.DatabaseDescription.Movie;

public class MoviesCollectionDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MoviesCollection.db";
    private static final int DATABASE_VERSION = 1;

    // constructor
    public MoviesCollectionDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // creates the movies table when the database is created
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL for creating the movies table
        final String CREATE_MOVIES_TABLE =
                "CREATE TABLE " + Movie.TABLE_NAME + "(" +
                        Movie._ID + " integer primary key, " +
                        Movie.COLUMN_TITLE + " TEXT, " +
                        Movie.COLUMN_YEAR + " TEXT, " +
                        Movie.COLUMN_RATING + " TEXT, " +
                        Movie.COLUMN_LENGTH + " TEXT, " +
                        Movie.COLUMN_DIRECTOR + " TEXT, " +
                        Movie.COLUMN_SCORE + " TEXT);";
        db.execSQL(CREATE_MOVIES_TABLE); // create the movies table
    }

    // normally defines how to upgrade the database when the schema changes
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) { }
}
