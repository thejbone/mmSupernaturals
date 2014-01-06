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
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.mmiillkkaa.supernaturals.SuperNPlayer;
import com.mmiillkkaa.supernaturals.SupernaturalsPlugin;
import com.mmiillkkaa.supernaturals.io.SNConfigHandler;
import com.mmiillkkaa.supernaturals.util.Language;

public class WereManager extends ClassManager {

    public WereManager() {
        super();
    }

    private String permissions2 = "supernatural.player.wolfbane";
    private static HashMap<Wolf, SuperNPlayer> wolvesMap = new HashMap<Wolf, SuperNPlayer>();

    // -------------------------------------------- //
    // Damage Events //
    // -------------------------------------------- //

    @Override
    public double victimEvent(EntityDamageEvent event, double damage) {
        if (event.getCause().equals(DamageCause.FALL)) {
            damage *= SNConfigHandler.wereDamageFall;
        }
        return damage;
    }

    @Override
    public double damagerEvent(EntityDamageByEntityEvent event, double damage) {
        Entity damager = event.getDamager();
        Player pDamager = (Player) damager;
        SuperNPlayer snDamager = SuperNManager.get(pDamager);
        ItemStack item = pDamager.getItemInHand();

        if (SuperNManager.worldTimeIsNight(pDamager)) {
            if (item != null) {
                if (SNConfigHandler.wereWeapons.contains(item.getType())) {
                    SuperNManager.sendMessage(snDamager,
                            Language.WEREWOLF_WEAPON_LIMIT.toString());
                    damage = 0;
                } else {
                    damage += damage
                            * snDamager.scale(SNConfigHandler.wereDamageFactor);
                }
            }
        }
        return damage;
    }

    @Override
    public void deathEvent(Player player) {
        SuperNPlayer snplayer = SuperNManager.get(player);

        SuperNManager.alterPower(snplayer,
                -SNConfigHandler.wereDeathPowerPenalty,
                Language.YOU_DIE.toString());
    }

    @Override
    public void killEvent(Player pDamager, SuperNPlayer damager,
            SuperNPlayer victim) {
        if (victim == null) {
            SuperNManager.alterPower(damager,
                    SNConfigHandler.wereKillPowerCreatureGain,
                    Language.KILL_CREATURE.toString());
        } else {
            double random = Math.random();
            if (victim.getPower() > SNConfigHandler.wereKillPowerPlayerGain) {
                SuperNManager.alterPower(damager,
                        SNConfigHandler.wereKillPowerPlayerGain,
                        Language.KILL_PLAYER.toString());
            } else {
                SuperNManager.sendMessage(damager,
                        Language.NO_POWER_GAIN.toString());
            }
            if (SNConfigHandler.wereKillSpreadCurse
                    && !victim.isSuper()
                    && SuperNManager
                            .worldTimeIsNight(SupernaturalsPlugin.instance
                                    .getServer().getPlayer(victim.getName()))) {
                if (random < SNConfigHandler.spreadChance) {
                    SuperNManager.sendMessage(victim,
                            Language.WEREWOLF_DEATH.toString());
                    SuperNManager.convert(victim, "werewolf");
                }
            }
        }

    }

    // -------------------------------------------- //
    // Interact //
    // -------------------------------------------- //

    @SuppressWarnings("deprecation")
    @Override
    public boolean playerInteract(PlayerInteractEvent event) {

        Action action = event.getAction();
        Player player = event.getPlayer();
        SuperNPlayer snplayer = SuperNManager.get(player);
        Material itemMaterial = event.getMaterial();

        if (action.equals(Action.LEFT_CLICK_AIR)
                || action.equals(Action.LEFT_CLICK_BLOCK)) {
            if (player.getItemInHand() == null) {
                return false;
            }

            if (itemMaterial.toString().equalsIgnoreCase(
                    SNConfigHandler.wolfMaterial)) {
                if (SuperNManager.worldTimeIsNight(player)) {
                    summon(player);
                    event.setCancelled(true);
                    return true;
                } else {
                    SuperNManager.sendMessage(snplayer,
                            Language.WEREWOLF_ABILITY_LIMIT.toString());
                    return false;
                }
            } else if (itemMaterial.toString().equalsIgnoreCase(
                    SNConfigHandler.wolfbaneMaterial)) {
                if (!SupernaturalsPlugin.hasPermissions(player, permissions2)) {
                    return false;
                }
                if (SuperNManager.worldTimeIsNight(player)) {
                    SuperNManager.sendMessage(snplayer,
                            Language.WEREWOLF_CURE_LIMIT.toString());
                    return false;
                } else {
                    wolfbane(player);
                    event.setCancelled(true);
                    return true;
                }
            } else if (itemMaterial.toString().equalsIgnoreCase(
                    SNConfigHandler.dashMaterial)) {
                if (SuperNManager.worldTimeIsNight(player)) {
                    SuperNManager.jump(event.getPlayer(),
                            SNConfigHandler.dashDeltaSpeed, false);
                    event.setCancelled(true);
                    return true;
                } else {
                    SuperNManager.sendMessage(snplayer,
                            Language.WEREWOLF_ABILITY_LIMIT.toString());
                    return false;
                }
            }
        }

        if (!(action.equals(Action.RIGHT_CLICK_AIR) || action
                .equals(Action.RIGHT_CLICK_BLOCK))) {
            return false;
        }

        if (action.equals(Action.RIGHT_CLICK_AIR)) {
            if (SuperNManager.worldTimeIsNight(player)) {
                if (itemMaterial != null) {
                    if (SNConfigHandler.foodMaterials.contains(itemMaterial)) {
                        if (itemMaterial.equals(Material.BREAD)) {
                            SuperNManager.sendMessage(snplayer,
                                    Language.WEREWOLF_EAT_LIMIT.toString());
                            return false;
                        } else {
                            SuperNManager.alterPower(snplayer,
                                    SNConfigHandler.werePowerFood,
                                    Language.WEREWOLF_EAT.toString());
                            player.setFoodLevel(player.getFoodLevel() + 6); // Hardcoded
                                                                            // value
                                                                            // :D
                            Inventory inv = player.getInventory();
                            inv.removeItem(new ItemStack(itemMaterial, 1));
                            player.updateInventory();
                            return true;
                        }
                    }
                }
            }
            if (itemMaterial != null) {
                if (SNConfigHandler.foodMaterials.contains(itemMaterial)) {
                    if (player.getFoodLevel() == 20) {
                        return false;
                    }
                    if (itemMaterial.equals(Material.BREAD)) {
                        SuperNManager.sendMessage(snplayer,
                                Language.WEREWOLF_EAT_LIMIT.toString());
                        return false;
                    } else {
                        SuperNManager.alterPower(snplayer,
                                SNConfigHandler.werePowerFood,
                                Language.WEREWOLF_EAT.toString());
                        player.setFoodLevel(player.getFoodLevel() + 6); // Hardcoded
                                                                        // value
                                                                        // :D
                        Inventory inv = player.getInventory();
                        inv.removeItem(new ItemStack(itemMaterial, 1));
                        player.updateInventory();
                        return true;
                    }
                }
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
                if (!SNConfigHandler.wereArmor.contains(helmet.getType())
                        && !helmet.getType().equals(Material.WOOL)) {
                    inv.setHelmet(null);
                    dropItem(player, helmet);
                }
            }
            if (chest != null) {
                if (!SNConfigHandler.wereArmor.contains(chest.getType())) {
                    inv.setChestplate(null);
                    dropItem(player, chest);
                }
            }
            if (leggings != null) {
                if (!SNConfigHandler.wereArmor.contains(leggings.getType())) {
                    inv.setLeggings(null);
                    dropItem(player, leggings);
                }
            }
            if (boots != null) {
                if (!SNConfigHandler.wereArmor.contains(boots.getType())) {
                    inv.setBoots(null);
                    dropItem(player, boots);
                }
            }
        }
    }

    // -------------------------------------------- //
    // Wolfbane //
    // -------------------------------------------- //

    public boolean wolfbane(Player player) {
        SuperNPlayer snplayer = SuperNManager.get(player);
        if (SNConfigHandler.wereWolfbaneRecipe.playerHasEnough(player)) {
            SuperNManager.sendMessage(snplayer,
                    Language.WEREWOLF_POTION_NOTICE_SELF.toString());
            SuperNManager.sendMessage(snplayer,
                    SNConfigHandler.wereWolfbaneRecipe.getRecipeLine());
            SNConfigHandler.wereWolfbaneRecipe.removeFromPlayer(player);
            SuperNManager.cure(snplayer);
            return true;
        } else {
            SuperNManager.sendMessage(snplayer,
                    Language.WEREWOLF_POTION_NEED.toString());
            SuperNManager.sendMessage(snplayer,
                    SNConfigHandler.wereWolfbaneRecipe.getRecipeLine());
            return false;
        }
    }

    // -------------------------------------------- //
    // Summonings //
    // -------------------------------------------- //

    public boolean summon(Player player) {
        SuperNPlayer snplayer = SuperNManager.get(player);
        ItemStack item = player.getItemInHand();
        if (!SupernaturalsPlugin.instance.getSpawn(player)) {
            SuperNManager.sendMessage(snplayer,
                    Language.WEREWOLF_SUMMON_NOT_ALLOW.toString());
            return false;
        }
        if (SuperNManager.worldTimeIsNight(player)) {
            if (snplayer.getPower() >= SNConfigHandler.werePowerSummonCost) {
                int i = 0;
                for (Wolf wolf : wolvesMap.keySet()) {
                    if (wolvesMap.get(wolf).equals(snplayer)) {
                        i++;
                    }
                }
                if (i <= 4) {
                    Wolf wolf = (Wolf) player.getWorld().spawnEntity(
                            player.getLocation(), EntityType.WOLF);
                    wolf.setTamed(true);
                    wolf.setOwner(player);
                    wolf.setHealth(8);
                    wolvesMap.put(wolf, snplayer);
                    SuperNManager.alterPower(snplayer,
                            -SNConfigHandler.werePowerSummonCost,
                            Language.WEREWOLF_SUMMON_WOLF.toString());
                    if (item.getAmount() == 1) {
                        player.setItemInHand(null);
                    } else {
                        item.setAmount(player.getItemInHand().getAmount() - 1);
                    }
                    return true;
                } else {
                    SuperNManager.sendMessage(snplayer,
                            Language.WEREWOLF_SUMMON_TOO_MUCH_WOLF.toString());
                    return false;
                }
            } else {
                SuperNManager.sendMessage(snplayer,
                        Language.NO_POWER.toString());
                return false;
            }
        } else {
            SuperNManager.sendMessage(snplayer,
                    Language.WEREWOLF_ABILITY_LIMIT.toString());
            return false;
        }
    }

    public static HashMap<Wolf, SuperNPlayer> getWolves() {
        return wolvesMap;
    }

    public static void removeWolf(Wolf wolf) {
        if (wolvesMap.containsKey(wolf)) {
            wolvesMap.remove(wolf);
        }
    }

    public static void removePlayer(SuperNPlayer player) {
        List<Wolf> removeWolf = new ArrayList<Wolf>();
        for (Wolf wolf : wolvesMap.keySet()) {
            if (wolvesMap.get(wolf).equals(player)) {
                wolf.setTamed(false);
                removeWolf.add(wolf);
            }
        }
        for (Wolf wolf : removeWolf) {
            wolvesMap.remove(wolf);
        }
    }
}
