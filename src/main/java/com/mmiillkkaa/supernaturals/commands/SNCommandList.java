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

import com.mmiillkkaa.supernaturals.SuperNPlayer;
import com.mmiillkkaa.supernaturals.SupernaturalsPlugin;
import com.mmiillkkaa.supernaturals.manager.SuperNManager;
import com.mmiillkkaa.supernaturals.util.Language;
import com.mmiillkkaa.supernaturals.util.TextUtil;

public class SNCommandList extends SNCommand {

    public SNCommandList() {
        requiredParameters = new ArrayList<String>();
        optionalParameters = new ArrayList<String>();
        senderMustBePlayer = false;
        permissions = "supernatural.command.list";
        helpNameAndParams = "list";
        helpDescription = "List supernaturals on the server.";
    }

    @Override
    public void perform() {
        List<String> vampires = new ArrayList<String>();
        List<String> werewolves = new ArrayList<String>();
        List<String> ghouls = new ArrayList<String>();
        List<String> priests = new ArrayList<String>();
        List<String> hunters = new ArrayList<String>();
        List<String> demons = new ArrayList<String>();
        List<String> angels = new ArrayList<String>();

        for (SuperNPlayer snplayer : SuperNManager.findAllOnline()) {
            if (snplayer.isVampire()) {
                vampires.add(snplayer.getName());
            } else if (snplayer.isPriest()) {
                priests.add(snplayer.getName());
            } else if (snplayer.isWere()) {
                werewolves.add(snplayer.getName());
            } else if (snplayer.isGhoul()) {
                ghouls.add(snplayer.getName());
            } else if (snplayer.isHunter()) {
                hunters.add(snplayer.getName());
            } else if (snplayer.isDemon()) {
                demons.add(snplayer.getName());
            } else if (snplayer.isAngel()) {
                angels.add(snplayer.getName());
            }
        }

        // Create Messages
        List<String> messages = new ArrayList<String>();
        messages.add(String.format("*** %s ***", Language.ONLINE_PLAYERS));
        messages.add(String.format("%s: %s%s", Language.SN_VAMPIRE_NAME,
                ChatColor.WHITE, TextUtil.implode(vampires, ", ")));
        messages.add(String.format("%s: %s%s", Language.SN_WEREWOLF_NAME,
                ChatColor.WHITE, TextUtil.implode(werewolves, ", ")));
        messages.add(String.format("%s: %s%s", Language.SN_GHOUL_NAME,
                ChatColor.WHITE, TextUtil.implode(ghouls, ", ")));
        messages.add(String.format("%s: %s%s", Language.SN_PREIEST_NAME,
                ChatColor.WHITE, TextUtil.implode(priests, ", ")));
        messages.add(String.format("%s: %s%s", Language.SN_WITCHHUNTER_NAME,
                ChatColor.WHITE, TextUtil.implode(hunters, ", ")));
        messages.add(String.format("%s: %s%s", Language.SN_DEMON_NAME,
                ChatColor.WHITE, TextUtil.implode(demons, ", ")));
        messages.add(String.format("%s: %s%s", Language.SN_ANGEL_NAME,
                ChatColor.WHITE, TextUtil.implode(angels, ", ")));

        if (!(sender instanceof Player)) {
            // Send them
            this.sendMessage(messages);
            return;
        }

        Player senderPlayer = (Player) sender;
        if (!SupernaturalsPlugin.hasPermissions(senderPlayer, permissions)) {
            this.sendMessage(Language.NO_PREMISSION.toString());
            return;
        }

        // Send them
        this.sendMessage(messages);
    }
}
