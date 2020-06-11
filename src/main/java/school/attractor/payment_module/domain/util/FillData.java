package school.attractor.payment_module.domain.util;


import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import school.attractor.payment_module.domain.commersant.BusinessActivity;
import school.attractor.payment_module.domain.commersant.Commersant;
import school.attractor.payment_module.domain.commersant.CommersantRepository;
import school.attractor.payment_module.domain.order.OrderRepository;
import school.attractor.payment_module.domain.registries.CSVFile;
import school.attractor.payment_module.domain.shop.Shop;
import school.attractor.payment_module.domain.shop.ShopRepository;

@Configuration
public class FillData {

    @Bean
    public CommandLineRunner fill(PasswordEncoder encoder, CommersantRepository commersantRepository,
                                  ShopRepository shopRepository, OrderRepository orderRepository) {
        return (args) -> {
            Commersant commersant1 = Commersant.builder ( )
                    .email ( "commersant1@mail" )
                    .password ( encoder.encode ( "12345678" ) )
                    .directorIdentityCard("90909090000")
                    .name("Attractor School")
                    .bin("990990000099")
                    .bik("KZKZKZKZ")
                    .kbe("17")
                    .build ();

            Commersant commersant2 = Commersant.builder ( )
                    .email ( "commersant2@mail" )
                    .password ( encoder.encode ( "12345678" ) )
                    .directorIdentityCard("60606060000")
                    .name("Alibaba Group")
                    .bin("1222222222")
                    .bik("ABCDABCD")
                    .kbe("19")
                    .build ( );

            commersantRepository.save (commersant2);
            commersantRepository.save (commersant1 );

            Shop testShop = Shop.builder ()
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
                    .commersant ( commersant1 )
                    .activity ( BusinessActivity.SALE_OF_CLOTHING )
                    .build ( );

            Shop aliexpress = Shop.builder ( )
                    .siteName ( "www.aliexpress.com" )
                    .commersant ( commersant2 )
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
                    .commersant ( commersant2 )
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

            shopRepository.save( testShop );
            shopRepository.save ( taobao );
            shopRepository.save( aliexpress );
            orderRepository.saveAll( GenerateData.addOrdersForCommersant1(testShop));
            orderRepository.saveAll ( GenerateData.addOrdersForCommersant2 ( aliexpress,taobao));

            CSVFile csvFile = new CSVFile();
            csvFile.creatCSVFile(commersantRepository);
        };
    }
}
