package com.example.mediaplayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class HelloApplication extends Application {

    private Connection connection;

    public HelloApplication() throws SQLException {
        // creating connection String
        String url = "jdbc:sqlserver://localhost:1433;databaseName=dbMp4";

        Properties properties = new Properties();

        properties.setProperty("user", "sa");
        properties.setProperty("password", "1234");
        properties.setProperty("encrypt", "false");

        // create connection
        connection = DriverManager.getConnection(url, properties);
    }

    //setting stage
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root, 800, 1280);
        stage.setTitle("Mediaplayer");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}