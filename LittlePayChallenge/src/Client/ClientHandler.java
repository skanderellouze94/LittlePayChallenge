package Client;


import static Utils.DataMapper.getCurrencyName;
import static Utils.HexConverterUtil.cleanHex;
import static Utils.HexConverterUtil.toHex;
import static Utils.DataMapper.getKernelName;
import static Utils.ReformatCardNumber.reformatCardNumber;

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

        String Transmission = cleanHex(toHex(inputLine.getBytes()));

        List<Message> messages = parseTransmission(Transmission);

        for (Message message : messages) {
          out.println("Kernel: " + message.getKernel());
          out.println("Card number: " + message.getCardNumber());
          out.println("Amount: " + message.getAmount());
          out.println("Currency: " + message.getCurrency());
          out.println();
        }
      }

    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      try {
        clientSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public List<Message> parseTransmission(String receivedData) throws Exception {
    if (receivedData != null) {
      receivedData = receivedData.toUpperCase(Locale.ROOT);
    } else {
      throw new Exception("Transmission data should not be null or empty");
    }
    receivedData = receivedData.toUpperCase(Locale.ROOT);
    List<Message> messages = new ArrayList<>();
    Message message = new Message();

    int index =
        receivedData.length() >= 7 && receivedData.substring(0, 7).lastIndexOf("02") == 4 ? 4 : 5;
    int transmissionSize = Integer.parseInt(receivedData.substring(0, index), 16) * 2;

    if (transmissionSize != receivedData.length() - index) {
      throw new Exception("Transmission is inaccurate");
    }

    while (index < receivedData.length()) {
      if (receivedData.length() >= 2 + index) {
        String tagOneByte = receivedData.substring(index, index + 2);
        index = manageOneByteTag(tagOneByte, receivedData, index, messages, message);
      }
      if (receivedData.length() >= index + 4) {
        String tagTwoByte = receivedData.substring(index, index + 4);
        index = manageTwoByteTag(tagTwoByte, receivedData, index, message);
      }
      if ((receivedData.length() == 1 - index) || (receivedData.length() == 1 + index)) {
        index++;
      }
    }
    return messages;
  }

  private int processStartTag(int index) {
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
        return processStartTag(index);
      case "03":
        return processEndTag(index, messages, message);
      case "5A":
        int valueLength = Integer.parseInt(receivedData.substring(index + 2, index + 4), 16) * 2;
        message.setCardNumber(reformatCardNumber(receivedData.substring(index + 4, index + 4 + valueLength)));
        return index + 4 + valueLength;
      default:
        return index + 2;
    }
  }

  public int manageTwoByteTag(String tagTwoByte, String receivedData, int index, Message message) {
    int valueLength = 0;
    if (receivedData.length() > index + 6) {
      valueLength = Integer.parseInt(receivedData.substring(index + 4, index + 6), 16) * 2;
    }

    switch (tagTwoByte) {
      case "9F2A":
        message.setKernel(getKernelName(receivedData.substring(index + 6, index + 6 + valueLength)));
        return index + 4 + valueLength;
      case "9F02":
        message.setAmount(
            Double.parseDouble(receivedData.substring(index + 6, index + 6 + valueLength)) / 100);
        return index + 4 + valueLength;
      case "5F2A":
        message.setCurrency(getCurrencyName(receivedData.substring(index + 6, index + 6 + valueLength)));
        return index + 4 + valueLength;
      case "9F03":
        return index + 4 + valueLength;
      default:
        return index;
    }
  }
}
