package com.amazon.mbp.service;

import com.amazon.mbp.entity.Order;
import com.amazon.mbp.entity.Product;
import com.amazon.mbp.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class OrderService {

    @Autowired OrderRepository orderRepository;
    @Autowired ProductService productService;
    @Autowired MemoryDBService memoryService;
    @Autowired KafkaTemplate kafkaTemplate;

   // @Autowired QueueMessagingTemplate messagingTemplate;
   // final static String SQS_TOPIC   = "OrderQueue";

    final static String KAFKA_TOPIC = "ocktank";


    public Page<Order> findAll(Pageable page) {
        Page<Order> retPage = orderRepository.findAll(page);
      //  retPage.
        return retPage;
    }

    public Optional<Order> findById(int id) {
        return orderRepository.findById(id);
    }


    public Order save(Order order) {
        productService.addBuyCount(order.getProductId());
        Order retOrder = doOrder(order);
        return retOrder;

        // doOrder 부터 호출하면 데드락
        // addBuyCount 부터 호출하면 데드락을 피한다. 이해할 수 없다.

    }

    // OrderType 에 일반, 이벤트 등으로 나누는 것이 좋겠다.
    //
    public Order eventSave(Order order) {
        memoryService.addProductBuyCount(order.getProductId());
        Order retOrder = doOrder(order);
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

        //kafkaTemplate.send(KAFKA_TOPIC, savedOrder.toOrderMessage());
        //messagingTemplate.convertAndSend(SQS_TOPIC, "this is test..");


        return savedOrder;
    }




}
