package com.example.demo;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.demo.entities.Account;
import com.example.demo.entities.AccountEntry;
import com.example.demo.entities.Item;
import com.example.demo.entities.ItemMeta;
import com.example.demo.entities.Order;
import com.example.demo.entities.Order.ORDER_TYPE;
import com.example.demo.repositories.AccountEntryRepo;
import com.example.demo.repositories.AccountRepo;
import com.example.demo.repositories.ItemMetaRepo;
import com.example.demo.repositories.ItemRepo;
import com.example.demo.repositories.OrderRepo;
import com.example.demo.service.TransactionService;

import jakarta.transaction.Transactional;

@Component
public class DemoSeeder {

    private final AccountRepo ar;
    private final ItemRepo ir;
    private final ItemMetaRepo irp;
    private final OrderRepo or;
    private final AccountEntryRepo aer;
    private final TransactionService transactionService;

    public DemoSeeder(AccountRepo ar, ItemRepo ir, ItemMetaRepo irp, OrderRepo or, AccountEntryRepo aer,
            TransactionService transactionService) {
        this.ar = ar;
        this.ir = ir;
        this.irp = irp;
        this.or = or;
        this.aer = aer;
        this.transactionService = transactionService;
    }

    @Transactional
    public void seed() {

        Account plat = ar.findByUsername("plat");
        if (plat == null) {
            plat = new Account();
            plat.setUsername("plat");
            ar.save(plat);
        }

        Account alicee = ar.findByUsername("alice");
        if (alicee == null) {
            alicee = new Account();
            alicee.setUsername("alice");
            ar.save(alicee);
        }

        Account bobbbb = ar.findByUsername("bobbbb");
        if (bobbbb == null) {
            bobbbb = new Account();
            bobbbb.setUsername("bobbbb");
            ar.save(bobbbb);
        }

        if (alicee.getBalance() == 0) {
            AccountEntry grant = new AccountEntry(Instant.now(), plat, alicee, plat, null, 1000, 0, 0);
            aer.save(grant);
        }

        if (bobbbb.getBalance() == 0) {
            AccountEntry grant2 = new AccountEntry(Instant.now(), plat, bobbbb, plat, null, 1000, 0, 0);
            aer.save(grant2);
        }

        ItemMeta mannCoKey = irp.findByName("Mann Co. Supply Crate Key");
        if (mannCoKey == null) {
            mannCoKey = new ItemMeta("Mann Co. Supply Crate Key", Map.of(), null, 1L, "hi", "erk", List.of(),
                    ItemMeta.ItemType.COMMODITY);
            irp.save(mannCoKey);
        }

        Item keyy = ir.findByOwnerIdAndItemSeries(alicee.getId(), mannCoKey).orElse(null);
        if (keyy == null) {
            keyy = new Item(mannCoKey, alicee.getId());
            ir.save(keyy);
        }

        if (keyy.getOwnerId() == alicee.getId()) {
            Order ord = new Order();
            ord.setPoster(alicee);
            ord.setItem(keyy);
            ord.setType(ORDER_TYPE.SELL);
            ord.setPrice(10);
            ord.setClient(bobbbb);
            or.save(ord);
            transactionService.completeTransaction(ord);
        }
        System.out.println("new balance");
        System.out.println(alicee.getBalance());
        System.out.println(bobbbb.getBalance());
    }
}
