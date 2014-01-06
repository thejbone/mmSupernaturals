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

import org.bukkit.entity.Player;

import com.mmiillkkaa.supernaturals.SuperNPlayer;
import com.mmiillkkaa.supernaturals.SupernaturalsPlugin;
import com.mmiillkkaa.supernaturals.manager.HunterManager;
import com.mmiillkkaa.supernaturals.manager.SuperNManager;
import com.mmiillkkaa.supernaturals.util.Language;
import com.mmiillkkaa.supernaturals.util.LanguageTag;

public class SNCommandRmTarget extends SNCommand {
    public SNCommandRmTarget() {
        super();
        requiredParameters = new ArrayList<String>();
        optionalParameters = new ArrayList<String>();
        senderMustBePlayer = false;
        optionalParameters.add("playername");
        permissions = "supernatural.admin.command.rmtarget";
    }

    @Override
    public void perform() {
        if (!(sender instanceof Player)) {
            if (parameters.isEmpty()) {
                this.sendMessage(Language.MISSING_PLAYER.toString());
            } else {
                String playername = parameters.get(0);
                SuperNPlayer snplayer = SuperNManager.get(playername);

                if (snplayer == null) {
                    this.sendMessage(Language.PLAYER_NOT_FOUND.toString());
                    return;
                }

                if (HunterManager.removeBounty(snplayer)) {
                    this.sendMessage(Language.PLAYER_REMOVE_FROM_TARGET_LIST
                            .toString().replace(LanguageTag.PLAYER.toString(),
                                    snplayer.getName()));
                    HunterManager.addBounty();
                    return;
                } else {
                    this.sendMessage(Language.PLAYER_NOT_A_ACTIVE_TARGET
                            .toString().replace(LanguageTag.PLAYER.toString(),
                                    snplayer.getName()));
                    return;
                }
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
            if (HunterManager.removeBounty(snplayer)) {
                this.sendMessage(Language.NOTICE_REMOVE_FROM_TARGET_LIST
                        .toString());
            } else {
                this.sendMessage(Language.NOTICE_NOT_A_ACTIVE_TARGET.toString());
            }
        } else {
            String playername = parameters.get(0);
            SuperNPlayer snplayer = SuperNManager.get(playername);

            if (snplayer == null) {
                this.sendMessage(Language.PLAYER_NOT_FOUND.toString());
                return;
            }

            if (HunterManager.removeBounty(snplayer)) {
                this.sendMessage(Language.PLAYER_REMOVE_FROM_TARGET_LIST
                        .toString().replace(LanguageTag.PLAYER.toString(),
                                snplayer.getName()));
                HunterManager.addBounty();
            } else {
                this.sendMessage(Language.PLAYER_NOT_A_ACTIVE_TARGET.toString()
                        .replace(LanguageTag.PLAYER.toString(),
                                snplayer.getName()));
            }
        }
    }
}
