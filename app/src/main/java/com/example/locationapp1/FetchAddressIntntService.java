package com.example.locationapp1;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.google.android.gms.location.LocationResult;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FetchAddressIntntService extends IntentService {
    private ResultReceiver resultReceiver;
    public FetchAddressIntntService(){

        super("FetchAddressIntntService");

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent!=null){
            String errorMessage="";
            resultReceiver=intent.getParcelableExtra(Constants.RECEIVER);
            Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
            if(location == null){
                return;
            }
            Geocoder geocoder=new Geocoder(this, Locale.getDefault());
            List<Address> addresses=null;
            try {
                addresses=geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1);



            }catch (Exception exception){
                errorMessage=exception.getMessage();
            }
            if(addresses==null || addresses.isEmpty()){
                deliverResultToReceiver(Constants.FAILURE_RESULT,errorMessage);
            }else {
                Address address=addresses.get(0);
                ArrayList<String> addresssFragments =new ArrayList<>();
                for (int i = 0;i <=address.getMaxAddressLineIndex();i++){
                    addresssFragments.add(address.getAddressLine(i));

                }
                deliverResultToReceiver(
                        Constants.SUCCESS_RESULT,
                        TextUtils.join(
                                Object.requireNonNull(System.getProperty("line.separator")),
                                addresssFragments
                        )
                );

            }
        }

    }
    private void deliverResultToReceiver(int resultCode,String addessMessage){
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY,addessMessage);
        resultReceiver.send(resultCode,bundle);
    }

}
