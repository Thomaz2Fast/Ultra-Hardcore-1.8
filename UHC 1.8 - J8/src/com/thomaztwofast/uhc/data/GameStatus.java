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

public class GameStatus {
	private Stat st = Stat.DISABLED;

	/**
	 * Enum > Status
	 */
	public enum Stat {
		DISABLED(0, true, "Disabled"),
		ERROR(1, true, "Error"),
		LOADING(2, true, "Loading"),
		RESET(3, true, "Restarting"),
		WAITING(4, true, "Waiting"),
		WAITING_STARTING(5, true, "Waiting + Countdown"),
		STARTING(6, false, "Starting"),
		INGAME(7, false, "InGame"),
		FINISHED(8, true, "Finished");

		private boolean sB;
		private int sI;
		private String sS;

		Stat(int i, boolean b, String s) {
			sI = i;
			sB = b;
			sS = s;
		}

		@Override
		public String toString() {
			return sS;
		}

		/**
		 * Is > Status Active?
		 */
		public boolean isActive() {
			return sB;
		}

		/**
		 * Get > Status Level
		 */
		public int getLvl() {
			return sI;
		}
	}

	/**
	 * Get > Status
	 */
	public Stat getStat() {
		return st;
	}

	/**
	 * Set > New Status
	 */
	public void setStat(Stat stat) {
		st = stat;
	}
}