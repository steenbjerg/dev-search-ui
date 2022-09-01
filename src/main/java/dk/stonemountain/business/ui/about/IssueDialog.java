package dk.stonemountain.business.ui.about;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.stonemountain.business.ui.DocSearchApplication;
import dk.stonemountain.business.ui.util.gui.ClientRuntime;
import dk.stonemountain.business.ui.util.gui.DialogHelper;
import dk.stonemountain.business.ui.util.gui.IconHelper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Window;

public class IssueDialog extends Dialog<Void> {
	private static final Logger logger = LoggerFactory.getLogger(IssueDialog.class);

    @FXML private ListView<String> attachments;
    @FXML private TextField buildTime;
    @FXML private TextArea details;
    @FXML private CheckBox includeEnvironment;
    @FXML private CheckBox includeLog;
    @FXML private CheckBox includeSystemProperties;
    @FXML private TextField log;
    @FXML private TextField user;
    @FXML private TextField version;
    @FXML private TextField gitSha;
	@FXML private Button attachButton;
	@FXML private Button viewEnvironmentButton;
	@FXML private Button viewLogButton;
	@FXML private Button viewPropertiesButton;

    private final Map<String, String> env;
    private final Map<String, String> props = new HashMap<>();
    
	public IssueDialog(Window owner) {
		Node node;
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setController(this);
			loader.setLocation(this.getClass().getResource("/fxml/issue-dialog.fxml"));
			node = loader.load(this.getClass().getResourceAsStream("/fxml/issue-dialog.fxml"));
		} catch (IOException e) {
			throw new RuntimeException("Failed to load fxml", e);
		}
		initOwner(owner);
		getDialogPane().setContent(node);
		
		env = System.getenv();
		System.getProperties().entrySet().stream().forEach(e -> props.put(e.getKey() != null ? e.getKey().toString() : "null", e.getValue() != null ? e.getValue().toString() : "null"));
	}

	@FXML
	public void initialize() {
		logger.trace("IssueDialog initialize");
		final DialogPane pane = getDialogPane();
		pane.getButtonTypes().addAll(ButtonType.OK);
		pane.setHeaderText("Report Issue");

		setResizable(false);
		setTitle("Report Issue");
		setGraphic(new ImageView(this.getClass().getResource("/icons/report_64.png").toString()));
		setHeaderText("Report Issue");
		setDialogPane(pane);

		Button okButton = (Button) pane.lookupButton(ButtonType.OK);
		okButton.setGraphic(new ImageView(this.getClass().getResource("/icons/send_16.png").toString()));
		okButton.setText("Send Issue Report");
		okButton.addEventFilter(ActionEvent.ACTION, event -> {
			logger.info("Sending data");
		});

//		setResultConverter(dialogButton -> dialogButton == ButtonType.OK ? authenticationData : null);
		
		user.setText(ProcessHandle.current().info().user().orElseGet(() -> "unknown"));
		log.setText(ClientRuntime.getApplicationLog());
		version.setText(ClientRuntime.getApplicationVersion());
		gitSha.setText(ClientRuntime.getApplicationGitSha());
		buildTime.setText(ClientRuntime.getApplicationBuildTime());

		IconHelper.patchIconPath(attachButton);
		IconHelper.patchIconPath(viewEnvironmentButton);
		IconHelper.patchIconPath(viewLogButton);
		IconHelper.patchIconPath(viewPropertiesButton);

		setWidth(800);
		Platform.runLater(() -> okButton.requestFocus());
	}
    @FXML
    void doAttach(ActionEvent event) {
    	Optional<File> file = DialogHelper.getOpenFile(getOwner(), "Select file to attach");
    	if (file.isPresent()) {
    		attachments.getItems().add(file.get().getPath());
    	}
    }

    @FXML
    void doViewEnvironment(ActionEvent event) {
    	new ShowDataDialog(getDialogPane().getScene().getWindow(), env, "Environment Variables:").showAndWait();
    }

    @FXML
    void doViewLog(ActionEvent event) {
		File logFile = new File(ClientRuntime.getApplicationLog());
		logger.trace("View log file: {}", logFile.toURI().toString());
		DocSearchApplication.getApplication().getHostServices().showDocument(logFile.toURI().toString());
    }

    @FXML
    void doViewSystemProperties(ActionEvent event) {
    	new ShowDataDialog(getDialogPane().getScene().getWindow(), props, "System Properties:").showAndWait();
   }
}