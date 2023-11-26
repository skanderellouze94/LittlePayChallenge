package DTO;

public class Message {
  private String kernel;
  private String cardNumber;
  private double amount;
  private String currency;

  public String getKernel() {
    return kernel;
  }

  public void setKernel(String kernel) {
    this.kernel = kernel;
  }

  public String getCardNumber() {
    return cardNumber;
  }

  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public Message(String kernel, String cardNumber, double amount, String currency) {
    this.kernel = kernel;
    this.cardNumber = cardNumber;
    this.amount = amount;
    this.currency = currency;
  }

  public Message() {
  }
}
