package school.attractor.payment_module.domain.order;import com.google.common.collect.ImmutableList;import com.querydsl.core.types.Predicate;import lombok.AllArgsConstructor;import org.springframework.data.domain.Page;import org.springframework.data.domain.PageImpl;import org.springframework.data.domain.Pageable;import org.springframework.stereotype.Service;import school.attractor.payment_module.domain.commersant.CommersantNotFoundException;import school.attractor.payment_module.domain.commersant.CommersantRepository;import school.attractor.payment_module.domain.config.Utility;import school.attractor.payment_module.domain.exception.OrderNotFound;import school.attractor.payment_module.domain.shop.Shop;import school.attractor.payment_module.domain.shop.ShopRepository;import school.attractor.payment_module.domain.shop.ShopService;import school.attractor.payment_module.domain.transaction.NewOrderDetails;import school.attractor.payment_module.domain.transaction.Transaction;import school.attractor.payment_module.domain.transaction.TransactionStatus;import school.attractor.payment_module.domain.transaction.TransactionType;import java.security.Principal;import java.text.DateFormat;import java.text.ParseException;import java.text.SimpleDateFormat;import java.util.Date;import java.util.List;import java.util.Optional;import java.util.stream.Collectors;@Service@AllArgsConstructorpublic class OrderService {    public OrderRepository orderRepository;    public CommersantRepository commersantRepository;    public ShopRepository shopRepository;    private final ShopService shopService;    private List<Shop> findAllShopsByCommersant(String email){        var commersant = commersantRepository.findByEmail ( email ).orElseThrow ( () -> new CommersantNotFoundException ( "" ) );        return shopRepository.findAllByCommersant ( commersant );    }    Page<Order> getOrders(Pageable pageable, Principal principal) {        Date today = new Date();        List<Order> allOrders = orderRepository.findAllByShopIn (findAllShopsByCommersant(principal.getName ()));        List<Order> orders = allOrders.stream ()                .filter ( order -> {                    try {                        return getDayWithNoTime(order.getDate ()).equals(  getDayWithNoTime ( today));                    } catch (ParseException e) {                        e.printStackTrace ( );                    }                    return false;                } )                .collect ( Collectors.toList ( ) );        int start = (int) pageable.getOffset();        int end = (int) (Math.min ( (start + pageable.getPageSize ( )), orders.size ( ) ));        Page<Order> todayOrders = new PageImpl<Order> ( orders.subList ( start, end ), pageable, orders.size ( ) );        return todayOrders;    }    List<Order> getAllOrders(Principal principal)  {        Date today = new Date();        List<Order> allOrders = orderRepository.findAllByShopIn (findAllShopsByCommersant(principal.getName ()));        return allOrders.stream ()                .filter ( order -> {                    try {                        return getDayWithNoTime(order.getDate ()).equals( getDayWithNoTime ( today));                    } catch (ParseException e) {                        e.printStackTrace ( );                    }                    return false;                } )                .collect ( Collectors.toList ( ) );    }    public Date getDayWithNoTime(Date date) throws ParseException {        DateFormat formatter = new SimpleDateFormat ("dd/MM/yyyy");        return formatter.parse(formatter.format(date));    }    List<Order> getAllSearchOrders(Predicate predicate, Principal principal) {        List<Shop> shops =  findAllShopsByCommersant(principal.getName ());;        List<Order> allOrders = ImmutableList.copyOf(orderRepository.findAll ( predicate));        return allOrders.stream ()                .filter ( order -> shops.stream ( ).map ( Shop::getId ).anyMatch ( shop -> shop.equals ( order.getShop ( ).getId ( ) ) ) )                .collect ( Collectors.toList ( ) );    }    Page<Order> getSearchOrders(Pageable pageable, Predicate predicate, Principal principal) {        List<Shop> shops =  findAllShopsByCommersant(principal.getName ());        List<Order> allOrders = ImmutableList.copyOf(orderRepository.findAll ( predicate));        List<Order> orders = allOrders.stream ()                .filter ( order -> shops.stream ( ).map ( Shop::getId ).anyMatch ( shop -> shop.equals ( order.getShop ( ).getId ( ) ) ) )                .collect ( Collectors.toList ( ) );        int start = (int) pageable.getOffset();;        int end = (int) (Math.min ( (start + pageable.getPageSize ( )), orders.size ( ) ));        Page<Order> searchOrders = new PageImpl<Order> ( orders.subList ( start, end ), pageable, orders.size ( ) );        return searchOrders;    }    public OrderDTO findByOrderId(int id) {        Optional<Order> order = orderRepository.findById(id);        return OrderDTO.from(order.orElseThrow( OrderNotFound::new));    }    public OrderDTO findByCommersantOrderId(int id){        Optional<Order> order = orderRepository.findByOrderId(id);        return OrderDTO.from(order.orElseThrow( OrderNotFound::new));    }    public Order save (OrderDTO orderDTO) {        Order order = Order.from(orderDTO);        order.setDate ( new Date (  ) );        return orderRepository.save(order);    }    public void save (Order order) {        order.setCard(Utility.maskCardNumber((order.getCard()), "####********####"));        orderRepository.save(order);    }    public void setOrderParam(Order order, Transaction transaction, String internalReferenceNumber, String retrievalReferenceNumber) {        switch (transaction.getType ()){            case HOLD:                order.setStatus ( TransactionStatus.RESERVED );                order.setInternalReferenceNumber ( internalReferenceNumber );                order.setRetrievalReferenceNumber ( retrievalReferenceNumber);                orderRepository.save ( order );                break;            case PAYMENT:                order.setInternalReferenceNumber ( internalReferenceNumber );                order.setRetrievalReferenceNumber ( retrievalReferenceNumber);                order.setStatus ( TransactionStatus.APPROVED );                orderRepository.save ( order );            case AUTH:                order.setStatus ( TransactionStatus.APPROVED );                orderRepository.save ( order );                break;            default:                if(transaction.getAmount () == order.getAmount ()){                    order.setStatus ( TransactionStatus.TOTAL_REFUND);                    orderRepository.save ( order );                }else {                    order.setStatus ( TransactionStatus.PARTIAL_REFUND );                    order.setResidual ( order.getResidual () - transaction.getAmount () );                    orderRepository.save ( order );                }                break;        }    }    public void change(Order order) {        orderRepository.save(order);    }    public Order findById(int orderId){        return orderRepository.findById ( orderId ).orElseThrow ( OrderNotFound::new );    }    Integer getTotalAmountSum(List<Order> orderList) {        Integer total = 0;        for (Order order : orderList) {            total += order.getAmount();        }        return total;    }    Integer getTotalQuantity(List<Order> orderList) {        return orderList.size();    }    public Order createOrder(NewOrderDetails newOrderDetails, Shop shop) {        Order order = Order.from(newOrderDetails, shop);        order.setShop ( shop );        if(shop.getHold () == 1 ){            order.setType ( TransactionType.PAYMENT);        }else{            order.setType ( TransactionType.HOLD );        }        save(order);        return order;    }}