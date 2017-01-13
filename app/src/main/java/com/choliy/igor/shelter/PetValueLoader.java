package com.choliy.igor.shelter;

import android.content.ContentValues;
import android.database.Cursor;

import com.choliy.igor.shelter.data.PetContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class for getting Cursor data
 */
public class PetValueLoader {

    private static PetValueLoader sValueLoader;

    private PetValueLoader() {
    }

    public static PetValueLoader getInstance() {
        if (sValueLoader == null) {
            sValueLoader = new PetValueLoader();
        }
        return sValueLoader;
    }

    public List<ContentValues> getListCursorData(Cursor cursor) {
        List<ContentValues> valuesList = new ArrayList<>();

        int indexName = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME);
        int indexBreed = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BREED);
        int indexGender = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_GENDER);
        int indexAge = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_AGE);
        int indexWeight = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_WEIGHT);
        int indexBites = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BITES);
        int indexSick = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_SICK);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            ContentValues values = new ContentValues();
            values.put(PetContract.PetEntry.COLUMN_PET_NAME, cursor.getString(indexName));
            values.put(PetContract.PetEntry.COLUMN_PET_BREED, cursor.getString(indexBreed));
            values.put(PetContract.PetEntry.COLUMN_PET_GENDER, cursor.getInt(indexGender));
            values.put(PetContract.PetEntry.COLUMN_PET_AGE, cursor.getInt(indexAge));
            values.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, cursor.getInt(indexWeight));
            values.put(PetContract.PetEntry.COLUMN_PET_BITES, cursor.getInt(indexBites));
            values.put(PetContract.PetEntry.COLUMN_PET_SICK, cursor.getInt(indexSick));
            valuesList.add(values);
        }

        return valuesList;
    }

    public ContentValues getCursorData(Cursor cursor) {
        ContentValues values = new ContentValues();
        cursor.moveToFirst();

        int indexName = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME);
        int indexBreed = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BREED);
        int indexGender = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_GENDER);
        int indexAge = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_AGE);
        int indexWeight = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_WEIGHT);
        int indexBites = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BITES);
        int indexSick = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_SICK);

        values.put(PetContract.PetEntry.COLUMN_PET_NAME, cursor.getString(indexName));
        values.put(PetContract.PetEntry.COLUMN_PET_BREED, cursor.getString(indexBreed));
        values.put(PetContract.PetEntry.COLUMN_PET_GENDER, cursor.getInt(indexGender));
        values.put(PetContract.PetEntry.COLUMN_PET_AGE, cursor.getInt(indexAge));
        values.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, cursor.getInt(indexWeight));
        values.put(PetContract.PetEntry.COLUMN_PET_BITES, cursor.getInt(indexBites));
        values.put(PetContract.PetEntry.COLUMN_PET_SICK, cursor.getInt(indexSick));

        return values;
    }
}