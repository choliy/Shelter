package com.choliy.igor.shelter.activity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.choliy.igor.shelter.PetCursorAdapter;
import com.choliy.igor.shelter.PetValueLoader;
import com.choliy.igor.shelter.R;
import com.choliy.igor.shelter.data.PetContract;

import java.util.List;

/**
 * Displays list of pets that were entered and stored in the app
 */
public class CatalogActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = CatalogActivity.class.getSimpleName();
    public static final int PET_LOADER = 111;
    public static Cursor sCursor;
    public static int sPetInfo;
    private PetCursorAdapter mCursorAdapter;
    private PetValueLoader mValueLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        setupUi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        switch (sPetInfo) {
            case EditorActivity.PET_INSERTED:
                showSimpleSnackBar(R.string.info_insert_pet_successful);
                break;
            case EditorActivity.PET_UPDATED:
                showSimpleSnackBar(R.string.info_update_pet_successful);
                break;
            case EditorActivity.PET_DELETED:
                showActionSnackBar(R.string.info_delete_pet_successful, R.string.info_pet_restored);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sPetInfo = EditorActivity.PET_CLEAN;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
                showInfoDialog();
                break;
            case R.id.action_delete_all_pets:
                showDeleteDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String[] projection = {
                PetContract.PetEntry._ID,
                PetContract.PetEntry.COLUMN_PET_NAME,
                PetContract.PetEntry.COLUMN_PET_BREED,
                PetContract.PetEntry.COLUMN_PET_GENDER,
                PetContract.PetEntry.COLUMN_PET_AGE,
                PetContract.PetEntry.COLUMN_PET_WEIGHT};
        final String sortOrder = PetContract.PetEntry.COLUMN_PET_NAME + " ASC";
        return new CursorLoader(this, PetContract.PetEntry.CONTENT_URI, projection, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    private void setupUi() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CatalogActivity.this, EditorActivity.class));
            }
        });

        mValueLoader = PetValueLoader.getInstance();
        mCursorAdapter = new PetCursorAdapter(this, null);

        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setEmptyView(findViewById(R.id.empty_view));
        listView.setAdapter(mCursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                Uri petUri = ContentUris.withAppendedId(PetContract.PetEntry.CONTENT_URI, id);
                Log.i(LOG_TAG, String.valueOf(petUri));
                intent.setData(petUri);
                startActivity(intent);
            }
        });

        getSupportLoaderManager().initLoader(PET_LOADER, null, this);
    }

    private void showInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_info_title)
                .setMessage(R.string.dialog_info_message)
                .setPositiveButton(R.string.dialog_info_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_message_delete_all)
                .setPositiveButton(R.string.dialog_button_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        sPetInfo = EditorActivity.PET_CLEAN;
                        if (mCursorAdapter.getCount() > 0) {
                            /** Save Pets to Cursor before deleting */
                            sCursor = getContentResolver().query(PetContract.PetEntry.CONTENT_URI,
                                    null, null, null, PetContract.PetEntry.COLUMN_PET_NAME + " ASC");
                            /** Delete all Pets */
                            getContentResolver().delete(PetContract.PetEntry.CONTENT_URI, null, null);
                            dialog.dismiss();
                            showActionSnackBar(R.string.info_all_pets_deleted, R.string.info_pets_restored);
                        } else {
                            showSimpleSnackBar(R.string.info_empty_shelter);
                            dialog.dismiss();
                        }
                    }
                })
                .setNegativeButton(R.string.dialog_button_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public void showActionSnackBar(final int firstTextId, final int secondTextId) {
        View view = findViewById(R.id.coordinator_layout);
        Snackbar.make(view, firstTextId, Snackbar.LENGTH_LONG)
                .setAction(R.string.snack_bar_undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /** Restore Pets from the Cursor */
                        if (sPetInfo == EditorActivity.PET_DELETED) {
                            ContentValues deleteValues = mValueLoader.getCursorData(sCursor);
                            getContentResolver().insert(PetContract.PetEntry.CONTENT_URI, deleteValues);
                            showSimpleSnackBar(R.string.info_pet_restored);
                        } else {
                            List<ContentValues> values = mValueLoader.getListCursorData(sCursor);
                            for (int i = 0; i < values.size(); i++) {
                                getContentResolver().insert(PetContract.PetEntry.CONTENT_URI, values.get(i));
                            }
                            showSimpleSnackBar(secondTextId);
                        }
                    }
                }).show();
    }

    private void showSimpleSnackBar(int textResId) {
        View view = findViewById(R.id.coordinator_layout);
        Snackbar.make(view, textResId, Snackbar.LENGTH_SHORT).show();
    }
}