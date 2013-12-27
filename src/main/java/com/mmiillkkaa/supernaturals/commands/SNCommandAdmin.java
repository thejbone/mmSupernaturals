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

	static {
		adminHelpMessages.add("*** " + ChatColor.WHITE
				+ "Supernatural 管理者指令" + ChatColor.RED + " ***");
		adminHelpMessages.add("/sn cure <玩家名稱> " + ChatColor.WHITE
				+ "- 治療自己或玩家.");
		adminHelpMessages.add("/sn convert <玩家名稱> [超自然生物] "
				+ ChatColor.WHITE
				+ "- 將自己或玩家轉換為任意的超自然生物.");
		adminHelpMessages.add("/sn reset <玩家名稱>" + ChatColor.WHITE
				+ "- 重置自己或玩家的能量.");
		adminHelpMessages.add("/sn power <玩家名稱> [Power] "
				+ ChatColor.WHITE + "- 給自己或玩家能量.");
		adminHelpMessages.add("/sn rmtarget <玩家名稱> " + ChatColor.WHITE
				+ "- 將玩家從魔女獵人的獵殺名單移除.");
		adminHelpMessages.add("/sn save " + ChatColor.WHITE
				+ "- 儲存資料到硬碟.");
		adminHelpMessages.add("/sn reload " + ChatColor.WHITE
				+ "- 從硬碟讀取資料.");
		adminHelpMessages.add("/sn restartTask " + ChatColor.WHITE
				+ "- 重置技能計時器..");
		adminHelpMessages.add("/sn setchurch " + ChatColor.WHITE
				+ "- 將你目前的位置設定為牧師的教堂.");
		adminHelpMessages.add("/sn setbanish " + ChatColor.WHITE
				+ "- 將你目前的位置設定為牧師的放逐點.");
	}

	@Override
	public void perform() {
		if (!(sender instanceof Player)) {
			this.sendMessage(adminHelpMessages);
			return;
		}
		Player player = (Player) sender;
		if (SupernaturalsPlugin.hasPermissions(player, permissions)) {
			this.sendMessage(adminHelpMessages);
		} else {
			if (!SNConfigHandler.spanish) {
				this.sendMessage("你沒有權限使用這個指令.");
			} else {
				this.sendMessage("No tienes permiso para usar este comando.");
			}
		}
	}
}
