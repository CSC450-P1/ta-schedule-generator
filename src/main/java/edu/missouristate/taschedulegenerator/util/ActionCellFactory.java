package edu.missouristate.taschedulegenerator.util;

import java.util.function.Consumer;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class ActionCellFactory<T> implements Callback<TableColumn<T, Void>, TableCell<T, Void>> {

    private final Consumer<T> editCallback;
    private final Consumer<T> removeCallback;

    public ActionCellFactory(final Consumer<T> editCallback, final Consumer<T> removeCallback) {
        this.editCallback = editCallback;
        this.removeCallback = removeCallback;
    }

    @Override
    public TableCell<T, Void> call(TableColumn<T, Void> param) {
        return new ActionCell(editCallback, removeCallback);
    }

    private class ActionCell extends TableCell<T, Void> {
        
        private static final double SPACING = 5d;

        private final HBox root = new HBox();
        
        public ActionCell(final Consumer<T> editCallback, final Consumer<T> removeCallback) {
            super();
            
            root.setSpacing(SPACING);
            root.setAlignment(Pos.CENTER);
            
            final Button edit = new Button("Edit");
            edit.setOnAction((e) -> {
                editCallback.accept(getTableView().getItems().get(getIndex()));
            });
            final Button remove = new Button("Remove");
            remove.setOnAction((e) -> {
                removeCallback.accept(getTableView().getItems().get(getIndex()));
            });
            root.getChildren().addAll(edit, remove);
        }

        @Override
        public void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                setGraphic(root);
            }
        }

    }

}
