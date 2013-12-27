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

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.mmiillkkaa.supernaturals.SuperNPlayer;
import com.mmiillkkaa.supernaturals.SupernaturalsPlugin;
import com.mmiillkkaa.supernaturals.io.SNConfigHandler;

public class PriestManager extends HumanManager {

	public SupernaturalsPlugin plugin;

	public PriestManager(SupernaturalsPlugin instance) {
		super(instance);
		plugin = instance;
	}

	// -------------------------------------------- //
	// Damage Events //
	// -------------------------------------------- //

	@Override
	public double victimEvent(EntityDamageEvent event, double damage) {
		return damage;
	}

	@Override
	public double damagerEvent(EntityDamageByEntityEvent event, double damage) {
		Player pDamager = (Player) event.getDamager();
		Entity victim = event.getEntity();

		SuperNPlayer snDamager = SuperNManager.get(pDamager);
		ItemStack item = pDamager.getItemInHand();

		if (item != null) {
			if (SNConfigHandler.priestWeapons.contains(item.getType())) {
				SuperNManager.sendMessage(snDamager,
						"牧師(Priests)無法使用這個武器!");
				return 0;
			}
		}

		if (victim instanceof Animals && !(victim instanceof Wolf)) {
			SuperNManager.sendMessage(SuperNManager.get(pDamager),
					"你不能傷害無辜的動物.");
			damage = 0;
		} else if (victim instanceof Player) {
			Player pVictim = (Player) victim;
			if (!SupernaturalsPlugin.instance.getPvP(pVictim)) {
				return damage;
			}
			SuperNPlayer snvictim = SuperNManager.get(pVictim);
			if (snvictim.isSuper()) {
				if (!snvictim.isDemon()) {
					pVictim.setFireTicks(SNConfigHandler.priestFireTicks);
				}
				damage += damage
						* SuperNManager.get(pDamager).scale(
								SNConfigHandler.priestDamageFactorAttackSuper);
			} else {
				damage += damage
						* SuperNManager.get(pDamager).scale(
								SNConfigHandler.priestDamageFactorAttackHuman);
			}
		} else if (victim instanceof Monster) {
			Monster mVictim = (Monster) victim;
			mVictim.setFireTicks(SNConfigHandler.priestFireTicks);
		}
		return damage;
	}

	@Override
	public void deathEvent(Player player) {
		super.deathEvent(player);
		SuperNPlayer snplayer = SuperNManager.get(player);
		SuperNManager.alterPower(snplayer,
				-SNConfigHandler.priestDeathPowerPenalty, "你死了!");
	}

	@Override
	public void killEvent(Player pDamager, SuperNPlayer damager,
			SuperNPlayer victim) {
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
			if (itemMaterial.equals(Material.BOWL)) {
				remoteDonations(player);
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
				if (!SNConfigHandler.priestArmor.contains(helmet.getType())) {
					inv.setHelmet(null);
					dropItem(player, helmet);
				}
			}
			if (chest != null) {
				if (!SNConfigHandler.priestArmor.contains(chest.getType())) {
					inv.setChestplate(null);
					dropItem(player, chest);
				}
			}
			if (leggings != null) {
				if (!SNConfigHandler.priestArmor.contains(leggings.getType())) {
					inv.setLeggings(null);
					dropItem(player, leggings);
				}
			}
			if (boots != null) {
				if (!SNConfigHandler.priestArmor.contains(boots.getType())) {
					inv.setBoots(null);
					dropItem(player, boots);
				}
			}
		}
	}

	// -------------------------------------------- //
	// Church //
	// -------------------------------------------- //

	@SuppressWarnings("deprecation")
	public void useAltar(Player player) {
		Location location = player.getLocation();
		World world = location.getWorld();
		int locX = location.getBlockX();
		int locY = location.getBlockY();
		int locZ = location.getBlockZ();
		SuperNPlayer snplayer = SuperNManager.get(player);
		int amount = 0;
		int delta = 0;
		if (world.getName().equalsIgnoreCase(SNConfigHandler.priestChurchWorld)) {
			if (Math.abs(locX - SNConfigHandler.priestChurchLocationX) <= 10) {
				if (Math.abs(locY - SNConfigHandler.priestChurchLocationY) <= 10) {
					if (Math.abs(locZ - SNConfigHandler.priestChurchLocationZ) <= 10) {
						if (snplayer.isPriest()) {
							if (player.getItemInHand().getType()
									.equals(Material.COAL)) {
								SuperNManager.sendMessage(snplayer,
										"你已被逐出教會!");
								SuperNManager.cure(snplayer);
							} else {
								PlayerInventory inv = player.getInventory();
								ItemStack[] items = inv.getContents();
								for (Material mat : SNConfigHandler.priestDonationMap
										.keySet()) {
									for (ItemStack itemStack : items) {
										if (itemStack != null) {
											if (itemStack.getType().equals(mat)) {
												amount += itemStack.getAmount();
											}
										}
									}
									delta += amount
											* SNConfigHandler.priestDonationMap
													.get(mat);
									amount = 0;
								}
								for (Material mat : SNConfigHandler.priestDonationMap
										.keySet()) {
									inv.remove(mat);
								}
								player.updateInventory();
								SuperNManager.sendMessage(snplayer,
												"教會接受你盛情捐贈的麵包(Bread), 魚(Fish), 烤豬肉(Grilled Pork)和頻果(Apole).");
								SuperNManager.alterPower(snplayer, delta,
										"已捐贈!");
							}
						} else {
							SuperNManager.sendMessage(snplayer,
									"教會的祭壇散發著神聖力量.");
							if (snplayer.isSuper()) {
								SuperNManager.sendMessage(snplayer,
												"這股神聖的力量讓你哭得四分五裂!");
								EntityDamageEvent event = new EntityDamageEvent(
										player, DamageCause.BLOCK_EXPLOSION, 20);
								player.setLastDamageCause(event);
								player.setHealth(0);
								if (snplayer.isGhoul()) {
									double random = Math.random();
									if (random < SNConfigHandler.ghoulCureChance - 0.1) {
										SuperNManager.cure(snplayer);
									}
								}
								return;
							}
							if (SNConfigHandler.priestAltarRecipe
									.playerHasEnough(player)) {
								if (!SupernaturalsPlugin.hasPermissions(player,
										"supernatural.player.shrineuse.priest")) {
									SuperNManager.sendMessage(snplayer,
											"你無法使用牧師(Priest)祭壇.");
									return;
								}
								SuperNManager
										.sendMessage(snplayer,
												"你捐贈這些物品給教會:");
								SuperNManager.sendMessage(snplayer,
										SNConfigHandler.priestAltarRecipe
												.getRecipeLine());
								SuperNManager.sendMessage(snplayer,
												"教會承認你神聖的靈魂並且賦予你牧師的身份.");
								SNConfigHandler.priestAltarRecipe
										.removeFromPlayer(player);
								SuperNManager.convert(snplayer, "priest",
										SNConfigHandler.priestPowerStart);
							} else {
								SuperNManager.sendMessage(snplayer,
												"教會認定你的捐獻不足. 你必須收集以下物品: ");
								SuperNManager.sendMessage(snplayer,
										SNConfigHandler.priestAltarRecipe
												.getRecipeLine());
							}
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void remoteDonations(Player player) {
		SuperNPlayer snplayer = SuperNManager.get(player);
		PlayerInventory inv = player.getInventory();
		ItemStack[] items = inv.getContents();
		double delta = 0;
		invCheck: for (Material mat : SNConfigHandler.priestDonationMap
				.keySet()) {
			for (ItemStack itemStack : items) {
				if (itemStack != null) {
					if (itemStack.getType().equals(mat)) {
						delta = SNConfigHandler.priestDonationMap.get(mat);
						if (itemStack.getAmount() == 1) {
							inv.clear(inv.first(itemStack.getType()));
						} else {
							itemStack.setAmount(itemStack.getAmount() - 1);
						}
						break invCheck;
					}
				}
			}
		}
		if (delta == 0) {
			SuperNManager.sendMessage(snplayer,
							"教會只能接受麵包(Bread), 魚(Fish), 烤豬肉(Grilled)和蘋果(Applea)的捐獻.");
		} else {
			player.updateInventory();
			SuperNManager.sendMessage(snplayer,
					"因為捐獻你而獲得了一些能量.");
			SuperNManager.alterPower(snplayer, delta * .5, "已捐獻!");
		}
	}

	// -------------------------------------------- //
	// Spells //
	// -------------------------------------------- //

	@Override
	public void spellEvent(EntityDamageByEntityEvent event, Player target) {
		Player player = (Player) event.getDamager();
		SuperNPlayer snplayer = SuperNManager.get(player);
		Material itemMaterial = player.getItemInHand().getType();

		boolean cancelled = false;

		if (player.getItemInHand() == null) {
			return;
		}

		if (itemMaterial != null) {
			if (SNConfigHandler.priestSpellMaterials.contains(itemMaterial)) {
				if (itemMaterial.equals(SNConfigHandler.priestSpellMaterials
						.get(0))) {
					banish(player, target);
					cancelled = true;
				} else if (itemMaterial
						.equals(SNConfigHandler.priestSpellMaterials.get(1))) {
					exorcise(player, target);
					cancelled = true;
				} else if (itemMaterial
						.equals(SNConfigHandler.priestSpellMaterials.get(2))) {
					cancelled = cure(player, target, itemMaterial);
				} else if (itemMaterial
						.equals(SNConfigHandler.priestSpellMaterials.get(3))) {
					cancelled = heal(player, target);
				} else if (itemMaterial
						.equals(SNConfigHandler.priestSpellMaterials.get(4))) {
					drainPower(player, target);
					cancelled = true;
				}
				if (!event.isCancelled()) {
					event.setCancelled(cancelled);
				}
            } else if (itemMaterial.toString().equalsIgnoreCase(
					SNConfigHandler.priestSpellGuardianAngel)) {
				cancelled = guardianAngel(player, target);
				if (!event.isCancelled()) {
					event.setCancelled(cancelled);
				}
            } else if (itemMaterial.equals(Material.BOWL)) {
				remoteDonations(player);
            }
		}
	}

	public boolean banish(Player player, Player victim) {
		SuperNPlayer snplayer = SuperNManager.get(player);
		SuperNPlayer snvictim = SuperNManager.get(victim);
		if (!SupernaturalsPlugin.instance.getPvP(victim)) {
			SuperNManager.sendMessage(snplayer,
					"無法再禁止戰鬥的地方施展.");
			return false;
		}
		if (snplayer.getPower() > SNConfigHandler.priestPowerBanish) {
			if (snvictim.isSuper()) {
				SuperNManager.alterPower(snplayer,
						-SNConfigHandler.priestPowerBanish, "放逐了 "
								+ victim.getName());
				SuperNManager.sendMessage(snvictim, "你已被 "
						+ ChatColor.WHITE + snplayer.getName() + ChatColor.RED
						+ "　放逐(Banish)!");
				victim.teleport(SNConfigHandler.priestBanishLocation);
				ItemStack item = player.getItemInHand();
				if (item.getAmount() == 1) {
					player.setItemInHand(null);
				} else {
					item.setAmount(player.getItemInHand().getAmount() - 1);
				}
				return true;
			}
			SuperNManager.sendMessage(snplayer,
					"只能放逐(Banish)超自然玩家.");
			return false;
		} else {
			SuperNManager.sendMessage(snplayer, "沒有足夠的能量來放逐(Banish).");
			return false;
		}
	}

	public boolean heal(Player player, Player victim) {
		SuperNPlayer snplayer = SuperNManager.get(player);
		SuperNPlayer snvictim = SuperNManager.get(victim);
		if (snplayer.getPower() > SNConfigHandler.priestPowerHeal) {
			if (!snvictim.isSuper()
					&& victim.getHealth() < victim.getMaxHealth()
					&& !victim.isDead()) {
				SuperNManager.alterPower(snplayer,
						-SNConfigHandler.priestPowerHeal,
						"治療了 " + victim.getName());
				SuperNManager.sendMessage(snvictim, "你被 "
						+ ChatColor.WHITE + snplayer.getName() + ChatColor.RED
						+ "　治療了(Heal)!");
				double health = victim.getHealth()
						+ SNConfigHandler.priestHealAmount;
				if (health > victim.getMaxHealth()) {
					health = victim.getMaxHealth();
				}
				victim.setHealth(health);
				ItemStack item = player.getItemInHand();
				if (item.getAmount() == 1) {
					player.setItemInHand(null);
				} else {
					item.setAmount(player.getItemInHand().getAmount() - 1);
				}
				return true;
			} else {
				SuperNManager.sendMessage(snplayer, "這個玩家無法被治療(Heal).");
				return false;
			}
		} else {
			SuperNManager.sendMessage(snplayer, "沒有足夠的能量來治療(Heal).");
			return false;
		}
	}

	public boolean exorcise(Player player, Player victim) {
		SuperNPlayer snplayer = SuperNManager.get(player);
		SuperNPlayer snvictim = SuperNManager.get(victim);
		if (!SupernaturalsPlugin.instance.getPvP(victim)) {
			SuperNManager.sendMessage(snplayer,
					"無法再禁止戰鬥的地方施展.");
			return false;
		}
		if (snplayer.getPower() > SNConfigHandler.priestPowerExorcise) {
			if (snvictim.isSuper()) {
				SuperNManager.alterPower(snplayer,
						-SNConfigHandler.priestPowerExorcise, "淨化了 "
								+ victim.getName());
				SuperNManager.sendMessage(snvictim, "你被 "
						+ ChatColor.WHITE + snplayer.getName() + ChatColor.RED
						+ " 淨化(Exorcise)了!");
				SuperNManager.cure(snvictim);
				ItemStack item = player.getItemInHand();
				if (item.getAmount() == 1) {
					player.setItemInHand(null);
				} else {
					item.setAmount(player.getItemInHand().getAmount() - 1);
				}
				return true;
			} else {
				SuperNManager.sendMessage(snplayer,
						"只有超自然玩家可以被淨化(Exorcise).");
				return false;
			}
		} else {
			SuperNManager
					.sendMessage(snplayer, "沒有足夠的能量來淨化(Exorcise).");
			return false;
		}
	}

	public boolean cure(Player player, Player victim, Material material) {
		SuperNPlayer snplayer = SuperNManager.get(player);
		SuperNPlayer snvictim = SuperNManager.get(victim);
		if (snplayer.getPower() > SNConfigHandler.priestPowerCure) {
			if (snvictim.isSuper()) {
				if (victim.getItemInHand().getType().equals(material)) {
					SuperNManager.alterPower(snplayer,
							-SNConfigHandler.priestPowerCure,
							"治癒了 " + victim.getName());
					SuperNManager.sendMessage(snvictim, ChatColor.WHITE
							+ snplayer.getName() + ChatColor.RED
							+ " 讓你重拾了人性");
					SuperNManager.cure(snvictim);
					ItemStack item = player.getItemInHand();
					ItemStack item2 = victim.getItemInHand();
					if (item.getAmount() == 1) {
						player.setItemInHand(null);
					} else {
						item.setAmount(player.getItemInHand().getAmount() - 1);
					}
					if (item2.getAmount() == 1) {
						victim.setItemInHand(null);
					} else {
						item2.setAmount(victim.getItemInHand().getAmount() - 1);
					}
					return true;
				} else {
					SuperNManager.sendMessage(snplayer,
							ChatColor.WHITE + snvictim.getName()
									+ ChatColor.RED + " 沒有拿著 "
									+ ChatColor.WHITE + material.toString()
									+ ChatColor.RED + ".");
					return false;
				}
			} else {
				SuperNManager.sendMessage(snplayer,
						"你只能治癒(Cure)超自然玩家.");
				return false;
			}
		} else {
			SuperNManager.sendMessage(snplayer, "沒有足夠的能量來治癒(Cure).");
			return false;
		}
	}

	public boolean drainPower(Player player, Player victim) {
		SuperNPlayer snplayer = SuperNManager.get(player);
		SuperNPlayer snvictim = SuperNManager.get(victim);
		if (!SupernaturalsPlugin.instance.getPvP(victim)) {
			SuperNManager.sendMessage(snplayer,
					"無法再禁止戰鬥的地方施展.");
			return false;
		}
		if (snplayer.getPower() > SNConfigHandler.priestPowerDrain) {
			if (snvictim.isSuper()) {
				double power = snvictim.getPower();
				power *= SNConfigHandler.priestDrainFactor;
				SuperNManager.alterPower(snplayer,
						-SNConfigHandler.priestPowerDrain, "流失了 "
								+ snvictim.getName() + " 的能量!");
				SuperNManager.alterPower(snvictim, -power, "被 "
						+ snplayer.getName() + "流失了能量");
				ItemStack item = player.getItemInHand();
				if (item.getAmount() == 1) {
					player.setItemInHand(null);
				} else {
					item.setAmount(player.getItemInHand().getAmount() - 1);
				}
				return true;
			} else {
				SuperNManager.sendMessage(snplayer,
						"只有超自然玩家可以被流失(Drain)能量.");
				return false;
			}
		} else {
			SuperNManager.sendMessage(snplayer,
					"沒有足夠的能量來流失(Drain)能量.");
			return false;
		}
	}

	public boolean guardianAngel(Player player, Player victim) {
		SuperNPlayer priest = SuperNManager.get(player);
		SuperNPlayer snvictim = SuperNManager.get(victim);

		if (priest.getPower() > SNConfigHandler.priestPowerGuardianAngel) {
			if (!snvictim.isSuper()) {
				if (SupernaturalsPlugin.instance.getDataHandler().hasAngel(
						priest)) {
					SuperNManager.sendMessage(
							priest,
							"從 " + ChatColor.WHITE +
                                                        SupernaturalsPlugin.instance.getDataHandler().getAngelPlayer(priest).getName() +
                                                        ChatColor.RED + "　的身上移除了守護天使(Guardian Angel)");
					SuperNManager.sendMessage(SupernaturalsPlugin.instance.getDataHandler().getAngelPlayer(priest),
							"已移除守護天使(Guardian Angel)!");
					SupernaturalsPlugin.instance.getDataHandler().removeAngel(
							priest);
				}
				SuperNManager.sendMessage(snvictim,
						"一個守護天使(Guardian Angel)出現在你的身旁!");
				SuperNManager.alterPower(
						priest,
						-SNConfigHandler.priestPowerGuardianAngel,
						"施展守護天使(Guardian Angel)在 " + ChatColor.WHITE
								+ snvictim.getName() + ChatColor.RED + " 的身上!");
				SupernaturalsPlugin.instance.getDataHandler().addAngel(priest,
						snvictim);

				ItemStack item = player.getItemInHand();
				if (item.getAmount() == 1) {
					player.setItemInHand(null);
				} else {
					item.setAmount(player.getItemInHand().getAmount() - 1);
				}
				return true;
			}
			SuperNManager.sendMessage(priest,
							"你無法在超自然玩家身上施放守護天使(Guardian Angel).");
			return false;
		} else {
			SuperNManager.sendMessage(priest,
					"沒有足夠的力量施展守護天使(Guardian Angel).");
			return false;
		}
	}
}
