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

package com.mmiillkkaa.supernaturals.manager;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.Door;

import com.mmiillkkaa.supernaturals.SuperNPlayer;
import com.mmiillkkaa.supernaturals.SupernaturalsPlugin;
import com.mmiillkkaa.supernaturals.io.SNConfigHandler;
import com.mmiillkkaa.supernaturals.util.GeometryUtil;

public class VampireManager extends ClassManager {

	public VampireManager() {
		super();
	}

	private String permissions = "supernatural.player.preventsundamage";
	public SupernaturalsPlugin plugin = SupernaturalsPlugin.instance;
	private ArrayList<Location> hallDoors = new ArrayList<Location>();

	// -------------------------------------------- //
	// Damage Events //
	// -------------------------------------------- //

	@Override
	public double victimEvent(EntityDamageEvent event, double damage) {
		Player victim = (Player) event.getEntity();
		SuperNPlayer snVictim = SuperNManager.get(victim);
		if (event.getCause().equals(DamageCause.DROWNING)) {
			if (snVictim.getPower() > SNConfigHandler.vampireDrowningCost) {
				SuperNManager.alterPower(snVictim,
						-SNConfigHandler.vampireDrowningCost, "Water!");
				event.setCancelled(true);
				return 0;
			} else {
				SuperNManager.sendMessage(snVictim,
						"Not enough power to prevent water damage!");
				return damage;
			}
		} else if (event.getCause().equals(DamageCause.FALL)) {
			event.setCancelled(true);
			return 0;
		} else if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent edbeEvent = (EntityDamageByEntityEvent) event;
			Entity damager = edbeEvent.getDamager();
			if (damager instanceof Projectile) {
				return damage;
			} else if (damager instanceof Player) {
				Player pDamager = (Player) damager;
				ItemStack item = pDamager.getInventory().getItemInMainHand();

				if (item != null) {
					if (SNConfigHandler.woodMaterials.contains(item.getType())) {
						damage += damage * SNConfigHandler.woodFactor;
						SuperNManager.sendMessage(snVictim,
								"Vampires have a weakness to wood!");
					} else {
						damage -= damage
								* snVictim
										.scale(1 - SNConfigHandler.vampireDamageReceivedFactor);
					}
				}
			}
		}
		return damage;
	}

	@Override
	public void deathEvent(Player player) {
		SuperNPlayer snplayer = SuperNManager.get(player);

		SuperNManager.alterPower(snplayer,
				-SNConfigHandler.vampireDeathPowerPenalty, "You died!");
	}

	@Override
	public void killEvent(Player pDamager, SuperNPlayer damager,
			SuperNPlayer victim) {
		if (victim == null) {
			pDamager.setFoodLevel(pDamager.getFoodLevel()
					+ SNConfigHandler.vampireHungerRegainMob);
			SuperNManager.alterPower(damager,
					SNConfigHandler.vampireKillPowerCreatureGain,
					"Creature death!");
		} else {
			if (!victim.isSuper()) {
				pDamager.setFoodLevel(pDamager.getFoodLevel()
						+ SNConfigHandler.vampireHungerRegainPlayer);
			}
			double random = Math.random();
			if (victim.getPower() > SNConfigHandler.vampireKillPowerPlayerGain) {
				SuperNManager.alterPower(damager,
						SNConfigHandler.vampireKillPowerPlayerGain,
						"Player killed!");
			} else {
				SuperNManager
						.sendMessage(damager,
								"You cannot gain power from a player with no power themselves.");
			}
			if (SNConfigHandler.vampireKillSpreadCurse && !victim.isSuper()) {
				if (random < SNConfigHandler.spreadChance) {
					SuperNManager
							.sendMessage(victim,
									"You feel your heart stop! You have contracted vampirism.");
					SuperNManager.convert(victim, "vampire");
				}
			}
		}
	}

	@Override
	public double damagerEvent(EntityDamageByEntityEvent event, double damage) {
		Entity damager = event.getDamager();
		Player pDamager = (Player) damager;
		SuperNPlayer snDamager = SuperNManager.get(pDamager);

		ItemStack item = pDamager.getInventory().getItemInMainHand();

		if (item != null) {
			if (SNConfigHandler.vampireWeapons.contains(item.getType())) {
				SuperNManager.sendMessage(snDamager,
						"Vampires cannot use this weapon!");
				return 0;
			}
		}

		damage += damage * snDamager.scale(SNConfigHandler.vampireDamageFactor);
		return damage;
	}

	// -------------------------------------------- //
	// Interact //
	// -------------------------------------------- //

	@Override
	public boolean playerInteract(PlayerInteractEvent event) {

		Action action = event.getAction();
		Player player = event.getPlayer();
		SuperNPlayer snplayer = SuperNManager.get(player);
		Material itemMaterial = event.getMaterial();

		if (action.equals(Action.LEFT_CLICK_AIR)
				|| action.equals(Action.LEFT_CLICK_BLOCK)) {
			if (player.getInventory().getItemInMainHand() == null) {
				return false;
			}

			if (itemMaterial.toString().equalsIgnoreCase(
					SNConfigHandler.vampireJumpMaterial)) {
				SuperNManager
						.jump(player, SNConfigHandler.jumpDeltaSpeed, true);
				event.setCancelled(true);
				return true;
			} else if (itemMaterial.toString().equalsIgnoreCase(
					SNConfigHandler.vampireMaterial)) {
				teleport(player);
				event.setCancelled(true);
				return true;
			}
		}

		if (!(action.equals(Action.RIGHT_CLICK_AIR) || action
				.equals(Action.RIGHT_CLICK_BLOCK))) {
			if (itemMaterial.equals(Material.CAKE)) {
				event.setCancelled(true);
				SuperNManager
						.sendMessage(snplayer,
								"Vampires can't eat food. You must drink blood instead.");
				return true;
			}
			return false;
		}

		if (itemMaterial != null) {
			if (SNConfigHandler.foodMaterials.contains(itemMaterial)) {
				SuperNManager
						.sendMessage(snplayer,
								"Vampires can't eat food. You must drink blood instead.");
				event.setCancelled(true);
				return true;
			} else if (itemMaterial.toString().equalsIgnoreCase(
					SNConfigHandler.vampireTeleportMaterial)) {
				setTeleport(player);
				return true;
			}
		}
		return false;
	}

	// -------------------------------------------- //
	// Armor //
	// -------------------------------------------- //

	@Override
	public void armorCheck(Player player) {
		if (!player.hasPermission("supernatural.player.ignorearmor")) {
			PlayerInventory inv = player.getInventory();
			ItemStack helmet = inv.getHelmet();
			ItemStack chest = inv.getChestplate();
			ItemStack leggings = inv.getLeggings();
			ItemStack boots = inv.getBoots();

			if (helmet != null) {
				if (!SNConfigHandler.vampireArmor.contains(helmet.getType())
						&& !helmet.getType().toString().endsWith("WOOL")) {
					inv.setHelmet(null);
					dropItem(player, helmet);
				}
			}
			if (chest != null) {
				if (!SNConfigHandler.vampireArmor.contains(chest.getType())) {
					inv.setChestplate(null);
					dropItem(player, chest);
				}
			}
			if (leggings != null) {
				if (!SNConfigHandler.vampireArmor.contains(leggings.getType())) {
					inv.setLeggings(null);
					dropItem(player, leggings);
				}
			}
			if (boots != null) {
				if (!SNConfigHandler.vampireArmor.contains(boots.getType())) {
					inv.setBoots(null);
					dropItem(player, boots);
				}
			}
		}
	}

	// -------------------------------------------- //
	// Power Altering //
	// -------------------------------------------- //

	public void gainPowerAdvanceTime(SuperNPlayer snplayer, int milliseconds) {
		double deltaSeconds = milliseconds / 1000D;
		double deltaPower = deltaSeconds
				* SNConfigHandler.vampireTimePowerGained;
		SuperNManager.alterPower(snplayer, deltaPower);
	}

	// -------------------------------------------- //
	// Teleport //
	// -------------------------------------------- //

	public void setTeleport(Player player) {
		SuperNPlayer snplayer = SuperNManager.get(player);

		SupernaturalsPlugin.instance.getDataHandler().addTeleport(snplayer);
		SuperNManager.sendMessage(snplayer, "Teleport Location Saved!");
	}

	public boolean teleport(Player player) {
		SuperNPlayer snplayer = SuperNManager.get(player);
		ItemStack item = player.getInventory().getItemInMainHand();
		if (SupernaturalsPlugin.instance.getDataHandler().checkPlayer(snplayer)) {
			if (snplayer.getPower() > SNConfigHandler.vampireTeleportCost) {
				SuperNManager.alterPower(snplayer,
						-SNConfigHandler.vampireTeleportCost, "Teleport!");
				player.teleport(SupernaturalsPlugin.instance.getDataHandler()
						.getTeleport(snplayer));
				if (item.getAmount() == 1) {
					player.setItemInHand(null);
				} else {
					item.setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
				}
				return true;
			} else {
				SuperNManager.sendMessage(snplayer,
						"Not enough power to teleport.");
				return false;
			}
		} else {
			SuperNManager.sendMessage(snplayer,
					"You have not set a teleport location yet!");
			return false;
		}
	}

	// -------------------------------------------- //
	// Altar Usage //
	// -------------------------------------------- //

	public void useAltarInfect(Player player, Block centerBlock) {

		SuperNPlayer snplayer = SuperNManager.get(player);

		// The altar must be big enough
		int count = GeometryUtil
				.countNearby(
						centerBlock,
						Material.getMaterial(SNConfigHandler.vampireAltarInfectMaterialSurround),
						SNConfigHandler.vampireAltarInfectMaterialRadius);
		if (count == 0) {
			return;
		}

		if (count < SNConfigHandler.vampireAltarInfectMaterialSurroundCount) {
			SuperNManager.sendMessage(snplayer, "Something happens... The "
					+ SNConfigHandler.vampireAltarInfectMaterial.toLowerCase()
							.replace('_', ' ')
					+ " draws energy from the "
					+ SNConfigHandler.vampireAltarInfectMaterialSurround
							.toLowerCase().replace('_', ' ')
					+ "... But there doesn't seem to be enough "
					+ SNConfigHandler.vampireAltarInfectMaterialSurround
							.toLowerCase().replace('_', ' ') + " nearby.");
			return;
		}

		// Always examine first
		SuperNManager.sendMessage(snplayer, "This altar looks really evil.");

		if (!SupernaturalsPlugin.hasPermissions(player,
				"supernatural.player.shrineuse.vampire")) {
			SuperNManager.sendMessage(snplayer,
					"You cannot use vampire altars.");
			return;
		}

		// Is Vampire
		if (snplayer.isVampire()) {
			SuperNManager.sendMessage(snplayer,
					"This is of no use to you as you are already a vampire.");
			return;
		} else if (snplayer.isSuper()) {
			SuperNManager
					.sendMessage(snplayer,
							"This is of no use to you as you are already supernatural.");
			return;
		}

		// Is healthy and thus can be infected...
		if (SNConfigHandler.vampireAltarInfectRecipe.playerHasEnough(player)) {
			SuperNManager.sendMessage(snplayer,
					"You use these items on the altar:");
			SuperNManager.sendMessage(snplayer,
					SNConfigHandler.vampireAltarInfectRecipe.getRecipeLine());
			SuperNManager
					.sendMessage(
							snplayer,
							"The "
									+ SNConfigHandler.vampireAltarInfectMaterial
											.toLowerCase().replace('_', ' ')
									+ " draws energy from the "
									+ SNConfigHandler.vampireAltarInfectMaterialSurround
											.toLowerCase().replace('_', ' ')
									+ "... The energy rushes through you and you feel a bitter cold...");
			SNConfigHandler.vampireAltarInfectRecipe.removeFromPlayer(player);
			SuperNManager.convert(snplayer, "vampire",
					SNConfigHandler.vampirePowerStart);
		} else {
			SuperNManager.sendMessage(snplayer,
					"To use it you need to collect these ingredients:");
			SuperNManager.sendMessage(snplayer,
					SNConfigHandler.vampireAltarInfectRecipe.getRecipeLine());
		}
	}

	public void useAltarCure(Player player, Block centerBlock) {
		SuperNPlayer snplayer = SuperNManager.get(player);

		// Altar must be big enough
		int count = GeometryUtil.countNearby(centerBlock, Material
				.getMaterial(SNConfigHandler.vampireAltarCureMaterialSurround),
				SNConfigHandler.vampireAltarCureMaterialRadius);
		if (count == 0) {
			return;
		}

		if (count < SNConfigHandler.vampireAltarCureMaterialSurroundCount) {
			SuperNManager.sendMessage(snplayer, "Something happens... The "
					+ SNConfigHandler.vampireAltarCureMaterial.toLowerCase()
							.replace('_', ' ').replace('_', ' ')
					+ " draws energy from the "
					+ SNConfigHandler.vampireAltarCureMaterialSurround
							.toLowerCase().replace('_', ' ')
					+ "... But there doesn't seem to be enough "
					+ SNConfigHandler.vampireAltarCureMaterialSurround
							.toLowerCase().replace('_', ' ') + " nearby.");
			return;
		}

		// Always examine first
		SuperNManager.sendMessage(snplayer, "This altar looks pure and clean.");

		if (!SupernaturalsPlugin.hasPermissions(player,
				"supernatural.player.shrineuse.vampire")) {
			SuperNManager.sendMessage(snplayer,
					"You cannot use vampire altars.");
			return;
		}

		// If healthy
		if (!snplayer.isVampire()) {
			SuperNManager.sendMessage(snplayer,
					"It can probably cure curses, but you feel fine.");
		}

		// Is vampire and thus can be cured...
		else if (SNConfigHandler.vampireAltarCureRecipe.playerHasEnough(player)) {
			SuperNManager.sendMessage(snplayer,
					"You use these items on the altar:");
			SuperNManager.sendMessage(snplayer,
					SNConfigHandler.vampireAltarCureRecipe.getRecipeLine());
			SuperNManager
					.sendMessage(
							snplayer,
							"The "
									+ SNConfigHandler.vampireAltarCureMaterial
											.toLowerCase().replace('_', ' ')
									+ " draws energy from the "
									+ SNConfigHandler.vampireAltarCureMaterialSurround
											.toLowerCase().replace('_', ' ')
									+ "... Then the energy rushes through you and you feel pure and clean.");
			SNConfigHandler.vampireAltarCureRecipe.removeFromPlayer(player);
			SuperNManager.cure(snplayer);
		} else {
			SuperNManager.sendMessage(snplayer,
					"To use it you need to collect these ingredients:");
			SuperNManager.sendMessage(snplayer,
					SNConfigHandler.vampireAltarCureRecipe.getRecipeLine());
		}
	}

	// -------------------------------------------- //
	// Combustion //
	// -------------------------------------------- //

	public boolean combustAdvanceTime(Player player, long milliseconds) {
		SuperNPlayer snplayer = SuperNManager.get(player);
		if (!standsInSunlight(player)) {
			return false;
		}

		if (!SNConfigHandler.vampireBurnInSunlight) {
			return false;
		}

		// We assume the next tick will be in milliseconds.
		int ticksTillNext = (int) (milliseconds / 1000D * 20D); // 20 minecraft
																// ticks is a
																// second.
		ticksTillNext += 5; // just to be on the safe side.

		if (player.getFireTicks() <= 0
				&& SNConfigHandler.vampireBurnMessageEnabled) {
			SuperNManager.sendMessage(snplayer,
					"Vampires burn in sunlight! Take cover!");
		}

		player.setFireTicks(ticksTillNext
				+ SNConfigHandler.vampireCombustFireTicks);
		return true;
	}

	public boolean standsInSunlight(Player player) {
		// No need to set on fire if the water will put the fire out at once.
		Material material = player.getLocation().getBlock().getType();
		World playerWorld = player.getWorld();

		if (SupernaturalsPlugin.hasPermissions(player, permissions)) {
			return false;
		}

        return !(player.getWorld().getEnvironment().equals(Environment.NETHER)
                || SuperNManager.worldTimeIsNight(player)
                || isUnderRoof(player)
                || material.equals(Material.WATER) || playerWorld.hasStorm()
                || hasHelmet(player));
    }

	public boolean hasHelmet(Player player) {
		if (player.getInventory().getHelmet() != null) {
			if (player.getInventory().getHelmet().getType().toString()
					.equalsIgnoreCase(SNConfigHandler.vampireHelmet)) {
				return true;
			}
		}
		return false;
	}

	public boolean isUnderRoof(Player player) {
		/*
		 * We start checking opacity 2 blocks up. As Max Y is 255 there CAN be a
		 * roof over the player if he is standing in block 253: 255 Solid Block
		 * 254 253 Player However if he is standing in 254 there is no chance.
		 */
		boolean retVal = false;
		Block blockCurrent = player.getLocation().getBlock();

		if (player.getLocation().getY() >= 254) {
			retVal = false;
		} else {
			// blockCurrent = blockCurrent.getFace(BlockFace.UP, 1); //What was
			// the point?
			while (blockCurrent.getY() + 1 <= 255) {
				blockCurrent = blockCurrent.getRelative(BlockFace.UP);

				if (blockCurrent.getType().isSolid()) {
					retVal = true; // Blocks not in that map have opacity 1;
					break;
				}
			}
		}
		return retVal;
	}

	private void addDoorLocation(Location location) {
		if (!hallDoors.contains(location)) {
			hallDoors.add(location);
		}
	}

	private void removeDoorLocation(Location location) {
		hallDoors.remove(location);
	}

	public boolean doorIsOpening(Location location) {
        return hallDoors.contains(location);
    }

	public boolean doorEvent(Player player, Block block, Door door) {
		if (door.isOpen()) {
			return true;
		}

		SuperNPlayer snplayer = SuperNManager.get(player);

		final Location loc = block.getLocation();
		Location newLoc;
		Block newBlock;

		if (snplayer.isVampire()) {
			if (door.isTopHalf()) {
				newLoc = new Location(loc.getWorld(), loc.getBlockX(),
						loc.getBlockY() - 1, loc.getBlockZ());
				newBlock = newLoc.getBlock();
				block.setType(Material.IRON_DOOR, false);
				newBlock.setType(Material.IRON_DOOR,false);
			} else {
				newLoc = new Location(loc.getWorld(), loc.getBlockX(),
						loc.getBlockY() + 1, loc.getBlockZ());
				newBlock = newLoc.getBlock();
				block.setType(Material.IRON_DOOR, false);
				newBlock.setType(Material.IRON_DOOR,false);
			}

			addDoorLocation(loc);
			addDoorLocation(newLoc);

			SupernaturalsPlugin.instance
					.getServer()
					.getScheduler()
					.scheduleSyncDelayedTask(SupernaturalsPlugin.instance,
							new Runnable() {
								@Override
								public void run() {
									closeDoor(loc);
								}
							}, 20);
			return true;
		}
		SuperNManager.sendMessage(snplayer, "Vampires Only!");
		return true;
	}

	private void closeDoor(Location loc) {
		Block block = loc.getBlock();
		Door door = (Door) block.getState().getData();
		if (!door.isOpen()) {
			return;
		}

		Location newLoc;
		Block newBlock;

		if (door.isTopHalf()) {
			newLoc = new Location(loc.getWorld(), loc.getBlockX(),
					loc.getBlockY() - 1, loc.getBlockZ());
			newBlock = newLoc.getBlock();
			block.setType(Material.IRON_DOOR, false);
			newBlock.setType(Material.IRON_DOOR,false);
		} else {
			newLoc = new Location(loc.getWorld(), loc.getBlockX(),
					loc.getBlockY() + 1, loc.getBlockZ());
			newBlock = newLoc.getBlock();
			block.setType(Material.IRON_DOOR, false);
			newBlock.setType(Material.IRON_DOOR,false);
		}

		removeDoorLocation(loc);
		removeDoorLocation(newLoc);
	}
}
