package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Edge;
import util.ValidateInput;

public class ChangeLabelPopupController extends AbstractController implements Initializable {

	@FXML
	private TextField txtValueLabel;
	@FXML
	private Button btnChangeLabel;

	private Stage stage = null;
	private Edge edge;
	private String value;

	public ChangeLabelPopupController() {

	}

	public ChangeLabelPopupController(Edge edge) {
		super();
		this.edge = edge;

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		ValidateInput.onlyUserInputNumerics(txtValueLabel);

		btnChangeLabel.setOnAction((event) -> {
			value = txtValueLabel.getText();
			value.matches("^[a-zA-Z0-9]*$");
			if (value.length() == 0) {
				value = "0";//set the string is 0 when user input nothing
			} 
			edge.setTextWeight(value);
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

	public Edge getResult() {
		return this.edge;
	}

}
