package com.xiii.libertycity.launcher;

import com.xiii.libertycity.launcher.gamesaver.GameSaver;
import fr.trxyy.alternative.alternative_apiv2.base.GameEngine;
import fr.trxyy.alternative.alternative_apiv2.settings.GameInfos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;

public class LauncherSettings extends JPanel implements ActionListener {

    public JLabel titleLabel;
    private Font stratumFont;

    public JLabel resolutionLabel, vmLabel;
    public JTextField resolutionField, vmField;
    private JButton saveSettingsButton;
    private JToggleButton keepLauncherOpenButton;
    private GameEngine engine;
    private File launcherSettingsFile;

    public LauncherSettings(GameEngine engin) {
        this.engine = engin;
        this.setLayout(null);
        this.setBackground(new Color(84, 89, 87, 200));

        InputStream stream2 = LauncherPanel.class.getResourceAsStream("/resources/font/StratumM.ttf");
        try {
            stratumFont = Font.createFont(Font.TRUETYPE_FONT, stream2).deriveFont(30f);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        com.xiii.libertycity.launcher.gamesaver.GameSaver saver = new com.xiii.libertycity.launcher.gamesaver.GameSaver(engine);
        GameInfos savedInfos = saver.readConfig();

        this.titleLabel = new JLabel();
        this.titleLabel.setText("Paramètres du launcher");
        this.titleLabel.setForeground(Color.black);
        this.titleLabel.setFont(stratumFont.deriveFont(35f));
        this.titleLabel.setBounds(17, 5, 350, 45);
        this.add(this.titleLabel);

        this.resolutionField = new JTextField();
        this.resolutionField.setForeground(Color.black);
        this.resolutionField.setBackground(new Color(255, 255, 255, 255));
        this.resolutionField.setBorder(null);
        this.resolutionField.setText(savedInfos.getResolution());
        this.resolutionField.setHorizontalAlignment(JTextField.CENTER);
        this.resolutionField.setBounds(20, 80, 130, 20);
        this.resolutionField.setFont(stratumFont.deriveFont(15f));
        this.add(this.resolutionField);

        this.resolutionLabel = new JLabel();
        this.resolutionLabel.setText("Resolution (WxH)");
        this.resolutionLabel.setForeground(Color.black);
        this.resolutionLabel.setFont(stratumFont.deriveFont(20f));
        this.resolutionLabel.setBounds(17, 40, 350, 45);
        this.add(this.resolutionLabel);

        this.vmLabel = new JLabel();
        this.vmLabel.setText("Arguments VM");
        this.vmLabel.setForeground(Color.black);
        this.vmLabel.setFont(stratumFont.deriveFont(20f));
        this.vmLabel.setBounds(20, 100, 350, 45);
        this.add(this.vmLabel);

        this.vmField = new JTextField();
        this.vmField.setForeground(Color.black);
        this.vmField.setBackground(new Color(255, 255, 255, 255));
        this.vmField.setBorder(null);
        this.vmField.setText(savedInfos.getVmArguments());
        this.vmField.setHorizontalAlignment(JTextField.CENTER);
        this.vmField.setBounds(20, 140, 575, 20);
        this.add(this.vmField);

        this.saveSettingsButton = new JButton("Sauvegarder");
        this.saveSettingsButton.setForeground(Color.BLACK);
        this.saveSettingsButton.setBounds(440, 100, 150, 30);
        this.saveSettingsButton.setFont(stratumFont.deriveFont(20F));
        this.saveSettingsButton.addActionListener(this);
        this.add(this.saveSettingsButton);

        this.keepLauncherOpenButton = new JToggleButton("Garder le launcher ouvert", readKeepOpen());
        this.keepLauncherOpenButton.setForeground(Color.BLACK);
        this.keepLauncherOpenButton.setBounds(180, 80, 200, 20);
        this.keepLauncherOpenButton.setFont(stratumFont.deriveFont(15F));
        this.keepLauncherOpenButton.addActionListener(this);
        this.add(this.keepLauncherOpenButton);
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (e.getSource().equals(this.saveSettingsButton)) {
            GameInfos gameInfo = new GameInfos();
            gameInfo.setResolution(resolutionField.getText());
            gameInfo.setVmArguments(vmField.getText());
            GameSaver gameSaver = new GameSaver(gameInfo, this.engine);
            gameSaver.saveSettings();
            JDialog topFrame = (JDialog) SwingUtilities.getWindowAncestor(this);
            topFrame.dispose();



        }

        if(e.getSource().equals(this.keepLauncherOpenButton)) {
            String fileName = "keepLauncherOpen";
            File folder = new File(this.engine.getGameFolder().getGameDir(), "private/settings");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            this.launcherSettingsFile = new File(this.engine.getGameFolder().getGameDir(), "private/settings/" + fileName);
            try {
                if (!this.launcherSettingsFile.exists()) this.launcherSettingsFile.createNewFile();
                FileWriter fw = new FileWriter(this.launcherSettingsFile);
                fw.write(String.valueOf(this.keepLauncherOpenButton.getModel().isSelected()));
                fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public boolean readKeepOpen() {
        String fileName = "keepLauncherOpen";
        File folder = new File(this.engine.getGameFolder().getGameDir(), "private/settings");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        this.launcherSettingsFile = new File(this.engine.getGameFolder().getGameDir(), "private/settings/" + fileName);
        if (!this.launcherSettingsFile.exists()) {
            try {
                this.launcherSettingsFile.createNewFile();
                FileWriter fw = new FileWriter(this.launcherSettingsFile);
                fw.write("true");
                fw.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                BufferedReader br = new BufferedReader(new FileReader(this.launcherSettingsFile));
                String line = br.readLine();
                return Boolean.parseBoolean(line);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}