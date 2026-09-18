package com.example.demo.entities;

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

    

    Item(){
    }

    public Item(ItemMeta itemSeries, long ownerId) {
        this.itemSeries = itemSeries;
        this.ownerId = ownerId;
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
