package school.attractor.payment_module.domain.order;import com.querydsl.core.types.Predicate;import lombok.AllArgsConstructor;import org.springframework.data.domain.Page;import org.springframework.data.domain.PageImpl;import org.springframework.data.domain.Pageable;import org.springframework.stereotype.Service;import school.attractor.payment_module.domain.commersant.CommersantNotFoundException;import school.attractor.payment_module.domain.commersant.CommersantRepository;import school.attractor.payment_module.domain.exception.OrderNotFound;import school.attractor.payment_module.domain.shop.Shop;import school.attractor.payment_module.domain.shop.ShopRepository;import school.attractor.payment_module.domain.transaction.Transaction;import school.attractor.payment_module.domain.transaction.TransactionStatus;import java.security.Principal;import java.util.Date;import java.util.List;import java.util.Optional;import java.util.stream.Collectors;@Service@AllArgsConstructorpublic class OrderService {    public OrderRepository orderRepository;    public CommersantRepository commersantRepository;    public ShopRepository shopRepository;    public Page<Order> getOrders(Pageable pageable, Principal principal) {        var commersant = commersantRepository.findByEmail ( principal.getName ( ) ).orElseThrow (()->new CommersantNotFoundException ( "" ) );        List<Shop> shops = shopRepository.findAllByCommersant ( commersant );        return orderRepository.findAllByShopIn(pageable, shops);    }    public Page<Order> getSearchOrders(Pageable pageable, Predicate predicate,Principal principal) {        var commersant = commersantRepository.findByEmail ( principal.getName ( ) ).orElseThrow ( () -> new CommersantNotFoundException ( "" ) );        List<Shop> shops = shopRepository.findAllByCommersant ( commersant );        Page<Order> allOrders = orderRepository.findAll ( predicate, pageable );        List<Order> allOrdersContent = allOrders.getContent ( );        List<Order> orders = allOrdersContent.stream ()                .filter ( e -> shops.stream ( ).map ( Shop::getId ).anyMatch ( shop -> shop.equals ( e.getShop ( ).getId ( ) ) ) )                .collect ( Collectors.toList ( ) );        return new PageImpl<> ( orders, pageable, orders.size ( ) );    }    public OrderDTO findByOrderId(int id) {        Optional<Order> order = orderRepository.findById(id);        return OrderDTO.from(order.orElseThrow( OrderNotFound::new));    }    public Order save (OrderDTO orderDTO) {        Order order = Order.from(orderDTO);        order.setDate ( new Date (  ) );        return orderRepository.save(order);    }    public void save (Order order) {        orderRepository.save(order);    }    public void setOrderParam(Order order, Transaction transaction, String internalReferenceNumber, String retrievalReferenceNumber) {        switch (transaction.getType ()){            case HOLD:                order.setStatus ( TransactionStatus.RESERVED );                order.setInternalReferenceNumber ( internalReferenceNumber );                order.setRetrievalReferenceNumber ( retrievalReferenceNumber);                orderRepository.save ( order );                break;            case PAYMENT:                order.setInternalReferenceNumber ( internalReferenceNumber );                order.setRetrievalReferenceNumber ( retrievalReferenceNumber);                order.setStatus ( TransactionStatus.APPROVED );                orderRepository.save ( order );            case AUTH:                order.setStatus ( TransactionStatus.APPROVED );                orderRepository.save ( order );                break;            default:                if(transaction.getAmount () == order.getAmount ()){                    order.setStatus ( TransactionStatus.TOTAL_REFUND);                    orderRepository.save ( order );                }else {                    order.setStatus ( TransactionStatus.PARTIAL_REFUND );                    order.setResidual ( order.getResidual () - transaction.getAmount () );                    orderRepository.save ( order );                }                break;        }    }    public void change(Order order) {        orderRepository.save(order);    }    public Order findById(int orderId){        return orderRepository.findById ( orderId ).get ();    }    Integer getTotalAmountSum(Page<Order> orders) {        List<Order> orderList = orders.getContent();        Integer total = 0;        for (Order order : orderList) {            total += order.getAmount();        }        return total;    }    Integer getTotalQuantity(Page<Order> orders) {        List<Order> orderList = orders.getContent();        return orderList.size();    }}