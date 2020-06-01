package com.amazon.mbp.dto;

import com.amazon.mbp.common.PayStatus;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
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
