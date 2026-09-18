package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.example.demo.entities.Account;
import com.example.demo.entities.AccountEntry;

import org.springframework.data.jpa.repository.Query;


public interface AccountEntryRepo extends JpaRepository<AccountEntry,Long>{
    @Query("""
    SELECT COALESCE(SUM(
        CASE
            WHEN e.fromAccount = :acc THEN -(e.amount + e.tax + e.fees)
            WHEN e.toAccount = :acc THEN e.amount
            WHEN e.platformAccount = :acc THEN e.tax + e.fees
            ELSE 0
        END
    ), 0)
    FROM AccountEntry e
    WHERE e.fromAccount = :acc OR e.toAccount = :acc OR e.platformAccount = :acc""")
    long getBalanceOfAcc(@Param("acc") Account acc);

}
