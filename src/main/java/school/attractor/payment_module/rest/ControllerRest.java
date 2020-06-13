package school.attractor.payment_module.rest;


import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import school.attractor.payment_module.domain.ApacheHttp.ResponseService;
import school.attractor.payment_module.domain.exception.OrderNotFound;
import school.attractor.payment_module.domain.order.Order;
import school.attractor.payment_module.domain.order.OrderDTO;
import school.attractor.payment_module.domain.order.OrderDetailsDTO;
import school.attractor.payment_module.domain.order.OrderService;
import school.attractor.payment_module.domain.shop.Shop;
import school.attractor.payment_module.domain.shop.ShopService;
import school.attractor.payment_module.domain.order.NewOrderDetails;
import school.attractor.payment_module.domain.transaction.Transaction;
import school.attractor.payment_module.domain.transaction.TransactionService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;


@CrossOrigin
@AllArgsConstructor
@RestController
public class ControllerRest {

    private final OrderService orderService;
    private final ShopService shopService;
    private final TransactionService transactionService;
    private final ResponseService responseService;

    @PostMapping("/pay")
    public ResponseEntity<String> mainController(@Valid @RequestBody NewOrderDetails newOrderDetails,
                                                 HttpServletResponse response, BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            List<ObjectError> errors = result.getAllErrors();
            for (ObjectError e : errors) {
                errorMessage.append("ERROR: ").append(e.getDefaultMessage());
            }
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        } else {
            Shop shop = shopService.getShop(newOrderDetails.getShopId());
//            Order order = Order.from(newOrderDetails, shop);
//            order.setDate(new Date());
//            orderService.save(order);
            Order order = orderService.createOrder ( newOrderDetails, shop );
            Transaction transaction = transactionService.makeTransaction(order, order.getAmount(), order.getType());
            orderService.save(order);
            String trStatus = responseService.sendRequest(transaction);
            order.getTransactions().add(transaction);
            orderService.change(order);
            if (trStatus.equals("SUCCESS")) {
                return ResponseEntity.status(HttpStatus.OK).body("okay");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("NOT OK");
            }
//            return ResponseEntity.status(HttpStatus.OK).body("okay");
        }
    }

    @GetMapping("/orders/{id}")
    public OrderDetailsDTO transactionData(@PathVariable Integer id) {
        try {
            OrderDTO order = orderService.findByCommersantOrderId(id);
            return OrderDetailsDTO.from(order);
        } catch (OrderNotFound e) {
            return  null;
        }
    }

}
