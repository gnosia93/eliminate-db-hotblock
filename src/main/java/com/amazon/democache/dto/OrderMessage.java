package com.amazon.democache.dto;

import com.amazon.democache.common.PayStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class OrderMessage {

    int orderId;

    int productId;

    int orderPrice;

    PayStatus payStatus;

    LocalDateTime orderDate;

    LocalDateTime payDate;

    LocalDateTime errorDate;

    String errorMessage;
}
