public class DefaultPaymentStrategy implements PaymentStrategy{
    @Override
    public double calculateTransactionFee(double totalAmount) {
        return totalAmount * 0.1;
    }
}
