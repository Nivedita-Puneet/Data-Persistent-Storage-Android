package com.example.petsprovider;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.petsprovider.data.PetsContract;
import com.example.petsprovider.data.PetsDBHelper;

public class MainActivity extends AppCompatActivity {

    TextView petTextView;
    TextView cursorCount;
    FloatingActionButton addPets;
    PetsDBHelper mDBHelper;
    ListView listView;

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

        displayDatabaseInfo();
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
                displayDatabaseInfo();
                 break;
            case R.id.action_delete_all_entries:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void displayDatabaseInfo() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.

        // Create and/or open a database to read from it
        /*SQLiteDatabase db = mDBHelper.getReadableDatabase();

        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
        Cursor cursor = db.query(PetsContract.PetsEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null, null);*/

        Cursor cursor = getContentResolver().query(PetsContract.PetsEntry.CONTENT_URI, null, null, null, null);
        PetsCursorAdapter cursorAdapter = new PetsCursorAdapter(MainActivity.this, cursor);
        listView.setAdapter(cursorAdapter);

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
    public void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

}
