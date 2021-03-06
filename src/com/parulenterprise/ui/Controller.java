package com.parulenterprise.ui;

import com.parulenterprise.background.SaveChangesToFile;
import com.parulenterprise.db.DataSource;
import com.parulenterprise.model.Product;
import com.parulenterprise.model.StockData;
import com.parulenterprise.db.StockDataFile;
import com.parulenterprise.model.Item;
import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Controller {

    @FXML
    private Label saveProgressMessage;

    @FXML
    private TableView<Product> productTableView;

    @FXML
    private VBox centerPaneVBox;

    @FXML
    private ScrollPane centerPaneScrollPane;

    @FXML
    private Button goToTopBtn, goToBottomBtn;

    @FXML
    private ContextMenu itemContextMenu;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private ProgressBar saveProgressBar;

    @FXML
    private HBox bottomHBox;

    private List<Product> products;
    private static List<Item> items;
    private Map<String, TableView<Item>> tableViewMap = new HashMap<>();

    //position properties in scroll pane required to scroll programmatically.
    private Map<String, Double> initialHeightOfProduct = new HashMap<>();
    private double totalHeightOfProducts;

    private static final int FIXED_CELL_SIZE = 25;
    private static final int FIXED_TABLE_HEAD_SIZE = 2;

    public void initialize(){
        //todo: can be moved to a better place. StockData taking up RAM space.
//        StockDataFile.getInstance().loadStockData();

        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                ObservableList list = FXCollections.observableArrayList(StockDataFile.getInstance().getStockDataList());
                bottomHBox.visibleProperty().bind(Bindings.isEmpty(list).not());
            }
        }, 0, 1, TimeUnit.SECONDS);

        saveProgressBar.setVisible(false);
        //because list is already on the top we don't need that button to be visible
        goToTopBtn.setVisible(false);

        goToTopBtn.setOnMouseClicked( mouseEvent -> {
            centerPaneScrollPane.setVvalue(0);
        });

        goToBottomBtn.setOnMouseClicked( mouseEvent -> {
            centerPaneScrollPane.setVvalue(1);
        });

        centerPaneScrollPane.vvalueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                if(centerPaneScrollPane.getVvalue() == 0){
                    goToTopBtn.setVisible(false);
                }else{
                    goToTopBtn.setVisible(true);
                }

                if(centerPaneScrollPane.getVvalue() == 1){
                    goToBottomBtn.setVisible(false);
                }else{
                    goToBottomBtn.setVisible(true);
                }
            }
        });

        productTableView.setOnMouseClicked( mouseEvent -> {
            String productSelected = productTableView.getSelectionModel().getSelectedItems().get(0).getName();
            double position = initialHeightOfProduct.get(productSelected);
            centerPaneScrollPane.setVvalue(position / totalHeightOfProducts);

            //this block of code is to get focus on the item tables when a product is selected
            for (Node node : centerPaneVBox.getChildren()) {
                if (node instanceof TitledPane) {
                    if (((TitledPane) node).getText().equals(productSelected)) {
                        ((TitledPane) node).setAnimated(false);
                        ((TitledPane) node).setExpanded(true);
                        ((TitledPane) node).setAnimated(true);
                        Node node1 = ((TitledPane) node).getContent();
                        if (node1 instanceof TableView) {
                            node1.requestFocus();
                            ((TableView) node1).getSelectionModel().selectFirst();
                        }
                    }
                }
            }
        });

        TableColumn stockProductColumn = productTableView.getColumns().get(1);
        customizeProductTable(stockProductColumn);

    }

    private void addContextMenuToItemTable(){
        //todo: bug fix: don't show context menu when list(table) is empty
        for(Product product : products) {
            final Item[] item = new Item[1];
            tableViewMap.get(product.getName()).setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
//                    System.out.println(tableViewMap.get(product.getName()).getSelectionModel().getSelectedItem().getName());
                    item[0] = tableViewMap.get(product.getName()).getSelectionModel().getSelectedItem();
                }
            });

            //Add context menu on items table
            itemContextMenu = new ContextMenu();
            MenuItem updateStockMenuItem = new MenuItem("Update stock");
            MenuItem editItemMenuItem = new MenuItem("Edit item..");
            MenuItem addNewItemMenuItem = new MenuItem("Add new item");
            MenuItem deleteItemMenuItem = new MenuItem("Delete item");

            item[0] = tableViewMap.get(product.getName()).getSelectionModel().getSelectedItem();
            updateStockMenuItem.setOnAction(actionEvent -> {
                updateItemStock(item[0], product);
            });

            editItemMenuItem.setOnAction(actionEvent -> {
                editSelectedItem(item[0], product);
            });

            addNewItemMenuItem.setOnAction(actionEvent -> {
                addNewItem(item[0], product);
            });

            deleteItemMenuItem.setOnAction(actionEvent -> {
                deleteSelectedItem(item[0], product);
            });

            itemContextMenu.getItems().addAll(updateStockMenuItem, editItemMenuItem, addNewItemMenuItem, deleteItemMenuItem);
            tableViewMap.get(product.getName()).setContextMenu(itemContextMenu);
        }

    }

    //Loading the center tables i.e item(Stock)TableView
    public void loadItems(){
        //clearing screen (VBox) when method is called again. Also clearing tableView attribute - height.
        centerPaneVBox.getChildren().clear();
        initialHeightOfProduct.clear();
        totalHeightOfProducts = 0;

        if(items == null) {
            items = new ArrayList<>();
            items = DataSource.getInstance().getItems();
        }
        if(products == null) {
            this.products = DataSource.getInstance().getProducts();
        }
//        Map<String, Integer> productStock = DataSource.getInstance().getProductStock();

        for(Product product : products){
            //created a map of tableView to record context menu for each table individually
            tableViewMap.put(product.getName(), new TableView<>());

            //add all products to the center pane
            TitledPane titledPane = new TitledPane();
            titledPane.setText(product.getName());
            titledPane.setPadding(new Insets(6));

            TableColumn<Item, String> columnID = new TableColumn<>("ID");
            columnID.setCellValueFactory(new PropertyValueFactory<>("id"));

            TableColumn<Item, String> columnName = new TableColumn<>("Name");
            columnName.setCellValueFactory(new PropertyValueFactory<>("name"));

            TableColumn<Item, Integer> columnPages = new TableColumn<>("Pages");
            columnPages.setCellValueFactory(new PropertyValueFactory<>("pages"));

            TableColumn<Item, Integer> columnStock = new TableColumn<>("Stock");
            columnStock.setCellValueFactory(new PropertyValueFactory<>("stock"));


            tableViewMap.get(product.getName()).getColumns().add(columnID);
            tableViewMap.get(product.getName()).getColumns().add(columnName);
            tableViewMap.get(product.getName()).getColumns().add(columnPages);
            tableViewMap.get(product.getName()).getColumns().add(columnStock);

            for(Item item : items){
                if(item.getProductID().equals(product.getId())){
                    tableViewMap.get(product.getName()).getItems().add(item);
                }
            }

            //set height of table view to number of elements in the table 25 is the cell height
            //add(2) is adding the title in preferred height of the table view
            tableViewMap.get(product.getName()).setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tableViewMap.get(product.getName()).setFixedCellSize(FIXED_CELL_SIZE);
            tableViewMap.get(product.getName()).prefHeightProperty().bind(tableViewMap.get(product.getName()).fixedCellSizeProperty()
                    .multiply(Bindings.size(tableViewMap.get(product.getName()).getItems()).add(FIXED_TABLE_HEAD_SIZE)));

            initialHeightOfProduct.put(product.getName(), totalHeightOfProducts);
            //don't add height in scroll bar of last item from the table
            if(centerPaneVBox.getChildren().size() != (products.size() - 1)) {
                totalHeightOfProducts = totalHeightOfProducts +
                        (FIXED_TABLE_HEAD_SIZE + tableViewMap.get(product.getName()).prefHeightProperty().get());
            }

            titledPane.setContent(tableViewMap.get(product.getName()));
            centerPaneVBox.getChildren().add(titledPane);
            customizeItemTable(columnStock);

            tableViewMap.get(product.getName()).getSortOrder().add(columnID);
        }

        centerPaneScrollPane.setFitToWidth(true);
        centerPaneScrollPane.setContent(centerPaneVBox);

        addContextMenuToItemTable();
    }

    private void customizeItemTable(TableColumn<Item, Integer> tableColumn){
        tableColumn.setCellFactory( tableColumn1 -> {
            return new TableCell<>(){
                @Override
                protected void updateItem(Integer integer, boolean b) {
                    super.updateItem(integer, b);
                    if(!isEmpty()){

                        //updating color to item stock
                        if(integer < 200 && integer > 50){
                            Font font = Font.getDefault();
                            setTextFill(Color.ORANGE);
                            setFont(Font.font(font.getName(), FontWeight.BOLD, font.getSize()));
                        }else if(integer <= 50){
                            Font font = Font.getDefault();
                            setTextFill(Color.RED);
                            setFont(Font.font(font.getName(), FontWeight.BOLD, font.getSize()));
                        }else{
                            setFont(Font.getDefault());
                        }
                        setText("" + integer);
                    }
                }
            };
        });
    }

    private void customizeProductTable(TableColumn<Product, Integer> tableColumn){
        tableColumn.setCellFactory( column -> {
            return new TableCell<Product, Integer>(){
                @Override
                protected void updateItem(Integer integer, boolean b) {
                    super.updateItem(integer, b);
                    if(!isEmpty()){
                        if(integer < 200 && integer > 50){
                            Font font = Font.getDefault();
                            setTextFill(Color.ORANGE);
                            setFont(Font.font(font.getName(), FontWeight.BOLD, font.getSize()));
                        }else if(integer <= 50){
                            Font font = Font.getDefault();
                            setTextFill(Color.RED);
                            setFont(Font.font(font.getName(), FontWeight.BOLD, font.getSize()));
                        }else{
                            setFont(Font.getDefault());
                        }
                        setText("" + integer);
                    }
                }
            };
        });
    }

    private void updateItemStock(Item item, Product product){
        Item oldItem = new Item(item);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("updateStockDialog.fxml"));
        try{
            dialog.getDialogPane().setContent(loader.load());
        }catch (IOException e){
            System.out.println("Couldn't load the dialog add new stock: " + e.getMessage());
        }
        UpdateStockDialog updateStockDialog = loader.getController();

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.APPLY);

        //binding disable property for apply button
        Node applyBtn = dialog.getDialogPane().lookupButton(ButtonType.APPLY);
        applyBtn.disableProperty().bind(updateStockDialog.proceed());

        updateStockDialog.processResult(item, product);

        Optional<ButtonType> result = dialog.showAndWait();

        if(result.get() == ButtonType.APPLY){
            int newStock = Integer.parseInt(updateStockDialog.getStockValueTextField().getText());
            item.setStock(item.getStock() + newStock);
            for (Item item1 : items) {
                if(item1.getId().equals(item.getId())){
                    item1.setStock(item.getStock());
                }
            }
            tableViewMap.get(product.getName()).refresh();
            refreshProductTableView(product);
            //creating StockData
            String note = updateStockDialog.getNotes();
            LocalDate stockDate = updateStockDialog.getUpdateStockDate();
            StockData stockData = new StockData(stockDate, StockData.Type.UPDATE_STOCK, product, oldItem, item, note);
            StockDataFile.getInstance().addStockData(stockData);
        }
        if(result.get() == ButtonType.CANCEL){

        }
    }

    private void editSelectedItem(Item item, Product product){
        Item oldItem = new Item(item);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("editItemDialog.fxml"));
        try{
            dialog.getDialogPane().setContent(loader.load());
        }catch (IOException e){
            System.out.println("Couldn't load the dialog add new stock: " + e.getMessage());
        }
        EditItemDialog editItemDialog = loader.getController();

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.APPLY);

        //binding disable property for apply button
        Node applyBtn = dialog.getDialogPane().lookupButton(ButtonType.APPLY);
        applyBtn.disableProperty().bind(editItemDialog.proceed());

        editItemDialog.processResult(item);

        Optional<ButtonType> result = dialog.showAndWait();
        if(result.get() == ButtonType.APPLY){
            Item editedItem = editItemDialog.getItem();
            //editing item
            item.setName(editedItem.getName());
            item.setPages(editedItem.getPages());
            item.setPackageSize(editedItem.getPackageSize());
            item.setPrice(editedItem.getPrice());
            item.setStock(editedItem.getStock());

            tableViewMap.get(product.getName()).refresh();
            refreshProductTableView(product);
            //creating stockData and saving item both on file
            String note = editItemDialog.getNote();
            LocalDate editDate = editItemDialog.getEditItemDate();
            StockData stockData = new StockData(editDate, StockData.Type.EDIT_ITEM, product, oldItem, editedItem, note);
            StockDataFile.getInstance().addStockData(stockData);
        }
    }

    private void addNewItem(Item item, Product product){
        Item oldItem = new Item(item);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("addNewItemDialog.fxml"));
        try{
            dialog.getDialogPane().setContent(loader.load());
        }catch (IOException e){
            System.out.println("Couldn't load the dialog add new stock: " + e.getMessage());
        }
        AddNewItemDialog addNewItemDialog = loader.getController();

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.APPLY);

        //binding disable property for apply button
        Node applyBtn = dialog.getDialogPane().lookupButton(ButtonType.APPLY);
        applyBtn.disableProperty().bind(addNewItemDialog.proceed());

        addNewItemDialog.processResult(item);

        Optional<ButtonType> result = dialog.showAndWait();
        if(result.get() == ButtonType.APPLY){

            Item newItem = addNewItemDialog.getItem();
            for (Item item1 : items) {
                if(newItem.getId().equals(item1.getId()) || newItem.getName().equals(item1.getName())){
                    String errorText = "Error! Unable to add new Item with ID: " + newItem.getId() + " & name: " + item1.getName();
                    Alert alert = new Alert(Alert.AlertType.ERROR, errorText,
                            ButtonType.OK);
                    alert.setTitle("Please try again");
                    alert.showAndWait();
                    return;
                }
            }

            items.add(newItem);

            loadItems();
            tableViewMap.get(product.getName()).refresh();
            refreshProductTableView(product);

            //creating StockData
            String note = addNewItemDialog.getNote();
            LocalDate editDate = addNewItemDialog.getEditItemDate();
            StockData stockData = new StockData(editDate, StockData.Type.ADD_ITEM, product, oldItem, newItem, note);
            StockDataFile.getInstance().addStockData(stockData);
        }
    }

    private void deleteSelectedItem(Item item, Product product){
        Item oldItem = new Item(item);

        String contextText = product.getName() + ": " + item.getName();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, contextText +" ?",
                ButtonType.YES, ButtonType.CANCEL);
        alert.setTitle("CONFIRM DELETE?");
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            item.setStock(0);
            items.remove(item);

            loadItems();
            refreshProductTableView(product);

            //creating StockData
            StockData stockData = new StockData(LocalDate.now(), StockData.Type.DELETE_ITEM, product, oldItem, item, "");
            StockDataFile.getInstance().addStockData(stockData);
        }
    }

    private void refreshProductTableView(Product selectedProduct){
        int productStock = 0;
        for(Item item : items){
            if(item.getProductID().equals(selectedProduct.getId())){
                productStock += item.getStock();
            }
        }

        for(Product product : productTableView.getItems()){
            if(product.getName().equals(selectedProduct.getName())){
                product.setStock(productStock);
            }
        }
        productTableView.refresh();
    }

    //when user clicks undo in saveChangesDialog
    public static void undoChanges(StockData stockData){
        Item oldItem = stockData.getOldItem();
        for(Item item : items){
            if(stockData.getType() == StockData.Type.DELETE_ITEM){
                items.add(oldItem);
                System.out.println("Old item added back:" + oldItem.getName());
                return;
            }
            if(stockData.getType() == StockData.Type.ADD_ITEM){
                if(stockData.getNewItem() != null) {
                    items.remove(stockData.getNewItem());
                }else{
                    System.out.println("Error correction needed");
                }
                return;
            }
            if(item.getId().equals(oldItem.getId())){
                int index = items.indexOf(item);
                items.set(index, oldItem);
                System.out.println("old stock: " + items.get(index).getStock());
            }
        }
    }

    private FadeTransition createFadeTransition(Node node, int seconds){
        FadeTransition fade = new FadeTransition(Duration.seconds(seconds), node);
        fade.setFromValue(1);
        fade.setToValue(0);

        return fade;
    }

    @FXML
    public void listProducts(){
//        productTableView.setItems((ObservableList<Product>) products);
        Task<ObservableList<Product>> task = new GetAllProductsTask();
        productTableView.itemsProperty().bind(task.valueProperty());
        new Thread(task).start();
    }

    //method: apply changes when save button is clicked, before closing application and on CTRL + S
    @FXML
    public void saveOnFile(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader loader = new FXMLLoader();

        loader.setLocation(getClass().getResource("saveChangesDialog.fxml"));
        try{
            dialog.getDialogPane().setContent(loader.load());
        }catch (IOException e){
            System.out.println("Couldn't load the dialog add new stock: " + e.getMessage());
        }

        SaveChangesDialog saveChangesDialog = loader.getController();
        saveChangesDialog.processResult();

        ButtonType cancelBtn = new ButtonType("Go back", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType saveBtn = new ButtonType("Save changes", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().add(cancelBtn);
        dialog.getDialogPane().getButtonTypes().add(saveBtn);

        Node applyBtn = dialog.getDialogPane().lookupButton(saveBtn);
        applyBtn.disableProperty().bind(saveChangesDialog.proceed());

        dialog.showAndWait();

        try {
            if (dialog.getResult() == saveBtn) {
                String inputName = saveChangesDialog.getInputName().getText();
                for(StockData sd : StockDataFile.getInstance().getStockDataList()){
                    sd.setInputName(inputName);
                }
                //save all stockData before executing task. (task removes stockData after database is updated)
                StockDataFile.getInstance().saveStockData();
                SaveChangesToFile task = new SaveChangesToFile();
                Thread thread = new Thread(task);
                thread.start();
                saveProgressBar.setVisible(true);
                saveProgressBar.progressProperty().bind(task.progressProperty());
                task.setOnRunning(e -> {
                    saveProgressMessage.setText("saving...");
                });
                task.setOnSucceeded(e -> {
                    saveProgressBar.setVisible(false);
                    saveProgressMessage.setText("Saved!");
                    FadeTransition fade = createFadeTransition(saveProgressMessage, 3);
                    fade.play();
                });
                task.setOnFailed(e -> {
                    saveProgressBar.setVisible(false);
                    saveProgressMessage.setText("failed! Please try again.");
                    saveProgressMessage.setTextFill(Color.RED);
                    FadeTransition fade = createFadeTransition(saveProgressMessage, 5);
                    fade.play();
                });
            } else if(dialog.getResult() == cancelBtn){

            }
        }finally {
            loadItems();
        }
    }
}

class GetAllProductsTask extends Task<ObservableList<Product>>{

    @Override
    protected ObservableList<Product> call() throws Exception {
        return FXCollections.observableArrayList(DataSource.getInstance().getProducts());
    }
}