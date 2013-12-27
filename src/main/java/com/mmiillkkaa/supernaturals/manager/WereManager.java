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
                            "狼人(Werewolve)無法在晚上使用武器!");
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
                -SNConfigHandler.wereDeathPowerPenalty, "你死了!");
    }

    @Override
    public void killEvent(Player pDamager, SuperNPlayer damager,
            SuperNPlayer victim) {
        if (victim == null) {
            SuperNManager.alterPower(damager,
                    SNConfigHandler.wereKillPowerCreatureGain, "擊殺生物!");
        } else {
            double random = Math.random();
            if (victim.getPower() > SNConfigHandler.wereKillPowerPlayerGain) {
                SuperNManager.alterPower(damager,
                        SNConfigHandler.wereKillPowerPlayerGain, "擊殺玩家!");
            } else {
                SuperNManager.sendMessage(damager, "你無法從沒有能量的玩家身上獲得能量.");
            }
            if (SNConfigHandler.wereKillSpreadCurse
                    && !victim.isSuper()
                    && SuperNManager
                            .worldTimeIsNight(SupernaturalsPlugin.instance
                                    .getServer().getPlayer(victim.getName()))) {
                if (random < SNConfigHandler.spreadChance) {
                    SuperNManager.sendMessage(victim,
                            "你的天性已被改變... 你感受到內心的野獸覺醒了.");
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
                    SuperNManager.sendMessage(snplayer, "無法在白天使用這個能力.");
                    return false;
                }
            } else if (itemMaterial.toString().equalsIgnoreCase(
                    SNConfigHandler.wolfbaneMaterial)) {
                if (!SupernaturalsPlugin.hasPermissions(player, permissions2)) {
                    return false;
                }
                if (SuperNManager.worldTimeIsNight(player)) {
                    SuperNManager.sendMessage(snplayer, "無法在晚上解除狼人的狀態.");
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
                    SuperNManager.sendMessage(snplayer, "無法在白天使用這個能力.");
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
                                    "狼人(Werewolve)無法從麵包獲得能量.");
                            return false;
                        } else {
                            SuperNManager.alterPower(snplayer,
                                    SNConfigHandler.werePowerFood, "吃吧!");
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
                                "狼人(Werewolve)無法從麵包獲得能量.");
                        return false;
                    } else {
                        SuperNManager.alterPower(snplayer,
                                SNConfigHandler.werePowerFood, "吃吧!");
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
            SuperNManager
                    .sendMessage(snplayer, "你創造了了狼人治癒藥水(wolfbane potion)!");
            SuperNManager.sendMessage(snplayer,
                    SNConfigHandler.wereWolfbaneRecipe.getRecipeLine());
            SNConfigHandler.wereWolfbaneRecipe.removeFromPlayer(player);
            SuperNManager.cure(snplayer);
            return true;
        } else {
            SuperNManager.sendMessage(snplayer,
                    "如果你沒有這些材料就不能製造狼人治癒藥水(Wolfbane potion): ");
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
            SuperNManager.sendMessage(snplayer, "你無法在這招喚(Summon).");
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
                            -SNConfigHandler.werePowerSummonCost, "招喚野狼!");
                    if (item.getAmount() == 1) {
                        player.setItemInHand(null);
                    } else {
                        item.setAmount(player.getItemInHand().getAmount() - 1);
                    }
                    return true;
                } else {
                    SuperNManager.sendMessage(snplayer, "你已擁有所有你可控制的狼群了.");
                    return false;
                }
            } else {
                SuperNManager.sendMessage(snplayer, "沒有足夠的力量來招喚(Summon).");
                return false;
            }
        } else {
            SuperNManager.sendMessage(snplayer, "無法在白天使用狼人(Werewolf)能力!");
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
