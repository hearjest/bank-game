package com.example.demo.entities;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import java.time.Instant;
import com.example.demo.enums.ORDER_STATUS;
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
    
    // FK lives on the items table (items.order_id). No cascade/orphanRemoval: removing an
    // item from this list means it sold and changed hands, never that it should be deleted.
    @OneToMany(mappedBy="currOrder")
    private List<Item> items = new ArrayList<>();

    private int filledQuantity;

    // How many units this order is for. For a SELL this mirrors the items it holds;
    // for a BUY it's the only record of size, since a buy order holds no items.
    private int wantedQuantity;

    @ManyToOne(fetch=FetchType.LAZY)
    private ItemMeta itemMeta;
    private Instant posted;
    private ORDER_STATUS status;
    private Instant completedDate;
    

    Order(){
        this.type=null;
        this.poster=null;
        this.client=null;
        this.price=-1;
        this.currency=null;
        this.posted=Instant.now();
        this.completedDate=null;
        this.status=ORDER_STATUS.OPEN;
        this.itemMeta=null;
        this.filledQuantity=0;
        this.wantedQuantity=0;
    }

    // SELL orders: the quantity is however many items were handed over.
    public Order(Account poster, List<Item> items, ORDER_TYPE type, int price, ItemMeta itemMeta){
        this(poster, items, type, price, itemMeta, items!=null?items.size():0);
    }

    public Order(Account poster, List<Item> items, ORDER_TYPE type, int price, ItemMeta itemMeta, int wantedQuantity){
        this.type=type;
        this.poster=poster;
        this.client=null;
        this.price=price;
        this.currency=null;
        this.posted=Instant.now();
        this.completedDate=null;
        this.status=ORDER_STATUS.OPEN;
        this.itemMeta=itemMeta;
        this.filledQuantity=0;
        this.wantedQuantity=wantedQuantity;
        // via addItem so the FK on the item side actually gets written
        if(items!=null){
            for(Item i:items){ addItem(i); }
        }
    }


    // A BUY order holds no items, so its outstanding size has to come from the counters.
    public int getRemainingQuantity(){
        if(this.type==ORDER_TYPE.BUY){
            return this.wantedQuantity-this.filledQuantity;
        }
        return this.items.size();
    }

    public int getWantedQuantity(){
        return this.wantedQuantity;
    }

    public void setWantedQuantity(int wantedQuantity){
        this.wantedQuantity=wantedQuantity;
    }

    // Records a fill on an order that holds no items to pop (i.e. a BUY).
    public void recordFill(int quantity){
        if(quantity<=0 || quantity>getRemainingQuantity()){
            throw new IllegalArgumentException(
                "Cannot fill "+quantity+" on an order with "+getRemainingQuantity()+" outstanding");
        }
        this.filledQuantity+=quantity;
    }

    public int getFilledQuantity(){
        return this.filledQuantity;
    }

    // Removes and returns the next `quantity` items, for a full or partial fill.
    // Caller is responsible for transferring ownership of the returned items.
    public List<Item> popItems(int quantity){
        if (quantity <= 0 || quantity > this.items.size()) {
            throw new IllegalArgumentException(
                "Cannot pop " + quantity + " items from an order holding " + this.items.size());
        }
        List<Item> popped = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            Item item = this.items.remove(0);
            item.setCurrOrder(null);   // leaving the order releases it from escrow
            popped.add(item);
        }
        this.filledQuantity += quantity;
        return popped;
    }

    public ORDER_STATUS getStatus(){
        return this.status;
    }

    public void setStatus(ORDER_STATUS os){
        this.status=os;
    }

    public void setCompletedDate(){
        this.completedDate=Instant.now();
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

    public ItemMeta getItemMeta() {
        return this.itemMeta;
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

    public List<Item> getItems() {
        return this.items;
    }

    public void setItems(List<Item> items) {
        for(Item old:this.items){ old.setCurrOrder(null); }
        this.items = new ArrayList<>();
        if(items!=null){
            for(Item i:items){ addItem(i); }
        }
    }

    public void addItem(Item item) {
        this.items.add(item);
        item.setCurrOrder(this);
    }

    public Instant getPosted() {
        return this.posted;
    }

    public void setPosted(Instant posted) {
        this.posted = posted;
    }

    public Instant getCompletedDate() {
        return this.completedDate;
    }

    public void setCompletedDate(Instant time) {
        this.completedDate = time;
    }
}
