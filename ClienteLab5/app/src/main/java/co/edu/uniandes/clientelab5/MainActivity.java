package co.edu.uniandes.clientelab5;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    public static String HOLA="HOLA";
    public static String SELECCIONE="SELECCIONE PROTOCOLO";
    public static String TCP="TCP";
    public static String UDP="UDP";
    public static String IP="10.0.0.2";

    private RadioButton radioSele;
    private RadioGroup conexiones;
    private Button  boton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        conexiones=(RadioGroup)findViewById(R.id.radioGroup);
        boton=(Button)findViewById(R.id.button);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = conexiones.getCheckedRadioButtonId();

                radioSele = (RadioButton) findViewById(id);

                new clase().execute(radioSele.getText().toString());
            }
        });
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
            Socket cliente=new Socket(IP,8180);
            PrintWriter out=new PrintWriter(cliente.getOutputStream(),true);
            BufferedReader in=new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            out.println(HOLA);
            String linea=in.readLine();
            if(linea.equals(SELECCIONE))
            {
                if(boton.equals(TCP))
                {
                    out.println(TCP);
                    cliente.close();
                    conectarTCP();
                }
                else if(boton.equals(UDP))
                {
                    out.println(UDP);
                    cliente.close();
                    conectarUDP();
                }
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void conectarTCP()
    {
        try {
            String sentence="holi";
            String modifiedSentence;
            BufferedReader inFromUser =
                    new BufferedReader(new InputStreamReader(System.in));
            Socket clientSocket = new Socket(IP, 8180);
            DataOutputStream outToServer =
                    new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer =
                    new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            sentence = inFromUser.readLine();
            outToServer.writeBytes(sentence + '\n');
            modifiedSentence = inFromServer.readLine();
            System.out.println("FROM SERVER: " + modifiedSentence);
            clientSocket.close();
        }
        catch(Exception e)
        {
            System.out.println("No hubo TCP :(");
        }
    }

    public void conectarUDP()
    {
        try{
            BufferedReader inFromUser =
                    new BufferedReader(new InputStreamReader(System.in));
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName(IP);
            byte[] sendData = new byte[1024];
            byte[] receiveData = new byte[1024];
            String sentence = inFromUser.readLine(); sendData = sentence.getBytes();
            DatagramPacket sendPacket =
                    new DatagramPacket(sendData, sendData.length, IPAddress, 8180);
            clientSocket.send(sendPacket);
            DatagramPacket receivePacket =
                    new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            String modifiedSentence =
                    new String(receivePacket.getData());
            System.out.println("FROM SERVER:" + modifiedSentence); clientSocket.close();
        }
        catch(Exception e)
        {
            System.out.println("No hubo UDP :(");
        }
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
