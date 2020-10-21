package com.parulenterprise.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Item implements Serializable {
    private transient SimpleStringProperty id;
    private transient SimpleStringProperty name;
    private transient SimpleIntegerProperty pages;
    private transient SimpleIntegerProperty packageSize;
    private transient SimpleDoubleProperty price;
    private transient SimpleIntegerProperty stock;
    private transient SimpleStringProperty productID;

    public static final long serialVersionUID = 10L;

    public Item() {
        this.id = new SimpleStringProperty();
        this.name = new SimpleStringProperty();
        this.pages = new SimpleIntegerProperty();
        this.packageSize = new SimpleIntegerProperty();
        this.price = new SimpleDoubleProperty();
        this.stock = new SimpleIntegerProperty();
        this.productID = new SimpleStringProperty();
    }

    public Object clone() throws CloneNotSupportedException{
        return super.clone();
    }

    public Item(Item item){
        this.id = new SimpleStringProperty();
        this.name = new SimpleStringProperty();
        this.pages = new SimpleIntegerProperty();
        this.packageSize = new SimpleIntegerProperty();
        this.price = new SimpleDoubleProperty();
        this.stock = new SimpleIntegerProperty();
        this.productID = new SimpleStringProperty();

        this.id.set(item.getId());
        this.name.set(item.getName());
        this.pages.set(item.getPages());
        this.packageSize.set(item.getPackageSize());
        this.price.set(item.getPrice());
        this.stock.set(item.getStock());
        this.productID.set(item.getProductID());
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setPackageSize(int packageSize) {
        this.packageSize.set(packageSize);
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public void setStock(int stock) {
        this.stock.set(stock);
    }

    public void setProductID(String productID) {
        this.productID.set(productID);
    }

    public int getPages() {
        return pages.get();
    }

    public void setPages(int pages) {
        this.pages.set(pages);
    }

    public String getId() {
        return id.get();
    }

    public String getName() {
        return name.get();
    }

    public int getPackageSize() {
        return packageSize.get();
    }

    public double getPrice() {
        return price.get();
    }

    public int getStock() {
        return stock.get();
    }

    public String getProductID() {
        return productID.get();
    }

    private void writeObject(ObjectOutputStream s) throws IOException{
        s.defaultWriteObject();
        s.writeUTF(id.getValueSafe());
        s.writeUTF(name.getValueSafe());
        s.writeInt(pages.getValue());
        s.writeInt(packageSize.getValue());
        s.writeDouble(price.getValue());
        s.writeInt(stock.getValue());
        s.writeUTF(productID.getValueSafe());
    }



    private void readObject(ObjectInputStream s) throws IOException {
        this.id = new SimpleStringProperty(s.readUTF());
        this.name = new SimpleStringProperty(s.readUTF());
        this.pages = new SimpleIntegerProperty(s.readInt());
        this.packageSize = new SimpleIntegerProperty(s.readInt());
        this.price = new SimpleDoubleProperty(s.readDouble());
        this.stock = new SimpleIntegerProperty(s.readInt());
        this.productID = new SimpleStringProperty(s.readUTF());
    }
}