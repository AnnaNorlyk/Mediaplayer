package com.example.mediaplayer;

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
                    "SELECT tblPlaylistContent.fldPlaylistOrder, tblMedia.fldFilePath FROM tblPlaylistContent JOIN tblMedia ON tblPlaylistContent.fldMediaId = tblMedia.fldMediaId WHERE tblPlaylistContent.fldPlaylistId = 1 ORDER BY tblPlaylistContent.fldPlaylistOrder ");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            ResultSet resultSet = preparedStatement.executeQuery();

            // Create a list to hold Media objects
            List<Media> playlist = new ArrayList<>();

            // Iterate over the result set and add Media objects to the playlist
            while (resultSet.next()) {
                String filePath = resultSet.getString("fldFilePath");

                // Build the path to the location of the media file
                String absolutePath = new File(filePath).getAbsolutePath();

                // Create new Media object (the actual media content)
                Media media = new Media(new File(absolutePath).toURI().toString());

                playlist.add(media);
            }

            // Create new MediaPlayer
            mp = new MediaPlayer(playlist.get(0));
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

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    @FXML
    private void onMenuClick () {

    }

    @FXML
    //Handles mouse click event on listview and displays of mp4 name on Label
    public void handleNameClick() {
        String selectedName = listviewName.getSelectionModel().getSelectedItem().toString();
        nameTitleLabel.setText(selectedName);
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

    public static void printDbPlaylist() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareCall("SELECT * FROM tblPlaylistContent JOIN tblMedia ON tblPlaylistContent.fldMediaId = tblMedia.fldMediaId JOIN tblAuthor ON TblMedia.fldAuthorId = tblAuthor.fldAuthorId WHERE tblPlaylistContent.fldPlaylistId = 1 ORDER BY tblPlaylistContent.fldPlaylistOrder");

        System.out.println("Playliste:");
        try {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int column1Value = resultSet.getInt("fldPlaylistOrder");
                String column2Value = resultSet.getString("fldTitle").trim();
                String column3Value = resultSet.getString("fldAuthorName");

                System.out.println("" + column1Value + ". " + column2Value + " by " + column3Value);
            }
            resultSet.close();
            System.out.println("");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}