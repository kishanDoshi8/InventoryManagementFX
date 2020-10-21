package com.parulenterprise.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Product implements Serializable {

    private transient SimpleStringProperty name;
    private transient SimpleStringProperty id;
    private transient SimpleIntegerProperty stock;

    public Product() {
        this.name = new SimpleStringProperty();
        this.id = new SimpleStringProperty();
        this.stock = new SimpleIntegerProperty();
    }

    public int getStock() {
        return stock.get();
    }

    public void setStock(int stock) {
        this.stock.set(stock);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    private void writeObject(ObjectOutputStream s) throws IOException{
        s.defaultWriteObject();
        s.writeUTF(id.getValueSafe());
        s.writeUTF(name.getValueSafe());
        s.writeInt(stock.getValue());
    }

    private void readObject(ObjectInputStream s) throws IOException{
        this.id = new SimpleStringProperty(s.readUTF());
        this.name = new SimpleStringProperty(s.readUTF());
        this.stock = new SimpleIntegerProperty(s.readInt());
    }
}
