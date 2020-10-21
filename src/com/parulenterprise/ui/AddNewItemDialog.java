package com.parulenterprise.ui;

import com.parulenterprise.model.Item;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AddNewItemDialog {

    @FXML
    private TextField addItemId, addItemName, addItemPage, addItemPackage, addItemPrice, addItemStock, noteTextField;

    @FXML
    private DatePicker addItemDate;

    @FXML
    private Hyperlink addNoteToAddItem;

    @FXML
    private GridPane addNewItemGridPane;

    private static final String DATE_PATTERN = "dd-MMM-yyyy";
    private DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private Item oldItem;

    public void processResult(Item item){
        oldItem = new Item(item);
        addItemDate.setValue(LocalDate.now());
        addItemDate.getEditor().setDisable(true);
        addItemDate.setStyle("-fx-opacity: 1");
        addItemDate.getEditor().setStyle("-fx-opacity: 1");
        addItemDate.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate localDate) {
                return DATE_FORMATTER.format(localDate);
            }

            @Override
            public LocalDate fromString(String s) {
                return LocalDate.parse(s, DATE_FORMATTER);
            }
        });

        addItemPage.setText("0");
        addItemPackage.setText("0");
        addItemPrice.setText("0.0");
        addItemStock.setText("0");

        addItemPage.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("\\d*")){
                addItemPage.setText(oldValue);
            }
        });

        addItemPackage.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("\\d*")){
                addItemPackage.setText(oldValue);
            }
        });

        addItemPrice.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(!newValue.matches("(\\d*)(\\.\\d*)?")){
                addItemPrice.setText(oldValue);
            }
        });

        addItemStock.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("(-)?\\d*")){
                addItemStock.setText(oldValue);
            }
        });

        addNoteToAddItem.setOnAction(actionEvent -> {
            int rowPosition = GridPane.getRowIndex(addNoteToAddItem);
            System.out.println("Hyper link at row: " + rowPosition);
            Label addNote = new Label("Note: ");
            noteTextField = new TextField();
            addNewItemGridPane.add(addNote, 0, rowPosition);
            addNewItemGridPane.add(noteTextField, 1, rowPosition);
        });
    }

    public Item getItem(){
        Item item = new Item();
        item.setId(addItemId.getText());
        item.setName(addItemName.getText());
        item.setPages(Integer.parseInt(addItemPage.getText()));
        item.setPackageSize(Integer.parseInt(addItemPackage.getText()));
        item.setPrice(Double.parseDouble(addItemPrice.getText()));
        item.setStock(Integer.parseInt(addItemStock.getText()));
        item.setProductID(oldItem.getProductID());

        return item;
    }

    public LocalDate getEditItemDate(){
        return addItemDate.getValue();
    }

    public BooleanExpression proceed(){
        BooleanBinding emptyInput;
        emptyInput = Bindings.createBooleanBinding(
                () -> addItemId.getText().isEmpty() ||
                        addItemName.getText().isEmpty() ||
                        addItemPage.getText().isEmpty() ||
                        addItemPackage.getText().isEmpty() ||
                        addItemPrice.getText().isEmpty() ||
                        addItemStock.getText().isEmpty(),
                addItemId.textProperty(),
                addItemName.textProperty(),
                addItemPage.textProperty(),
                addItemPackage.textProperty(),
                addItemPrice.textProperty(),
                addItemStock.textProperty()
        );
        return emptyInput;
    }

    public String getNote(){
        if(noteTextField != null){
            return noteTextField.getText();
        }
        return "";
    }

}
