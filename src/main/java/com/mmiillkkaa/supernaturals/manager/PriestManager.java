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
import com.mmiillkkaa.supernaturals.util.Language;
import com.mmiillkkaa.supernaturals.util.LanguageTag;

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
                        Language.PRIEST_LIMIT_WEAPON.toString());
                return 0;
            }
        }

        if (victim instanceof Animals && !(victim instanceof Wolf)) {
            SuperNManager.sendMessage(SuperNManager.get(pDamager),
                    Language.PRIEST_LIMIT_TARGET.toString());
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
                -SNConfigHandler.priestDeathPowerPenalty,
                Language.YOU_DIE.toString());
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
                                        Language.PRIEST_LEAVE_CRUSH.toString());
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
                                        Language.PRIEST_DONATE_ACCEPT
                                                .toString());
                                SuperNManager.alterPower(snplayer, delta,
                                        Language.DAEMON_SNARE_NOTICE_SELF
                                                .toString());
                            }
                        } else {
                            SuperNManager.sendMessage(snplayer,
                                    Language.PRIEST_ALTAR_POWER_HUMAN
                                            .toString());
                            if (snplayer.isSuper()) {
                                SuperNManager
                                        .sendMessage(
                                                snplayer,
                                                Language.PRIEST_ALTAR_POWER_SUPERNATURAL
                                                        .toString());
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
                                            Language.PRIEST_ALTAR_NOT_ALLOW
                                                    .toString());
                                    return;
                                }
                                SuperNManager.sendMessage(snplayer,
                                        Language.PRIEST_DONATE_CONFIRM
                                                .toString());
                                SuperNManager.sendMessage(snplayer,
                                        SNConfigHandler.priestAltarRecipe
                                                .getRecipeLine());
                                SuperNManager.sendMessage(snplayer,
                                        Language.PRIEST_DONATE_ENOUGHT
                                                .toString());
                                SNConfigHandler.priestAltarRecipe
                                        .removeFromPlayer(player);
                                SuperNManager.convert(snplayer, "priest",
                                        SNConfigHandler.priestPowerStart);
                            } else {
                                SuperNManager.sendMessage(snplayer,
                                        Language.PRIEST_DONATE_NOT_ENOGHT
                                                .toString());
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
                    Language.PRIEST_DONATE_ONLY.toString());
        } else {
            player.updateInventory();
            SuperNManager.sendMessage(snplayer,
                    Language.PRIEST_DONATE_REWARD.toString());
            SuperNManager.alterPower(snplayer, delta * .5,
                    Language.PRIEST_DONATE_NOTICE_SELF.toString());
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
                    Language.NOT_ALLOW_PVP.toString());
            return false;
        }
        if (snplayer.getPower() > SNConfigHandler.priestPowerBanish) {
            if (snvictim.isSuper()) {
                SuperNManager.alterPower(
                        snplayer,
                        -SNConfigHandler.priestPowerBanish,
                        Language.PRIEST_BANISH_NOTICE_SELF.toString()
                                .replace(LanguageTag.PLAYER.toString(),
                                        victim.getName()));
                SuperNManager.sendMessage(
                        snvictim,
                        Language.PRIEST_BANISH_NOTICE_OTHER.toString().replace(
                                LanguageTag.PLAYER.toString(),
                                snplayer.getName()));
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
                    Language.ONLY_ON_SUPERNATUTAL.toString());
            return false;
        } else {
            SuperNManager.sendMessage(snplayer, Language.NO_POWER.toString());
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
                SuperNManager.alterPower(
                        snplayer,
                        -SNConfigHandler.priestPowerHeal,
                        Language.PRIEST_HEAL_NOTICE_SELF.toString()
                                .replace(LanguageTag.PLAYER.toString(),
                                        victim.getName()));
                SuperNManager.sendMessage(
                        snvictim,
                        Language.PRIEST_HEAL_NOTICE_OTHER.toString().replace(
                                LanguageTag.PLAYER.toString(),
                                snplayer.getName()));
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
                SuperNManager.sendMessage(snplayer,
                        Language.ONLY_ON_PLAYER.toString());
                return false;
            }
        } else {
            SuperNManager.sendMessage(snplayer, Language.NO_POWER.toString());
            return false;
        }
    }

    public boolean exorcise(Player player, Player victim) {
        SuperNPlayer snplayer = SuperNManager.get(player);
        SuperNPlayer snvictim = SuperNManager.get(victim);
        if (!SupernaturalsPlugin.instance.getPvP(victim)) {
            SuperNManager.sendMessage(snplayer,
                    Language.NOT_ALLOW_PVP.toString());
            return false;
        }
        if (snplayer.getPower() > SNConfigHandler.priestPowerExorcise) {
            if (snvictim.isSuper()) {
                SuperNManager.alterPower(
                        snplayer,
                        -SNConfigHandler.priestPowerExorcise,
                        Language.PRIEST_EXORISE_NOTICE_SELF.toString()
                                .replace(LanguageTag.PLAYER.toString(),
                                        victim.getName()));
                SuperNManager.sendMessage(
                        snvictim,
                        Language.PRIEST_EXORISE_NOTICE_OTHER.toString()
                                .replace(LanguageTag.PLAYER.toString(),
                                        snplayer.getName()));
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
                        Language.ONLY_ON_SUPERNATUTAL.toString());
                return false;
            }
        } else {
            SuperNManager.sendMessage(snplayer, Language.NO_POWER.toString());
            return false;
        }
    }

    public boolean cure(Player player, Player victim, Material material) {
        SuperNPlayer snplayer = SuperNManager.get(player);
        SuperNPlayer snvictim = SuperNManager.get(victim);
        if (snplayer.getPower() > SNConfigHandler.priestPowerCure) {
            if (snvictim.isSuper()) {
                if (victim.getItemInHand().getType().equals(material)) {
                    SuperNManager.alterPower(
                            snplayer,
                            -SNConfigHandler.priestPowerCure,
                            Language.PRIEST_CURE_NOTICE_SELF.toString()
                                    .replace(LanguageTag.PLAYER.toString(),
                                            victim.getName()));
                    SuperNManager.sendMessage(
                            snvictim,
                            Language.PRIEST_CURE_NOTICE_OTHER.toString()
                                    .replace(LanguageTag.PLAYER.toString(),
                                            snplayer.getName()));
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
                    SuperNManager.sendMessage(
                            snplayer,
                            Language.PRIEST_CURE_NOT_HOLD
                                    .toString()
                                    .replace(LanguageTag.PLAYER.toString(),
                                            snvictim.getName())
                                    .replace(LanguageTag.MATERIAL.toString(),
                                            material.toString()));
                    return false;
                }
            } else {
                SuperNManager.sendMessage(snplayer,
                        Language.ONLY_ON_SUPERNATUTAL.toString());
                return false;
            }
        } else {
            SuperNManager.sendMessage(snplayer, Language.NO_POWER.toString());
            return false;
        }
    }

    public boolean drainPower(Player player, Player victim) {
        SuperNPlayer snplayer = SuperNManager.get(player);
        SuperNPlayer snvictim = SuperNManager.get(victim);
        if (!SupernaturalsPlugin.instance.getPvP(victim)) {
            SuperNManager.sendMessage(snplayer,
                    Language.NOT_ALLOW_PVP.toString());
            return false;
        }
        if (snplayer.getPower() > SNConfigHandler.priestPowerDrain) {
            if (snvictim.isSuper()) {
                double power = snvictim.getPower();
                power *= SNConfigHandler.priestDrainFactor;
                SuperNManager.alterPower(
                        snplayer,
                        -SNConfigHandler.priestPowerDrain,
                        Language.PRIEST_DRAIN_NOTICE_SELF.toString().replace(
                                LanguageTag.PLAYER.toString(),
                                snvictim.getName()));
                SuperNManager.alterPower(
                        snvictim,
                        -power,
                        Language.PRIEST_DRAIN_NOTICE_OTHER.toString().replace(
                                LanguageTag.PLAYER.toString(),
                                snplayer.getName()));
                ItemStack item = player.getItemInHand();
                if (item.getAmount() == 1) {
                    player.setItemInHand(null);
                } else {
                    item.setAmount(player.getItemInHand().getAmount() - 1);
                }
                return true;
            } else {
                SuperNManager.sendMessage(snplayer,
                        Language.ONLY_ON_SUPERNATUTAL.toString());
                return false;
            }
        } else {
            SuperNManager.sendMessage(snplayer, Language.NO_POWER.toString());
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
                            Language.PRIEST_GUARDANGEL_REMOVE_NOTICE_SELF
                                    .toString().replace(
                                            LanguageTag.PLAYER.toString(),
                                            SupernaturalsPlugin.instance
                                                    .getDataHandler()
                                                    .getAngelPlayer(priest)
                                                    .getName()));
                    SuperNManager.sendMessage(SupernaturalsPlugin.instance
                            .getDataHandler().getAngelPlayer(priest),
                            Language.PRIEST_GUARDANGEL_REMOVE_NOTICE_OTHER
                                    .toString());
                    SupernaturalsPlugin.instance.getDataHandler().removeAngel(
                            priest);
                }
                SuperNManager.sendMessage(snvictim,
                        Language.PRIEST_GUARDANGEL_NOTICE_OTHER.toString());
                SuperNManager.alterPower(
                        priest,
                        -SNConfigHandler.priestPowerGuardianAngel,
                        Language.PRIEST_GUARDANGEL_NOTICE_SELF.toString()
                                .replace(LanguageTag.PLAYER.toString(),
                                        snvictim.getName()));
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
                    Language.ONLY_ON_PLAYER.toString());
            return false;
        } else {
            SuperNManager.sendMessage(priest, Language.NO_POWER.toString());
            return false;
        }
    }
}
