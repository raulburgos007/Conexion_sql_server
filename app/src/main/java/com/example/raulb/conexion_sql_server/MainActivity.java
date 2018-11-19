package com.example.raulb.conexion_sql_server;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnLeerCodQR;
    private EditText editText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLeerCodQR = (Button) findViewById(R.id.btnLeerQR);
        btnLeerCodQR.setOnClickListener(this);
        Bundle resultado = this.getIntent().getExtras();
        if (resultado != null) {
            String datos = resultado.getString("datos");
            showMessageDialog("resultado",datos,false);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLeerQR:

                Intent intent = new Intent(this, SimpleScannerActivity.class);
                startActivityForResult(intent,190);



                //if (resultado != null){         }
                break;
        }
    }
    public void showMessageDialog(String title, String message, boolean cancelButon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title);

        if (cancelButon) {
            builder.setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // TODO: handle the OK
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
        } else {
            builder.setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // TODO: handle the OK
                        }
                    });
        }
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
