package com.amazon.mbp.entity;

import com.amazon.mbp.common.PayStatus;
import com.amazon.mbp.dto.OrderMessage;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/*
create table `order`
(
   order_id                int not null,
   product_id              int not null,
   order_price             int not null,
   pay_status              enum('Queued', 'Processing', 'error', 'Completed'),
   order_ymdt              datetime default CURRENT_TIMESTAMP,
   pay_ymdt                datetime,
   error_ymdt              datetime,
   error_message           varchar(300),
   primary key(order_id)
);
 */

@Entity
@Table(name = "`order`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    int orderId;

    @Column(name = "product_id")
    int productId;

    @Column(name = "thumb_image_url")
    private String thumImageUrl;

    @Column(name = "order_price")
    int orderPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "pay_status")
    PayStatus payStatus;

    @Column(name = "order_ymdt")
    LocalDateTime orderDate;

    @Column(name = "pay_ymdt")
    LocalDateTime payDate;

    @Column(name = "error_ymdt")
    LocalDateTime errorDate;

    @Column(name = "error_message")
    String errorMessage;

    @Builder
    public Order(int productId, int orderPrice, String thumImageUrl) {
        this.productId = productId;
        this.orderPrice = orderPrice;
        this.thumImageUrl = thumImageUrl;
        this.payStatus = PayStatus.Queued;
        this.orderDate = LocalDateTime.now();
    }

    public OrderMessage toOrderMessage() {

        return OrderMessage.builder()
                .orderId(this.orderId)
                .productId(this.productId)
                .orderPrice(this.orderPrice)
                .orderDate(this.orderDate)
                .payStatus(PayStatus.Queued)
                .build();

    }
}
