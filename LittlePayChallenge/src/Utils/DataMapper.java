package Utils;

public class DataMapper {

  public static String getKernelName(String kernel) {
    switch (kernel) {
      case "02":
        return "American Express";
      case "0400000000000000":
        return "VISA";
      default:
        return kernel;
    }
  }

  public static String getCurrencyName(String currency) {
    switch (currency) {
      case "0978":
        return "EUR";
      case "0840":
        return "USD";
      case "0826":
        return "GBP";
      default:
        return currency;
    }
  }

}
