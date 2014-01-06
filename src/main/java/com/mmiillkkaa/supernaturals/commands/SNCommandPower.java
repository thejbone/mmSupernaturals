/*
 * Supernatural Players Plugin for Bukkit
 * Copyright (C) 2011  Matt Walker <mmw167@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.mmiillkkaa.supernaturals.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.mmiillkkaa.supernaturals.SuperNPlayer;
import com.mmiillkkaa.supernaturals.SupernaturalsPlugin;
import com.mmiillkkaa.supernaturals.manager.SuperNManager;
import com.mmiillkkaa.supernaturals.util.Language;
import com.mmiillkkaa.supernaturals.util.LanguageTag;

public class SNCommandPower extends SNCommand {

    public SNCommandPower() {
        super();
        requiredParameters = new ArrayList<String>();
        optionalParameters = new ArrayList<String>();
        senderMustBePlayer = true;
        senderMustBeSupernatural = true;
        permissions = "supernatural.command.power";
        optionalParameters.add("playername");
        optionalParameters.add("power");
        helpNameAndParams = "power [amount] | power [playername] [amount]";
        helpDescription = "See current power level";
    }

    @Override
    public void perform() {

        Player senderPlayer = (Player) sender;
        String permissions2 = "supernatural.admin.command.power";

        if (parameters.isEmpty()) {
            if (!SupernaturalsPlugin.hasPermissions(senderPlayer, permissions)) {
                this.sendMessage(Language.NO_PREMISSION.toString());
                return;
            }
            SuperNPlayer snplayer = SuperNManager.get(senderPlayer);

            this.sendMessage(Language.POWER_INFO
                    .toString()
                    .replace(LanguageTag.TYPE.toString(), snplayer.getType())
                    .replace(LanguageTag.POWER.toString(),
                            Double.toString(snplayer.getPower())));
        } else {
            if (!SupernaturalsPlugin.hasPermissions(senderPlayer, permissions2)) {
                this.sendMessage(Language.NO_PREMISSION.toString());
                return;
            }
            if (parameters.size() == 1) {
                double powerGain;

                try {
                    powerGain = Double.parseDouble(parameters.get(0));
                } catch (NumberFormatException e) {
                    this.sendMessage(Language.INVALID_NUMBER.toString());
                    return;
                }
                if (powerGain >= 10000D) {
                    powerGain = 9999;
                }

                SuperNPlayer snplayer = SuperNManager.get(senderPlayer);
                SuperNManager.alterPower(snplayer, powerGain,
                        Language.ADMIN_BOOST.toString());
            } else {
                String playername = parameters.get(0);
                Player player = SupernaturalsPlugin.instance.getServer()
                        .getPlayer(playername);
                if (player == null) {
                    this.sendMessage(Language.PLAYER_NOT_FOUND.toString());
                    return;
                }
                double powerGain;

                try {
                    powerGain = Double.parseDouble(parameters.get(1));
                } catch (NumberFormatException e) {
                    this.sendMessage(Language.INVALID_NUMBER.toString());
                    return;
                }
                if (powerGain >= 10000D) {
                    powerGain = 9999;
                }
                this.sendMessage(Language.PLAYER_POWER_UP.toString());
                SuperNPlayer snplayer = SuperNManager.get(player);
                SuperNManager.alterPower(snplayer, powerGain,
                        Language.ADMIN_BOOST.toString());
            }
        }
    }
}
