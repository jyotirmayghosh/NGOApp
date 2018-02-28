package com.example.jyotirmayghosh.ngo.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jyotirmayghosh.ngo.R;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class LocationFragment extends Fragment implements LocationListener {

    private ConstraintLayout locationLayout;
    private TextView setLocationView;
    LocationManager locationManager;
    String name, phone, latitude, longitude=null;
    ProgressDialog progressDialog;
    String serverUrl = "https://accelerated-invento.000webhostapp.com/send_location.php";
    Button getlocatonButton;

    public LocationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SharedPreferences preferences = this.getActivity().getSharedPreferences("NOG_Pref", MODE_PRIVATE);
        String nameSaved = preferences.getString("name", "N/A");
        String phoneSaved = preferences.getString("phone", "N/A");

        name=nameSaved;
        phone=phoneSaved;
        progressDialog = new ProgressDialog(getContext());
        getlocatonButton=getView().findViewById(R.id.btnGetLocation);
        getlocatonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Getting location, please wait...");
                progressDialog.show();
                getLocation();
            }
        });
        locationLayout=getView().findViewById(R.id.getLocationConstrain);
        setLocationView=getView().findViewById(R.id.tvLocation);
        locationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Getting location, please wait...");
                progressDialog.show();
                getLocation();
            }
        });

    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        setLocationView.setText("Latitude: " + location.getLatitude() + "\nLongitude: " + location.getLongitude()+"\n");
        setLocationView.setVisibility(View.VISIBLE);
        progressDialog.dismiss();
        try {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            setLocationView.setText(setLocationView.getText()+ addresses.get(0).getAddressLine(0));
            latitude=location.getLatitude()+"";
            longitude=location.getLongitude()+"";
        }catch(Exception e)
        {

        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        Toast.makeText(getContext(), " GPS Enabled.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String s) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage("Enable GPS to send current location.")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.action_menu_location, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_send) {
            if (longitude==null)
            {
                Toast.makeText(getContext(), "Click on \"GET CURRENT LOCATION \"", Toast.LENGTH_SHORT).show();
            }
            else {
                progressDialog.setMessage("Send location, please wait...");
                progressDialog.show();

                final RequestQueue queue = Volley.newRequestQueue(getContext());
                StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressDialog.dismiss();
                                setLocationView.setText("");
                                Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                                queue.stop();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.getMessage();
                        error.printStackTrace();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("user_name", name);
                        params.put("user_phone", phone);
                        params.put("latitude", latitude);
                        params.put("longitude", longitude);
                        return params;
                    }
                };
                queue.add(stringRequest);
            }
        } else if (item.getItemId() == R.id.action_discard) {
            setLocationView.setText("");
            Toast.makeText(getContext(), "Location Discarded.", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
