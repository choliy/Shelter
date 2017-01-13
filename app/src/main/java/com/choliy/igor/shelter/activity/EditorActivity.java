package com.choliy.igor.shelter.activity;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.choliy.igor.shelter.Pet;
import com.choliy.igor.shelter.R;
import com.choliy.igor.shelter.data.PetContract;
import com.choliy.igor.shelter.data.PetContract.PetEntry;

/**
 * Allows user to create a new pet or edit an existing one
 */
public class EditorActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = EditorActivity.class.getSimpleName();
    public static final int PET_CLEAN = 0;
    public static final int PET_INSERTED = 1;
    public static final int PET_UPDATED = 2;
    public static final int PET_DELETED = 3;
    public static Uri sPetUri;
    private EditText mNameEditText, mBreedEditText, mAgeEditText, mWeightEditText;
    private CheckBox mBitesCheckBox, mSickCheckBox;
    private Spinner mGenderSpinner;
    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female
     */
    private int mGender = PetEntry.GENDER_UNKNOWN;
    private boolean mBites, mSick, mPetHasChanged;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mPetHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        setupUi();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        if (sPetUri == null) {
            /** Hide delete icon when Adding mode */
            MenuItem deleteItem = menu.findItem(R.id.action_delete);
            deleteItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (!TextUtils.isEmpty(mNameEditText.getText())) {
                    saveData();
                } else {
                    showSnackBar(R.string.info_enter_pet_name);
                    mNameEditText.requestFocus();
                }
                break;
            case R.id.action_clear:
                showClearDialog();
                break;
            case R.id.action_delete:
                showDeleteDialog();
                break;
            case android.R.id.home:
                if (!mPetHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                } else {
                    showUnsavedChangesDialog();
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mPetHasChanged) {
            super.onBackPressed();
        } else {
            showUnsavedChangesDialog();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (sPetUri != null) {
            return new CursorLoader(this, sPetUri, null, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        bindData(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader.reset();
    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()) {
            case R.id.check_box_bites:
                mBites = checked;
                break;
            case R.id.check_box_sick:
                mSick = checked;
                break;
        }
    }

    private void setupUi() {
        Intent intent = getIntent();
        sPetUri = intent.getData();

        if (sPetUri == null) {
            setTitle(R.string.editor_activity_title_new_pet);
            //invalidateOptionsMenu();
        } else {
            setTitle(R.string.editor_activity_title_edit_pet);
        }

        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mNameEditText.setOnTouchListener(mTouchListener);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mBreedEditText.setOnTouchListener(mTouchListener);
        mAgeEditText = (EditText) findViewById(R.id.edit_pet_age);
        mAgeEditText.setOnTouchListener(mTouchListener);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mWeightEditText.setOnTouchListener(mTouchListener);
        mBitesCheckBox = (CheckBox) findViewById(R.id.check_box_bites);
        mBitesCheckBox.setOnTouchListener(mTouchListener);
        mSickCheckBox = (CheckBox) findViewById(R.id.check_box_sick);
        mSickCheckBox.setOnTouchListener(mTouchListener);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);
        mGenderSpinner.setOnTouchListener(mTouchListener);
        setupSpinner();

        getSupportLoaderManager().initLoader(CatalogActivity.PET_LOADER, null, this);
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet
     */
    private void setupSpinner() {
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mGenderSpinner.setAdapter(genderSpinnerAdapter);
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetEntry.GENDER_MALE;
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetEntry.GENDER_FEMALE;
                    } else {
                        mGender = PetEntry.GENDER_UNKNOWN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = PetEntry.GENDER_UNKNOWN;
            }
        });
    }

    private void saveData() {
        /** Overview Category */
        String name = mNameEditText.getText().toString().trim();
        String breed = mBreedEditText.getText().toString().trim();

        /** Measurement Category */
        String ageString = mAgeEditText.getText().toString().trim();
        int age = 0;
        if (!ageString.equals("")) {
            age = Integer.parseInt(ageString);
        }
        String weightString = mWeightEditText.getText().toString().trim();
        int weight = 0;
        if (!weightString.equals("")) {
            weight = Integer.parseInt(weightString);
        }

        /** New Pet with data */
        Pet pet = new Pet(name, breed, mGender, age, weight, mBites, mSick);

        Log.i(LOG_TAG, "NAME: " + name);
        Log.i(LOG_TAG, "BREED: " + breed);
        Log.i(LOG_TAG, "GENDER: " + mGender);
        Log.i(LOG_TAG, "AGE: " + age);
        Log.i(LOG_TAG, "WEIGHT: " + weight);
        Log.i(LOG_TAG, "BITES: " + mBites);
        Log.i(LOG_TAG, "SICK: " + mSick);

        ContentValues values = new ContentValues();
        values.put(PetContract.PetEntry.COLUMN_PET_NAME, pet.getName());
        values.put(PetContract.PetEntry.COLUMN_PET_BREED, pet.getBreed());
        values.put(PetContract.PetEntry.COLUMN_PET_GENDER, pet.getGender());
        values.put(PetContract.PetEntry.COLUMN_PET_AGE, pet.getAge());
        values.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, pet.getWeight());
        values.put(PetContract.PetEntry.COLUMN_PET_BITES, pet.isBites());
        values.put(PetContract.PetEntry.COLUMN_PET_SICK, pet.isSick());

        /** Add or Update data in ContentProvider */
        if (sPetUri == null) {
            Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI, values);
            Log.i(LOG_TAG, "new Pet URI: " + newUri);
            if (newUri == null) {
                showSnackBar(R.string.info_insert_pet_failed);
            } else {
                CatalogActivity.sPetInfo = PET_INSERTED;
                finish();
            }
        } else {
            int rowsAffected = getContentResolver().update(sPetUri, values, null, null);
            if (rowsAffected == 0) {
                showSnackBar(R.string.info_update_pet_failed);
            } else {
                CatalogActivity.sPetInfo = PET_UPDATED;
                finish();
            }
        }
    }

    private void bindData(Cursor cursor) {
        if (cursor.moveToFirst()) {
            /** Overview Category */
            String name = cursor.getString(cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME));
            mNameEditText.setText(name);

            String breed = cursor.getString(cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED));
            mBreedEditText.setText(breed);

            /** Gender Category */
            int gender = cursor.getInt(cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER));
            mGenderSpinner.setSelection(gender);

            /** Measurement Category */
            int age = cursor.getInt(cursor.getColumnIndex(PetEntry.COLUMN_PET_AGE));
            if (age == 0) {
                mAgeEditText.setText("");
            } else {
                mAgeEditText.setText(String.valueOf(age));
            }

            int weight = cursor.getInt(cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT));
            if (weight == 0) {
                mWeightEditText.setText("");
            } else {
                mWeightEditText.setText(String.valueOf(weight));
            }

            /** Other Category */
            boolean bites = cursor.getInt(cursor.getColumnIndex(PetEntry.COLUMN_PET_BITES)) != 0;
            mBites = bites;
            mBitesCheckBox.setChecked(bites);

            boolean sick = cursor.getInt(cursor.getColumnIndex(PetEntry.COLUMN_PET_SICK)) != 0;
            mSick = sick;
            mSickCheckBox.setChecked(sick);
        }
    }

    private void clearData() {
        mNameEditText.setText("");
        mBreedEditText.setText("");
        mGenderSpinner.setSelection(0, true);
        mAgeEditText.setText("");
        mWeightEditText.setText("");
        mBitesCheckBox.setChecked(false);
        mSickCheckBox.setChecked(false);
        mPetHasChanged = true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void deletePet() {
        if (sPetUri != null) {
            /** Save Pet to Cursor before deleting */
            CatalogActivity.sCursor = getContentResolver().query(sPetUri, null, null, null, null, null);
            /** Delete Pet */
            int rowsDeleted = getContentResolver().delete(sPetUri, null, null);
            if (rowsDeleted == 0) {
                showSnackBar(R.string.info_delete_pet_failed);
            } else {
                CatalogActivity.sPetInfo = PET_DELETED;
                finish();
            }
        }
    }

    private void showUnsavedChangesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_unsaved_message)
                .setPositiveButton(R.string.dialog_unsaved_discard,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                finish();
                            }
                        })
                .setNegativeButton(R.string.dialog_unsaved_keep_editing,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        }).show();
    }

    private void showClearDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_message_clear)
                .setPositiveButton(R.string.dialog_button_yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                clearData();
                                dialog.dismiss();
                                showSnackBar(R.string.info_data_cleaned);
                            }
                        })
                .setNegativeButton(R.string.dialog_button_no,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        }).show();
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_message_delete)
                .setPositiveButton(R.string.dialog_button_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        deletePet();
                    }
                })
                .setNegativeButton(R.string.dialog_button_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void showSnackBar(int textResId) {
        View view = findViewById(R.id.scroll_view);
        Snackbar.make(view, getString(textResId), Snackbar.LENGTH_SHORT).show();
    }
}