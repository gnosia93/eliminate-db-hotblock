```
@Transactional
@Service
public class OrderService {

    @Autowired OrderRepository orderRepository;
    @Autowired ProductService productService;
    @Autowired MemoryDBService memoryService;
    @Autowired KafkaTemplate kafkaTemplate;

    final static String KAFKA_TOPIC = "ocktank";


    public Page<Order> findAll(Pageable page) {
        Page<Order> retPage = orderRepository.findAll(page);
        return retPage;
    }

    public Optional<Order> findById(int id) {
        return orderRepository.findById(id);
    }


    public Order save(Order order) {
        productService.addBuyCount(order.getProductId());
        Order retOrder = doOrder(order);
        return retOrder;
    }

    // when database error happens, this method made exception, there is no elasticache update
    // and if cache service is down.. also this is no insert into database.
    //
    public Order eventSave(Order order) {
        Order retOrder = doOrder(order);
        memoryService.addProductBuyCount(order.getProductId());
        return retOrder;
    }

    private Order doOrder(Order order) {
        Optional<Product> optProduct = productService.findById(order.getProductId());
        Order newOrder = Order.builder()
                .productId(order.getProductId())
                .orderPrice(0)
                .orderPrice(optProduct.get().getPrice())
                .thumImageUrl(optProduct.get().getImageUrl())
                .build();

        Order savedOrder = orderRepository.save(newOrder);
        return savedOrder;
    }
```
