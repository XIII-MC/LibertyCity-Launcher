package com.xiii.libertycity.launcher.auth;

import fr.trxyy.alternative.alternative_apiv2.base.GameEngine;
import fr.trxyy.alternative.alternative_authv2.base.AuthConfig;
import fr.trxyy.alternative.alternative_authv2.base.Logger;
import fr.trxyy.alternative.alternative_authv2.base.Session;
import fr.trxyy.alternative.alternative_authv2.microsoft.AuthConstants;
import fr.trxyy.alternative.alternative_authv2.microsoft.MicrosoftAuth;
import fr.trxyy.alternative.alternative_authv2.microsoft.ParamType;
import fr.trxyy.alternative.alternative_authv2.microsoft.model.MicrosoftModel;
import javafx.collections.ListChangeListener;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class CustomAuth {

    public static boolean isAuthenticated = false;
    private static Session session = new Session();
    private static AuthConfig authConfig;
    private static boolean allowRefreshToken = true;

    public CustomAuth() {
    }

    public void connectMicrosoft(Pane root, GameEngine engine) {
        authConfig = new AuthConfig(engine);
        final WebView webView = new WebView();
        final WebEngine webEngine = webView.getEngine();
        webEngine.load(AuthConstants.MICROSOFT_BASE_URL);
        webEngine.setJavaScriptEnabled(true);
        webView.setPrefWidth(500.0D);
        webView.setPrefHeight(600.0D);
        root.getChildren().add(webView);
        webEngine.getHistory().getEntries().addListener((ListChangeListener<? super WebHistory.Entry>) (c) -> {
            if (c.next() && c.wasAdded()) {
                c.getAddedSubList().forEach((entry) -> {
                    try {
                        if (allowRefreshToken) {
                            Logger.log("Trying to login with RefreshToken.");
                            authConfig.loadConfiguration();
                            MicrosoftModel modelRefresh = null;

                            try {
                                modelRefresh = (new MicrosoftAuth()).getAuthorizationCode(ParamType.REFRESH, authConfig.microsoftModel.getRefresh_token());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            authConfig.updateValues(modelRefresh);
                            Session sessionRefresh = null;

                            try {
                                sessionRefresh = (new MicrosoftAuth()).getLiveToken(modelRefresh.getAccess_token());
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (entry.getUrl().startsWith(AuthConstants.MICROSOFT_RESPONSE_URL)) {
                                    final String authCodex = entry.getUrl().substring(entry.getUrl().indexOf("=") + 1, entry.getUrl().indexOf("&"));
                                    final MicrosoftModel refreshModel = (new MicrosoftAuth()).getAuthorizationCode(ParamType.AUTH, authCodex);
                                    authConfig.createConfigFile(refreshModel);
                                    final Session refreshSession = (new MicrosoftAuth()).getLiveToken(refreshModel.getAccess_token());
                                    setSession(refreshSession.getUsername(), refreshSession.getToken(), refreshSession.getUuid(), true);
                                    final Stage finalStage = (Stage)root.getScene().getWindow();
                                    finalStage.close();
                                } else {
                                    isAuthenticated = false;
                                }
                            }

                            setSession(sessionRefresh.getUsername(), sessionRefresh.getToken(), sessionRefresh.getUuid(), true);
                            final Stage stageRefresh = (Stage)root.getScene().getWindow();
                            stageRefresh.close();
                        } else if (entry.getUrl().startsWith(AuthConstants.MICROSOFT_RESPONSE_URL)) {
                            final String authCode = entry.getUrl().substring(entry.getUrl().indexOf("=") + 1, entry.getUrl().indexOf("&"));
                            final MicrosoftModel modelNew = (new MicrosoftAuth()).getAuthorizationCode(ParamType.AUTH, authCode);
                            authConfig.createConfigFile(modelNew);
                            final Session resultNew = (new MicrosoftAuth()).getLiveToken(modelNew.getAccess_token());
                            setSession(resultNew.getUsername(), resultNew.getToken(), resultNew.getUuid(), true);
                            final Stage stageNew = (Stage)root.getScene().getWindow();
                            stageNew.close();
                        } else {
                            isAuthenticated = false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
            }

        });
    }

    private static void setSession(final String user, final String token, final String id, final boolean log) {
        session.setUsername(user);
        session.setToken(token);
        session.setUuid(id);
        isAuthenticated = true;
        if (log) Logger.log("Connected Successfully !");
    }

    public boolean isLogged() {
        return isAuthenticated;
    }

    public Session getSession() {
        return session;
    }

    public static void resetAuth() {
        authConfig = null;
        session = new Session();
        setSession(null, null, null, false);
        isAuthenticated = false;
        allowRefreshToken = false;
    }
}