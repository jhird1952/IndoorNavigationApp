package com.example.jake1.designproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class QuickNavMenuActivity extends AppCompatActivity {

    private Communicator communicator;
    private float[] coordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_nav_menu);

        communicator = new Communicator();
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        Button btnBathrooms = findViewById(R.id.btnBathrooms);
        Button btnStormShelters = findViewById(R.id.btnStormShelters);
        Button btnWaterFountains = findViewById(R.id.btnWaterFountains);
        Button btnFood = findViewById(R.id.btnFood);
        Button btnEntertainment = findViewById(R.id.btnEntertainment);
        ImageView ivUTD = findViewById(R.id.ivUTD);
        ImageButton imBtnBackArrow = findViewById(R.id.imBtnBackArrow);
        coordinates = new float[] {0, 510, 850, 0, 580, 850, 0, 580, 1040, 0, 1760, 1040};

        setSupportActionBar(toolbar);
        ivUTD.setVisibility(View.GONE);
        imBtnBackArrow.setVisibility(View.VISIBLE);
        tvToolbarTitle.setText(R.string.tv_menu_title);

        imBtnBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent startMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(startMainActivity);

            }
        });

        btnBathrooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                communicator.setFromLocation("CMX");
                communicator.setToLocation("Bathroom");
                //coordinates = communicator.queryServer();
                displayQuickNavPath();

            }
        });

        btnStormShelters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                communicator.setFromLocation("CMX");
                communicator.setToLocation("Storm Shelter");
                //coordinates = communicator.queryServer();
                displayQuickNavPath();

            }
        });

        btnWaterFountains.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                communicator.setFromLocation("CMX");
                communicator.setToLocation("Water Fountain");
                //coordinates = communicator.queryServer();
                displayQuickNavPath();

            }
        });

        btnFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                communicator.setFromLocation("CMX");
                communicator.setToLocation("Food");
                coordinates = communicator.queryServer();
                displayQuickNavPath();

            }
        });

        btnEntertainment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                communicator.setFromLocation("CMX");
                communicator.setToLocation("Entertainment");
                //coordinates = communicator.queryServer();
                displayQuickNavPath();

            }
        });

    }

    private void displayQuickNavPath() {

        Intent startMainActivityWithPath = new Intent(getApplicationContext(), MainActivity.class);
        startMainActivityWithPath.putExtra("coordinateArray", coordinates);
        startActivity(startMainActivityWithPath);

    }

}
