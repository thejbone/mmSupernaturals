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
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.mmiillkkaa.supernaturals.SupernaturalsPlugin;
import com.mmiillkkaa.supernaturals.io.SNConfigHandler;
import com.mmiillkkaa.supernaturals.util.Language;

public class SNCommandHelp extends SNCommand {
    private static List<String> helpMessages = new ArrayList<String>();

    public SNCommandHelp() {
        super();
        requiredParameters = new ArrayList<String>();
        optionalParameters = new ArrayList<String>();
        senderMustBePlayer = false;
        senderMustBeSupernatural = false;
        permissions = "supernatural.command.help";
    }

    @Override
    public void perform() {
        helpMessages.add(String
                .format("*** %s ***", Language.SUPERNATURAL_HELP));
        helpMessages.add(String.format("/sn Power %s- %s", ChatColor.WHITE,
                Language.SN_CMD_POWER));
        helpMessages.add(String.format("/sn List %s- %s", ChatColor.WHITE,
                Language.SN_CMD_LIST));
        helpMessages.add(String.format("/sn Classes %s- %s", ChatColor.WHITE,
                Language.SN_CMD_CLASSES));
        helpMessages.add(String.format("/sn KillList %s- %s", ChatColor.WHITE,
                Language.SN_CMD_KILLIST));

        if (!(sender instanceof Player)) {
            helpMessages.add(String.format("/sn admin %s- %s", ChatColor.WHITE,
                    Language.SN_CMD_ADMIN));
            this.sendMessage(helpMessages);
            return;
        }
        String permissions2 = "supernatural.command.adminhelp";
        Player senderPlayer = (Player) sender;

        if (SupernaturalsPlugin.hasPermissions(senderPlayer, permissions2)) {
            helpMessages.add(String.format("/sn admin %s- %s", ChatColor.WHITE,
                    Language.SN_CMD_ADMIN));
        }

        if (!SupernaturalsPlugin.hasPermissions(senderPlayer, permissions)) {
            this.sendMessage(Language.NO_PREMISSION.toString());
            return;
        }
        this.sendMessage(helpMessages);
    }
}
