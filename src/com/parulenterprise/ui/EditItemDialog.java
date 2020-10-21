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

public class EditItemDialog {

    @FXML
    private TextField editItemId, editItemName, editItemPages, editItemPackage, editItemPrice, editItemStock;

    @FXML
    private Label showPreviousItemStock;

    @FXML
    private DatePicker editItemDate;

    @FXML
    private Hyperlink addNoteToEditItem;

    @FXML
    private GridPane editItemGridPane;

    @FXML
    private TextField noteTextField;

    private Item oldItem;
    private static final String DATE_PATTERN = "dd-MMM-yyyy";
    private DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    public void processResult(Item item){
        oldItem = item;
        editItemId.setEditable(false);

        editItemDate.setValue(LocalDate.now());
        editItemDate.getEditor().setDisable(true);
        editItemDate.setStyle("-fx-opacity: 1");
        editItemDate.getEditor().setStyle("-fx-opacity: 1");
        editItemDate.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate localDate) {
                return DATE_FORMATTER.format(localDate);
            }

            @Override
            public LocalDate fromString(String s) {
                return LocalDate.parse(s, DATE_FORMATTER);
            }
        });

        editItemId.setText(item.getId());
        editItemName.setText(item.getName());
        editItemPages.setText("" + item.getPages());
        editItemPackage.setText("" + item.getPackageSize());
        editItemPrice.setText("" + item.getPrice());

        showPreviousItemStock.setText("" + item.getStock());

        editItemPages.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("\\d*")){
                editItemPages.setText(oldValue);
            }
        });

        editItemPackage.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("\\d*")){
                editItemPackage.setText(oldValue);
            }
        });

        editItemPrice.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(!newValue.matches("(\\d*)(\\.\\d*)?")){
                editItemPrice.setText(oldValue);
            }
        });

        editItemStock.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("(-)?\\d*")){
                editItemStock.setText(oldValue);
            }
        });

        addNoteToEditItem.setOnAction(actionEvent -> {
            int rowPosition = GridPane.getRowIndex(addNoteToEditItem);
            Label addNote = new Label("Note: ");
            noteTextField = new TextField();
            editItemGridPane.add(addNote, 0, rowPosition);
            editItemGridPane.add(noteTextField, 1, rowPosition);
        });
    }

    public BooleanExpression proceed(){
        BooleanBinding emptyInput;
        emptyInput = Bindings.createBooleanBinding(
                () -> editItemId.getText().isEmpty() ||
                editItemName.getText().isEmpty() ||
                editItemPages.getText().isEmpty() ||
                editItemPackage.getText().isEmpty() ||
                editItemPrice.getText().isEmpty() ||
                editItemStock.getText().isEmpty(),
                editItemId.textProperty(),
                editItemName.textProperty(),
                editItemPages.textProperty(),
                editItemPackage.textProperty(),
                editItemPrice.textProperty(),
                editItemStock.textProperty()
        );

        return emptyInput;
    }

    public Item getItem(){
        Item item = new Item();
        item.setId(editItemId.getText());
        item.setName(editItemName.getText());
        item.setPages(Integer.parseInt(editItemPages.getText()));
        item.setPackageSize(Integer.parseInt(editItemPackage.getText()));
        item.setPrice(Double.parseDouble(editItemPrice.getText()));
        item.setStock(Integer.parseInt(editItemStock.getText()));
        item.setProductID(oldItem.getProductID());

        return item;
    }

    public LocalDate getEditItemDate(){
        return editItemDate.getValue();
    }

    public String getNote(){
        if(noteTextField != null){
            return  noteTextField.getText();
        }
        return "";
    }
}
