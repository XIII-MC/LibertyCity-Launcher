package com.xiii.libertycity.launcher;

import fr.trxyy.alternative.alternative_apiv2.base.*;
import fr.trxyy.alternative.alternative_apiv2.utils.Mover;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;

public class LauncherMain extends AlternativeBase {

    private final static GameFolder gameFolder = new GameFolder("libertycity");
    private final LauncherPreferences launcherPreferences = new LauncherPreferences("LibertyCity | Launcher", 950, 600, Mover.MOVE);
    private final GameLinks gameLinks = new GameLinks("https://libertycity-libs.wstr.fr/v5/libs/www/lc/", "1.12.2-dev.json");
    private final GameEngine gameEngine = new GameEngine(gameFolder, gameLinks, launcherPreferences);
    private final GameConnect gameConnect = new GameConnect("spartan.vagdedes.com", "25565");

    private static String serverStatus = "Maintenance";
    private static boolean isBanned = false;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {

        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.loadContent("<iframe src=\"https://discord.com/widget?id=696690512291299398&theme=dark\" width=\"350\" height=\"500\" allowtransparency=\"true\" frameborder=\"0\" sandbox=\"allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts\"></iframe>");

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
