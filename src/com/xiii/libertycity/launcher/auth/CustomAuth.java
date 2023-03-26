package com.xiii.libertycity.launcher.auth;

import fr.trxyy.alternative.alternative_apiv2.base.GameEngine;
import fr.trxyy.alternative.alternative_authv2.base.AuthConstants;
import fr.trxyy.alternative.alternative_authv2.base.Logger;
import fr.trxyy.alternative.alternative_authv2.base.Session;
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
    public boolean isAuthenticated = false;
    private Session session = new Session();
    private CustomAuthConfig authConfig;

    public CustomAuth() {
    }

    public WebView connectMicrosoft(Pane root, GameEngine engine) {
        this.authConfig = new CustomAuthConfig(engine);
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.load(AuthConstants.MICROSOFT_BASE_URL);
        webEngine.setJavaScriptEnabled(true);
        webView.setPrefWidth(500.0D);
        webView.setPrefHeight(600.0D);
        root.getChildren().add(webView);
        webEngine.getHistory().getEntries().addListener((ListChangeListener<? super WebHistory.Entry>) (c) -> {
            if (c.next() && c.wasAdded()) {
                c.getAddedSubList().forEach((entry) -> {
                    try {
                        if (this.authConfig.canRefresh()) {
                            Logger.log("Trying to logIn with RefreshToken.");
                            this.authConfig.loadConfiguration();
                            MicrosoftModel model = null;

                            try {
                                model = (new MicrosoftAuth()).getAuthorizationCode(ParamType.REFRESH, this.authConfig.microsoftModel.getRefresh_token());
                            } catch (Exception var10) {
                                var10.printStackTrace();
                            }

                            this.authConfig.updateValues(model);
                            Session result = null;

                            try {
                                result = (new MicrosoftAuth()).getLiveToken(model.getAccess_token());
                            } catch (Exception var11) {
                                var11.printStackTrace();
                                if (entry.getUrl().startsWith(AuthConstants.MICROSOFT_RESPONSE_URL)) {
                                    String authCodex = entry.getUrl().substring(entry.getUrl().indexOf("=") + 1, entry.getUrl().indexOf("&"));
                                    MicrosoftModel model2 = (new MicrosoftAuth()).getAuthorizationCode(ParamType.AUTH, authCodex);
                                    this.authConfig.createConfigFile(model2);
                                    Session result2 = (new MicrosoftAuth()).getLiveToken(model2.getAccess_token());
                                    this.setSession(result2.getUsername(), result2.getToken(), result2.getUuid());
                                    Stage stagexx = (Stage)root.getScene().getWindow();
                                    stagexx.close();
                                } else {
                                    this.isAuthenticated = false;
                                }
                            }

                            this.setSession(result.getUsername(), result.getToken(), result.getUuid());
                            Stage stage = (Stage)root.getScene().getWindow();
                            stage.close();
                        } else if (entry.getUrl().startsWith(AuthConstants.MICROSOFT_RESPONSE_URL)) {
                            String authCode = entry.getUrl().substring(entry.getUrl().indexOf("=") + 1, entry.getUrl().indexOf("&"));
                            MicrosoftModel modelx = (new MicrosoftAuth()).getAuthorizationCode(ParamType.AUTH, authCode);
                            this.authConfig.createConfigFile(modelx);
                            Session resultx = (new MicrosoftAuth()).getLiveToken(modelx.getAccess_token());
                            this.setSession(resultx.getUsername(), resultx.getToken(), resultx.getUuid());
                            Stage stagex = (Stage)root.getScene().getWindow();
                            stagex.close();
                        } else {
                            this.isAuthenticated = false;
                        }
                    } catch (Exception var12) {
                        var12.printStackTrace();
                    }

                });
            }

        });
        return webView;
    }

    private void setSession(String user, String token, String id) {
        this.session.setUsername(user);
        this.session.setToken(token);
        this.session.setUuid(id);
        this.isAuthenticated = true;
        Logger.log("Connected Successfully !");
    }

    public boolean isLogged() {
        return this.isAuthenticated;
    }

    public Session getSession() {
        return this.session;
    }
}
