package com.xiii.libertycity.launcher.auth;

import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.trxyy.alternative.alternative_apiv2.base.GameEngine;
import fr.trxyy.alternative.alternative_apiv2.minecraft.utils.GameUtils;
import fr.trxyy.alternative.alternative_apiv2.minecraft.utils.JsonUtil;
import fr.trxyy.alternative.alternative_authv2.base.EnumAuthConfig;
import fr.trxyy.alternative.alternative_authv2.base.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NewAuthConfig {
    public File authConfig;
    public MicrosoftAuthResult microsoftModel;
    public boolean read = false;
    public GameEngine engine;

    public NewAuthConfig(GameEngine engine) {
        this.engine = engine;
        this.authConfig = new File(engine.getGameFolder().getGameDir(), "auth_infosTest.json");
    }

    public boolean canRefresh() {
        this.authConfig = new File(engine.getGameFolder().getGameDir(), "auth_infosTest.json");
        return this.authConfig.exists();
    }

    public void createConfigFile(MicrosoftAuthResult model) {
        if (!this.authConfig.exists()) {
            try {
                this.authConfig.createNewFile();
            } catch (IOException var5) {
                var5.printStackTrace();
            }
        }

        JSONObject configDetails = new JSONObject();
        configDetails.put(EnumAuthConfig.ACCESS_TOKEN.getOption(), model.getAccessToken());
        configDetails.put(EnumAuthConfig.REFRESH_TOKEN.getOption(), model.getRefreshToken());

        try {
            FileWriter fw = new FileWriter(this.authConfig);
            JsonUtil.getGson().toJson(configDetails, fw);
            //fw.flush();
            fw.close();
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    public Object getValue(EnumAuthConfig option) {
        String configJson = null;
        try {
            configJson = loadJSON(this.authConfig.toURI().toURL().toString());
            JSONObject jsonObject = (JSONObject) JSONValue.parse(configJson);
            return jsonObject.get(option.getOption());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public void loadConfiguration() {
        String json = null;

        try {
            json = loadJSON(this.authConfig.toURI().toURL().toString());
            this.read = true;
        } catch (IOException var6) {
            Logger.err("ERROR !!!");
            var6.printStackTrace();
        } finally {
            if (this.read) {
                this.microsoftModel = (MicrosoftAuthResult)JsonUtil.getGson().fromJson(json, MicrosoftAuthResult.class);
            }

        }

    }

    public void updateValue(String toUpdate, Object value) {
        this.loadConfiguration();
        String configJson = JsonUtil.getGson().toJson(this.microsoftModel);
        JSONObject jsonObject = (JSONObject)JSONValue.parse(configJson);
        jsonObject.put(toUpdate, value);

        try {
            FileWriter fileWriter = new FileWriter(this.authConfig);
            JsonUtil.getGson().toJson(jsonObject, fileWriter);
            //fileWriter.flush();
            fileWriter.close();
        } catch (IOException var6) {
            var6.printStackTrace();
        }

    }

    public void updateValues(HashMap<String, String> values) {
        this.loadConfiguration();
        String configJson = JsonUtil.getGson().toJson(this.microsoftModel);
        JSONObject jsonObject = (JSONObject)JSONValue.parse(configJson);
        jsonObject.putAll(values);

        try {
            FileWriter fileWriter = new FileWriter(this.authConfig);
            JsonUtil.getGson().toJson(jsonObject, fileWriter);
            //fileWriter.flush();
            fileWriter.close();
        } catch (IOException var6) {
            var6.printStackTrace();
        }

    }

    public void updateValues(MicrosoftAuthResult model) {
        HashMap<String, String> values = new HashMap<>();
        values.put(EnumAuthConfig.ACCESS_TOKEN.getOption(), model.getAccessToken());
        values.put(EnumAuthConfig.REFRESH_TOKEN.getOption(), model.getRefreshToken());
        this.updateValues(values);
    }

    public void updateValues(String accessToken, String refreshToken) {
        HashMap<String, String> values = new HashMap<>();
        values.put(EnumAuthConfig.ACCESS_TOKEN.getOption(), accessToken);
        values.put(EnumAuthConfig.REFRESH_TOKEN.getOption(), refreshToken);
        this.updateValues(values);
    }

    public String loadJSON(String inUrl) throws IOException {
        URL url = new URL(inUrl);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        String json;
        String inputLine;
        for(json = ""; (inputLine = in.readLine()) != null; json = json + inputLine) {
        }
        in.close();
        return json;
    }
}
