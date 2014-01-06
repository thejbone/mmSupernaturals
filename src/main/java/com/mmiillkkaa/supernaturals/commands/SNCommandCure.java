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
import com.mmiillkkaa.supernaturals.io.SNConfigHandler;
import com.mmiillkkaa.supernaturals.manager.SuperNManager;
import com.mmiillkkaa.supernaturals.util.Language;
import com.mmiillkkaa.supernaturals.util.LanguageTag;

public class SNCommandCure extends SNCommand {
    public SNCommandCure() {
        super();
        requiredParameters = new ArrayList<String>();
        optionalParameters = new ArrayList<String>();
        senderMustBePlayer = false;
        optionalParameters.add("playername");
        permissions = "supernatural.admin.command.cure";
    }

    @Override
    public void perform() {
        if (!(sender instanceof Player)) {
            if (parameters.isEmpty()) {
                this.sendMessage(Language.MISSING_PLAYER.toString());
            } else {
                String playername = parameters.get(0);
                Player player = SupernaturalsPlugin.instance.getServer()
                        .getPlayer(playername);
                if (player == null) {
                    this.sendMessage(Language.PLAYER_NOT_FOUND.toString());
                    return;
                }
                this.sendMessage(Language.FULL_CURE.toString().replace(
                        LanguageTag.PLAYER.toString(), player.getDisplayName()));

                SuperNPlayer snplayer = SuperNManager.get(player);
                SuperNManager.cure(snplayer);
            }
            return;
        }
        Player senderPlayer = (Player) sender;
        if (!SupernaturalsPlugin.hasPermissions(senderPlayer, permissions)) {
            this.sendMessage(Language.NO_PREMISSION.toString());
            return;
        }

        if (parameters.isEmpty()) {
            SuperNPlayer snplayer = SuperNManager.get(senderPlayer);
            SuperNManager.cure(snplayer);
        } else {
            String playername = parameters.get(0);
            Player player = SupernaturalsPlugin.instance.getServer().getPlayer(
                    playername);
            if (player == null) {
                this.sendMessage(Language.MISSING_PLAYER.toString());
                return;
            }
            this.sendMessage(Language.FULL_CURE.toString().replace(
                    LanguageTag.PLAYER.toString(), player.getDisplayName()));

            SuperNPlayer snplayer = SuperNManager.get(player);
            SuperNManager.cure(snplayer);
        }
    }
}
