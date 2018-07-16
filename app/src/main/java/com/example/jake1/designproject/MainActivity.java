package com.example.jake1.designproject;

import android.content.SharedPreferences;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    protected PinchZoomPan pinchZoomPan;
    Toolbar toolbar;
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
        //these arrays will be replaced with array from ArcGIS
        noCoords = null;
        coords = new float[] {0, 0, 0, 0, 600, 450, 0, 600, 600, 1, 600, 600, 1, 800, 600, 1, 800, 450, 1, 1000, 450, 2, 1000, 450, 2, 1000, 800, 2, 1500, 800, 2, 1500, 2250};
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

        setSupportActionBar(toolbar);
        pinchZoomPan.loadImageOnCanvas(0);
        loadPrefs();

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
                savePrefs(1);
            }
        });

        btnBoth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectBoth();
                savePrefs(0);
            }
        });

        btnElevator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectElevator();
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

                btnNavigate.setVisibility(View.GONE);
                btnStairs.setVisibility(View.GONE);
                btnBoth.setVisibility(View.GONE);
                btnElevator.setVisibility(View.GONE);
                etFrom.setVisibility(View.GONE);
                etTo.setVisibility(View.GONE);
                tvTo.setVisibility(View.GONE);

                ViewGroup.LayoutParams params = clNavBar.getLayoutParams();
                params.height = 168;
                clNavBar.setLayoutParams(params);

                ivUTD.setVisibility(View.GONE);
                imBtnBackArrow.setVisibility(View.VISIBLE);
                btnStartNav.setVisibility(View.VISIBLE);

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

                ViewGroup.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.FILL_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                clNavBar.setLayoutParams(params);

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

        Toast.makeText(this, "Menu selected", Toast.LENGTH_SHORT).show();
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

}
