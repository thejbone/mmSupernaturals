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

public class SNCommandClasses extends SNCommand {
	private static List<String> classMessages = new ArrayList<String>();
	private static List<String> spanishClassMessages = new ArrayList<String>();

	public SNCommandClasses() {
		super();
		requiredParameters = new ArrayList<String>();
		optionalParameters = new ArrayList<String>();
		senderMustBePlayer = false;
		senderMustBeSupernatural = false;
		permissions = "supernatural.command.classes";
	}

	static {
		classMessages.add("*** " + ChatColor.WHITE + "Supernatural 種族/職業"
				+ ChatColor.RED + "***");
		classMessages.add("人類(Human): " + ChatColor.WHITE
				+ "- 不起眼的平凡人類.");
		classMessages.add("牧師(Priest): " + ChatColor.WHITE
				+ "- 一個擁有不凡力量來對抗邪惡生物的人類.");
		classMessages.add("吸血鬼(Vampire): " + ChatColor.WHITE
				+ "- 它們並不會冒出火花!");
		classMessages.add("食屍鬼(Ghoul): " + ChatColor.WHITE
				+ "- 行動遲緩並且非常不容易死亡.");
		classMessages.add("狼人(Werewolf): " + ChatColor.WHITE
				+ "- 在夜晚獲得強大的力量.");
		classMessages.add("女巫獵人(WitchHunter): " + ChatColor.WHITE
				+ "- 弓箭和潛行的專家.");
		classMessages.add("惡魔(Demon): " + ChatColor.WHITE
				+ "- 支配邪惡的生物與火焰.");
		classMessages.add("天使(Angel): " + ChatColor.WHITE
				+ "一個靈魂無法被束縛的人類.");
		spanishClassMessages.add("*** " + ChatColor.WHITE
				+ "Clases de Seres Místicos." + ChatColor.RED + "***");
		spanishClassMessages.add("Humano: " + ChatColor.WHITE
				+ "- De carne y hueso, solo sirven para destruir el mundo.");
		spanishClassMessages.add("Sacerdote: " + ChatColor.WHITE
				+ "- Humano bendecido por el mismisimo Dios.");
		spanishClassMessages.add("Vampiro: " + ChatColor.WHITE
				+ "- Criatura sin alma, dan miedo!");
		spanishClassMessages.add("Muerto Viviente: " + ChatColor.WHITE
				+ "- Feo, aterrador y sin cerebro.");
		spanishClassMessages.add("Hombre Lobo: " + ChatColor.WHITE
				+ "- Peludo y muy funcional durante la noche.");
		spanishClassMessages.add("Cazador de Brujas: " + ChatColor.WHITE
				+ "- Experto con arcos y sigiloso.");
		spanishClassMessages.add("Demonio: " + ChatColor.WHITE
				+ "- Tiene una extra�a union con el infierno.");
	}

	@Override
	public void perform() {
		if (!(sender instanceof Player)) {
			this.sendMessage(classMessages);
			return;
		}
		Player senderPlayer = (Player) sender;

		if (!SupernaturalsPlugin.hasPermissions(senderPlayer, permissions)) {
			if (!SNConfigHandler.spanish) {
				this.sendMessage("你沒有權限使用這個指令.");
			} else {
				this.sendMessage("No tienes permiso para este comando.");
			}
			return;
		}

		if (!SNConfigHandler.spanish) {
			this.sendMessage(classMessages);
		} else {
			this.sendMessage(spanishClassMessages);
		}
	}

}
