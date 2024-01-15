public interface PaymentStrategy {
    double calculateTransactionFee(double totalAmount);
}