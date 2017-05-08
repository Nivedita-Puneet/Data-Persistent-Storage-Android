package com.example.petsprovider;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.petsprovider.data.PetsContract;
import com.example.petsprovider.data.PetsDBHelper;

/**
 * Created by PUNEETU on 17-04-2017.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    Spinner category_gender;
    EditText mEditPetWeight, mEditPetName, mEditPetBreed;
    private int mGender = PetsContract.PetsEntry.GENDER_UNKNOWN;
    PetsDBHelper mDBHelper;

    public static final String TAG = EditorActivity.class.getSimpleName();
    public static final int EDIT_PETS_LOADER = 1;
    private Uri mContentUri;

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        initializeControls();
    }

    private void initializeControls(){

        mDBHelper = new PetsDBHelper(this);
        mEditPetWeight = (EditText) findViewById(R.id.edit_pet_weight);
        mEditPetName = (EditText) findViewById(R.id.edit_pet_name);
        mEditPetBreed = (EditText) findViewById(R.id.edit_pet_breed);

        category_gender = (Spinner)findViewById(R.id.spinner_gender);
        setSpinnerAdapter();

        mContentUri = getIntent().getData();

        if (mContentUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_pet));
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_pet));
            getLoaderManager().initLoader(EDIT_PETS_LOADER, null, EditorActivity.this);

            Log.i(TAG, mContentUri + "");
        }

    }

    private void setSpinnerAdapter(){

        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(EditorActivity.this,
                            R.array.array_gender_options,android.R.layout.simple_spinner_item );
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category_gender.setAdapter(genderAdapter);
        category_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selection = (String)adapterView.getItemAtPosition(position);

                if(!TextUtils.isEmpty(selection)){
                    if(selection.equals(R.string.gender_male)){
                        mGender = PetsContract.PetsEntry.GENDER_MALE;
                    }else if(selection.equals(R.string.gender_female)){
                        mGender = PetsContract.PetsEntry.GENDER_FEMALE;
                    }else {
                        mGender = PetsContract.PetsEntry.GENDER_UNKNOWN;
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                mGender = PetsContract.PetsEntry.GENDER_UNKNOWN;
            }
        });
    }

    private void savePet() {
        String petName = mEditPetName.getText().toString().trim();
        String petBreed = mEditPetBreed.getText().toString().trim();
        String petWeight = mEditPetWeight.getText().toString().trim();

        ContentValues contentValues = new ContentValues();
        contentValues.put(PetsContract.PetsEntry.COLUMN_PET_NAME, petName);
        contentValues.put(PetsContract.PetsEntry.COLUMN_PET_BREED, petBreed);
        contentValues.put(PetsContract.PetsEntry.COLUMN_PET_GENDER, mGender);
        contentValues.put(PetsContract.PetsEntry.COLUMN_PET_WEIGHT, Integer.parseInt(petWeight));

        if (mContentUri == null) {

            Uri rowID = getContentResolver().insert(PetsContract.PetsEntry.CONTENT_URI, contentValues);

            Toast.makeText(this, "Pet saved" + rowID, Toast.LENGTH_LONG).show();

        } else {

            int rowsAffected = getContentResolver().update(mContentUri, contentValues, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                savePet();
                finish();
                return true;
            case R.id.action_delete:
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                PetsContract.PetsEntry._ID,
                PetsContract.PetsEntry.COLUMN_PET_NAME,
                PetsContract.PetsEntry.COLUMN_PET_BREED,
                PetsContract.PetsEntry.COLUMN_PET_GENDER,
                PetsContract.PetsEntry.COLUMN_PET_WEIGHT};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mContentUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(PetsContract.PetsEntry.COLUMN_PET_NAME);
            int breedColumnIndex = cursor.getColumnIndex(PetsContract.PetsEntry.COLUMN_PET_BREED);
            int genderColumnIndex = cursor.getColumnIndex(PetsContract.PetsEntry.COLUMN_PET_GENDER);
            int weightColumnIndex = cursor.getColumnIndex(PetsContract.PetsEntry.COLUMN_PET_WEIGHT);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String breed = cursor.getString(breedColumnIndex);
            int gender = cursor.getInt(genderColumnIndex);
            int weight = cursor.getInt(weightColumnIndex);

            mEditPetName.setText(name);
            mEditPetBreed.setText(breed);
            mEditPetWeight.setText(Integer.toString(weight));

        }

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mEditPetWeight.setText(null);
        mEditPetName.setText(null);
        mEditPetBreed.setText(null);

    }
}



