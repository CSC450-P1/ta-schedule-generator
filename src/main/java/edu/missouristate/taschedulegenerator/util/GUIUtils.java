package edu.missouristate.taschedulegenerator.util;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

public class GUIUtils {

	public static void autoResizeColumns(TableView<?> table) {
		// Set the right policy
		table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		ObservableList<? extends TableColumn<?, ?>> columns = table.getColumns();
		DoubleBinding usedWidth = new SimpleDoubleProperty(0).add(0);
		for (int col = 0; col < columns.size(); col++) {
			TableColumn<?, ?> column = columns.get(col);
			// Minimal width = columnheader
			Text t = new Text(column.getText());
			double max = t.getLayoutBounds().getWidth();
			for (int i = 0; i < table.getItems().size(); i++) {
				// cell must not be empty
				if (column.getCellData(i) != null) {
					t = new Text(column.getCellData(i).toString());
					double calcwidth = t.getLayoutBounds().getWidth();
					// remember new max-width
					if (calcwidth > max) {
						max = calcwidth;
					}
				}
			}
			if(col == columns.size() - 1) {
				DoubleBinding availableWidth = table.widthProperty().subtract(usedWidth);
				if(max * 1.25 < availableWidth.get()) {
					column.prefWidthProperty().bind(availableWidth);
					return;
				}
			} else {
				usedWidth = usedWidth.add(columns.get(col).widthProperty());
			}
			// set the new max-width with some extra space
			column.setPrefWidth(max * 1.25);
		}
		
	}
	
	public static void showError(String header, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(header);
		alert.setContentText(message);
		alert.showAndWait();
	}

}
