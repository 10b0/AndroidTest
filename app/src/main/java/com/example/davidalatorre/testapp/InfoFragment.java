package com.example.davidalatorre.testapp;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

// Geolocalizacion imports
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import android.location.Location;
import android.util.Log;

// Sensors imports
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;




public class InfoFragment extends Fragment implements ConnectionCallbacks, OnConnectionFailedListener, SensorEventListener {

    final static String ARG_POSITION = "position";
    int mCurrentPosition = -1;

    protected static final String TAG = "InfoFragment";

    // Google play services
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;

    // device sensor manager
    private SensorManager mSensorManager;
    private boolean useGyro = false;

    private TextView article;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
        }

        // Inflate the layout for this fragment adding TextEdit
        return inflater.inflate(R.layout.fragment_info, container, false);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        //Text view
        article = (TextView) getActivity().findViewById(R.id.info);

        Bundle args = getArguments();
        if (args != null) {
            // Set article based on argument passed in
            updateArticleView(args.getInt(ARG_POSITION));
        } else if (mCurrentPosition != -1) {
            // Set article based on saved instance state defined during onCreateView
            updateArticleView(mCurrentPosition);
        }

    }


    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void updateArticleView(int position) {

        if(position < 2) {
            article.setText(Ipsum.Articles[position]);
        } else if (position == 2){
            //position = 3 geolocalizacion
            mGoogleApiClient = new GoogleApiClient.Builder(this.getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        } else if(position > 3) {
            //position = 4 Gyroscope
            //Init sensor
            useGyro = true;
            mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        }
        mCurrentPosition = position;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current article selection in case we need to recreate the fragment
        outState.putInt(ARG_POSITION, mCurrentPosition);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // get Geolocalizacion values
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {

            article.setText(getString(R.string.latitude) + String.format("%f",
                    mLastLocation.getLatitude()) + "\n" +
                    getString(R.string.longitude) +  String.format("%f",
                    mLastLocation.getLongitude()));
        } else {

            article.setText(getString(R.string.location_fail));
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());

    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }


    @Override
    public void onPause() {
        super.onPause();

        if(useGyro) {
            //stop listener and save battery
            mSensorManager.unregisterListener(this);
        }
    }

    public void onResume() {
        super.onResume();

        if(useGyro) {
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        article.setText(getString(R.string.x_axis) + x + "\n" + getString(R.string.x_axis) + y + "\n" + getString(R.string.z_axis) + z);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Not in use
    }
}
