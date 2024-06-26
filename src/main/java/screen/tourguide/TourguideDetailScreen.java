package screen.tourguide;

import controller.UserController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Pair;
import model.Location;
import model.Tour;
import screen.user.TrackingScreen;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ResourceBundle;
import java.util.StringJoiner;

public class TourguideDetailScreen implements Initializable {

    private Tour tour;
    @FXML
    private Text description;

    @FXML
    private Text emailTxt;

    @FXML
    private ImageView hearderImg;

    @FXML
    private VBox locationList;

    @FXML
    private Text locationTxtDetail;

    @FXML
    private Text nameTxt;

    @FXML
    private Text priceTxtDetail;

    @FXML
    private Text statusTxt;

    @FXML
    private HBox titleHbox;

    @FXML
    private Text titleTxtDetail;

    @FXML
    private VBox tourguideInfo;

    @FXML
    private Button btnTrack;

    public void setTour(Tour tour) throws SQLException {
        this.tour = tour;
        titleTxtDetail.setText(tour.getTourName());
        description.setText(tour.getDescription());
        priceTxtDetail.setText("Price: " + tour.getTotalCost() + " $");
        statusTxt.setText(tour.getStatus().toString());
        if (tour.getStatus().equals(Tour.Status.CONFIRMED)) {
            statusTxt.setStyle("-fx-text-fill: green");
            btnTrack.setVisible(true);
            btnTrack.setOnAction(event -> {
                BorderPane tourguideView = (BorderPane) ((Node) event.getSource()).getScene().lookup("#tourguideView");
                ScrollPane view = null;
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tourguide/tracking.fxml"));
                    view = loader.load();
                    TrackingGuideScreen controller = loader.getController();
                    controller.setTour(tour);
                    tourguideView.setCenter(view);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else if (tour.getStatus().equals(Tour.Status.PENDING)) {
            statusTxt.setStyle("-fx-text-fill: blue");
                tourguideInfo.setVisible(false);
        }  else if (tour.getStatus().equals(Tour.Status.CANCELLED)) {
            statusTxt.setStyle("-fx-text-fill: red");
        } else if (tour.getStatus().equals(Tour.Status.COMPLETED)) {
            statusTxt.setStyle("-fx-text-fill: gray");
        } else {
            statusTxt.setStyle("-fx-text-fill: orange");
            tourguideInfo.setVisible(false);
        }

        emailTxt.setText(new UserController().getById(tour.getTourId()).getEmail());
        nameTxt.setText(new UserController().getById(tour.getTourId()).getName());

        try {
            hearderImg.setImage(new Image(new FileInputStream(tour.getLocations().getFirst().getKey().getImageUrl())));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        StringJoiner locationJoiner = new StringJoiner(", ");
        for (Pair<Location, Timestamp> pair : tour.getLocations()){
            HBox hBox = createHBox(pair);
            locationList.getChildren().add(hBox);
            locationJoiner.add(pair.getKey().getName());
        }
        String locationStr = locationJoiner.toString();
        locationTxtDetail.setText(locationStr);



    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public HBox createHBox(Pair<Location, Timestamp> pair) {
        // Create Text nodes
        Text dateTimeText = new Text(String.valueOf(pair.getValue()));
        dateTimeText.setFont(new Font(14));

        Text locationText = new Text(pair.getKey().getName());
        locationText.setFont(new Font(14));

        Text descriptionText = new Text(pair.getKey().getDescription());
        descriptionText.setFont(new Font(14));

        // Create HBox and add Text nodes
        HBox hBox = new HBox(20);
        hBox.getChildren().addAll(dateTimeText, locationText, descriptionText);

        // Set padding for HBox
        hBox.setPadding(new Insets(0, 0, 0, 20));

        return hBox;
    }
}

