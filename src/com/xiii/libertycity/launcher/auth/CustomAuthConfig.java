package com.xiii.libertycity.launcher.auth;

import fr.trxyy.alternative.alternative_apiv2.base.GameEngine;
import fr.trxyy.alternative.alternative_apiv2.minecraft.utils.GameUtils;
import fr.trxyy.alternative.alternative_apiv2.minecraft.utils.JsonUtil;
import fr.trxyy.alternative.alternative_authv2.base.EnumAuthConfig;
import fr.trxyy.alternative.alternative_authv2.base.Logger;
import fr.trxyy.alternative.alternative_authv2.microsoft.model.MicrosoftModel;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CustomAuthConfig {
    public File authConfig;
    public MicrosoftModel microsoftModel;
    public boolean read = false;

    public CustomAuthConfig(GameEngine engine) {
        this.authConfig = new File(engine.getGameFolder().getGameDir(), "auth_infos.json");
    }

    public boolean canRefresh() {
        this.authConfig = GameUtils.getWorkingDirectory("libertyCity/auth_infos.json");
        return this.authConfig.exists();
    }

    public void createConfigFile(MicrosoftModel model) {
        if (!this.authConfig.exists()) {
            try {
                this.authConfig.createNewFile();
            } catch (IOException var5) {
                var5.printStackTrace();
            }
        }

        JSONObject configDetails = new JSONObject();
        configDetails.put(EnumAuthConfig.ACCESS_TOKEN.getOption(), model.getAccess_token());
        configDetails.put(EnumAuthConfig.REFRESH_TOKEN.getOption(), model.getRefresh_token());
        configDetails.put(EnumAuthConfig.USER_ID.getOption(), model.getUser_id());
        configDetails.put(EnumAuthConfig.SCOPE.getOption(), model.getScope());
        configDetails.put(EnumAuthConfig.TOKEN_TYPE.getOption(), model.getToken_type());
        configDetails.put(EnumAuthConfig.EXPIRES_IN.getOption(), model.getExpires_in());
        configDetails.put(EnumAuthConfig.FOCI.getOption(), model.getFoci());

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
        String configJson = JsonUtil.getGson().toJson(this.microsoftModel);
        JSONObject jsonObject = (JSONObject) JSONValue.parse(configJson);
        return jsonObject.get(option.getOption());
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
                this.microsoftModel = (MicrosoftModel)JsonUtil.getGson().fromJson(json, MicrosoftModel.class);
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
        Iterator var4 = values.entrySet().iterator();

        while(var4.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry)var4.next();
            jsonObject.put(entry.getKey(), entry.getValue());
        }

        try {
            FileWriter fileWriter = new FileWriter(this.authConfig);
            JsonUtil.getGson().toJson(jsonObject, fileWriter);
            //fileWriter.flush();
            fileWriter.close();
        } catch (IOException var6) {
            var6.printStackTrace();
        }

    }

    public void updateValues(MicrosoftModel model) {
        HashMap<String, String> values = new HashMap();
        values.put(EnumAuthConfig.ACCESS_TOKEN.getOption(), model.getAccess_token());
        values.put(EnumAuthConfig.REFRESH_TOKEN.getOption(), model.getRefresh_token());
        values.put(EnumAuthConfig.USER_ID.getOption(), model.getUser_id());
        values.put(EnumAuthConfig.SCOPE.getOption(), model.getScope());
        values.put(EnumAuthConfig.TOKEN_TYPE.getOption(), model.getToken_type());
        values.put(EnumAuthConfig.EXPIRES_IN.getOption(), model.getExpires_in());
        values.put(EnumAuthConfig.FOCI.getOption(), model.getFoci());
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
