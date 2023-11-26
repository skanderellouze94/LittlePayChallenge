package Utils;

public class ReformatCardNumber {

  public static String reformatCardNumber(String cardNumber) {

    StringBuilder formattedNumber = new StringBuilder();
    for (int i = 0; i < cardNumber.length(); i += 4) {
      formattedNumber.append(cardNumber, i, Math.min(i + 4, cardNumber.length()));
      if (i + 4 < cardNumber.length()) {
        formattedNumber.append(" ");
      }
    }
    return formattedNumber.toString();
  }

}
