package com.example.raulb.conexion_sql_server;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

import static android.content.ContentValues.TAG;

public class SimpleScannerActivity extends Activity implements ZBarScannerView.ResultHandler {
    private ZBarScannerView mScannerView;


    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        int permiso = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if(permiso != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        233456);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        mScannerView = new ZBarScannerView(this);    // Programmatically initialize the scanner view
        setContentView(mScannerView);// Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 233456: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void handleResult(Result rawResult) {
        final String datos = rawResult.getContents();
        final String formato = rawResult.getBarcodeFormat().getName();
        //final String format = rawResult.getBarcodeFormat().getName();
        try{
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),notification);
            r.play();
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
            //Intent returnIntent = new Intent();
            //setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
        ;
        //Bundle resultado = new Bundle();
        //resultado.putString("datos",datos);
        //Intent miIntent = new Intent(this, MainActivity.class);
        //miIntent.putExtras(resultado);
        //startActivity(miIntent);
        Intent returnIntent = new Intent();
        returnIntent.putExtra("datos", datos);
        returnIntent.putExtra("formato", formato);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
        //showMessageDialog("Resultado", fullMessage, false);
        //mScannerView.resumeCameraPreview(this);
    }
}
