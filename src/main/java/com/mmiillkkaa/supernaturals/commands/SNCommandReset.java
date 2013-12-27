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
import com.mmiillkkaa.supernaturals.manager.SuperNManager;

public class SNCommandReset extends SNCommand {

    public SNCommandReset() {
        requiredParameters = new ArrayList<String>();
        optionalParameters = new ArrayList<String>();
        senderMustBePlayer = false;
        optionalParameters.add("playername");
        permissions = "supernatural.admin.command.reset";
        helpNameAndParams = "reset | reset [玩家名稱]";
        helpDescription = "重置玩家的能量為零";
    }

    @Override
    public void perform() {
        if (!(sender instanceof Player)) {
            if (parameters.isEmpty()) {
                this.sendMessage("缺乏玩家名稱!");
            } else {
                String playername = parameters.get(0);
                Player player = SupernaturalsPlugin.instance.getServer()
                        .getPlayer(playername);

                if (player == null) {
                    this.sendMessage("沒有這個玩家!");
                    return;
                }
                SuperNPlayer snplayer = SuperNManager.get(player);
                SuperNManager.alterPower(snplayer, -10000, "天神");
                this.sendMessage("已重置玩家能量: " + snplayer.getName());
            }
            return;
        }

        Player senderPlayer = (Player) sender;
        if (!SupernaturalsPlugin.hasPermissions(senderPlayer, permissions)) {
            this.sendMessage("你沒有權限使用這個指令.");
            return;
        }
        if (parameters.isEmpty()) {
            SuperNPlayer snplayer = SuperNManager.get(senderPlayer);
            SuperNManager.alterPower(snplayer, -10000, "天神");
        } else {
            String playername = parameters.get(0);
            Player player = SupernaturalsPlugin.instance.getServer().getPlayer(
                    playername);

            if (player == null) {
                this.sendMessage("沒有這個玩家!");
                return;
            }
            SuperNPlayer snplayer = SuperNManager.get(player);
            SuperNManager.alterPower(snplayer, -10000, "天神");
            this.sendMessage("已重置玩家能量: " + snplayer.getName());
        }
    }
}
