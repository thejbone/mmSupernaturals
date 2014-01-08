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
import com.mmiillkkaa.supernaturals.io.SNConfigHandler;
import com.mmiillkkaa.supernaturals.manager.SuperNManager;
import com.mmiillkkaa.supernaturals.util.Language;
import com.mmiillkkaa.supernaturals.util.LanguageTag;

public class SNCommandConvert extends SNCommand {

    public SNCommandConvert() {
        requiredParameters = new ArrayList<String>();
        optionalParameters = new ArrayList<String>();
        senderMustBePlayer = false;
        optionalParameters.add("playername");
        requiredParameters.add("supernaturalType");
        permissions = "supernatural.admin.command.curse";
        helpNameAndParams = "convert [playername] [supernaturalType]";
        helpDescription = "Instantly turn a player into a supernatural.";
    }

    public static String permission2 = "supernatural.admin.partial.curse";

    @Override
    public void perform() {

        if (!(sender instanceof Player)) {
            if (parameters.size() == 1) {
                this.sendMessage(Language.MISSING_PLAYER.toString());
            } else {
                String playername = parameters.get(0);
                String superType = parameters.get(1).toLowerCase();
                Player player = SupernaturalsPlugin.instance.getServer()
                        .getPlayer(playername);

                if (player == null) {
                    this.sendMessage(Language.PLAYER_NOT_FOUND.toString());
                    return;
                }

                if (!SNConfigHandler.supernaturalTypes.contains(superType)) {
                    this.sendMessage(Language.INVALID_SUPERNATURAL_TYPE
                            .toString());
                    return;
                }

                SuperNPlayer snplayer = SuperNManager.get(player);
                if (snplayer.getType().equalsIgnoreCase(superType)) {
                    this.sendMessage(Language.TYPE_ALREADY
                            .toString()
                            .replace(LanguageTag.PLAYER.toString(),
                                    player.getName())
                            .replace(LanguageTag.TYPE.toString(), superType));
                } else if (snplayer.getOldType().equalsIgnoreCase(superType)) {
                    this.sendMessage(Language.TYPE_TURN_BACK
                            .toString()
                            .replace(LanguageTag.PLAYER.toString(),
                                    player.getName())
                            .replace(LanguageTag.TYPE.toString(), superType));
                    SuperNManager.revert(snplayer);
                } else {
                    this.sendMessage(Language.TYPE_TURN
                            .toString()
                            .replace(LanguageTag.PLAYER.toString(),
                                    player.getName())
                            .replace(LanguageTag.TYPE.toString(), superType));
                    SuperNManager.convert(snplayer, superType);
                }
            }
            return;
        }

        Player senderPlayer = (Player) sender;
        if (parameters.size() == 1) {
            if (!SupernaturalsPlugin.hasPermissions(senderPlayer, permission2)) {
                this.sendMessage(Language.NO_PREMISSION.toString());
                return;
            }
            String superType = parameters.get(0).toLowerCase();

            if (!SNConfigHandler.supernaturalTypes.contains(superType)) {
                this.sendMessage(Language.INVALID_SUPERNATURAL_TYPE.toString());
                return;
            }

            SuperNPlayer snplayer = SuperNManager.get(senderPlayer);
            if (snplayer.getType().equalsIgnoreCase(superType)) {
                this.sendMessage(Language.TYPE_ALREADY
                        .toString()
                        .replace(LanguageTag.PLAYER.toString(),
                                senderPlayer.getName())
                        .replace(LanguageTag.TYPE.toString(), superType));
            } else if (snplayer.getOldType().equalsIgnoreCase(superType)) {
                this.sendMessage(Language.TYPE_TURN_BACK
                        .toString()
                        .replace(LanguageTag.PLAYER.toString(),
                                senderPlayer.getName())
                        .replace(LanguageTag.TYPE.toString(), superType));
                SuperNManager.revert(snplayer);
            } else {
                this.sendMessage(Language.TYPE_TURN
                        .toString()
                        .replace(LanguageTag.PLAYER.toString(),
                                senderPlayer.getName())
                        .replace(LanguageTag.TYPE.toString(), superType));
                SuperNManager.convert(snplayer, superType);
            }

        } else {
            if (!SupernaturalsPlugin.hasPermissions(senderPlayer, permissions)) {
                this.sendMessage(Language.NO_PREMISSION.toString());
                return;
            }
            String playername = parameters.get(0);
            String superType = parameters.get(1).toLowerCase();
            Player player = SupernaturalsPlugin.instance.getServer().getPlayer(
                    playername);

            if (player == null) {
                this.sendMessage(Language.PLAYER_NOT_FOUND.toString());
                return;
            }

            if (!SNConfigHandler.supernaturalTypes.contains(superType)) {
                this.sendMessage(Language.INVALID_SUPERNATURAL_TYPE.toString());
                return;
            }

            SuperNPlayer snplayer = SuperNManager.get(player);
            if (snplayer.getType().equalsIgnoreCase(superType)) {
                this.sendMessage(Language.TYPE_ALREADY
                        .toString()
                        .replace(LanguageTag.PLAYER.toString(),
                                player.getName())
                        .replace(LanguageTag.TYPE.toString(), superType));
            } else if (snplayer.getOldType().equalsIgnoreCase(superType)) {
                this.sendMessage(Language.TYPE_TURN_BACK
                        .toString()
                        .replace(LanguageTag.PLAYER.toString(),
                                player.getName())
                        .replace(LanguageTag.TYPE.toString(), superType));
                SuperNManager.revert(snplayer);
            } else {
                this.sendMessage(Language.TYPE_TURN
                        .toString()
                        .replace(LanguageTag.PLAYER.toString(),
                                player.getName())
                        .replace(LanguageTag.TYPE.toString(), superType));
                SuperNManager.convert(snplayer, superType);
            }
        }
    }
}
