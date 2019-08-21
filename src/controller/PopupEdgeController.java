package controller;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import util.ValidateInput;

public class PopupEdgeController extends AbstractController implements Initializable {
	@FXML
	private TextField txtEdgeWeight;
	@FXML
	private Button btnDirected;
	@FXML
	private Button btnUndirected;
	private Stage stage = null;
	private HashMap<String, Object> result = new HashMap<String, Object>();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		ValidateInput.onlyUserInputNumerics(txtEdgeWeight);
		btnDirected.setOnAction((event) -> {
			result.clear();
			result.put("typeEdge", "directed");
			result.put("weight", txtEdgeWeight.getText());

			closeStage();
		});

		btnUndirected.setOnAction((event) -> {
			result.clear();
			result.put("typeEdge", "undirected");
			result.put("weight", txtEdgeWeight.getText());
			closeStage();
		});

	}

	/**
	 * setting the stage of this view
	 * 
	 * @param stage
	 */
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	/**
	 * Closes the stage of this view
	 */
	private void closeStage() {
		if (stage != null) {
			stage.close();
		}
	}

	public HashMap<String, Object> getResult() {
		return this.result;
	}

}
