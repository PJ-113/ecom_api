package com.ecom2.repo;

import com.ecom2.entity.Order;
import com.ecom2.entity.RedeemCode;
import com.ecom2.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
	@Query("""
	           select distinct o from Order o
	           left join fetch o.items i
	           left join fetch i.product   
	           where o.id = :id
	           """)
		Optional<Order> findWithItems(@Param("id") Long id);

	    @Query("""
	           select o from Order o
	           where o.user = :user
	             and o.total is not null and o.total > 0
	             and exists (select 1 from OrderItem i where i.order = o)
	           order by o.createdAt desc
	           """)
	    List<Order> findCheckedOutByUser(@Param("user") User user);

	    @Query("""
	           select o from Order o
	           where o.total is not null and o.total > 0
	             and exists (select 1 from OrderItem i where i.order = o)
	           order by o.createdAt desc
	           """)
	    List<Order> findAllCheckedOut();
}

