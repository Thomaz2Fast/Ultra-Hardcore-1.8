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

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.thomaztwofast.uhc.commands.CmdAutoTeam;
import com.thomaztwofast.uhc.commands.CmdChunkLoader;
import com.thomaztwofast.uhc.commands.CmdSelectTeam;
import com.thomaztwofast.uhc.commands.CmdStart;
import com.thomaztwofast.uhc.commands.CmdUhc;
import com.thomaztwofast.uhc.custom.PluginUpdateChecker;
import com.thomaztwofast.uhc.data.Config;
import com.thomaztwofast.uhc.data.GameStatus;
import com.thomaztwofast.uhc.data.PlayerList;
import com.thomaztwofast.uhc.events.EvDisabled;

public class Main extends JavaPlugin {
	public GameStatus mA = GameStatus.DISABLED;
	public PlayerList mB = new PlayerList();
	public Config mC = new Config(this);
	public PluginUpdateChecker mD = new PluginUpdateChecker(this);
	public GameManager mE = new GameManager(this);

	@Override
	public void onDisable() {
		if (mC.cCa) {
			mE.unloadGame();
		}
	}

	@Override
	public void onLoad() {
		mC.loadConfig();
		if (mC.cCb) {
			mD.updateCheck();
		}
	}

	@Override
	public void onEnable() {
		getCommand("autoteam").setExecutor(new CmdAutoTeam(this));
		getCommand("chunkloader").setExecutor(new CmdChunkLoader(this));
		getCommand("selectteam").setExecutor(new CmdSelectTeam(this));
		getCommand("start").setExecutor(new CmdStart(this));
		getCommand("uhc").setExecutor(new CmdUhc(this));
		getOnlinePlayers();
		if (mC.cCa) {
			mE.loadGame();
			return;
		}
		getServer().getPluginManager().registerEvents(new EvDisabled(this), this);
	}

	// ------:- PUBLIC -:---------------------------------------------------------------------------

	public void log(int a, String b) {
		String c = "[" + getDescription().getName() + "] ";
		switch (a) {
		case 1:
			System.err.println("\u001B[31m" + c + b + "\u001B[0m");
			return;
		case 2:
			System.out.println("\u001B[35m" + c + "[DEBUG] " + b + "\u001B[0m");
			return;
		default:
			System.out.println(c + b);
			return;
		}
	}

	// ------:- PRIVATE -:--------------------------------------------------------------------------

	private void getOnlinePlayers() {
		for (Player p : getServer().getOnlinePlayers()) {
			mB.addPlayer(this, p);
		}
	}
}
