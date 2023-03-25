package com.xiii.libertycity.launcher;

import fr.trxyy.alternative.alternative_apiv2.base.*;
import fr.trxyy.alternative.alternative_apiv2.utils.Mover;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class LauncherMain extends AlternativeBase {

    private final static GameFolder gameFolder = new GameFolder("libertycity");
    private final LauncherPreferences launcherPreferences = new LauncherPreferences("LibertyCity | Launcher", 950, 600, Mover.MOVE);
    private final GameLinks gameLinks = new GameLinks("https://libertycity-libs.wstr.fr/v5/libs/www/lc/", "1.12.2.json");
    private final GameEngine gameEngine = new GameEngine(gameFolder, gameLinks, launcherPreferences);
    private final GameConnect gameConnect = new GameConnect("hypixel.net", "25565");
    private static boolean serverStatus = true;//URLReader.readUrl("http://libertycity-libs.rf.gd/www/lc/status.cfg").equalsIgnoreCase("Ok");

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {

        Scene scene = new Scene(createContent());

        LauncherBase launcherBase = new LauncherBase(stage, scene, StageStyle.UNDECORATED, gameEngine);

        launcherBase.setIconImage(stage, "favicon.png");
    }

    private Parent createContent() {

        LauncherPane contentPane = new LauncherPane(gameEngine);

        this.gameEngine.reg(gameConnect);

        new LauncherBackground(gameEngine, "background.png", contentPane);
        new LauncherPanel(contentPane, gameEngine);

        return contentPane;
    }

    public static boolean getServerStatus() {
        return serverStatus;
    }
}
