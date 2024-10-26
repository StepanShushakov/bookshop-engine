package com.example.mybookshopapp.data.services;

import com.example.mybookshopapp.data.model.payments.BalanceTransactionEntity;
import com.example.mybookshopapp.data.model.user.UserEntity;
import com.example.mybookshopapp.data.repositories.TransactionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * @author karl
 */

@Service
public class TransactionsService {

    private final TransactionsRepository transactionsRepository;

    @Autowired
    public TransactionsService(TransactionsRepository transactionsRepository) {
        this.transactionsRepository = transactionsRepository;
    }

    public Page<BalanceTransactionEntity> getPageOfUserTransactions(UserEntity user, String sort, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit, Sort.by(sort.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, "time"));
        return  transactionsRepository.findBalanceTransactionEntitiesByUser(user, nextPage);
    }

    public Integer getBalanceOnUserId(int userId) {
        return transactionsRepository.findBalanceSumByUserId(userId);
    }
}
