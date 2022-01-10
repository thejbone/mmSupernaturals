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


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.material.Door;

import com.mmiillkkaa.supernaturals.SuperNPlayer;
import com.mmiillkkaa.supernaturals.SupernaturalsPlugin;
import com.mmiillkkaa.supernaturals.io.SNConfigHandler;
import com.mmiillkkaa.supernaturals.manager.SuperNManager;

public class SNPlayerListener implements Listener {

	public SupernaturalsPlugin plugin;
	private String worldPermission = "supernatural.world.enabled";

	public SNPlayerListener(SupernaturalsPlugin instance) {
		plugin = instance;
	}

	// @Override
	// public void onPlayerToggleSneak(PlayerToggleSneakEvent event){
	// Player player = event.getPlayer();
	// SuperNPlayer snplayer = SupernaturalManager.get(player);
	// if(snplayer.isHunter()){
	// player.setSneaking(true);
	// event.setCancelled(true);
	// }
	// }

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		Player player = event.getPlayer();
		SuperNPlayer snplayer = SuperNManager.get(player);

		if (!(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_AIR))
				&& event.isCancelled()) {
			return;
		}

		if (!SupernaturalsPlugin.hasPermissions(player, worldPermission)
				&& SNConfigHandler.multiworld) {
			return;
		}

		Location blockLoc;
		Block block = event.getClickedBlock();
		if (action.equals(Action.RIGHT_CLICK_BLOCK)
				|| action.equals(Action.LEFT_CLICK_BLOCK)) {
			try {
				blockLoc = block.getLocation();
			} catch (NullPointerException e) {
				SupernaturalsPlugin.log("Door trying to close.");
				event.setCancelled(true);
				return;
			}

			if (block.getType().equals(Material.IRON_DOOR_BLOCK)) {
				for (int x = blockLoc.getBlockX() - 2; x < blockLoc.getBlockX() + 3; x++) {
					for (int y = blockLoc.getBlockY() - 2; y < blockLoc.getBlockY() + 3; y++) {
						for (int z = blockLoc.getBlockZ() - 2; z < blockLoc.getBlockZ() + 3; z++) {
							Location newLoc = new Location(block.getWorld(), x, y, z);
							Block newBlock = newLoc.getBlock();
							if (newBlock.getType().equals(Material.SIGN)
									|| newBlock.getType().equals(Material.WALL_SIGN)) {
								Sign sign = (Sign) newBlock.getState();
								String[] text = sign.getLines();
								for (int i = 0; i < text.length; i++) {
									if (text[i].contains(SNConfigHandler.hunterHallMessage)) {
										if (plugin.getHunterManager().doorIsOpening(blockLoc)) {
											event.setCancelled(true);
											return;
										}
										Door door = (Door) block.getState().getData();
										boolean open = plugin.getHunterManager().doorEvent(player, block, door);
										event.setCancelled(open);
										return;
									}
									if (text[i].contains(SNConfigHandler.demonHallMessage)) {
										if (plugin.getHunterManager().doorIsOpening(blockLoc)) {
											event.setCancelled(true);
											return;
										}
										Door door = (Door) block.getState().getData();
										boolean open = plugin.getDemonManager().doorEvent(player, block, door);
										event.setCancelled(open);
										return;
									}
									if (text[i].contains(SNConfigHandler.vampireHallMessage)) {
										if (plugin.getHunterManager().doorIsOpening(blockLoc)) {
											event.setCancelled(true);
											return;
										}
										Door door = (Door) block.getState().getData();
										boolean open = plugin.getVampireManager().doorEvent(player, block, door);
										event.setCancelled(open);
										return;
									}
								}
							}
						}
					}
				}
			}
		}

		boolean cancelled = false;

		cancelled = plugin.getClassManager(player).playerInteract(event);

		if (cancelled) {
			return;
		}

		if (!action.equals(Action.RIGHT_CLICK_BLOCK)) {
			return;
		}

		Material blockMaterial = event.getClickedBlock().getType();

		if (blockMaterial == Material.getMaterial(SNConfigHandler.vampireAltarInfectMaterial)) {
			plugin.getVampireManager().useAltarInfect(player, event.getClickedBlock());
		} else if (blockMaterial == Material.getMaterial(SNConfigHandler.vampireAltarCureMaterial)) {
			plugin.getVampireManager().useAltarCure(player, event.getClickedBlock());
		} else if (blockMaterial == Material.getMaterial(SNConfigHandler.priestAltarMaterial)) {
			plugin.getPriestManager().useAltar(player);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerKick(PlayerKickEvent event) {
		if (event.isCancelled()) {
			return;
		}

		if (!SupernaturalsPlugin.hasPermissions(event.getPlayer(), worldPermission)
				&& SNConfigHandler.multiworld) {
			return;
		}

		if (event.getLeaveMessage().contains("Flying")
				|| event.getReason().contains("Flying")) {
			SuperNPlayer snplayer = SuperNManager.get(event.getPlayer());
			if (snplayer.isVampire()
					&& event.getPlayer().getInventory().getItemInMainHand().getType().toString().equalsIgnoreCase(SNConfigHandler.vampireJumpMaterial)) {
				event.setCancelled(true);
			}
		}
	}
}
