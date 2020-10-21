package com.parulenterprise.ui;

import com.parulenterprise.model.Product;
import com.parulenterprise.model.Item;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UpdateStockDialog {

    @FXML
    private TextField productTextField, itemTextField, stockValueTextField;

    @FXML
    private Label totalStockLabel, nameLabel;

    @FXML
    private DatePicker updateStockDate;

    @FXML
    private Hyperlink addNoteToUpdateStock;

    @FXML
    private GridPane updateStockGridPane;

    @FXML
    private TextField noteTextField;

    private static final String DATE_PATTERN = "dd-MMM-yyyy";
    private DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    public void processResult(Item item, Product product){

        productTextField.setEditable(false);
        itemTextField.setEditable(false);

        updateStockDate.setValue(LocalDate.now());
        updateStockDate.getEditor().setDisable(true);
        updateStockDate.setStyle("-fx-opacity: 1");
        updateStockDate.getEditor().setStyle("-fx-opacity: 1");
        updateStockDate.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate localDate) {
                return DATE_FORMATTER.format(localDate);
            }

            @Override
            public LocalDate fromString(String s) {
                return LocalDate.parse(s, DATE_FORMATTER);
            }
        });

        productTextField.setText(product.getName());
        itemTextField.setText(item.getName());
        totalStockLabel.setText("" + item.getStock());
        int totalStock = Integer.parseInt(totalStockLabel.getText());

        stockValueTextField.requestFocus();
        stockValueTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if(!newValue.matches("(-)?\\d*")){
                    stockValueTextField.setText(oldValue);
                }
                if(!stockValueTextField.getText().isEmpty()) {
                    try {
                        int newStock = Integer.parseInt(stockValueTextField.getText()) + totalStock;
                        totalStockLabel.setText("" + newStock);
                    }catch (NumberFormatException e){}
                }else{
                    totalStockLabel.setText("" + totalStock);
                }
            }
        });

        addNoteToUpdateStock.setOnAction(actionEvent -> {
            int rowPosition = GridPane.getRowIndex(addNoteToUpdateStock);
            Label addNote = new Label("Note: ");
            noteTextField = new TextField();
            updateStockGridPane.add(addNote, 0, rowPosition);
            updateStockGridPane.add(noteTextField, 1, rowPosition);
        });
    }

    //this method will bind disableProperty of apply button in the current dialogPane
    public BooleanExpression proceed(){

        BooleanBinding emptyInput;
        emptyInput = Bindings.createBooleanBinding(
                () -> stockValueTextField.getText().isEmpty(),
                stockValueTextField.textProperty()
        );
        return emptyInput;
    }

    public TextField getStockValueTextField(){
        return stockValueTextField;
    }

    public LocalDate getUpdateStockDate(){
        return updateStockDate.getValue();
    }

    public String getNotes(){
        if(noteTextField != null){
            return noteTextField.getText();
        }
        return "";
    }
}
