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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mmiillkkaa.supernaturals.io.SNConfigHandler;

public class SNCommand {
	public List<String> requiredParameters;
	public List<String> optionalParameters;
	public String permissions;
	public String helpNameAndParams;
	public String helpDescription;
	public boolean senderMustBePlayer;
	public boolean senderMustBeSupernatural;
	public CommandSender sender;
	public List<String> parameters;

	public SNCommand() {
		requiredParameters = new ArrayList<String>();
		optionalParameters = new ArrayList<String>();
		permissions = "";
		senderMustBePlayer = false;
		senderMustBeSupernatural = false;
		helpNameAndParams = "失敗!";
		helpDescription = "沒有簡介";
	}

	public String getName() {
		String name = this.getClass().getName().toLowerCase();
		if (name.lastIndexOf('.') > 0) {
			name = name.substring(name.lastIndexOf('.') + 1);
		}
		return name.substring(9);
	}

	public String getBaseName() {
		return "sn";
	}

	public void execute(CommandSender sender, List<String> parameters) {
		this.sender = sender;
		this.parameters = parameters;

		if (!validateCall()) {
			if (!SNConfigHandler.spanish) {
				sendMessage("請輸入 /sn help");
			} else {
				sendMessage("Escribe /sn help");
			}
			return;
		}

		perform();
	}

	public void perform() {

	}

	public void sendMessage(String message) {
		sender.sendMessage(ChatColor.RED + message);
	}

	public void sendMessage(List<String> messages) {
		for (String message : messages) {
			this.sendMessage(message);
		}
	}

	// Test if the number of params is correct.
	public boolean validateCall() {

		if (senderMustBePlayer && !(sender instanceof Player)) {
			if (!SNConfigHandler.spanish) {
				sendMessage("這個指令只能被遊戲內的玩家使用.");
			} else {
				sendMessage("Solo puedes usar este comando si estas dentro del juego.");
			}
			return false;
		}

		if (parameters.size() < requiredParameters.size()) {
			int missing = requiredParameters.size() - parameters.size();
			if (SNConfigHandler.spanish) {
				sendMessage("Par�metros incorrectos. Debes ingresar "
						+ missing + " more.");
			} else {
				sendMessage("參數不足. 你必須輸入 " + missing
						+ " 個以上.");
			}
			return false;
		}

		if (parameters.size() > requiredParameters.size()
				+ optionalParameters.size()) {
			sendMessage("太多參數.");
			return false;
		}

		return true;
	}
}
