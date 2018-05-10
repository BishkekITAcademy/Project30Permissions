package itacademy.com.project30permissionmaps;

import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import itacademy.com.project30permissionmaps.utils.AndroidUtils;
import itacademy.com.project30permissionmaps.utils.PermissionUtils;

import static itacademy.com.project30permissionmaps.utils.AppConstants.REQUEST_CODE_LOCATION_PERMISSION;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;

    private Marker mCarMarker;

    private LatLng startLocation = new LatLng(42.87, 74.59);
    private LatLng endLocation = new LatLng(42.89, 74.61);

    private float fraction;

    private Handler mHandler = new Handler();

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            animateCar();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMap();
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkForLocationServices();
    }

    private void enableMyLocation() {
        if (PermissionUtils.checkLocationPermission(this)) {
            if (mGoogleMap != null) {
                mGoogleMap.setMyLocationEnabled(true);
            }
        }
    }

    private void checkForLocationServices() {
        if (PermissionUtils.isLocationServicesEnabled(this)) {
            enableMyLocation();
        } else {
            callServiceDisabledDialog();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_GRANTED) {
                    AndroidUtils.showShortToast(this, "LOCATION PERMISSION GRANTED");
                }
            }
        }
    }

    private void callServiceDisabledDialog() {
        new AlertDialog.Builder(this)
                .setTitle("GPS is turned off")
                .setMessage("Please, turn on GPS to HIGH ACCURACY mode!")
                .setCancelable(false)
                .setPositiveButton("SETTINGS", onClickListener)
                .create()
                .show();
    }

    private final DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            dialog.dismiss();
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        enableMyLocation();

        drawCarMarker();
    }

    private void drawCarMarker() {
        MarkerOptions options = new MarkerOptions()
                .position(startLocation)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car));

        mCarMarker = mGoogleMap.addMarker(options);

        animateToCar(startLocation);
    }

    private void animateToCar(LatLng carLatLng) {
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(carLatLng, 80);
        mGoogleMap.animateCamera(cu);

//        mHandler.post(mRunnable);


    }

    private void animateCar() {
        ValueAnimator carAnimator = ValueAnimator.ofFloat(0, 1);
        carAnimator.setDuration(2000);
        carAnimator.setInterpolator(new LinearInterpolator());
        carAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                fraction = animation.getAnimatedFraction();

                LatLng newLocation = SphericalUtil.interpolate(startLocation, endLocation, fraction);
                mCarMarker.setPosition(newLocation);

                float bearing = (float) SphericalUtil.computeHeading(startLocation, endLocation);
                mCarMarker.setRotation(bearing);

                CameraUpdate cu = CameraUpdateFactory.newCameraPosition(
                        new CameraPosition.Builder()
                                .target(newLocation)
                                .zoom(15.5f)
                                .build());

                mGoogleMap.moveCamera(cu);
            }
        });
        carAnimator.start();
        mHandler.postDelayed(mRunnable, 3000);
    }
}
