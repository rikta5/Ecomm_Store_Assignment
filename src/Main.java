import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        ArrayList<ECommerceStore> customers = new ArrayList<>();
        fillEcommerceStore(customers);

        ArrayList<Thread> threads = new ArrayList<>();
        for (ECommerceStore customer : customers) {
            Thread t = new Thread(customer);
            threads.add(t);
            t.start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }

        totalRevenueReport();
        totalProfitReport();
        profitPerShirtSizeReport();
    }

    private static void fillEcommerceStore(ArrayList<ECommerceStore> customers) throws FileNotFoundException {
        Map<String, PaymentStrategy> paymentStrategyMap = new HashMap<>();
        paymentStrategyMap.put("wallet", new WalletPaymentStrategy());
        paymentStrategyMap.put("bankcard", new BankcardPaymentStrategy());
        paymentStrategyMap.put("visa", new VisaPaymentStrategy());
        paymentStrategyMap.put("mastercard", new MastercardPaymentStrategy());

        File f = new File("src/customer_orders.csv");
        Scanner scanner = new Scanner(f);
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] values = line.split(",");
            String shirtSize = values[1];
            Boolean withDesign = Boolean.parseBoolean(values[2]);
            Boolean withHoodie = Boolean.parseBoolean(values[3]);
            String paymentMethod = values[4];
            PaymentStrategy paymentStrategy = paymentStrategyMap.getOrDefault(paymentMethod, new DefaultPaymentStrategy());

            customers.add(new ECommerceStore(shirtSize, withDesign, withHoodie, paymentStrategy));
        }
    }

    private static void totalRevenueReport() {
        FileWriter fw;
        try {
            fw = new FileWriter("total_revenue_report.txt");
            double totalRevenue = ECommerceStore.getTotalRevenue();
            totalRevenue = Math.round(totalRevenue * 100.0) / 100.0;
            fw.write("Total revenue: " + totalRevenue);
            fw.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void totalProfitReport() {
        FileWriter fw;
        try {
            fw = new FileWriter("total_profit_report.txt");
            double totalProfit = ECommerceStore.getTotalProfit();
            totalProfit = Math.round(totalProfit * 100.0) / 100.0;
            fw.write("Total profit: " + totalProfit);
            fw.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void profitPerShirtSizeReport() {
        FileWriter fw;
        try {
            fw = new FileWriter("profit_per_shirt_size_report.txt");
            fw.write("Shirt size" + " - " + "Profit\n");
            for (Map.Entry<String, Double> entry : ECommerceStore.getProfitPerSize().entrySet()) {
                entry.setValue(Math.round(entry.getValue() * 100.0) / 100.0);
                fw.write(entry.getKey() + " - " + entry.getValue() + "\n");
            }
            fw.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}