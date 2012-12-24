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
			this.sendMessage("Sorry, this command is player-only.");
			return;
		}
		Player player = (Player) sender;
		if (SupernaturalsPlugin.hasPermissions(player, permissions)) {
			if(stepNumber == 0) {
				this.sendMessage("Hey there! This command will help you setup mmSupernaturals!");
				this.sendMessage("To continue, type /sn setup again.");
				stepNumber = 1;
			} else if (stepNumber == 1) {
				this.sendMessage("First, we will make a Church.");
				this.sendMessage("To do this, make an altar, and put a " + SNConfigHandler.priestAltarMaterial.toLowerCase().replace("_", " ") + " in the center.");
				this.sendMessage("To continue, type /sn setup again.");
				stepNumber = 2;
			} else if (stepNumber == 2) {
				this.sendMessage("Within 10 blocks of your " + SNConfigHandler.priestAltarMaterial.toLowerCase().replace("_", " ") + " in the altar,");
				this.sendMessage("Use the command /sn setChurch to allow players to use the altar.");
				this.sendMessage("Players can use the altar by right-clicking the " + SNConfigHandler.priestAltarMaterial.toLowerCase().replace("_", " "));
				this.sendMessage("To continue, type /sn setup again.");
				stepNumber = 3;
			} else if (stepNumber == 3) {
				this.sendMessage("Okay, next, we have to make a WitchHunter hall.");
				this.sendMessage("Next to an Iron Door, place a sign with " + SNConfigHandler.hunterHallMessage + " on the 2nd line.");
				this.sendMessage("Setup is finished! Congratulations!");
				stepNumber = 0;
			}
		} else {
			if (!SNConfigHandler.spanish) {
				this.sendMessage("You do not have permissions to use this command.");
			} else {
				this.sendMessage("No tienes permiso para usar este comando.");
			}
		}
	}

}
