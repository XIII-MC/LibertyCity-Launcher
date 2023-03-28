package com.xiii.libertycity.launcher.auth;

import com.xiii.libertycity.launcher.gamerunner.GameRunner;
import fr.trxyy.alternative.alternative_apiv2.base.GameEngine;
import fr.trxyy.alternative.alternative_apiv2.base.LauncherPane;
import fr.trxyy.alternative.alternative_apiv2.minecraft.json.MinecraftVersion;
import fr.trxyy.alternative.alternative_apiv2.updater.GameUpdater;
import fr.trxyy.alternative.alternative_authv2.base.Session;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.lang.reflect.Field;

public class CustomCopy {

    public void showMicrosoftAuth(GameEngine engine, CustomAuth auth) {
        Scene scene = new Scene(this.createMicrosoftPanel(engine, auth));
        Stage stage = new Stage();
        scene.setFill(Color.TRANSPARENT);
        stage.setResizable(false);
        stage.setTitle("Microsoft Authentication");
        stage.setWidth(500.0D);
        stage.setHeight(600.0D);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    private Parent createMicrosoftPanel(GameEngine engine, CustomAuth auth) {
        LauncherPane contentPane = new LauncherPane(engine);
        auth.connectMicrosoft(contentPane, engine);
        return contentPane;
    }

    public static void downloadGameAndRun(GameEngine engine, GameUpdater updater, MinecraftVersion version, Session session) {
        updater.updateAssets();
        updater.updateJars();
        updater.downloadJavaManifest();
        updater.updateLog4j();
        runGame(engine, updater, version, session);
    }

    public static void runGame(GameEngine engine, GameUpdater updater, MinecraftVersion version, Session session) {
        try {
            Field updateText = updater.getClass().getDeclaredField("updateText");
            updateText.setAccessible(true);
            updateText.set(updater, "Demarrage...");
            updateText.setAccessible(false);
        }catch (NoSuchFieldError | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        GameRunner runner = new GameRunner(session, version, engine);

        try {
            runner.launch();
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }
}
