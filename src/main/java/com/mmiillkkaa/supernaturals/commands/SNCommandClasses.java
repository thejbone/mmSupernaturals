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
import com.mmiillkkaa.supernaturals.util.Language;

public class SNCommandClasses extends SNCommand {
    private static List<String> classMessages = new ArrayList<String>();

    public SNCommandClasses() {
        super();
        requiredParameters = new ArrayList<String>();
        optionalParameters = new ArrayList<String>();
        senderMustBePlayer = false;
        senderMustBeSupernatural = false;
        permissions = "supernatural.command.classes";
    }

    @Override
    public void perform() {

        classMessages.add(String.format("*** %s ***",
                Language.SUPERNATURAL_CLASS));
        classMessages.add(String.format("%s: %s- %s", Language.SN_HUMAN_NAME,
                ChatColor.WHITE, Language.SN_HUMAN_DESC));
        classMessages.add(String.format("%s: %s- %s", Language.SN_PREIEST_NAME,
                ChatColor.WHITE, Language.SN_PRIEST_DESC));
        classMessages.add(String.format("%s: %s- %s", Language.SN_VAMPIRE_NAME,
                ChatColor.WHITE, Language.SN_VAMPIRE_DESC));
        classMessages.add(String.format("%s: %s- %s", Language.SN_GHOUL_NAME,
                ChatColor.WHITE, Language.SN_GHOUL_DESC));
        classMessages.add(String.format("%s: %s- %s",
                Language.SN_WEREWOLF_NAME, ChatColor.WHITE,
                Language.SN_WEREWOLF_DESC));
        classMessages.add(String.format("%s: %s- %s",
                Language.SN_WITCHHUNTER_NAME, ChatColor.WHITE,
                Language.SN_WITCHHUNTER_DESC));
        classMessages.add(String.format("%s: %s- %s", Language.SN_DEMON_NAME,
                ChatColor.WHITE, Language.SN_DEMON_DESC));
        classMessages.add(String.format("%s: %s- %s", Language.SN_ANGEL_NAME,
                ChatColor.WHITE, Language.SN_ANGEL_DESC));

        if (!(sender instanceof Player)) {
            this.sendMessage(classMessages);
            return;
        }
        Player senderPlayer = (Player) sender;

        if (!SupernaturalsPlugin.hasPermissions(senderPlayer, permissions)) {
            this.sendMessage(Language.NO_PREMISSION.toString());
            return;
        }

        this.sendMessage(classMessages);
    }

}
