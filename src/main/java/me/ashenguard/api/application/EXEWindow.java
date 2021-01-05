package me.ashenguard.api.application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import me.ashenguard.api.WebReader;
import me.ashenguard.api.utils.Version;

import java.util.ArrayList;
import java.util.List;

public class EXEWindow extends Application {

    static final ImageView X_MARK = new ImageView("https://img.techpowerup.org/200325/xmark.png");
    static final ImageView CHECK_MARK = new ImageView("https://img.techpowerup.org/200325/check.png");
    static {
        X_MARK.setFitHeight(20);
        X_MARK.setFitWidth(20);
        CHECK_MARK.setFitHeight(20);
        CHECK_MARK.setFitWidth(20);
    }

    private final String name;
    private final Image logo;
    private final Version version;
    private final Version latest;
    private final String page;


    protected List<PLDependency> dependencies = new ArrayList<>();
    protected String discord = null;

    private GridPane layout = new GridPane();

    private Button dependencyURLButton = new Button();
    private Button discordButton = new Button();
    private Button updateButton = new Button();

    TableView<PLDependency> dependencyTableView = new TableView<>();

    public EXEWindow(String name, int spigotID, Version version) {
        this.name = name;
        this.logo = new Image("https://www.spigotmc.org/data/resource_icons/" + (spigotID / 1000) + "/" + spigotID + ".jpg");
        this.version = version;
        this.page = "https://www.spigotmc.org/resources/" + spigotID + "/";
        this.latest = new Version(new WebReader("https://api.spigotmc.org/legacy/update.php?resource=" + spigotID).read());
    }


    @Override
    public void start(Stage stage) {
        layout.setVgap(8);
        layout.setHgap(10);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.setAlignment(Pos.CENTER);

        setupButtons();
        setupDependencyTableView();

        Scene scene = new Scene(layout, 420, 420);
        stage.setTitle(String.format("%s-%s", name , version.toString(true)));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.getIcons().add(logo);
        stage.show();
    }

    private void setupButtons() {
        dependencyURLButton.setDisable(true);
        dependencyURLButton.setText("Download Plugin");
        dependencyURLButton.setOnMouseClicked(event -> {
            PLDependency selected = dependencyTableView.getSelectionModel().getSelectedItem();
            dependencyTableView.getSelectionModel().clearSelection();
            dependencyURLButton.setDisable(true);

            if (selected == null) return;
            getHostServices().showDocument(selected.getUrl());
        });
        layout.add(dependencyURLButton, 0, 1);

        discordButton.setText("Support Discord");
        discordButton.setDisable(discord == null || discord.isEmpty());
        discordButton.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && discord != null && !discord.isEmpty()) getHostServices().showDocument(discord);
        });
        layout.add(discordButton, 1, 1);

        updateButton.setText("Update Plugin");
        updateButton.setDisable(latest.isHigher(version));
        updateButton.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) getHostServices().showDocument(page);
        });
        layout.add(updateButton, 2, 1);
    }

    private void setupDependencyTableView() {
        ObservableList<PLDependency> dependencyObservableList = FXCollections.observableArrayList(dependencies);
        TableColumn<PLDependency, String> pluginTableColumn = new TableColumn<>("Requirements");
        pluginTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        pluginTableColumn.setCellFactory(this::pluginCallback);
        pluginTableColumn.setMinWidth(200);
        pluginTableColumn.setStyle( "-fx-alignment: CENTER-LEFT;");
        TableColumn<PLDependency, String> versionTableColumn = new TableColumn<>("Required Version");
        versionTableColumn.setCellValueFactory(new PropertyValueFactory<>("version"));
        versionTableColumn.setMinWidth(200);
        versionTableColumn.setStyle( "-fx-alignment: CENTER-LEFT;");

        dependencyTableView.setItems(dependencyObservableList);
        dependencyTableView.getColumns().add(pluginTableColumn);
        dependencyTableView.getColumns().add(versionTableColumn);
        dependencyTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        dependencyTableView.setOnMouseClicked(this::dependencyTableViewClickHandler);
        layout.add(dependencyTableView, 0, 0, 3, 1);
        dependencyTableView.setFixedCellSize(40);
    }

    private TableCell<PLDependency, String> pluginCallback(TableColumn<PLDependency, String> param) {
        return new TableCell<PLDependency, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                PLDependency dependency = PLDependency.find(dependencies, item);

                if (item == null || dependency == null || empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setGraphic(dependency.getLogo());
                    setText(item);
                }
                this.setItem(item);
            }
        };
    }

    private void dependencyTableViewClickHandler(MouseEvent event) {
        PLDependency selection = dependencyTableView.getSelectionModel().getSelectedItem();
        dependencyURLButton.setDisable(selection == null || !selection.isLinked());
    }
}
