package com.choliy.igor.shelter.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class PetContract {

    public static final String CONTENT_AUTHORITY = "com.choliy.igor.shelter";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PETS = "pets";

    /**
     * For each table in DB, create own inner class
     */
    public static final class PetEntry implements BaseColumns {
        /**
         * The content URI to access the pet data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + ".provider." + PATH_PETS;
        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + ".provider." + PATH_PETS;
        /**
         * Name of database table for pets
         */
        public static final String TABLE_NAME = "pets";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PET_NAME = "name";
        public static final String COLUMN_PET_BREED = "breed";
        public static final String COLUMN_PET_GENDER = "gender";
        public static final String COLUMN_PET_AGE = "age";
        public static final String COLUMN_PET_WEIGHT = "weight";
        public static final String COLUMN_PET_BITES = "bites";
        public static final String COLUMN_PET_SICK = "sick";

        public static final String CREATE_PETS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_PET_NAME + " TEXT NOT NULL, "
                + COLUMN_PET_BREED + " TEXT, "
                + COLUMN_PET_GENDER + " INTEGER NOT NULL, "
                + COLUMN_PET_AGE + " INTEGER, "
                + COLUMN_PET_WEIGHT + " INTEGER, "
                + COLUMN_PET_BITES + " INTEGER NOT NULL DEFAULT 0, "
                + COLUMN_PET_SICK + " INTEGER NOT NULL DEFAULT 0);";

        public static final String DELETE_PETS_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;
    }
}