package dk.stonemountain.business.ui.about;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;
import javafx.stage.Window;

public class InfoDialog extends Dialog<Void> {
	private static final Logger logger = LoggerFactory.getLogger(ShowDataDialog.class);

	@FXML private WebView content;
    
	private final String url;
	private final String header;
	private final String title;
	
	public InfoDialog(Window owner, String url, String title, String header) {
		logger.debug("Info showing {}", url);
		this.url = url;
		this.header = header;
		this.title = title;

		Node node;
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setController(this);
			loader.setLocation(this.getClass().getResource("/fxml/info-dialog.fxml"));
			node = loader.load(this.getClass().getResourceAsStream("/fxml/info-dialog.fxml"));
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
		pane.setHeaderText(header);

		setResizable(false);
		setTitle(title);
		setGraphic(new ImageView(this.getClass().getResource("/icons/info_64_icon.png").toString()));
		setHeaderText(header);
		setDialogPane(pane);

		Button okButton = (Button) pane.lookupButton(ButtonType.OK);
		// okButton.setGraphic(new ImageView(this.getClass().getResource("/icons/tick_32.png").toString()));
		okButton.setText("Close");

		// content.getEngine().setUserStyleSheetLocation(getClass().getResource("/htmlstyle.css").toString());
		content.getEngine().load(url);
		logger.info("Showing {}", url);
//		setResultConverter(dialogButton -> dialogButton == ButtonType.OK ? authenticationData : null);
				 
		Platform.runLater(() -> okButton.requestFocus());
	}
}