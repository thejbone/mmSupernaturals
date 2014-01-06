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

public class SNCommandAdmin extends SNCommand {
    private static List<String> adminHelpMessages = new ArrayList<String>();

    public SNCommandAdmin() {
        super();
        requiredParameters = new ArrayList<String>();
        optionalParameters = new ArrayList<String>();
        senderMustBePlayer = false;
        senderMustBeSupernatural = false;
        permissions = "supernatural.admin.command.adminhelp";
    }

    @Override
    public void perform() {

        adminHelpMessages.add(String.format("*** %s ***",
                Language.SN_ADMIN_HELP));
        adminHelpMessages.add(String.format("/sn cure <%s> %s- %s",
                Language.PLAYER_NAME, ChatColor.WHITE,
                Language.SN_ADMIN_CMD_CURE));
        adminHelpMessages.add(String.format("/sn convert <%s> [%s] %s- %s",
                Language.PLAYER_NAME, Language.SUPERNATURAL_TYPE,
                ChatColor.WHITE, Language.SN_ADMIN_CMD_CONVERT));
        adminHelpMessages.add(String.format("/sn reset <%s> %s- %s",
                Language.PLAYER_NAME, ChatColor.WHITE,
                Language.SN_ADMIN_CMD_RESET));
        adminHelpMessages.add(String.format("/sn power <%s> [%s] %s- %s",
                Language.PLAYER_NAME, Language.POWER, ChatColor.WHITE,
                Language.SN_ADMIN_CMD_POWER));
        adminHelpMessages.add(String.format("/sn rmtarget <%s> %s- %s ",
                Language.PLAYER_NAME, ChatColor.WHITE,
                Language.SN_ADMIN_CMD_RMTARGET));
        adminHelpMessages.add(String.format("/sn save %s- %s", ChatColor.WHITE,
                Language.SN_ADMIN_CMD_SAVE));
        adminHelpMessages.add(String.format("/sn reload %s- %s",
                ChatColor.WHITE, Language.SN_ADMIN_CMD_RELOAD));
        adminHelpMessages.add(String.format("/sn restartTask %s- %s",
                ChatColor.WHITE, Language.SN_ADMIN_CMD_RESTARTTASK));
        adminHelpMessages.add(String.format("/sn setchurch %s- %s",
                ChatColor.WHITE, Language.SN_ADMIN_CMD_SETCHRUSH));
        adminHelpMessages.add(String.format("/sn setbanish %s- %s",
                ChatColor.WHITE, Language.SN_ADMIN_CMD_SETBANISH));
        adminHelpMessages.add(String.format("/sn setup %s- %s",
                ChatColor.WHITE, Language.SN_ADMIN_SETUP));

        if (!(sender instanceof Player)) {
            this.sendMessage(adminHelpMessages);
            return;
        }
        Player player = (Player) sender;
        if (SupernaturalsPlugin.hasPermissions(player, permissions)) {
            this.sendMessage(adminHelpMessages);
        } else {
            this.sendMessage(Language.NO_PREMISSION.toString());
        }
    }
}
