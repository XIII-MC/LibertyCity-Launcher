package com.xiii.libertycity.launcher;

import fr.trxyy.alternative.alternative_api_uiv2.components.LauncherAlert;
import fr.trxyy.alternative.alternative_apiv2.base.*;
import fr.trxyy.alternative.alternative_apiv2.utils.Mover;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class LauncherMain extends AlternativeBase {

    private final static GameFolder gameFolder = new GameFolder("libertycity");
    private final LauncherPreferences launcherPreferences = new LauncherPreferences("LibertyCity | Launcher", 950, 600, Mover.MOVE);
    private final GameLinks gameLinks = new GameLinks("https://libertycity-libs.wstr.fr/v5/libs/www/lc/", "1.12.2.json");
    private final GameEngine gameEngine = new GameEngine(gameFolder, gameLinks, launcherPreferences);
    private final GameConnect gameConnect = new GameConnect("178.33.40.181", "25681");

    private static String serverStatus = "Maintenance";
    private static boolean isBanned = false;
    private static final String launcherVersion = "0001";

    public static void main(String[] args) throws IOException {

        final HttpsURLConnection urlConnection = (HttpsURLConnection) new URL("https://libertycity-libs.wstr.fr/v5/libs/www/lc/launcher/launcher_version.cfg").openConnection();
        final BufferedReader inputStream = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        if (!launcherVersion.equalsIgnoreCase(inputStream.readLine())) {
            new LauncherAlert("Mise à jour requise!", "Veuillez vous rendre sur notre Discord pour télécharger la nouvelle version du launcher!");
            System.exit(0);
        }
        urlConnection.getInputStream().close();

        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {

        final HttpsURLConnection urlConnection = (HttpsURLConnection) new URL("https://libertycity-libs.wstr.fr/v5/libs/www/lc/status.cfg").openConnection();
        final BufferedReader inputStream = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        serverStatus = inputStream.readLine();
        urlConnection.getInputStream().close();

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
        return serverStatus.equalsIgnoreCase("Ok");
    }

    public static boolean isBanned() {
        return isBanned;
    }

    public static void setBanned(boolean isBanned) {
        LauncherMain.isBanned = isBanned;
    }
}
