package com.choliy.igor.shelter.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class PetDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = PetDbHelper.class.getSimpleName();
    private static final String DB_NAME = "Shelter.db";
    private static final int DB_VERSION = 1;

    PetDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PetContract.PetEntry.CREATE_PETS_TABLE);
        Log.i(LOG_TAG, PetContract.PetEntry.CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(PetContract.PetEntry.DELETE_PETS_TABLE);
        onCreate(db);
    }
}