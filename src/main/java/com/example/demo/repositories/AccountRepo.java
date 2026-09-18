package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entities.Account;

import java.util.List;


public interface AccountRepo extends JpaRepository<Account,Long>{
    Account findByUsername(String username);

    Account findById(long id);
    
}
