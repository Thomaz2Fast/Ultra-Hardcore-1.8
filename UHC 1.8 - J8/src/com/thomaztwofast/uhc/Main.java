/*
 * Ultra Hardcore 1.8, a Minecraft survival game mode.
 * Copyright (C) <2016> Thomaz2Fast
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

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.thomaztwofast.uhc.commands.CmdAutoTeam;
import com.thomaztwofast.uhc.commands.CmdChunkLoader;
import com.thomaztwofast.uhc.commands.CmdSelectTeam;
import com.thomaztwofast.uhc.commands.CmdStart;
import com.thomaztwofast.uhc.commands.CmdUhc;
import com.thomaztwofast.uhc.custom.Logger;
import com.thomaztwofast.uhc.custom.UpdateNotification;
import com.thomaztwofast.uhc.data.Config;
import com.thomaztwofast.uhc.data.PlayerData;
import com.thomaztwofast.uhc.events.EvDisabled;

public class Main extends JavaPlugin {
	private HashMap<UUID, PlayerData> pd = new HashMap<>();
	private HashMap<String, UUID> ppd = new HashMap<>();
	private Logger l = new Logger(this);
	private Function f = new Function();
	private UpdateNotification un;
	private Config c;
	private GameManager gm;

	@Override
	public void onDisable() {
		if (c.pl_Enabled()) {
			gm.unLoadUHC();
		}
		f.getAllOnlinePlayers(this, false);
	}

	@Override
	public void onLoad() {
		c = new Config(this);
		gm = new GameManager(this);
		if (c.pl_updateNotification()) {
			un = new UpdateNotification(this);
		}
		f.getAllOnlinePlayers(this, true);
	}

	@Override
	public void onEnable() {
		getCommand("autoteam").setExecutor(new CmdAutoTeam(this));
		getCommand("chunkloader").setExecutor(new CmdChunkLoader(this));
		getCommand("selectteam").setExecutor(new CmdSelectTeam(this));
		getCommand("start").setExecutor(new CmdStart(this));
		getCommand("uhc").setExecutor(new CmdUhc(this));
		if (c.pl_Enabled()) {
			gm.loadUHC();
			return;
		}
		getServer().getPluginManager().registerEvents(new EvDisabled(this), this);
	}

	// :: PUBLIC :: //

	/**
	 * Get Logger
	 */
	public Logger getPlLog() {
		return l;
	}

	/**
	 * Get Function.
	 */
	public Function getPlFun() {
		return f;
	}

	/**
	 * Get Config
	 */
	public Config getPlConf() {
		return c;
	}

	/**
	 * Get Update Notification.
	 */
	public UpdateNotification getPlUpdate() {
		return un;
	}

	/**
	 * Get Game Manager.
	 */
	public GameManager getGame() {
		return gm;
	}

	/**
	 * Get registered player data.
	 */
	public HashMap<UUID, PlayerData> getRegPlayerData() {
		return pd;
	}

	/**
	 * Get registered player by UUID.
	 */
	public PlayerData getRegPlayer(UUID u) {
		return pd.get(u);
	}

	/**
	 * Get registered player by name.
	 */
	public PlayerData getRegPlayerByName(String s) {
		return pd.get(ppd.get(s));
	}

	/**
	 * Add Registered player.
	 */
	public void regPlayer(Player p) {
		ppd.put(p.getName(), p.getUniqueId());
		pd.put(p.getUniqueId(), new PlayerData(this, p));
	}

	/**
	 * Remove registered player.
	 */
	public void removeRegPlayer(Player p) {
		pd.get(p.getUniqueId()).saveProfile(true);
		pd.remove(p.getUniqueId());
		ppd.remove(p.getName());
	}
}