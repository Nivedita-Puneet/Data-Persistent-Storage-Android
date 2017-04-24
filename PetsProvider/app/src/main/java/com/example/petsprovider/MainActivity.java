package com.example.petsprovider;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.petsprovider.data.PetsContract;
import com.example.petsprovider.data.PetsDBHelper;

public class MainActivity extends AppCompatActivity {

    TextView petTextView;
    TextView cursorCount;
    FloatingActionButton addPets;
    PetsDBHelper mDBHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeControls();
    }

    private void initializeControls(){

        petTextView = (TextView)findViewById(R.id.textviewPet);
        addPets = (FloatingActionButton)findViewById(R.id.addPets);
        cursorCount = (TextView) findViewById(R.id.textviewPet);
        mDBHelper = new PetsDBHelper(this);

        addPets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

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
        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
        Cursor cursor = db.rawQuery("SELECT * FROM " + PetsContract.PetsEntry.TABLE_NAME, null);
        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
            cursorCount.setText("Number of pets available in Pets Database." + ":" + cursor.getCount());
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    private void insertPet() {

        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(PetsContract.PetsEntry.COLUMN_PET_NAME, "Toto");
        contentValues.put(PetsContract.PetsEntry.COLUMN_PET_BREED, "Tabby");
        contentValues.put(PetsContract.PetsEntry.COLUMN_PET_GENDER, PetsContract.PetsEntry.GENDER_MALE);
        contentValues.put(PetsContract.PetsEntry.COLUMN_PET_WEIGHT, 7);
        long newRowId = db.insert(PetsContract.PetsEntry.TABLE_NAME, null, contentValues);

        Log.i(MainActivity.class.getSimpleName(), "" + newRowId);

    }

    @Override
    public void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

}
