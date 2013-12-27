package com.mmiillkkaa.supernaturals.commands;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.mmiillkkaa.supernaturals.SupernaturalsPlugin;
import com.mmiillkkaa.supernaturals.io.SNConfigHandler;

public class SNCommandSetup extends SNCommand {

    public int stepNumber = 0;

    public SNCommandSetup() {
        super();
        requiredParameters = new ArrayList<String>();
        optionalParameters = new ArrayList<String>();
        senderMustBePlayer = true;
        senderMustBeSupernatural = false;
        permissions = "supernatural.command.setup";
    }

    @Override
    public void perform() {
        if (!(sender instanceof Player)) {
            this.sendMessage("對不起, 這個指令只能在遊戲內使用.");
            return;
        }
        Player player = (Player) sender;
        if (SupernaturalsPlugin.hasPermissions(player, permissions)) {
            if (stepNumber == 0) {
                this.sendMessage("哈囉! 這個指令將會幫助你設定 mmSupernaturals!");
                this.sendMessage("如果要繼續, 再次輸入 /sn setup.");
                stepNumber = 1;
            } else if (stepNumber == 1) {
                this.sendMessage("首先, 我們要設定教堂.");
                this.sendMessage("為了達到這個目的, 蓋一個祭壇, 並且放置一個 "
                        + SNConfigHandler.priestAltarMaterial.toLowerCase()
                                .replace("_", " ") + " 在祭壇中間.");
                this.sendMessage("如果要繼續, 再次輸入 /sn setup.");
                stepNumber = 2;
            } else if (stepNumber == 2) {
                this.sendMessage("在你祭壇的 "
                        + SNConfigHandler.priestAltarMaterial.toLowerCase()
                                .replace("_", " ") + " 10 格內,");
                this.sendMessage("輸入指令 /sn setChurch 來允許玩家使用這個祭壇.");
                this.sendMessage("玩家可以透過右鍵點擊 "
                        + SNConfigHandler.priestAltarMaterial.toLowerCase()
                                .replace("_", " ") + " 來使用祭壇.");
                this.sendMessage("如果要繼續, 再次輸入 /sn setup.");
                stepNumber = 3;
            } else if (stepNumber == 3) {
                this.sendMessage("Okay, 接下來, 我們要設定女巫獵人的大廳.");
                this.sendMessage("在任意鐵門(Iron Door)的旁邊, 擺放一個告示排並在第2行輸入 "
                        + SNConfigHandler.hunterHallMessage + " .");
                this.sendMessage("設定完成! 恭喜!");
                stepNumber = 0;
            }
        } else {
            if (!SNConfigHandler.spanish) {
                this.sendMessage("你沒有權限使用這個指令.");
            } else {
                this.sendMessage("No tienes permiso para usar este comando.");
            }
        }
    }

}
