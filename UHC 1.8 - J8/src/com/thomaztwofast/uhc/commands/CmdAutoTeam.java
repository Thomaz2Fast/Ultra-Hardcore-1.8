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
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.thomaztwofast.uhc.Main;
import com.thomaztwofast.uhc.data.Node;
import com.thomaztwofast.uhc.data.UHCPlayer;
import com.thomaztwofast.uhc.lib.F;
import com.thomaztwofast.uhc.lib.J;

public class CmdAutoTeam implements CommandExecutor {
	private Main pl;
	private Scoreboard scoreboard;

	public CmdAutoTeam(Main pl) {
		this.pl = pl;
		scoreboard = pl.getServer().getScoreboardManager().getMainScoreboard();
	}

	/*
	 * Command - - - - - - - - - - > - AutoTeam
	 * Access  - - - - - - - - - - > - Private Mode
	 * Who can use it  - - - - - - > - Players
	 * Arguments - - - - - - - - - > - None
	 * 
	 * Default Permission  - - - - > - OP
	 * Permission Node - - - - - - > - com.thomaztwofast.uhc.commands.autoteam [ See plugin.yml ]
	 * 
	 * ----------------------------
	 * 
	 * AutoTeam select all players on the server and then put them on a random teams.
	 * All previous teams players will be removed in the progress. (All fake players
	 * will be lost)
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			UHCPlayer u = pl.getRegisterPlayer(sender.getName());
			if (pl.config.pluginEnable && pl.config.gameInTeam && !pl.config.serverEnable) {
				if (pl.status.ordinal() <= 5) {
					new Thread(new Runnable() {
						public void run() {
							List<UHCPlayer> players = new ArrayList<>(pl.PLAYERS.values());
							List<Team> teams = getSelectedTeams(players.size());
							Random rand = new Random();
							int teamPos = 0;
							while (players.size() != 0) {
								UHCPlayer t = players.get(rand.nextInt(players.size()));
								pl.gameManager.teams.join(t, teams.get(teamPos).getName(), true);
								players.remove(t);
								teamPos = teamPos == teams.size() - 1 ? 0 : teamPos + 1;
							}
							pl.gameManager.teams.updateInv();
							u.sendCmdMessage("AutoTeam", "Done.");
						}

						private List<Team> getSelectedTeams(int value) {
							List<Team> teams = new ArrayList<>();
							int max = (int) Math.ceil(value / pl.config.gameMaxTeam);
							max = max == 0 ? 1 : max > pl.config.gameTeamNames.size() ? pl.config.gameTeamNames.size() : max;
							for (String tm : pl.config.gameTeamNames) {
								Team team = scoreboard.getTeam(tm.split("\\|")[0].replace(" ", "_"));
								if (team != null) {
									team.getEntries().forEach(e -> team.removeEntry(e));
									teams.add(team);
								}
							}
							Collections.shuffle(teams);
							return teams.subList(0, max);
						}
					}).start();
					return true;
				}
			}
			J str = new J("AutoTeam> ", "8", "bi");
			if (u.hasNode(Node.UHC))
				str.addTextWithCmd("Disabled!", "7", "i", "/uhc help page 2", F.chatTitle("Help Information", "Click here to find out how to enable this command?"));
			else
				str.addText("Disabled!", "7", "i");
			u.sendJsonMessage(str.print());
			return true;
		}
		pl.log(0, "Command 'AutoTeam' can only execute from player.");
		return true;
	}
}
