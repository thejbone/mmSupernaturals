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

import org.bukkit.ChatColor;
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
            prefix = ChatColor.GOLD + "Priest";
        } else if (snplayer.isVampire()) {
            prefix = ChatColor.DARK_PURPLE + "Vampire";
        } else if (snplayer.isGhoul()) {
            prefix = ChatColor.DARK_GRAY + "Ghoul";
        } else if (snplayer.isWere()) {
            prefix = ChatColor.BLUE + "Werewolf";
        } else if (snplayer.isHunter()) {
            prefix = ChatColor.GREEN + "WitchHunter";
        } else if (snplayer.isDemon()) {
            prefix = ChatColor.RED + "Demon";
        } else if (snplayer.isAngel()) {
            prefix = ChatColor.AQUA + "Angel";
        } else {
            prefix = ChatColor.WHITE + "Human";
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
            SuperNManager.sendMessage(snplayer,
                    "你的職業已被重置, 因為你企圖閃避 mmSupernaturals 白名單.");
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
            if (SNConfigHandler.spanish) {
                if (snplayer.isHuman()) {
                    player.setDisplayName(player
                            .getDisplayName()
                            .trim()
                            .replace(player.getName(),
                                    ChatColor.WHITE + player.getName()));
                    plugin.getServer().broadcastMessage(
                            ChatColor.WHITE + "El Humano " + player.getName()
                                    + ChatColor.GOLD + " ha entrado al juego.");
                } else if (snplayer.isVampire()) {
                    player.setDisplayName(player
                            .getDisplayName()
                            .trim()
                            .replace(player.getName(),
                                    ChatColor.DARK_PURPLE + player.getName()));
                    plugin.getServer().broadcastMessage(
                            ChatColor.DARK_PURPLE + "El Vampiro "
                                    + player.getName() + ChatColor.GOLD
                                    + " ha entrado al juego.");
                } else if (snplayer.isWere()) {
                    player.setDisplayName(player
                            .getDisplayName()
                            .trim()
                            .replace(player.getName(),
                                    ChatColor.BLUE + player.getName()));
                    plugin.getServer().broadcastMessage(
                            ChatColor.BLUE + "El Hombre Lobo "
                                    + player.getName() + ChatColor.GOLD
                                    + " ha entrado al juego.");
                } else if (snplayer.isGhoul()) {
                    player.setDisplayName(player
                            .getDisplayName()
                            .trim()
                            .replace(player.getName(),
                                    ChatColor.DARK_GRAY + player.getName()));
                    plugin.getServer().broadcastMessage(
                            ChatColor.DARK_GRAY + "El Muerto Viviente "
                                    + player.getName() + ChatColor.GOLD
                                    + " ha entrado al juego.");
                } else if (snplayer.isPriest()) {
                    player.setDisplayName(player
                            .getDisplayName()
                            .trim()
                            .replace(player.getName(),
                                    ChatColor.GOLD + player.getName()));
                    plugin.getServer().broadcastMessage(
                            ChatColor.GOLD + "El Sacerdote " + player.getName()
                                    + ChatColor.GOLD + " ha entrado al juego.");
                } else if (snplayer.isHunter()) {
                    // player.setSneaking(true);
                    player.setDisplayName(player
                            .getDisplayName()
                            .trim()
                            .replace(player.getName(),
                                    ChatColor.GREEN + player.getName()));
                    plugin.getServer().broadcastMessage(
                            ChatColor.GREEN + "El Cazador de Brujas "
                                    + player.getName() + ChatColor.GOLD
                                    + " ha entrado al juego.");
                } else if (snplayer.isDemon()) {
                    player.setDisplayName(player
                            .getDisplayName()
                            .trim()
                            .replace(player.getName(),
                                    ChatColor.RED + player.getName()));
                    plugin.getServer().broadcastMessage(
                            ChatColor.RED + "El Demonio " + player.getName()
                                    + ChatColor.GOLD + " ha entrado al juego.");
                } else if (snplayer.isAngel()) {
                    player.setDisplayName(player
                            .getDisplayName()
                            .trim()
                            .replace(player.getName(),
                                    ChatColor.AQUA + player.getName()));
                    plugin.getServer().broadcastMessage(
                            ChatColor.AQUA + "El Angel " + player.getName()
                                    + ChatColor.GOLD + " ha entrado al juego.");
                }
                return;
            }
            if (snplayer.isHuman()) {
                player.setDisplayName(player
                        .getDisplayName()
                        .trim()
                        .replace(
                                player.getName(),
                                ChatColor.WHITE + player.getName()
                                        + ChatColor.WHITE));
                plugin.getServer().broadcastMessage(
                        ChatColor.WHITE + "人類(Human) " + player.getName()
                                + ChatColor.GOLD + " 加入了伺服器.");
            } else if (snplayer.isVampire()) {
                player.setDisplayName(player
                        .getDisplayName()
                        .trim()
                        .replace(
                                player.getName(),
                                ChatColor.DARK_PURPLE + player.getName()
                                        + ChatColor.WHITE));
                plugin.getServer().broadcastMessage(
                        ChatColor.DARK_PURPLE + "吸血鬼(Vampire) "
                                + player.getName() + ChatColor.GOLD
                                + " 加入了伺服器.");
            } else if (snplayer.isWere()) {
                player.setDisplayName(player
                        .getDisplayName()
                        .trim()
                        .replace(
                                player.getName(),
                                ChatColor.BLUE + player.getName()
                                        + ChatColor.WHITE));
                plugin.getServer().broadcastMessage(
                        ChatColor.BLUE + "狼人(Werewolf) " + player.getName()
                                + ChatColor.GOLD + " 加入了伺服器.");
            } else if (snplayer.isGhoul()) {
                player.setDisplayName(player
                        .getDisplayName()
                        .trim()
                        .replace(
                                player.getName(),
                                ChatColor.DARK_GRAY + player.getName()
                                        + ChatColor.WHITE));
                plugin.getServer().broadcastMessage(
                        ChatColor.DARK_GRAY + "食屍鬼(Ghoul) " + player.getName()
                                + ChatColor.GOLD + " 加入了伺服器.");
            } else if (snplayer.isPriest()) {
                player.setDisplayName(player
                        .getDisplayName()
                        .trim()
                        .replace(
                                player.getName(),
                                ChatColor.GOLD + player.getName()
                                        + ChatColor.WHITE));
                plugin.getServer().broadcastMessage(
                        ChatColor.GOLD + "牧師(Priest) " + player.getName()
                                + ChatColor.GOLD + " 加入了伺服器.");
            } else if (snplayer.isHunter()) {
                // player.setSneaking(true);
                player.setDisplayName(player
                        .getDisplayName()
                        .trim()
                        .replace(
                                player.getName(),
                                ChatColor.GREEN + player.getName()
                                        + ChatColor.WHITE));
                plugin.getServer().broadcastMessage(
                        ChatColor.GREEN + "女巫獵人(WitchHunter) "
                                + player.getName() + ChatColor.GOLD
                                + " 加入了伺服器.");
            } else if (snplayer.isDemon()) {
                player.setDisplayName(player
                        .getDisplayName()
                        .trim()
                        .replace(
                                player.getName(),
                                ChatColor.RED + player.getName()
                                        + ChatColor.WHITE));
                plugin.getServer().broadcastMessage(
                        ChatColor.RED + "惡魔(Demon) " + player.getName()
                                + ChatColor.GOLD + " 加入了伺服器.");
            } else if (snplayer.isAngel()) {
                player.setDisplayName(player
                        .getDisplayName()
                        .trim()
                        .replace(
                                player.getName(),
                                ChatColor.AQUA + player.getName()
                                        + ChatColor.WHITE));
                plugin.getServer().broadcastMessage(
                        ChatColor.AQUA + "天使(Angel) " + player.getName()
                                + ChatColor.GOLD + " 加入了伺服器.");
            }
        }
    }
}
