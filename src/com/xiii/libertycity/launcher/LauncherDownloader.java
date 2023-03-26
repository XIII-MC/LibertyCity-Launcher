package com.xiii.libertycity.launcher;

import com.xiii.libertycity.launcher.downloader.Downloader;
import fr.trxyy.alternative.alternative_apiv2.minecraft.utils.GameUtils;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

// Made by DukeinPro with massive Brain <3
public class LauncherDownloader {

    public static ArrayList<String> mods = new ArrayList<>();
    public static ArrayList<String> whiteListedMods = new ArrayList<>();
    public static ArrayList<String> addons = new ArrayList<>();
    public static ArrayList<String> ressourcePacks = new ArrayList<>();
    public static final File modFolder = GameUtils.getWorkingDirectory("libertycity/mods");
    private static final boolean isDev = true;
    private static final String fileURLMods = "https://libertycity-libs.wstr.fr/v5/libs/www/lc/files/" + (isDev ? "dev" : "game") + "/mods/";
    private static final String fileURLWhitelistedMods = "https://libertycity-libs.wstr.fr/v5/libs/www/lc/files/" + (isDev ? "dev" : "game") + "/whitelisted_mods/";
    private static final String fileURLAddons = "https://libertycity-libs.wstr.fr/v5/libs/www/lc/files/" + (isDev ? "dev" : "game") + "/addons/";
    private static final String fileURLRessourcePacks = "https://libertycity-libs.wstr.fr/v5/libs/www/lc/files/" + (isDev ? "dev" : "game") + "/ressourcepacks/";

    public static void downloadMods() {
        if (!modFolder.exists()) modFolder.mkdir();
        try {
            final HttpsURLConnection urlConnection = (HttpsURLConnection) new URL(fileURLMods).openConnection();
            final BufferedReader inputStream = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String current;

            while ((current = inputStream.readLine()) != null) {
                if (current.startsWith("<tr>") && current.contains(".jar")) { // :)
                    final String[] splitString = current.split(".jar\">");
                    final String jarFileName = splitString[0].replace("<tr><td valign=\"top\"><img src=\"/icons/unknown.gif\" alt=\"[   ]\"></td><td><a href=\"", "") + ".jar";
                    mods.add(jarFileName);
                }
            }
            urlConnection.getInputStream().close();
            final HttpsURLConnection urlConnection2 = (HttpsURLConnection) new URL(fileURLWhitelistedMods).openConnection();
            final BufferedReader inputStream2 = new BufferedReader(new InputStreamReader(urlConnection2.getInputStream()));
            String current2;

            while ((current2 = inputStream2.readLine()) != null) {
                if (current2.startsWith("<tr>") && current2.contains(".jar")) { // :)
                    final String[] splitString = current2.split(".jar\">");
                    final String jarFileName = splitString[0].replace("<tr><td valign=\"top\"><img src=\"/icons/unknown.gif\" alt=\"[   ]\"></td><td><a href=\"", "") + ".jar";
                    whiteListedMods.add(jarFileName);
                }
            }
            urlConnection2.getInputStream().close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (modFolder.listFiles() == null || Objects.requireNonNull(modFolder.listFiles()).length < 1) {
            for (String modName : mods) {
                final String fileURL = fileURLMods + modName;
                Downloader downloader = new Downloader(fileURL, new File(modFolder.getAbsoluteFile() + "\\" + modName));
                downloader.run();
            }
        } else {
            for (File file : Objects.requireNonNull(modFolder.listFiles())) {
                if (!mods.contains(file.getName())) {
                    if (!whiteListedMods.contains(file.getName())) {
                        System.out.println("Unauthorised mod (" + file.getName() + ") detected on client, deleting...");
                        file.delete();
                    } else {
                        System.out.println("Unnecessary mod (" + file.getName() + ") detected on client, ignoring...");
                        try {
                            byte[] sha1 = createSha1(new FileInputStream(file));
                            final String fileURL = fileURLWhitelistedMods + file.getName();
                            if (!Arrays.equals(sha1, getSha1FromURL(fileURL))) {
                                System.out.println("Unoriginal mod (" + file.getName() + ") detected on client, deleting...");
                                file.delete();
                                Downloader downloader = new Downloader(fileURL, new File(modFolder.getAbsoluteFile() + "\\" + file.getName()));
                                downloader.run();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        byte[] sha1 = createSha1(new FileInputStream(file));
                        final String fileURL = fileURLMods + file.getName();
                        if (!Arrays.equals(sha1, getSha1FromURL(fileURL))) {
                            System.out.println("Unoriginal mod (" + file.getName() + ") detected on client, re-downloading...");
                            file.delete();
                            Downloader downloader = new Downloader(fileURL, new File(modFolder.getAbsoluteFile() + "\\" + file.getName()));
                            downloader.run();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
            for (String modName : mods) {
                if (Arrays.stream(Objects.requireNonNull(modFolder.listFiles())).noneMatch(file -> file.getName().equals(modName))) {
                    final String fileURL = fileURLMods + modName;
                    Downloader downloader = new Downloader(fileURL, new File(modFolder.getAbsoluteFile() + "\\" + modName));
                    downloader.run();
                }
            }
        }

    }

    public static void downloadAddons() {
        try {
            final HttpsURLConnection urlConnection = (HttpsURLConnection) new URL(fileURLAddons).openConnection();
            final BufferedReader inputStream = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String current;

            while ((current = inputStream.readLine()) != null) {
                if (current.startsWith("<tr>") && current.contains(".zip")) { // :)
                    final String[] splitString = current.split(".zip\">");
                    final String zipFileName = splitString[0].replace("<tr><td valign=\"top\"><img src=\"/icons/compressed.gif\" alt=\"[   ]\"></td><td><a href=\"", "") + ".zip";
                    addons.add(zipFileName);
                }
            }
            urlConnection.getInputStream().close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String addon : addons) {
            if (GameUtils.getWorkingDirectory("libertycity/" + addon).exists()) {
                try {
                    byte[] sha1 = createSha1(new FileInputStream(GameUtils.getWorkingDirectory("libertycity/" + addon)));
                    if (!Arrays.equals(sha1, getSha1FromURL(fileURLAddons + addon))) {
                        System.out.println("Unoriginal addon (" + GameUtils.getWorkingDirectory("libertycity/" + addon).getName() + ") detected on client, re-downloading...");
                        GameUtils.getWorkingDirectory("libertycity/" + addon).delete();
                        Downloader downloader = new Downloader(fileURLAddons + addon, GameUtils.getWorkingDirectory("libertycity/" + addon));
                        downloader.run();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Downloader downloader = new Downloader(fileURLAddons + addon, GameUtils.getWorkingDirectory("libertycity/" + addon));
                downloader.run();
            }
            final String directoryName = addon.replace(".zip", "");
            final File addonDir = GameUtils.getWorkingDirectory("libertycity/" + directoryName);
            final File addonDirPath = GameUtils.getWorkingDirectory("libertycity\\");
            if (!addonDir.exists()) {
                Unzip unzip = new Unzip(GameUtils.getWorkingDirectory("libertycity/" + addon).getAbsolutePath(), addonDirPath + "\\");
                unzip.unzip();
            } else {
                addonDir.delete();
                Unzip unzip = new Unzip(GameUtils.getWorkingDirectory("libertycity/" + addon).getAbsolutePath(), addonDirPath + "\\");
                unzip.unzip();
            }
            /*List<File> files = getFilesFromZip(GameUtils.getWorkingDirectory("libertycity/" + addon).getAbsolutePath());
            for (File file : Objects.requireNonNull(addonDir.listFiles())) {
                if (!file.isDirectory()) {
                    System.out.println("Test NAme: " + file.getName());
                    try {

                        File file1 = files.stream().filter(fileName -> fileName.getName().equals(file.getName())).findFirst().get();
                        byte[] sha1 = createSha1(new FileInputStream(file));
                        byte[] sha2 = createSha1(new FileInputStream(file1));
                        if(!Arrays.equals(sha1, sha2))
                            System.out.println("" + file.getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            } */

        }
    }

    public static void downloadRessourcePacks() {
        File ressourcePackFolder = GameUtils.getWorkingDirectory("libertycity/ressourcepacks");
        if (!ressourcePackFolder.exists()) ressourcePackFolder.mkdir();
        try {
            final HttpsURLConnection urlConnection = (HttpsURLConnection) new URL(fileURLAddons).openConnection();
            final BufferedReader inputStream = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String current;

            while ((current = inputStream.readLine()) != null) {
                if (current.startsWith("<tr>") && current.contains(".zip")) { // :)
                    final String[] splitString = current.split(".zip\">");
                    final String zipFileName = splitString[0].replace("<tr><td valign=\"top\"><img src=\"/icons/compressed.gif\" alt=\"[   ]\"></td><td><a href=\"", "") + ".zip";
                    ressourcePacks.add(zipFileName);
                }
            }
            urlConnection.getInputStream().close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (ressourcePackFolder.listFiles() != null || Objects.requireNonNull(ressourcePackFolder.listFiles()).length > 0) {
            for (String s : ressourcePacks) {
                for (File file : Objects.requireNonNull(ressourcePackFolder.listFiles())) {

                }
            }
        } else {

        }

    }



    public static byte[] getSha1FromURL(String urlToCheck) {
        try {
            final URL url = new URL(urlToCheck);
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            return createSha1(conn.getInputStream());
        } catch (Exception e) {
            System.out.println("Couldn't establish connection to the distant server.");
        }
        return "".getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] createSha1(InputStream inputStream) throws Exception  {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        int n = 0;
        byte[] buffer = new byte[8192];
        while (n != -1) {
            n = inputStream.read(buffer);
            if (n > 0) {
                digest.update(buffer, 0, n);
            }
        }
        return digest.digest();
    }

    public static List<File> getFilesFromZip(String fileZip) {
        List<File> files = new ArrayList<>();
        try (ZipFile file = new ZipFile(fileZip)) {
            Enumeration zipEntries = file.entries();
            while (zipEntries.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) zipEntries.nextElement();
                if (zipEntry.isDirectory()) {
                    String subDir = fileZip + "\\" +zipEntry.getName();
                    File as = new File(subDir);
                    as.mkdirs();
                } else {
                    File newFile = new File(zipEntry.getName());
                    files.add(newFile);
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }

}
