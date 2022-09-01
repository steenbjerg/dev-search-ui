package dk.stonemountain.business.ui.util.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

public class IconHelper {
	private static final Logger log = LoggerFactory.getLogger(IconHelper.class);
	
	public static void patchIconPath(Button btn) {
		log.debug("Button graphics: {}", btn.getGraphic());
		if (btn.getGraphic() instanceof ImageView) {
			btn.setGraphic(patchIconPath((ImageView) btn.getGraphic()));
		}
	}
	
	public static ImageView patchIconPath(ImageView v) {
		if (v.getImage() != null) {
			log.debug("Image Url: {}, {}, {}", v.getImage().getUrl(), v.getFitWidth(), v.getFitHeight());
			String resourceUrl = "/icons" + v.getImage().getUrl().substring(v.getImage().getUrl().lastIndexOf("/"));
			log.debug("Now using: {}", resourceUrl);
			ImageView v2 = new ImageView(IconHelper.class.getResource(resourceUrl).toString());
			v2.setFitWidth(v.getFitWidth());
			v2.setFitHeight(v.getFitHeight());
			v2.setPreserveRatio(v.isPreserveRatio());
			return v2;
		} else {
			return v;
		}
	}

}
