package com.xiii.libertycity.launcher;

import fr.trxyy.alternative.alternative_apiv2.base.GameEngine;
import fr.trxyy.alternative.alternative_apiv2.base.LauncherPane;
import fr.trxyy.alternative.alternative_authv2.base.GameAuth;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
}
