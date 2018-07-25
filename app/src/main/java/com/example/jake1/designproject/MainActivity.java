package com.example.jake1.designproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    protected PinchZoomPan pinchZoomPan;
    ConstraintLayout clNavBar;
    EditText etFrom;
    EditText etTo;
    TextView tvTo;
    Button btnFloor1;
    Button btnFloor2;
    Button btnFloor3;
    Button btnNavigate;
    Button btnStairs;
    Button btnBoth;
    Button btnElevator;
    Button btnStartNav;
    ImageButton imBtnBackArrow;
    ImageView ivUTD;
    PopupMenu pumFrom;
    PopupMenu pumTo;
    float[] noCoords;
    float[] coords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        clNavBar = findViewById(R.id.clNavBar);
        pinchZoomPan = findViewById(R.id.pinchZoomPan);
        etFrom = findViewById(R.id.etFrom);
        etTo = findViewById(R.id.etTo);
        tvTo = findViewById(R.id.tvTo);
        btnFloor1 = findViewById(R.id.btnFloor1);
        btnFloor2 = findViewById(R.id.btnFloor2);
        btnFloor3 = findViewById(R.id.btnFloor3);
        btnNavigate = findViewById(R.id.btnNavigate);
        btnStairs = findViewById(R.id.btnStairs);
        btnBoth = findViewById(R.id.btnBoth);
        btnElevator = findViewById(R.id.btnElevator);
        btnStartNav = findViewById(R.id.btnStartNav);
        ivUTD = findViewById(R.id.ivUTD);
        imBtnBackArrow = findViewById(R.id.imBtnBackArrow);
        pumFrom = new PopupMenu(this, etFrom);
        pumTo = new PopupMenu(this, etTo);
        //these arrays will be replaced with array from ArcGIS
        noCoords = null;
        coords = new float[] {0, 450, 950, 0, 510, 950, 0, 510, 1180, 0, 1900, 1180, 1, 1900, 1180, 1, 1200, 1180, 1, 1200, 180, 2, 1330, 180, 2, 1330, 230, 2, 1250, 230, 2, 1250, 1100, 2, 1170, 1250, 2, 1170, 1660, 2, 930, 1660, 2, 930, 1860};

        setSupportActionBar(toolbar);
        pinchZoomPan.loadImageOnCanvas(0);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM, WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        loadPrefs();
        setPrefs();

        pumFrom.getMenuInflater().inflate( R.menu.start_location_menu, pumFrom.getMenu());
        pumTo.getMenuInflater().inflate( R.menu.destination_menu, pumTo.getMenu());

        etFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pumFrom.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        etFrom.setText(menuItem.toString());
                        return true;

                    }
                });
                pumFrom.show();
            }
        });

        etTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pumTo.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        etTo.setText(menuItem.toString());
                        return true;

                    }
                });
                pumTo.show();
            }
        });

        btnStairs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectStairs();
                setPrefs();
                savePrefs(1);
            }
        });

        btnBoth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectBoth();
                setPrefs();
                savePrefs(0);
            }
        });

        btnElevator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectElevator();
                setPrefs();
                savePrefs(2);
            }
        });

        btnFloor1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFloor1();
                pinchZoomPan.setFloorPath(0);
                pinchZoomPan.loadImageOnCanvas(0);
            }
        });

        btnFloor2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFloor2();
                pinchZoomPan.setFloorPath(1);
                pinchZoomPan.loadImageOnCanvas(1);
            }
        });

        btnFloor3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFloor3();
                pinchZoomPan.setFloorPath(2);
                pinchZoomPan.loadImageOnCanvas(2);
            }
        });

        btnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Communicator communicator = new Communicator();

                btnNavigate.setVisibility(View.GONE);
                btnStairs.setVisibility(View.GONE);
                btnBoth.setVisibility(View.GONE);
                btnElevator.setVisibility(View.GONE);
                etFrom.setVisibility(View.GONE);
                etTo.setVisibility(View.GONE);
                tvTo.setVisibility(View.GONE);

                ivUTD.setVisibility(View.GONE);
                imBtnBackArrow.setVisibility(View.VISIBLE);
                btnStartNav.setVisibility(View.VISIBLE);

                if (etFrom.getText().toString().equals("Your Location")) {
                    communicator.setFromLocation("CMX");
                }
                else {
                    communicator.setFromLocation(etFrom.getText().toString());
                }
                communicator.setToLocation(etTo.getText().toString());

                //once communicator is connected to server, uncomment this
                //coords = communicator.queryServer();
                pinchZoomPan.popCoordinates(coords);

            }
        });

        imBtnBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btnNavigate.setVisibility(View.VISIBLE);
                btnStairs.setVisibility(View.VISIBLE);
                btnBoth.setVisibility(View.VISIBLE);
                btnElevator.setVisibility(View.VISIBLE);
                etFrom.setVisibility(View.VISIBLE);
                etTo.setVisibility(View.VISIBLE);
                tvTo.setVisibility(View.VISIBLE);

                imBtnBackArrow.setVisibility(View.GONE);
                ivUTD.setVisibility(View.VISIBLE);
                btnStartNav.setVisibility(View.GONE);

                pinchZoomPan.popCoordinates(noCoords);

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

        Intent startQuickNavActivity = new Intent(getApplicationContext(), QuickNavMenuActivity.class);
        startActivity(startQuickNavActivity);

        return true;

    }

    private void selectStairs() {

        btnStairs.setTextColor(getResources().getColor(R.color.colorAccent));
        btnBoth.setTextColor(getResources().getColor(R.color.colorPrimary));
        btnElevator.setTextColor(getResources().getColor(R.color.colorPrimary));
        btnStairs.setBackgroundResource(R.drawable.btn_pressed_background);
        btnBoth.setBackgroundResource(R.drawable.btn_background);
        btnElevator.setBackgroundResource(R.drawable.btn_background);
        btnStairs.setEnabled(false);
        btnBoth.setEnabled(true);
        btnElevator.setEnabled(true);

    }

    private void selectBoth() {

        btnStairs.setTextColor(getResources().getColor(R.color.colorPrimary));
        btnBoth.setTextColor(getResources().getColor(R.color.colorAccent));
        btnElevator.setTextColor(getResources().getColor(R.color.colorPrimary));
        btnStairs.setBackgroundResource(R.drawable.btn_background);
        btnBoth.setBackgroundResource(R.drawable.btn_pressed_background);
        btnElevator.setBackgroundResource(R.drawable.btn_background);
        btnStairs.setEnabled(true);
        btnBoth.setEnabled(false);
        btnElevator.setEnabled(true);

    }

    private void selectElevator() {

        btnStairs.setTextColor(getResources().getColor(R.color.colorPrimary));
        btnBoth.setTextColor(getResources().getColor(R.color.colorPrimary));
        btnElevator.setTextColor(getResources().getColor(R.color.colorAccent));
        btnStairs.setBackgroundResource(R.drawable.btn_background);
        btnBoth.setBackgroundResource(R.drawable.btn_background);
        btnElevator.setBackgroundResource(R.drawable.btn_pressed_background);
        btnStairs.setEnabled(true);
        btnBoth.setEnabled(true);
        btnElevator.setEnabled(false);

    }

    private void selectFloor1() {

        btnFloor1.setTextColor(getResources().getColor(R.color.colorAccent));
        btnFloor2.setTextColor(getResources().getColor(R.color.colorPrimary));
        btnFloor3.setTextColor(getResources().getColor(R.color.colorPrimary));
        btnFloor1.setBackgroundResource(R.drawable.btn_pressed_background);
        btnFloor2.setBackgroundResource(R.drawable.btn_background);
        btnFloor3.setBackgroundResource(R.drawable.btn_background);
        btnFloor1.setEnabled(false);
        btnFloor2.setEnabled(true);
        btnFloor3.setEnabled(true);

    }

    private void selectFloor2() {

        btnFloor1.setTextColor(getResources().getColor(R.color.colorPrimary));
        btnFloor2.setTextColor(getResources().getColor(R.color.colorAccent));
        btnFloor3.setTextColor(getResources().getColor(R.color.colorPrimary));
        btnFloor1.setBackgroundResource(R.drawable.btn_background);
        btnFloor2.setBackgroundResource(R.drawable.btn_pressed_background);
        btnFloor3.setBackgroundResource(R.drawable.btn_background);
        btnFloor1.setEnabled(true);
        btnFloor2.setEnabled(false);
        btnFloor3.setEnabled(true);

    }

    private void selectFloor3() {

        btnFloor1.setTextColor(getResources().getColor(R.color.colorPrimary));
        btnFloor2.setTextColor(getResources().getColor(R.color.colorPrimary));
        btnFloor3.setTextColor(getResources().getColor(R.color.colorAccent));
        btnFloor1.setBackgroundResource(R.drawable.btn_background);
        btnFloor2.setBackgroundResource(R.drawable.btn_background);
        btnFloor3.setBackgroundResource(R.drawable.btn_pressed_background);
        btnFloor1.setEnabled(true);
        btnFloor2.setEnabled(true);
        btnFloor3.setEnabled(false);

    }

    private void savePrefs(int value) {

        SharedPreferences sp = getSharedPreferences("PATHPREFS", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt("pathPrefs", value);
        edit.apply();

    }

    private void loadPrefs() {

        SharedPreferences sp = getSharedPreferences("PATHPREFS", MODE_PRIVATE);
        int prefs = sp.getInt("pathPrefs", 0);

        if (prefs == 1) {
            selectStairs();
        }
        else if (prefs == 2) {
            selectElevator();
        }
        else {
            selectBoth();
        }

    }

    private void setPrefs() {

        Communicator communicator = new Communicator();

        if (!btnStairs.isEnabled()) {
            communicator.setTakeStairs(true);
            communicator.setTakeElevator(false);
        }
        else if (!btnElevator.isEnabled()) {
            communicator.setTakeStairs(false);
            communicator.setTakeElevator(true);
        }
        else {
            communicator.setTakeStairs(true);
            communicator.setTakeElevator(true);
        }

    }

}
