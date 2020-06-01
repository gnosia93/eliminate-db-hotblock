package com.amazon.mbp.dto;


import com.amazon.mbp.common.DeliveryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
