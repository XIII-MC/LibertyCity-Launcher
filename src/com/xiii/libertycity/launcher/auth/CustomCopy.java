package com.xiii.libertycity.launcher.auth;

import com.google.gson.JsonObject;
import com.xiii.libertycity.launcher.LauncherPanel;
import com.xiii.libertycity.launcher.gamerunner.GameRunner;
import fr.litarvan.openauth.microsoft.AuthTokens;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import fr.litarvan.openauth.microsoft.model.response.MinecraftProfile;
import fr.trxyy.alternative.alternative_apiv2.base.GameEngine;
import fr.trxyy.alternative.alternative_apiv2.base.LauncherPane;
import fr.trxyy.alternative.alternative_apiv2.minecraft.json.MinecraftVersion;
import fr.trxyy.alternative.alternative_apiv2.minecraft.utils.GameUtils;
import fr.trxyy.alternative.alternative_apiv2.minecraft.utils.JsonUtil;
import fr.trxyy.alternative.alternative_apiv2.updater.GameUpdater;
import fr.trxyy.alternative.alternative_authv2.base.AuthConstants;
import fr.trxyy.alternative.alternative_authv2.base.EnumAuthConfig;
import fr.trxyy.alternative.alternative_authv2.base.Logger;
import fr.trxyy.alternative.alternative_authv2.base.Session;
import fr.trxyy.alternative.alternative_authv2.microsoft.MicrosoftAuth;
import fr.trxyy.alternative.alternative_authv2.microsoft.ParamType;
import fr.trxyy.alternative.alternative_authv2.microsoft.model.MicrosoftModel;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        updater.updateJars();
        updater.downloadJavaManifest();
        updater.updateLog4j();
        runGame(engine, updater, version, session);
    }

    public static void runGame(GameEngine engine, GameUpdater updater, MinecraftVersion version, Session session) {
        try {
            Field updateText = updater.getClass().getDeclaredField("updateText");
            updateText.setAccessible(true);
            updateText.set(updater, "Téléchargement des mods...");
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

    public String match(String regex, String content) {
        Matcher matcher = Pattern.compile(regex).matcher(content);
        return !matcher.find() ? null : matcher.group(1);
    }

}
