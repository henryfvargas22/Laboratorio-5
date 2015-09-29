import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.*;
import java.util.Properties;

public class Servidor 
{	
	public static String HOLA="HOLA";
	public static String SELECCIONE="SELECCIONE PROTOCOLO";
	public static String TCP="TCP";
	public static String UDP="UDP";

	private static String csv;

	public static void main(String argv[]) throws Exception
	{
		ServerSocket socket = new ServerSocket(8180);
		int i = 1;
		System.out.println("El servidor esta listo para aceptar conexiones.");
		while (true)
		{
			Socket s = socket.accept();
			PrintWriter writer = new PrintWriter(s.getOutputStream(), true);
			BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
			String linea = reader.readLine();

			if (linea.equals(HOLA)) 
			{
				writer.println(SELECCIONE);
			}
			linea = reader.readLine();

			if(linea.equals(TCP))
			{
				System.out.println("Protocolo TCP");
				System.out.println(s.getInetAddress());
				String lineaCliente=reader.readLine();
				System.out.println(lineaCliente);
				csv += "Thread No. "+i+" Protocolo: "+TCP+" "+lineaCliente+"."+"\n";
				System.out.println("CSV: "+csv);
				guardar();
				s.close();
			}
			else if(linea.equals(UDP))
			{
				System.out.println("Protocolo UDP");
				System.out.println(s.getInetAddress());
				s.close();
				DatagramSocket serverSocket = new DatagramSocket(8180);
				byte[] receiveData = new byte[1024]; 
				byte[] sendData = new byte[1024];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				String sentence = new String(receivePacket.getData());
				System.out.println(sentence);
				InetAddress IPAddress = receivePacket.getAddress(); 
				int port = receivePacket.getPort();
				String capitalizedSentence = sentence.toUpperCase();
				sendData = capitalizedSentence.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
				serverSocket.send(sendPacket);
				csv += "Thread No. "+i+" Protocolo: "+UDP+" Mensaje: "+sentence+"."+"\n";
				serverSocket.close();
				guardar();
				System.out.println("CSV: "+csv);
			}
			i++;
		}

	}
	public static  void guardar(){
		String fileName = "./csv.txt";
		try 
		{
			FileWriter fileWriter = new FileWriter(fileName);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(csv);
			bufferedWriter.close();
		}
		
		catch(IOException ex) 
		{
			System.out.println("Error writing to file '"+ fileName + "'");
			ex.printStackTrace();
		}

		try {
			Properties props = new Properties();
			File f = new File("./resultados.properties");
			OutputStream out = new FileOutputStream( f );
			props.store(out, "Indicadores");
		}
		catch (Exception e ) {
			e.printStackTrace();
		}
	}
}
