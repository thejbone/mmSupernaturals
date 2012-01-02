package me.matterz.supernaturals.listeners;

import java.util.logging.Level;

import me.matterz.supernaturals.SupernaturalsPlugin;

import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

import com.nijikokun.bukkit.Permissions.Permissions;

import ru.tehkode.permissions.bukkit.PermissionsEx;

public class SNServerMonitor extends ServerListener {

	public SupernaturalsPlugin plugin;

	public SNServerMonitor(SupernaturalsPlugin instance) {
		this.plugin = instance;
	}

	@Override
	public void onPluginEnable(PluginEnableEvent event) {
		if(SupernaturalsPlugin.foundPerms) {
			return;
		}
		Plugin enabledPlugin = event.getPlugin();
		if(enabledPlugin.toString().startsWith("Permissions") && !enabledPlugin.toString().startsWith("PermissionsEx") && !enabledPlugin.toString().startsWith("PermissionsBukkit")) {
			SupernaturalsPlugin.permissionsPlugin = enabledPlugin;
			SupernaturalsPlugin.permissionHandler = ((Permissions) SupernaturalsPlugin.permissionsPlugin).getHandler();
			SupernaturalsPlugin.log("Found and will use plugin "+ SupernaturalsPlugin.permissionsPlugin.getDescription().getFullName());
			SupernaturalsPlugin.foundPerms = true;
		} else if(enabledPlugin.toString().startsWith("PermissionsEx")) {
			SupernaturalsPlugin.permissionsPlugin = enabledPlugin;
			SupernaturalsPlugin.permissionExManager = PermissionsEx.getPermissionManager();
			SupernaturalsPlugin.log("Found and will use plugin "+ SupernaturalsPlugin.permissionsPlugin.getDescription().getFullName());
			SupernaturalsPlugin.foundPerms = true;
		} else if(enabledPlugin.toString().startsWith("GroupManager")) {
			SupernaturalsPlugin.log("Found GroupManager");
			SupernaturalsPlugin.permissionsPlugin = enabledPlugin;
			SupernaturalsPlugin.permissionHandler = ((Permissions) SupernaturalsPlugin.permissionsPlugin).getHandler();
			SupernaturalsPlugin.foundPerms = true;
		} else if(enabledPlugin.toString().startsWith("EssentialsGroupManager")) {
			SupernaturalsPlugin.log("Found EssentialsGroupManager");
			SupernaturalsPlugin.permissionsPlugin = enabledPlugin;
			SupernaturalsPlugin.permissionHandler = ((Permissions) SupernaturalsPlugin.permissionsPlugin).getHandler();
			SupernaturalsPlugin.foundPerms = true;
		} else if(enabledPlugin.toString().startsWith("bPermissions")) {
			SupernaturalsPlugin.log("Found bPermissions.");
			SupernaturalsPlugin.log(Level.WARNING, "If something goes wrong with bPermissions and this plugin, I will not help!");
			SupernaturalsPlugin.bukkitperms = true;
			SupernaturalsPlugin.foundPerms = true;
		} else if(enabledPlugin.toString().startsWith("PermissionsBukkit")) {
			SupernaturalsPlugin.log("Found PermissionsBukkit!");
			SupernaturalsPlugin.bukkitperms = true;
			SupernaturalsPlugin.foundPerms = true;
		}
	}

}
