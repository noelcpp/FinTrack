package com.fintrack.model;

import com.fintrack.model.enums.TransactionType;

public class Category {
    private Long id;
    private String name;
    private TransactionType type;

    public Category(Long id, String name, TransactionType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public Category(String name, TransactionType type) { this(null, name, type); }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }
    @Override public String toString() { return name + " (" + type + ")"; }
}
