package school.attractor.payment_module.domain.commersant;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import school.attractor.payment_module.domain.ApacheHttp.ResponseService;
import school.attractor.payment_module.domain.order.Order;
import school.attractor.payment_module.domain.order.OrderService;
import school.attractor.payment_module.domain.transaction.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;


@Controller
@AllArgsConstructor
@RequestMapping
public class CommersantController{

    private final TransactionService transactionService;
    private final OrderService orderService;
    private final ResponseService responseService;
    private final CommersantService commersantService;

    @GetMapping("/")
    public String hello() {
        return "index";
    }


    @GetMapping("/home")
    public String homePage() {
        return "main";
    }



    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false, defaultValue = "false") Boolean error, Model model){
        model.addAttribute ( "error", error );
        return "login";
    }

    @GetMapping("/registration")
    public  String registerPage(Model model){
        if(!model.containsAttribute ( "commersant" )){
            model.addAttribute ( "commersant", new CommersantRegistrationDataDTO () );
            model.addAttribute("errors", model.getAttribute("errors"));
        }
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute("commersant") @Valid CommersantRegistrationDataDTO data, BindingResult bindingResult, Model model
    ){
        System.out.println(data);
        model.addAttribute ( "commersant", data );
        if(bindingResult.hasFieldErrors (  )){
            model.addAttribute ( "errors", bindingResult.getAllErrors ()  );
            return "registration";
        }
        try {
            commersantService.register(data);
        } catch (CommersantAlreadyRegisteredException uarEx) {
            model.addAttribute ("message", "Commersant already exists.");
            return "registration";
        }
        return "redirect:/login";
    }

    @PostMapping("/sendRequest")
    public String sendRequest(Model model, @RequestParam int orderId, @RequestParam int amount, @RequestParam String type,
                              RedirectAttributes attributes) {
        attributes.addFlashAttribute ( "type", type );
        attributes.addFlashAttribute ( "orderId", orderId );
        attributes.addFlashAttribute ( "amount", amount );
        return "redirect:/send";
    }

    @GetMapping("/send")
    public String openRequestPage() {
        return "request";
    }

    @GetMapping("/reversePage")
    public String openResponsePage() {
        return "reverse-response";
    }


    @PostMapping("/confirm")
    public String confirmReverse(@RequestParam int orderId, @RequestParam int amount, @RequestParam TransactionType type,
                                 RedirectAttributes attributes) {
        Order order = orderService.findById ( orderId );
        Transaction transaction = transactionService.makeTransaction ( order, amount, type );
//        String trStatus = responseService.sendRequest ( transaction);
        if(transaction.getAmount () == order.getAmount ()){
            order.setStatus ( TransactionStatus.TOTAL_REFUND);
            orderService.change ( order );
        }else {
            order.setStatus ( TransactionStatus.PARTIAL_REFUND );
            order.setResidual ( order.getResidual () - transaction.getAmount () );
            orderService.change ( order );
        }
        String trStatus = "SUCCESS";
        order.getTransactions ().add(transaction);
        orderService.change ( order );
        if (trStatus.equals ( "SUCCESS" )) {
            attributes.addFlashAttribute ( "response", "SUCCESS" );
        } else {
            attributes.addFlashAttribute ( "response", "REFUSED" );
        }
        return "redirect:/reversePage";
    }

}

