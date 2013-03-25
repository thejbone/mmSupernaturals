package com.mmiillkkaa.supernaturals.manager;


import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import com.mmiillkkaa.supernaturals.SuperNPlayer;
import com.mmiillkkaa.supernaturals.SupernaturalsPlugin;
import com.mmiillkkaa.supernaturals.io.SNConfigHandler;

public class AngelManager extends ClassManager {

	public AngelManager() {
		super();
	}

	public HashMap<Wolf, SuperNPlayer> angelWolfMap = new HashMap<Wolf, SuperNPlayer>();

	@Override
	public void deathEvent(Player player) {
		SuperNPlayer snplayer = SuperNManager.get(player);
		if(player.getLastDamageCause().getCause().equals(DamageCause.LAVA) || player.getLastDamageCause().getCause().equals(DamageCause.FIRE) || player.getLastDamageCause().getCause().equals(DamageCause.FIRE_TICK)) {
			SuperNManager.sendMessage(snplayer, "Flames have removed your holy powers!");
			SuperNManager.cure(snplayer);
		}
	}

	@Override
	public double damagerEvent(EntityDamageByEntityEvent event, double damage) {
		Player player = (Player) event.getDamager();
		SuperNPlayer snplayer = SuperNManager.get(player);
		if (event.getEntity() instanceof Animals) {
			if (player.getItemInHand().getType().equals(Material.DIAMOND_SWORD)) {
				SuperNManager.sendMessage(snplayer, "Angels cannot use diamond swords on animals!");
				event.setCancelled(true);
				return 0;
			}
		}
		if(SNConfigHandler.angelWeapons.contains(player.getItemInHand())) {
			SuperNManager.sendMessage(snplayer, "Angels cannot use this weapon!");
			event.setCancelled(true);
			return 0;
		}
		return damage;
	}

	@Override
	public void spellEvent(EntityDamageByEntityEvent event, Player target) {
		Player player = (Player) event.getDamager();
		SuperNPlayer snplayer = SuperNManager.get(player);
		SuperNPlayer sntarget = SuperNManager.get(target);
		if (player.getItemInHand().getType().toString().equals(SNConfigHandler.angelHealMaterial)) {
			if (snplayer.getPower() > SNConfigHandler.angelHealPowerCost) {
				if(!(target.getHealth() + SNConfigHandler.angelHealHealthGain > 20)) {
					target.setHealth(target.getHealth()
							+ SNConfigHandler.angelHealHealthGain);
				} else {
					target.setHealth(20);
				}
				SuperNManager.alterPower(snplayer, -SNConfigHandler.angelHealPowerCost, "Healing "
						+ target.getName());
				target.sendMessage(ChatColor.RED + "Healed by "
						+ player.getName());
			} else {
				SuperNManager.sendMessage(snplayer, "Not enough power to heal!");
			}
		}
		if (player.getItemInHand().getType().toString().equals(SNConfigHandler.angelCureMaterial)) {
			if (snplayer.getPower() > SNConfigHandler.angelCurePowerCost) {
				if (sntarget.isSuper()) {
					SuperNManager.cure(sntarget);
					target.sendMessage(ChatColor.RED + "Cured by "
							+ player.getName());
					SuperNManager.alterPower(snplayer, -SNConfigHandler.angelCurePowerCost, "Cured "
							+ target.getName());
				} else {
					SuperNManager.sendMessage(snplayer, "Player is not a supernatural!");
				}
			} else {
				SuperNManager.sendMessage(snplayer, "Not enough power!");
			}
		}
	}

	@Override
	public boolean playerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Action action = event.getAction();
		Material itemInHandMaterial = player.getItemInHand().getType();
		SuperNPlayer snplayer = SuperNManager.get(player);
		if (action.equals(Action.LEFT_CLICK_AIR)
				|| action.equals(Action.LEFT_CLICK_BLOCK)) {
			if (itemInHandMaterial.toString().equals(SNConfigHandler.angelJumpMaterial)) {
				if (snplayer.getPower() > SNConfigHandler.angelJumpPowerCost) {
					jump(player, SNConfigHandler.angelJumpDeltaSpeed);
				} else {
					SuperNManager.sendMessage(snplayer, "Not enough power to jump!");
				}
			}
			Block targetBlock = player.getTargetBlock(null, 20);
			Location targetBlockLocation = targetBlock.getLocation();
			Location plusOne = new Location(targetBlockLocation.getWorld(), targetBlockLocation.getBlockX(), targetBlockLocation.getBlockY() +1, targetBlockLocation.getBlockZ());
			if (itemInHandMaterial.equals(Material.RAW_BEEF)
					|| itemInHandMaterial.equals(Material.BONE)
					|| itemInHandMaterial.equals(Material.PORK)) {
				if (snplayer.getPower() > SNConfigHandler.angelSummonPowerCost) {
					if (itemInHandMaterial.toString().equals(SNConfigHandler.angelSummonCowMaterial)) {
						player.getWorld().spawnEntity(plusOne, EntityType.COW);
						event.setCancelled(true);
						SuperNManager.alterPower(snplayer, -SNConfigHandler.angelSummonPowerCost, "Summoned cow.");
						return true;
					}
					if (itemInHandMaterial.toString().equals(SNConfigHandler.angelSummonWolfMaterial)) {
						int wolves = 0;
						for(Wolf wolf : angelWolfMap.keySet()) {
							if(angelWolfMap.get(wolf).equals(snplayer)) {
								wolves++;
							}
						}
						if(wolves > 4) {
							player.sendMessage(ChatColor.RED + "Too much wolves, not enough space! (Stop spawning wolves!)");
							return true;
						}
						Wolf spawnedWolf = (Wolf) player.getWorld().spawnEntity(plusOne, EntityType.WOLF);
						spawnedWolf.setTamed(true);
						spawnedWolf.setOwner(player);
						spawnedWolf.setHealth(8);
						event.setCancelled(true);
						SuperNManager.alterPower(snplayer, -SNConfigHandler.angelSummonPowerCost, "Summoned wolf.");
						return true;
					}
					if (itemInHandMaterial.toString().equals(SNConfigHandler.angelSummonPigMaterial)) {
						player.getWorld().spawnEntity(plusOne, EntityType.PIG);
						event.setCancelled(true);
						SuperNManager.alterPower(snplayer, -SNConfigHandler.angelSummonPowerCost, "Summoned pig.");
						return true;
					}
				}
			}
			return false;
		}
		return false;
	}

	@Override
	public double victimEvent(EntityDamageEvent event, double damage) {
		if (event.getCause().equals(DamageCause.FALL)) {
			event.setCancelled(true);
			return 0;
		}
		return damage;
	}

	public static boolean jump(Player player, double deltaSpeed) {
		SuperNPlayer snplayer = SuperNManager.get(player);

		if (snplayer.getPower() < SNConfigHandler.angelJumpPowerCost) {
			SuperNManager.sendMessage(snplayer, "Not enough Power to jump.");
			return false;
		} else {
			SuperNManager.alterPower(snplayer, -SNConfigHandler.angelJumpPowerCost, "SuperJump!");
			if (SNConfigHandler.debugMode) {
				SupernaturalsPlugin.log(snplayer.getName() + " used jump!");
			}
		}

		Vector vjadd;
		vjadd = new Vector(0, 1, 0);
		vjadd.multiply(deltaSpeed);

		player.setVelocity(player.getVelocity().add(vjadd));
		return true;
	}

	@Override
	public void armorCheck(Player player) {
		PlayerInventory inv = player.getInventory();
		ItemStack helmet = inv.getHelmet();
		ItemStack chest = inv.getChestplate();
		ItemStack leggings = inv.getLeggings();
		ItemStack boots = inv.getBoots();

		if (helmet != null) {
			if (!SNConfigHandler.angelArmor.contains(helmet.getType())) {
				inv.setHelmet(null);
				dropItem(player, helmet);
			}
		}
		if (chest != null) {
			if (!SNConfigHandler.angelArmor.contains(chest.getType())) {
				inv.setChestplate(null);
				dropItem(player, chest);
			}
		}
		if (leggings != null) {
			if (!SNConfigHandler.angelArmor.contains(leggings.getType())) {
				inv.setLeggings(null);
				dropItem(player, leggings);
			}
		}
		if (boots != null) {
			if (!SNConfigHandler.angelArmor.contains(boots.getType())) {
				inv.setBoots(null);
				dropItem(player, boots);
			}
		}
	}

	public void waterAdvanceTime(Player player) {
		if (player.isDead()) {
			return;
		}

		if (player.isInsideVehicle()) {
			if (player.getVehicle() instanceof Boat) {
				return;
			}
		}

		SuperNPlayer snplayer = SuperNManager.get(player);

		Material material = player.getLocation().getBlock().getType();

		if (material == Material.STATIONARY_WATER || material == Material.WATER) {
			SuperNManager.alterPower(snplayer, SNConfigHandler.angelSwimPowerGain, "Swimming in water");
		}
	}

}