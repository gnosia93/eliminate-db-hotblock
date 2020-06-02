package com.amazon.democache.common;

import lombok.Getter;

//    delivery_type      enum('Free', 'Charged'),
@Getter
public enum DeliveryType
{
    Free("Free", 1),
    Charged("Charged", 2);

    String value;
    int number;

    DeliveryType(String value, int number) {
        this.value = value;
        this.number = number;
    }
}
