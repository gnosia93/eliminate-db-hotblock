package com.amazon.democache.dto;


import com.amazon.democache.common.DeliveryType;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class ProductMessage implements Serializable {

    private int productId;

    private String name;

    private int price;

    private String description;

    private String thumImageUrl;

    private String imageUrl;

    private DeliveryType deliveryType;

    private int commentCount;

    private int buyCount;

    private int eventBuyCount;

}
