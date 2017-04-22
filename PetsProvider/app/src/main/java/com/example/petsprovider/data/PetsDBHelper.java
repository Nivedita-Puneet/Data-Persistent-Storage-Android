package com.example.petsprovider.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by PUNEETU on 20-04-2017.
 */

public class PetsDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Shelter.db";
    public static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PetsContract.PetsEntry.TABLE_NAME + " ( "+
                    PetsContract.PetsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    PetsContract.PetsEntry.COLUMN_PET_NAME + " TEXT," +
                    PetsContract.PetsEntry.COLUMN_PET_BREED + " TEXT NOT NULL,"+
                    PetsContract.PetsEntry.COLUMN_PET_GENDER+ " INTEGER NOT NULL,"+
                    PetsContract.PetsEntry.COLUMN_PET_WEIGHT+ " INTEGER NOT NULL DEFAULT 0)";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS" + PetsContract.PetsEntry.TABLE_NAME;

    public PetsDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
