## Spring Boot Transaction Managment Between Auroa and Elasticache for Redis ##

Below code snippet is spring boot's OrderService class implementation.
In a spring boot application, @Service Component is used as transaction management object.
Here, you are using eventSave(Order order) method call, in order to make method level transcation between aurora rds and redis cache cluster.

In the case of database is failed, or redis update(addProductBuyCount) call is failed, 
all operation is rollbacked and consitency between aurora rds and redis cluster is kept.
In fact, this is like a trick, because as you know database support transaction, but
redis just support atomic operation not transaction.

So we position redis call(addProductBuyCount) at the end of eventSave(Order order) method call.

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
