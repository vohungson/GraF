package util;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

public class ValidateInput {

	public static boolean userWantToCreate(String typeEdge, String edgeWeight) {
		if (isNullOrEmpty(typeEdge) && isNullOrEmpty(edgeWeight)) {
			return false;
		} else {
			return true;
		}

	}

	public static boolean isNullOrEmpty(String input) {
		if (input != null && !input.isEmpty()) {
			return false;
		} else {
			return true;
		}

	}

	public static void onlyUserInputNumerics(TextField text) {
		text.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
					text.setText(newValue.replaceAll("[^\\d]", ""));
				}
			}
		});

	}

}
