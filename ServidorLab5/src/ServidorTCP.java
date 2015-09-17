import java.io.*;
import java.net.*;

public class ServidorTCP
{

	public static void main(ServerSocket socket,int i) throws Exception
	{
		String clientSentence;
		String capitalizedSentence;
		ServerSocket welcomeSocket = socket;
		while(true) 
		{
			Socket connectionSocket = welcomeSocket.accept();
			BufferedReader inFromClient =
					new BufferedReader(new
							InputStreamReader(connectionSocket.getInputStream())); 
		DataOutputStream outToClient =
				new DataOutputStream(connectionSocket.getOutputStream());
				clientSentence = inFromClient.readLine();
				capitalizedSentence = clientSentence.toUpperCase() + '\n';
				outToClient.writeBytes(capitalizedSentence);
		}
	}
}
