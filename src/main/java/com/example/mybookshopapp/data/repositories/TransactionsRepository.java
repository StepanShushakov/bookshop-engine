package com.example.mybookshopapp.data.repositories;

import com.example.mybookshopapp.data.model.payments.BalanceTransactionEntity;
import com.example.mybookshopapp.data.model.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author karl
 */

@Repository
public interface TransactionsRepository extends JpaRepository<BalanceTransactionEntity, Integer> {
    Page<BalanceTransactionEntity> findBalanceTransactionEntitiesByUser(UserEntity user, Pageable nextPage);

    @Query(nativeQuery = true, value = """
            select
                coalesce(sum(value), 0) as balance
            from
                balance_transaction bt
            where
                bt.user_id = :userId
""")
    Integer findBalanceSumByUserId(@Param("userId") int userId);
}