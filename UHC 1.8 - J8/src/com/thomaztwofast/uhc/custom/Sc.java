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

package com.thomaztwofast.uhc.custom;

import java.util.ArrayList;
import java.util.List;

public class Sc {
	private boolean sA = false;
	private String sB = "\u00A78\u00A7m--------------------------------------------\u00A7r";
	private String sC;
	private List<String> sD = new ArrayList<>();

	public void setConsole() {
		sA = true;
	}

	public void setTitle(String a) {
		sC = a;
	}

	public void addHeader(String a) {
		sD.add(" \n\u00A76 " + a + "\n");
	}

	public void addTextLn(String a) {
		sD.add("\u00a7a " + a + "\n");
	}

	public void addListLn(String a, String b) {
		sD.add("\u00a7a " + a + ":\u00a77 " + b + "\n");
	}

	public String o() {
		StringBuilder a = new StringBuilder();
		a.append((sA ? "\n" : "") + sB + "\n\u00A7l " + sC + "\n");
		for (String b : sD) {
			a.append(b);
		}
		a.append(sB);
		return a.toString();
	}
}
