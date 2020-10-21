package com.parulenterprise.model;
import com.parulenterprise.db.StockDataFile;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StockData implements Serializable {
    //format (yyyymmddXXT####  where y is year m is month d is day XX is user code(01 management)
    // T is StockData.Type  # is 4 digit serial number)
    //UID T = (1, UPDATE_ITEM) (2, EDIT_ITEM) (3, ADD_ITEM) (4, DELETE_ITEM)
    private static final String userCode = "01";
    private long UID;
    //name of the person adding or selling the stock item
    private  String inputName;
    //the date on which stock was added in (manufactured) or sold from the warehouse
    private transient LocalDate stockDate;
    //the date on which stock was added or sold om the file
    private LocalDate inputDate;
    //type of stockData
    private Type type;
    //Product
    private Product product;
    //Item before changes made
    private Item oldItem;
    //Item after changes made
    private Item newItem;
    //Notes if needed. empty string is default
    private String note;

    public enum Type{
        UPDATE_STOCK,
        EDIT_ITEM,
        ADD_ITEM,
        DELETE_ITEM
    }

    public StockData(LocalDate stockDate, Type type, Product product, Item oldItem, Item newItem, String note){
        new StockData();
        this.stockDate = stockDate;
        this.type = type;
        this.product = product;
        this.oldItem = oldItem;
        this.newItem = newItem;
        this.note = note;
        this.UID = setUID();
        System.out.println("UID: " + UID);
    }

    //initialize here
    private StockData(){
        this.inputDate = LocalDate.now();
    }

    public LocalDate getStockDate() {
        return stockDate;
    }

    public void setStockDate(LocalDate stockDate) {
        this.stockDate = stockDate;
    }

    public LocalDate getInputDate() {
        return inputDate;
    }

    public void setInputDate(LocalDate inputDate) {
        this.inputDate = inputDate;
    }

    public String getInputName() {
        return inputName;
    }

    public void setInputName(String inputName) {
        this.inputName = inputName;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Item getOldItem() {
        return oldItem;
    }

    public void setOldItem(Item oldItem) {
        this.oldItem = oldItem;
    }

    public Item getNewItem() {
        return newItem;
    }

    public void setNewItem(Item newItem) {
        this.newItem = newItem;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getUID() {
        return UID;
    }

    private long setUID(){
        //getting yyyyMMdd for UID
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        //checking if its a new day. if true #(UID 4 digit) should start from 1
        boolean isNewDay = true;
        //updating StockData.Type in UID (T)
        String type = "";
        if(this.type == Type.UPDATE_STOCK){
            type = "1";
        }else if(this.type == Type.EDIT_ITEM){
            type = "2";
        }else if(this.type == Type.ADD_ITEM){
            type = "3";
        }else if(this.type == Type.DELETE_ITEM){
            type = "4";
        }
        //getting first 6 digit of the UID that is yyyyMMdd
        long dateCheck = StockDataFile.lastUID / (int) (Math.pow(10, 7));
        //if its the same day increment the #(4 digit serial number)
        if(String.valueOf(dateCheck).equals(date)){
            isNewDay = false;
        }
        //First entry of the day starts with 0001
        String serialNo;
        if(StockDataFile.lastUID == 0 || isNewDay){
            serialNo = "0001";
        }else{
            int serialNumber = (int) (StockDataFile.lastUID % (int) Math.pow(10,3));
            serialNo = String.format("%04d", serialNumber);
        }
        //creating UID of the StockData.
        String uid = date + userCode + type + serialNo;
        //saving/updating the last UID on RAM.
        StockDataFile.lastUID = Long.parseLong(uid);

        //incrementing and saving the UID.
        return StockDataFile.lastUID++;
    }
}
