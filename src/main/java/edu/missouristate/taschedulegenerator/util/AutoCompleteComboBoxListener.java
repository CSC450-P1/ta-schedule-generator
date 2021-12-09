package edu.missouristate.taschedulegenerator.util;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Adds auto-complete functionality to a ComboBox. 
 *
 * @param <T> The type of the combobox the auto-complete functionality is being added to.
 */
public class AutoCompleteComboBoxListener<T> implements EventHandler<KeyEvent> {

	/**
	 * The ComboBox auto-complete is being added to.
	 */
	private ComboBox<T> comboBox;
	/**
	 * The data contained in the ComboBox.
	 */
	private ObservableList<T> data;
	/**
	 * Used to update the typing caret position.
	 */
	private boolean moveCaretToPos = false;
	/**
	 * Stores the typing caret position.
	 */
	private int caretPos;
	
	/**
	 * Adds autocomplete to a ComboBox.
	 * @param <T> The type of the ComboBox.
	 * @param comboBox The ComboBox to add auto-complete to
	 */
	public static <T> void addAutoComplete(final ComboBox<T> comboBox) {
		new AutoCompleteComboBoxListener<>(comboBox);
	}

	/**
	 * Creates a new AutoCompleteComboBoxListener.
	 * 
	 * @param comboBox The ComboBox to add auto-complete to.
	 */
	private AutoCompleteComboBoxListener(final ComboBox<T> comboBox) {
		this.comboBox = comboBox;
		data = comboBox.getItems();

		// Make combobox typable
		this.comboBox.setEditable(true);
		// Hide
		this.comboBox.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent t) {
				comboBox.hide();
			}
		});
		// Show with new filtered options
		this.comboBox.setOnKeyReleased(AutoCompleteComboBoxListener.this);
		// If focus changes then show the dropdown if focused and validate if unfocused
		this.comboBox.focusedProperty().addListener(new ChangeListener<>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if(newValue) {
					comboBox.show();
				} else {
					if(!data.contains(comboBox.getEditor().getText())) {
						comboBox.setItems(data);
						comboBox.setValue(null);;
					}
				}
			}
			
		});
	}

	@Override
	public void handle(KeyEvent event) {
		// Handle moving to different options
		if (event.getCode() == KeyCode.UP) {
			caretPos = -1;
			moveCaret(comboBox.getEditor().getText().length());
			return;
		} else if (event.getCode() == KeyCode.DOWN) {
			if (!comboBox.isShowing()) {
				comboBox.show();
			}
			caretPos = -1;
			moveCaret(comboBox.getEditor().getText().length());
			return;
		} else if (event.getCode() == KeyCode.BACK_SPACE) {
			moveCaretToPos = true;
			caretPos = comboBox.getEditor().getCaretPosition();
		} else if (event.getCode() == KeyCode.DELETE) {
			moveCaretToPos = true;
			caretPos = comboBox.getEditor().getCaretPosition();
		}

		if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT || event.isControlDown()
				|| event.getCode() == KeyCode.HOME || event.getCode() == KeyCode.END
				|| event.getCode() == KeyCode.TAB || event.getCode() == KeyCode.ENTER) {
			return;
		}
		// Filter combobox options
		ObservableList<T> list = FXCollections.observableArrayList();
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).toString().toLowerCase()
					.startsWith(AutoCompleteComboBoxListener.this.comboBox.getEditor().getText().toLowerCase())) {
				list.add(data.get(i));
			}
		}
		String t = comboBox.getEditor().getText();

		comboBox.setItems(list);
		comboBox.getEditor().setText(t);
		if (!moveCaretToPos) {
			caretPos = -1;
		}
		moveCaret(t.length());
		if (!list.isEmpty()) {
			comboBox.show();
		}
	}

	/**
	 * Moves the text caret in the input.
	 * 
	 * @param textLength The position to put the caret.
	 */
	private void moveCaret(int textLength) {
		if (caretPos == -1) {
			comboBox.getEditor().positionCaret(textLength);
		} else {
			comboBox.getEditor().positionCaret(caretPos);
		}
		moveCaretToPos = false;
	}

}
