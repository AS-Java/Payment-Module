package school.attractor.payment_module.domain.registries;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import school.attractor.payment_module.domain.commersant.Commersant;
import school.attractor.payment_module.domain.commersant.CommersantRepository;
import school.attractor.payment_module.domain.order.Order;
import school.attractor.payment_module.domain.order.OrderRepository;
import school.attractor.payment_module.domain.shop.Shop;
import school.attractor.payment_module.domain.shop.ShopRepository;

import java.io.FileWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.time.DateUtils.MILLIS_PER_DAY;
import static org.apache.commons.lang3.time.DateUtils.MILLIS_PER_MINUTE;


@Configuration
@EnableScheduling
@AllArgsConstructor
public class CSVFile {
    private final CommersantRepository commersantRepository;
    private final ShopRepository shopRepository;
    private final OrderRepository orderRepository;

    @Scheduled(cron = "0 0 23 * * 1-5")
//    @Scheduled(fixedRate = MILLIS_PER_MINUTE)
//    second, minute, hour, day of month, month, day(s) of week
    public void creatCSVFile() throws Exception{
        List<Commersant> commersants = commersantRepository.findAll ( );
        LocalDate  today = LocalDate.now ();
        LocalDate yesterday = LocalDate.now ().minusDays ( 1 );
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.YYYY");

        String csvFile = "csv/registryforBank_" + today.toString ()+".csv";
        FileWriter writer = new FileWriter(csvFile);
        String  account = "KZ678560000005917533";

            for (Commersant commersant : commersants) {
                List<Shop> shops = shopRepository.findAllByCommersant ( commersant );
                List<Order> allOrders = orderRepository.findAllByShopIn (shops);
                int amount  =  allOrders.stream ()
                        .map( Order::getResidual )
                        .collect(Collectors.toList()).stream()
                        .mapToInt(Integer::intValue)
                        .sum();
                List<String> list = new ArrayList<>();
                list.add(formatter.format(today));
                list.add(commersant.getBik ());
                list.add(account);
                list.add(commersant.getBin ());
                list.add( Integer.toString ( amount ));
                list.add(commersant.getName());
                list.add("возмещение коммерсанту на сумму " + Integer.toString ( amount ) + " за "+ formatter.format(yesterday));
                list.add(commersant.getKbe ());
                list.add("852");
                list.add("KZ47826A0KZT0T006904");
                CSVUtils.writeLine(writer, list, ';');
            }
            writer.flush();
            writer.close();

        }

    }



