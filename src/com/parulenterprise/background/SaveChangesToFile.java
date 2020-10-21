package com.parulenterprise.background;

import com.parulenterprise.db.DataSource;
import com.parulenterprise.model.StockData;
import com.parulenterprise.db.StockDataFile;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;

/* Task calling SQLite functions/codes required to add/edit/update/delete database entries*/
public class SaveChangesToFile extends Task {

    @Override
    protected Object call() throws Exception {
        //update progress in progressBar (updateProgress()) and starts with 1
        int progress = 1;
        int progressLength = StockDataFile.getInstance().getStockDataList().size();
        List<StockData> removeStockData = new ArrayList<>();
        List<StockData> stockDataList = StockDataFile.getInstance().getStockDataList();
        for(StockData sd : stockDataList){
            if(sd.getType() == StockData.Type.UPDATE_STOCK){
                if(DataSource.getInstance().updateItemStock(sd.getNewItem())){
                    removeStockData.add(sd);
                }else {
                    System.out.println("Error updating stock" + sd.getNewItem().getName());
                }
            }else if(sd.getType() == StockData.Type.EDIT_ITEM){
                if(DataSource.getInstance().editItem(sd.getNewItem())){
                    removeStockData.add(sd);
                }else{
                    System.out.println("Error editing item" + sd.getNewItem().getName());
                }
            }else if(sd.getType() == StockData.Type.ADD_ITEM){
                if(DataSource.getInstance().addItem(sd.getNewItem())){
                    removeStockData.add(sd);
                }else{
                    System.out.println("Error adding item" + sd.getNewItem().getName());
                }
            }else if(sd.getType() == StockData.Type.DELETE_ITEM){
                if(DataSource.getInstance().deleteItem(sd.getNewItem())){
                    removeStockData.add(sd);
                }else{
                    System.out.println("Error deleting item" + sd.getNewItem().getName());
                }
            }
            Thread.sleep(1500);
            updateProgress(progress, progressLength);
        }
        for (StockData stockData : removeStockData) {
            StockDataFile.getInstance().deleteStockData(stockData);
        }

        return null;
    }
}