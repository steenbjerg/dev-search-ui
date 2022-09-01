package dk.stonemountain.business.ui.about;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.stage.Window;

public class ShowDataDialog extends Dialog<Void> {
	private static final Logger logger = LoggerFactory.getLogger(ShowDataDialog.class);

	@FXML private Label dataViewHeader;
	@FXML private TableView<Map.Entry<String, String>> dataView;
    @FXML private TableColumn<Map.Entry<String, String>, String> dataViewKeyColumn;
    @FXML private TableColumn<Map.Entry<String, String>, String> dataViewValueColumn;
    
	private final Map<String, String> data;
	private final String dataHeader;
	
	public ShowDataDialog(Window owner, Map<String, String> data, String dataHeader) {
		this.data = data;
		this.dataHeader = dataHeader;
		
		Node node;
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setController(this);
			loader.setLocation(this.getClass().getResource("/fxml/show-map-dialog.fxml"));
			node = loader.load(this.getClass().getResourceAsStream("/fxml/show-map-dialog.fxml"));
		} catch (IOException e) {
			throw new RuntimeException("Failed to load fxml", e);
		}
		initOwner(owner);
		getDialogPane().setContent(node);
	}

	@FXML
	public void initialize() {
		logger.trace("Dialog initialize");
		final DialogPane pane = getDialogPane();
		pane.getButtonTypes().addAll(ButtonType.OK);
		pane.setHeaderText("Viewing Report Data");

		setResizable(false);
		setTitle("Viewing Data");
		setGraphic(new ImageView(this.getClass().getResource("/icons/report_64.png").toString()));
		setHeaderText("View Report Data");
		setDialogPane(pane);

		Button okButton = (Button) pane.lookupButton(ButtonType.OK);
		okButton.setGraphic(new ImageView(this.getClass().getResource("/icons/tick_32.png").toString()));
		okButton.setText("");

//		setResultConverter(dialogButton -> dialogButton == ButtonType.OK ? authenticationData : null);
		
		dataViewKeyColumn.setCellValueFactory(l -> new SimpleStringProperty(l.getValue().getKey()));
		dataViewKeyColumn.setCellFactory(TextFieldTableCell.forTableColumn());

		dataViewValueColumn.setCellValueFactory(l -> new SimpleStringProperty(l.getValue().getValue()));
		dataViewValueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		
		dataView.setItems(FXCollections.observableArrayList(data.entrySet()));
		dataViewHeader.setText(dataHeader);
		 
		Platform.runLater(() -> okButton.requestFocus());
	}
}