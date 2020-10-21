package com.parulenterprise.ui;

import com.parulenterprise.model.SaveData;
import com.parulenterprise.model.StockData;
import com.parulenterprise.db.StockDataFile;
import com.parulenterprise.model.Item;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.List;

public class SaveChangesDialog {

    @FXML
    private TextField inputName;

    @FXML
    private VBox saveDataVBox;

    @FXML
    private ScrollPane saveCenterScrollPane;

    @FXML
    private TableView<SaveData> saveDataTableView;



    public void processResult(){
        inputName.requestFocus();
        //clearing old values(tableView) when method called again
        saveDataVBox.getChildren().clear();

        List<StockData> stockDataList = StockDataFile.getInstance().getStockDataList();

        saveDataTableView = new TableView<>();

        TableColumn<SaveData, String> type = new TableColumn<>(" ");
        type.setCellValueFactory(new PropertyValueFactory<>("dataType"));

        TableColumn<SaveData, String> productName = new TableColumn<>("Product");
        productName.setCellValueFactory(new PropertyValueFactory<>("productName"));

        TableColumn<SaveData, String> itemName = new TableColumn<>("Item");
        itemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));

        TableColumn<SaveData, String> stock = new TableColumn<>("Stock");
        stock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        TableColumn<SaveData, Hyperlink> view = new TableColumn<>(" ");
        view.setCellValueFactory(new PropertyValueFactory<>("viewLink"));

        TableColumn<SaveData, Button> undoBtn = new TableColumn<>(" ");
        undoBtn.setCellValueFactory(new PropertyValueFactory<>("deleteButton"));

        saveDataTableView.getColumns().add(type);
        saveDataTableView.getColumns().add(productName);
        saveDataTableView.getColumns().add(itemName);
        saveDataTableView.getColumns().add(stock);
        saveDataTableView.getColumns().add(view);
        saveDataTableView.getColumns().add(undoBtn);

        for(StockData stockData : stockDataList){
            String stockValue = stockData.getNewItem().getStock() + " ";
            if(stockData.getNewItem().getStock() > stockData.getOldItem().getStock()
                    && stockData.getType() == StockData.Type.UPDATE_STOCK){
                stockValue += " (+" + (stockData.getNewItem().getStock() - stockData.getOldItem().getStock() + ")");
            }else{
                stockValue += " (" + (stockData.getNewItem().getStock() - stockData.getOldItem().getStock() + ")");
            }

            if(stockData.getType() == StockData.Type.ADD_ITEM){
                stockValue = "" + stockData.getNewItem().getStock();
            }

            SaveData saveData = new SaveData();
            saveData.setDataType(stockData.getType().toString());
            saveData.setProductName(stockData.getProduct().getName());
            saveData.setItemName(stockData.getNewItem().getName());
            saveData.setStock(stockValue);

            saveDataTableView.getItems().add(saveData);

            saveData.getViewLink().setOnAction(actionEvent -> {
                viewHyperlinkClick(stockDataList.indexOf(stockData));
            });

            saveData.getDeleteButton().setOnAction(actionEvent -> {
                undoBtnClicked(stockDataList.indexOf(stockData));
            });
        }


        saveDataTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        saveCenterScrollPane.setFitToWidth(true);
        saveDataVBox.getChildren().add(saveDataTableView);
    }

    private void viewHyperlinkClick(int index){
        StockData stockData = StockDataFile.getInstance().getStockDataList().get(index);
        System.out.println("Selected stock item UID: " + stockData.getUID() + " : " + stockData.getType());
        Item oldItem = stockData.getOldItem();
        Item newItem = stockData.getNewItem();
        boolean isNewItem;
        if(stockData.getType() == StockData.Type.ADD_ITEM){
            isNewItem = true;
        }else{
            isNewItem = false;
        }

        //don't show old value if new Item is added to the product. Otherwise, show all new value and changes made
        StringBuilder info = new StringBuilder();
        info.append("UID: \t\t" + stockData.getUID());
        info.append("\nID: \t\t" + newItem.getId());
        info.append("\nProduct: \t" + stockData.getProduct().getName());

        info.append("\nItem: \t" + newItem.getName());
        if(!isNewItem && !newItem.getName().equals(oldItem.getName())){
            info.append("  (" + oldItem.getName() + ")");
        }
        info.append("\nPage: \t" + newItem.getPages());
        if(!isNewItem && newItem.getPages() != oldItem.getPages()){
            info.append("  (" + oldItem.getPages() + ")");
        }
        info.append("\nPackage: \t" + newItem.getPackageSize());
        if(!isNewItem && newItem.getPackageSize() != oldItem.getPackageSize()){
            info.append("  (" + oldItem.getPackageSize() + ")");
        }
        info.append("\nPrice: \t" + newItem.getPrice());
        if(!isNewItem && newItem.getPrice() != oldItem.getPrice()){
            info.append("  (" + oldItem.getPrice() + ")");
        }
        info.append("\nStock: \t" + newItem.getStock());
        if(!isNewItem && newItem.getStock() != oldItem.getStock()){
            info.append("  (" + oldItem.getStock() + ")");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION, info.toString() ,ButtonType.OK);
        alert.setTitle(null);
        alert.setGraphic(null);
        if(stockData.getType() == StockData.Type.UPDATE_STOCK){
            alert.setHeaderText("Stock update");
        }else if(stockData.getType() == StockData.Type.EDIT_ITEM){
            alert.setHeaderText("Item edited");
        }else if(stockData.getType() == StockData.Type.ADD_ITEM){
            alert.setHeaderText("New item added");
        }else if(stockData.getType() == StockData.Type.DELETE_ITEM){
            alert.setHeaderText("Item deleted");
        }
        alert.showAndWait();
    }

    private void undoBtnClicked(int index){
        StockData stockData = StockDataFile.getInstance().getStockDataList().get(index);
        System.out.println("delete stockData: " + stockData.getNewItem().getName());

        Controller.undoChanges(stockData);
        StockDataFile.getInstance().deleteStockData(stockData);
        processResult();
        saveDataTableView.refresh();
    }

    public TextField getInputName(){
        return inputName;
    }

    public BooleanExpression proceed(){
        BooleanBinding emptyInput;
        emptyInput = Bindings.createBooleanBinding(
                () -> inputName.getText().isEmpty(),
                inputName.textProperty()
        );

        return emptyInput;
    }

}
