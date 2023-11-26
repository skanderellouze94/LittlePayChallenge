package Utils;

public class HexConverterUtil {

  public static String toHex(byte[] bytes) {
    StringBuilder hexStringBuilder = new StringBuilder();
    for (byte b : bytes) {
      hexStringBuilder.append(String.format("%02X", b));
    }
    return hexStringBuilder.toString();
  }
  public static String cleanHex(String hex) {
    hex = hex.replaceAll("EFBFBD", "9F");
    hex = hex.toLowerCase();
    return hex;
  }
}
