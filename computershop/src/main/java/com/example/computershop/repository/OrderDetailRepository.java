package com.example.computershop.repository;

import com.example.computershop.model.OrderDetail;
import com.example.computershop.model.Product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    @Query("SELECT od FROM OrderDetail od WHERE od.order.orderId = :orderId")
    List<OrderDetail> findOrderDetailsByOrderId(@Param("orderId") Long orderId);

    // @Query("SELECT od, p, pv FROM OrderDetail od " +
    // "JOIN Product p ON p.productId = od.product.productId " +
    // "LEFT JOIN ProductVariation pv ON pv.variationId =
    // od.productVariation.variationId " +
    // "WHERE od.order.orderId = :orderId")
    // List<Object[]> findOrderDetailsWithProductsAndVariations(@Param("orderId")
    // Long orderId);

    @Query("SELECT od, p, pv, c FROM OrderDetail od " +
            "JOIN Product p ON p.productId = od.product.productId " +
            "LEFT JOIN ProductVariation pv ON pv.variationId = od.productVariation.variationId " +
            "LEFT JOIN p.category c " + // JOIN thêm với Category
            "WHERE od.order.orderId = :orderId")
    List<Object[]> findOrderDetailsWithProductsAndVariations(@Param("orderId") Long orderId);

    @Query("SELECT DISTINCT od.product FROM OrderDetail od WHERE od.order.user.userId = :userId")
    List<Product> findPurchasedProductsByUser(@Param("userId") Long userId);

    boolean existsByOrder_User_UserIdAndProduct_ProductId(Long userId, Long productId);
}