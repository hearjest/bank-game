package com.example.demo.entities;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "item_meta")
public class ItemMeta {

    public enum ItemType {
        COMMODITY,
        UNIQUE
    }
    private String name;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "fields", columnDefinition = "jsonb")
    private Map<String,String> fields;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private Long gameId;

    @Column(name = "description")
    private String desc;
    private String metaDesc;

    @ElementCollection
    @CollectionTable(name = "item_meta_tags", joinColumns = @JoinColumn(name = "item_meta_id"))
    @Column(name = "tag")
    private List<String> tags;

    @Enumerated
    private ItemType type;

    ItemMeta(){
    }

    public ItemMeta(String name, Map<String,String> fields, Long id, Long gameId, String desc, String metaDesc,List<String> tags,ItemType type){
        this.name=name;
        this.fields=fields;
        this.id=id;
        this.gameId=gameId;
        this.desc=desc;
        this.metaDesc=metaDesc;
        this.tags=tags;
        this.type=type;

    }



    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getFields() {
        return this.fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGameId() {
        return this.gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getMetaDesc() {
        return this.metaDesc;
    }

    public void setMetaDesc(String metaDesc) {
        this.metaDesc = metaDesc;
    }

    public List<String> getTags() {
        return this.tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
