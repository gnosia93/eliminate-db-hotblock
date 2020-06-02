package com.amazon.democache.request;

import com.amazon.democache.entity.Order;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Setter
@Getter
@ToString
public class OrderRequest {

    int orderId;

    int productId;

    int orderPrice;

    public Order toOrder() {

        return Order.builder().productId(this.productId)
                .orderPrice(this.orderPrice)
                .build();
    }

}
