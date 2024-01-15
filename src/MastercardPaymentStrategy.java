public class MastercardPaymentStrategy implements PaymentStrategy{
    @Override
    public double calculateTransactionFee(double totalAmount) {
        return totalAmount * 0.03;
    }
}
