package co.edu.uniandes.clienteandroid;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
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
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static String HOLA = "HOLA";
    public static String SELECCIONE = "SELECCIONE PROTOCOLO";
    public static String TCP = "TCP";
    public static String UDP = "UDP";
    public static String IP = "10.0.0.2";

    private RadioButton radioSele;
    private RadioGroup conexiones;
    private Button boton;
    private Button boton2;
    private Socket socket;
    private DatagramSocket socket2;
    private String ip;
    private int puerto;
    TextView messageTextView;
    private String conex;
    private Location loc;
    private Handler handler = new Handler();
    private  Handshake hand = new Handshake();
    private InetAddress serverAddr;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        buildGoogleApiClient();
        final LocationService locs=new LocationService(getApplicationContext());
        loc=locs.darUbicacion();
        conexiones = (RadioGroup) findViewById(R.id.radioGroup);
        messageTextView = (TextView) findViewById(R.id.textView2);
        boton = (Button) findViewById(R.id.button);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                loc=locs.darUbicacion();
                int id = conexiones.getCheckedRadioButtonId();
                EditText ipE = (EditText) findViewById(R.id.editText);
                ip = ipE.getText().toString();
                EditText puE = (EditText) findViewById(R.id.editText2);
                puerto = Integer.parseInt(puE.getText().toString());
                radioSele = (RadioButton) findViewById(id);
                conex = radioSele.getText().toString();
                handler.postDelayed(hand,10);
            }
        });
        boton2 = (Button) findViewById(R.id.button2);
        boton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(hand, null);
            }
        });
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
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

    @Override
    public void onConnected(Bundle bundle)
    {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    public class Handshake implements Runnable{

        @Override
        public void run() {

            Thread thread = new Thread(new Cliente());
            thread.start();

            while (thread.isAlive())
            {
            }
                try {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out.println(HOLA);
                    String linea = in.readLine();
                    System.out.println(linea);
                    if (linea.equals(SELECCIONE)) {
                        if (conex.equals("TCP")) {
                            out.println(TCP);
                            out.println(loc.getLatitude() + "," + loc.getLongitude() + "," +
                                    "2625.0" + "," + loc.getSpeed());
                        } else {
                            out.println(UDP);
                        }
                    }

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();

                }

                handler.postDelayed(hand, 1000);

        }
    }

    class Cliente implements Runnable {

        @Override
        public void run() {

            try {

                serverAddr = InetAddress.getByName(ip);

                if(conex.equals("TCP"))
                {
                    socket = new Socket(ip, puerto);
                }
                else{
                    socket2 = new DatagramSocket();
                    String b = loc.getLatitude()+","+loc.getLongitude()+","+
                            "2625.0"+","+loc.getSpeed();
                    byte[] buffer = b.getBytes();
                    DatagramPacket packet = new DatagramPacket(
                            buffer, buffer.length, serverAddr, puerto);
                    socket2.send(packet);
                }

            } catch (UnknownHostException e1) {

                e1.printStackTrace();
            } catch (IOException e1) {

                e1.printStackTrace();

            }

        }

    }
}
