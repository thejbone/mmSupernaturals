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

package com.mmiillkkaa.supernaturals;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.mmiillkkaa.supernaturals.commands.SNCommand;
import com.mmiillkkaa.supernaturals.commands.SNCommandAdmin;
import com.mmiillkkaa.supernaturals.commands.SNCommandClasses;
import com.mmiillkkaa.supernaturals.commands.SNCommandConvert;
import com.mmiillkkaa.supernaturals.commands.SNCommandCure;
import com.mmiillkkaa.supernaturals.commands.SNCommandHelp;
import com.mmiillkkaa.supernaturals.commands.SNCommandJoin;
import com.mmiillkkaa.supernaturals.commands.SNCommandKillList;
import com.mmiillkkaa.supernaturals.commands.SNCommandList;
import com.mmiillkkaa.supernaturals.commands.SNCommandPower;
import com.mmiillkkaa.supernaturals.commands.SNCommandReload;
import com.mmiillkkaa.supernaturals.commands.SNCommandReset;
import com.mmiillkkaa.supernaturals.commands.SNCommandRestartTask;
import com.mmiillkkaa.supernaturals.commands.SNCommandRmTarget;
import com.mmiillkkaa.supernaturals.commands.SNCommandSave;
import com.mmiillkkaa.supernaturals.commands.SNCommandSetBanish;
import com.mmiillkkaa.supernaturals.commands.SNCommandSetChurch;
import com.mmiillkkaa.supernaturals.commands.SNCommandSetup;
import com.mmiillkkaa.supernaturals.io.SNConfigHandler;
import com.mmiillkkaa.supernaturals.io.SNDataHandler;
import com.mmiillkkaa.supernaturals.io.SNPlayerHandler;
import com.mmiillkkaa.supernaturals.io.SNWhitelistHandler;
import com.mmiillkkaa.supernaturals.listeners.SNBlockListener;
import com.mmiillkkaa.supernaturals.listeners.SNEntityListener;
import com.mmiillkkaa.supernaturals.listeners.SNEntityMonitor;
import com.mmiillkkaa.supernaturals.listeners.SNPlayerListener;
import com.mmiillkkaa.supernaturals.listeners.SNPlayerMonitor;
import com.mmiillkkaa.supernaturals.listeners.SNServerMonitor;
import com.mmiillkkaa.supernaturals.manager.AngelManager;
import com.mmiillkkaa.supernaturals.manager.ClassManager;
import com.mmiillkkaa.supernaturals.manager.DemonManager;
import com.mmiillkkaa.supernaturals.manager.GhoulManager;
import com.mmiillkkaa.supernaturals.manager.HumanManager;
import com.mmiillkkaa.supernaturals.manager.HunterManager;
import com.mmiillkkaa.supernaturals.manager.PriestManager;
import com.mmiillkkaa.supernaturals.manager.SuperNManager;
import com.mmiillkkaa.supernaturals.manager.VampireManager;
import com.mmiillkkaa.supernaturals.manager.WereManager;
import com.mmiillkkaa.supernaturals.util.TextUtil;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class SupernaturalsPlugin extends JavaPlugin {
	public static SupernaturalsPlugin instance;

	private final SNConfigHandler snConfig = new SNConfigHandler(this);
	private SNDataHandler snData = new SNDataHandler();
	private SNWhitelistHandler snWhitelist = new SNWhitelistHandler(this);

	private SuperNManager superManager = new SuperNManager(this);
	private HumanManager humanManager = new HumanManager(this);
	private VampireManager vampManager = new VampireManager();
	private PriestManager priestManager = new PriestManager(this);
	private WereManager wereManager = new WereManager();
	private GhoulManager ghoulManager = new GhoulManager();
	private HunterManager hunterManager = new HunterManager(this);
	private DemonManager demonManager = new DemonManager(this);
	private AngelManager angelManager = new AngelManager();

	public List<SNCommand> commands = new ArrayList<SNCommand>();

	private static File dataFolder;

	public static boolean foundPerms = false;

	public PluginManager pm;

	public SNDataHandler getDataHandler() {
		return snData;
	}

	// -------------------------------------------- //
	// Managers //
	// -------------------------------------------- //

	public SuperNManager getSuperManager() {
		return superManager;
	}

	public SNConfigHandler getConfigManager() {
		return snConfig;
	}

	public SNWhitelistHandler getWhitelistHandler() {
		return snWhitelist;
	}

	public VampireManager getVampireManager() {
		return vampManager;
	}

	public AngelManager getAngelManager() {
		return angelManager;
	}

	public PriestManager getPriestManager() {
		return priestManager;
	}

	public WereManager getWereManager() {
		return wereManager;
	}

	public GhoulManager getGhoulManager() {
		return ghoulManager;
	}

	public HunterManager getHunterManager() {
		return hunterManager;
	}

	public DemonManager getDemonManager() {
		return demonManager;
	}

	public ClassManager getClassManager(Player player) {
		SuperNPlayer snplayer = SuperNManager.get(player);
		if (snplayer.getType().equalsIgnoreCase("demon")) {
			return demonManager;
		} else if (snplayer.getType().equalsIgnoreCase("ghoul")) {
			return ghoulManager;
		} else if (snplayer.getType().equalsIgnoreCase("witchhunter")) {
			return hunterManager;
		} else if (snplayer.getType().equalsIgnoreCase("priest")) {
			return priestManager;
		} else if (snplayer.getType().equalsIgnoreCase("vampire")) {
			return vampManager;
		} else if (snplayer.getType().equalsIgnoreCase("werewolf")) {
			return wereManager;
		} else if (snplayer.getType().equalsIgnoreCase("angel")) {
			return angelManager;
		} else {
			return humanManager;
		}
	}

	// -------------------------------------------- //
	// Plugin Enable/Disable //
	// -------------------------------------------- //

	@Override
	public void onDisable() {
		SuperNManager.cancelTimer();
		snData.write();

		saveData();
		demonManager.removeAllWebs();
		PluginDescriptionFile pdfFile = getDescription();
		log(pdfFile.getName() + " version " + pdfFile.getVersion()
				+ " disabled.");

	}

	@Override
	public void onEnable() {

		SupernaturalsPlugin.instance = this;
		getDataFolder().mkdir();
		pm = getServer().getPluginManager();

		// Add the commands
		commands.add(new SNCommandHelp());
		commands.add(new SNCommandAdmin());
		commands.add(new SNCommandPower());
		commands.add(new SNCommandReload());
		commands.add(new SNCommandSave());
		commands.add(new SNCommandConvert());
		commands.add(new SNCommandCure());
		commands.add(new SNCommandList());
		commands.add(new SNCommandClasses());
		commands.add(new SNCommandSetChurch());
		commands.add(new SNCommandSetBanish());
		commands.add(new SNCommandReset());
		commands.add(new SNCommandKillList());
		commands.add(new SNCommandRmTarget());
		commands.add(new SNCommandRestartTask());
		commands.add(new SNCommandJoin());
		commands.add(new SNCommandSetup());

		pm.registerEvents(new SNBlockListener(this), this);
		pm.registerEvents(new SNEntityListener(this), this);
		pm.registerEvents(new SNEntityMonitor(this), this);
		pm.registerEvents(new SNPlayerListener(this), this);
		pm.registerEvents(new SNPlayerMonitor(this), this);
		pm.registerEvents(new SNServerMonitor(this), this);

		PluginDescriptionFile pdfFile = getDescription();
		log(pdfFile.getName() + " version " + pdfFile.getVersion()
				+ " enabled.");

		dataFolder = getDataFolder();
		SNConfigHandler.getConfiguration();

		loadData();
		snData = SNDataHandler.read();

		SNWhitelistHandler.reloadWhitelist();

		if (snData == null) {
			snData = new SNDataHandler();
		}

		SuperNManager.startTimer();
		HunterManager.createBounties();
		setupPermissions();
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
			log("Couldn't start Metrics.");
		}
	}

	// -------------------------------------------- //
	// Chat Commands //
	// -------------------------------------------- //

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender instanceof Player) {
			List<String> parameters = new ArrayList<String>(Arrays.asList(args));
			handleCommand(sender, parameters, true);
			return true;
		} else {
			List<String> parameters = new ArrayList<String>(Arrays.asList(args));
			handleCommand(sender, parameters, false);
			return true;
		}
	}

	public void handleCommand(CommandSender sender, List<String> parameters, boolean isPlayer) {
		if (parameters.size() == 0) {
			for (SNCommand vampcommand : commands) {
				if (vampcommand.getName().equalsIgnoreCase("help")) {
					vampcommand.execute(sender, parameters);
					return;
				}
			}
			sender.sendMessage(ChatColor.RED + "未知的指令. 請輸入 /sn help");
			return;
		}

		String command = parameters.get(0).toLowerCase();
		parameters.remove(0);

		for (SNCommand vampcommand : commands) {
			if (command.equals(vampcommand.getName())) {
				if (!isPlayer && vampcommand.senderMustBePlayer) {
					sender.sendMessage("這個指令, sn " + command
							+ ", 只能在遊戲內使用");
				}
				vampcommand.execute(sender, parameters);
				return;
			}
		}

		sender.sendMessage(ChatColor.RED + "未知的指令 \"" + command
				+ "\". 請輸入 /sn help");
	}

	// -------------------------------------------- //
	// Data Management //
	// -------------------------------------------- //

	public static void saveAll() {
		File file = new File(dataFolder, "data.yml");
		SNPlayerHandler.save(SuperNManager.getSupernaturals(), file);

		SNConfigHandler.saveConfig();
	}

	public static void saveData() {
		File file = new File(dataFolder, "data.yml");
		SNPlayerHandler.save(SuperNManager.getSupernaturals(), file);
	}

	public static void loadData() {
		File file = new File(dataFolder, "data.yml");
		SuperNManager.setSupernaturals(SNPlayerHandler.load(file));
	}

	public static void reConfig() {
		SNConfigHandler.reloadConfig();
	}

	public static void reloadData() {
		File file = new File(dataFolder, "data.yml");
		SuperNManager.setSupernaturals(SNPlayerHandler.load(file));
	}

	public static void restartTask() {
		SuperNManager.cancelTimer();
		SuperNManager.startTimer();
	}

	// -------------------------------------------- //
	// Permissions //
	// -------------------------------------------- //

	private void setupPermissions() {
		if (pm.isPluginEnabled("PermissionsEx")) {
			log("Found PermissionsEx!");
			foundPerms = true;
		} else if (pm.isPluginEnabled("PermissionsBukkit")) {
			log("Found PermissionsBukkit!");
			foundPerms = true;
		} else if (pm.isPluginEnabled("bPermissions")) {
			log("Found bPermissions.");
			log(Level.WARNING, "If something goes wrong with bPermissions and this plugin, I will not help!");
			foundPerms = true;
		} else if (pm.isPluginEnabled("GroupManager")) {
			log("Found GroupManager.");
			foundPerms = true;
		}

		if (!foundPerms) {
			log("Permission system not detected, defaulting to SuperPerms");
			log("A permissions system may be detected later, just wait.");
		}
	}

	public static boolean hasPermissions(Player player, String permissions) {
		return player.hasPermission(permissions);
	}

	public static boolean hasPermissions(SuperNPlayer player, String permissions) {
		return instance.getServer().getPlayer(player.getName()).hasPermission(permissions);
	}

	private WorldGuardPlugin getWorldGuard() {
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

		// WorldGuard may not be loaded
		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			return null; // Maybe you want throw an exception instead
		}

		return (WorldGuardPlugin) plugin;
	}

	public boolean getPvP(Player player) {
		WorldGuardPlugin worldGuard = SupernaturalsPlugin.instance.getWorldGuard();
		if (worldGuard == null) {
			return true;
		}
		Vector pt = toVector(player.getLocation());
		RegionManager regionManager = worldGuard.getRegionManager(player.getWorld());
		ApplicableRegionSet set = regionManager.getApplicableRegions(pt);
		return set.allows(DefaultFlag.PVP);
	}

	public boolean getSpawn(Player player) {
		WorldGuardPlugin worldGuard = SupernaturalsPlugin.instance.getWorldGuard();
		if (worldGuard == null) {
			return true;
		}
		Vector pt = toVector(player.getLocation());
		RegionManager regionManager = worldGuard.getRegionManager(player.getWorld());
		ApplicableRegionSet set = regionManager.getApplicableRegions(pt);
		return set.allows(DefaultFlag.MOB_SPAWNING);
	}

	// -------------------------------------------- //
	// Logging //
	// -------------------------------------------- //

	public static void log(String msg) {
		log(Level.INFO, msg);
	}

	public static void log(Level level, String msg) {
		Logger.getLogger("Minecraft").log(level, "["
				+ instance.getDescription().getFullName() + "] " + msg);
	}

}
