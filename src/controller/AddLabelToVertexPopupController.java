package controller;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Edge;
import model.Vertex;
import util.ValidateInput;

public class AddLabelToVertexPopupController extends AbstractController implements Initializable {
	@FXML
	private TextField txtValueLabel;
	@FXML
	private Button btnAddLabelVertex;
	private Stage stage = null;
	private String value="";

	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		

		btnAddLabelVertex.setOnAction((event) -> {
			value = txtValueLabel.getText();
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

	public String getResult() {
		return this.value;
	}

}
