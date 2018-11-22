package com.example.raulb.conexion_sql_server;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnLeerCodQR;
    private Button btnRegistrPresencia;
    private Button btnObtenerDatos;
    private TextView textViewCodActivo;
    private TextView textViewDetalles;
    private String codigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLeerCodQR = (Button) findViewById(R.id.btnLeerQR);
        btnRegistrPresencia = (Button) findViewById(R.id.btnMarcarPresente);
        btnObtenerDatos = (Button) findViewById(R.id.btnLeerBD);
        textViewCodActivo = (TextView) findViewById(R.id.textViewCodActivo);
        textViewDetalles = (TextView) findViewById(R.id.textViewDetalles);
        textViewDetalles.setMovementMethod(new ScrollingMovementMethod());
        btnLeerCodQR.setOnClickListener(this);
        btnRegistrPresencia.setOnClickListener(this);
        btnRegistrPresencia.setEnabled(false);
        btnObtenerDatos.setOnClickListener(this);
        btnObtenerDatos.setEnabled(false);

        //}
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLeerQR: //si se hace click en el boton se llama a la actividad de lectura de QR
                Intent intent = new Intent(this, SimpleScannerActivity.class);
                startActivityForResult(intent,190); //llamada esperando un resultado con el ID 190
                break;
            case R.id.btnMarcarPresente:
                registraPresencia(codigo);
                break;
            case R.id.btnLeerBD:
                showMessageDialog("Detalles de: " + codigo,obtenerDatosActivoBD(codigo),false);
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
                        codigo = result.substring(5, 20).trim();
                        String[] resultDividido = result.split(";");
                        textViewCodActivo.setText(codigo);
                        textViewDetalles.setText("");
                        //editTextDetalles.setText(result);
                        textViewDetalles.setText(resultDividido[1].trim() + '\n' +
                                resultDividido[2].trim() + '\n' + resultDividido[3].trim() + '\n' + resultDividido[4].trim() + '\n' +
                                resultDividido[5].trim() + '\n' + resultDividido[6].trim() + '\n' + resultDividido[7].trim() + '\n' +
                                resultDividido[8].trim() + '\n' + resultDividido[9].trim() + '\n' + resultDividido[10].trim());
                        //showMessageDialog("resultado",result,false);
                        btnRegistrPresencia.setEnabled(true);
                        btnObtenerDatos.setEnabled(true);
                    } else {
                        textViewCodActivo.setText("");
                        textViewDetalles.setText("");
                        btnRegistrPresencia.setEnabled(false);
                        btnObtenerDatos.setEnabled(false);
                        showMessageDialog("Error","El código escaneado no es del tipo QR",false);
                    }
                }
                if (resultCode == Activity.RESULT_CANCELED) {
                //No exite resultado
                    textViewCodActivo.setText("");
                    textViewDetalles.setText("");
                    btnRegistrPresencia.setEnabled(false);
                    btnObtenerDatos.setEnabled(false);
                    showMessageDialog("Error", "No se pudo leer el código QR", false);
                }
            }
        }
        catch (Exception e){
            textViewCodActivo.setText("");
            textViewDetalles.setText("");
            btnRegistrPresencia.setEnabled(false);
            btnObtenerDatos.setEnabled(false);
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

    public Connection conexionBD() {
        Connection conexion=null;
        try{
            StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            conexion= DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.0.4;databaseName=activos_db;user=sa;password=EmdLa1975;");
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            btnRegistrPresencia.setEnabled(false);
            btnObtenerDatos.setEnabled(false);
        }
        return conexion;
    }

    public void registraPresencia(String codActivo){
        try{
            PreparedStatement pst=conexionBD().prepareStatement("update dbo.activo set activo_fecha_registro = CONVERT (date, getdate()) where activo_codigo_generado=?");
            pst.setString(1,codActivo);
            pst.executeUpdate();
            Toast.makeText(getApplicationContext(),"Registro de presencia exitoso",Toast.LENGTH_LONG).show();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            btnRegistrPresencia.setEnabled(false);
            btnObtenerDatos.setEnabled(false);
        }
    }

    public String obtenerDatosActivoBD(String codigoActivo){
        String datos="Sin datos";
        try{

            Statement st=conexionBD().createStatement();
            String cadenaSQL = "Select Descripcion, Responsable, Edificio, Piso, Ambiente, FechaRegistro, Estado, Observaciones from listado_asignaciones where Codigo='" + codigoActivo + "'";
            ResultSet rs=st.executeQuery(cadenaSQL);
            while (rs.next()){
                String descripcionActivo = rs.getString("descripcion") == null ? "Sin descripción" : rs.getString("descripcion").trim();
                String observacionesActivo = rs.getString("observaciones") == null ? "Sin observaciones" : rs.getString("observaciones").trim();
                datos= descripcionActivo + "\nResponsable: " + rs.getString("responsable") + "\nEdificio: " + rs.getString("edificio") +
                        "\nPiso: " + rs.getString("piso") + "\nAmbiente: " +  rs.getString("ambiente") + "\nEstado: " +  rs.getString("estado") +
                        "\nObservaciones: " + observacionesActivo + "\nUltima verificación: " +  rs.getString("fecharegistro");
            }
        }catch(Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            btnRegistrPresencia.setEnabled(false);
            btnObtenerDatos.setEnabled(false);
        }
        return datos;
    }
}
