package school.attractor.payment_module.domain.order;

import lombok.*;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.Range;
import school.attractor.payment_module.domain.shop.Shop;

import javax.validation.constraints.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewOrderDetails {

    @NotNull
    @Positive
    private Integer orderId;

    @Positive
    @Range(min =  1, max = 10000000, message = "сумма должна быть больше 1")
    private int amount;


    @Email
    @NotBlank
    private String email;

    @NotBlank
    @NotEmpty(message = "имя не должно быть пустым")
    private String userName;


    @NotNull(message = "shop id не заполнен")
    private Integer shopId;

    @NotBlank
    @NotEmpty
    private String phone;

    @NotNull
    @CreditCardNumber
    private String card;

    @NotBlank
    @NotEmpty
    @NotNull
    @Size(min = 2, max = 20, message = "Имя должно быть больше 2 символов и меньше 20")
    private String cardHolderName;

    @NotNull
    @Positive(message = "CVC код  не может быть отрицательным")
    private Integer cvc2;

    @NotNull
    @Positive(message = "дата не может быть отрицательным числом")
    private Integer exp;

    @NotNull
    private Integer exp_year;

}
