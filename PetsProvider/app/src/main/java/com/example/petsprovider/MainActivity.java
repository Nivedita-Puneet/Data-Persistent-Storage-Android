package com.example.petsprovider;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.petsprovider.data.PetsContract;
import com.example.petsprovider.data.PetsDBHelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    TextView petTextView;
    TextView cursorCount;
    FloatingActionButton addPets;
    PetsDBHelper mDBHelper;
    ListView listView;

    PetsCursorAdapter petsCursorAdapter;

    static final String[] projection = new String[]{PetsContract.PetsEntry._ID,
            PetsContract.PetsEntry.COLUMN_PET_NAME,
            PetsContract.PetsEntry.COLUMN_PET_BREED};
    static final int INIT_PETS_LOADER = 0;


    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeControls();
    }

    private void initializeControls(){


        addPets = (FloatingActionButton)findViewById(R.id.addPets);
        mDBHelper = new PetsDBHelper(this);

        addPets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        listView = (ListView) findViewById(R.id.petsListview);
        View empty_view = findViewById(R.id.empty_view);
        listView.setEmptyView(empty_view);

        petsCursorAdapter = new PetsCursorAdapter(MainActivity.this, null);
        listView.setAdapter(petsCursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri updatePetWithId = ContentUris.withAppendedId(PetsContract.PetsEntry.CONTENT_URI, id);
                intent.setData(updatePetWithId);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(INIT_PETS_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.action_insert_dummy_data:
                insertPet();
                 break;
            case R.id.action_delete_all_entries:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void insertPet() {

        ContentValues contentValues = new ContentValues();
        contentValues.put(PetsContract.PetsEntry.COLUMN_PET_NAME, "Toto");
        contentValues.put(PetsContract.PetsEntry.COLUMN_PET_BREED, "Tabby");
        contentValues.put(PetsContract.PetsEntry.COLUMN_PET_GENDER, PetsContract.PetsEntry.GENDER_MALE);
        contentValues.put(PetsContract.PetsEntry.COLUMN_PET_WEIGHT, 7);
        Uri newRowId = getContentResolver().insert(PetsContract.PetsEntry.CONTENT_URI, contentValues);

        Log.i(MainActivity.class.getSimpleName(), "" + newRowId);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        return new CursorLoader(this, PetsContract.PetsEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        petsCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        petsCursorAdapter.swapCursor(null);
    }
}
