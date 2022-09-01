package dk.stonemountain.business.ui.util.gui;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.stonemountain.business.ui.ApplicationContainer;
import dk.stonemountain.business.ui.UserPreferences;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.Window;

public class DialogHelper {
	private static final String DEMO_ICON = "/icons/message_64.png";
	private static final Logger logger = LoggerFactory.getLogger(DialogHelper.class);
	private static final String SYSTEM_FAILURE_MESSAGE = "System Failure. Exception message: ";

	private DialogHelper() {
		// preventing instance creation
	}
	
	public static boolean showConfirmationDialog(Window owner, String contentText, String errorLogMessage, Action action) {
		try {
			Alert confirmDialog = new Alert(AlertType.CONFIRMATION);
			confirmDialog.setResizable(true);
			confirmDialog.setWidth(500);
			confirmDialog.setHeight(400);
			confirmDialog.getDialogPane().setMinWidth(450);
			confirmDialog.getDialogPane().setMinHeight(150);
			confirmDialog.setContentText(contentText);
			confirmDialog.initOwner(owner);
			
			((Stage) confirmDialog.getDialogPane().getScene().getWindow()).getIcons().add(new Image(DialogHelper.class.getResource(DEMO_ICON).toString()));
			
			Optional<ButtonType> buttonType = confirmDialog.showAndWait();
			if (buttonType.isPresent() && buttonType.get() == ButtonType.OK) {
				action.execute();
				return true;
			}
		} catch (Exception e) {
			logger.info(errorLogMessage, e);
			Alert errorDialog = new Alert(AlertType.ERROR);
			errorDialog.setContentText(SYSTEM_FAILURE_MESSAGE + e.getMessage());
			errorDialog.showAndWait();
		}
		return false;
	}
	
	
	public static void showDialogWithContent(Window owner, AlertType type, String title, String header, String content, String errorLogMessage) {
		try {
			Alert alert = new Alert(type);
			alert.setTitle(title);
			alert.setHeaderText(header);
			alert.setContentText(content);
			alert.setResizable(true);
			alert.setWidth(1000);
			alert.setHeight(800);
			alert.getDialogPane().setMinWidth(900);
			alert.getDialogPane().setMinHeight(300);
			alert.initOwner(owner);
			
			((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image(DialogHelper.class.getResource(DEMO_ICON).toString()));
			alert.showAndWait();
		} catch (Exception e) {
			logger.info(errorLogMessage, e);
			Alert errorDialog = new Alert(AlertType.ERROR);
			errorDialog.setContentText(SYSTEM_FAILURE_MESSAGE + e.getMessage());
			errorDialog.showAndWait();
		}
	}
	
	public static void showInformationDialog(Window owner, String title, String header, String content, String errorLogMessage) {
		showDialogWithContent(owner, AlertType.INFORMATION, title, header, content, errorLogMessage);
	}

	public static void showErrorDialog(Window owner, String title, String header, String content, String errorLogMessage) {
		showDialogWithContent(owner, AlertType.ERROR, title, header, content, errorLogMessage);
	}
	
	public static Optional<File> getOpenFile(Window window, String title, ExtensionFilter...extensionFilters) {
		UserPreferences prefs = ApplicationContainer.getInstance().getUserPreferences();
		Path storedLocation = prefs.getFileLocation().orElseGet(() -> Paths.get(System.getProperty("user.home")));

		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(storedLocation.toFile());
		fileChooser.setTitle(title);
		fileChooser.getExtensionFilters().addAll(extensionFilters);
		File selectedFile = fileChooser.showOpenDialog(window);
		if (selectedFile != null) {
			prefs.putFileLocation(selectedFile.toPath());
			return Optional.of(selectedFile);
		} else {
			return Optional.empty();
		}
	}
}
