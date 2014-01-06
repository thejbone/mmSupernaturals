package com.mmiillkkaa.supernaturals.util;

import org.bukkit.ChatColor;

import com.mmiillkkaa.supernaturals.util.Language;

public enum Color {

    HUMAN("&f"),
    VAMPIRE("&5"),
    WEREWOLF("&9"),
    GHOUL("&8"),
    PRIEST("&6"),
    WITCHHUNTER("&a"),
    DEMON("&c"),
    ANGEL("&b"),
    ;

    private String code;

    Color(String code) {
        this.code = code;
    }

    public String prefix(String name) {
        return  ChatColor.translateAlternateColorCodes('&', this.code + name);
    }

    public String prefix(Language lang) {
        return  ChatColor.translateAlternateColorCodes('&', this.code + lang.toString());
    }

    public String name(String name) {
        return  ChatColor.translateAlternateColorCodes('&', this.code + name.toString() + "&f");
    }

    public String name(Language lang) {
        return  ChatColor.translateAlternateColorCodes('&', this.code + lang.toString() + "&f");
    }

}
