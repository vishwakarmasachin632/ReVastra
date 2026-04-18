package com.gl.orderservice.orderservice.repository;

import com.gl.orderservice.orderservice.entity.Order;
import com.gl.orderservice.orderservice.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    List<Order> findByWorkerId(Long workerId);

    List<Order> findAllByOrderByCreatedAtDesc();

    long countByStatus(OrderStatus status);

    long countByWorkerId(Long workerId);

    List<Order> findByWorkerIdOrderByCreatedAtDesc(Long workerId);

    List<Order> findByWorkerIdAndStatusIn(Long workerId, List<OrderStatus> statuses);
}