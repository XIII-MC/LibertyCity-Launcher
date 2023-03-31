package com.xiii.libertycity.launcher.auth;

import com.xiii.libertycity.launcher.LauncherPanel;
import fr.trxyy.alternative.alternative_apiv2.base.GameEngine;
import fr.trxyy.alternative.alternative_apiv2.minecraft.utils.GameUtils;
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
    public WebView webView = new WebView();
    public AuthListener listener = null;

    public CustomAuth() {
        webView.getEngine().getHistory().getEntries().clear();
    }

    public WebView connectMicrosoft(Pane root, GameEngine engine) {
        this.authConfig = new CustomAuthConfig(engine);
        LauncherPanel.varUtil.isAuthenticated = false;
        webView.getEngine().load("https://login.live.com/oauth20_authorize.srf?client_id=00000000402b5328&response_type=code&redirect_uri=https%3A%2F%2Flogin.live.com%2Foauth20_desktop.srf&scope=XboxLive.signin%20offline_access");
        webView.getEngine().setJavaScriptEnabled(true);
        webView.setPrefWidth(500.0D);
        webView.setPrefHeight(600.0D);
        root.getChildren().add(webView);
        webView.getEngine().getHistory().getEntries().addListener(listener = new AuthListener(root, engine, authConfig, session));

        Thread t = new Thread(() -> {
            while(!LauncherPanel.varUtil.isAuthenticated) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            CustomAuth.this.webView.getEngine().getHistory().getEntries().removeListener(listener);
        });
        t.start();
        while(t.isAlive()) {

        }
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
        return LauncherPanel.varUtil.isAuthenticated;
    }

    public Session getSession() {
        return this.session;
    }
}
