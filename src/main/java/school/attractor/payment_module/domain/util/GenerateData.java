package school.attractor.payment_module.domain.util;

import org.springframework.security.crypto.password.PasswordEncoder;
import school.attractor.payment_module.domain.commersant.BusinessActivity;
import school.attractor.payment_module.domain.commersant.Commersant;
import school.attractor.payment_module.domain.config.Utility;
import school.attractor.payment_module.domain.order.Order;
import school.attractor.payment_module.domain.shop.Shop;
import school.attractor.payment_module.domain.transaction.TransactionStatus;

import java.util.*;

class GenerateData {

    static  Commersant addCommersant1(PasswordEncoder encoder) {
        return  Commersant.builder ( )
                .email ( "commersant1@mail" )
                .password ( encoder.encode ( "12345678" ) )
                .directorIdentityCard("90909090000")
                .name("Attractor School")
                .bin("990990000099")
                .bik("KZKZKZKZ")
                .kbe("17")
                .build ();
    }

    static  Commersant addCommersant2(PasswordEncoder encoder) {
        return  Commersant.builder ( )
                .email ( "commersant2@mail" )
                .password ( encoder.encode ( "12345678" ) )
                .directorIdentityCard("60606060000")
                .name("Alibaba Group")
                .bin("1222222222")
                .bik("ABCDABCD")
                .kbe("19")
                .build ( );
    }

    static  Shop addShopForCommersant1(Commersant commersant) {
        return Shop.builder ()
                .siteName ( "www.attractor-shop.kz" )
                .phoneForCustomer("777-777-77-77")
                .emailForCustomer("attractor-school@com")
                .contactPhone("77-77-77")
                .contactName("Attractor Almaty")
                .contactEmail("attractor-school@com")
                .registerEmail("attractor-school@com")
                .locality("Казахстан")
                .region("Алматы")
                .street("Центральная")
                .house("100")
                .index("005022")
                .building("Корпус - B")
                .office("Офис - 2")
                .commersant ( commersant )
                .activity ( BusinessActivity.SALE_OF_CLOTHING )
                .build ( );
    }

    static List<Shop> addShopsForCommersant2(Commersant commersant) {
        List<Shop> shops = new ArrayList<>();

        Shop aliexpress = Shop.builder ( )
                .siteName ( "www.aliexpress.com" )
                .commersant ( commersant )
                .phoneForCustomer("777-777-77-77")
                .emailForCustomer("aliexpress@example.com")
                .contactPhone("55555")
                .contactName("Aliexpress Group")
                .contactEmail("aliexpress@example.com")
                .registerEmail("aliexpress@example.com")
                .locality("Китай")
                .region("Китай")
                .street("Китай")
                .house("1232311")
                .index("00121115022")
                .activity ( BusinessActivity.SALE_OF_CLOTHING )
                .build ( );

        Shop taobao = Shop.builder ( )
                .siteName ( "www.taobao.kz" )
                .commersant ( commersant )
                .phoneForCustomer("4444")
                .emailForCustomer("taobaol@com")
                .contactPhone("4444")
                .contactName("Taobao Taobao")
                .contactEmail("example@mail.com")
                .registerEmail("example@mail.com")
                .locality("Китай")
                .region("Китай")
                .street("Китай")
                .house("120")
                .index("00225022")
                .activity ( BusinessActivity.SALE_OF_CLOTHING )
                .build ( );
        shops.add(aliexpress);
        shops.add(taobao);
        return shops;
    }

    static List<Order> addOrdersForCommersant1(Shop shop) {

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
                    .shop ( shop )
                    .shopName(shop.getSiteName ())
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

    static List<Order> addOrdersForCommersant2(List<Shop> shops) {
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
                    .shop ( shops.get(0) )
                    .shopName(shops.get(0).getSiteName ())
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
                test.setShop ( shops.get(1) );
                test.setShopName ( shops.get(1).getSiteName () );
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
