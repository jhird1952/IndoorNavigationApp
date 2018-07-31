package com.example.jake1.designproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.Math;


public class NavigationActivity extends AppCompatActivity {

    NavView navView;
    Communicator communicator;
    Toolbar toolbar;
    ConstraintLayout clNavMap;
    TextView tvToolbarTitle;
    private static TextView directionText;
    private static CountDownTimer locationTimer;
    ImageButton imBtnBackArrow;
    ImageView ivUTD;
    //private static ImageView mapView;
    private static ImageView arrowImage;
    private static ImageView locationDot;
    String finalDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        toolbar = findViewById(R.id.toolbar);
        navView = findViewById(R.id.navView);
        clNavMap = findViewById(R.id.clNavMap);
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        directionText = findViewById(R.id.tvDirections);
        arrowImage = findViewById(R.id.ivDirectionArrow);
        locationDot = findViewById(R.id.ivLocationDot);
        ivUTD = findViewById(R.id.ivUTD);
        imBtnBackArrow = findViewById(R.id.imBtnBackArrow);

        setSupportActionBar(toolbar);
        ivUTD.setVisibility(View.GONE);
        imBtnBackArrow.setVisibility(View.VISIBLE);
        tvToolbarTitle.setText(R.string.final_destination);
        navView.loadMap();
        updateLocation();

        //set-up the map image
        //mapView = findViewById(R.id.mapView);
        //mapView.setImageResource(R.drawable.gr2);

        finalDestination = getIntent().getExtras().getString("finalDestination");
        tvToolbarTitle.setText("Path to " + finalDestination);

        imBtnBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent startMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(startMainActivity);

            }
        });

    }

    //update the location of the location dot every 5 seconds based on your current location
    public void updateLocation() {
        locationTimer = new CountDownTimer(5000, 20) {
            public void onTick(long millisUntilFinished) {}
            public void onFinish() {
                //send an HTTPRequest to the server to get the location of the user
                final RequestQueue httpRequestQueue = Volley.newRequestQueue(NavigationActivity.this);
                final StringRequest httpSignupRequest = new StringRequest(Request.Method.POST, ("http://www.WEBSERVERURL.com/getLocation.php"),
                        //success, the request responded successfully!
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    //the current location of the user from CiscoCMX
                                    JSONObject locationData = new JSONObject(response);

                                    //need to do: update the coordinate of the locationDot based on the data from locationData
                                    locationDot.setX(0);
                                    locationDot.setY(0);

                                    //start the the timer again, it's basically an infinite loop
                                    locationTimer.start();
                                } catch (JSONException error) {
                                    error.printStackTrace();
                                    httpRequestQueue.stop();
                                }
                            }
                        },
                        //error, the request responded with a failure...
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(NavigationActivity.this, "An error occurred with the web-server...", Toast.LENGTH_LONG).show();
                                error.printStackTrace();
                                httpRequestQueue.stop();
                            }
                        }
                );
                httpRequestQueue.add(httpSignupRequest);
            }
        }.start();
    }

    //set the direction of the arrow based on where you need to navigate
    public static boolean updateArrowImage(double[] rArray1, double[] rArray2, double[] rArray3) {
        //absolute value margin of error for a straight line
        double epsilon = 0.25854206621;

        //this determines whether you should go up a floor or down a floor (the value of z changes)
        if(zChange(rArray1,rArray2,rArray3) == 1){
            //set the image to be an up arrow since we're going up a floor
            arrowImage.setRotation(90);
            directionText.setText("Go up to the next floor.");
        }
        else if(zChange(rArray1,rArray2,rArray3) == -1) {
            //set the image to be a down arrow since we're going down a floor
            arrowImage.setRotation(270);
            directionText.setText("Go down to the next floor.");
        }
        //this determines whether the arrow shows right or left
        else {
            if (determineDirection(rArray1, rArray2, rArray3) > epsilon) {
                //set the image to be a right arrow since we're going right
                arrowImage.setRotation(0);
                directionText.setText("Take a right turn.");
                return false;
            } else if (determineDirection(rArray1, rArray2, rArray3) < -epsilon) {
                //set the image to be a left arrow since we're going left
                arrowImage.setRotation(180);
                directionText.setText("Take a left turn.");
                return false;
            } else
                arrowImage.setRotation(90);
            directionText.setText("Keep straight.");
            return true;
        }
        return false;
    }

    public static double determineDirection(double[] array1, double[] array2, double[] array3)  {
        /* having (0,0) coordinates will break this formula in x or y.
         shouldn't be a problem due to these being outside of use-case for map. */
        double mag1 = Math.sqrt(Math.pow(array1[1],2) + Math.pow(array1[2],2));
        double mag2 = Math.sqrt(Math.pow(array2[1],2) + Math.pow(array2[2],2));
        double mag3 = Math.sqrt(Math.pow(array3[1],2) + Math.pow(array3[2],2));

        //this value is the direction of the vector sum, which determines the arrow positioning
        return (array2[1]/mag2-array1[2]/mag1)*(array3[2]/mag3-array1[2]/mag1)-(array2[2]/mag2-array1[2]/mag1)*(array3[1]/mag3-array1[1]/mag1);
    }

    public static int zChange(double[] zArray1, double[] zArray2, double[] zArray3)  {
        // if dif = 0 no change, if dif = positive then go up, if dif = negative then go down
        if(zArray1[0] == zArray3[0])
            return 0;
        else if (zArray1[0] < zArray3[0])
            return 1;
        else
            return -1;
    }

    public static double distanceFormula(double[] c1, double[] c2){
        double sum = 0.0;
        if (c1.length== c2.length){
            for(int i = 0; i<c1.length; i++){
                sum += Math.pow((c2[i] - c1[i]),2);
            }
        }
        return Math.sqrt(sum);
    }

    public static boolean recalculatePath(double coordinates[][], double myCord[]) {
        //the maximum you can leave the path is 10
        int maxRange = 10;
        //cur is how far you are currently from the path
        double cur = 0.0;
        double min = 2147483647;

        for (int i = 0; i < coordinates.length; i++) {

            //get the distance between two coordinates
            cur = distanceFormula(myCord, coordinates[i]);

            if (cur < min) {
                min = cur;
            }

        }

        if (min > maxRange) {
            return false;
        }
        else {
            return true;
        }

    }

}