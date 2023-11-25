import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

  public static void main(String[] args) {
    final int portNumber = 8888;

    try {
      ServerSocket serverSocket = new ServerSocket(portNumber);
      System.out.println("Server listening on port " + portNumber);

      while (true) {
        Socket clientSocket = serverSocket.accept();
        System.out.println("Client connected: " + clientSocket.toString());

      }
    }catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}