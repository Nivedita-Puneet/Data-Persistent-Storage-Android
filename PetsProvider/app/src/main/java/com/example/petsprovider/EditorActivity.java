package com.example.petsprovider;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.petsprovider.data.PetsContract;

/**
 * Created by PUNEETU on 17-04-2017.
 */

public class EditorActivity extends AppCompatActivity{

    Spinner category_gender;
    EditText weight, name, breed;
    private int mGender = PetsContract.PetsEntry.GENDER_UNKNOWN;

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        initializeControls();
    }

    private void initializeControls(){
        weight = (EditText)findViewById(R.id.edit_pet_weight);
        name = (EditText)findViewById(R.id.edit_pet_name);
        breed = (EditText)findViewById(R.id.edit_pet_breed);

        category_gender = (Spinner)findViewById(R.id.spinner_gender);
        setSpinnerAdapter();
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
}
