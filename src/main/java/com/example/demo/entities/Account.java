package com.example.demo.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;


@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(nullable=false,name="username")
    private String username;

    @Column(nullable=false,name="is_platform")
    private boolean isPlatformAcc;

    @OneToMany(mappedBy = "fromAccount")
    private List<AccountEntry> entriesAsFrom = new ArrayList<>();

    @OneToMany(mappedBy = "toAccount")
    private List<AccountEntry> entriesAsTo = new ArrayList<>();

    @OneToMany(mappedBy = "platformAccount")
    private List<AccountEntry> entriesAsPlatform = new ArrayList<>();

    public void setUsername(String newUsername){
        this.username=newUsername;
    }

    public String getUsername(){
        return this.username;
    }

    public long getId(){
        return this.id;
    }

    public long getBalance(){
        long balance = 0;
        for (AccountEntry entry : entriesAsFrom) {
            balance += entry.getFromDelta();
        }
        for (AccountEntry entry : entriesAsTo) {
            balance += entry.getToDelta();
        }
        for (AccountEntry entry : entriesAsPlatform) {
            balance += entry.getPlatformDelta();
        }
        return balance;
    }

}
