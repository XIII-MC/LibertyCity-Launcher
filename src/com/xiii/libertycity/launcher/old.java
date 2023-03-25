package com.xiii.libertycity.launcher;

import fr.trxyy.alternative.alternative_api_uiv2.components.*;
import fr.trxyy.alternative.alternative_apiv2.base.GameEngine;
import fr.trxyy.alternative.alternative_apiv2.base.IScreen;
import fr.trxyy.alternative.alternative_apiv2.settings.UsernameSaver;
import fr.trxyy.alternative.alternative_apiv2.updater.GameUpdater;
import fr.trxyy.alternative.alternative_apiv2.utils.FontLoader;
import fr.trxyy.alternative.alternative_authv2.base.GameAuth;
import fr.trxyy.alternative.alternative_authv2.base.Session;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;
import java.io.File;

public class old extends IScreen {
    // TOP */
    private final LauncherRectangle topRectangle;
    private final LauncherLabel titleLabel;
    private final LauncherImage titleImage;
    private final LauncherButton closeButton;
    private final LauncherButton reduceButton;
    // LOGIN */
    private LauncherTextField usernameField;
    private LauncherPasswordField passwordField;
    private LauncherButton loginButton;
    private final LauncherButton microsoftButton;
    private final LauncherButton settingsButton;
    // USERNAME SAVER */
    public UsernameSaver usernameSaver;
    // GAME-ENGINE, UPDATE */
    private final GameEngine gameEngine;
    private final LauncherProgressBar progressBar;
    private final LauncherLabel updateLabel;
    private final Rectangle updateRectangle;
    private Thread updateThread;
    private GameUpdater updater;
    // LOGGED IN **/
    private final Rectangle loggedRectangle;
    private final LauncherImage headImage;
    private final LauncherLabel accountLabel;
    // SETTINGS **/
    private GameAuth gameAuth;
    private Session gameSession;
    private Font customFont = FontLoader.loadFont("Comfortaa-Regular.ttf", "Comfortaa", 18F);

    public old(Pane root, GameEngine engine) {
        this.gameEngine = engine;
        this.usernameSaver = new UsernameSaver(engine);

        //Top black rectangle
        this.topRectangle = new LauncherRectangle(root, 0, 0, engine.getWidth(), 31);
        this.topRectangle.setFill(new Color(0, 0, 0, 0.70));

        //Top title label
        this.titleLabel = new LauncherLabel(root);
        this.titleLabel.setText("LibertyCity | Launcher");
        this.titleLabel.setFont(FontLoader.loadFont("Comfortaa-Regular.ttf", "Comfortaa", 18F));
        this.titleLabel.addStyle("-fx-background-color: transparent;");
        this.titleLabel.addStyle("-fx-text-fill: white;");
        this.titleLabel.setBounds(engine.getWidth() / 2 - 80, -4, 500, 40);
        this.titleLabel.setOpacity(0.7);

        //Top icon next to title label
        this.titleImage = new LauncherImage(root, loadImage(gameEngine, "minecraft.png"));
        this.titleImage.setBounds(engine.getWidth() / 3 + 40, 3, 25, 25);

        //==================== RECTANGLE NOIR EN BAS ===================== */
        //this.drawRect(root, 0, engine.getHeight() - 110, engine.getWidth(), 300, Color.rgb(0, 0, 0, 0.4));
        //==================== AFFICHER UN LOGO ===================== */
        //this.drawImage(gameEngine, loadImage(gameEngine, "alternativeapi_logo.png"), 0, this.gameEngine.getHeight() - 100, 400, 100, root, Mover.DONT_MOVE);

        //Top right close button
        this.closeButton = new LauncherButton(root);
        this.closeButton.setInvisible();
        this.closeButton.setBounds(gameEngine.getWidth() - 50, 0, 25, 25);
        LauncherImage closeImage = new LauncherImage(root, loadImage(gameEngine, "close.png"));
        closeImage.setSize(25, 25);
        this.closeButton.setGraphic(closeImage);
        this.closeButton.setOnAction(event -> System.exit(0));

        //Top right reduce button
        this.reduceButton = new LauncherButton(root);
        this.reduceButton.setInvisible();
        this.reduceButton.setBounds(gameEngine.getWidth() - 83, 0, 25, 25);
        LauncherImage reduceImage = new LauncherImage(root, loadImage(gameEngine, "reduce.png"));
        reduceImage.setSize(25, 25);
        this.reduceButton.setGraphic(reduceImage);
        this.reduceButton.setOnAction(event -> {
            Stage stage = (Stage) ((LauncherButton) event.getSource()).getScene().getWindow();
            stage.setIconified(true);
        });
        //==================== CASE PSEUDONYME ===================== */
        //this.usernameField = new LauncherTextField(usernameSaver.getUsername(), root);
        //this.usernameField.setBounds(this.gameEngine.getWidth() - 360, this.gameEngine.getHeight() - 100, 220, 20);
        //this.setFontSize(14.0F);
        //this.usernameField.setFont(this.customFont);
        //this.usernameField.addStyle("-fx-background-color: rgb(230, 230, 230);");
        //this.usernameField.addStyle("-fx-text-fill: black;");
        //this.usernameField.addStyle("-fx-border-radius: 0 0 0 0;");
        //this.usernameField.addStyle("-fx-background-radius: 0 0 0 0;");
        //this.usernameField.setVoidText("Nom de compte");
        //==================== CASE MOT DE PASSE ===================== */
        //this.passwordField = new LauncherPasswordField(root);
        //this.passwordField.setBounds(this.gameEngine.getWidth() - 360, this.gameEngine.getHeight() - 65, 220, 20);
        //this.setFontSize(14.0F);
        //this.passwordField.setFont(this.customFont);
        //this.passwordField.addStyle("-fx-background-color: rgb(230, 230, 230);");
        //this.passwordField.addStyle("-fx-text-fill: black;");
        //this.passwordField.addStyle("-fx-border-radius: 0 0 0 0;");
        //this.passwordField.addStyle("-fx-background-radius: 0 0 0 0;");
        //this.passwordField.setVoidText("Mot de passe (vide = crack)");
        //==================== BOUTON DE CONNEXION ===================== */
        //this.loginButton = new LauncherButton("Se connecter", root);
        //this.setFontSize(12.5F);
        //this.loginButton.setFont(this.customFont);
        //this.loginButton.setBounds(this.gameEngine.getWidth() - 130, this.gameEngine.getHeight() - 64, 105, 20);
        //this.loginButton.addStyle("-fx-background-color: rgb(230, 230, 230);");
        //this.loginButton.addStyle("-fx-text-fill: black;");
        //this.loginButton.addStyle("-fx-border-radius: 0 0 0 0;");
        //this.loginButton.addStyle("-fx-background-radius: 0 0 0 0;");
        //this.loginButton.setOnAction(event -> {
        //    usernameSaver.saveSettings(usernameField.getText());
        //    //==================== AUTHENTIFICATION OFFLINE (CRACK) =====================
        //    if (usernameField.getText().length() < 1) {
        //        new LauncherAlert("Le pseudonyme n'est pas assez long (3 caracteres minimum.)",
        //                "Il y a un probleme lors de la tentative de connexion: Le pseudonyme doit comprendre au moins 3 caracteres.");
        //    } else if (usernameField.getText().length() > 1 && passwordField.getText().isEmpty()) {
        //        gameSession = new Session(usernameField.getText(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
        //        File jsonFile = downloadVersion(engine.getGameLinks().getJsonUrl(), engine);
        //        updateGame(gameSession, jsonFile);
        //    }
        //});
        //==================== BOUTON DES OPTIONS ===================== */
        this.settingsButton = new LauncherButton(root);
        this.settingsButton.setInvisible();
        this.settingsButton.setBounds(this.gameEngine.getWidth() - 130, this.gameEngine.getHeight() - 99, 105, 20);
        LauncherImage settingsImage = new LauncherImage(root, loadImage(gameEngine, "settings.png"));
        settingsImage.setSize(25, 25);
        this.reduceButton.setGraphic(settingsImage);
        this.settingsButton.setOnAction(event -> {
            final JDialog frame = new JDialog();
            frame.setTitle("Modification des parametres");
            frame.setContentPane(new LauncherSettings(engine));
            frame.setResizable(false);
            frame.setModal(true);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            frame.setSize(630, 210);
            frame.setVisible(true);
        });
        //==================== BOUTON DE CONNEXION MICROSOFT ===================== */
        this.microsoftButton = new LauncherButton("Connexion avec Microsoft", root);
        this.setFontSize(12.5F);
        this.microsoftButton.setFont(this.customFont);
        this.microsoftButton.setBounds(this.gameEngine.getWidth() - 345, this.gameEngine.getHeight() - 33, 190, 20);
        this.microsoftButton.addStyle("-fx-background-color: rgb(230, 230, 230);");
        this.microsoftButton.addStyle("-fx-text-fill: black;");
        this.microsoftButton.addStyle("-fx-border-radius: 0 0 0 0;");
        this.microsoftButton.addStyle("-fx-background-radius: 0 0 0 0;");
        this.microsoftButton.setOnAction(event -> {
            gameAuth = new GameAuth();
            showMicrosoftAuth(gameEngine, gameAuth);
            if (gameAuth.isLogged()) {
                gameSession = gameAuth.getSession();
                File jsonFile = downloadVersion(engine.getGameLinks().getJsonUrl(), engine);
                updateGame(gameSession, jsonFile);
            }
        });

        //======================================================= **/
        this.updateLabel = new LauncherLabel(root);
        this.updateLabel.setText("Mise a jour...");
        this.setFontSize(30.0F);
        this.updateLabel.setFont(this.customFont);
        this.updateLabel.setBounds(this.gameEngine.getWidth() - 250, this.gameEngine.getHeight() - 70, 230, 20);
        this.updateLabel.addStyle("-fx-text-fill: white;");
        this.updateLabel.setOpacity(0.0D);
        this.updateLabel.setVisible(false);

        this.loggedRectangle = this.drawRect(root, this.gameEngine.getWidth() / 2 - 115, 50, 230, 200, Color.rgb(0, 0, 0, 0.4));
        this.loggedRectangle.setOpacity(0.0D);
        this.loggedRectangle.setVisible(false);

        this.headImage = new LauncherImage(root);
        this.headImage.setFitWidth(120);
        this.headImage.setFitHeight(120);
        this.headImage.setLayoutX(this.gameEngine.getWidth() / 2 - 60);
        this.headImage.setLayoutY(70);
        this.headImage.setOpacity(0.0D);
        this.headImage.setVisible(false);

        this.accountLabel = new LauncherLabel(root);
        this.accountLabel.setAlignment(Pos.CENTER);
        this.setFontSize(20.0F);
        this.accountLabel.setFont(this.customFont);
        this.accountLabel.setBounds(this.gameEngine.getWidth() / 2 - 110, this.gameEngine.getHeight() / 2 - 50, 220, 20);
        this.accountLabel.addStyle("-fx-text-fill: white;");
        this.accountLabel.setOpacity(0.0D);
        this.accountLabel.setVisible(false);

        this.updateRectangle = this.drawRect(root, this.gameEngine.getWidth() / 2 - 250, this.gameEngine.getHeight() / 2 + 70, 500, 30, Color.rgb(0, 0, 0, 0.4));
        this.updateRectangle.setOpacity(0.0D);
        this.updateRectangle.setVisible(false);

        this.progressBar = new LauncherProgressBar(root);
        this.progressBar.setBounds(this.gameEngine.getWidth() / 2 - 245, this.gameEngine.getHeight() / 2 + 75, 490, 21);
        this.progressBar.setOpacity(0.0D);
        this.progressBar.setVisible(false);
    }

    private void updateGame(Session auth, File jsonFile) {
        this.accountLabel.setText(auth.getUsername());
        this.fadeOut(this.loginButton, 300).setOnFinished(event -> {
            loginButton.setVisible(false);
            updateLabel.setVisible(true);
            fadeIn(updateLabel, 300);

            progressBar.setVisible(true);
            fadeIn(progressBar, 300);

            loggedRectangle.setVisible(true);
            fadeIn(loggedRectangle, 300);

            headImage.setImage(new Image("https://minotar.net/helm/" + auth.getUsername() + "/120.png"));
            headImage.setVisible(true);
            fadeIn(headImage, 300);

            updateRectangle.setVisible(true);
            fadeIn(updateRectangle, 300);

            loggedRectangle.setVisible(true);
            fadeIn(loggedRectangle, 300);

            accountLabel.setVisible(true);
            fadeIn(accountLabel, 300);
        });
        this.fadeOut(this.usernameField, 300).setOnFinished(event -> usernameField.setVisible(false));
        this.fadeOut(this.passwordField, 300).setOnFinished(event -> passwordField.setVisible(false));
        this.fadeOut(this.settingsButton, 300).setOnFinished(event -> settingsButton.setVisible(false));
        this.fadeOut(this.microsoftButton, 300).setOnFinished(event -> microsoftButton.setVisible(false));

        this.updateThread = new Thread(() -> {
            updater = new GameUpdater(prepareGameUpdate(updater, gameEngine, auth, jsonFile), gameEngine);
            gameEngine.reg(updater);
            Timeline t = new Timeline(new KeyFrame(Duration.seconds(0.0D), event -> {
                double percent = (gameEngine.getGameUpdater().downloadedFiles * 100.0D / gameEngine.getGameUpdater().filesToDownload / 100.0D);
                progressBar.setProgress(percent);
                updateLabel.setText(gameEngine.getGameUpdater().getUpdateText());
            }), new KeyFrame(Duration.seconds(0.1D)));
            t.setCycleCount(Animation.INDEFINITE);
            t.play();
            downloadGameAndRun(updater, auth);
        });
        this.updateThread.start();
    }

    private void setFontSize(float size) {
        this.customFont = FontLoader.loadFont("Comfortaa-Regular.ttf", "Comfortaa", size);
    }
}
