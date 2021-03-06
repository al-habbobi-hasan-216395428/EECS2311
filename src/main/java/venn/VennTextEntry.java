package venn;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.UUID;

import static venn.VennEntryHandler.getWebColor;

public class VennTextEntry extends Region {
    @SerializedName("d")
    @Expose
    StringProperty data;
    @SerializedName("l")
    @Expose
    EntryLocations location;
    
    Region draggable;
    HBox pane;
    VennSection section;
    @SerializedName("x")
    @Expose
    double xCoordinate;
    @SerializedName("y")
    @Expose
    double yCoordinate;

    @SerializedName("i")
    @Expose
    String id;

    @SerializedName("c")
    @Expose
    Color draggableColor;

    public VennTextEntry(String data) {
        super();
        this.data = new SimpleStringProperty(data);

        this.id = UUID.randomUUID().toString();

        this.section = null;

        // the location where it is after creation
        this.location = EntryLocations.Draggable;

        this.xCoordinate = -1;
        this.yCoordinate = -1;
        
        this.draggable = null;

        this.draw();
    }

    public void setLocation(EntryLocations location) {
        this.location = location;
    }

    public void setDraggable () {
        StackPane pane = new StackPane();
        Label label = new Label("");
        label.textProperty().bind(this.data);
        label.setTextFill(Color.BLACK);
        label.setStyle("-fx-font-weight: bold;");

        pane.getStyleClass().add("rounded-label");

        if (this.draggableColor == null) this.draggableColor = VennEntryHandler.generateColour();

        pane.setStyle("-fx-background-color: " + getWebColor(this.draggableColor));

        Tooltip tooltip = new Tooltip();
        tooltip.textProperty().bind(this.data);
        Tooltip.install(label, tooltip);

        pane.getChildren().add(label);

        this.initClickHandlers(pane);

        // store the x
        pane.widthProperty().addListener((obs, oldValue, newValue) -> {
            this.positionDraggable(this.xCoordinate, this.yCoordinate, true, false);
        });
        pane.layoutXProperty().addListener((obs, oldValue, newValue) -> {
            this.xCoordinate = (double) newValue;
        });
        pane.layoutYProperty().addListener((obs, oldValue, newValue) -> {
            this.yCoordinate = (double) newValue;
        });

        this.draggable = pane;
    }

    private void initClickHandlers (Pane pane) {
        pane.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)){
                if (event.getClickCount() == 2){
                    VennEntryModalHandler.edit(this.data);
                }
            }
        });
    }

    public void positionDraggable (double x, double y, boolean isCenterCoordinate, boolean doCentreY) {
        // if centered around this point, subtract to get the min

        double centerX = x;
        double centerY = y;
        if (isCenterCoordinate) {
            centerX -= (draggable.getWidth() / 2);
            if (doCentreY) centerY -= (draggable.getHeight() / 2);
        }
        draggable.relocate(centerX, centerY);

        this.xCoordinate = x;
    }

    public HBox draw () {
        Label text = new Label();
        text.textProperty().bind(this.data);

        this.pane = new HBox();
        this.pane.setAlignment(Pos.CENTER);
        this.pane.getChildren().addAll(text);

        this.pane.setPadding(new Insets(5));
        this.pane.getStyleClass().add("el-default");
        
        this.pane.setPrefWidth(200);

        this.pane.setUserData(this);
        
        Tooltip tooltip = new Tooltip();
        tooltip.textProperty().bind(this.data);
        Tooltip.install(this.pane, tooltip);

        return this.pane;
    }
}
