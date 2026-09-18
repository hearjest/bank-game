package com.example.demo.entities;

import java.time.Instant;


import jakarta.persistence.*;

@Entity
@Table(name="account_entries")
public class AccountEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private Instant time;

    @ManyToOne
    @JoinColumn(name="from_account_id", referencedColumnName="id")
    private Account fromAccount;

    @ManyToOne
    @JoinColumn(name="to_account_id", referencedColumnName="id")
    private Account toAccount;

    @ManyToOne
    @JoinColumn(name="platform_account_id", referencedColumnName="id")
    private Account platformAccount;

    // Nullable - only set when this entry settles a trade. Purely for traceability;
    // the amount itself always comes from the `amount` field below, never from order.getPrice(),
    // so entries with no underlying order (rewards, grants, refunds) work the same way.
    @ManyToOne
    @JoinColumn(name="order_id", referencedColumnName="orderId")
    private Order order;

    private long amount;
    private long tax;
    private long fees;

    AccountEntry(){
        this.time=null;
        this.fromAccount=null;
        this.toAccount=null;
        this.platformAccount=null;
        this.order=null;
        this.amount=0;
        this.tax=0;
        this.fees=0;
    }

    public AccountEntry(Instant time, Account fromAccount, Account toAccount, Account platformAccount, Order order, long amount, long tax, long fees){
        this.time=time;
        this.fromAccount=fromAccount;
        this.toAccount=toAccount;
        this.platformAccount=platformAccount;
        this.order=order;
        this.amount=amount;
        this.tax=tax;
        this.fees=fees;
    }

    public long getId() {
        return this.id;
    }

    public Instant getTime() {
        return this.time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public Account getFromAccount() {
        return this.fromAccount;
    }

    public void setFromAccount(Account fromAccount) {
        this.fromAccount = fromAccount;
    }

    public Account getToAccount() {
        return this.toAccount;
    }

    public void setToAccount(Account toAccount) {
        this.toAccount = toAccount;
    }

    public Account getPlatformAccount() {
        return this.platformAccount;
    }

    public void setPlatformAccount(Account platformAccount) {
        this.platformAccount = platformAccount;
    }

    public Order getOrder() {
        return this.order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public long getAmount() {
        return this.amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getTax() {
        return this.tax;
    }

    public void setTax(long tax) {
        this.tax = tax;
    }

    public long getFees() {
        return this.fees;
    }

    public void setFees(long fees) {
        this.fees = fees;
    }

    public long getFromDelta() {
        return -(this.amount + this.tax + this.fees);
    }

    public long getToDelta() {
        return this.amount;
    }

    public long getPlatformDelta() {
        return this.tax + this.fees;
    }
}
