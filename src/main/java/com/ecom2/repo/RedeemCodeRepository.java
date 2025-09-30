package com.ecom2.repo;

import com.ecom2.entity.Product;
import com.ecom2.entity.RedeemCode;
import com.ecom2.entity.RedeemCode.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RedeemCodeRepository extends JpaRepository<RedeemCode, Long> {

    long countByProductAndStatus(Product product, Status status);

    RedeemCode findByCode(String code);

    
    @Query(value = """
        SELECT *
        FROM redeem_codes
        WHERE product_id = :pid AND status = 'AVAILABLE'
        ORDER BY id
        FOR UPDATE SKIP LOCKED
        """, nativeQuery = true)
    List<RedeemCode> pickAvailableForUpdate(@Param("pid") long productId, Pageable pageable);

    List<RedeemCode> findByProductAndStatus(Product product, Status status);
    
    @Query("""
    	       select rc from RedeemCode rc
    	       join fetch rc.orderItem oi
    	       join oi.order o
    	       where o.id = :orderId
    	       """)
    	List<RedeemCode> findAllByOrderIdFetchItem(@Param("orderId") Long orderId);

    	@Query("select rc from RedeemCode rc where rc.orderItem.id = :orderItemId")
    	List<RedeemCode> findAllByOrderItemId(@Param("orderItemId") Long orderItemId);
    	
    	List<RedeemCode> findByProductOrderByIdAsc(Product product);

}
