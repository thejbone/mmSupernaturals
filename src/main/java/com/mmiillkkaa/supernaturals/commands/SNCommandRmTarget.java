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
import com.mmiillkkaa.supernaturals.manager.HunterManager;
import com.mmiillkkaa.supernaturals.manager.SuperNManager;

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
                this.sendMessage("缺乏玩家名稱!");
            } else {
                String playername = parameters.get(0);
                SuperNPlayer snplayer = SuperNManager.get(playername);

                if (snplayer == null) {
                    this.sendMessage("沒有這個玩家.");
                    return;
                }

                if (HunterManager.removeBounty(snplayer)) {
                    this.sendMessage(ChatColor.WHITE + snplayer.getName()
                            + ChatColor.RED + " 已從獵殺清單移除!");
                    HunterManager.addBounty();
                    return;
                } else {
                    this.sendMessage(ChatColor.WHITE + snplayer.getName()
                            + ChatColor.RED + " 並不在目標中.");
                    return;
                }
            }
            return;
        }
        Player senderPlayer = (Player) sender;
        if (SNConfigHandler.spanish) {
            if (!SupernaturalsPlugin.hasPermissions(senderPlayer, permissions)) {
                this.sendMessage("No tienes permiso para este comando.");
                return;
            }

            if (parameters.isEmpty()) {
                SuperNPlayer snplayer = SuperNManager.get(senderPlayer);
                if (HunterManager.removeBounty(snplayer)) {
                    this.sendMessage("Fuiste eliminado de la lista de objetivos!");
                } else {
                    this.sendMessage("No eres un objetivo activo.");
                }
            } else {
                String playername = parameters.get(0);
                SuperNPlayer snplayer = SuperNManager.get(playername);

                if (snplayer == null) {
                    this.sendMessage("Jugador no encontrado.");
                    return;
                }

                if (HunterManager.removeBounty(snplayer)) {
                    this.sendMessage(ChatColor.WHITE + snplayer.getName()
                            + ChatColor.RED
                            + " ha sido removido de la lista de objetivos!");
                    HunterManager.addBounty();
                } else {
                    this.sendMessage(ChatColor.WHITE + snplayer.getName()
                            + ChatColor.RED + " no es un objetivo activo.");
                }
            }
        } else {
            if (!SupernaturalsPlugin.hasPermissions(senderPlayer, permissions)) {
                this.sendMessage("你沒有權限使用這個指令.");
                return;
            }

            if (parameters.isEmpty()) {
                SuperNPlayer snplayer = SuperNManager.get(senderPlayer);
                if (HunterManager.removeBounty(snplayer)) {
                    this.sendMessage("你已從獵殺清單中移除!");
                } else {
                    this.sendMessage("你並不在目標中.");
                }
            } else {
                String playername = parameters.get(0);
                SuperNPlayer snplayer = SuperNManager.get(playername);

                if (snplayer == null) {
                    this.sendMessage("沒有這個玩家.");
                    return;
                }

                if (HunterManager.removeBounty(snplayer)) {
                    this.sendMessage(ChatColor.WHITE + snplayer.getName()
                            + ChatColor.RED + " 已從獵殺清單移除!");
                    HunterManager.addBounty();
                } else {
                    this.sendMessage(ChatColor.WHITE + snplayer.getName()
                            + ChatColor.RED + " 並不在目標中.");
                }
            }
        }
    }
}
