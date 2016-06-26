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

package com.thomaztwofast.uhc.data;

import com.thomaztwofast.uhc.Main;
import com.thomaztwofast.uhc.custom.Function;

public class Config extends Function {
	private Main cA;
	private int cBa = 1;
	private int cBb = 1;
	public boolean cCa = false;
	public boolean cCb = false;
	public int cDa = 250;
	public int cDb = 8;
	public int cDc = 20;
	public boolean cDd = false;
	public boolean cDe = false;
	public boolean cEa = true;
	public boolean cEb = true;
	public boolean cEc = true;
	public boolean cEd = true;
	public boolean cEe = true;
	public boolean cEf = true;
	public boolean cEg = true;
	public int cEh = 3;
	public boolean cEi = false;
	public boolean cEj = false;
	public int cEk = 0;
	public boolean cFa = false;
	public boolean cFb = false;
	public int cFc = 1;
	public int cFd = 8;
	public int cFe = 4;
	public int cFf = 30;
	public String cFg = "{0}|{1}{2}{3}";
	public String cFh = "\u00A7cUltra Hardcore 1.8\u00A7r\n\u00A77\u00A7lStatus:\u00A7r\u00A7c Disabled";
	public String cFi = "\u00A7cUltra Hardcore 1.8\u00A7r\n\u00A77\u00A7lStatus:\u00A7r\u00A74\u00A7l Error";
	public String cFj = "\u00A7cUltra Hardcore 1.8\u00A7r\n\u00A77\u00A7lStatus:\u00A7r\u00A77 Loading...";
	public String cFk = "\u00A7cUltra Hardcore 1.8\u00A7r\n\u00A77\u00A7lStatus:\u00A7r\u00A7c Restarting";
	public String cFl = "\u00A7cUltra Hardcore 1.8\u00A7r\n\u00A77\u00A7lStatus:\u00A7r\u00A7a Waiting for players...";
	public String cFm = "\u00A7cUltra Hardcore 1.8\u00A7r\n\u00A77\u00A7lStatus:\u00A7r\u00A7e Starting soon...";
	public String cFn = "\u00A7cUltra Hardcore 1.8\u00A7r\n\u00A77\u00A7lStatus:\u00A7r\u00A77 Started";
	public String cFo = "\u00A7cUltra Hardcore 1.8\u00A7r\n\u00A77\u00A7lStatus:\u00A7r\u00A77 InGame";
	public String cFp = "\u00A7cUltra Hardcore 1.8\u00A7r\n\u00A77\u00A7lStatus:\u00A7r\u00A77 Finished";
	public String cFq = "\u00A7c\u00A7lUltra Hardcore 1.8 - Error";
	public String cFr = "\u00A7c\u00A7lUltra Hardcore 1.8 - Loading";
	public String cFs = "\u00A7c\u00A7lUltra Hardcore 1.8 - Ending";
	public String cFt = "\u00A7c\u00A7lUltra Hardcore 1.8 - Update / Restart";
	public String cFu = "\u00A7c\u00A7lUltra Hardcore 1.8\u00A7r\n\u00A7cServer Restart\u00A7r\n\u00A7aThanks for playing.";
	public boolean cFv = false;
	public String cFw = "hub";
	public String cFx = "BARRIER";
	public int cFy = 8;
	public boolean cGa = false;
	public int cGb = 3;
	public int cGc = 1;
	public boolean cGd = true;
	public boolean cGe = false;
	public int cGf = 0;
	public int cGg = 0;
	public boolean cHa = false;
	public String cHb = "\u00A7cUltra Hardcore 1.8\u00A7r";
	public String cHc = "";
	public int cIa = 2;
	public int cIb = 1000;
	public int cIc = 0;
	public int cJa = 0;
	public int cJb = 1250;
	public int cJc = 150;
	public int cJd = 10800;
	public boolean cKa = false;
	public int cKb = 7;
	public String cKc = "\u00A7cUltra Hardcore";
	public String[] cKd = new String[] { "0|", "0|\u00A76- \u00A77Info", "0|\u00A76- \u00A77Rules" };
	public String[] cKe = new String[] { "Welcome to \u00A74\u00A7lUHC\u00A70.\n\nThis game you can only regenerate health by\n \u00A78- \u00A71Golden Apple.\u00A7r\n \u00A78- \u00A71Potions.\u00A7r\n\nI wish you \u00A75Good Luck\u00A7r\nand may the best player / team win.", "\u00A7l\u00A7n    UHC - Rules     \u00A7r\n\n\u00A711.\u00A7r Branch Mining\n\u00A78 You can not branch\n mining but if you\n hear a sound,\n you can dig to it.\u00A7r\n\n\u00A712. Staircases\u00A7r\n\u00A78 You can only dig\n staircases if you\n want to find a cave." };
	public boolean cLa = false;
	public boolean cLb = false;
	public boolean cLc = true;
	public boolean cMa = true;
	public int cMb = 10;
	public String cNa = "\u00A7cUHC\u00A78 |\u00A7r YOU ARE NOW IN \u00A7a{0}\u00A7r MIN IN";
	public int cNb = 20;
	public boolean cOa = false;
	public int cPa = 5;
	public String cPb = "{0} was killed by Offline Timer";
	public boolean cQa = true;
	public String cQb = "<{0}> {1}";
	public String cQc = "<\u00A77\u00A7o{0}\u00A7r> {1}";
	public String cQd = "<{0}> {1}";
	public String cQe = "\u00A77Team\u00A7r <{0}> \u00A77{1}";

	public Config(Main a) {
		cA = a;
	}

	public void loadConfig() {
		cA.saveDefaultConfig();
		cBb = i(cBb, "Config", 0, cBb);
		cCa = b(cCa, "Plugin.Enabled");
		cCb = b(cCb, "Plugin.UpdateNotification");
		cDa = i(cDa, "Chunkloader.ArenaBorder", 0, 20000);
		cDb = i(cDb, "Chunkloader.DelayTick", 0, 20000);
		cDc = i(cDc, "Chunkloader.Task", 0, 20000);
		cDd = b(cDd, "Chunkloader.ShowHiddenDetail");
		cDe = b(cDe, "Chunkloader.LoadNether");
		cEa = b(cEa, "Gamerules.doDaylightCycle");
		cEb = b(cEb, "Gamerules.doEntityDrops");
		cEc = b(cEc, "Gamerules.doFireTick");
		cEd = b(cEd, "Gamerules.doMobLoot");
		cEe = b(cEe, "Gamerules.doMobSpawning");
		cEf = b(cEf, "Gamerules.doTileDrops");
		cEg = b(cEg, "Gamerules.mobGriefing");
		cEh = i(cEh, "Gamerules.randomTickSpeed", 0, 10);
		cEi = b(cEi, "Gamerules.reducedDebugInfo");
		cEj = b(cEj, "Gamerules.spectatorsGenerateChunks");
		cEk = i(cEk, "Gamerules.spawnRadius", 0, 10);
		cFa = b(cFa, "ServerMode.Enabled");
		cFb = b(cFb, "ServerMode.AdvancedMotd");
		cFc = i(cFc, "ServerMode.ServerID", 1, 9999);
		cFd = i(cFd, "ServerMode.MinPlayerToStart", 2, 100);
		cFe = i(cFe, "ServerMode.MinTeamToStart", 2, 16);
		cFf = i(cFf, "ServerMode.Countdown", 10, 20000);
		cFg = s(cFg, "ServerMode.SimpleMotd", true);
		cFh = s(cFh, "ServerMode.AdvencedMotdStatus.Disabled", true);
		cFi = s(cFi, "ServerMode.AdvencedMotdStatus.Error", true);
		cFj = s(cFj, "ServerMode.AdvencedMotdStatus.Loading", true);
		cFk = s(cFk, "ServerMode.AdvencedMotdStatus.Reset", true);
		cFl = s(cFl, "ServerMode.AdvencedMotdStatus.Waiting", true);
		cFm = s(cFm, "ServerMode.AdvencedMotdStatus.WaitingStarting", true);
		cFn = s(cFn, "ServerMode.AdvencedMotdStatus.Starting", true);
		cFo = s(cFo, "ServerMode.AdvencedMotdStatus.InGame", true);
		cFp = s(cFp, "ServerMode.AdvencedMotdStatus.Finished", true);
		cFq = s(cFq, "ServerMode.KickMessage.PlayerJoinError", true);
		cFr = s(cFr, "ServerMode.KickMessage.PlayerJoinLoading", true);
		cFs = s(cFs, "ServerMode.KickMessage.PlayerJoinGameEnd", true);
		cFt = s(cFt, "ServerMode.KickMessage.PlayerKickRestart", true);
		cFu = s(cFu, "ServerMode.KickMessage.PlayerKickGameEnd", true);
		cFv = b(cFv, "ServerMode.BungeeCordSupport.Enabled");
		cFw = s(cFw, "ServerMode.BungeeCordSupport.FallbackServer", true);
		cFx = s(cFx, "ServerMode.BungeeCordSupport.Item", true);
		cFy = i(cFy, "ServerMode.BungeeCordSupport.InvSlot", 0, 8);
		cGa = b(cGa, "Game.TeamMode");
		cGb = i(cGb, "Game.MaxTeamPlayer", 1, 20000);
		cGc = i(cGc, "Game.SelectTeamInvSlot", 0, 8);
		cGd = b(cGd, "Game.Options.FriendlyFire");
		cGe = b(cGe, "Game.Options.SeeFriendlyInvisibles");
		cGf = i(cGf, "Game.Options.NameTagVisibility", 0, 3);
		cGg = i(cGg, "Game.Options.CollisionRule", 0, 3);
		cHa = b(cHa, "TabList.Enabled");
		cHb = s(cHb, "TabList.Header", false);
		cHc = s(cHc, "TabList.Footer", false);
		cIa = i(cIa, "WorldSettings.Difficulty", 1, 3);
		cIb = i(cIb, "WorldSettings.ArenaSize", 100, 20000);
		cIc = i(cIc, "WorldSettings.SunTime", 0, 23999);
		cJa = i(cJa, "WorldBorder.StartDelay", 0, 20000);
		cJb = i(cJb, "WorldBorder.StartPos", 50, 20000);
		cJc = i(cJc, "WorldBorder.EndPos", 5, 20000);
		cJd = i(cJd, "WorldBorder.Time", 0, 100000);
		cKa = b(cKa, "Book.Enabled");
		cKb = i(cKb, "Book.InvSlot", 0, 8);
		cKc = s(cKc, "Book.Name", true);
		cKd = l(cKd, "Book.Lord");
		cKe = l(cKe, "Book.Pages");
		cLa = b(cLa, "GoldenHead.Enabled");
		cLb = b(cLb, "GoldenHead.ItemsRecipes.DefaultApple.Enabled");
		cLc = b(cLc, "GoldenHead.ItemsRecipes.GoldenApple.Enabled");
		cMa = b(cMa, "FreezePlayer.Enabled");
		cMb = i(cMb, "FreezePlayer.Size", 5, 15);
		cNa = s(cNa, "Marker.Message", false);
		cNb = i(cNb, "Marker.Delay", 0, 20000);
		cOa = b(cOa, "DamagerLogger");
		cPa = i(cPa, "OfflineKicker.Timeout", 1, 20);
		cPb = s(cPb, "OfflineKicker.Message", true);
		cQa = b(cQa, "GlobalChat.Enabled");
		cQb = s(cQb, "GlobalChat.Default", false);
		cQc = s(cQc, "GlobalChat.Spectator", false);
		cQd = s(cQd, "GlobalChat.Team", false);
		cQe = s(cQe, "GlobalChat.PrivateTeamChat", false);
		cc();
	}

	// ------:- PRIVATE -:--------------------------------------------------------------------------

	private boolean b(boolean a, String b) {
		if (cA.getConfig().isBoolean(b)) {
			return cA.getConfig().getBoolean(b);
		}
		cA.log(1, "[CONFIG] Path: '" + b + "' was not found!");
		return a;
	}

	private int i(int a, String b, int c, int d) {
		if (cA.getConfig().isInt(b)) {
			int e = cA.getConfig().getInt(b);
			if (e >= c && e <= d) {
				return e;
			}
			cA.log(1, "[CONFIG] Path: '" + b + "' can only be " + c + " - " + d + "");
			return a;
		}
		cA.log(1, "[CONFIG] Path: '" + b + "' was not found!");
		return a;
	}

	private String[] l(String[] a, String b) {
		if (cA.getConfig().isList(b)) {
			String[] c = new String[cA.getConfig().getStringList(b).size()];
			for (int d = 0; d < c.length; d++) {
				if (b.equals("Book.Lord")) {
					c[d] = "0|" + colorLn(cA.getConfig().getStringList(b).get(d));
				} else {
					c[d] = colorLn(cA.getConfig().getStringList(b).get(d));
				}
			}
			return c;
		}
		cA.log(1, "[CONFIG] Path: '" + b + "' was not found!");
		return a;
	}

	private String s(String a, String b, boolean c) {
		if (cA.getConfig().isString(b)) {
			String d = cA.getConfig().getString(b);
			if (c && d.length() == 0) {
				cA.log(1, "[CONFIG] Path: '" + b + "' can not be empty!");
				return a;
			}
			return colorLn(d);
		}
		cA.log(1, "[CONFIG] Path: '" + b + "' was not found!");
		return a;
	}

	private void cc() {
		if (cBb < cBa) {
			cA.log(1, "Your config file is outdated!");
		}
	}
}
