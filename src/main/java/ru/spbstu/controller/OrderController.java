package ru.spbstu.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.entity.Order;
import ru.spbstu.entity.PayType;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private static final AtomicLong seq = new AtomicLong(0L);
    private static final String description = "The number of orders ever placed for ";

    private final MeterRegistry meterRegistry;
    private final List<Order> orders = new ArrayList<>();
    private final Map<String, Counter> counters = new HashMap<>();

    @PostConstruct
    public void init() {
        initOrderCounters();
        Gauge.builder("shop.all.orders", orders, Collection::size)
                .description("x")
                .register(meterRegistry);
    }

    @GetMapping("/createOrder")
    public ResponseEntity<String> getMetrics(@RequestParam("type") String type) {
        PayType payType = PayType.valueOf(type);
        Order order = Order.builder()
                .id(seq.incrementAndGet())
                .type(payType)
                .build();
        orders.add(order);
        counterIncrement(payType);
        return new ResponseEntity<>("Order is successful!", HttpStatus.OK);
    }

    private void initOrderCounters() {
        counters.put(PayType.CASH.getValue(), createCounter(PayType.CASH.getValue()));
        counters.put(PayType.NON_CASH.getValue(), createCounter(PayType.NON_CASH.getValue()));
    }

    private Counter createCounter(String type) {
        return Counter.builder("shop.orders")
                .tag("type", type)
                .description(description + type)
                .register(meterRegistry);
    }

    private void counterIncrement(PayType type) {
        if (type.equals(PayType.CASH)) {
            counters.get(type.getValue()).increment();
        } else if (type.equals(PayType.NON_CASH)) {
            counters.get(type.getValue()).increment();
        }
    }
}
