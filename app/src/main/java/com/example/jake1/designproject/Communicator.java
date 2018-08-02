package com.example.jake1.designproject;

import android.app.Activity;
import android.content.Context;
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

import java.util.HashMap;
import java.util.Map;

public class Communicator {

    private boolean takeStairs;
    private boolean takeElevator;
    private String fromLocation;
    private String toLocation;
    private double[] coordinates;

    Communicator(){

    }

    public void setTakeStairs(boolean stairs) {

        takeStairs = stairs;

    }

    public void setTakeElevator(boolean elevator) {

        takeElevator = elevator;

    }

    public void setFromLocation(String from) {

        fromLocation = from;

    }

    public void setToLocation(String to) {

        toLocation = to;

    }

    public void queryServer() {

        coordinates = new double[] {0,763730.923299998,2147981.0242,0,763729.7814999968,2147981.0053999983,0,763729.7805999964,2147980.8935000002};

    }

    public double[] getCoordinates() {
        return coordinates;
    }

}
