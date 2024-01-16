import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ECommerceStore implements Runnable{
    private static final double SHIRT_COST_FOR_COMPANY = 14.0;
    private static final double DESIGN_COST = 2.0;
    private static final double HOODIE_COST = 3.0;
    private static final double SHIRT_PRICE = 40.0;
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

    private double applyTransactionFee() {
        return paymentStrategy.calculateTransactionFee(ECommerceStore.SHIRT_PRICE);
    }

    private double calculateProfitPrice() {
        double profitForCompany = SHIRT_PRICE - SHIRT_COST_FOR_COMPANY;

        if (withDesign) {
            profitForCompany -= DESIGN_COST;
        }

        if (withHoodie) {
            profitForCompany -= HOODIE_COST;
        }

        profitForCompany -= applyTransactionFee();

        return profitForCompany;
    }

    private synchronized static void updateTotals(double shirtPriceForCompany) {
        totalRevenue += SHIRT_PRICE;
        totalProfit += shirtPriceForCompany;
    }

    private synchronized static void updateProfitPerSize(String shirtSize, double totalAmount) {
        profitPerSize.putIfAbsent(shirtSize, 0.0);
        profitPerSize.computeIfPresent(shirtSize, (key, oldValue) -> oldValue + (totalAmount));
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
        double profitForCompany = calculateProfitPrice();

        updateTotals(profitForCompany);
        updateProfitPerSize(shirtSize, profitForCompany);
    }
}
