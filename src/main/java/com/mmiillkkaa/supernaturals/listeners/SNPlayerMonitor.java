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

package com.mmiillkkaa.supernaturals.listeners;

import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;

import com.mmiillkkaa.supernaturals.SuperNPlayer;
import com.mmiillkkaa.supernaturals.SupernaturalsPlugin;
import com.mmiillkkaa.supernaturals.io.SNConfigHandler;
import com.mmiillkkaa.supernaturals.io.SNWhitelistHandler;
import com.mmiillkkaa.supernaturals.manager.SuperNManager;
import com.mmiillkkaa.supernaturals.util.Color;
import com.mmiillkkaa.supernaturals.util.Language;
import com.mmiillkkaa.supernaturals.util.LanguageTag;

public class SNPlayerMonitor implements Listener {

    private SupernaturalsPlugin plugin;
    private String worldPermission = "supernatural.world.enabled";

    public SNPlayerMonitor(SupernaturalsPlugin instance) {
        plugin = instance;
    }

    // @Override
    // public void onPlayerRespawn(PlayerRespawnEvent event){
    // if(SupernaturalManager.get(event.getPlayer()).isHunter()){
    // event.getPlayer().setSneaking(true);
    // }
    // }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
        SuperNPlayer snplayer = SuperNManager.get(player);
        String prefix;
        if (snplayer.isPriest()) {
            prefix = Color.PRIEST.prefix(Language.SN_PREIEST_NAME);
        } else if (snplayer.isVampire()) {
            prefix = Color.VAMPIRE.prefix(Language.SN_VAMPIRE_NAME);
        } else if (snplayer.isGhoul()) {
            prefix = Color.GHOUL.prefix(Language.SN_GHOUL_NAME);
        } else if (snplayer.isWere()) {
            prefix = Color.WEREWOLF.prefix(Language.SN_WEREWOLF_NAME);
        } else if (snplayer.isHunter()) {
            prefix = Color.WITCHHUNTER.prefix(Language.SN_WITCHHUNTER_NAME);
        } else if (snplayer.isDemon()) {
            prefix = Color.DEMON.prefix(Language.SN_DEMON_NAME);
        } else if (snplayer.isAngel()) {
            prefix = Color.ANGEL.prefix(Language.SN_ANGEL_NAME);
        } else {
            prefix = Color.HUMAN.prefix(Language.SN_HUMAN_NAME);
        }

        event.setFormat(event.getFormat().replace("[SN]", prefix));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getTo() == null) {
            return;
        }
        if (event.getTo().getWorld() == null) {
            return;
        }
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        if (!SupernaturalsPlugin.hasPermissions(player, worldPermission)
                && SNConfigHandler.multiworld) {
            return;
        }
        if (event.getTo().getWorld().getEnvironment()
                .equals(Environment.NETHER)) {
            plugin.getDemonManager().checkInventory(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!SupernaturalsPlugin.hasPermissions(event.getPlayer(),
                worldPermission) && SNConfigHandler.multiworld) {
            return;
        }
        SuperNPlayer snplayer = SuperNManager.get(player);
        if (!SNWhitelistHandler.isWhitelisted(SuperNManager.get(player))) {
            SuperNManager
                    .sendMessage(snplayer, Language.CLASS_RESET.toString());
        }

        if (SupernaturalsPlugin.hasPermissions(player,
                "supernatural.admin.infinitepower")) {
            snplayer.setPower(10000); // Making power really infinite.
        }

        if (!SNConfigHandler.enableLoginMessage) {
            return;
        }
        boolean vanished = false;
        for (Player onePlayer : plugin.getServer().getOnlinePlayers()) {
            if (onePlayer != player) {
                if (!onePlayer.canSee(player)) {
                    vanished = true;
                }
            }
        }
        SuperNManager.updateName(snplayer);
        if (vanished) {
            return;
        }

        if (SNConfigHandler.enableColors) {
            if (snplayer.isHuman()) {
                player.setDisplayName(player
                        .getDisplayName()
                        .trim()
                        .replace(player.getName(),
                                Color.HUMAN.name(player.getName())));
                plugin.getServer().broadcastMessage(
                        Color.HUMAN.name(Language.SN_HUMAN_NAME)
                                + Language.CLASS_JOIN_SERVER.toString()
                                        .replace(LanguageTag.PLAYER.toString(),
                                                player.getName()));
            } else if (snplayer.isVampire()) {
                player.setDisplayName(player
                        .getDisplayName()
                        .trim()
                        .replace(player.getName(),
                                Color.VAMPIRE.name(player.getName())));
                plugin.getServer().broadcastMessage(
                        Color.VAMPIRE.name(Language.SN_VAMPIRE_NAME)
                                + Language.CLASS_JOIN_SERVER.toString()
                                        .replace(LanguageTag.PLAYER.toString(),
                                                player.getName()));
            } else if (snplayer.isWere()) {
                player.setDisplayName(player
                        .getDisplayName()
                        .trim()
                        .replace(player.getName(),
                                Color.WEREWOLF.name(player.getName())));
                plugin.getServer().broadcastMessage(
                        Color.WEREWOLF.name(Language.SN_WEREWOLF_NAME)
                                + Language.CLASS_JOIN_SERVER.toString()
                                        .replace(LanguageTag.PLAYER.toString(),
                                                player.getName()));
            } else if (snplayer.isGhoul()) {
                player.setDisplayName(player
                        .getDisplayName()
                        .trim()
                        .replace(player.getName(),
                                Color.GHOUL.name(player.getName())));
                plugin.getServer().broadcastMessage(
                        Color.GHOUL.name(Language.SN_GHOUL_NAME)
                                + Language.CLASS_JOIN_SERVER.toString()
                                        .replace(LanguageTag.PLAYER.toString(),
                                                player.getName()));
            } else if (snplayer.isPriest()) {
                player.setDisplayName(player
                        .getDisplayName()
                        .trim()
                        .replace(player.getName(),
                                Color.PRIEST.name(player.getName())));
                plugin.getServer().broadcastMessage(
                        Color.PRIEST.name(Language.SN_PREIEST_NAME)
                                + Language.CLASS_JOIN_SERVER.toString()
                                        .replace(LanguageTag.PLAYER.toString(),
                                                player.getName()));
            } else if (snplayer.isHunter()) {
                // player.setSneaking(true);
                player.setDisplayName(player
                        .getDisplayName()
                        .trim()
                        .replace(player.getName(),
                                Color.WITCHHUNTER.name(player.getName())));
                plugin.getServer().broadcastMessage(
                        Color.WITCHHUNTER.name(Language.SN_WITCHHUNTER_NAME)
                                + Language.CLASS_JOIN_SERVER.toString()
                                        .replace(LanguageTag.PLAYER.toString(),
                                                player.getName()));
            } else if (snplayer.isDemon()) {
                player.setDisplayName(player
                        .getDisplayName()
                        .trim()
                        .replace(player.getName(),
                                Color.DEMON.name(player.getName())));
                plugin.getServer().broadcastMessage(
                        Color.DEMON.name(Language.SN_DEMON_NAME)
                                + Language.CLASS_JOIN_SERVER.toString()
                                        .replace(LanguageTag.PLAYER.toString(),
                                                player.getName()));
            } else if (snplayer.isAngel()) {
                player.setDisplayName(player
                        .getDisplayName()
                        .trim()
                        .replace(player.getName(),
                                Color.ANGEL.name(player.getName())));
                plugin.getServer().broadcastMessage(
                        Color.ANGEL.name(Language.SN_ANGEL_NAME)
                                + Language.CLASS_JOIN_SERVER.toString()
                                        .replace(LanguageTag.PLAYER.toString(),
                                                player.getName()));
            }
        }
    }
}
