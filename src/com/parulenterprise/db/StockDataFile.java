package com.parulenterprise.db;

import com.parulenterprise.model.Item;
import com.parulenterprise.model.StockData;

import java.io.*;
import java.util.*;

public class StockDataFile implements Serializable {

    private static StockDataFile instance = new StockDataFile();
    private static String filename = "stockData.dat";
    private long serialVersionUID = 1L;
    //Last UID on file format (yyyymmddXXT####  where y is year m is month d is day XX is user code(01 management)
    //    // T is StockData.Type  # is 4 digit serial number)
    //    //UID T = (1, UPDATE_ITEM) (2, EDIT_ITEM) (3, ADD_ITEM) (4, DELETE_ITEM)
    public static long lastUID;

    private List<StockData> stockDataList;
    private List<StockData> allStockData;

    public static StockDataFile getInstance() {
        return instance;
    }

    private StockDataFile() {
        stockDataList = new ArrayList<>();
        allStockData = new ArrayList<>();
    }

    public List<StockData> getStockDataList(){
        return stockDataList;
    }

    public void deleteStockData(StockData stockData){
        stockDataList.remove(stockData);
    }

    public void addStockData(StockData stockData){
        for(StockData s : stockDataList){
            if(s.getOldItem().getId().equals(stockData.getOldItem().getId())){
                Item oldItem = s.getOldItem();
                stockData.setOldItem(oldItem);
                int index = stockDataList.indexOf(s);
                stockDataList.set(index, stockData);
                return;
            }
        }
        stockDataList.add(stockData);
    }

    public boolean clearAllStockData(){
        if(!stockDataList.isEmpty()){
            stockDataList.clear();
            return true;
        }else{
            return false;
        }
    }

    public void loadStockData(){
        try(ObjectInputStream stockFile = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
            boolean eof = false;
            while(!eof) {
                try {
                    StockData stockData = (StockData) stockFile.readObject();
                    allStockData.add(stockData);
                    lastUID = stockData.getUID() + 1;
                } catch (EOFException e) {
                    eof = true;
                }
            }
        } catch (IOException e) {
            System.out.println("IOException Error loadStockData: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException Error loadStockData: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<StockData> getAllStockData() {
        return allStockData;
    }

    public void saveStockData(){
        try(ObjectOutputStream stockFile = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filename)))) {
            for(StockData stockData : stockDataList){
                stockFile.writeObject(stockData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
