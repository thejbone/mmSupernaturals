package com.mmiillkkaa.supernaturals.io;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;

import org.bukkit.configuration.file.YamlConfiguration;

import com.mmiillkkaa.supernaturals.SupernaturalsPlugin;
import com.mmiillkkaa.supernaturals.util.Language;

public class SNLanguageHandler {
    public static SupernaturalsPlugin plugin;
    public static YamlConfiguration config;
    public static String configName;
    public static File configFile;

    public SNLanguageHandler(SupernaturalsPlugin instance) {
        SNLanguageHandler.plugin = instance;
        SNLanguageHandler.configName = "language.yml";
    }

    public static void getConfiguration() {
        configFile = new File(plugin.getDataFolder().getAbsolutePath()
                .toString(), configName);
        config = new YamlConfiguration();
        loadValues(config);
    }

    private static String addQuotes(String def) {
        if (!def.startsWith("\"") && !def.endsWith("\"")) {
            def = "\"" + def + "\"";
        }
        return def;
    }

    public static void loadValues(YamlConfiguration config) {
        try {
            config.load(configFile);
        } catch (Exception e) {
            SupernaturalsPlugin.log(Level.WARNING, String.format(
                    "Language file not found, use default.", e));
        }

        for (Language l : Language.values()) {
            //config.set(l.getPath(), addQuotes(config.getString(l.getPath(), l.getDef())));
            config.set(l.getPath(), config.getString(l.getPath(), l.getDef()));
        }
        saveConfig();
    }

    public static void saveConfig() {
        try {
            //. backup first
            if (Files.exists(configFile.toPath())) {
                File backupFile = new File(configFile.getAbsolutePath() + ".bak");
                Files.copy(configFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            //. write the new config
            config.save(configFile);
        } catch (Exception e) {
            SupernaturalsPlugin.log(Level.WARNING,
                    String.format("Language file writing error - %s", e));
        }
    }

    public static void reloadConfig() {
        loadValues(config);
    }

    public static YamlConfiguration getConfig() {
        return config;
    }
}
