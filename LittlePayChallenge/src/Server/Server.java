package Server;

import Client.ClientHandler;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

  private final int portNumber;

  public Server(int portNumber) {
    this.portNumber = portNumber;
  }

  public void startServer(){
    try {
      ServerSocket serverSocket = new ServerSocket(portNumber);
      System.out.println("Server listening on port " + portNumber);

      while (true) {
        Socket clientSocket = serverSocket.accept();
        System.out.println("Client connected: " + clientSocket.toString());

        ClientHandler clientHandler = new ClientHandler(clientSocket);
        new Thread(clientHandler).start();
      }
    }catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
