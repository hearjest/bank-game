package com.example.demo.entities;

import java.util.Optional;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;

@Entity
@Table(name="items")
public class Item {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private long id;
    private long ownerId;

    @ManyToOne
    @JoinColumn(name="item_series_id",referencedColumnName = "id")
    private ItemMeta itemSeries;


    @ManyToOne
    @JoinColumn(name="order_id")
    @Nullable 
    Order currOrder;

    Item(){
    }

    public Item(ItemMeta itemSeries, long ownerId) {
        this.itemSeries = itemSeries;
        this.ownerId = ownerId;
    }


    public Order getCurrOrder(){
        return this.currOrder;
    }

    public void setCurrOrder(Order newOrder){
        this.currOrder=newOrder;
    }


    public long getId() {
        return this.id;
    }

    public ItemMeta getItemSeries() {
        return this.itemSeries;
    }

    public long getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(Long id){
        this.ownerId=id;
    }



    public void transferTo(long newOwnerId) {
        this.ownerId = newOwnerId;
    }
}
