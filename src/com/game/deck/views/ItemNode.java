package com.game.deck.views;

import com.game.deck.equipment.Item;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.TransferMode;

public class ItemNode extends ImageView{
    public static final double ITEM_SIZE = 37.5;
    private final Item item;

    ItemNode(Item item){
        this.item = item;
        
        Image image = loadImage(item.getAssetPath());
        if (image == null) {
            image = loadImage("assets/items/ego_relic.png");
        }

        setFitWidth(ITEM_SIZE);
        setFitHeight(ITEM_SIZE);
        setPreserveRatio(true);
        setSmooth(false);
        setImage(image);
        
        Tooltip tooltip = new Tooltip(item.getName() + "\n" + item.getDescription());
        tooltip.setWrapText(true);
        tooltip.setPrefWidth(240);
        tooltip.setShowDelay(javafx.util.Duration.millis(250));
        tooltip.getStyleClass().add("equipment-tooltip");
        Tooltip.install(this, tooltip);

        setOnDragDetected(event -> {
            // Drag payload is the item name; GameView resolves the source node.
            ClipboardContent content = new ClipboardContent();
            content.putString(item.getName());
            startDragAndDrop(TransferMode.MOVE).setContent(content);
            setOpacity(0.7);
            event.consume();
        });
        setOnDragDone(event -> setOpacity(1.0));
    }

    private Image loadImage(String relativePath) {
        String withSlash = relativePath.startsWith("/") ? relativePath : "/" + relativePath;
        String withoutSlash = relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;
        try {
            // --- CLASSPATH/JAR RESOLUTION ---
            URL url = getClass().getResource(withSlash);
            if (url != null) {
                Image image = new Image(url.toExternalForm());
                if (!image.isError()) {
                    return image;
                }
            }
            try (InputStream stream = getClass().getClassLoader().getResourceAsStream(withoutSlash)) {
                if (stream != null) {
                    Image image = new Image(stream);
                    if (!image.isError()) {
                        return image;
                    }
                }
            }

            // --- IDE/LOOSE RESOURCE RESOLUTION ---
            // Eclipse may leave resources under resources/ instead of copying
            // them into bin/, so check project and application layouts.
            File applicationDirectory = getApplicationDirectory();
            File[] projectCandidates = {
                    new File(withoutSlash),
                    new File("resources", withoutSlash),
                    new File("src", withoutSlash),
                    new File("src", "resources/" + withoutSlash)
            };
            for (File candidate : projectCandidates) {
                if (candidate.isFile()) {
                    Image image = new Image(candidate.toURI().toString());
                    if (!image.isError()) {
                        System.out.println("[ASSET] Loaded Joker image: " + candidate.getPath());
                        return image;
                    }
                }
            }

            if (applicationDirectory != null) {
                File[] applicationCandidates = {
                        new File(applicationDirectory, withoutSlash),
                        new File(applicationDirectory, "resources/" + withoutSlash)
                };
                for (File candidate : applicationCandidates) {
                    if (candidate.isFile()) {
                        Image image = new Image(candidate.toURI().toString());
                        if (!image.isError()) {
                            System.out.println("[ASSET] Loaded Joker image: " + candidate.getPath());
                            return image;
                        }
                    }
                }
            }
        } catch (Exception exception) {
            System.err.println("[WARNING] Could not load joker image " + relativePath
                    + ": " + exception.getMessage());
        }
        return null;
    }

    private File getApplicationDirectory() {
        try {
            URL codeSource = getClass().getProtectionDomain().getCodeSource().getLocation();
            File location = new File(codeSource.toURI());
            return location.isFile() ? location.getParentFile() : location;
        } catch (URISyntaxException | SecurityException | NullPointerException exception) {
            System.err.println("[WARNING] Could not resolve application asset directory: "
                    + exception.getMessage());
            return null;
        }
    }

    public Item getItem(){ return item;}
}
