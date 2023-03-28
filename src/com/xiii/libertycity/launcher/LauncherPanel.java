package com.xiii.libertycity.launcher;

import com.xiii.libertycity.launcher.auth.CustomAuth;
import com.xiii.libertycity.launcher.auth.CustomCopy;
import com.xiii.libertycity.launcher.downloader.LauncherDownloader;
import com.xiii.libertycity.launcher.utils.VarUtil;
import fr.trxyy.alternative.alternative_api_uiv2.components.LauncherButton;
import fr.trxyy.alternative.alternative_api_uiv2.components.LauncherImage;
import fr.trxyy.alternative.alternative_api_uiv2.components.LauncherLabel;
import fr.trxyy.alternative.alternative_api_uiv2.components.LauncherProgressBar;
import fr.trxyy.alternative.alternative_apiv2.base.GameEngine;
import fr.trxyy.alternative.alternative_apiv2.base.IScreen;
import fr.trxyy.alternative.alternative_apiv2.base.LauncherPane;
import fr.trxyy.alternative.alternative_apiv2.minecraft.utils.GameUtils;
import fr.trxyy.alternative.alternative_apiv2.updater.GameUpdater;
import fr.trxyy.alternative.alternative_apiv2.utils.FontLoader;
import fr.trxyy.alternative.alternative_authv2.base.Session;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;


import javax.net.ssl.HttpsURLConnection;
import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Objects;

public class LauncherPanel extends IScreen {

    /** INTERNALS */
    private final GameEngine gameEngine;
    private final File authFile = GameUtils.getWorkingDirectory("libertycity/auth_infos.json");
    private final DecimalFormat f = new DecimalFormat("00.00");
    public static VarUtil varUtil = new VarUtil();

    /** TOP */
    private final LauncherLabel topLabel;
    private final LauncherImage topMinecraftLogo;
    private final LauncherButton topReduceButton;
    private final LauncherButton topCloseButton;
    private final LauncherLabel topCreditsLabel;

    /** SOCIAL LINKS */
    private final LauncherButton discordButton;
    private final LauncherButton twitterButton;
    private final LauncherButton youtubeButton;
    private final LauncherButton siteButton;

    /** LOGIN */
    private final LauncherButton playButton;
    private final LauncherButton loginButton;
    private CustomAuth gameAuth;
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
    private LauncherLabel updatePercentage;
    private Thread updateThread;
    private GameUpdater gameUpdater;

    /** BOTTOM */
    private LauncherLabel annoucementLabel;
    private LauncherLabel patchNoteLabel;

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

        /* Top Credits Label */
        this.topCreditsLabel = new LauncherLabel(root);
        this.topCreditsLabel.setText("Powered By AlternativeAPI also credits to DukeinPro and XIII"); // You might want to change this if you want!
        this.topCreditsLabel.setFont(getFont(12F));
        this.topCreditsLabel.addStyle(getFxTransparent());
        this.topCreditsLabel.addStyle(getFxWhiteText());
        this.topCreditsLabel.setBounds(10, -4, 500, 40);

        WebView browser = new WebView();
        browser.setLayoutY(engine.getHeight() - 110 - 486.5);
        browser.setLayoutX(engine.getWidth() - 372.5);
        browser.setScaleY(0.89);
        browser.setScaleX(0.79);
        browser.setPrefHeight(520);
        browser.setVisible(true);
        browser.getEngine().load("https://libertycity-libs.wstr.fr/v5/libs/www/lc/discordWidget.html");
        browser.getEngine().reload();
        final com.sun.webkit.WebPage webPage = com.sun.javafx.webkit.Accessor.getPageFor(browser.getEngine());
        webPage.setBackgroundColor(0);
        root.getChildren().add(browser);


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
            openLink("https://libertycity-libs.wstr.fr");
        });

        /* Play button */
        this.playButton = new LauncherButton(root);
        if (LauncherMain.getServerStatus()) {
            this.playButton.setText("Jouer");
            this.playButton.addStyle(getFxColor(61, 61, 61));
            this.playButton.setOnAction(event -> {
                if (gameAuth != null && gameAuth.isLogged() && !LauncherMain.isBanned()) {
                    gameSession = gameAuth.getSession();
                    File jsonFile = downloadVersion(engine.getGameLinks().getJsonUrl(), engine); //
                    updateGame(gameSession, jsonFile, root);
                }
            });
        } else {
            this.playButton.setText("Maintenance");
            this.playButton.addStyle(getFxColor(255, 0, 0));
            this.playButton.setOpacity(0.5D);

        }
        this.playButton.setUnHover(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                if (gameAuth == null || LauncherMain.isBanned() || !gameAuth.isLogged() || !gameAuth.isAuthenticated || !LauncherMain.getServerStatus()) playButton.setOpacity(0.5D);
                else if (gameAuth != null && !LauncherMain.isBanned() && gameAuth.isLogged() && LauncherMain.getServerStatus()) playButton.setOpacity(1.0D);
            }
        });
        this.playButton.setHover(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                if (gameAuth == null || LauncherMain.isBanned() || !gameAuth.isLogged() || !gameAuth.isAuthenticated || !LauncherMain.getServerStatus()) playButton.setOpacity(0.5D);
                else if (gameAuth != null && !LauncherMain.isBanned() && gameAuth.isLogged() && LauncherMain.getServerStatus()) playButton.setOpacity(0.85D);
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
            if (gameAuth == null) gameAuth = new CustomAuth();
            if (gameAuth.isLogged()) {
                if (LauncherMain.getServerStatus()) {
                    this.playButton.addStyle(getFxColor(61, 61, 61));
                }
                this.loginButton.addStyle(getFxColor(0, 120, 0));
                this.loginButton.setText("Connexion");
                this.loginButton.setOpacity(1.0D);
                this.playButton.addStyle(getFxColor(61, 61, 61));
                this.playButton.setOpacity(0.5D);
                authFile.delete();
                gameAuth = new CustomAuth();
                headImage.setVisible(false);
                accountNameLabel.setVisible(false);
            } else {
                this.loginButton.addStyle(getFxColor(255, 160, 0));
                this.loginButton.setText("Connexion...");
                this.loginButton.setOpacity(0.5D);
                CustomCopy customCopy = new CustomCopy();
                customCopy.showMicrosoftAuth(engine, gameAuth);
                gameSession = gameAuth.getSession();
                if (LauncherMain.getServerStatus()) {
                    this.playButton.addStyle(getFxColor(0, 120, 0));
                    this.playButton.setOpacity(1.0D);
                }
                if (gameAuth.isAuthenticated) {
                    try {
                        final HttpsURLConnection urlConnection = (HttpsURLConnection) new URL("https://libertycity-libs.wstr.fr/v5/libs/www/lc/launcher_http/banlist.json").openConnection();
                        final BufferedReader inputStream = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        if (Objects.equals(gameAuth.getSession().getUuid(), inputStream.readLine())) LauncherMain.setBanned(true);
                        urlConnection.getInputStream().close();
                    } catch (IOException ignored) {
                    }

                    this.loginButton.addStyle(getFxColor(120, 0, 0));
                    this.loginButton.setText("Déconnexion");
                    this.loginButton.setOpacity(1.0D);

                    if (LauncherMain.isBanned()) {
                        this.playButton.addStyle(getFxColor(255, 0, 0));
                        this.playButton.setText("BANNI");
                        this.playButton.setFont(getFont(28F));
                        this.playButton.setOpacity(0.5D);
                        return;
                    }

                    this.headImage = new LauncherImage(root);
                    this.headImage.setVisible(false);
                    this.headImage.setFitWidth(32);
                    this.headImage.setFitHeight(32);
                    this.headImage.setLayoutX(this.gameEngine.getWidth() - 368);
                    this.headImage.setLayoutY(engine.getHeight() - 40);
                    this.headImage.setOpacity(0.0D);

                    this.accountNameLabel = new LauncherLabel(root);
                    this.accountNameLabel.setVisible(false);
                    this.accountNameLabel.setText(gameAuth.getSession().getUsername());
                    this.accountNameLabel.setFont(getFont(12F));
                    this.accountNameLabel.setAlignment(Pos.CENTER_LEFT);
                    this.accountNameLabel.setBounds(this.gameEngine.getWidth() - 330, engine.getHeight() - 40, 165, 28);
                    this.accountNameLabel.addStyle(getFxWhiteText());
                    this.accountNameLabel.setOpacity(0.0D);

                    this.headImage.setImage(new Image("https://minotar.net/helm/" + gameSession.getUsername() + "/120.png"));
                    this.headImage.setVisible(true);
                    this.fadeIn(this.headImage, 300);

                    this.accountNameLabel.setVisible(true);
                    fadeIn(this.accountNameLabel, 300);
                } else {

                    if (LauncherMain.getServerStatus()) {
                        this.playButton.addStyle(getFxColor(61, 61, 61));
                    }
                    this.loginButton.addStyle(getFxColor(0, 120, 0));
                    this.loginButton.setText("Connexion");
                    this.loginButton.setOpacity(1.0D);
                }
            }
        });

        /* Annoucements */
        this.drawRect(root, 160, engine.getHeight() - 100, 5, 90, Color.rgb(0, 140, 255, 0.85D));

        this.annoucementLabel = new LauncherLabel(root);
        this.annoucementLabel.setText(getAnnouncement(47, 5));
        this.annoucementLabel.setFont(getFont(13F));
        this.annoucementLabel.addStyle(getFxWhiteText());
        this.annoucementLabel.setBounds(170, engine.getHeight() - 135, 400, 90 + 70);

        /* Launcher Patchnote */
        this.drawRect(root, engine.getWidth() - 375, engine.getHeight() - 100, 5, 45, Color.rgb(40, 190, 0, 0.85D));
        // 24 for patchNotes
        this.patchNoteLabel = new LauncherLabel(root);
        this.patchNoteLabel.setText(getLauncherPatchNotes(24, 3));
        this.patchNoteLabel.setFont(getFont(10F));
        this.patchNoteLabel.addStyle(getFxWhiteText());
        this.patchNoteLabel.setBounds(engine.getWidth() - 367, engine.getHeight() - 115, 165, 45 + 30);

        /* Player profile */
        this.drawRect(root, engine.getWidth() - 375, engine.getHeight() - 45, 5, 40, Color.rgb(200, 0, 0, 0.85D));
    }

    private void updateGame(Session auth, File jsonFile, Pane root) {

        this.updateLabel = new LauncherLabel(root);
        this.updateLabel.setVisible(false);
        this.updateLabel.setText("Démarage...");
        this.updateLabel.setFont(getItalicFont(12F));
        this.updateLabel.setBounds(this.gameEngine.getWidth() - 190, this.gameEngine.getHeight() - 18, 200, 10);
        this.updateLabel.addStyle(getFxWhiteText());
        this.updateLabel.setOpacity(0.0D);

        this.updatePercentage = new LauncherLabel(root);
        this.updatePercentage.setVisible(false);
        this.updatePercentage.setText("0%");
        this.updatePercentage.setFont(getItalicFont(12F));
        this.updatePercentage.setBounds(this.gameEngine.getWidth() - 45, this.gameEngine.getHeight() - 18, 200, 10);
        this.updatePercentage.addStyle(getFxWhiteText());
        this.updatePercentage.setOpacity(0.0D);

        this.progressBar = new LauncherProgressBar(root);
        this.progressBar.setVisible(false);
        this.progressBar.setBounds(this.gameEngine.getWidth() -190, this.gameEngine.getHeight() - 35, 180, 10);
        this.progressBar.setOpacity(0.0D);

        this.fadeOut(this.settingsButton, 300).setOnFinished(settingsButtonEvent -> this.settingsButton.setVisible(false));
        this.fadeOut(this.loginButton, 300).setOnFinished(event -> {
            this.loginButton.setVisible(false);

            this.fadeOut(this.playButton, 300).setOnFinished(playButtonEvent -> {
                this.playButton.setVisible(false);
                this.playButton.addStyle(getFxColor(255, 160, 0));
                this.playButton.setText("Mise à jour...");
                this.playButton.setVisible(true);
                fadeIn(this.playButton, 300);

                this.fadeOut(this.progressBar, 300).setOnFinished(progressBarEvent -> {
                    this.progressBar.setVisible(true);
                    fadeIn(this.progressBar, 300);

                    this.fadeOut(this.updateLabel, 300).setOnFinished(updateLabelEvent -> {
                        this.updateLabel.setVisible(true);
                        fadeIn(this.updateLabel, 300);

                        this.fadeOut(this.updatePercentage, 300).setOnFinished(updatePercentageEvent -> {
                            this.updatePercentage.setVisible(true);
                            fadeIn(this.updatePercentage, 300);
                        });
                    });
                });
            });
        });

        this.updateThread = new Thread(() -> {
            gameUpdater = new GameUpdater(prepareGameUpdate(gameUpdater, gameEngine, auth, jsonFile), gameEngine);
            gameEngine.reg(gameUpdater);
            LauncherDownloader.downloadMods();
            LauncherDownloader.downloadAddons();
            LauncherDownloader.downloadRessourcePacks();
            Timeline t = new Timeline(new KeyFrame(Duration.seconds(0.0D), event -> {
                double percent = (gameEngine.getGameUpdater().downloadedFiles * 100.0D / gameEngine.getGameUpdater().filesToDownload / 100.0D);
                updatePercentage.setText(f.format(percent * 100) + "%");
                progressBar.setProgress(percent);
                updateLabel.setText(gameEngine.getGameUpdater().getUpdateText());
            }, new KeyValue[0]), new KeyFrame(Duration.seconds(0.1D), new KeyValue[0]));
            t.setCycleCount(Animation.INDEFINITE);
            t.play();
            CustomCopy.downloadGameAndRun(gameEngine, gameUpdater, prepareGameUpdate(gameUpdater, gameEngine, auth, jsonFile), auth);
        });
        this.updateThread.start();
        Platform.runLater(new Runnable() {
            public void run() {
                varUtil.isRunning = true;
            }
        });

        new Thread(() -> {
            try {

                Thread.sleep(1500);
                while (varUtil.isRunning) {
                    Thread.sleep(5);
                }
                showFirstScreen();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }).start();
    }

    public void showFirstScreen() {
        this.fadeOut(this.updateLabel, 300).setOnFinished(updateLabelEvent -> this.updateLabel.setVisible(false));
        this.fadeOut(this.updatePercentage, 300).setOnFinished(updatePercentageEvent -> this.updatePercentage.setVisible(false));
        this.fadeOut(this.progressBar, 300).setOnFinished(progressBarEvent -> this.progressBar.setVisible(false));
        this.fadeIn(this.loginButton, 300).setOnFinished(loginButtonEvent -> this.loginButton.setVisible(true));
        this.fadeIn(this.settingsButton, 300).setOnFinished(settingsButtonEvent -> this.settingsButton.setVisible(true));
        this.fadeIn(this.playButton, 1).setOnFinished(playButtonEvent -> this.playButton.setText("Jouer"));
        this.fadeIn(this.playButton, 1).setOnFinished(playButtonEvent -> this.playButton.addStyle(getFxColor(0, 120, 0)));
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
            gameAuth = new CustomAuth();
            gameSession = null;
            authFile.delete();
        } catch (IOException ignored) {
        }
    }

    private String getAnnouncement(int splitAtCharNumber, int showNumberOfLines) {
        try {
            String lines;
            final HttpsURLConnection urlConnection = (HttpsURLConnection) new URL("https://libertycity-libs.wstr.fr/v5/libs/www/lc/launcher_http/annoucement.json").openConnection();
            final BufferedReader inputStream = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            int count = 0;
            int didTimes = 0;
            boolean stop = false;
            boolean wasFirstLine = true;
            boolean didNewLine = false;
            while (!stop) {
                lines = inputStream.readLine();
                if(!wasFirstLine && !didNewLine) {
                   // System.out.println("" + lines);
                    count = 0;
                    stringBuilder.append("\n");
                    didTimes++;
                    if (didTimes >= showNumberOfLines) {
                        stop = true;
                    }
                }
                didNewLine = false;
                for (char c : lines.toCharArray()) {
                    if (!stop) {
                        count++;
                        stringBuilder.append(c);
                        if (count >= splitAtCharNumber) {
                            didNewLine = true;
                            stringBuilder.append("\n");
                            count = 0;
                            didTimes++;
                            if (didTimes >= showNumberOfLines) {
                                stop = true;
                            }
                        }
                    }
                }
                wasFirstLine = false;
            }

            urlConnection.getInputStream().close();
            return stringBuilder.toString();
        } catch (IOException ignored) {
        }
        return null;
    }

    private String getLauncherPatchNotes(int splitAtCharNumber, int showNumberOfLines) {
        try {
            String lines;
            final HttpsURLConnection urlConnection = (HttpsURLConnection) new URL("https://libertycity-libs.wstr.fr/v5/libs/www/lc/launcher_http/launcher_patchnote.json").openConnection();
            final BufferedReader inputStream = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            int count = 0;
            int didTimes = 0;
            boolean stop = false;
            boolean wasFirstLine = true;
            boolean didNewLine = false;
            while (!stop) {
                lines = inputStream.readLine();
                if(!wasFirstLine && !didNewLine) {
                    //System.out.println("" + lines);
                    count = 0;
                    stringBuilder.append("\n");
                    didTimes++;
                    if (didTimes >= showNumberOfLines) {
                        stop = true;
                    }
                }
                didNewLine = false;
                for (char c : lines.toCharArray()) {
                    if (!stop) {
                        count++;
                        stringBuilder.append(c);
                        if (count >= splitAtCharNumber) {
                            didNewLine = true;
                            stringBuilder.append("\n");
                            count = 0;
                            didTimes++;
                            if (didTimes >= showNumberOfLines) {
                                stop = true;
                            }
                        }
                    }
                }
                wasFirstLine = false;
            }

            urlConnection.getInputStream().close();
            return stringBuilder.toString();
        } catch (IOException ignored) {
        }
        return null;
    }

    /*private String getLauncherPatchNotes() {
        try {
            final String lines;
            final HttpsURLConnection urlConnection = (HttpsURLConnection) new URL("https://libertycity-libs.wstr.fr/v5/libs/www/lc/launcher_http/launcher_patchnote.json").openConnection();
            final BufferedReader inputStream = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            lines = inputStream.readLine();
            urlConnection.getInputStream().close();
            return lines;
        } catch (IOException ignored) {
        }
        return null;
    } */
}