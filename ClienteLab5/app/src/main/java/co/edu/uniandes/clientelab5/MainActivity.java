package co.edu.uniandes.clientelab5;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
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

public class MainActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parar = false;
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

        /* Use the LocationManager class to obtain GPS locations */
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        MyLocationListener mlocListener = new MyLocationListener();
        mlocListener.setMainActivity(this);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
             public void requestPermissions(@NonNull String[] permissions, int requestCode);
            // here to request the missing permissions, and then overriding
            public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) mlocListener);
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
        try
        {
            Thread.sleep(2000);
            while(!parar) {
                Socket cliente = new Socket(ip, puerto);
                PrintWriter out = new PrintWriter(cliente.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                out.println(HOLA);
                String linea = in.readLine();
                if (linea.equals(SELECCIONE)) {
                    if (conex.equals(TCP)) {
                        out.println(TCP);
                        String Text = "Mi ubicación actual es: " + "\n Lat = "
                                + loc.getLatitude() + "\n Long = " + loc.getLongitude()
                                +"\n Vel = "+loc.getSpeed()+"\n Alt = "+loc.getAltitude();
                        out.println(Text);
                    } else if (conex.equals(UDP)) {
                        out.println(UDP);
                        cliente.close();
                        conectarUDP();
                    }
                }
            }
            conex="";
        }
        catch (Exception e)
        {
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
            String sentence = "Mi ubicación actual es: " + "\n Lat = "
                    + loc.getLatitude() + "\n Long = " + loc.getLongitude()
                    +"\n Vel = "+loc.getSpeed()+"\n Alt = "+loc.getAltitude();
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

    public void setLocation(Location loc) {
        //Obtener la dirección de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            this.loc=loc;
        }
    }

    private class MyLocationListener implements LocationListener {
        MainActivity mainActivity;

        public MainActivity getMainActivity() {
            return mainActivity;
        }

        public void setMainActivity(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public void onLocationChanged(Location loc) {
            // Este método se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la detección de un cambio de ubicacion
            loc.getLatitude();
            loc.getLongitude();
            loc.getSpeed();
            loc.getAltitude();
            String Text = "Mi ubicación actual es: " + "\n Lat = "
                    + loc.getLatitude() + "\n Long = " + loc.getLongitude()
                    +"\n Vel = "+loc.getSpeed()+"\n Alt = "+loc.getAltitude();
            messageTextView.setText(Text);
            this.mainActivity.setLocation(loc);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este método se ejecuta cuando el GPS es desactivado
            messageTextView.setText("GPS Desactivado");
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este método se ejecuta cuando el GPS es activado
            messageTextView.setText("GPS Activado");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // Este método se ejecuta cada vez que se detecta un cambio en el
            // status del proveedor de localización (GPS)
            // Los diferentes Status son:
            // OUT_OF_SERVICE -> Si el proveedor esta fuera de servicio
            // TEMPORARILY_UNAVAILABLE -> Tempòralmente no disponible pero se
            // espera que este disponible en breve
            // AVAILABLE -> Disponible
        }

    }/* End of Class MyLocationListener */

    private class clase extends AsyncTask<String,String,String>
    {
        @Override
        protected String doInBackground(String... params) {
            handshake(params[0]);
            return "";
        }
    }
}
