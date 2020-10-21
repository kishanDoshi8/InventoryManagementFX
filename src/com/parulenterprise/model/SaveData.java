package com.parulenterprise.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;

public class SaveData {

    private SimpleStringProperty dataType;
    private SimpleStringProperty productName;
    private SimpleStringProperty itemName;
    private SimpleStringProperty stock;
    private Hyperlink viewLink;
    private Button deleteButton;

    public SaveData(){
        this.dataType = new SimpleStringProperty();
        this.productName = new SimpleStringProperty();
        this.itemName = new SimpleStringProperty();
        this.stock = new SimpleStringProperty();
        this.viewLink = new Hyperlink("view...");
        this.deleteButton = new Button("Undo");
    }

    public String getDataType() {
        return dataType.get();
    }

    public SimpleStringProperty dataTypeProperty() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType.set(dataType);
    }

    public String getProductName() {
        return productName.get();
    }

    public SimpleStringProperty productNameProperty() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName.set(productName);
    }

    public String getItemName() {
        return itemName.get();
    }

    public SimpleStringProperty itemNameProperty() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName.set(itemName);
    }

    public String getStock() {
        return stock.get();
    }

    public SimpleStringProperty stockProperty() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock.set(stock);
    }

    public Hyperlink getViewLink() {
        return viewLink;
    }

    public void setViewLink(Hyperlink viewLink) {
        this.viewLink = viewLink;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    public void setDeleteButton(Button deleteButton) {
        this.deleteButton = deleteButton;
    }
}
