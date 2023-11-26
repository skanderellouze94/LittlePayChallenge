package Client;

import DTO.Message;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClientHandler implements Runnable {

  private final Socket clientSocket;

  public ClientHandler(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  @Override
  public void run() {
    try (
        BufferedReader in = new BufferedReader(
            new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(
            clientSocket.getOutputStream(), true)
    ) {
      String inputLine;

      while ((inputLine = in.readLine()) != null) {

        List<Message> messages = parseTransmition(inputLine);

        for (Message message : messages) {
          out.println("Kernel: " + message.getKernel());
          out.println("Card number: " + message.getCardNumber());
          out.println("Amount: " + message.getAmount());
          out.println("Currency: " + message.getCurrency());
        }
        System.out.println(messages.size());
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        clientSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  List<Message> parseTransmition(String receivedData) {
    receivedData = receivedData.toUpperCase(Locale.ROOT);
    List<Message> messages = new ArrayList<>();
    Message message = new Message();
    int index = 0;
    if (receivedData.substring(0, 7).lastIndexOf("02") == 4) {
      index = 4;
    } else {
      index = 5;
    }
    String tagTwoByte = "";
    while (index < receivedData.length()) {

      String tagOneByte = receivedData.substring(index, index + 2);
      if (receivedData.length() > index + 4) {
        tagTwoByte = receivedData.substring(index, index + 4);
        index = manageTwoByteTag(tagTwoByte, receivedData, index, message);
      }
      index = manageOneByteTag(tagOneByte, receivedData, index, messages, message);

    }

    return messages;
  }

  private int processStartTag(int index, Message message) {
    return index + 2;
  }

  private int processEndTag(int index, List<Message> messages, Message message) {
    messages.add(new Message(message.getKernel(), message.getCardNumber(), message.getAmount(),
        message.getCurrency()));
    return index + 2;
  }

  private int manageOneByteTag(String tagOneByte, String receivedData, int index,
      List<Message> messages, Message message) {
    switch (tagOneByte) {
      case "02":
        return processStartTag(index,message);
      case "03":
        return processEndTag(index, messages, message);
      case "5A":
        int valueLength = Integer.parseInt(receivedData.substring(index + 2, index + 4), 16) * 2;
        message.setCardNumber(receivedData.substring(index + 4, index + 4 + valueLength));
        return index + 4 + valueLength;
      default:
        return index + 2;
    }
  }

  public int manageTwoByteTag(String tagTwoByte, String receivedData, int index, Message message) {
    int valueLength = Integer.parseInt(receivedData.substring(index + 4, index + 6), 16) * 2;
    switch (tagTwoByte) {
      case "9F2A":
        message.setKernel(receivedData.substring(index + 6, index + 6 + valueLength));
        return index + 4 + valueLength;
      case "9F02":
        message.setAmount(
            Double.parseDouble(receivedData.substring(index + 6, index + 6 + valueLength)) / 100);
        return index + 4 + valueLength;
      case "5F2A":
        message.setCurrency(receivedData.substring(index + 6, index + 6 + valueLength));
        return index + 4 + valueLength;
      case "9F03":
        return index + 4 + valueLength;

      default:
        return index;
    }
  }


}
