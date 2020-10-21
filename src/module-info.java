module InventoryManagementFX {

    requires javafx.fxml;
    requires javafx.controls;
    requires java.sql;

    opens com.parulenterprise.main;
    opens com.parulenterprise.ui;
    opens com.parulenterprise.db;
    opens com.parulenterprise.model;
}