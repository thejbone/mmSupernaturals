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
    public static String language;
    public static String defaultLanguage;
    public static ArrayList<String> languageFiles;

    public SNLanguageHandler(SupernaturalsPlugin instance) {
        SNLanguageHandler.plugin = instance;
        SNLanguageHandler.configDir = "language";
        SNLanguageHandler.defaultLanguage = "en";
        SNLanguageHandler.languageFiles = new ArrayList<String>(Arrays.asList(
                defaultLanguage, "zh_TW"));
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

        // init & check language files
        for (String lang : languageFiles) {
            File file = getLanguageFile(lang);
            if (lang != defaultLanguage) {
                try {
                    if (!file.exists()) {
                        plugin.saveResource(
                                String.format("%s/%s.yml", configDir, lang),
                                true);
                    }
                } catch (Exception e) {
                    SupernaturalsPlugin.log(Level.WARNING, String.format(
                            "Can not create the language file: %s.yml - %s!!",
                            lang, e));
                }
            }
            config = loadValues(file);
            saveConfig(config, file);
        }

        // load config language
        language = SNConfigHandler.language;
        SupernaturalsPlugin.log(Level.INFO,
                String.format("Use language file: %s.yml", language));
        File file = getLanguageFile(SNConfigHandler.language);
        if (file.exists()) {
            config = loadValues(file);
        } else {
            // load from util.Language
            SupernaturalsPlugin.log(Level.INFO, String.format(
                    "Fail to loading Language file: %s.yml, use default!",
                    language));
            config = loadValues();
        }
    }

    public static File getLanguageFile(String lang) {
        File file = new File(String.format("%s/%s/%s.yml", plugin
                .getDataFolder().getAbsolutePath(), configDir, lang));
        return file;
    }

    public static YamlConfiguration loadValues(File file) {
        config = YamlConfiguration.loadConfiguration(file);
        for (Language l : Language.values()) {
            config.set(l.getPath(), config.getString(l.getPath(), l.getDef()));
        }
        return config;
    }

    public static YamlConfiguration loadValues(YamlConfiguration config) {
        for (Language l : Language.values()) {
            config.set(l.getPath(), config.getString(l.getPath(), l.getDef()));
        }
        return config;
    }

    public static YamlConfiguration loadValues() {
        config = new YamlConfiguration();
        for (Language l : Language.values()) {
            config.set(l.getPath(), config.getString(l.getPath(), l.getDef()));
        }
        return config;
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

    public static void reloadConfig() {
        File file = getLanguageFile(language);
        config = loadValues(file);
    }

    public static YamlConfiguration getConfig() {
        return config;
    }
}
