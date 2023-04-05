package com.xiii.libertycity.launcher.gamerunner;

import com.xiii.libertycity.launcher.LauncherPanel;
import fr.trxyy.alternative.alternative_apiv2.base.GameEngine;
import fr.trxyy.alternative.alternative_apiv2.base.GameFolder;
import fr.trxyy.alternative.alternative_apiv2.minecraft.json.Argument;
import fr.trxyy.alternative.alternative_apiv2.minecraft.json.ArgumentType;
import fr.trxyy.alternative.alternative_apiv2.minecraft.json.MinecraftVersion;
import fr.trxyy.alternative.alternative_apiv2.minecraft.utils.FileUtil;
import fr.trxyy.alternative.alternative_apiv2.minecraft.utils.GameUtils;
import fr.trxyy.alternative.alternative_apiv2.minecraft.utils.OperatingSystem;
import fr.trxyy.alternative.alternative_apiv2.settings.GameInfos;
import fr.trxyy.alternative.alternative_apiv2.settings.GameSaver;
import fr.trxyy.alternative.alternative_authv2.base.Session;
import javafx.application.Platform;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.io.*;
import java.util.*;

public class GameRunner {
    private final MinecraftVersion minecraftVersion;
    private final GameEngine engine;
    private final GameFolder workDir;
    private final Session session;
    private final GameInfos savedInfos;

    public GameRunner(Session ses, MinecraftVersion mcVersion, GameEngine engin) {
        this.session = ses;
        this.minecraftVersion = mcVersion;
        this.engine = engin;
        this.workDir = this.engine.getGameFolder();
        FileUtil.deleteFolder(this.workDir.getNativesDir());
        this.unpackNatives();
        GameSaver saver = new GameSaver(this.engine);
        this.savedInfos = saver.readConfig();
        if(!readKeepOpen()) {
            Platform.runLater(new Runnable() {
                public void run() {
                    GameRunner.this.engine.getStage().hide();
                }
            });
        }

    }

    public boolean readKeepOpen() {
        String fileName = "keepLauncherOpen";
        File folder = new File(this.engine.getGameFolder().getGameDir(), "private/settings");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File keepOpenSettingsFile = new File(this.engine.getGameFolder().getGameDir(), "private/settings/" + fileName);
        if (!keepOpenSettingsFile.exists()) {
            try {
                keepOpenSettingsFile.createNewFile();
                FileWriter fw = new FileWriter(keepOpenSettingsFile);
                fw.write("true");
                fw.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                BufferedReader br = new BufferedReader(new FileReader(keepOpenSettingsFile));
                String line = br.readLine();
                return Boolean.parseBoolean(line);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public void launch() throws Exception {
        ArrayList<String> commands = this.getLaunchCommand();
        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
        processBuilder.directory(this.workDir.getGameDir());
        processBuilder.redirectErrorStream(true);
        String cmds = "";

        String command;
        for(Iterator var4 = commands.iterator(); var4.hasNext(); cmds = cmds + command + " ") {
            command = (String)var4.next();
        }

        String[] ary = cmds.split(" ");
        System.out.println("Launching: " + hideAccessToken(ary));

        try {

            Process process = processBuilder.start();
            //VarUtil.INSTANCE.isRunning = true;
            process.waitFor();
            int exitVal = process.exitValue();
            if (exitVal != 0) {
                System.out.println("Minecraft has crashed.");
            }
            Platform.runLater(new Runnable() {
                public void run() {
                    LauncherPanel.varUtil.isRunning = false;
                }
            });
            if(!readKeepOpen()) System.exit(0);


        } catch (IOException var7) {
            throw new Exception("Cannot launch !", var7);
        }
    }

    private ArrayList<String> getLaunchCommand() {
        ArrayList<String> commands = new ArrayList();
        OperatingSystem os = OperatingSystem.getCurrentPlatform();
        String arguments1;
        if (this.minecraftVersion.getJavaVersion() != null) {
            arguments1 = this.minecraftVersion.getJavaVersion().getComponent();
            if (arguments1 != null) {
                commands.add(OperatingSystem.getJavaPath(this.minecraftVersion, this.engine));
            } else {
                commands.add(OperatingSystem.getJavaPath());
            }
        } else {
            commands.add(OperatingSystem.getJavaPath());
        }

        if (os.equals(OperatingSystem.OSX)) {
            commands.add("-Xdock:name=Minecraft");
            commands.add("-Xdock:icon=" + this.workDir.getAssetsDir() + "icons/minecraft.icns");
        } else if (os.equals(OperatingSystem.WINDOWS)) {
            if (this.minecraftVersion.getJavaVersion() == null) {
                commands.add("-XX:+UseConcMarkSweepGC");
            }

            if (this.minecraftVersion.getArguments() == null) {
                commands.add("-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump");
            }
        }

        if (this.minecraftVersion.getJavaVersion() != null) {
            commands.add("-XX:+UnlockExperimentalVMOptions");
            commands.add("-XX:+UseG1GC");
            commands.add("-XX:G1NewSizePercent=20");
            commands.add("-XX:G1ReservePercent=20");
            commands.add("-XX:MaxGCPauseMillis=50");
            commands.add("-XX:G1HeapRegionSize=32M");
            commands.add("-Dfml.ignoreInvalidMinecraftCertificates=true");
            commands.add("-Dfml.ignorePatchDiscrepancies=true");
            commands.add("-Djava.net.preferIPv4Stack=true");
            commands.add("-Dminecraft.applet.TargetDirectory=" + this.workDir.getGameDir());
        }

        if (this.minecraftVersion.getLogging() != null) {
            File log4jFile = new File(this.workDir.getLogConfigsDir(), this.minecraftVersion.getLogging().getClient().getFile().getId());
            commands.add(this.minecraftVersion.getLogging().getClient().getArgument().replace("${path}", log4jFile.getAbsolutePath()));
        }

        arguments1 = this.savedInfos.getVmArguments();
        String[] str2 = arguments1.split(" ");
        List<String> args2 = Arrays.asList(str2);
        commands.addAll(args2);
        List argsNewer;
        String[] defaultArguments;
        StringBuffer sb;
        int i;
        if (this.minecraftVersion.getArguments() != null) {
            argsNewer = (List)this.minecraftVersion.getArguments().get(ArgumentType.JVM);
            defaultArguments = this.getJvmArguments(argsNewer);
            sb = new StringBuffer();

            for(i = 0; i < defaultArguments.length; ++i) {
                sb.append(defaultArguments[i] + " ");
            }

            String[] splittedString = sb.toString().split(" ");
            List<String> finaliseList = Arrays.asList(splittedString);
            commands.addAll(finaliseList);
        } else {
            commands.add("-Djava.library.path=" + this.workDir.getNativesDir().getAbsolutePath());
            commands.add("-Dfml.ignoreInvalidMinecraftCertificates=true");
            commands.add("-Dfml.ignorePatchDiscrepancies=true");
            commands.add("-Dminecraft.launcher.brand=Minecraft");
            commands.add("-Dminecraft.launcher.version=999");
            commands.add("-cp");
            commands.add("\"" + GameUtils.constructClasspath(this.minecraftVersion, this.engine) + "\"");
            System.out.println("Using Old Arguments, Putting -cp argument.");
        }

        commands.add(this.minecraftVersion.getMainClass());
        if (this.minecraftVersion.getMinecraftArguments() != null) {
            String[] argsD = this.getArgumentsOlder(this.minecraftVersion.getMinecraftArguments());
            List<String> arguments = Arrays.asList(argsD);
            commands.addAll(arguments);
        }

        if (this.minecraftVersion.getArguments() != null) {
            argsNewer = (List)this.minecraftVersion.getArguments().get(ArgumentType.GAME);
            defaultArguments = this.getArgumentsNewer(argsNewer);
            sb = new StringBuffer();

            for(i = 0; i < defaultArguments.length; ++i) {
                sb.append(defaultArguments[i] + " ");
            }

            String sub = sb.toString().replace("--demo", "").replace("--width", "").replace("--height", "");
            String[] strcs = sub.split(" ");
            List<String> newerList = Arrays.asList(strcs);
            commands.addAll(newerList);
        }

        if (this.engine.getGameConnect() != null) {
            commands.add("--server");
            commands.add(this.engine.getGameConnect().getIp());
            commands.add("--port");
            commands.add(this.engine.getGameConnect().getPort());
        }

        if (this.engine.getGameSize() != null) {
            commands.add("--width");
            commands.add(this.savedInfos.getResolutionWidth());
            commands.add("--height");
            commands.add(this.savedInfos.getResolutionHeight());
        }

        return commands;
    }

    private String[] getArgumentsOlder(String argum) {
        Map<String, String> map = new HashMap();
        StrSubstitutor substitutor = new StrSubstitutor(map);
        String[] split = argum.split(" ");
        map.put("auth_player_name", this.session.getUsername());
        map.put("auth_uuid", this.session.getUuid());
        map.put("auth_access_token", this.session.getToken());
        map.put("user_type", "legacy");
        map.put("version_name", this.minecraftVersion.getId());
        map.put("version_type", "release");
        map.put("game_directory", this.workDir.getGameDir().getAbsolutePath());
        map.put("assets_root", this.workDir.getAssetsDir().getAbsolutePath());
        map.put("assets_index_name", this.minecraftVersion.getAssets());
        map.put("user_properties", "{}");

        for(int i = 0; i < split.length; ++i) {
            split[i] = substitutor.replace(split[i]);
        }

        return split;
    }

    private String[] getArgumentsNewer(List<Argument> args) {
        Map<String, String> map = new HashMap();
        StrSubstitutor substitutor = new StrSubstitutor(map);
        String[] split = new String[args.size()];

        int i;
        for(i = 0; i < args.size(); ++i) {
            split[i] = ((Argument)args.get(i)).getArguments();
        }

        map.put("auth_player_name", this.session.getUsername());
        map.put("auth_uuid", this.session.getUuid());
        map.put("auth_access_token", this.session.getToken());
        map.put("user_type", "msa");
        map.put("version_name", this.minecraftVersion.getId());
        map.put("version_type", "release");
        map.put("game_directory", this.workDir.getGameDir().getAbsolutePath());
        map.put("assets_root", this.workDir.getAssetsDir().getAbsolutePath());
        map.put("assets_index_name", this.minecraftVersion.getAssets());
        map.put("user_properties", "{}");
        map.put("clientid", "2535464861463420");
        map.put("auth_xuid", this.session.getUuid());

        for(i = 0; i < split.length; ++i) {
            split[i] = substitutor.replace(split[i]);
        }

        return split;
    }

    private String[] getJvmArguments(List<Argument> args) {
        Map<String, String> map = new HashMap();
        StrSubstitutor substitutor = new StrSubstitutor(map);
        String[] split = new String[args.size()];

        int i;
        for(i = 0; i < args.size(); ++i) {
            split[i] = ((Argument)args.get(i)).getArguments();
        }

        map.put("natives_directory", this.workDir.getNativesDir().getAbsolutePath());
        map.put("launcher_name", "Minecraft");
        map.put("launcher_version", "999");
        map.put("classpath", GameUtils.constructClasspath(this.minecraftVersion, this.engine));
        map.put("launcher_version", "999");
        map.put("library_directory", this.workDir.getLibsDir().getAbsolutePath());
        map.put("classpath_separator", ";");
        map.put("version_name", this.minecraftVersion.getId());

        for(i = 0; i < split.length; ++i) {
            split[i] = substitutor.replace(split[i]);
        }

        return split;
    }

    private void unpackNatives() {
        try {
            FileUtil.unpackNatives(this.workDir.getNativesDir(), this.engine);
        } catch (IOException var2) {
            System.out.println("Couldn't unpack natives!");
            var2.printStackTrace();
        }
    }

    public static List<String> hideAccessToken(String[] arguments) {
        ArrayList<String> output = new ArrayList();

        for(int i = 0; i < arguments.length; ++i) {
            if (i > 0 && Objects.equals(arguments[i - 1], "--accessToken")) {
                output.add("????????");
            } else {
                output.add(arguments[i]);
            }
        }

        return output;
    }
}
