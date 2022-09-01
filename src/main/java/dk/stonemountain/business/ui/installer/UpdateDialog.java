package dk.stonemountain.business.ui.installer;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.stonemountain.business.ui.ApplicationContainer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Window;

public class UpdateDialog extends Dialog<Boolean> {
	private static final Logger logger = LoggerFactory.getLogger(UpdateDialog.class);

    @FXML TextArea releaseNote;
    @FXML TextField releaseTime;
    @FXML TextField version;

	public UpdateDialog(Window owner) {
		Node node;
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setController(this);
			loader.setLocation(this.getClass().getResource("/fxml/version.fxml"));
			node = loader.load(this.getClass().getResourceAsStream("/fxml/version.fxml"));
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
		pane.getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
		pane.setHeaderText("Update DocSearch");

		setResizable(false);
		setTitle("DocSearch: New version ready");
		setGraphic(new ImageView(this.getClass().getResource("/icons/install_64.png").toString()));
		setHeaderText("DocSearch Update");
		setDialogPane(pane);

		Button okButton = (Button) pane.lookupButton(ButtonType.OK);
		okButton.setGraphic(new ImageView(this.getClass().getResource("/icons/install_16.png").toString()));
		okButton.setText("Install");
		
		setResultConverter(dialogButton -> dialogButton == ButtonType.OK ? Boolean.TRUE : null);

		version.setText(ApplicationContainer.getInstance().getVersion().getNewVersion());
		releaseTime.setText(ApplicationContainer.getInstance().getVersion().getNewSha());
		releaseNote.setText(ApplicationContainer.getInstance().getVersion().getNewReleaseNote());

		// IconHelper.patchIconPath(viewLogButton);
		 
		Platform.runLater(() -> okButton.requestFocus());
	}	
}