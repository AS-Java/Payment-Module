package school.attractor.payment_module.domain.util;

import school.attractor.payment_module.domain.commersant.Commersant;
import school.attractor.payment_module.domain.config.Utility;
import school.attractor.payment_module.domain.order.Order;
import school.attractor.payment_module.domain.shop.Shop;
import school.attractor.payment_module.domain.transaction.TransactionStatus;

import java.util.*;

class GenerateData {


    static List<Order> addOrdersForCommersant1(Shop shop1) {
        Random random = new Random();
        List<Order> orders = new ArrayList<>();
        List<String> userName = Arrays.asList("Артур","Бакытжан","Кирил","Вячеслав","Чингиз");
        List<String> emails = Arrays.asList("email@com", "mail@com" , "com@com");
        for (int i = 0; i < 30; i++) {
            int randomAmount = random.nextInt(80000);
            int randomOrderId = random.nextInt ( 100000 ) + 999999;
            String name = userName.get(random.nextInt(userName.size()));
            Date today = new Date();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1); // number represents number of days
            Date yesterday = cal.getTime();
            cal.add(Calendar.DATE, -2);
            Date dayBeforeYesterday = cal.getTime ();
            Order test = Order.builder()
                    .orderId (randomOrderId )
                    .shop ( shop1 )
                    .shopName(shop1.getSiteName ())
                    .userName(name)
                    .status( TransactionStatus.APPROVED)
                    .amount((randomAmount))
                    .residual(randomAmount)
                    .email("example" + emails.get(random.nextInt(emails.size())))
                    .cardHolderName(name)
                    .card(Utility.maskCardNumber("1111111111111111", "####********####" ))
                    .exp(01)
                    .exp_year(20)
                    .cvc2(202)
                    .phone("777-77-77")
                    .date( today)
                    .build();
            orders.add(test);

            if (i % 6 == 0) {
                test.setStatus( TransactionStatus.REFUSED);
                test.setDate ( dayBeforeYesterday );
            }
            if (i % 3 == 0) {
                test.setStatus( TransactionStatus.RESERVED);
                test.setDate ( yesterday );
            }
            if (i % 4 == 0) {
                test.setStatus( TransactionStatus.PARTIAL_REFUND);
            }
            if (i % 5 == 0) {
                test.setStatus( TransactionStatus.TOTAL_REFUND);
            }
        }
        return orders;
    }

    static List<Order> addOrdersForCommersant2(Shop shop1, Shop shop2) {

        Random random = new Random();
        List<Order> orders = new ArrayList<>();
        List<String> userName = Arrays.asList("Артур","Бакытжан","Кирил","Вячеслав","Чингиз");
        List<String> emails = Arrays.asList("email@com", "mail@com" , "com@com");
        for (int i = 0; i < 30; i++) {
            int randomAmount = random.nextInt(80000);
            int randomId = random.nextInt(5000);
            int randomOrderId = random.nextInt ( 100000 ) + 999999;
            String name = userName.get(random.nextInt(userName.size()));

            Order test = Order.builder()
                    .id(randomId)
                    .orderId (randomOrderId )
                    .shop ( shop1 )
                    .shopName(shop1.getSiteName ())
                    .userName(name)
                    .status( TransactionStatus.APPROVED)
                    .amount((randomAmount))
                    .residual(randomAmount)
                    .email("example" + emails.get(random.nextInt(emails.size())))
                    .cardHolderName(name)
                    .card(Utility.maskCardNumber("1111111111111111", "####********####" ))
                    .exp(01)
                    .exp_year(20)
                    .cvc2(202)
                    .date(new Date())
                    .phone("777-77-77")
                    .build();
            orders.add(test);
            if (i % 6 == 0) {
                test.setStatus( TransactionStatus.REFUSED);
            }
            if (i % 3 == 0) {
                test.setStatus( TransactionStatus.RESERVED);
                test.setShop ( shop2 );
                test.setShopName ( shop2.getSiteName () );
            }
            if (i % 4 == 0) {
                test.setStatus( TransactionStatus.PARTIAL_REFUND);
            }
            if (i % 5 == 0) {
                test.setStatus( TransactionStatus.TOTAL_REFUND);
            }
        }
        return orders;
    }
}
