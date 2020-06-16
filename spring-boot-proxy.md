Below snippet is spring boot's OrderService class implementation.
In a spring boot application, @Service Component is used transaction management object.
Here, you are using eventSave(Order order) method call, in order to make method level transcation between aurora rds and redis cache cluster.

In this example, when we call eventSave method, 
if database is failed, and if addProductBuyCount (redis call) is failed, 
Consitency between aurora rds and redis cluster is keeped..

```
@Transactional
@Service
public class OrderService {

    ...
    
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
    
    ...
```
