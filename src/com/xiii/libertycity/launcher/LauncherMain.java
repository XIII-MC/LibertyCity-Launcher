package com.xiii.libertycity.launcher;

import com.xiii.libertycity.launcher.auth.CustomAuth;
import fr.trxyy.alternative.alternative_api_uiv2.components.LauncherAlert;
import fr.trxyy.alternative.alternative_apiv2.base.*;
import fr.trxyy.alternative.alternative_apiv2.minecraft.utils.GameUtils;
import fr.trxyy.alternative.alternative_apiv2.utils.Mover;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;

public class LauncherMain extends AlternativeBase {

    private static GameFolder gameFolder;
    private final LauncherPreferences launcherPreferences = new LauncherPreferences("LibertyCity | Launcher", 950, 600, Mover.MOVE);
    private final GameLinks gameLinks = new GameLinks("https://libraries-libertycity.websr.fr/v5/libs/www/lc/", "1.12.2.json");
    private GameEngine gameEngine;
    private final GameConnect gameConnect = new GameConnect("178.33.40.181", "25681");
    private static String serverStatus = "Maintenance";
    private static boolean isBanned = false;
    private static boolean isWhitelisted = false;
    private static final String launcherVersion = "0003";

    private static int httpRequestCount = 0;
    private static long lastHTTPRequest;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {

        final HttpsURLConnection versionChecker = (HttpsURLConnection) new URL("https://libraries-libertycity.websr.fr/v5/libs/www/lc/launcher/launcher_version.cfg").openConnection();
        LauncherMain.updateHTTPRequestCount(); // TODO: UPDATE HTTP REQUEST
        final BufferedReader versionInputStream = new BufferedReader(new InputStreamReader(versionChecker.getInputStream()));
        if (!launcherVersion.equalsIgnoreCase(versionInputStream.readLine())) {
            new LauncherAlert("Mise à jour requise! \nVeuillez vous rendre sur notre Discord pour télécharger la nouvelle version du launcher!", "");
            System.exit(0);
        }
        versionChecker.getInputStream().close();
        versionInputStream.close();

        final HttpsURLConnection urlConnection = (HttpsURLConnection) new URL("https://libraries-libertycity.websr.fr/v5/libs/www/lc/status.cfg").openConnection();
        LauncherMain.updateHTTPRequestCount(); // TODO: UPDATE HTTP REQUEST
        final BufferedReader inputStream = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        serverStatus = inputStream.readLine();
        urlConnection.getInputStream().close();
        inputStream.close();

        File getFileForDirectory = GameUtils.getWorkingDirectory("libertyCity/gameDirectory");
        if (!getFileForDirectory.exists()) {
            getFileForDirectory.createNewFile();
            FileWriter fw = new FileWriter(getFileForDirectory);
            gameFolder = new GameFolder(LauncherPanel.varUtil.workDirectoryName);
            fw.write("" + gameFolder.getGameDir().getAbsolutePath());
            fw.close();

        } else {
            BufferedReader br = new BufferedReader(new FileReader(getFileForDirectory));
            LauncherPanel.varUtil.getPathToGameDirectory = br.readLine();
            gameFolder = new GameFolder(LauncherPanel.varUtil.workDirectoryName);
            gameFolder.getLibsDir().delete();
            gameFolder.getCacheDir().delete();
            gameFolder.getLogConfigsDir().delete();
            gameFolder.getGameDir().delete();
            gameFolder.getNativesDir().delete();
            gameFolder.getNativesCacheDir().delete();
            gameFolder.getVersionsDir().delete();
            gameFolder.getRuntimeDir().delete();
            gameFolder.getAssetsDir().delete();
            gameFolder.gameDir = new File(LauncherPanel.varUtil.getPathToGameDirectory);
            gameFolder.assetsDir = new File(gameFolder.gameDir, "assets");
            gameFolder.libsDir = new File(gameFolder.gameDir, "libraries");
            gameFolder.versionsDir = new File(gameFolder.gameDir, "versions");
            gameFolder.resourcepackDir = new File(gameFolder.gameDir, "resourcepacks");
            gameFolder.cacheDir = new File(gameFolder.gameDir, "cache");
            gameFolder.log_configsDir = new File(gameFolder.assetsDir, "log_configs");
            gameFolder.nativesDir = new File(gameFolder.gameDir, "natives");
            gameFolder.nativesCacheDir = new File(gameFolder.gameDir, "cache_natives");
            gameFolder.runtimeDir = new File(gameFolder.gameDir, "runtime");
            gameFolder.getLibsDir().mkdirs();
            gameFolder.getCacheDir().mkdirs();
            gameFolder.getAssetsDir().mkdirs();
            gameFolder.getLogConfigsDir().mkdirs();
            gameFolder.getGameDir().mkdirs();
            gameFolder.getNativesDir().mkdirs();
            gameFolder.getNativesCacheDir().mkdirs();
            gameFolder.getVersionsDir().mkdirs();
            gameFolder.getRuntimeDir().mkdirs();
            br.close();
        }
        gameEngine = new GameEngine(gameFolder, gameLinks, launcherPreferences);
        Scene scene = new Scene(createContent());
        LauncherBase launcherBase = new LauncherBase(stage, scene, StageStyle.UNDECORATED, gameEngine);
        launcherBase.setIconImage(stage, "favicon.png");
        CustomAuth.setAllowRefreshToken(new File(this.gameEngine.getGameFolder().getGameDir(), "auth_infos.json").exists());
    }

    private Parent createContent() {

        LauncherPane contentPane = new LauncherPane(gameEngine);

        this.gameEngine.reg(gameConnect);

        new LauncherBackground(gameEngine, "background.png", contentPane);
        new LauncherPanel(contentPane, gameEngine);

        return contentPane;
    }

    public static boolean getServerStatus(final boolean newRequest) {
        try {

            if (newRequest) {

                final HttpsURLConnection urlConnection = (HttpsURLConnection) new URL("https://libraries-libertycity.websr.fr/v5/libs/www/lc/status.cfg").openConnection();
                LauncherMain.updateHTTPRequestCount(); // TODO: UPDATE HTTP REQUEST
                final BufferedReader inputStream = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                serverStatus = inputStream.readLine();

                urlConnection.getInputStream().close();
                inputStream.close();
            } else return serverStatus.equalsIgnoreCase("Ok");
        } catch (IOException ignored) {
        }
        return serverStatus.equalsIgnoreCase("Ok");
    }

    public static boolean isBanned() {
        return isBanned;
    }

    public static void setBanned(final boolean isBanned) {
        LauncherMain.isBanned = isBanned;
    }

    public static String getLauncherVersion() {
        return launcherVersion;
    }

    public static void updateHTTPRequestCount() {
        httpRequestCount++;
        //System.out.print("HTTP REQUEST SENT (" + (System.currentTimeMillis() - lastHTTPRequest) + "ms): " + httpRequestCount + "\n");
        lastHTTPRequest = System.currentTimeMillis();
    }

    public static void setWhitelisted(final String uuid) {
        try {
            final HttpsURLConnection whitelistConnection = (HttpsURLConnection) new URL("https://libraries-libertycity.websr.fr/v5/libs/www/lc/launcher/http/whitelist.json").openConnection();
            LauncherMain.updateHTTPRequestCount(); // TODO: UPDATE HTTP REQUEST
            final BufferedReader whitelistInputStream = new BufferedReader(new InputStreamReader(whitelistConnection.getInputStream()));

            final StringBuffer sb = new StringBuffer();
            String line;
            while ((line = whitelistInputStream.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            whitelistConnection.getInputStream().close();
            whitelistInputStream.close();
            isWhitelisted = sb.toString().contains(uuid);
        } catch (IOException ignored) {
        }
    }

    public static boolean isWhitelisted() {
        return isWhitelisted;
    }
}
