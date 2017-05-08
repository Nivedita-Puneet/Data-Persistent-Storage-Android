package com.example.petsprovider.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by PUNEETU on 25-04-2017.
 */

public class PetProvider extends ContentProvider {

    PetsDBHelper mDBHelper;
    private static final int PETS = 100;
    private static final int PETS_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(PetsContract.CONTENT_AUTHORITY, PetsContract.PATH_PETS, PETS);
        sUriMatcher.addURI(PetsContract.CONTENT_AUTHORITY, PetsContract.PATH_PETS + "/#", PETS_ID);
    }

    @Override
    public boolean onCreate() {
        mDBHelper = new PetsDBHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = mDBHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor cursor;

        switch (match) {
            case PETS:
                String[] projectionArgs = new String[]{
                        PetsContract.PetsEntry._ID,
                        PetsContract.PetsEntry.COLUMN_PET_NAME,
                        PetsContract.PetsEntry.COLUMN_PET_BREED,
                        PetsContract.PetsEntry.COLUMN_PET_GENDER,
                        PetsContract.PetsEntry.COLUMN_PET_WEIGHT
                };
                cursor = database.query(PetsContract.PetsEntry.TABLE_NAME, projectionArgs, null, null, null, null, null);
                break;
            case PETS_ID:
                selection = PetsContract.PetsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(PetsContract.PetsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query Unknown Uri" + uri);

        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("The insertion is not supported for this uri" + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] strings) {

        final int match = sUriMatcher.match(uri);
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int rowsDeleted = 0;
        switch (match) {
            case PETS:
                rowsDeleted = db.delete(PetsContract.PetsEntry.TABLE_NAME, null, null);
            case PETS_ID:
                rowsDeleted = db.delete(PetsContract.PetsEntry.TABLE_NAME, selection, strings);
        }
        if (rowsDeleted > 0) {

            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case PETS_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = PetsContract.PetsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

    }

    private Uri insertPet(Uri uri, ContentValues contentValues) {

        String name = contentValues.getAsString(PetsContract.PetsEntry.COLUMN_PET_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Pet requires name");
        }

        Integer gender = contentValues.getAsInteger(PetsContract.PetsEntry.COLUMN_PET_GENDER);

        if (gender == null && !PetsContract.PetsEntry.isValidGender(gender)) {

            throw new IllegalArgumentException("Please enter valid gender");
        }

        Integer weight = contentValues.getAsInteger(PetsContract.PetsEntry.COLUMN_PET_WEIGHT);

        if (weight != null && weight < 0) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }

        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        long id = db.insert(PetsContract.PetsEntry.TABLE_NAME, null, contentValues);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);

    }

    private int updatePet(Uri uri, ContentValues contentValues, String selction, String[] selectionArgs) {

        if (contentValues.containsKey(PetsContract.PetsEntry.COLUMN_PET_NAME)) {
            String name = contentValues.getAsString(PetsContract.PetsEntry.COLUMN_PET_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_GENDER} key is present,
        // check that the gender value is valid.
        if (contentValues.containsKey(PetsContract.PetsEntry.COLUMN_PET_GENDER)) {
            Integer gender = contentValues.getAsInteger(PetsContract.PetsEntry.COLUMN_PET_GENDER);
            if (gender == null || !PetsContract.PetsEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("Pet requires valid gender");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_WEIGHT} key is present,
        // check that the weight value is valid.
        if (contentValues.containsKey(PetsContract.PetsEntry.COLUMN_PET_WEIGHT)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer weight = contentValues.getAsInteger(PetsContract.PetsEntry.COLUMN_PET_WEIGHT);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }

        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int rowsUpdated = db.update(PetsContract.PetsEntry.TABLE_NAME, contentValues, selction, selectionArgs);
        if (rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;

    }

}
