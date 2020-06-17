package school.attractor.payment_module.domain.shop;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.attractor.payment_module.domain.commersant.BusinessActivity;
import school.attractor.payment_module.domain.commersant.Commersant;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopDTO {

    private int id;
   private Commersant commersant;
    private BusinessActivity activity;

    @NotBlank(message = "Введите название сайта")
    private String siteName;

    @Email
    @NotBlank(message = "Введите email адрес")
    private String registerEmail;

    @NotBlank(message = "Введите счет аккаунта")
    @Size(min = 20, max = 20, message = "Неверное формат счета ( 20 значный код )")
    private String account;

    @NotBlank(message = "Введите ваш индекс города")
    @Size(min = 7, max = 7, message = "Вы ввели неправильный формат индекс города - (A55A5A5)")
    private String index;

    @NotBlank(message = "Введите регион")
    private String region;

    @NotBlank(message = "Введите название населенного пункта")
    private String locality;

    @NotBlank(message = "Введите существующую улицу")
    private String street;

    private String building;

    @NotBlank(message = "Введите корпус строения (1А, Б9)")
    private String house;

    private String office;

    @NotBlank(message = "Введите номер покупателя")
    @Pattern(regexp="^(0|[1-9][0-9]*)$", message = "Вы ввели неправильные номер телефона, например +77071112223")
    @Size(min = 11, max = 11, message = "Вы ввели неправильное количество цифр")
    private String phoneForCustomer;

    @Email
    @NotBlank(message = "Введите email адрес")
    private String emailForCustomer;

    @Email
    @NotBlank(message = "Введите email адрес")
    private String contactEmail;

    @NotBlank(message = "Введите название сайта")
    private String contactName;

    @NotBlank(message = "Введите номер телефона")
    @Pattern(regexp="^(0|[1-9][0-9]*)$", message = "Вы ввели неправильные номер телефона, например +77071112223")
    @Size(min = 11, max = 11, message = "Вы ввели неправильное количество цифр")
    private String contactPhone;

    public static ShopDTO from(Shop shop) {
     return ShopDTO.builder()
            .commersant(shop.getCommersant())
            .activity(shop.getActivity())
            .siteName(shop.getSiteName())
            .registerEmail(shop.getRegisterEmail())
            .account(shop.getAccount())
            .index(shop.getIndex())
            .region(shop.getRegion())
            .locality(shop.getLocality())
            .street(shop.getStreet())
            .building(shop.getBuilding())
            .house(shop.getHouse())
            .office(shop.getOffice())
            .phoneForCustomer(shop.getPhoneForCustomer())
            .emailForCustomer(shop.getEmailForCustomer())
            .contactEmail(shop.getContactEmail())
            .contactName(shop.getContactName())
            .contactPhone(shop.getContactPhone())
            .build();
    }
}
