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

    private MediaPlayer mp;
    private Media me;
    boolean isPlaying = false;

    /**
     * This method is invoked automatically in the beginning. Used for initializing, loading data etc.
     *
     * @param location
     * @param resources
     */
    public void initialize(URL location, ResourceBundle resources){
        // Build the path to the location of the media file
        String path = new File("src/main/media/dontstopme.mp4").getAbsolutePath();
        // Create new Media object (the actual media content)
        me = new Media(new File(path).toURI().toString());
        // Create new MediaPlayer and attach the media to be played
        mp = new MediaPlayer(me);
        mediaV.setMediaPlayer(mp);

        // Set fitWidth and fitHeight to the AnchorPane's width and height
        mediaV.fitWidthProperty().bind(((Pane) mediaV.getParent()).widthProperty());
        mediaV.fitHeightProperty().bind(((Pane) mediaV.getParent()).heightProperty());

        mp.setAutoPlay(false);
        volumeSlider.setValue(mp.getVolume() * 100);
        volumeSlider.valueProperty().addListener(observable -> {
            mp.setVolume(volumeSlider.getValue() / 100);
            int volumePercentValue = (int) volumeSlider.getValue();
            volumeLabel.setText(volumePercentValue + "%");
        });

    }
    @FXML
    private void onMenuClick () {

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