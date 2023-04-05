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

    private MicrosoftAuthResult microsoftAuthResult = null;
    private boolean hideStage = false;

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

    public void showMicrosoftAuth2(GameEngine engine, CustomAuth auth) {
        Scene scene = new Scene(this.createMicrosoftPanel2(engine, auth));
        Stage stage = new Stage();
        scene.setFill(Color.TRANSPARENT);
        stage.setResizable(false);
        stage.setTitle("Microsoft Authentication");
        stage.setWidth(500.0D);
        stage.setHeight(600.0D);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        //stage.show();
        stage.showAndWait();

    }

    private Parent createMicrosoftPanel2(GameEngine engine, CustomAuth auth) {
        LauncherPane contentPane = new LauncherPane(engine);
        doMicrosoftAuth(contentPane, engine, auth);
        return contentPane;
    }

    public void doMicrosoftAuth(Pane panel, GameEngine engine, CustomAuth auth) {
        NewAuthConfig config = new NewAuthConfig(engine);
        File authFile = new File(engine.getGameFolder().getGameDir(), "auth_infosTest.json");
        if(!authFile.exists()) {


            new Thread(() -> {
                MicrosoftAuthenticator authenticator2 = new MicrosoftAuthenticator();
                MicrosoftAuthResult result2 = null;
                try {
                    result2 = authenticator2.loginWithWebview();
                    auth.getSession().setToken(result2.getAccessToken());
                    auth.getSession().setUsername(result2.getProfile().getName());
                    auth.getSession().setUuid(result2.getProfile().getId());
                    config.createConfigFile(result2);
                    LauncherPanel.varUtil.isAuthenticated = true;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Stage stagexx = (Stage) panel.getScene().getWindow();
                            stagexx.close();
                        }
                    });
                } catch (MicrosoftAuthenticationException e) {
                    e.printStackTrace();
                }
            }).start();

           /* MicrosoftAuthResult finalResult = result;
            Thread t = new Thread(() -> {

                while (true) {
                    if (!(finalResult == null)) break;
                }
                config.createConfigFile(finalResult);
                auth.getSession().setToken(finalResult.getAccessToken());
                auth.getSession().setUsername(finalResult.getProfile().getName());
                auth.getSession().setUuid(finalResult.getProfile().getId());
                LauncherPanel.varUtil.isAuthenticated = true;
                panel.gameSession = auth.getSession();
            });
            t.start(); */
            //LauncherPane contentPane = new LauncherPane(engine);
            WebView webView = new WebView();
            webView.getEngine().load("https://login.live.com/oauth20_authorize.srf?client_id=00000000402b5328&response_type=code&redirect_uri=https%3A%2F%2Flogin.live.com%2Foauth20_desktop.srf&scope=XboxLive.signin%20offline_access");
            webView.getEngine().setJavaScriptEnabled(true);
            webView.setPrefWidth(500.0D);
            webView.setPrefHeight(600.0D);
            //webView.setVisible(false);

            //panel.getChildren().add(webView);
            /*webView.getEngine().getHistory().getEntries().addListener((ListChangeListener<? super WebHistory.Entry>) (c) -> {
                if (c.next() && c.wasAdded()) {
                    c.getAddedSubList().forEach((entry) -> {

                        if (entry.getUrl().startsWith(AuthConstants.MICROSOFT_RESPONSE_URL)) {
                            String authCode = entry.getUrl().substring(entry.getUrl().indexOf("=") + 1, entry.getUrl().indexOf("&"));
                            try {
                                authFile.createNewFile();
                                config.authConfig = authFile;

                                MicrosoftModel modelx = (new MicrosoftAuth()).getAuthorizationCode(ParamType.AUTH, authCode);
                                System.out.println("" + modelx.getAccess_token());
                                System.out.println("" + modelx.getRefresh_token());
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty(EnumAuthConfig.ACCESS_TOKEN.getOption(), modelx.getAccess_token());
                                jsonObject.addProperty(EnumAuthConfig.REFRESH_TOKEN.getOption(), modelx.getRefresh_token());
                                PrintWriter printWriter = null;
                                try {
                                    printWriter = new PrintWriter(new FileWriter(authFile));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                printWriter.println(JsonUtil.getGson().toJson(jsonObject));
                                printWriter.close();
                                MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
                                MicrosoftAuthResult result = null;
                                config.authConfig = authFile;
                                try {
                                    result = authenticator.loginWithTokens(new AuthTokens((String) config.getValue(EnumAuthConfig.ACCESS_TOKEN), (String) config.getValue(EnumAuthConfig.REFRESH_TOKEN)));
                                    auth.getSession().setToken(result.getAccessToken());
                                    auth.getSession().setUsername(result.getProfile().getName());
                                    auth.getSession().setUuid(result.getProfile().getId());
                                    LauncherPanel.varUtil.isAuthenticated = true;
                                } catch (MicrosoftAuthenticationException e) {
                                    e.printStackTrace();
                                }
                                //Session result2 = (new MicrosoftAuth()).getLiveToken(modelx.getAccess_token());
                                //auth.getSession().setToken(result2.getToken());
                                //auth.getSession().setUsername(result2.getUsername());
                                //auth.getSession().setUuid(result2.getUuid());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                /*try {
                    MicrosoftAuthResult result = authenticator.loginWithTokens();
                    auth.getSession().setToken(result.getAccessToken());
                    auth.getSession().setUsername(result.getProfile().getName());
                    auth.getSession().setUuid(result.getProfile().getId());
                    LauncherPanel.varUtil.isAuthenticated = true;
                    //panel.gameSession = auth.getSession();
                } catch (MicrosoftAuthenticationException e) {
                    e.printStackTrace();
                }
                            Stage stagexx = (Stage) panel.getScene().getWindow();
                            stagexx.close();
                        }
                    });
                }
            }); */
            //MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();




        } else {
            config.loadConfiguration();
            if(config.microsoftModel != null) {
                MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
                MicrosoftAuthResult result = null;
                try {
                    result = authenticator.loginWithTokens(new AuthTokens((String) config.getValue(EnumAuthConfig.ACCESS_TOKEN), (String) config.getValue(EnumAuthConfig.REFRESH_TOKEN)));
                    auth.getSession().setToken(result.getAccessToken());
                    auth.getSession().setUsername(result.getProfile().getName());
                    auth.getSession().setUuid(result.getProfile().getId());
                    LauncherPanel.varUtil.isAuthenticated = true;
                } catch (MicrosoftAuthenticationException e) {
                    e.printStackTrace();
                }


            }

        }

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

    public AuthTokens extractTokens(String url) throws MicrosoftAuthenticationException {
        return new AuthTokens(this.extractValue(url, "access_token"), this.extractValue(url, "refresh_token"));
    }

    public String extractValue(String url, String key) throws MicrosoftAuthenticationException {
        String matched = this.match(key + "=([^&]*)", url);
        if (matched == null) {
            throw new MicrosoftAuthenticationException("Invalid credentials or tokens");
        } else {
            try {
                return URLDecoder.decode(matched, "UTF-8");
            } catch (UnsupportedEncodingException var5) {
                throw new MicrosoftAuthenticationException(var5);
            }
        }
    }

    public String match(String regex, String content) {
        Matcher matcher = Pattern.compile(regex).matcher(content);
        return !matcher.find() ? null : matcher.group(1);
    }

}
