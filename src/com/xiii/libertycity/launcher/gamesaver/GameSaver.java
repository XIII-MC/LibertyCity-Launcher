package com.xiii.libertycity.launcher.gamesaver;

import fr.trxyy.alternative.alternative_apiv2.base.GameEngine;
import fr.trxyy.alternative.alternative_apiv2.settings.Encoder;
import fr.trxyy.alternative.alternative_apiv2.settings.GameInfos;

import java.io.*;

public class GameSaver {
    public GameInfos infos;
    public File settingsFile;
    private GameEngine engine;

    public GameSaver(GameEngine engin) {
        this.engine = engin;
        String fileName = "game_settings";
        File folder = new File(this.engine.getGameFolder().getGameDir(), "private/settings");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        this.settingsFile = new File(this.engine.getGameFolder().getGameDir(), "private/settings/" + fileName);
        if (!this.settingsFile.exists()) {
            try {
                this.settingsFile.createNewFile();
                this.writeConfig("854x480", "-XX:+UseG1GC -Dsun.rmi.dgc.server.gcInterval=2147483646 -XX:+UnlockExperimentalVMOptions -XX:G1NewSizePercent=20 -XX:G1ReservePercent=20 -XX:MaxGCPauseMillis=51 -XX:G1HeapRegionSize=32M");
            } catch (IOException var5) {
                var5.printStackTrace();
            }
        }

        this.readConfig();
    }

    public GameSaver(GameInfos info, GameEngine engin) {
        this.infos = info;
        this.engine = engin;
    }

    public void saveSettings() {
        String fileName = "game_settings";
        File folder = new File(this.engine.getGameFolder().getGameDir(), "private/settings");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        this.settingsFile = new File(this.engine.getGameFolder().getGameDir(), "private/settings/" + fileName);
        if (!this.settingsFile.exists()) {
            try {
                this.settingsFile.createNewFile();
                this.writeConfig("854x480", "-XX:+UseG1GC -Dsun.rmi.dgc.server.gcInterval=2147483646 -XX:+UnlockExperimentalVMOptions -XX:G1NewSizePercent=20 -XX:G1ReservePercent=20 -XX:MaxGCPauseMillis=51 -XX:G1HeapRegionSize=32M");
            } catch (IOException var4) {
                var4.printStackTrace();
            }
        } else {
            this.writeConfig(this.infos.getResolution(), this.infos.getVmArguments());
        }

    }

    public void writeConfig(String... s) {
        try {
            FileWriter fw = new FileWriter(this.settingsFile);
            fw.write(Encoder.encryptString(s[0]) + ";");
            fw.write(Encoder.encryptString(s[1]));
            fw.close();
        } catch (IOException var3) {
            System.out.println(var3.toString());
        }

    }

    public GameInfos readConfig() {
        GameInfos accountRead = null;

        try {
            BufferedReader br = new BufferedReader(new FileReader(this.settingsFile));
            String line = br.readLine();
            String[] result = line.split(";");
            accountRead = new GameInfos(Encoder.decryptString(result[0]), Encoder.decryptString(result[1]));
            br.close();
        } catch (IOException var5) {
            System.out.println(var5.toString());
        }

        return accountRead;
    }
}
