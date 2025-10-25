package com.ecom.retailsoftware.repository;

import com.ecom.retailsoftware.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {

    Optional<ItemEntity> findByItemId(String id);

    @Modifying
    @Query("""
      UPDATE ItemEntity i
      SET i.stockQuantity = i.stockQuantity - :qty
      WHERE i.itemId = :itemId AND i.stockQuantity >= :qty
    """)
    int reserveStock(@Param("itemId") String itemId, @Param("qty") int qty);

    @Query("""
        SELECT i FROM ItemEntity i
        WHERE i.stockQuantity BETWEEN 1 AND :threshold
        """)
    List<ItemEntity> findByStockQuantityLessThanEqualOrderByStockQuantityAsc(Integer threshold);

    List<ItemEntity> findByStockQuantityOrderByNameAsc(Integer stockQuantity);

    Integer countByCategoryId(Long Id);
}
