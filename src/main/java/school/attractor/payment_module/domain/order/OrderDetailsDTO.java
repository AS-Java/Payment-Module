package school.attractor.payment_module.domain.order;

import lombok.*;
import school.attractor.payment_module.domain.config.Utility;
import school.attractor.payment_module.domain.shop.Shop;
import school.attractor.payment_module.domain.shop.ShopDTO;
import school.attractor.payment_module.domain.transaction.TransactionStatus;
import school.attractor.payment_module.domain.transaction.TransactionType;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailsDTO {

    private int id;

    private String shopName;

    private String userName;

    private String email;

    private String phone;

    private Date date;

    private String cardHolderName;

    private String card;

    private int amount;

    private int residual;

    private TransactionStatus status;

    private TransactionType type;

    private Integer orderId;

    private List<OrderTransactionList> transactions;


    public static OrderDetailsDTO from(Order order) {
        return OrderDetailsDTO.builder()
                .id(order.getId())
                .orderId(order.getOrderId())
                .userName(order.getUserName())
                .email(order.getEmail())
                .shopName(order.getShop().getSiteName())
                .date(order.getDate())
                .cardHolderName(order.getCardHolderName())
                .card(order.getCard())
                .amount(order.getAmount())
                .phone(order.getPhone())
                .residual(order.getResidual())
                .status(order.getStatus())
                .transactions(order.getTransactions().stream().map(OrderTransactionList::from).collect(Collectors.toList()))
                .build();
    }

    public static OrderDetailsDTO from(OrderDTO order) {
        return OrderDetailsDTO.builder()
                .id(order.getId())
                .orderId(order.getOrderId())
                .userName(order.getUserName())
                .shopName(order.getShopName())
                .email(order.getEmail())
                .date(order.getDate())
                .cardHolderName(order.getCardHolderName())
                .card( Utility.maskCardNumber(order.getCard(),"####********####"))
                .amount(order.getAmount())
                .residual(order.getResidual())
                .phone(order.getPhone())
                .status(order.getStatus())
                .transactions(order.getTransactions().stream().map(OrderTransactionList::from).collect(Collectors.toList()))
                .build();
    }


}
