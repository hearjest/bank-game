package com.example.demo.entities;
import java.time.LocalDateTime;
import java.util.Currency;
import com.example.demo.enums.ORDER_TYPE;
import org.springframework.stereotype.Service;

import jakarta.persistence.*;

@Entity
@Table(name="orders")
public class Order {
    
   
    @Enumerated
    private ORDER_TYPE type;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private long orderId;
    @ManyToOne
    @JoinColumn(name="poster_id",referencedColumnName = "id")
    private Account poster;

    @ManyToOne
    @JoinColumn(name="client_id",referencedColumnName = "id")
    private Account client;
    private long price;
    private Currency currency;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="item_id", referencedColumnName="id")
    private Item item;

    @ManyToOne(fetch=FetchType.LAZY)
    private ItemMeta itemMeta;
    private LocalDateTime posted;
    private boolean completed;
    private LocalDateTime completedDate;
    

    Order(){
        this.type=null;
        this.poster=null;
        this.client=null;
        this.price=-1;
        this.currency=null;
        this.item=null;
        this.posted=LocalDateTime.now();
        this.completedDate=LocalDateTime.now();
        this.completed=false;
        this.itemMeta=null;
    }

    public Order(Account poster, Item item, ORDER_TYPE type, int price, ItemMeta itemMeta){
        this.type=type;
        this.poster=poster;
        this.client=null;
        this.price=price;
        this.currency=null;
        this.item=item;
        this.posted=LocalDateTime.now();
        this.completedDate=null;
        this.completed=false;
        this.itemMeta=itemMeta;
    }


    public void setCompleted(boolean bool){
        this.completed=bool;
    }

    public boolean isCompleted(){
        return this.completed;
    }


    public void setClient(Account client){
        this.client=client;
    }

    public Account getClient(){
        return this.client;
    }

    public ORDER_TYPE getType() {
        return this.type;
    }

    public void setType(ORDER_TYPE type) {
        this.type = type;
    }

    public long getOrderId() {
        return this.orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public Account getPoster() {
        return this.poster;
    }

    public void setPoster(Account poster) {
        this.poster = poster;
    }

    public long getPrice() {
        return this.price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Currency getCurrency() {
        return this.currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Item getItem() {
        return this.item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public LocalDateTime getPosted() {
        return this.posted;
    }

    public void setPosted(LocalDateTime posted) {
        this.posted = posted;
    }

    public LocalDateTime getCompletedDate() {
        return this.completedDate;
    }

    public void setCompletedDate(LocalDateTime completedDate) {
        this.completedDate = completedDate;
    }
}
