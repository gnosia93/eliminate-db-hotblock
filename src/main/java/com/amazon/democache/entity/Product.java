package com.amazon.democache.entity;

import com.amazon.democache.common.DeliveryType;
import com.amazon.democache.dto.ProductMessage;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;


/*
  product_id         int not null auto_increment,
   name               varchar(100) not null,
   price              int not null,
   description        text,
   thumb_image_url    varchar(300),
   image_url          varchar(300),
   delivery_type      enum('Free', 'Charged'),
   comment_cnt        int not null default 0,
   buy_cnt            int not null default 0,
 */

@Entity
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private int productId;

    private String name;

    private int price;

    private String description;

    @Column(name = "thumb_image_url")
    private String thumImageUrl;

    @Column(name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name="delivery_type")
    private DeliveryType deliveryType;

    @Column(name="comment_cnt")
    private int commentCount;

    @Column(name="buy_cnt")
    private int buyCount;

    @Transient
    private int eventBuyCount;

    public Product updateBuyCount() {
        this.buyCount += 1;
        return this;
    }

    @Builder
    public Product(String name, int price, String description, String thumImageUrl,
                   String imageUrl, DeliveryType deliveryType, int commentCount, int buyCount, int eventBuyCount) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.thumImageUrl = thumImageUrl;
        this.imageUrl = imageUrl;
        this.deliveryType = deliveryType;
        this.commentCount = commentCount;
        this.buyCount = buyCount;
        this.eventBuyCount = eventBuyCount;
    }

    public ProductMessage toProductMessage() {
        return ProductMessage.builder()
                .name(this.name).price(this.price)
                .imageUrl(this.imageUrl).thumImageUrl(this.thumImageUrl)
                .deliveryType(this.deliveryType)
                .commentCount(this.commentCount)
                .buyCount(this.buyCount)
                .eventBuyCount(this.eventBuyCount)
                .build();
    }
}
