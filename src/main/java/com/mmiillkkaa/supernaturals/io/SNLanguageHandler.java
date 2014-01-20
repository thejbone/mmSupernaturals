package com.mmiillkkaa.supernaturals.io;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.configuration.file.YamlConfiguration;

import com.mmiillkkaa.supernaturals.SupernaturalsPlugin;
import com.mmiillkkaa.supernaturals.util.Language;
import com.mmiillkkaa.supernaturals.io.SNConfigHandler;

public class SNLanguageHandler {
    public static SupernaturalsPlugin plugin;
    public static YamlConfiguration config;
    public static String configName;
    public static String configDir;
    public static File configFile;
    public static ArrayList<String> languageFiles;

    public SNLanguageHandler(SupernaturalsPlugin instance) {
        SNLanguageHandler.plugin = instance;
        SNLanguageHandler.configDir = "language";
        SNLanguageHandler.languageFiles = new ArrayList<String>(Arrays.asList(
                "en", "zh_TW"));
    }

    public static void getConfiguration() {
        // copy from resources dir
        File dir = new File(String.format("%s/%s/", plugin.getDataFolder()
                .getAbsolutePath(), configDir));
        try {
            Files.createDirectory(dir.toPath());
        } catch (Exception e) {
            SupernaturalsPlugin.log(Level.WARNING, String.format(
                    "Can't create the directory for language files!!", e));
        }
        for (String lang : languageFiles) {
            File file = new File(String.format("%s/%s/%s.yml", plugin
                    .getDataFolder().getAbsolutePath(), configDir, lang));
            if (lang != "en") {
                try {
                    if (!file.exists()) {
                        plugin.saveResource(
                                String.format("%s/%s.yml", configDir, lang),
                                true);
                    }
                } catch (Exception e) {
                    SupernaturalsPlugin.log(Level.WARNING, String.format(
                            "Can not create the language file: %s.yml!!", e,
                            lang));
                }
            }
            loadValues(file);
        }

        // load specify language
        // TODO: Don't backup the language again.
        File file = new File(String.format("%s/%s/%s.yml", plugin
                .getDataFolder().getAbsolutePath(), configDir,
                SNConfigHandler.language));
        loadValues(file);
        SupernaturalsPlugin.log(Level.WARNING, String.format(
                "Use language file: %s.yml", SNConfigHandler.language));
    }

    public static void loadValues(File file) {
        config = YamlConfiguration.loadConfiguration(file);
        for (Language l : Language.values()) {
            config.set(l.getPath(), config.getString(l.getPath(), l.getDef()));
        }
        saveConfig(config, file);
    }

    public static void saveConfig(YamlConfiguration config, File file) {
        try {
            // backup first
            if (file.exists()) {
                File bakFile = new File(file.getAbsolutePath() + ".bak");
                Files.copy(file.toPath(), bakFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            }
            config.save(file);
        } catch (Exception e) {
            SupernaturalsPlugin.log(Level.WARNING,
                    String.format("Language file writing error - %s", e));
        }
    }

    // TODO: Rewrite this method.
    // public static void reloadConfig() {
    // loadValues(config);
    // }

    public static YamlConfiguration getConfig() {
        return config;
    }
}
