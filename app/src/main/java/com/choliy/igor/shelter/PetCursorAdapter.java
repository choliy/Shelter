package com.choliy.igor.shelter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.choliy.igor.shelter.data.PetContract;

public class PetCursorAdapter extends CursorAdapter {

    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageGender = (ImageView) view.findViewById(R.id.image_pet_gender);
        TextView textName = (TextView) view.findViewById(R.id.text_pet_name);
        TextView textBreed = (TextView) view.findViewById(R.id.text_pet_breed);
        TextView textAge = (TextView) view.findViewById(R.id.text_pet_age);
        TextView textWeight = (TextView) view.findViewById(R.id.text_pet_weight);

        /** Gender data */
        int gender = cursor.getInt(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_GENDER));
        switch (gender) {
            case PetContract.PetEntry.GENDER_MALE:
                imageGender.setVisibility(View.VISIBLE);
                imageGender.setImageResource(R.drawable.gender_male_two);
                break;
            case PetContract.PetEntry.GENDER_FEMALE:
                imageGender.setVisibility(View.VISIBLE);
                imageGender.setImageResource(R.drawable.gender_female_two);
                break;
            case PetContract.PetEntry.GENDER_UNKNOWN:
                imageGender.setVisibility(View.GONE);
                break;
        }

        /** Name data */
        String name = cursor.getString(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME));
        textName.setText(name);

        /** Breed data */
        String breed = cursor.getString(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BREED));
        if (breed.equals("")) {
            textBreed.setText(context.getString(R.string.list_item_unknown_breed));
        } else {
            textBreed.setText(breed);
        }

        /** Age data */
        int age = cursor.getInt(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_AGE));
        if (age == 0) {
            textAge.setText(context.getString(R.string.gender_unknown));
        } else {
            textAge.setText(String.valueOf(age));
        }

        /** Weight data */
        int weight = cursor.getInt(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_WEIGHT));
        if (weight == 0) {
            textWeight.setText(context.getString(R.string.gender_unknown));
        } else {
            textWeight.setText(String.valueOf(weight));
        }
    }
}