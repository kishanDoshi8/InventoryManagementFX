package com.parulenterprise.main;

import com.parulenterprise.db.DataSource;
import com.parulenterprise.db.StockDataFile;
import com.parulenterprise.ui.Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(Controller.class.getResource("mainWindow.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        controller.listProducts();
        controller.loadItems();

        primaryStage.setOnCloseRequest(windowEvent -> {
            if(!StockDataFile.getInstance().getStockDataList().isEmpty()){
                ButtonType exitBtn = new ButtonType("Exit", ButtonBar.ButtonData.OK_DONE);
                ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                ButtonType saveBtn = new ButtonType("Save changes", ButtonBar.ButtonData.APPLY);
                Alert closingAlert = new Alert(Alert.AlertType.WARNING, "Changes made will not be saved. Are you sure you want to exit?",
                        exitBtn, cancelBtn, saveBtn);
                Stage stage = (Stage) closingAlert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image("file:pe_logo.png"));
                closingAlert.setHeaderText(null);
                closingAlert.setTitle("Confirm Exit");
                closingAlert.showAndWait();

                if(closingAlert.getResult() == exitBtn){
                    closeApplication();
                }else if(closingAlert.getResult() == cancelBtn){
                    windowEvent.consume();
                }else if(closingAlert.getResult() == saveBtn){
                    windowEvent.consume();
                    controller.saveOnFile();
                }
            }else {
                closeApplication();
            }
        });

        primaryStage.setTitle("Parul Enterprise - Inventory");
        primaryStage.setScene(new Scene(root, 800, 600));
//        primaryStage.getIcons().add(new Image(Controller.class.getResourceAsStream("com/parulenterprise/ui/pe_logo.png")));
        primaryStage.getIcons().add(new Image("file:pe_logo.png"));
        primaryStage.show();
    }

    public void closeApplication(){
        DataSource.getInstance().close();
        System.out.println("Closing application");
        Platform.exit();
        System.exit(0);
    }

    @Override
    public void init() throws Exception {
        super.init();
        if(!DataSource.getInstance().open()){
            System.out.println("Could not connect to the database");
            Platform.exit();
            System.exit(-10);
        }

    }

    @Override
    public void stop() throws Exception {
        DataSource.getInstance().close();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}