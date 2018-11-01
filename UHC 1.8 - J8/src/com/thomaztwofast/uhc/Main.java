/*
 * Ultra Hardcore 1.8, a Minecraft survival game mode.
 * Copyright (C) <2018> Thomaz2Fast
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

import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.thomaztwofast.uhc.commands.CmdAutoTeam;
import com.thomaztwofast.uhc.commands.CmdChunkLoader;
import com.thomaztwofast.uhc.commands.CmdSelectTeam;
import com.thomaztwofast.uhc.commands.CmdStart;
import com.thomaztwofast.uhc.commands.CmdUhc;
import com.thomaztwofast.uhc.data.Config;
import com.thomaztwofast.uhc.data.Status;
import com.thomaztwofast.uhc.data.UHCPlayer;

public class Main extends JavaPlugin {
	public final HashMap<String, UHCPlayer> PLAYERS = new HashMap<>();
	public final String NMS_VER = getNMSVersion();
	public Status status = Status.DISABLED;
	public Config config = new Config(this);
	public Update update = new Update(this);
	public GameManager gameManager = new GameManager(this);
	public String lastError = "";

	@Override
	public void onDisable() {
		gameManager.unLoad();
	}

	@Override
	public void onLoad() {
		config.load();
		update.checkForUpdate();
	}

	@Override
	public void onEnable() {
		getCommand("autoteam").setExecutor(new CmdAutoTeam(this));
		getCommand("chunkloader").setExecutor(new CmdChunkLoader(this));
		getCommand("selectteam").setExecutor(new CmdSelectTeam(this));
		getCommand("start").setExecutor(new CmdStart(this));
		getCommand("uhc").setExecutor(new CmdUhc(this));
		getServer().getOnlinePlayers().forEach(e -> registerPlayer(e));
		gameManager.load();
	}

	// ---------------------------------------------------------------------------

	public void log(int lvl, String log) {
		log = "[" + getName() + "] " + log;
		switch (lvl) {
		case 1:
			lastError = log.substring(log.indexOf(' ') + 1);
		case 2:
			System.out.println("\u001B[3" + (lvl == 1 ? "1" : "5") + "m" + log + "\u001B[0m");
			return;
		default:
			System.out.println(log);
			return;
		}
	}

	public UHCPlayer registerPlayer(Player p) {
		PLAYERS.put(p.getName(), new UHCPlayer(this, p));
		return PLAYERS.get(p.getName());
	}

	public UHCPlayer getRegisterPlayer(String name) {
		return PLAYERS.get(name);
	}

	public void unRegisterPlayer(Player p) {
		PLAYERS.remove(p.getName());
	}

	public void updateWorldGamerules(World w) {
		w.setGameRule(GameRule.DISABLE_ELYTRA_MOVEMENT_CHECK, config.grDisabledElytra);
		w.setGameRule(GameRule.DO_ENTITY_DROPS, config.grEntityDrops);
		w.setGameRule(GameRule.DO_FIRE_TICK, config.grFireTick);
		w.setGameRule(GameRule.DO_LIMITED_CRAFTING, config.grLimitedCrafting);
		w.setGameRule(GameRule.DO_MOB_LOOT, config.grMobLoot);
		w.setGameRule(GameRule.DO_MOB_SPAWNING, config.grMobSpawning);
		w.setGameRule(GameRule.DO_TILE_DROPS, config.grTileDrops);
		w.setGameRule(GameRule.DO_WEATHER_CYCLE, config.grWeather);
		w.setGameRule(GameRule.MAX_ENTITY_CRAMMING, config.grMaxCramming);
		w.setGameRule(GameRule.MOB_GRIEFING, config.grMobGriefing);
		w.setGameRule(GameRule.RANDOM_TICK_SPEED, config.grRandom);
		w.setGameRule(GameRule.REDUCED_DEBUG_INFO, config.grDebugInfo);
		w.setGameRule(GameRule.SPAWN_RADIUS, config.grSpawnRadius);
		w.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, config.grSpectators);
	}

	// ---------------------------------------------------------------------------

	private String getNMSVersion() {
		String a = getServer().getClass().getPackage().getName();
		return a.substring(a.lastIndexOf('.') + 1);
	}
}
