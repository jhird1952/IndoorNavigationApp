package com.example.jake1.designproject;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Communicator communicator;
    private Toolbar toolbar;
    private PinchZoomPan pinchZoomPan;
    private EditText etFrom;
    private EditText etTo;
    private TextView tvTo;
    private Button btnFloor1;
    private Button btnFloor2;
    private Button btnFloor3;
    private Button btnNavigate;
    private Button btnStairs;
    private Button btnBoth;
    private Button btnElevator;
    private Button btnStartNav;
    private ImageButton imBtnBackArrow;
    private ImageView ivUTD;
    private PopupMenu pumFrom;
    private PopupMenu pumTo;

    //URL to the web-server, change it to your web-server's URL
    private String serverURL = "http://ecsclark18.utdallas.edu/";
    //a variable to hold the parsed path data from ArcGIS
    ArrayList<String[]> parsedPathData = new ArrayList<String[]>();
    double[] coordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        communicator = new Communicator();
        toolbar = findViewById(R.id.toolbar);
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

        setSupportActionBar(toolbar);
        pinchZoomPan.loadImageOnCanvas(0);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM, WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        loadPrefs();

        pumFrom.getMenuInflater().inflate( R.menu.start_location_menu, pumFrom.getMenu());
        pumTo.getMenuInflater().inflate( R.menu.destination_menu, pumTo.getMenu());

        if (getIntent().hasExtra("coordinateArray")) {

            coordinates = getIntent().getExtras().getDoubleArray("coordinateArray");
            displayPath();

        }

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

                if (etFrom.getText().toString().equals("Your Location")) {
                    communicator.setFromLocation("CMX");
                }
                else {
                    communicator.setFromLocation(etFrom.getText().toString());
                }
                communicator.setToLocation(etTo.getText().toString());

                //once communicator is connected to server, uncomment next line and delete line after that
                //coordinates = communicator.queryServer();
                coordinates = new double[] {0, 0, 0};
                try {
                    getArcGISPath(etFrom.getText().toString() + ":" + etTo.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                displayPath();

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

                pinchZoomPan.popCoordinates(null);

            }
        });

        btnStartNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent startNavigationActivity = new Intent(getApplicationContext(), NavigationActivity.class);
                startNavigationActivity.putExtra("finalDestination", etTo.getText().toString());
                startActivity(startNavigationActivity);

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

    public void displayPath() {

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

        pinchZoomPan.popCoordinates(coordinates);

    }

    //sends an HTTP Request to the PHP page, which is the URL in the StringRequest variable
    private void getArcGISPath(final String fromToLocations) throws JSONException {
        //send an HTTPRequest to the server to send the destination to the server
        JSONObject test = new JSONObject();
        test.put("from", "2.114");
        test.put("to","2.328");
        test.put("stairs", true);
        test.put("elevators", true);
        final RequestQueue httpRequestQueue = Volley.newRequestQueue(MainActivity.this);
        final StringRequest httpSendDestination = new StringRequest(Request.Method.POST, (serverURL + "index.php"), new Response.Listener<String>() {

                    @Override
                        public void onResponse(String response) {
                        try {

                            //getting the path data and parsing it into an array

                            //String[] pathData = response.split(",");
                            //System.out.println(pathData);


                            /*splitting the path data into individual coordinates, for every 3 commas in pathData
                            it is considered a coordinate(z,x,y)
                            for(int i = 0; i < pathData.length; i += 3) {
                                String[] coordinate = new String[3];
                                make sure we don't cause an ArrayOutOfBounds exception
                                if(!(i + 2 >= pathData.length)) {
                                    coordinate[0] = pathData[i];
                                    coordinate[1] = pathData[i + 1];
                                    coordinate[2] = pathData[i + 2];
                                    parsedPathData.add(coordinate);
                                }
                            }

                            output each coordinate into an array format
                            for(int j = 0; j < parsedPathData.size(); j++) {
                                System.out.println("[" + parsedPathData.get(j)[0] + "," + parsedPathData.get(j)[1] + "," + parsedPathData.get(j)[2] + "]");
                            }
                            */
                        } catch (Exception error) {
                            error.printStackTrace();
                            httpRequestQueue.stop();
                        }
                    }
                },
                //error, the request responded with a failure...
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "An error occurred with the server...", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                        httpRequestQueue.stop();
                    }
                }
        ) {
            //POST variables to send to the web-server
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("locations", fromToLocations);
                return params;
            }
            //header values to send to the web-server
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        httpRequestQueue.add(httpSendDestination);
    }

}
