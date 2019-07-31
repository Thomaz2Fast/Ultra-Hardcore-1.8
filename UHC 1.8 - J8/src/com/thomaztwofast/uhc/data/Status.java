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

public enum Status {
	DISABLED("Disabled"),
	ERROR("Error"),
	LOADING("Loading"),
	RESET("Reset"),
	WAITING("Waiting"),
	WAITING_STARTING("Waiting + Countdown"),
	STARTING("Starting"),
	INGAME("InGame"),
	FINISHED("Finished");

	private final String name;

	private Status(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
