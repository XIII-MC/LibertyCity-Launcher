package com.xiii.libertycity.launcher;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
// Made by DukeinPro with massive Brain <3
public class Downloader implements Runnable {

    private final String link;
    private final File out;

    public Downloader(String link, File out) {
        this.link = link;
        this.out = out;
    }

    @Override
    public void run() {
        try {
            URL url = new URL(link);
            URLConnection http = url.openConnection();
            http.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
            BufferedInputStream in = new BufferedInputStream(http.getInputStream());
            FileOutputStream outputStream = new FileOutputStream(this.out);
            System.out.println("Missing file (" + this.out.getName() + ") undetected on client, downloading...");
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer, 0, 1024)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.close();
            in.close();

        } catch (IOException e) {
            System.out.println("Couldn't download file from the distant server.");
            e.printStackTrace();
        }
    }
}
