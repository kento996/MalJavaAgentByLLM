package com.jas.quickstart.core.config;

import com.jas.quickstart.core.utils.YamlUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author ReaJason
 * @since 2024/2/3
 */
@SuppressWarnings("CallToPrintStackTrace")
public class ConfigManager {

    private final String CONFIG_PATH = System.getProperty("user.dir") + File.separator + "conf" + File.separator + "agent.yaml";

    public AgentConfig getConfig() {
        try {
            FileInputStream fileInputStream = new FileInputStream(CONFIG_PATH);
//            return YamlUtil.loadAs(fileInputStream, AgentConfig.class);
            return YamlUtil.loadAgentConfig(fileInputStream);
        } catch (FileNotFoundException e) {
            System.out.println("config init error");
            e.printStackTrace();
        }
        return new AgentConfig();
    }
}
