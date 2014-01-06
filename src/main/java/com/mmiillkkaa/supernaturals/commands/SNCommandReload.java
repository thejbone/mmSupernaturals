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

import com.mmiillkkaa.supernaturals.SupernaturalsPlugin;
import com.mmiillkkaa.supernaturals.util.Language;

public class SNCommandReload extends SNCommand {

    public SNCommandReload() {
        requiredParameters = new ArrayList<String>();
        optionalParameters = new ArrayList<String>();
        optionalParameters.add("type");
        senderMustBePlayer = false;
        permissions = "supernatural.admin.command.reload";
        helpNameAndParams = "";
        helpDescription = "Reload Config or Data files";
    }

    @Override
    public void perform() {
        if (!(sender instanceof Player)) {
            if (parameters.isEmpty()) {
                SupernaturalsPlugin.reConfig();
                this.sendMessage(Language.CONFIG_RELOAD.toString());
            } else {
                if (parameters.get(0).equalsIgnoreCase("config")) {
                    SupernaturalsPlugin.reConfig();
                    this.sendMessage(Language.CONFIG_RELOAD.toString());
                } else if (parameters.get(0).equalsIgnoreCase("data")) {
                    SupernaturalsPlugin.reloadData();
                    this.sendMessage(Language.DATA_RELOAD.toString());
                } else {
                    this.sendMessage(Language.INVALID_OPTION.toString());
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
            SupernaturalsPlugin.reConfig();
            this.sendMessage(Language.CONFIG_RELOAD.toString());
        } else {
            if (parameters.get(0).equalsIgnoreCase("config")) {
                SupernaturalsPlugin.reConfig();
                this.sendMessage(Language.CONFIG_RELOAD.toString());
            } else if (parameters.get(0).equalsIgnoreCase("data")) {
                SupernaturalsPlugin.reloadData();
                this.sendMessage(Language.DATA_RELOAD.toString());
            } else {
                this.sendMessage(Language.INVALID_OPTION.toString());
            }
        }
    }
}
