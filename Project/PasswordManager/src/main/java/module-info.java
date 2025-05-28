module main.passwordmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires javafx.web;

    opens passwordmanager.icons;
    opens passwordmanager to javafx.fxml;
    opens passwordmanager.controllers to javafx.fxml;
    opens passwordmanager.models to javafx.fxml;

    exports passwordmanager;
    exports passwordmanager.controllers;
    exports passwordmanager.models;
    exports passwordmanager.dto;
    opens passwordmanager.dto to javafx.fxml;
}