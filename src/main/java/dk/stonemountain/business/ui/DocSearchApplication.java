package dk.stonemountain.business.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class DocSearchApplication extends Application {
    private static final Logger log = LoggerFactory.getLogger(DocSearchApplication.class);
    private static DocSearchApplication mainApp = null;
    
    public static DocSearchApplication getApplication() {
    	return mainApp;
    }
    
    public static void main(String[] args) {
        launch("No Arguments");
    }

    public void quit() {
    	log.info("Application is shut down");
    	Platform.exit();
    }
    
    @Override
    public void start(Stage stage) throws Exception {
    	log.info("Application is starting up");
    	DocSearchApplication.mainApp = this; // NOSONAR
    	log.trace("JFX start");
        
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/application.fxml"));
        Parent rootNode = loader.load();

        log.trace("Showing JFX scene");
        Scene scene = new Scene(rootNode, 800, 800);

        scene.getStylesheets().add("dark.css");
        // JMetro jmetro = new JMetro(Style.DARK);
        // jmetro.setScene(scene);

        // DEBUG - start
        Screen.getScreens().stream()
        	.forEach(s -> log.debug("screen: {}", s.getVisualBounds()));
        log.debug("Primary screen: {}", Screen.getPrimary());
        // DEBUG - end

        ApplicationContainer.getInstance().start();
        
        log.trace("Image: {}, {}", this.getClass().getResource("/icons/java_64.png"), this.getClass().getResourceAsStream("/icons/java_64.png"));
        stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/icons/java_64.png")));
        stage.setTitle("Stonemountain Demo FX applications");
        stage.setScene(scene);
        stage.show();
    }
}
