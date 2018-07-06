package com.example.jake1.designproject;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    protected PinchZoomPan pinchZoomPan;
    Toolbar toolbar;
    Button btnFloor1;
    Button btnFloor2;
    Button btnFloor3;
    Button btnNavigate;
    ImageButton imBtnBackArrow;
    ImageView ivUTD;
    Spinner sStartLocations;
    Spinner sDestinations;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        pinchZoomPan = findViewById(R.id.pinchZoomPan);
        sStartLocations = findViewById(R.id.sStartLocations);
        sDestinations = findViewById(R.id.sDestinations);
        btnFloor1 = findViewById(R.id.btnFloor1);
        btnFloor2 = findViewById(R.id.btnFloor2);
        btnFloor3 = findViewById(R.id.btnFloor3);
        btnNavigate = findViewById(R.id.btnNavigate);
        ivUTD = findViewById(R.id.ivUTD);
        imBtnBackArrow = findViewById(R.id.imBtnBackArrow);

        setSupportActionBar(toolbar);
        loadImage(pinchZoomPan, 0);

        ArrayAdapter<CharSequence> startAdapter = ArrayAdapter.createFromResource(this, R.array.startRoomNums, R.layout.room_num_spinner_item);
        startAdapter.setDropDownViewResource(R.layout.room_num_spinner_item);
        sStartLocations.setAdapter(startAdapter);
        sStartLocations.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> destinationAdapter = ArrayAdapter.createFromResource(this, R.array.destinationRoomNums, R.layout.room_num_spinner_item);
        destinationAdapter.setDropDownViewResource(R.layout.room_num_spinner_item);
        sDestinations.setAdapter(destinationAdapter);
        sDestinations.setOnItemSelectedListener(this);

        btnFloor1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImage(pinchZoomPan, 0);
            }
        });

        btnFloor2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImage(pinchZoomPan, 1);
            }
        });

        btnFloor3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImage(pinchZoomPan, 2);
            }
        });

        btnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btnNavigate.setVisibility(View.INVISIBLE);
                ivUTD.setVisibility(View.GONE);
                imBtnBackArrow.setVisibility(View.VISIBLE);

            }
        });

        imBtnBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imBtnBackArrow.setVisibility(View.GONE);
                btnNavigate.setVisibility(View.VISIBLE);
                ivUTD.setVisibility(View.VISIBLE);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.fast_nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Toast.makeText(this, "Menu selected", Toast.LENGTH_SHORT).show();
        return true;

    }

    private void loadImage(PinchZoomPan pinchZoomPan, int floorNum) {

        Uri mapUri = null;

        if (floorNum == 0) {
            mapUri = Uri.parse("android.resource://com.example.jake1.designproject/drawable/landscape");
        }
        else if (floorNum == 1) {
            mapUri = Uri.parse("android.resource://com.example.jake1.designproject/drawable/landscape2");
        }
        else if (floorNum == 2) {
            mapUri = Uri.parse("android.resource://com.example.jake1.designproject/drawable/landscape3");
        }

        pinchZoomPan.loadImageOnCanvas(mapUri);

    }





    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
       //String text = adapterView.getItemAtPosition(i).toString();
       //Toast.makeText(adapterView.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
