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

import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;

import com.thomaztwofast.uhc.Function;
import com.thomaztwofast.uhc.Main;
import com.thomaztwofast.uhc.custom.Logger;

public class Config {
	private Logger l;
	private Function f;
	private FileConfiguration fC;
	private int iM = 0;
	private int iMX = 20000;
	private boolean pl = false;
	private boolean plUn = false;
	private boolean clShd = false;
	private int clDt = 8;
	private int clT = 20;
	private int clAb = 500;
	private boolean grDdlc = true;
	private boolean grDed = true;
	private boolean grDft = true;
	private boolean grDml = true;
	private boolean grDms = true;
	private boolean grDtd = true;
	private boolean grMg = true;
	private boolean grSgc = false;
	private int grRts = 3;
	private boolean grRdi = false;
	private boolean sm = false;
	private boolean smAm = false;
	private int smId = 1;
	private int smMpts = 8;
	private int smMtts = 4;
	private int smC = 30;
	private String smSm = "{0}|{1}|{2}|{3}";
	private String smAmsD = "§cUltra Hardcore 1.8§r\n§7§lStatus:§r §cDisabled";
	private String smAmsE = "§cUltra Hardcore 1.8§r\n§7§lStatus:§r §4§lError";
	private String smAmsL = "§cUltra Hardcore 1.8§r\n§7§lStatus:§r §7Loading...";
	private String smAmsR = "§cUltra Hardcore 1.8§r\n§7§lStatus:§r §cRestarting";
	private String smAmsW = "§cUltra Hardcore 1.8§r\n§7§lStatus:§r §aWaiting for players...";
	private String smAmsWs = "§cUltra Hardcore 1.8§r\n§7§lStatus:§r §eStarting soon...";
	private String smAmsS = "§cUltra Hardcore 1.8§r\n§7§lStatus:§r §7Started";
	private String smAmsIg = "§cUltra Hardcore 1.8§r\n§7§lStatus:§r §7InGame";
	private String smAmsF = "§cUltra Hardcore 1.8§r\n§7§lStatus:§r §7Finished";
	private String smKmPje = "§c§lUltra Hardcore 1.8 - Error";
	private String smKmPjl = "§c§lUltra Hardcore 1.8 - Loading";
	private String smKmPjg = "§c§lUltra Hardcore 1.8 - Ending";
	private String smKmPkr = "§c§lUltra Hardcore 1.8 - Update/Restart";
	private String smKmPkg = "§c§lUltra Hardcore 1.8§r\n§cServer Restart§r\n§aThanks for playing.";
	private boolean smBs = false;
	private String smBsFs = "hub";
	private String smBsI = "BARRIER";
	private int smBsIs = 8;
	private boolean gTm = false;
	private int gMtp = 3;
	private int gStis = 1;
	private boolean gOFf = true;
	private boolean gOSfi = false;
	private int gONt = 0;
	private boolean tl = false;
	private String tlH = "§c§lUltra Hardcore 1.8§r";
	private String tlF = "";
	private int wsD = 2;
	private int wsAs = 1000;
	private int wsSt = 0;
	private int wbSd = 0;
	private int wbSp = 1250;
	private int wbEp = 150;
	private int wbT = 10800;
	private boolean bk = false;
	private boolean bkSs = false;
	private int bkIs = 7;
	private String bkN = "§eUltra Hardcore";
	private ArrayList<String> bkL = new ArrayList<>();
	private ArrayList<String> bkP = new ArrayList<>();
	private boolean gh = false;
	private boolean ghIrDa = false;
	private boolean ghIrGha = true;
	private boolean fp = false;
	private int fpS = 10;
	private String mkM = "§cUHC§8 |§r YOU ARE NOW IN §a{0}§r MIN IN";
	private int mkD = 20;
	private boolean dl = false;
	private int ofT = 5;
	private String ofM = "{0} was killed by Offline Timer";
	private String gcD = "<{0}> {1}";
	private String gcS = "<§7§o{0}§r> {1}";
	private String gcTD = "<§o{0}§r> {1}";
	private String gcTT = "<{0}> {1}";
	private String gcTPc = "§7§lTeam§r <{0}> §7{1}";

	public Config(Main m) {
		m.saveDefaultConfig();
		l = m.getPlLog();
		f = m.getPlFun();
		fC = m.getConfig();

		// Load config
		pl = gcb(pl, "Plugin.Enabled");
		plUn = gcb(plUn, "Plugin.UpdateNotification");
		clShd = gcb(clShd, "ChunkLoader.ShowHiddenDetail");
		clDt = gci(clDt, "ChunkLoader.DelayTick", iM, iMX);
		clT = gci(clT, "ChunkLoader.Task", iM, iMX);
		clAb = gci(clAb, "ChunkLoader.ArenaBorder", iM, iMX);
		grDdlc = gcb(grDdlc, "Gamerules.doDaylightCycle");
		grDed = gcb(grDed, "Gamerules.doEntityDrops");
		grDft = gcb(grDft, "Gamerules.doFireTick");
		grDml = gcb(grDml, "Gamerules.doMobLoot");
		grDms = gcb(grDms, "Gamerules.doMobSpawning");
		grDtd = gcb(grDtd, "Gamerules.doTileDrops");
		grMg = gcb(grMg, "Gamerules.mobGriefing");
		grRts = gci(grRts, "Gamerules.randomTickSpeed", iM, 10);
		grRdi = gcb(grRdi, "Gamerules.reducedDebugInfo");
		grSgc = gcb(grSgc, "Gamerules.spectatorsGenerateChunks");
		sm = gcb(sm, "ServerMode.Enabled");
		smAm = gcb(smAm, "ServerMode.AdvancedMotd");
		smId = gci(smId, "ServerMode.ServerID", 1, 9999);
		smMpts = gci(smMpts, "ServerMode.MinPlayerToStart", 2, 100);
		smMtts = gci(smMtts, "ServerMode.MinTeamToStart", 2, 16);
		smC = gci(smC, "ServerMode.Countdown", 10, iMX);
		smSm = gcsc(smSm, "ServerMode.SimpleMotd", true);
		smAmsD = gcsc(smAmsD, "ServerMode.AdvancedMotdStatus.Disabled", true);
		smAmsE = gcsc(smAmsE, "ServerMode.AdvancedMotdStatus.Error", true);
		smAmsL = gcsc(smAmsL, "ServerMode.AdvancedMotdStatus.Loading", true);
		smAmsR = gcsc(smAmsR, "ServerMode.AdvancedMotdStatus.Reset", true);
		smAmsW = gcsc(smAmsW, "ServerMode.AdvancedMotdStatus.Waiting", true);
		smAmsWs = gcsc(smAmsWs, "ServerMode.AdvancedMotdStatus.WaitingStarting", true);
		smAmsS = gcsc(smAmsS, "ServerMode.AdvancedMotdStatus.Starting", true);
		smAmsIg = gcsc(smAmsIg, "ServerMode.AdvancedMotdStatus.InGame", true);
		smAmsF = gcsc(smAmsF, "ServerMode.AdvancedMotdStatus.Finished", true);
		smKmPje = gcsc(smKmPje, "ServerMode.KickMessage.PlayerJoinError", true);
		smKmPjl = gcsc(smKmPjl, "ServerMode.KickMessage.PlayerJoinLoading", true);
		smKmPjg = gcsc(smKmPjg, "ServerMode.KickMessage.PlayerJoinGameEnd", true);
		smKmPkr = gcsc(smKmPkr, "ServerMode.KickMessage.PlayerKickRestart", true);
		smKmPkg = gcsc(smKmPkg, "ServerMode.KickMessage.PlayerKickGameEnd", true);
		smBs = gcb(smBs, "ServerMode.BungeeCordSupport.Enabled");
		smBsFs = gcs(smBsFs, "ServerMode.BungeeCordSupport.FallbackServer", true);
		smBsI = gcs(smBsI, "ServerMode.BungeeCordSupport.Item", true);
		smBsIs = gci(smBsIs, "ServerMode.BungeeCordSupport.InvSlot", iM, 8);
		gTm = gcb(gTm, "Game.TeamMode");
		gMtp = gci(gMtp, "Game.MaxTeamPlayer", 1, iMX);
		gStis = gci(gStis, "Game.SelectTeamInvSlot", iM, 8);
		gOFf = gcb(gOFf, "Game.Options.FriendlyFire");
		gOSfi = gcb(gOSfi, "Game.Options.SeeFriendlyInvisibles");
		gONt = gci(gONt, "Game.Options.NameTagVisibility", 0, 3);
		tl = gcb(tl, "TabList.Enabled");
		tlH = gcsc(tlH, "TabList.Header", false);
		tlF = gcsc(tlF, "TabList.Footer", false);
		wsD = gci(wsD, "WorldSettings.Difficulty", 1, 3);
		wsAs = gci(wsAs, "WorldSettings.ArenaSize", 100, iMX);
		wsSt = gci(wsSt, "WorldSettings.SunTime", iM, 23999);
		wbSd = gci(wbSd, "Worldborder.StartDelay", iM, iMX);
		wbSp = gci(wbSp, "Worldborder.StartPos", 50, iMX);
		wbEp = gci(wbEp, "Worldborder.EndPos", 5, iMX);
		wbT = gci(wbT, "Worldborder.Time", iM, 100000);
		bk = gcb(bk, "Book.Enabled");
		bkSs = gcb(bkSs, "Book.ShowSettings");
		bkIs = gci(bkIs, "Book.InventorySlot", iM, 8);
		bkN = gcsc(bkN, "Book.Name", true);
		bkL = gcl(0, "Book.Lord");
		bkP = gcl(1, "Book.Pages");
		gh = gcb(gh, "GoldenHead.Enabled");
		ghIrDa = gcb(ghIrDa, "GoldenHead.ItemsRecipes.DefaultApple.Enabled");
		ghIrGha = gcb(ghIrGha, "GoldenHead.ItemsRecipes.GoldenHeadApple.Enabled");
		fp = gcb(fp, "FreezePlayer.Enabled");
		fpS = gci(fpS, "FreezePlayer.Size", 5, 15);
		mkM = gcsc(mkM, "Marker.Message", false);
		mkD = gci(mkD, "Marker.Delay", iM, iMX);
		dl = gcb(dl, "DamagerLogger");
		ofT = gci(ofT, "OfflineKicker.Timeout", 1, 20);
		ofM = gcsc(ofM, "OfflineKicker.Message", true);
		gcD = gcsc(gcD, "GlobalChat.Default", false);
		gcS = gcsc(gcS, "GlobalChat.Spectator", false);
		gcTD = gcsc(gcTD, "GlobalChat.Teams.Default", false);
		gcTT = gcsc(gcTT, "GlobalChat.Teams.Team", false);
		gcTPc = gcsc(gcTPc, "GlobalChat.Teams.PrivateChat", false);
	}

	// :: PUBLIC > GET :: //

	public boolean pl_Enabled() {
		return pl;
	}

	public boolean pl_updateNotification() {
		return plUn;
	}

	public boolean c_ShowHidden() {
		return clShd;
	}

	public int c_DelayTick() {
		return clDt;
	}

	public int c_Task() {
		return clT;
	}

	public int c_Border() {
		return clAb;
	}

	public boolean gr_DaylightCycle() {
		return grDdlc;
	}

	public boolean gr_EntityDrops() {
		return grDed;
	}

	public boolean gr_FireTick() {
		return grDft;
	}

	public boolean gr_MobLoot() {
		return grDml;
	}

	public boolean gr_MobSpawning() {
		return grDms;
	}

	public boolean gr_TileDrops() {
		return grDtd;
	}

	public boolean gr_MobGriefing() {
		return grMg;
	}

	public int gr_RandomTick() {
		return grRts;
	}

	public boolean gr_ReducedDebugInfo() {
		return grRdi;
	}

	public boolean gr_SpectatorsGenerateChunks() {
		return grSgc;
	}

	public boolean server() {
		return sm;
	}

	public boolean serverAdvancedMotd() {
		return smAm;
	}

	public int server_ID() {
		return smId;
	}

	public int server_MinPlayerToStart() {
		return smMpts;
	}

	public int server_MinTeamToStart() {
		return smMtts;
	}

	public int server_Countdown() {
		return smC;
	}

	public String server_SimpleMotd() {
		return smSm;
	}

	public String server_AdvancedMotd_Disabled() {
		return smAmsD;
	}

	public String server_AdvancedMotd_Error() {
		return smAmsE;
	}

	public String server_AdvancedMotd_Loading() {
		return smAmsL;
	}

	public String server_AdvancedMotd_Reset() {
		return smAmsR;
	}

	public String server_AdvancedMotd_Waiting() {
		return smAmsW;
	}

	public String server_AdvancedMotd_WaitingStart() {
		return smAmsWs;
	}

	public String server_AdvancedMotd_Starting() {
		return smAmsS;
	}

	public String server_AdvancedMotd_InGame() {
		return smAmsIg;
	}

	public String server_AdvancedMotd_Finished() {
		return smAmsF;
	}

	public String server_KickMessage_PlayerJoinError() {
		return smKmPje.replace("{N}", "\n");
	}

	public String server_KickMessage_PlayerJoinLoading() {
		return smKmPjl.replace("{N}", "\n");
	}

	public String server_KickMessage_PlayerJoinGameEnd() {
		return smKmPjg.replace("{N}", "\n");
	}

	public String server_KickMessage_PlayerKickRestart() {
		return smKmPkr.replace("{N}", "\n");
	}

	public String server_KickMessage_PlayerKickGameEnd() {
		return smKmPkg.replace("{N}", "\n");
	}

	public boolean server_BungeeCord() {
		return smBs;
	}

	public String server_BungeeCordHub() {
		return smBsFs;
	}

	public String server_BungeeCordItem() {
		return smBsI;
	}

	public int server_BungeeCordInvSlot() {
		return smBsIs;
	}

	public boolean g_teamMode() {
		return gTm;
	}

	public int g_maxTeamPlayer() {
		return gMtp;
	}

	public int g_selectTeamSlot() {
		return gStis;
	}

	public boolean go_Friendly() {
		return gOFf;
	}

	public boolean go_SeeFriendly() {
		return gOSfi;
	}

	public int go_NameTagVisibility() {
		return gONt;
	}

	public boolean tablist() {
		return tl;
	}

	public String tablist_Header() {
		return tlH;
	}

	public String tablist_Footer() {
		return tlF;
	}

	public int ws_Difficulty() {
		return wsD;
	}

	public int ws_ArenaSize() {
		return wsAs;
	}

	public int ws_SunTime() {
		return wsSt;
	}

	public int wb_StartDelay() {
		return wbSd;
	}

	public int wb_StartPos() {
		return wbSp;
	}

	public int wb_EndPos() {
		return wbEp;
	}

	public int wb_Time() {
		return wbT;
	}

	public boolean book_Enabled() {
		return bk;
	}

	public boolean book_ShowSettings() {
		return bkSs;
	}

	public int book_InventorySlot() {
		return bkIs;
	}

	public String book_Name() {
		return bkN;
	}

	public ArrayList<String> book_Lord() {
		return bkL;
	}

	public ArrayList<String> book_Pages() {
		return bkP;
	}

	public boolean gh_Enalbed() {
		return gh;
	}

	public boolean gh_DefaultApple() {
		return ghIrDa;
	}

	public boolean gh_GoldenApple() {
		return ghIrGha;
	}

	public boolean fp_Enabled() {
		return fp;
	}

	public int fp_Size() {
		return fpS;
	}

	public String mk_Message() {
		return mkM;
	}

	public int mk_Delay() {
		return mkD;
	}

	public boolean damageLog() {
		return dl;
	}

	public int of_Timeout() {
		return ofT;
	}

	public String of_Message() {
		return ofM;
	}

	public String gc_Default() {
		return gcD;
	}

	public String gc_Spectator() {
		return gcS;
	}

	public String gc_TeamDefault() {
		return gcTD;
	}

	public String gc_TeamTeam() {
		return gcTT;
	}

	public String gc_TeamPrivate() {
		return gcTPc;
	}

	// :: PUBLIC > SET, UPDATE :: //

	public void setWsSunTime(int i) {
		wsSt = i;
	}

	public void setWbStartDelay(int i) {
		wbSd = i;
	}

	public void setMkDelay(int i) {
		mkD = i;
	}

	public void setOfTimeout(int i) {
		ofT = i;
	}

	public void setWbStartPos(int i) {
		wbSp = i;
	}

	public void setWsArenaSize(int i) {
		wsAs = i;
	}

	public void setWbEndPos(int i) {
		wbEp = i;
	}

	public void setWbTimeDelay(int i) {
		wbT = i;
	}

	public void toggleNameTagVis() {
		if (gONt == 3) {
			gONt = 0;
			return;
		}
		gONt++;
	}

	public void toggleMaxTeamPlayer(boolean b) {
		if (b) {
			gMtp++;
			return;
		}
		gMtp--;
	}

	public void toggleFriendlyFire() {
		gOFf = !gOFf;
	}

	public void toggleSeeFriendlyInvs() {
		gOSfi = !gOSfi;
	}

	public void toggleGameruleDaylight() {
		grDdlc = !grDdlc;
	}

	public void toggleGameruleEntityDrops() {
		grDed = !grDed;
	}

	public void toggleGameruleFireTick() {
		grDft = !grDft;
	}

	public void toggleGameruleMobLoot() {
		grDml = !grDml;
	}

	public void toggleGameruleMobSpawn() {
		grDms = !grDms;
	}

	public void toggleGameruleTilsDrops() {
		grDtd = !grDtd;
	}

	public void toggleGameruleMobGriefing() {
		grMg = !grMg;
	}

	public void toggleGameruleTickSpeed(boolean b) {
		if (b) {
			grRts++;
			return;
		}
		grRts--;
	}

	public void toggleGameruleDebugInfo() {
		grRdi = !grRdi;
	}

	public void toggleGameruleSpectatorsGenerateChunks() {
		grSgc = !grSgc;
	}

	public void toggleSettingsFreezePlayer() {
		fp = !fp;
	}

	public void toggleSettingsDamageLog() {
		dl = !dl;
	}

	public void toggleSettingsGoldenHead() {
		gh = !gh;
	}

	public void toggleSettingsWorldDifficulty() {
		if (wsD == 3) {
			wsD = 1;
			return;
		}
		wsD++;
	}

	public void toggleSettingsFreezePlayerSize(boolean b) {
		if (b) {
			fpS++;
			return;
		}
		fpS--;
	}

	public void toggleSettingsGoldenHeadDefault() {
		ghIrDa = !ghIrDa;
	}

	public void toggleSettingsGoldenHeadGold() {
		ghIrGha = !ghIrGha;
	}

	// :: PRIVATE :: //

	private boolean gcb(boolean b, String s) {
		if (fC.isBoolean(s)) {
			return fC.getBoolean(s);
		}
		confLog(0, s, 0, 0);
		return b;
	}

	private int gci(int i, String s, int imm, int imx) {
		if (fC.isInt(s)) {
			int v = fC.getInt(s);
			if (v >= imm && v <= imx) {
				return v;
			}
			confLog(2, s, imm, imx);
			return i;
		}
		confLog(0, s, 0, 0);
		return i;
	}

	private String gcs(String m, String s, boolean b) {
		if (fC.isString(s)) {
			String v = fC.getString(s);
			if (b) {
				if (v.length() == 0) {
					confLog(1, s, 0, 0);
					return m;
				}
			}
			return v;
		}
		confLog(0, s, 0, 0);
		return m;
	}

	private String gcsc(String m, String s, boolean b) {
		if (fC.isString(s)) {
			String v = fC.getString(s);
			if (b) {
				if (v.length() == 0) {
					confLog(1, s, 0, 0);
					return m;
				}
			}
			return f.MinecraftColor(v, false);
		}
		confLog(0, s, 0, 0);
		return m;
	}

	private ArrayList<String> gcl(int i, String s) {
		ArrayList<String> al = new ArrayList<>();
		if (fC.isList(s)) {
			if (fC.getStringList(s).size() != 0) {
				for (String l : fC.getStringList(s)) {
					al.add(f.MinecraftColor(l, true));
				}
			}
			return al;
		}
		confLog(0, s, 0, 0);
		return gcm(i, al);
	}

	private ArrayList<String> gcm(int i, ArrayList<String> al) {
		if (i == 0) {
			al.add("");
			al.add("§6- §7Info");
			al.add("§6- §7Rules");
		} else if (i == 1) {
			al.add("Welcome to §4§lUHC§0.\n\nThis game you can only regenerate health by\n §8- §1Golden Apple.§r\n §8- §1Potions.§r\n\nI wish you §5Good Luck§r\nand may the best player / team win.");
			al.add("§l§n    UHC - Rules     §r\n\n§11.§r Branch Mining\n§8 You can not branch\n mining but if you\n hear a sound,\n you can dig to it.§r\n\n§12. Staircases§r\n§8 You can only dig\n staircases if you\n want to find a cave.");
		}
		return al;
	}

	private void confLog(int i, String s, int mn, int mx) {
		String m;
		switch (i) {
		case 1:
			m = "'" + s + "' can not be empty.";
			break;
		case 2:
			m = "'" + s + "' can only be [" + mn + " - " + mx + "]";
			break;

		default:
			m = "Could not load '" + s + "'";
			break;
		}
		l.warn("[CONFIG] " + m);
	}
}