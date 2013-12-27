package com.mmiillkkaa.supernaturals.commands;

import org.bukkit.entity.Player;

import com.mmiillkkaa.supernaturals.io.SNConfigHandler;
import com.mmiillkkaa.supernaturals.io.SNWhitelistHandler;

public class SNCommandJoin extends SNCommand {

    public SNCommandJoin() {
        permissions = "";
        senderMustBePlayer = true;
        senderMustBeSupernatural = false;
        helpNameAndParams = "sn join";
        helpDescription = "加入任一個有趣的超自然生物體驗!";
    }

    @Override
    public void perform() {
        if (!SNConfigHandler.enableJoinCommand) {
            this.sendMessage("這個功能並沒有開放, 你已在體驗超自然生物的樂趣中!");
            return;
        }
        Player senderPlayer = (Player) sender;
        if (SNWhitelistHandler.playersInWhitelist.contains(senderPlayer
                .getName())) {
            this.sendMessage("你已在白名單內!");
            return;
        }
        SNWhitelistHandler.addPlayer(senderPlayer.getName());
    }

}
