package com.amazon.democache.repository;

import com.amazon.democache.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
}
