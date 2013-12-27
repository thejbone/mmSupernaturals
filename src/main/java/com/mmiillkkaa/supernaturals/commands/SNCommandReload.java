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

public class SNCommandReload extends SNCommand {
    public String msgLoadSuccess = "載入 %s.";
    public String msgLoadFail = "%s 載入失敗.";
    public String msgSaveSuccess = "儲存 %s.";
    public String msgSaveFail = "%s 儲存失敗.";

    public SNCommandReload() {
        requiredParameters = new ArrayList<String>();
        optionalParameters = new ArrayList<String>();
        optionalParameters.add("type");
        senderMustBePlayer = false;
        permissions = "supernatural.admin.command.reload";
        helpNameAndParams = "";
        helpDescription = "重新載入設定檔或資料檔";
    }

    @Override
    public void perform() {
        if (!(sender instanceof Player)) {
            if (parameters.isEmpty()) {
                SupernaturalsPlugin.reConfig();
                this.sendMessage("設定檔已重新讀取");
            } else {
                if (parameters.get(0).equalsIgnoreCase("config")) {
                    SupernaturalsPlugin.reConfig();
                    this.sendMessage("設定檔已重新讀取");
                } else if (parameters.get(0).equalsIgnoreCase("data")) {
                    SupernaturalsPlugin.reloadData();
                    this.sendMessage("資料檔已重新讀取");
                } else {
                    this.sendMessage("不正確的選項.");
                }
            }
            return;
        }
        Player senderPlayer = (Player) sender;
        if (!SupernaturalsPlugin.hasPermissions(senderPlayer, permissions)) {
            this.sendMessage("你沒有權限使用這個指令.");
            return;
        }
        if (parameters.isEmpty()) {
            SupernaturalsPlugin.reConfig();
            this.sendMessage("設定檔已重新讀取");
        } else {
            if (parameters.get(0).equalsIgnoreCase("config")) {
                SupernaturalsPlugin.reConfig();
                this.sendMessage("設定檔已重新讀取");
            } else if (parameters.get(0).equalsIgnoreCase("data")) {
                SupernaturalsPlugin.reloadData();
                this.sendMessage("資料檔已重新讀取");
            } else {
                this.sendMessage("不正確的選項.");
            }
        }
    }
}
