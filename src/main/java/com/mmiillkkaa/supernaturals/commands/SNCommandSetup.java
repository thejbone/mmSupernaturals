package com.mmiillkkaa.supernaturals.commands;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.mmiillkkaa.supernaturals.SupernaturalsPlugin;
import com.mmiillkkaa.supernaturals.io.SNConfigHandler;
import com.mmiillkkaa.supernaturals.util.Language;
import com.mmiillkkaa.supernaturals.util.LanguageTag;

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
            this.sendMessage(Language.ONLY_IN_GAME.toString());
            return;
        }
        Player player = (Player) sender;
        if (SupernaturalsPlugin.hasPermissions(player, permissions)) {
            if (stepNumber == 0) {
                this.sendMessage(Language.SN_SETUP_CHURCH_GREETING.toString());
                this.sendMessage(Language.SN_SETUP_CONTINUE.toString());
                stepNumber = 1;
            } else if (stepNumber == 1) {
                this.sendMessage(Language.SN_SETUP_CHURCH_GREETING.toString());
                this.sendMessage(Language.SN_SETUP_CHURCH_ALTAR.toString()
                        .replace(
                                LanguageTag.MATERIAL.toString(),
                                SNConfigHandler.priestAltarMaterial
                                        .toLowerCase().replace("_", " ")));

                this.sendMessage(Language.SN_SETUP_CONTINUE.toString());
                stepNumber = 2;
            } else if (stepNumber == 2) {
                this.sendMessage(Language.SN_SETUP_CHURSH_WITHIN_BLOCK
                        .toString().replace(
                                LanguageTag.MATERIAL.toString(),
                                SNConfigHandler.priestAltarMaterial
                                        .toLowerCase().replace("_", " ")));
                this.sendMessage(Language.SN_SETUP_CHRUSH_CMD.toString());
                this.sendMessage(Language.SN_SETUP_CHRUSH_USAGE.toString()
                        .replace(
                                LanguageTag.MATERIAL.toString(),
                                SNConfigHandler.priestAltarMaterial
                                        .toLowerCase().replace("_", " ")));
                this.sendMessage(Language.SN_SETUP_CONTINUE.toString());
                stepNumber = 3;
            } else if (stepNumber == 3) {
                this.sendMessage(Language.SN_SETUP_HALL_GREETING.toString());
                this.sendMessage(Language.SN_SETUP_HALL_SIGN.toString()
                        .replace(LanguageTag.MSG.toString(),
                                SNConfigHandler.hunterHallMessage));
                this.sendMessage(Language.SN_SETUP_FINISH.toString());
                stepNumber = 0;
            }
        } else {
            this.sendMessage(Language.NO_PREMISSION.toString());
        }
    }
}
