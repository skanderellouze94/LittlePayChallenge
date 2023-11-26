package Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import Client.ClientHandler;
import DTO.Message;
import java.util.List;
import org.junit.Test;

public class ClientHandlerTest {

  @Test
  public void testParseTransmission() throws Exception {
    ClientHandler clientHandler = new ClientHandler(null);

    // Test case 1: Test with a one message
    String input1 = "001B02189F2A01029F020201005A0841111111111111115F2A02097803";
    List<Message> result1 = clientHandler.parseTransmission(input1);
    assertEquals(1, result1.size());
    assertEquals("02", result1.get(0).getKernel());
    assertEquals("4111111111111111", result1.get(0).getCardNumber());
    assertEquals(1.0, result1.get(0).getAmount(), 0.0);
    assertEquals("0978", result1.get(0).getCurrency());

    // Test case 2: Test with multiple messages
    String input2 = "004202189F2A01029F020201005A0841111111111111115F2A0209780302249F2A080"
        + "4000000000000005F2A0208269F02031234565A08379F9F246310005F9F03010003";
    List<Message> result2 = clientHandler.parseTransmission(input2);
    assertEquals(2, result2.size());
    assertEquals("02", result2.get(0).getKernel());
    assertEquals("4111111111111111", result2.get(0).getCardNumber());
    assertEquals(1.0, result2.get(0).getAmount(), 0.0);
    assertEquals("0978", result2.get(0).getCurrency());

    assertEquals("0400000000000000", result2.get(1).getKernel());
    assertEquals("379F9F246310005F", result2.get(1).getCardNumber());
    assertEquals(1234.56, result2.get(1).getAmount(), 0.0);
    assertEquals("0826", result2.get(1).getCurrency());

    // Test case for an empty input
    assertThrows(Exception.class, () -> {
      clientHandler.parseTransmission(null);
    });

    // Test case for invalid input
    assertThrows(Exception.class, () -> {
      clientHandler.parseTransmission("ABsCGJ8qAQKfAgIBAFoIQRERERERERFfKgIJeAM=");
    });

  }
}
