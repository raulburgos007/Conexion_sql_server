package com.example.raulb.conexion_sql_server;

import android.Manifest;
import android.app.Activity;
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
    private EditText editTextCodActivo;
    private EditText editTextDetalles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLeerCodQR = (Button) findViewById(R.id.btnLeerQR);
        editTextCodActivo = (EditText) findViewById(R.id.editTextCodActivo);
        editTextDetalles = (EditText) findViewById(R.id.editTextDetalles);
        btnLeerCodQR.setOnClickListener(this);
        //Bundle resultado = this.getIntent().getExtras();
        //if (resultado != null) {
        //    String datos = resultado.getString("datos");
        //    showMessageDialog("resultado",datos,false);
        //}
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLeerQR: //si se hace click en el boton se llama a la actividad de lectura de QR
                Intent intent = new Intent(this, SimpleScannerActivity.class);
                startActivityForResult(intent,190); //llamada esperando un resultado con el ID 190
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //aca se recibe el retorno otorgado por laactividad de lectura de codigos QR
        //En el if se usa el mismo id que el staractivityfor result
        try {
            if (requestCode == 190) {
                if (resultCode == Activity.RESULT_OK) {
                    String formato = data.getStringExtra("formato");
                    if (formato.equals("QRCODE")) {
                        //existe resultado y el codigo es QR
                        String result = data.getStringExtra("datos");
                        String codigo = result.substring(5, 20).trim();
                        String[] resultDividido = result.split(";");
                        editTextCodActivo.setText(codigo);
                        editTextDetalles.getText().clear();
                        //editTextDetalles.setText(result);
                        editTextDetalles.setText(resultDividido[1].trim() + '\n' +
                                resultDividido[2].trim() + '\n' + resultDividido[3].trim() + '\n' + resultDividido[4].trim() + '\n' +
                                resultDividido[5].trim() + '\n' + resultDividido[6].trim() + '\n' + resultDividido[7].trim() + '\n' +
                                resultDividido[8].trim() + '\n' + resultDividido[9].trim() + '\n' + resultDividido[10].trim());
                        //showMessageDialog("resultado",result,false);
                    } else {
                        editTextCodActivo.getText().clear();
                        editTextDetalles.getText().clear();
                        showMessageDialog("Error","El código escaneado no es del tipo QR",false);
                    }
                }
                if (resultCode == Activity.RESULT_CANCELED) {
                //No exite resultado
                    editTextCodActivo.getText().clear();
                    editTextDetalles.getText().clear();
                    showMessageDialog("Error", "No se pudo leer el código QR", false);
                }
            }
        }
        catch (Exception e){
            editTextCodActivo.getText().clear();
            editTextDetalles.getText().clear();
            Log.e(TAG, e.getLocalizedMessage());
            showMessageDialog("Error", "Error al leer e interpretar  el código QR\n"+e.getLocalizedMessage(), false);                    }
    }//onActivityResult


    //gestion de dialogos
    public void showMessageDialog(String title, String message, boolean cancelButon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title);

        if (cancelButon) {
            //Se mostrara el boton cancel
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
            //Solo se mostrara el boton ok
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
