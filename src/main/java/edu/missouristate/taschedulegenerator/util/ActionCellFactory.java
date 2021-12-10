/*
 * MIT License
 * 
 * Copyright (c) 2021 Missouri State University
 * 
 * See LICENSE in the project's root directory for full license details
 * 
 */
package edu.missouristate.taschedulegenerator.util;

import java.util.function.Consumer;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

/**
 * Utility class that easily adds edit and remove buttons to a table.
 * 
 * SDD 3.2.1
 * 
 * @author Noah Geren, Keegan Maynard
 *
 * @param <T> The type of the table the cell factory is being added to. Also used for edit and remove callbacks.
 */
public class ActionCellFactory<T> implements Callback<TableColumn<T, Void>, TableCell<T, Void>> {

    private final Consumer<T> editCallback;
    private final Consumer<T> removeCallback;

    /**
     * Creates a new action cell factory that can be added to a table column using column.addCellFactory()
     * @param editCallback The callback function for when the edit button is clicked.
     * @param removeCallback The callback function for when the remove button is clicked.
     */
    public ActionCellFactory(final Consumer<T> editCallback, final Consumer<T> removeCallback) {
        this.editCallback = editCallback;
        this.removeCallback = removeCallback;
    }

    @Override
    public TableCell<T, Void> call(TableColumn<T, Void> param) {
        return new ActionCell(editCallback, removeCallback);
    }

    /**
     * Provides the rendering functions for the action column table cells.
     * 
     * @author Noah Geren, Keegan Maynard
     *
     */
    private class ActionCell extends TableCell<T, Void> {
        
    	/**
    	 * The horizontal spacing between the buttons.
    	 */
        private static final double SPACING = 5d;

        /**
         * Root component that stores the buttons
         */
        private final HBox root = new HBox();
        
        public ActionCell(final Consumer<T> editCallback, final Consumer<T> removeCallback) {
            super();
            
            root.setSpacing(SPACING);
            root.setAlignment(Pos.CENTER);
            
            // Setup the edit callback
            final Button edit = new Button("Edit");
            edit.setOnAction((e) -> {
                editCallback.accept(getTableView().getItems().get(getIndex()));
            });
            
            // Setup the remove callback
            final Button remove = new Button("Remove");
            remove.setOnAction((e) -> {
                removeCallback.accept(getTableView().getItems().get(getIndex()));
            });
            root.getChildren().addAll(edit, remove);
        }

        @Override
        public void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            // Render the buttons
            if (empty) {
                setGraphic(null);
            } else {
                setGraphic(root);
            }
        }

    }

}
