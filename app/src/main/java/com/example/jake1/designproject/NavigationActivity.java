package com.example.jake1.designproject;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jake1.designproject.services.AsyncResponse;
import com.example.jake1.designproject.services.UiService;
import com.example.jake1.designproject.services.WebServiceCaller;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;


public class NavigationActivity extends AppCompatActivity {

    private Communicator communicator;
    private CountDownTimer locationTimer;
    private ConstraintLayout clNavMap;
    private NavView navView;
    private static TextView directionText;
    private static ImageView arrowImage;
    private static ImageView arrowImage2;
    private ImageView locationDot;
    private DirectionsRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        communicator = new Communicator();
        Toolbar toolbar = findViewById(R.id.toolbar);
        clNavMap = findViewById(R.id.clNavMap);
        navView = findViewById(R.id.navView);
        TextView tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        directionText = findViewById(R.id.tvDirections);
        ImageButton imBtnBackArrow = findViewById(R.id.imBtnBackArrow);
        ImageView ivUTD = findViewById(R.id.ivUTD);
        arrowImage = findViewById(R.id.ivDirectionArrow1);
        arrowImage2 = findViewById(R.id.ivDirectionArrow2);
        navView = findViewById(R.id.navView);
        locationDot = findViewById(R.id.ivLocationDot);
        ViewTreeObserver vto = clNavMap.getViewTreeObserver();

        setSupportActionBar(toolbar);
        ivUTD.setVisibility(View.GONE);
        imBtnBackArrow.setVisibility(View.VISIBLE);
        tvToolbarTitle.setText(R.string.final_destination);
        //updateLocation();

        String finalDestination = getIntent().getExtras().getString("finalDestination");
        tvToolbarTitle.setText("Path to " + finalDestination);

        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    clNavMap.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    clNavMap.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                int width  = clNavMap.getMeasuredWidth();
                int height = clNavMap.getMeasuredHeight();

                navView.setImageWidth(width);
                navView.setImageHeight(height);
                navView.loadMap();

                if (getIntent().hasExtra("navigationArray")) {

                    double[] coordinates = getIntent().getExtras().getDoubleArray("navigationArray");

                    double[][] segmentedCoordinates= formatPathData(coordinates);
                    String[] directionsList= DirectionMessagesList(segmentedCoordinates);



                    mAdapter.setData(Arrays.asList(directionsList));


                    directionText.setText("" + String.valueOf(coordinates[1]));
                    navView.displayPath(coordinates);

                }

            }
        });

        imBtnBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent startMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(startMainActivity);

            }
        });



        // Recycler View is being set here..
        ArrayList<String> tempStrings= new ArrayList<>();
        tempStrings.add("hello");
        tempStrings.add("hello1");
        tempStrings.add("hello2");
        tempStrings.add("hello3");
        tempStrings.add("hello4");
        tempStrings.add("hello5");

        RecyclerView directionsRecyclerView = findViewById(R.id.rv_recyclerView);
        directionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter= new DirectionsRecyclerViewAdapter(this, tempStrings);
        directionsRecyclerView.setAdapter(mAdapter);

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
        if(zChange(rArray1,rArray2,rArray3) > 0 ){
            //set the image to be an up arrow since we're going up a floor
            arrowImage.setRotation(90);
            arrowImage2.setRotation(90);
            directionText.setText("Go up " + zChange(rArray1,rArray2,rArray3) + " floor(s).");
        }
        else if(zChange(rArray1,rArray2,rArray3) < 0) {
            //set the image to be a down arrow since we're going down a floor
            arrowImage.setRotation(270);
            arrowImage2.setRotation(270);
            directionText.setText("Go down " + -zChange(rArray1,rArray2,rArray3) + " floor(s).");
        }
        //this determines whether the arrow shows right or left
        else {
            if (determineDirection(rArray1, rArray2, rArray3) > epsilon) {
                //set the image to be a right arrow since we're going right
                arrowImage.setRotation(0);
                arrowImage2.setRotation(0);
                directionText.setText("Take a right turn.");
                return false;
            } else if (determineDirection(rArray1, rArray2, rArray3) < -epsilon) {
                //set the image to be a left arrow since we're going left
                arrowImage.setRotation(180);
                arrowImage2.setRotation(180);
                directionText.setText("Take a left turn.");
                return false;
            } else
                arrowImage.setRotation(90);
                arrowImage.setRotation(90);
                directionText.setText("Keep straight");
            return true;
        }
        return false;
    }
    public static boolean getDirectionMessage(double[] rArray1, double[] rArray2, double[] rArray3) {
        // same method as above but modified for RecyclerView to format strings.
        //absolute value margin of error for a straight line
        double epsilon = 0.25854206621;

        //this determines whether you should go up a floor or down a floor (the value of z changes)
        // I might have to wrap code for vertical movement.
        if(zChange(rArray1,rArray2,rArray3) > 0 ){
            //set the image to be an up arrow since we're going up a floor

            directionText.setText("Go up " + zChange(rArray1,rArray2,rArray3) + " floor(s).");
        }
        else if(zChange(rArray1,rArray2,rArray3) < 0) {
            //set the image to be a down arrow since we're going down a floor

            directionText.setText("Go down " + -zChange(rArray1,rArray2,rArray3) + " floor(s).");
        }
        //this determines whether the arrow shows right or left
        else {
            if (determineDirection(rArray1, rArray2, rArray3) > epsilon) {
                //set the image to be a right arrow since we're going right

                directionText.setText("Take a right turn.");
                return false;
            } else if (determineDirection(rArray1, rArray2, rArray3) < -epsilon) {
                //set the image to be a left arrow since we're going left

                directionText.setText("Take a left turn.");
                return false;
            } else

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

    public static double zChange(double[] zArray1, double[] zArray2, double[] zArray3) {
        // subtract all 3. If dif=0 no change, if dif= pos go up, if dif= neg go down
        return Math.round((zArray3[0]-zArray1[0])/3.6576);
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

    public static boolean shouldRecalculatePath(double coordinates[][], double myCord[]) {
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

    public double[][] formatPathData(double coordinatesArray[]){
        // formats the 1D array of doubles received from ArcGIS and turns them into a 2D array with coordinates in the
        // z,x,y format on each row
        double[][] coordinatesList=new double[coordinatesArray.length/3][3];
        for(int i=0; i<coordinatesArray.length; i++){
            coordinatesList[i/3][i%3]= coordinatesArray[i];
        }

        return coordinatesList;

    }

    public double[] RecalculatePath(double coordinates[][], double myCord[]){
        if(shouldRecalculatePath(coordinates,myCord)== true){

            AsyncResponse asyncResponse = new AsyncResponse() {
                @Override
                public void returnResponse(String result) {
                    UiService.printString(result);
                }
            };

            WebServiceCaller.request(NavigationActivity.this, "https://ecsclark18.utdallas.edu/", "index.php", "CMX", MainActivity.etTo.getText().toString(), MainActivity.takeStairs, MainActivity.takeElevator, asyncResponse);
            return null;
        }
        else
            return null;

    }
    public String[] DirectionMessagesList(double[][] arr){
        ArrayList<String> directionsList= new ArrayList<String>();
        double distance=0.0;
        double epsilon = 0.25854206621;
        for(int i=0;i<arr.length;i++){
            if(i==0 || i==arr.length-1)
                continue;
            else
            {
                double[] previous= arr[i-1];
                double[] current= arr[i];
                double[] next= arr[i+1];
                double zChange = zChange(previous, current, next);
                double Direction= determineDirection(previous,current,next);


                if(zChange !=0 || Direction>epsilon || Direction<-epsilon )
                {
                    if(distance>0){
                        directionsList.add("Go straight for " + distance);

                    }
                    distance=0;

                    if(zChange>0){
                        directionsList.add("Go up");
                    }
                    else if(zChange<0){
                        directionsList.add("Go down");
                    }
                    else if(Direction>epsilon){
                        directionsList.add("Turn right");
                    }
                    else if(Direction<-epsilon){
                        directionsList.add("Turn left");
                    }

                }
                else
                    distance+=distanceFormula(current,next);
            }

        }


        return directionsList.toArray(new String[directionsList.size()]);
    }

}