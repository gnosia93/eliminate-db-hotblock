package com.amazon.democache.controller;

import com.amazon.democache.common.RecordNotFoundException;
import com.amazon.democache.common.Response;
import com.amazon.democache.entity.Order;
import com.amazon.democache.request.OrderRequest;
import com.amazon.democache.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired OrderService orderService;

    @RequestMapping(path = "/{page}/{size}", method = RequestMethod.GET)
    public Response<?> list(@PathVariable int page, @PathVariable int size) {

        PageRequest pageRequest = PageRequest.of(page-1, size, Sort.Direction.DESC, "orderId" );
        Page<Order> orderList = orderService.findAll(pageRequest);

        return Response.success(orderList);
    }

    @RequestMapping(path = "/{id:[0-9]+}", method = RequestMethod.GET)
    public Response<?> get(@PathVariable int id) {

        Optional<Order> order = orderService.findById(id);
        return Response.success(order.orElseThrow(RecordNotFoundException::new));
    }


    @RequestMapping(path="/add", method = RequestMethod.POST)
    public Response<?> add(@RequestBody OrderRequest orderRequest) {

        Order order = orderRequest.toOrder();
        order = orderService.save(order);

        return Response.success(order);
    }

    /*
     *  elasticache에 재고수량(상품 주문수량을 관리하여 데이터베이스 contention 을 감소시킨다.
     */
    @RequestMapping(path="/event-add", method = RequestMethod.POST)
    public Response<?> eventAdd(@RequestBody OrderRequest orderRequest) {

        Order order = orderRequest.toOrder();
        order = orderService.eventSave(order);

        return Response.success(order);
    }



}
