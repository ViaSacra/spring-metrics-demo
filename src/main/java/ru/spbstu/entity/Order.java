package ru.spbstu.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Order {
    private Long id;
    private PayType type;
}
