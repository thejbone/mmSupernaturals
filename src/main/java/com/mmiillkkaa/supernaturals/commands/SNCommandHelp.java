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

public class SNCommandHelp extends SNCommand {
	private static List<String> helpMessages = new ArrayList<String>();
	private static List<String> helpMessagesSpanish = new ArrayList<String>();

	public SNCommandHelp() {
		super();
		requiredParameters = new ArrayList<String>();
		optionalParameters = new ArrayList<String>();
		senderMustBePlayer = false;
		senderMustBeSupernatural = false;
		permissions = "supernatural.command.help";
	}

	static {
		helpMessagesSpanish.add("*** " + ChatColor.WHITE + "Supernatural Help "
				+ ChatColor.RED + "***");
		helpMessagesSpanish.add("/sn Power " + ChatColor.WHITE
				+ "- Indica tus Poderes actuales.");
		helpMessagesSpanish.add("/sn List " + ChatColor.WHITE
				+ "- Lista de Seres Místicos. conectados.");
		helpMessagesSpanish.add("/sn Classes " + ChatColor.WHITE
				+ "- Lista de Seres Místicos. disponibles.");
		helpMessagesSpanish.add("/sn KillList " + ChatColor.WHITE
				+ "- Lista de objetivos para Cazadores de Brujas.");
		helpMessages.add("*** " + ChatColor.WHITE + "Supernatural 說明文件 "
				+ ChatColor.RED + "***");
		helpMessages.add("/sn Power " + ChatColor.WHITE
				+ "- 顯示目前的能量等級.");
		helpMessages.add("/sn List " + ChatColor.WHITE
				+ "- 列出伺服器上的超自然生物.");
		helpMessages.add("/sn Classes " + ChatColor.WHITE
				+ "- 顯示可用的超自然生物類型/職業.");
		helpMessages.add("/sn KillList " + ChatColor.WHITE
				+ "- 顯示目前女巫獵人獵殺清單.");
	}

	@Override
	public void perform() {
		if (!(sender instanceof Player)) {
			if (helpMessages.size() == 5) {
				helpMessages.add("/sn admin " + ChatColor.WHITE
						+ "- 顯示管理員專用指令");
			}
			this.sendMessage(helpMessages);
			return;
		}
		String permissions2 = "supernatural.command.adminhelp";
		Player senderPlayer = (Player) sender;

		if (SupernaturalsPlugin.hasPermissions(senderPlayer, permissions2)) {
			if (helpMessages.size() == 5) {
				helpMessages.add("/sn admin " + ChatColor.WHITE
						+ "- 顯示管理員專用指令");
			}
		}

		if (!SupernaturalsPlugin.hasPermissions(senderPlayer, permissions)) {
			if (!SNConfigHandler.spanish) {
				this.sendMessage("你沒有權限使用這個指令.");
			} else {
				this.sendMessage("No tienes permiso para este comando.");
			}
			return;
		}
		if (!SNConfigHandler.spanish) {
			this.sendMessage(helpMessages);
		} else {
			this.sendMessage(helpMessagesSpanish);
		}
	}
}
