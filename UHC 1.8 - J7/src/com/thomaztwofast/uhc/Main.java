/*
 * Ultra Hardcore 1.8, a Minecraft survival game mode.
 * Copyright (C) <2015> Thomaz2Fast
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.thomaztwofast.uhc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.thomaztwofast.uhc.commands.CommandAutoTeam;
import com.thomaztwofast.uhc.commands.CommandChunkloader;
import com.thomaztwofast.uhc.commands.CommandSelectTeam;
import com.thomaztwofast.uhc.commands.CommandStart;
import com.thomaztwofast.uhc.commands.CommandUhc;
import com.thomaztwofast.uhc.events.UhcDisabledEvent;
import com.thomaztwofast.uhc.events.UhcEvents;
import com.thomaztwofast.uhc.events.UhcServerEvents;

public class Main extends JavaPlugin {
	public EnumGame gmStat = EnumGame.DISABLED;
	public boolean plMode = false;
	public boolean sm = false;
	public boolean clShd = false;
	public boolean uhcBook = false;
	public boolean uhcBookConf = false;
	public boolean tmMode = false;
	public boolean tmrFF = false;
	public boolean tmrSFI = false;
	public boolean tmsST = false;
	public boolean dmgLog = false;
	public boolean fzp = true;
	public boolean tlhf = false;
	public HashMap<String, HashMap<ItemStack, Integer>> itemStore = new HashMap<String, HashMap<ItemStack, Integer>>();
	public HashMap<UUID, Player> igPs = new HashMap<UUID, Player>();
	public HashMap<UUID, Location> igPsl = new HashMap<UUID, Location>();
	public HashMap<String, Location> igTms = new HashMap<String, Location>();
	public HashMap<UUID, Integer> igOffPs = new HashMap<UUID, Integer>();
	public HashMap<String, Inventory> invStore = new HashMap<String, Inventory>();
	public ArrayList<UUID> offPs = new ArrayList<UUID>();
	public ArrayList<String> ubLord = new ArrayList<String>();
	public ArrayList<String> ubPages = new ArrayList<String>();
	public ArrayList<String> grList = new ArrayList<String>();
	public long gameTimer = 0;
	public int clTick = 8;
	public int clTask = 20;
	public int clBorder = 500;
	public int ubInvSlot = 7;
	public int woArenaSize = 1000;
	public int woSunTime = 0;
	public int woDiff = 2;
	public int wbStartPos = 2500;
	public int wbEndPos = 300;
	public int wbTime = 10800;
	public int fzSize = 10;
	public int moMarkTime = 20;
	public int tmMaxPlayer = 3;
	public int tmSelTmInvSlot = 1;
	public int okMaxTime = 5;
	public int sysTimeStart;
	public int sysTimeGame;
	public int smMps = 8;
	public int smMts = 4;
	public int smCn = 30;
	public int smSID = 1;
	public String tlh = "§cUltra Hardcore";
	public String tlf = "";
	public String ubName = "§eUltra Hardcore";
	public String moMarkMeg = "§cUHC&8 |§r YOU ARE NOW IN §a$[TIME]§r MIN IN";
	public String okMsg = "$[P] was killed by Offline Timer";
	public String chatD = "<$[P]> $[M]";
	public String chatTD = "<§o$[P]§r> $[M]";
	public String chatTT = "<$[P]> $[M]";
	public String chatTPC = "§7§lTEAM§r <$[P]> §5$[M]";
	public String chatS = "<§7§o$[P]§r> $[M]";
	public String smMotd = "$[S]|$[OP]|$[IP]";
	private PluginManager pm;

	/**
	 * Trigger when server is about to reload or shutdown.<br>
	 * If plugin mode is enabled: Remove ultra hardcore settings and remove tablist header &
	 * footer.
	 */
	@Override
	public void onDisable() {
		if (tlhf) {
			Function.updateTabListHeaderFooterAllPlayers(getServer().getOnlinePlayers(), "", "");
		}
		if (plMode) {
			if (sm) {
				ServerMode.onDisable();
			} else {
				Function.uhcRemove(this);
			}
		}
	}

	/**
	 * Trigger when server loading this plugin.<br>
	 * Save the default config if the file exist and load the config to memory.
	 */
	@Override
	public void onLoad() {
		saveDefaultConfig();
		Function.loadConfig(this, this.getConfig());
		this.pm = this.getServer().getPluginManager();
	}

	/**
	 * Trigger when server is enabled this plugin.<br>
	 * Command | Tablist Header & Footer | Events | Private/Friend Mode | Server Mode
	 */
	@Override
	public void onEnable() {
		getCommand("autoteam").setExecutor(new CommandAutoTeam(this));
		getCommand("chunkloader").setExecutor(new CommandChunkloader(this));
		getCommand("chunkloader").setTabCompleter(new CommandChunkloader(this));
		getCommand("selectteam").setExecutor(new CommandSelectTeam(this));
		getCommand("start").setExecutor(new CommandStart(this));
		getCommand("start").setTabCompleter(new CommandStart(this));
		getCommand("uhc").setExecutor(new CommandUhc(this));
		getCommand("uhc").setTabCompleter(new CommandUhc(this));
		if (tlhf) {
			Function.updateTabListHeaderFooterAllPlayers(getServer().getOnlinePlayers(), tlh, tlf);
		}
		if (plMode) {
			if (sm) {
				gmStat = EnumGame.LOADING;
				pm.registerEvents(new UhcServerEvents(this), this);
				ServerMode.onLoad(this);
			} else {
				pm.registerEvents(new UhcEvents(this), this);
				Function.uhcSetup(this);
			}
		} else {
			pm.registerEvents(new UhcDisabledEvent(this), this);
		}
	}
}