package co.edu.uniandes.clienteandroid;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static String HOLA = "HOLA";
    public static String SELECCIONE = "SELECCIONE PROTOCOLO";
    public static String TCP = "TCP";
    public static String UDP = "UDP";
    public static String IP = "10.0.0.2";

    private RadioButton radioSele;
    private RadioGroup conexiones;
    private Button boton;
    private Button boton2;
    private String ip;
    private int puerto;
    TextView messageTextView;
    private boolean parar;
    private String conex;
    Location loc;
    private GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parar = true;
        conexiones = (RadioGroup) findViewById(R.id.radioGroup);
        messageTextView = (TextView) findViewById(R.id.textView2);
        boton = (Button) findViewById(R.id.button);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = conexiones.getCheckedRadioButtonId();
                EditText ipE = (EditText) findViewById(R.id.editText);
                ip = ipE.getText().toString();
                EditText puE = (EditText) findViewById(R.id.editText2);
                puerto = Integer.parseInt(puE.getText().toString());
                radioSele = (RadioButton) findViewById(id);
                conex = radioSele.getText().toString();
                new clase().execute(radioSele.getText().toString());
            }
        });
        boton2 = (Button) findViewById(R.id.button2);
        boton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parar = true;
            }
        });
        buildGoogleApiClient();
        createLocationRequest();
        mGoogleApiClient.connect();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void handshake(String boton)
    {
        try {
            Thread.sleep(2000);
            //while(!parar) {
                Socket cliente = new Socket(ip, puerto);
                PrintWriter out = new PrintWriter(cliente.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                out.println(HOLA);
                String linea = in.readLine();
                if (linea.equals(SELECCIONE)) {
                    if (conex.equals(TCP)) {
                        out.println(TCP);
                        String Text = "Mi ubicación actual es: " + "\n Lat = ";
                               // + loc.getLatitude() + "\n Long = " + loc.getLongitude()
                                //+"\n Vel = "+loc.getSpeed()+"\n Alt = "+loc.getAltitude();
                        out.println(Text);
                    } else if (conex.equals(UDP)) {
                        out.println(UDP);
                        cliente.close();
                        conectarUDP();
                    }
                }
            //}
            conex="";
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void conectarUDP()
    {
        try{
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName(ip);
            byte[] sendData = new byte[1024];
            byte[] receiveData = new byte[1024];
            String sentence = "Mi ubicación actual es: " + "\n Lat = ";
                   // + loc.getLatitude() + "\n Long = " + loc.getLongitude()
                    //+"\n Vel = "+loc.getSpeed()+"\n Alt = "+loc.getAltitude();
            sendData = sentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, puerto);
            clientSocket.send(sendPacket);
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            String modifiedSentence = new String(receivePacket.getData());
            System.out.println("FROM SERVER:" + modifiedSentence); clientSocket.close();
        }
        catch(Exception e)
        {
            System.out.println("No hubo UDP :(");
        }
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        System.out.println("yap "+loc);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        loc = location;
        parar = false;
    }

    private class clase extends AsyncTask<String,String,String>
    {
        @Override
        protected String doInBackground(String... params) {
            handshake(params[0]);
            return "";
        }
    }
}
