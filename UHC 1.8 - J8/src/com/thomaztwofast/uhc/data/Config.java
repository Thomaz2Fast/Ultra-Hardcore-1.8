/*
 * Ultra Hardcore 1.8, a Minecraft survival game mode.
 * Copyright (C) <2019> Thomaz2Fast
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

package com.thomaztwofast.uhc.data;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

import com.thomaztwofast.uhc.Main;
import com.thomaztwofast.uhc.lib.F;

public class Config {
	private final String[] PATHS = { "Plugin", "Chunkloader", "Gamerules", "ServerMode", "ServerMode.AdvencedMotdStatus", "ServerMode.KickMessage", "ServerMode.BungeeCordSupport", "Game", "Game.Options", "Tablist", "WorldSettings", "WorldBorder", "Book", "GoldenHead", "FreezePlayer", "Marker", "OfflineKicker", "GlobalChat" };
	private final String[] STATUS_MSG = { "\u00A7CUltra Hardcore 1.8\u00A7R{N}\u00A77\u00A7LStatus:\u00A7R\u00A7", "\u00A7C\u00A7LUltra Hardcore 1.8" };
	private final int VERSION = 4;
	private Main pl;
	private int configVersion;
	public boolean pluginEnable;
	public boolean pluingUpdate;
	public int chunkBorder;
	public int chunkDelay;
	public boolean chunkInNether;
	public boolean chunkInDetail;
	public int chunkTask;
	public boolean grDisabledElytra;
	public boolean grDaylight;
	public boolean grEntityDrops;
	public boolean grFireTick;
	public boolean grLimitedCrafting;
	public boolean grMobLoot;
	public boolean grMobSpawning;
	public boolean grTileDrops;
	public boolean grWeather;
	public int grMaxCramming;
	public boolean grMobGriefing;
	public int grRandom;
	public boolean grDebugInfo;
	public int grSpawnRadius;
	public boolean grSpectators;
	public boolean serverEnable;
	public boolean serverAdvancedMotd;
	public int serverID;
	public int serverMinSolo;
	public int serverMinTeam;
	public int serverCountdown;
	public String serverMotd;
	public boolean serverActiveChunkloader;
	public String serverStatusDisabled = STATUS_MSG[0] + "C Disabled";
	public String serverStatusError = STATUS_MSG[0] + "4\u00A7l Error";
	public String serverStatusLoading = STATUS_MSG[0] + "7 Loading... %C";
	public String serverStatusReset = STATUS_MSG[0] + "C Restarting";
	public String serverStatusWaiting = STATUS_MSG[0] + "A Waiting for players...";
	public String serverStatusCountdown = STATUS_MSG[0] + "E Starting soon...";
	public String serverStatusStarting = STATUS_MSG[0] + "7 Started";
	public String serverStatusInGame = STATUS_MSG[0] + "7 InGame";
	public String serverStatusFinished = STATUS_MSG[0] + "7 Finished";
	public String serverKickError = STATUS_MSG[1] + " - Error";
	public String serverKickLoading = STATUS_MSG[1] + " - Loading";
	public String serverKickEnding = STATUS_MSG[1] + " - Ending";
	public String serverKickReset = STATUS_MSG[1] + " - Update / Restart";
	public String serverKickShutdown = STATUS_MSG[1] + "\u00A7R\n\u00A7CServer Restart\u00A7R\n\u00A7AThanks for playing.";
	public boolean serverIsBungeeCord;
	public String serverHub;
	public String serverMaterial;
	public int serverInventorySlot;
	public boolean gameInTeam;
	public int gameMaxTeam;
	public int gameSelectTeamInventory;
	public boolean gameOldCombat;
	public boolean gamePlayerListHearts;
	public int gameCollision;
	public boolean gameIsFriendly;
	public int gameNameTag;
	public boolean gameSeeFriendly;
	public List<String> gameTeamNames;
	public boolean tabEnable;
	public String tabHeader = "\u00A7CUltra Hardcore 1.8\u00A7R";
	public String tabFooter;
	public int worldDifficulty;
	public int worldSize;
	public int worldTime;
	public int borderDelay;
	public int borderStartSize;
	public int borderEndSize;
	public int borderTime;
	public boolean bookEnable;
	public int bookInventorySlot;
	public String boolTitle = "\u00A7CUltra Hardcore";
	public String[] bookLore = { "0|", "0|\u00A76- \u00A77Info", "0|\u00A76- \u00A77Rules" };
	public String[] bookPages = { "Welcome to \u00A74\u00A7LUHC\u00A70.\n\nThis game you can only regenerate health by\n \u00A78- \u00A71Golden Apple.\u00A7R\n \u00A78- \u00A71Potions.\u00A7R\n\nI wish you \u00A75Good Luck\u00A7R\nand may the best player / team win.", "\u00A7L\u00A7N    UHC - Rules     \u00A7R\n\n\u00A711.\u00A7R Branch Mining\n\u00A78 You can not branch\n mining but if you\n hear a sound,\n you can dig to it.\u00A7R\n\n\u00A712. Staircases\u00A7R\n\u00A78 You can only dig\n staircases if you\n want to find a cave." };
	public boolean headEnable;
	public boolean headDefault;
	public boolean headGolden;
	public boolean freezeEnable;
	public int freezeSize;
	public int markerDelay;
	public String markerMsg = "\u00A7CUHC\u00A78 |\u00A7R YOU ARE NOW IN \u00A7A%0\u00A7R MIN IN";
	public boolean dmgEnable;
	public String kickerMsg;
	public int kickerDelay;
	public boolean chatEnable;
	public String chatDefault = "<%0> %0";
	public String chatTeamMsg = "\u00A77Team\u00A7R <%0> \u00A77%0";
	public String chatSpectator = "<\u00A77\u00A7O%0\u00A7R> %0";
	public String chatTeam = "<%0> %0";

	public Config(Main pl) {
		this.pl = pl;
	}

	public void load() {
		pl.saveDefaultConfig();
		configVersion = getInt(3, "Config", 0, VERSION);
		pluginEnable = getBoolean(false, PATHS[0] + ".Enabled");
		pluingUpdate = getBoolean(false, PATHS[0] + ".UpdateNotification");
		chunkBorder = getInt(250, PATHS[1] + ".ArenaBorder", 0, 20000);
		chunkDelay = getInt(8, PATHS[1] + ".DelayTick", 0, 20000);
		chunkInNether = getBoolean(true, PATHS[1] + ".LoadNether");
		chunkInDetail = getBoolean(false, PATHS[1] + ".ShowHiddenDetail");
		chunkTask = getInt(20, PATHS[1] + ".Task", 0, 20000);
		grDisabledElytra = getBoolean(false, PATHS[2] + ".disableElytraMovementCheck");
		grDaylight = getBoolean(true, PATHS[2] + ".doDaylightCycle");
		grEntityDrops = getBoolean(true, PATHS[2] + ".doEntityDrops");
		grFireTick = getBoolean(true, PATHS[2] + ".doFireTick");
		grLimitedCrafting = getBoolean(true, PATHS[2] + ".doLimitedCrafting");
		grMobLoot = getBoolean(true, PATHS[2] + ".doMobLoot");
		grMobSpawning = getBoolean(true, PATHS[2] + ".doMobSpawning");
		grTileDrops = getBoolean(true, PATHS[2] + ".doTileDrops");
		grWeather = getBoolean(true, PATHS[2] + ".doWeatherCycle");
		grMaxCramming = getInt(24, PATHS[2] + ".maxEntityCramming", 0, 100);
		grMobGriefing = getBoolean(true, PATHS[2] + ".mobGriefing");
		grRandom = getInt(3, PATHS[2] + ".randomTickSpeed", 0, 10);
		grDebugInfo = getBoolean(false, PATHS[2] + ".reducedDebugInfo");
		grSpawnRadius = getInt(0, PATHS[2] + ".spawnRadius", 0, 10);
		grSpectators = getBoolean(false, PATHS[2] + ".spectatorsGenerateChunks");
		serverEnable = getBoolean(false, PATHS[3] + ".Enabled");
		serverAdvancedMotd = getBoolean(false, PATHS[3] + ".AdvancedMotd");
		serverID = getInt(1, PATHS[3] + ".ServerID", 1, 9999);
		serverMinSolo = getInt(8, PATHS[3] + ".MinPlayerToStart", 2, 100);
		serverMinTeam = getInt(4, PATHS[3] + ".MinTeamToStart", 2, 53);
		serverCountdown = getInt(30, PATHS[3] + ".Countdown", 10, 20000);
		serverMotd = getString("%0|%1|%2|%3", PATHS[3] + ".SimpleMotd", true);
		serverActiveChunkloader = getBoolean(false, PATHS[3] + ".DisabledChunkloader");
		serverStatusDisabled = getString(serverStatusDisabled, PATHS[4] + ".Disabled", true);
		serverStatusError = getString(serverStatusError, PATHS[4] + ".Error", true);
		serverStatusLoading = getString(serverStatusLoading, PATHS[4] + ".Loading", true);
		serverStatusReset = getString(serverStatusReset, PATHS[4] + ".Reset", true);
		serverStatusWaiting = getString(serverStatusWaiting, PATHS[4] + ".Waiting", true);
		serverStatusCountdown = getString(serverStatusCountdown, PATHS[4] + ".WaitingStarting", true);
		serverStatusStarting = getString(serverStatusStarting, PATHS[4] + ".Starting", true);
		serverStatusInGame = getString(serverStatusInGame, PATHS[4] + ".InGame", true);
		serverStatusFinished = getString(serverStatusFinished, PATHS[4] + ".Finished", true);
		serverKickError = getString(serverKickError, PATHS[5] + ".PlayerJoinError", true);
		serverKickLoading = getString(serverKickLoading, PATHS[5] + ".PlayerJoinLoading", true);
		serverKickEnding = getString(serverKickEnding, PATHS[5] + ".PlayerJoinGameEnd", true);
		serverKickReset = getString(serverKickReset, PATHS[5] + ".PlayerKickRestart", true);
		serverKickShutdown = getString(serverKickShutdown, PATHS[5] + ".PlayerKickGameEnd", true);
		serverIsBungeeCord = getBoolean(false, PATHS[6] + ".Enabled");
		serverHub = getString("hub", PATHS[6] + ".FallbackServer", true);
		serverMaterial = getString("BARRIER", PATHS[6] + ".Item", true);
		serverInventorySlot = getInt(8, PATHS[6] + ".InvSlot", 0, 8);
		gameInTeam = getBoolean(false, PATHS[7] + ".TeamMode");
		gameMaxTeam = getInt(2, PATHS[7] + ".MaxTeamPlayer", 1, 20000);
		gameSelectTeamInventory = getInt(1, PATHS[7] + ".SelectTeamInvSlot", 0, 8);
		gameOldCombat = getBoolean(false, PATHS[7] + ".OldCombatMode");
		gamePlayerListHearts = getBoolean(true, PATHS[7] + ".PlayerListHearts");
		gameCollision = getInt(0, PATHS[8] + ".CollisionRule", 0, 3);
		gameIsFriendly = getBoolean(true, PATHS[8] + ".FriendlyFire");
		gameNameTag = getInt(0, PATHS[8] + ".NameTagVisibility", 0, 3);
		gameSeeFriendly = getBoolean(false, PATHS[8] + ".SeeFriendlyInvisibles");
		gameTeamNames = getListTeam(gameTeamNames, PATHS[7] + ".TeamNames");
		tabEnable = getBoolean(false, PATHS[9] + ".Enabled");
		tabHeader = getString(tabHeader, PATHS[9] + ".Header", false);
		tabFooter = getString("", PATHS[9] + ".Footer", false);
		worldDifficulty = getInt(2, PATHS[10] + ".Difficulty", 1, 3);
		worldSize = getInt(1000, PATHS[10] + ".ArenaSize", 100, 20000);
		worldTime = getInt(0, PATHS[10] + ".SunTime", 0, 23999);
		borderDelay = getInt(0, PATHS[11] + ".StartDelay", 0, 20000);
		borderStartSize = getInt(1250, PATHS[11] + ".StartSize", 50, 20000);
		borderEndSize = getInt(150, PATHS[11] + ".EndSize", 5, 20000);
		borderTime = getInt(10800, PATHS[11] + ".Time", 0, 100000);
		bookEnable = getBoolean(false, PATHS[12] + ".Enabled");
		bookInventorySlot = getInt(7, PATHS[12] + ".InvSlot", 0, 8);
		boolTitle = getString(boolTitle, PATHS[12] + ".Name", true);
		bookLore = getList(bookLore, PATHS[12] + ".Lord");
		bookPages = getList(bookPages, PATHS[12] + ".Pages");
		headEnable = getBoolean(false, PATHS[13] + ".Enabled");
		headDefault = getBoolean(false, PATHS[13] + ".DefaultApple");
		headGolden = getBoolean(false, PATHS[13] + ".GoldenApple");
		freezeEnable = getBoolean(true, PATHS[14] + ".Enabled");
		freezeSize = getInt(10, PATHS[14] + ".Size", 5, 15);
		markerMsg = getString(markerMsg, PATHS[15] + ".Message", false);
		markerDelay = getInt(20, PATHS[15] + ".Delay", 0, 20000);
		dmgEnable = getBoolean(false, "DamagerLogger");
		kickerMsg = getString("%0 was killed by Offline Timer", PATHS[16] + ".Message", true);
		kickerDelay = getInt(5, PATHS[16] + ".Timeout", 1, 20);
		chatEnable = getBoolean(true, PATHS[17] + ".Enabled");
		chatDefault = getString(chatDefault, PATHS[17] + ".Default", false);
		chatTeamMsg = getString(chatTeamMsg, PATHS[17] + ".PrivateTeamChat", false);
		chatSpectator = getString(chatSpectator, PATHS[17] + ".Spectator", false);
		chatTeam = getString(chatTeam, PATHS[17] + ".Team", false);
		if (configVersion < VERSION)
			log("Your config file is outdated!");
	}

	// ---------------------------------------------------------------------------

	private boolean getBoolean(boolean b, String path) {
		if (pl.getConfig().isBoolean(path))
			return pl.getConfig().getBoolean(path);
		log("'" + path + "' was not found!");
		return b;
	}

	private int getInt(int value, String path, int min, int max) {
		if (pl.getConfig().isInt(path)) {
			int newValue = pl.getConfig().getInt(path);
			if (newValue >= min && newValue <= max)
				return newValue;
			log("'" + path + "' can only be between [" + min + " - " + max + "]");
			return value;
		}
		log("'" + path + "' was not found!");
		return value;
	}

	private String[] getList(String[] list, String path) {
		if (pl.getConfig().isList(path)) {
			String[] newList = new String[pl.getConfig().getStringList(path).size()];
			for (int i = 0; i < newList.length; i++)
				newList[i] = (path.equals(PATHS[12] + ".Lord") ? "0|" : "") + F.mcCodeLn(pl.getConfig().getStringList(path).get(i));
			return newList;
		}
		log("'" + path + "' was not found!");
		return list;
	}

	private List<String> getListTeam(List<String> list, String path) {
		if (pl.getConfig().isList(path)) {
			List<String> newList = new ArrayList<>();
			List<String> newList2 = new ArrayList<>();
			for (int i = 0; i < pl.getConfig().getList(path).size(); i++) {
				String[] tm = pl.getConfig().getStringList(path).get(i).split("\\|");
				if (!newList2.contains(tm[0]) && tm[0].toLowerCase().matches("^[a-z0-9 ]+")) {
					if (ChatColor.getByChar(tm[1]) != null && tm[1].toLowerCase().matches("[a-f0-9]")) {
						newList.add(pl.getConfig().getStringList(path).get(i));
						newList2.add(tm[0]);
						if(newList2.size() == 53) {
							pl.log(2, "[CONFIG] Maximum teams has been reached! (53 Teams)");
							return newList;
						}
					} else
						pl.log(1, "[CONFIG] Team '" + tm[0] + " | " + tm[1] + "' has wrong color code!");
				} else
					pl.log(1, "[CONFIG] Team '" + tm[0] + " | " + tm[1] + "' " + (newList2.contains(tm[0]) ? "already created!" : "has unwanted characters!"));
			}
			return newList;
		}
		log("'" + path + "' was not found!");
		return list;
	}

	private String getString(String str, String path, boolean b) {
		if (pl.getConfig().isString(path)) {
			String newStr = pl.getConfig().getString(path);
			if (b && newStr.length() == 0) {
				log("'" + path + "' can not be empty!");
				return str;
			}
			return F.mcCodeLn(newStr);
		}
		log("'" + path + "' was not found!");
		return str;
	}

	private void log(String log) {
		pl.log(1, "[CONFIG] " + log);
	}
}
