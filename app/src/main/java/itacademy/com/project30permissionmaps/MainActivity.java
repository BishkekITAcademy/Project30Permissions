package itacademy.com.project30permissionmaps;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import itacademy.com.project30permissionmaps.utils.AndroidUtils;
import itacademy.com.project30permissionmaps.utils.PermissionUtils;

import static itacademy.com.project30permissionmaps.utils.AppConstants.REQUEST_CODE_LOCATION_PERMISSION;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkForLocationServices();
    }

    private void enableMyLocation() {
        if (PermissionUtils.checkLocationPermission(this)) {
            AndroidUtils.showLongSnackBar(this, "LOCATION ACCESSED");
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
}
