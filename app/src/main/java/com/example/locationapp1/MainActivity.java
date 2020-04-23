package com.example.locationapp1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.security.Permission;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private TextView textLatLong,textAddress;
    private ProgressBar progressBar;
    private ResultReceiver resultReceiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultReceiver=new AddressResultRceiver(new Handler());

        textLatLong = findViewById(R.id.textLatLong);
        progressBar = findViewById(R.id.progressBar);
        textAddress = findViewById(R.id.textAddess);


        findViewById(R.id.buttonGetCurrentLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_LOCATION_PERMISSION
                    );
                }else {
                    getCurrentLocation();
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length>0)    {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }else {

                Toast.makeText(this,"Permission denied!",Toast.LENGTH_SHORT ).show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        progressBar.setVisibility(View.VISIBLE);


        LocationRequest locationRequest;
        LocationRequest.locationRequest = new LocationRequest();
        LocationRequest setInterval (10000);
        LocationRequest setFastestInterval (30000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public onLocationResult(LocationResult locationResult) {
                        super.onLocationResults(locationResult);
                        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocationIndex().size() - 1;
                            double latitude =
                                    locationResult.getLocations.get(latestLocationIndex).getLatitude();
                            double longitude =
                                    LocationResult.getLocations().get(latestLocationIndex).getLongitude();
                            textLatLong.setText(
                                    String.format(
                                            "Latitude: %s\nLongitude: %s",
                                            latitude,
                                            longitude
                                    )
                            );
                            Location location=new Location("providerNA");
                            location.setLatitude(latitude);
                            location.setLongitude(longitude);
                            fetchAddressFromLatLong(location);


                        }else {
                            progressBar.setVisibility(View.GONE);
                        }

                    }


                }, Looper.getMainLooper());





    }
    private void fetchAddressFromLatLong(Location location){
        Intent intent=new Intent(this,FetchAddressIntntService.class);
        intent.putExtra(Constants.RECEIVER,resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA,location);
        startService(intent);

    }
    private class AddressResultRceiver extends ResultReceiver{
        public AddressResultRceiver(Handler handler){
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode==Constants.SUCCESS_RESULT){
                textAddress.setText(resultCode.getString(Constants.RESULT_DATA_KEY));
            }else {
                Toast.makeText(MainActivity.this,resultData.getString(Constants.RESULT_DATA_KEY),Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.GONE);
        }
    }
}
