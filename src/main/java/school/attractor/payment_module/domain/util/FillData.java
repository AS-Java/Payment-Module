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

import java.util.List;

@Configuration
public class FillData {

    @Bean
    public CommandLineRunner fill(PasswordEncoder encoder, CommersantRepository commersantRepository,
                                  ShopRepository shopRepository, OrderRepository orderRepository) {
        return (args) -> {
            Commersant commersant1 = GenerateData.addCommersant1(encoder);
            Commersant commersant2 = GenerateData.addCommersant2(encoder);

            Shop testShop = GenerateData.addShopForCommersant1(commersant1);
            List<Shop> shops = GenerateData.addShopsForCommersant2(commersant2);

            commersantRepository.save (commersant1 );
            commersantRepository.save (commersant2);

            shopRepository.save( testShop );
            shopRepository.saveAll(shops);

//            orderRepository.saveAll( GenerateData.addOrdersForCommersant1(testShop));
            orderRepository.saveAll ( GenerateData.addOrdersForCommersant2 ( shops));

        };
    }
}
