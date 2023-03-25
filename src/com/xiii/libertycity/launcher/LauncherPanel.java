package com.xiii.libertycity.launcher;

import fr.trxyy.alternative.alternative_api_uiv2.components.LauncherButton;
import fr.trxyy.alternative.alternative_api_uiv2.components.LauncherImage;
import fr.trxyy.alternative.alternative_api_uiv2.components.LauncherLabel;
import fr.trxyy.alternative.alternative_api_uiv2.components.LauncherProgressBar;
import fr.trxyy.alternative.alternative_apiv2.base.GameEngine;
import fr.trxyy.alternative.alternative_apiv2.base.IScreen;
import fr.trxyy.alternative.alternative_apiv2.build.GameRunner;
import fr.trxyy.alternative.alternative_apiv2.minecraft.utils.GameUtils;
import fr.trxyy.alternative.alternative_apiv2.updater.GameUpdater;
import fr.trxyy.alternative.alternative_apiv2.utils.FontLoader;
import fr.trxyy.alternative.alternative_authv2.base.GameAuth;
import fr.trxyy.alternative.alternative_authv2.base.Session;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class LauncherPanel extends IScreen {

    /** INTERNALS */
    private final GameEngine gameEngine;
    private final File authFile = GameUtils.getWorkingDirectory("libertycity/auth_infos.json");

    /** TOP */
    private final LauncherLabel topLabel;
    private final LauncherImage topMinecraftLogo;
    private final LauncherButton topReduceButton;
    private final LauncherButton topCloseButton;

    /** SOCIAL LINKS */
    private final LauncherButton discordButton;
    private final LauncherButton twitterButton;
    private final LauncherButton youtubeButton;
    private final LauncherButton siteButton;

    /** LOGIN */
    private final LauncherButton playButton;
    private final LauncherButton loginButton;
    private GameAuth gameAuth;
    private Session gameSession;
    private Rectangle loggedRectangle;
    private LauncherImage headImage;
    private LauncherLabel accountLabel;
    private LauncherLabel accountNameLabel;

    /** SETTINGS */
    private final LauncherButton settingsButton;

    /** UPDATE */
    private LauncherProgressBar progressBar;
    private LauncherLabel updateLabel;
    private Thread updateThread;
    private GameUpdater gameUpdater;

    public LauncherPanel(Pane root, GameEngine engine) {
        this.gameEngine = engine;




        /* Top Rectangle */
        this.drawRect(root, 0, 0, engine.getWidth(), 31, Color.rgb(0, 0, 0, 0.7));

        /* Top Label */
        this.topLabel = new LauncherLabel(root);
        this.topLabel.setText("LibertyCity | Launcher");
        this.topLabel.setFont(getFont(18F));
        this.topLabel.addStyle(getFxTransparent());
        this.topLabel.addStyle(getFxWhiteText());
        this.topLabel.setBounds(engine.getWidth() / 2 - 80, -4, 500, 40);

        /* Top Minecraft logo next to Label */
        this.topMinecraftLogo = new LauncherImage(root, loadImage(engine, "minecraft.png"));
        this.topMinecraftLogo.setBounds(engine.getWidth() / 3 + 45, 4, 25, 25);

        /* Top right reduce button */
        this.topReduceButton = new LauncherButton(root);
        this.topReduceButton.setInvisible();
        this.topReduceButton.setBounds(engine.getWidth() - 75, 0, 25, 25);
        final LauncherImage reduceButton = new LauncherImage(root, loadImage(engine, "reduce.png"));
        reduceButton.setSize(25, 25);
        this.topReduceButton.setGraphic(reduceButton);
        this.topReduceButton.setOnAction(event -> {
            Stage stage = (Stage) ((LauncherButton) event.getSource()).getScene().getWindow();
            stage.setIconified(true);
        });

        /* Top right close button */
        this.topCloseButton = new LauncherButton(root);
        this.topCloseButton.setInvisible();
        this.topCloseButton.setBounds(engine.getWidth() - 45, 0, 25, 25);
        final LauncherImage closeButton = new LauncherImage(root, loadImage(engine, "close.png"));
        closeButton.setSize(25, 25);
        this.topCloseButton.setGraphic(closeButton);
        this.topCloseButton.setOnAction(event -> System.exit(0));

        /* Bottom rectangle */
        this.drawRect(root, 0, engine.getHeight() - 110, engine.getWidth(), 300, Color.rgb(0, 0, 0, 0.5));

        /* Social links buttons */
        this.discordButton = new LauncherButton(root);
        this.discordButton.setInvisible();
        this.discordButton.setBounds(5, engine.getHeight() - 55, 62, 47);
        final LauncherImage discordImage = new LauncherImage(root, loadImage(engine, "discord.png"));
        discordImage.setSize(62, 47);
        this.discordButton.setGraphic(discordImage);
        this.discordButton.setOnAction(event -> {
            openLink("discord.com");
        });

        this.twitterButton = new LauncherButton(root);
        this.twitterButton.setInvisible();
        this.twitterButton.setBounds(8, engine.getHeight() - 120, 74, 74);
        final LauncherImage twitterButton = new LauncherImage(root, loadImage(engine, "twitter.png"));
        twitterButton.setSize(64, 64);
        this.twitterButton.setGraphic(twitterButton);
        this.twitterButton.setOnAction(event -> {
            openLink("twitter.com");
        });

        this.youtubeButton = new LauncherButton(root);
        this.youtubeButton.setInvisible();
        this.youtubeButton.setBounds(75, engine.getHeight() - 60, 64, 64);
        final LauncherImage youtubeImage = new LauncherImage(root, loadImage(engine, "youtube.png"));
        youtubeImage.setSize(64, 64);
        this.youtubeButton.setGraphic(youtubeImage);
        this.youtubeButton.setOnAction(event -> {
            openLink("youtube.com");
        });

        this.siteButton = new LauncherButton(root);
        this.siteButton.setInvisible();
        this.siteButton.setBounds(80, engine.getHeight() - 111, 64, 64);
        final LauncherImage siteImage = new LauncherImage(root, loadImage(engine, "site.png"));
        siteImage.setSize(54, 54);
        this.siteButton.setGraphic(siteImage);
        this.siteButton.setOnAction(event -> {
            openLink("https://www.libertycity.fr");
        });

        /* Play button */
        this.playButton = new LauncherButton(root);
        if (LauncherMain.getServerStatus()) {
            this.playButton.setText("Jouer");
            this.playButton.addStyle(getFxColor(61, 61, 61));
            this.playButton.setOnAction(event -> {
                if (gameAuth != null && gameAuth.isLogged()) {
                    gameSession = gameAuth.getSession();
                    File jsonFile = downloadVersion(engine.getGameLinks().getJsonUrl(), engine); //
                    updateGame(gameSession, jsonFile, root);
                }
            });
            //this.playButton.setOpacity(0.5D);
        } else {
            this.playButton.setText("Maintenance");
            this.playButton.addStyle(getFxColor(255, 0, 0));
            this.playButton.setOpacity(0.2D);

        }
        this.playButton.setUnHover(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                playButton.setOpacity(0.5D);
            }
        });
        this.playButton.setHover(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                playButton.setOpacity(0.4D);
            }
        });
        this.playButton.setFont(getFont(22F));
        this.playButton.setBounds(engine.getWidth() - 190, engine.getHeight() - 100, engine.getWidth() - 50, engine.getHeight() - 80);
        this.playButton.setSize(180, 60);
        this.playButton.addStyle(getFxWhiteText());
        this.playButton.setOpacity(0.5D);

        /* Settings button */
        this.settingsButton = new LauncherButton(root);
        this.settingsButton.setInvisible();
        this.settingsButton.setBounds(engine.getWidth() - 198, engine.getHeight() - 38, 35, 35);
        LauncherImage settingsImage = new LauncherImage(root, loadImage(engine, "settings.png"));
        settingsImage.setSize(28, 28);
        this.settingsButton.setGraphic(settingsImage);
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

        /* Microsoft login button */
        this.loginButton = new LauncherButton("Connexion", root);
        this.loginButton.setFont(getFont(12.5F));
        this.loginButton.setBounds(engine.getWidth() - 155, engine.getHeight() - 35, 145, 10);
        this.loginButton.addStyle(getFxColor(0, 120, 0));
        this.loginButton.addStyle(getFxWhiteText());
        this.loginButton.setOnAction(event -> {
            if (gameAuth == null) gameAuth = new GameAuth();
            if (gameAuth.isLogged()) {
                if (LauncherMain.getServerStatus()) {
                    this.playButton.addStyle(getFxColor(61, 61, 61));
                }
                this.loginButton.addStyle(getFxColor(0, 120, 0));
                this.loginButton.setText("Connexion");
            } else {
                this.loginButton.addStyle(getFxColor(255, 160, 0));
                this.loginButton.setText("Connexion...");
                try {
                    final BufferedReader br = new BufferedReader(new FileReader(authFile));
                    if (br.readLine() == null) FileUtils.forceDelete(authFile);
                } catch (IOException ignored) {
                }
                showMicrosoftAuth(engine, gameAuth);
                gameSession = gameAuth.getSession();
                if (LauncherMain.getServerStatus()) {
                    this.playButton.addStyle(getFxColor(0, 120, 0));
                    this.playButton.setOpacity(1.0D);
                }
                if (gameAuth.isAuthenticated) {
                    this.loginButton.addStyle(getFxColor(120, 0, 0));
                    this.loginButton.setText("Déconnexion");

                    this.loggedRectangle = this.drawRect(root, this.gameEngine.getWidth() - 250, 70, 220, 250, Color.rgb(0, 0, 0, 0.7));
                    this.loggedRectangle.setVisible(false);
                    this.loggedRectangle.setOpacity(0.0D);

                    this.headImage = new LauncherImage(root);
                    this.headImage.setVisible(false);
                    this.headImage.setFitWidth(32);
                    this.headImage.setFitHeight(32);
                    this.headImage.setLayoutX(this.gameEngine.getWidth() - 235);
                    this.headImage.setLayoutY(82);
                    this.headImage.setOpacity(0.0D);

                    this.accountLabel = new LauncherLabel(root);
                    this.accountLabel.setVisible(false);
                    this.accountLabel.setText("Votre Profil");
                    this.accountLabel.setFont(getItalicFont(32F));
                    this.accountLabel.setAlignment(Pos.CENTER_LEFT);
                    this.accountLabel.setBounds(this.gameEngine.getWidth() - 220, 30, 220, 40);
                    this.accountLabel.addStyle(getFxWhiteText());
                    this.accountLabel.setOpacity(0.0D);

                    this.accountNameLabel = new LauncherLabel(root);
                    this.accountNameLabel.setVisible(false);
                    this.accountNameLabel.setText(gameSession.getUsername());
                    this.accountNameLabel.setFont(getFont(22F));
                    this.accountNameLabel.setAlignment(Pos.CENTER_LEFT);
                    this.accountNameLabel.setBounds(this.gameEngine.getWidth() - 190, 85, 220, 32);
                    this.accountNameLabel.addStyle(getFxWhiteText());
                    this.accountNameLabel.setOpacity(0.0D);

                    this.loggedRectangle.setVisible(true);
                    fadeIn(this.loggedRectangle, 500);

                    this.headImage.setImage(new Image("https://minotar.net/helm/" + gameSession.getUsername() + "/120.png"));
                    this.headImage.setVisible(true);
                    this.fadeIn(this.headImage, 500);

                    this.accountLabel.setVisible(true);
                    fadeIn(this.accountLabel, 500);

                    this.accountNameLabel.setVisible(true);
                    fadeIn(this.accountNameLabel, 500);
                } else {

                    if (LauncherMain.getServerStatus()) {
                        this.playButton.addStyle(getFxColor(61, 61, 61));
                    }
                    this.loginButton.addStyle(getFxColor(0, 120, 0));
                    this.loginButton.setText("Connexion");
                }
            }
        });
    }

    private void updateGame(Session auth, File jsonFile, Pane root) {

        this.updateLabel = new LauncherLabel(root);
        this.updateLabel.setVisible(false);
        this.updateLabel.setText("Démarage...");
        this.updateLabel.setFont(getItalicFont(12F));
        this.updateLabel.setBounds(this.gameEngine.getWidth() - 190, this.gameEngine.getHeight() - 18, 200, 10);
        this.updateLabel.addStyle("-fx-text-fill: white;");
        this.updateLabel.setOpacity(0.0D);

        this.progressBar = new LauncherProgressBar(root);
        this.progressBar.setVisible(false);
        this.progressBar.setBounds(this.gameEngine.getWidth() -190, this.gameEngine.getHeight() - 35, 180, 10);
        this.progressBar.setOpacity(0.0D);

        this.fadeOut(this.settingsButton, 500).setOnFinished(settingsButtonEvent -> this.settingsButton.setVisible(false));
        this.fadeOut(this.loginButton, 500).setOnFinished(event -> {
            this.loginButton.setVisible(false);

            this.fadeOut(this.playButton, 500).setOnFinished(playButtonEvent -> {
                this.playButton.setVisible(false);
                this.playButton.addStyle(getFxColor(255, 160, 0));
                this.playButton.setText("Mise à jour...");
                this.playButton.setVisible(true);
                this.updateLabel.setVisible(true);
                fadeIn(this.playButton, 500);

                this.fadeOut(this.progressBar, 500).setOnFinished(progressBarEvent -> {
                    this.progressBar.setVisible(true);
                    fadeIn(this.progressBar, 500);

                    this.fadeOut(this.updateLabel, 500).setOnFinished(updateLabelEvent -> {
                        this.updateLabel.setVisible(true);
                        fadeIn(this.updateLabel, 500);
                    });
                });
            });
        });

        this.updateThread = new Thread() {
            public void run() {
                gameUpdater = new GameUpdater(prepareGameUpdate(gameUpdater, gameEngine, auth, jsonFile), gameEngine);
                gameEngine.reg(gameUpdater);
                LauncherModDownloader.downloadMods();
                Timeline t = new Timeline(new KeyFrame(Duration.seconds(0.0D), new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent event) {
                        double percent = (gameEngine.getGameUpdater().downloadedFiles * 100.0D / gameEngine.getGameUpdater().filesToDownload / 100.0D);
                        progressBar.setProgress(percent);
                        updateLabel.setText(gameEngine.getGameUpdater().getUpdateText());
                    }
                }, new KeyValue[0]), new KeyFrame(Duration.seconds(0.1D), new KeyValue[0]));
                t.setCycleCount(Animation.INDEFINITE);
                t.play();
                downloadGameAndRun(gameUpdater, auth);
            }
        };
        this.updateThread.start();
    }

    private Font getFont(final float size) {
        return FontLoader.loadFont("Comfortaa-Regular.ttf", "Comfortaa", size);
    }

    private Font getItalicFont(final float size) {
        return FontLoader.loadFontItalic("Comfortaa-Regular.ttf", "Comfortaa", size);
    }

    private String getFxTransparent() {
        return "-fx-background-color: transparent;";
    }

    private String getFxColor(final int red, final int green, final int blue) {
        return "-fx-background-color: rgb(" + red + ", " + green + ", " + blue + ");";
    }

    private String getFxWhiteText() {
        return "-fx-text-fill: white;";
    }

    private void clearAuthCache() {

        try {
            FileOutputStream fooStream = new FileOutputStream(authFile, false);

            fooStream.write("".getBytes());
            fooStream.close();
            gameAuth = new GameAuth();
            gameSession = null;
            authFile.delete();
        } catch (IOException ignored) {
        }
    }
}