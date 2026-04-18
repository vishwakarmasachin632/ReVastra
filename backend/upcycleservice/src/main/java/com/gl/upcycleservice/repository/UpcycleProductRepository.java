package com.gl.upcycleservice.repository;

import com.gl.upcycleservice.entity.UpcycleProduct;
import com.gl.upcycleservice.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UpcycleProductRepository extends JpaRepository<UpcycleProduct, Long> {
    List<UpcycleProduct> findByStatusOrderByCreatedAtDesc(ProductStatus status);
    List<UpcycleProduct> findByWorkerProfileIdOrderByCreatedAtDesc(Long workerProfileId);
}