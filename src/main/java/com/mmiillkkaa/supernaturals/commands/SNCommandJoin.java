package com.mmiillkkaa.supernaturals.commands;

import org.bukkit.entity.Player;

import com.mmiillkkaa.supernaturals.io.SNConfigHandler;
import com.mmiillkkaa.supernaturals.io.SNWhitelistHandler;
import com.mmiillkkaa.supernaturals.util.Language;

public class SNCommandJoin extends SNCommand {

    public SNCommandJoin() {
        permissions = "";
        senderMustBePlayer = true;
        senderMustBeSupernatural = false;
        helpNameAndParams = "sn join";
        helpDescription = "Join in on the mmSupernatuals fun!";
    }

    @Override
    public void perform() {
        if (!SNConfigHandler.enableJoinCommand) {
            this.sendMessage(Language.JOIN_NOT_ENABLE.toString());
            return;
        }
        Player senderPlayer = (Player) sender;
        if (SNWhitelistHandler.playersInWhitelist.contains(senderPlayer
                .getName())) {
            this.sendMessage(Language.ALREADY_WHITELISTED.toString());
            return;
        }
        SNWhitelistHandler.addPlayer(senderPlayer.getName());
    }

}
