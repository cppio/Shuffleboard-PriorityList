package org.usfirst.frc.team1923.shuffleboard;

import edu.wpi.first.shuffleboard.api.widget.ComplexAnnotatedWidget;
import edu.wpi.first.shuffleboard.api.widget.Description;
import edu.wpi.first.shuffleboard.api.widget.ParametrizedController;

import org.usfirst.frc.team1923.shuffleboard.SendablePriorityListData;
import org.usfirst.frc.team1923.shuffleboard.SendablePriorityListType;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.ListCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

@Description(
    name = "Priority List",
    summary = "A drag-and-drop-enabled Priority List",
    dataTypes = SendablePriorityListType.class
)
@ParametrizedController("PriorityListWidget.fxml")
public class PriorityListWidget extends ComplexAnnotatedWidget<SendablePriorityListData> {

    private void updateOrder() {
        this.setData(new SendablePriorityListData(this.listView.getItems()));
    }

    @FXML private Pane root;
    @FXML private ListView<String> listView;

    @FXML private void initialize() {
        this.listView.setCellFactory(param -> new DraggableCell());
        this.dataOrDefault.addListener((value, oldValue, newValue) -> {
            this.listView.getItems().setAll(newValue.getItems());
        });
        this.listView.setSelectionModel(new NoSelectionModel());
    }

    private static class NoSelectionModel extends MultipleSelectionModel<String> {
        @Override public ObservableList<Integer> getSelectedIndices() {
            return FXCollections.emptyObservableList();
        }
        @Override public ObservableList<String> getSelectedItems() {
            return FXCollections.emptyObservableList();
        }

        @Override public void selectAll() {}
        @Override public void selectFirst() {}
        @Override public void selectIndices(int index, int... indices) {}
        @Override public void selectLast() {}
        @Override public void selectNext() {}
        @Override public void selectPrevious() {}
        @Override public void clearSelection() {}
        @Override public void clearSelection(int index) {}
        @Override public void select(String obj) {}
        @Override public void select(int index) {}
        @Override public void clearAndSelect(int index) {}

        @Override public boolean isEmpty() {
            return true;
        }
        @Override public boolean isSelected(int index) {
            return false;
        }
    }

    @Override public Pane getView() {
        return this.root;
    }

    private DraggableCell last;
    private static final Border EMPTY_BORDER = new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2, 0, 0, 0)));
    private static final Border INSERT_BORDER = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2, 0, 0, 0)));
    private class DraggableCell extends ListCell<String> {

        public DraggableCell() {
            super();

            this.setBorder(EMPTY_BORDER);

            this.setOnDragDetected(event -> {
                String item = this.getItem();
                if (item != null) {
                    ClipboardContent content = new ClipboardContent();
                    content.putString(this.getIndex() + " " + item);

                    Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
                    dragboard.setContent(content);

                    Point2D point = this.sceneToLocal(event.getSceneX(), event.getSceneY());
                    dragboard.setDragView(this.snapshot(null, null), this.getWidth() / 2 - point.getX(), point.getY() - this.getHeight() / 2);

                    this.getListView().getItems().remove(item);
                    event.consume();
                }
            });
            this.setOnDragDropped(event -> {
                Dragboard dragboard = event.getDragboard();
                event.setDropCompleted(dragboard.hasString());
                if (dragboard.hasString()) {
                    this.getListView().getItems().add(Math.min(this.getIndex(), this.getListView().getItems().size()), dragboard.getString().split(" ", 2)[1]);
                    updateOrder();
                    dragboard.setContent(null);
                }
                event.consume();
            });
            this.setOnDragEntered(event -> {
                if (this.getListView().getItems().size() > this.getIndex()) {
                    this.setBorder(INSERT_BORDER);
                } else {
                    last.setBorder(INSERT_BORDER);
                }
                event.consume();
            });
            this.setOnDragExited(event -> {
                if (this.getListView().getItems().size() > this.getIndex()) {
                    this.setBorder(EMPTY_BORDER);
                } else {
                    last.setBorder(EMPTY_BORDER);
                }
                event.consume();
            });
            this.setOnDragDone(event -> {
                Dragboard dragboard = event.getDragboard();
                if (dragboard.hasString()) {
                    String[] values = dragboard.getString().split(" ", 2);
                    this.getListView().getItems().add(Integer.parseInt(values[0]), values[1]);
                    updateOrder();
                }
                event.consume();
            });
            this.setOnDragOver(event -> {
                event.acceptTransferModes(TransferMode.MOVE);
                event.consume();
            });
        }

        @Override public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (this.getListView().getItems().size() == this.getIndex()) {
                last = this;
            }
            this.setText(item);
        }
    }

}
