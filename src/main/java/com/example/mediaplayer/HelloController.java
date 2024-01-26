package com.example.mediaplayer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;


import java.io.File;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    @FXML
    private MediaView mediaV;
    @FXML
    private Button playButton;

    @FXML
    private Button forwardSkipButton;
    @FXML
    private Button backSkipButton;
    @FXML
    private MenuButton menuButton;
    @FXML
    private Label nameTitleLabel;

    @FXML
    private Label sourceLabel;
    @FXML
    private Slider volumeSlider;
    @FXML
    private ListView listviewName;
    @FXML
    private ListView listViewSource;
    @FXML
    private MenuButton addToMenu;
    @FXML
    private Label volumeLabel;
    @FXML
    private TextField searchbar;

    private MediaPlayer mp;
    private Media me;
    private static Connection connection;
    boolean isPlaying = false;


    /**
     * This method is invoked automatically in the beginning. Used for initializing, loading data etc.
     *
     * @param location
     * @param resources
     */
    public void initialize(URL location, ResourceBundle resources){
        // creating connection String
        String url = "jdbc:sqlserver://localhost:1433;databaseName=dbMp4";

        Properties properties = new Properties();

        properties.setProperty("user", "sa");
        properties.setProperty("password", "1234");
        properties.setProperty("encrypt", "false");

        // create connection
        try {
            connection = DriverManager.getConnection(url, properties);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareCall(
                    "SELECT tblPlaylistContent2.fldPlaylistOrder, tblMedia.fldFilePath FROM tblPlaylistContent2 JOIN tblMedia ON tblPlaylistContent2.fldMediaId = tblMedia.fldMediaId WHERE tblPlaylistContent2.fldPlaylistId = 1 ORDER BY tblPlaylistContent2.fldPlaylistOrder ");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            ResultSet resultSet = preparedStatement.executeQuery();

            // Create new MediaPlayer
            String path = new File("src/main/media/dontstopme.mp4").getAbsolutePath();
            // Create new Media object (the actual media content)
            me = new Media(new File(path).toURI().toString());

            mp = new MediaPlayer(me);
            mediaV.setMediaPlayer(mp);

            // Set fitWidth and fitHeight to the AnchorPane's width and height
            mediaV.fitWidthProperty().bind(((Pane) mediaV.getParent()).widthProperty());
            mediaV.fitHeightProperty().bind(((Pane) mediaV.getParent()).heightProperty());

            mp.setAutoPlay(false);

            // Set up volume slider and label
            volumeSlider.setValue(mp.getVolume() * 100);
            volumeSlider.valueProperty().addListener(observable -> {
                mp.setVolume(volumeSlider.getValue() / 100);
                int volumePercentValue = (int) volumeSlider.getValue();
                volumeLabel.setText(volumePercentValue + "%");
            });

            // Gets the whole mediafolder, creates a list and puts them as ListView items
            File folder = new File("./src/main/media");
            File[] listOfFiles = folder.listFiles();

            ObservableList<String> items = FXCollections.observableArrayList();


            for (File file : listOfFiles) {
                // Check if it's actually a file and then if it is a mp4
                if (file.isFile() && file.getName().endsWith(".mp4"))
                {
                    items.add(file.getName().substring(0,file.getName().length()-4));
                }
            }
            listviewName.setItems(items);


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    @FXML
    private void onMenuClick () {

    }

    @FXML
    private void selectPlaylistOne() {
        selectPlaylist(1);
    }

    @FXML
    private void selectPlaylistTwo() {
        selectPlaylist(2);
    }

    @FXML
    private void selectPlaylist(int playlistNr) {
        try {
            PreparedStatement preparedStatement = connection.prepareCall(
                    "SELECT tblPlaylistContent2.fldPlaylistOrder, tblMedia.fldFilePath FROM tblPlaylistContent2 JOIN tblMedia ON tblPlaylistContent2.fldMediaId = tblMedia.fldMediaId WHERE tblPlaylistContent2.fldPlaylistId = ? ORDER BY tblPlaylistContent2.fldPlaylistOrder ");

            // Set parameter value
            preparedStatement.setInt(1, playlistNr);
            ResultSet resultSet = preparedStatement.executeQuery();

            ObservableList<String> items = FXCollections.observableArrayList();

            while (resultSet.next()) {
                String filePath = resultSet.getString("fldFilePath");

                // Add filePath to the ObservableList
                items.add(filePath);
            }

            // Set the items to the ListView
            listviewName.setItems(items);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void addToPlaylist() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO tblPlaylistContent2 (fldMediaId, fldPlaylistId, fldPlaylistOrder) VALUES (?,?,?)");
        preparedStatement.setInt(1, 1);
        preparedStatement.setInt(2, 1);
        preparedStatement.setInt(3, 10);

        try {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) { //slet det her når det virker
                System.out.println("Sang tilføjet");
            } else {
                System.out.println("Der opstod en fejl");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    //Handles mouse click event on listview and displays of mp4 name on Label
    public void handleNameClick() {
        String selectedName = listviewName.getSelectionModel().getSelectedItem().toString().trim();
        nameTitleLabel.setText(selectedName);

        handleStop();
        // Create new MediaPlayer
        String path = new File("src/main/media/" + selectedName + ".mp4").getAbsolutePath();
        // Create new Media object (the actual media content)
        System.out.println(path);
        me = new Media(new File(path).toURI().toString());
        mp = new MediaPlayer(me);
        mediaV.setMediaPlayer(mp);
        handlePlay();
    }
    @FXML
    //Handles mouse click event on listview and displays name of source on Label
    public void handleSourceClick() {
        String selectedName = listviewName.getSelectionModel().getSelectedItem().toString();
        sourceLabel.setText(selectedName);
    }

    @FXML
    //Handles event on searchbar
    public void handleSearchbar() {
        String searchbarInput = searchbar.getText().toLowerCase();

    }

    @FXML
    private void handlePlay()
    {
        if (isPlaying) {
            mp.pause();
            playButton.setText("Play");
            isPlaying = false;
        } else {
            mp.play();
            playButton.setText("Pause");
            isPlaying = true;
        }

    }

    @FXML
    private void handleStop()
    {
        mp.stop();
        isPlaying = false;
    }

}