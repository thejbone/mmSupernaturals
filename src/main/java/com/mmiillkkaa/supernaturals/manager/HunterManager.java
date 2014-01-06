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
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.Door;

import com.mmiillkkaa.supernaturals.SuperNPlayer;
import com.mmiillkkaa.supernaturals.SupernaturalsPlugin;
import com.mmiillkkaa.supernaturals.io.SNConfigHandler;
import com.mmiillkkaa.supernaturals.util.Language;
import com.mmiillkkaa.supernaturals.util.LanguageTag;

public class HunterManager extends HumanManager {

    public HunterManager(SupernaturalsPlugin instance) {
        super(instance);
    }

    private HashMap<Arrow, String> arrowMap = new HashMap<Arrow, String>();
    private HashMap<SuperNPlayer, String> hunterMap = new HashMap<SuperNPlayer, String>();
    private ArrayList<Player> grapplingPlayers = new ArrayList<Player>();
    private ArrayList<Player> drainedPlayers = new ArrayList<Player>();
    private ArrayList<Location> hallDoors = new ArrayList<Location>();
    private ArrayList<SuperNPlayer> playerInvites = new ArrayList<SuperNPlayer>();
    private static ArrayList<SuperNPlayer> bountyList = new ArrayList<SuperNPlayer>();

    private String arrowType = "normal";

    // -------------------------------------------- //
    // Damage Events //
    // -------------------------------------------- //

    @Override
    public double victimEvent(EntityDamageEvent event, double damage) {
        if (event.getCause().equals(DamageCause.FALL)) {
            damage -= SNConfigHandler.hunterFallReduction;
            if (damage < 0) {
                damage = 0;
            }
        }
        return damage;
    }

    @Override
    public boolean shootArrow(Player shooter, EntityShootBowEvent event) {
        if (event.getProjectile() instanceof Arrow) {
            boolean cancelled = shoot(shooter, (Arrow) event.getProjectile());
            if (cancelled) {
                return true;
            }
        }
        return false;
    }

    @Override
    public double damagerEvent(EntityDamageByEntityEvent event, double damage) {
        if (event.getDamager() instanceof Projectile) {
            Entity victim = event.getEntity();
            if (event.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow) event.getDamager();
                if (getArrowMap().containsKey(arrow)) {
                    arrowType = getArrowType(arrow);
                } else {
                    arrowType = "normal";
                }
            }
            if (arrowType.equalsIgnoreCase("power")) {
                damage += damage * SNConfigHandler.hunterPowerArrowDamage;
            } else if (arrowType.equalsIgnoreCase("fire")) {
                victim.setFireTicks(SNConfigHandler.hunterFireArrowFireTicks);
            }
            return damage;
        } else {
            Player pDamager = (Player) event.getDamager();
            SuperNPlayer snDamager = SuperNManager.get(pDamager);
            ItemStack item = pDamager.getItemInHand();

            // Check Weapons and Modify Damage
            if (item != null) {
                if (SNConfigHandler.hunterWeapons.contains(item.getType())) {
                    SuperNManager.sendMessage(snDamager,
                            Language.WITCHHUNTER_LIMIT_WEAPON.toString());
                    return 0;
                }
            }
            return damage;
        }
    }

    @Override
    public void deathEvent(Player player) {
        if (player == null) {
            return;
        }
        super.deathEvent(player);
        SuperNPlayer snplayer = SuperNManager.get(player);
        SuperNManager.alterPower(snplayer,
                -SNConfigHandler.hunterDeathPowerPenalty,
                Language.YOU_DIE.toString());
    }

    @Override
    public void killEvent(Player pDamager, SuperNPlayer damager,
            SuperNPlayer victim) {
        if (victim == null) {
            if (SNConfigHandler.hunterKillPowerCreatureGain > 0) {
                SuperNManager.alterPower(damager,
                        SNConfigHandler.hunterKillPowerCreatureGain,
                        Language.KILL_CREATURE.toString());
            }
        } else {
            if (victim.getPower() > SNConfigHandler.hunterKillPowerPlayerGain) {
                SuperNManager.alterPower(damager,
                        SNConfigHandler.hunterKillPowerPlayerGain,
                        Language.KILL_PLAYER.toString());
                if (HunterManager.checkBounty(victim)) {
                    SuperNManager.alterPower(damager,
                            SNConfigHandler.hunterBountyCompletion,
                            Language.WITCHHUNTER_BOUNTY_GET.toString());
                    HunterManager.removeBounty(victim);
                    HunterManager.addBounty();
                }
            } else {
                SuperNManager.sendMessage(damager,
                        Language.NO_POWER_GAIN.toString());
            }
        }
    }

    // -------------------------------------------- //
    // Interact //
    // -------------------------------------------- //

    @Override
    public boolean playerInteract(PlayerInteractEvent event) {

        Action action = event.getAction();
        Player player = event.getPlayer();
        SuperNPlayer snplayer = SuperNManager.get(player);

        if (action.equals(Action.LEFT_CLICK_AIR)
                || action.equals(Action.LEFT_CLICK_BLOCK)) {
            if (player.getItemInHand() == null) {
                return false;
            }

            if (player.getItemInHand().getType().equals(Material.BOW)) {
                changeArrowType(snplayer);
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
                if (!SNConfigHandler.hunterArmor.contains(helmet.getType())) {
                    inv.setHelmet(null);
                    dropItem(player, helmet);
                }
            }
            if (chest != null) {
                if (!SNConfigHandler.hunterArmor.contains(chest.getType())) {
                    inv.setChestplate(null);
                    dropItem(player, chest);
                }
            }
            if (leggings != null) {
                if (!SNConfigHandler.hunterArmor.contains(leggings.getType())) {
                    inv.setLeggings(null);
                    dropItem(player, leggings);
                }
            }
            if (boots != null) {
                if (!SNConfigHandler.hunterArmor.contains(boots.getType())) {
                    inv.setBoots(null);
                    dropItem(player, boots);
                }
            }
        }
    }

    // -------------------------------------------- //
    // Bounties //
    // -------------------------------------------- //

    public static ArrayList<SuperNPlayer> getBountyList() {
        return bountyList;
    }

    public static boolean checkBounty(SuperNPlayer snplayer) {
        return bountyList.contains(snplayer);
    }

    public static boolean removeBounty(SuperNPlayer snplayer) {
        if (bountyList.contains(snplayer)) {
            bountyList.remove(snplayer);
            return true;
        }
        return false;
    }

    public static void addBounty() {
        List<SuperNPlayer> targets = SuperNManager.getSupernaturals();
        boolean bountyFound = false;
        Random generator = new Random();
        int count = 0;

        while (!bountyFound) {
            int randomIndex = generator.nextInt(targets.size());
            SuperNPlayer sntarget = targets.get(randomIndex);

            if (!bountyList.contains(sntarget) && sntarget.isSuper()) {
                bountyList.add(sntarget);
                SupernaturalsPlugin.instance.getServer().broadcastMessage(
                        Language.WITCHHUNTER_BOUNTY_ADD.toString().replace(
                                LanguageTag.PLAYER.toString(),
                                sntarget.getName()));
                bountyFound = true;
                return;
            }
            count++;
            if (count > 50) {
                return;
            }
        }
    }

    public static void updateBounties() {
        List<SuperNPlayer> snplayers = new ArrayList<SuperNPlayer>();

        if (bountyList.isEmpty()) {
            return;
        }

        for (SuperNPlayer snplayer : bountyList) {
            if (!snplayer.isSuper()) {
                snplayers.add(snplayer);
            }
        }

        for (SuperNPlayer snplayer : snplayers) {
            removeBounty(snplayer);
            addBounty();
        }
    }

    public static void createBounties() {
        List<SuperNPlayer> targets = SuperNManager.getSupernaturals();
        if (targets.size() == 0) {
            SupernaturalsPlugin.log(Level.WARNING,
                    "No targets found for WitchHunters!");
            return;
        }

        int numberFound = 0;
        Random generator = new Random();
        int count = 0;

        while (numberFound < SNConfigHandler.hunterMaxBounties) {
            int randomIndex = generator.nextInt(targets.size());
            SuperNPlayer sntarget = targets.get(randomIndex);
            if (!bountyList.contains(sntarget) && sntarget.isSuper()) {
                bountyList.add(sntarget);
                numberFound++;
                SupernaturalsPlugin.instance.getServer().broadcastMessage(
                        Language.WITCHHUNTER_BOUNTY_ADD.toString().replace(
                                LanguageTag.PLAYER.toString(),
                                sntarget.getName()));
            }
            count++;
            if (count > 100) {
                return;
            }
        }
    }

    // -------------------------------------------- //
    // Doors //
    // -------------------------------------------- //

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
        boolean open = false;

        final Location loc = block.getLocation();
        Location newLoc;
        Block newBlock;

        if (snplayer.isHuman()) {
            open = join(snplayer);
        }

        if (snplayer.isHunter() || snplayer.isHuman() && open) {
            if (door.isTopHalf()) {
                newLoc = new Location(loc.getWorld(), loc.getBlockX(),
                        loc.getBlockY() - 1, loc.getBlockZ());
                newBlock = newLoc.getBlock();
                block.setTypeIdAndData(71, (byte) (block.getData() + 4), false);
                newBlock.setTypeIdAndData(71, (byte) (newBlock.getData() + 4),
                        false);
            } else {
                newLoc = new Location(loc.getWorld(), loc.getBlockX(),
                        loc.getBlockY() + 1, loc.getBlockZ());
                newBlock = newLoc.getBlock();
                block.setTypeIdAndData(71, (byte) (block.getData() + 4), false);
                newBlock.setTypeIdAndData(71, (byte) (newBlock.getData() + 4),
                        false);
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
        SuperNManager.sendMessage(snplayer,
                Language.WITCHHUNTER_ONLY.toString());
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
            block.setTypeIdAndData(71, (byte) (block.getData() - 4), false);
            newBlock.setTypeIdAndData(71, (byte) (newBlock.getData() - 4),
                    false);
        } else {
            newLoc = new Location(loc.getWorld(), loc.getBlockX(),
                    loc.getBlockY() + 1, loc.getBlockZ());
            newBlock = newLoc.getBlock();
            block.setTypeIdAndData(71, (byte) (block.getData() - 4), false);
            newBlock.setTypeIdAndData(71, (byte) (newBlock.getData() - 4),
                    false);
        }

        removeDoorLocation(loc);
        removeDoorLocation(newLoc);
    }

    // -------------------------------------------- //
    // Join Event //
    // -------------------------------------------- //

    public void invite(final SuperNPlayer snplayer) {
        SupernaturalsPlugin.instance
                .getServer()
                .getScheduler()
                .scheduleSyncDelayedTask(SupernaturalsPlugin.instance,
                        new Runnable() {
                            @Override
                            public void run() {
                                SuperNManager.sendMessage(snplayer,
                                        Language.WITCHHUNTER_INVIT_GREETING
                                                .toString());
                                SuperNManager.sendMessage(snplayer,
                                        Language.WITCHHUNTER_INVIT_PROMPT
                                                .toString());
                                if (!playerInvites.contains(snplayer)) {
                                    playerInvites.add(snplayer);
                                }
                            }
                        }, 200);
    }

    public boolean join(SuperNPlayer snplayer) {
        if (playerInvites.contains(snplayer)) {
            SuperNManager.sendMessage(snplayer,
                    Language.WITCHHUNTER_INVIT_SUCCESS.toString());
            SuperNManager.convert(snplayer, "witchhunter",
                    SNConfigHandler.hunterPowerStart);
            return true;
        }
        return false;
    }

    // -------------------------------------------- //
    // Arrow Management //
    // -------------------------------------------- //

    public boolean changeArrowType(SuperNPlayer snplayer) {
        String currentType = hunterMap.get(snplayer);
        if (currentType == null) {
            currentType = "normal";
        }

        String nextType = "normal";

        for (int i = 0; i < SNConfigHandler.hunterArrowTypes.size(); i++) {
            if (SNConfigHandler.hunterArrowTypes.get(i).equalsIgnoreCase(
                    currentType)) {
                int newI = i + 1;
                if (newI >= SNConfigHandler.hunterArrowTypes.size()) {
                    newI = 0;
                }
                nextType = SNConfigHandler.hunterArrowTypes.get(newI);
                break;
            }
        }

        hunterMap.put(snplayer, nextType);
        SuperNManager.sendMessage(snplayer, Language.WITCHHUNTER_ARROW_CHANGE
                .toString().replace(LanguageTag.TYPE.toString(), nextType));
        return true;
    }

    public String getArrowType(Arrow arrow) {
        return arrowMap.get(arrow);
    }

    public HashMap<Arrow, String> getArrowMap() {
        return arrowMap;
    }

    public void removeArrow(final Arrow arrow) {
        SupernaturalsPlugin.instance
                .getServer()
                .getScheduler()
                .scheduleSyncDelayedTask(SupernaturalsPlugin.instance,
                        new Runnable() {
                            @Override
                            public void run() {
                                arrowMap.remove(arrow);
                            }
                        }, 20);
    }

    // -------------------------------------------- //
    // Attacks //
    // -------------------------------------------- //

    public boolean shoot(final Player player, final Arrow arrow) {

        final SuperNPlayer snplayer = SuperNManager.get(player);

        if (!player.getInventory().contains(Material.ARROW)) {
            return false;
        }

        if (!SupernaturalsPlugin.instance.getPvP(player)) {
            String arrowType = hunterMap.get(snplayer);
            if (arrowType == null) {
                hunterMap.put(snplayer, "normal");
                return false;
            }
            if (!arrowType.equalsIgnoreCase("normal")) {
                SuperNManager.sendMessage(snplayer,
                        Language.NOT_ALLOW_NONPVP.toString());
            }
            return false;
        }

        if (drainedPlayers.contains(player)) {
            player.getWorld().dropItem(player.getLocation(),
                    new ItemStack(Material.ARROW, 1));
            SuperNManager.sendMessage(snplayer,
                    Language.WITCHHUNTER_POWERARROW_COOLDOWN.toString());
            return true;
        }

        String arrowType = hunterMap.get(snplayer);
        if (arrowType == null) {
            arrowType = "normal";
        }

        if (arrowType.equalsIgnoreCase("fire")) {
            if (snplayer.getPower() > SNConfigHandler.hunterPowerArrowFire) {
                SuperNManager.alterPower(snplayer,
                        -SNConfigHandler.hunterPowerArrowFire,
                        Language.WITCHHUNTER_FIREARROW_NOTICE_SELF.toString());
                arrowMap.put(arrow, arrowType);
                arrow.setFireTicks(SNConfigHandler.hunterFireArrowFireTicks);
                return false;
            } else {
                SuperNManager.sendMessage(snplayer,
                        Language.NO_POWER.toString());
                SuperNManager.sendMessage(snplayer,
                        Language.WITCHHUNTER_ARROW_SWITCH_NORMAL.toString());
                hunterMap.put(snplayer, "normal");
                return false;
            }
        } else if (arrowType.equalsIgnoreCase("triple")) {
            if (snplayer.getPower() > SNConfigHandler.hunterPowerArrowTriple) {
                SuperNManager
                        .alterPower(snplayer,
                                -SNConfigHandler.hunterPowerArrowTriple,
                                Language.WITCHHUNTER_TRIPLEARROW_NOTICE_SELF
                                        .toString());
                arrowMap.put(arrow, arrowType);
                SupernaturalsPlugin.instance
                        .getServer()
                        .getScheduler()
                        .scheduleSyncDelayedTask(SupernaturalsPlugin.instance,
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        splitArrow(player, arrow);
                                    }
                                }, 4);
                return false;
            } else {
                SuperNManager.sendMessage(snplayer,
                        Language.NO_POWER.toString());
                SuperNManager.sendMessage(snplayer,
                        Language.WITCHHUNTER_ARROW_SWITCH_NORMAL.toString());
                hunterMap.put(snplayer, "normal");
                return false;
            }
        } else if (arrowType.equalsIgnoreCase("power")) {
            if (snplayer.getPower() > SNConfigHandler.hunterPowerArrowPower) {
                SuperNManager.alterPower(snplayer,
                        -SNConfigHandler.hunterPowerArrowPower,
                        Language.WITCHHUNTER_POWERARROW_NOTICE_SELF.toString());
                arrowMap.put(arrow, arrowType);
                drainedPlayers.add(player);
                SupernaturalsPlugin.instance
                        .getServer()
                        .getScheduler()
                        .scheduleSyncDelayedTask(SupernaturalsPlugin.instance,
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        drainedPlayers.remove(player);
                                        if (player.isOnline()) {
                                            SuperNManager
                                                    .sendMessage(
                                                            snplayer,
                                                            Language.WITCHHUNTER_POWERARROW_READY
                                                                    .toString());
                                        }
                                        SupernaturalsPlugin.log(snplayer
                                                .getName()
                                                + " is no longer drained.");
                                    }
                                }, SNConfigHandler.hunterCooldown / 50);
                return false;
            } else {
                SuperNManager.sendMessage(snplayer,
                        Language.NO_POWER.toString());
                SuperNManager.sendMessage(snplayer,
                        Language.WITCHHUNTER_ARROW_SWITCH_NORMAL.toString());
                hunterMap.put(snplayer, "normal");
                return false;
            }
        } else if (arrowType.equalsIgnoreCase("grapple")) {
            if (snplayer.getPower() > SNConfigHandler.hunterPowerArrowGrapple) {
                SuperNManager.alterPower(snplayer,
                        -SNConfigHandler.hunterPowerArrowGrapple,
                        Language.WITCHHUNTER_GRAPPLEARROW_NOTICE_SELF
                                .toString());
                arrowMap.put(arrow, arrowType);
                return false;
            } else {
                SuperNManager.sendMessage(snplayer,
                        Language.NO_POWER.toString());
                SuperNManager.sendMessage(snplayer,
                        Language.WITCHHUNTER_ARROW_SWITCH_NORMAL.toString());
                hunterMap.put(snplayer, "normal");
                return false;
            }
        } else {
            return false;
        }
    }

    public void splitArrow(final Player player, final Arrow arrow) {
        player.launchProjectile(Arrow.class).setVelocity(arrow.getVelocity());
        String arrowType = arrowMap.get(arrow);
        if (arrowType.equals("triple")) {
            arrowMap.put(arrow, "double");
            SupernaturalsPlugin.instance
                    .getServer()
                    .getScheduler()
                    .scheduleSyncDelayedTask(SupernaturalsPlugin.instance,
                            new Runnable() {
                                @Override
                                public void run() {
                                    splitArrow(player, arrow);
                                }
                            }, 4);
        } else {
            arrowMap.remove(arrow);
        }
    }

    /*
     * Grappling broke. Will be fixed at a later time. public boolean
     * isGrappling(Player player) { return grapplingPlayers.contains(player); }
     *
     * public void startGrappling(Player player, Location targetLocation) { if
     * (isGrappling(player)) { return; } ArrowUtil gh = new ArrowUtil(player,
     * targetLocation); grapplingPlayers.add(player);
     * SupernaturalsPlugin.instance
     * .getServer().getScheduler().scheduleSyncDelayedTask
     * (SupernaturalsPlugin.instance, gh); }
     *
     * public void stopGrappling(final Player player) { if (isGrappling(player))
     * { Vector v = new Vector(0, 0, 0); player.setVelocity(v);
     * SupernaturalsPlugin
     * .instance.getServer().getScheduler().scheduleSyncDelayedTask
     * (SupernaturalsPlugin.instance, new Runnable() {
     *
     * @Override public void run() { grapplingPlayers.remove(player); } }, 20 *
     * 2); } }
     */
}
