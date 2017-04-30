package com.example.petsprovider;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.petsprovider.data.PetsContract;

/**
 * Created by PUNEETU on 30-04-2017.
 */

public class PetsCursorAdapter extends CursorAdapter {

    public PetsCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView petName = (TextView) view.findViewById(R.id.name);
        TextView summary = (TextView) view.findViewById(R.id.summary);

        petName.setText(cursor.getString(cursor.getColumnIndexOrThrow(PetsContract.PetsEntry.COLUMN_PET_NAME)));
        summary.setText(cursor.getString(cursor.getColumnIndexOrThrow(PetsContract.PetsEntry.COLUMN_PET_BREED)));
    }
}
