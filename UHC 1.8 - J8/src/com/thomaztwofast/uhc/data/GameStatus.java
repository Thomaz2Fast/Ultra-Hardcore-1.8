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

public enum GameStatus {
	DISABLED(0, "Disabled"),
	ERROR(1, "Error"),
	LOADING(2, "Loading"),
	RESET(3, "Restarting"),
	WAITING(4, "Waiting"),
	WAITING_STARTING(5, "Waiting + Countdown"),
	STARTING(6, "Starting"),
	INGAME(7, "InGame"),
	FINISHED(8, "Finished");

	private int a;
	private String b;

	GameStatus(int a1, String b1) {
		a = a1;
		b = b1;
	}

	@Override
	public String toString() {
		return b;
	}

	public int i() {
		return a;
	}
}
