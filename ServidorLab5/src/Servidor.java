import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

public class Servidor 
{	
	public static String HOLA="HOLA";
	public static String SELECCIONE="SELECCIONE PROTOCOLO";
	public static String TCP="TCP";
	public static String UDP="UDP";
	
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
				s.close();
				ServidorTCP.main(socket,i);
			}
			else if(linea.equals(UDP))
			{
				System.out.println("Protocolo UDP");
				s.close();
				ServidorUDP.main(i);
			}

			i++;
		}
	}
}
