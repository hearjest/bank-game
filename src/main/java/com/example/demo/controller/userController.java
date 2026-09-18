package com.example.demo.controller;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entities.Account;
import com.example.demo.entities.Item;
import com.example.demo.entities.ItemMeta;
import com.example.demo.entities.Order;
import com.example.demo.repositories.AccountEntryRepo;
import com.example.demo.repositories.AccountRepo;
import com.example.demo.repositories.ItemMetaRepo;
import com.example.demo.repositories.ItemRepo;
import com.example.demo.repositories.OrderRepo;
import com.example.demo.service.TransactionService;
import com.example.demo.enums.ORDER_TYPE;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;


public class userController{
    private final AccountRepo accountRepo;
    private final OrderRepo orderRepo;
    private final TransactionService transServ;
    private final AccountEntryRepo accountEntryRepo;
    private final ItemRepo itemRepo;
    private final ItemMetaRepo itemMetaRepo;

    public userController(AccountRepo accountRepo, OrderRepo orderRepo, TransactionService transServ, AccountEntryRepo accountEntryRepo,ItemRepo itemRepo, ItemMetaRepo itemMetaRepo) {
        this.accountRepo = accountRepo;
        this.orderRepo=orderRepo;
        this.transServ=transServ;
        this.accountEntryRepo=accountEntryRepo;
        this.itemRepo=itemRepo;
        this.itemMetaRepo=itemMetaRepo;
    }

}

