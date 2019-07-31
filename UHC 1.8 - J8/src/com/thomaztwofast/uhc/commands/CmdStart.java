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

package com.thomaztwofast.uhc.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.StringUtil;

import com.thomaztwofast.uhc.Main;
import com.thomaztwofast.uhc.data.Node;
import com.thomaztwofast.uhc.data.UHCPlayer;
import com.thomaztwofast.uhc.lib.F;
import com.thomaztwofast.uhc.lib.J;

public class CmdStart implements CommandExecutor, TabCompleter {
	private Main pl;
	private List<String> names = new ArrayList<>();

	public CmdStart(Main pl) {
		this.pl = pl;
	}

	/*
	 * Command - - - - - - - - - - > - Start
	 * Access  - - - - - - - - - - > - Private Mode / Server Mode
	 * Who can use it  - - - - - - > - Players
	 * Arguments - - - - - - - - - > - 
	 *    ID               NAME                TAB COMPLETE
	 *    115760           uhc                 true
	 *   -1166190107       notify-player       false
	 * 
	 * Default Permission  - - - - > - OP
	 * Permission Node - - - - - - > - com.thomaztwofast.uhc.commands.start       [ See plugin.yml ]
	 * 
	 * ----------------------------
	 * 
	 * Start the Ultra Hardcore game.
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			UHCPlayer u = pl.getRegisterPlayer(sender.getName());
			if (pl.config.pluginEnable && pl.status.ordinal() == 4) {
				if (args.length != 0) {
					switch (args[0].toLowerCase().hashCode()) {
					case -1166190107:
						if (pl.config.gameInTeam && args.length == 2) {
							if (pl.PLAYERS.containsKey(args[1]) && u.player.getScoreboard().getEntryTeam(args[1]) == null) {
								UHCPlayer t = pl.getRegisterPlayer(args[1]);
								u.sendActionMessage("Send notification to \u00A7E" + t.player.getName());
								t.sendCmdMessage("UHC", "This game is about to start. Please select a team if you want to join the UHC or else you will be spectating the game.");
								t.playSound(Sound.UI_TOAST_IN, 1f);
							}
						}
						return true;
					case 115760:
						if (names.contains(sender.getName()) && getEntrieSize() > 1) {
							names.clear();
							if (pl.config.serverEnable) {
								pl.gameManager.server.forceStart(sender.getName());
								return true;
							}
							pl.gameManager.startGame(getEntrieSize());
							return true;
						}
					}
				}
				List<String> entries = pl.config.gameInTeam ? getTeams() : getSolo();
				J str = new J("                                                                   \n", "8", "s");
				str.addText(" ULTRA HARDCORE 1.8 - " + (pl.config.gameInTeam ? "TEAM" : "SOLO") + " MODE\n\n", "f", "b");
				str.addText(" Total " + (pl.config.gameInTeam ? "Teams" : "Players") + ": ", "a", "");
				str.addText(entries.get(0) + "\n", "7", "");
				if (pl.config.gameInTeam) {
					if (entries.size() > 1) {
						str.addText("\n Warning\n", "e", "");
						str.addText(" There " + ((entries.size() - 1) > 1 ? "are " + (entries.size() - 1) + " players" : "is 1 player") + " which is not in the team yet.\n", "7", "");
						str.addText(" Player" + ((entries.size() - 1) > 1 ? "s" : "") + ": ", "7", "");
						for (int d = 1; d < entries.size(); d++) {
							if (d > 1)
								str.addText(", ", "7", "");
							String name = entries.get(d);
							str.addTextWithCmd(name, "e", "", "/start notify-player " + name, "\u00A77Send notification?");
						}
						str.addText("\n\n For those who have not in the team yet, will be move to spectator mode.\n", "8", "i");
					}
				}
				if (Integer.valueOf(entries.get(0)) < 2) {
					str.addText("\n Error\n", "c", "");
					str.addText(" Not enough " + (pl.config.gameInTeam ? "teams" : "players") + " to start the game.\n", "7", "");
					str.addText(" Required 2 or more " + (pl.config.gameInTeam ? "teams" : "players") + ".\n", "7", "");
					names.clear();
				} else {
					str.addText("\n If everyone is ready to start?\n", "7", "");
					str.addText(" Type the command below to start the game.\n", "7", "");
					str.addTextWithCmd(" /start uhc\n", "8", "", "/start uhc", F.chatCmd("start uhc"));
					if (!names.contains(sender.getName()))
						names.add(sender.getName());
				}
				str.addText("                                                                   ", "8", "s");
				u.sendJsonMessage(str.print());
				return true;
			}
			J b = new J("Start> ", "8", "bi");
			if (u.hasNode(Node.UHC))
				b.addTextWithCmd("Disabled!", "7", "i", "/uhc help page 0", F.chatTitle("Help Information", "Click here to find out how to enable this command?"));
			else
				b.addText("Disabled!", "7", "i");
			u.sendJsonMessage(b.print());
			return true;
		}
		pl.log(0, "Command 'Start' can only execute from player.");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player && pl.config.pluginEnable && args.length == 1)
			return StringUtil.copyPartialMatches(args[0], Arrays.asList("uhc"), new ArrayList<>(1));
		return null;
	}

	// ---------------------------------------------------------------------------

	private int getEntrieSize() {
		return Integer.parseInt((pl.config.gameInTeam ? getTeams().get(0) : getSolo().get(0)));
	}

	private List<String> getSolo() {
		List<String> arr = new ArrayList<>();
		int i = 0;
		for (UHCPlayer u : pl.PLAYERS.values())
			if (u.player.getGameMode() == GameMode.ADVENTURE)
				i++;
		arr.add(i + "");
		return arr;
	}

	private List<String> getTeams() {
		List<String> arr = new ArrayList<>();
		Scoreboard scoreboard = pl.getServer().getScoreboardManager().getMainScoreboard();
		int i = 0;
		for (String tm : pl.config.gameTeamNames) {
			Team team = scoreboard.getTeam(tm.split("\\|")[0].replace(" ", "_"));
			if (team != null && team.getSize() != 0)
				i++;
		}
		arr.add(i + "");
		pl.PLAYERS.values().forEach(e -> {
			if (scoreboard.getEntryTeam(e.player.getName()) == null && e.player.getGameMode() == GameMode.ADVENTURE)
				arr.add(e.player.getName());
		});
		return arr;
	}
}
