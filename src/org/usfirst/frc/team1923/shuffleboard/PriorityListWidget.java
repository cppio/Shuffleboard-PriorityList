package org.usfirst.frc.team1923.shuffleboard;

import edu.wpi.first.shuffleboard.api.widget.ComplexAnnotatedWidget;
import edu.wpi.first.shuffleboard.api.widget.Description;
import edu.wpi.first.shuffleboard.api.widget.ParametrizedController;

import org.usfirst.frc.team1923.shuffleboard.SendablePriorityListData;
import org.usfirst.frc.team1923.shuffleboard.SendablePriorityListType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    private static enum Preset {
        ScaleFirst("Scale", "Switch", "Cross The Line"),
        SwitchFirst("Switch", "Scale", "Cross The Line"),
        CrossTheLine("Cross The Line"),
        DoNothing("Do Nothing"),
        Custom();

        private final String[] order;

        Preset(String... order) {
            this.order = order;
        }

        public String[] getOrder() {
            return this.order.clone();
        }
    }

    private static <T> boolean beginsWith(List<T> list, T[] array) {
        for (int i = 0; i < array.length; ++i) {
            if (!list.get(i).equals(array[i])) {
                return false;
            }
        }
        return true;
    }

    private final Property<Preset> preset = new SimpleObjectProperty<>(this, "Preset", Preset.CrossTheLine);
    private void recalculatePreset() {
        for (Preset i : Preset.values()) {
            if (beginsWith(this.listView.getItems(), i.getOrder())) {
                this.preset.setValue(i);
                return;
            }
        }
        preset.setValue(Preset.Custom);
    }

    @FXML private Pane root;
    @FXML private ListView<String> listView;

    @FXML private void initialize() {
        this.listView.setCellFactory(param -> new DraggableCell());
        this.preset.addListener((value, oldValue, newValue) -> {
            if (newValue == Preset.Custom) {
                recalculatePreset();
            } else {
                String[] order = newValue.getOrder();
                if (!beginsWith(this.listView.getItems(), order)) {
                    for (int i = 0; i < order.length; ++i) {
                        this.listView.getItems().remove(order[i]);
                        this.listView.getItems().add(i, order[i]);
                    }
                }
            }
        });
        this.dataOrDefault.addListener((value, oldValue, newValue) -> {
            this.listView.getItems().setAll(newValue.getItems()); // TODO
        });
        this.listView.setSelectionModel(new NoSelectionModel());
        this.listView.getItems().setAll(this.dataOrDefault.get().getItems());
        exportProperties(preset);
    }

    private DraggableCell last;

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
                    recalculatePreset();
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
                    recalculatePreset();
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
