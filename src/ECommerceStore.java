import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ECommerceStore implements Runnable{
    private static final double SHIRT_COST_FOR_COMPANY = 14.0;
    private static final double DESIGN_COST = 2.0;
    private static final double HOODIE_COST = 3.0;
    private static double totalRevenue = 0.0;
    private static double totalProfit = 0.0;
    private final String shirtSize;
    private final Boolean withDesign;
    private final Boolean withHoodie;
    private final PaymentStrategy paymentStrategy;
    private static final Map<String, Double> profitPerSize = new ConcurrentHashMap<>();

    public ECommerceStore(String shirtSize, Boolean withDesign,
                          Boolean withHoodie, PaymentStrategy paymentStrategy) {

        this.shirtSize = shirtSize;
        this.withDesign = withDesign;
        this.withHoodie = withHoodie;
        this.paymentStrategy = paymentStrategy;
    }

    private double applyTransactionFee(double amount) {
        double transactionFee = paymentStrategy.calculateTransactionFee(amount);
        return amount - transactionFee;
    }

    private double calculateShirtPrice() {
        double shirtPrice = 40.0; // Base price for a shirt that customer pays

        if (withDesign) {
            shirtPrice += DESIGN_COST;
        }

        if (withHoodie) {
            shirtPrice += HOODIE_COST;
        }

        return shirtPrice;
    }

    private synchronized static void updateTotals(double totalAmount, double shirtPrice) {
        totalRevenue += shirtPrice;
        totalProfit += totalAmount - SHIRT_COST_FOR_COMPANY;
    }

    private synchronized static void updateProfitPerSize(String shirtSize, double totalAmount) {
        profitPerSize.putIfAbsent(shirtSize, 0.0);
        profitPerSize.computeIfPresent(shirtSize, (key, oldValue) -> oldValue + (totalAmount - SHIRT_COST_FOR_COMPANY));
    }

    public static double getTotalProfit() {
        return totalProfit;
    }

    public static double getTotalRevenue() {
        return totalRevenue;
    }

    public static Map<String, Double> getProfitPerSize() {
        return profitPerSize;
    }

    @Override
    public void run() {
        double shirtPrice = calculateShirtPrice();
        double totalAmount = applyTransactionFee(shirtPrice);

        updateTotals(totalAmount, shirtPrice);
        updateProfitPerSize(shirtSize, totalAmount);
    }
}
