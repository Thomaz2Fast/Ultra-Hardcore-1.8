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

package com.thomaztwofast.uhc.lib;

import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.ChatColor;

public class J {
	private final ArrayList<String> CSS = new ArrayList<>(Arrays.asList("bold", "italic", "obfuscated", "strikethrough", "underlined"));
	private ArrayList<String> data = new ArrayList<>();

	public J(String str, String c, String css) {
		addText(str, c, css);
	}

	public void addText(String str, String c, String css) {
		data.add(setStyle(str, c, css) + "}");
	}

	public void addTextWithCmd(String str, String c, String css, String cmd, String hoverStr) {
		StringBuilder sb = setStyle(str, c, css);
		sb.append(setClickEvent("run_command", cmd));
		sb.append(setHoverEvent("show_text", hoverStr));
		data.add(sb + "}");
	}

	public void addTextWithHoverText(String str, String c, String css, String hoverStr) {
		StringBuilder sb = setStyle(str, c, css);
		sb.append(setHoverEvent("show_text", hoverStr));
		data.add(sb + "}");
	}

	public void addTextWithURL(String str, String c, String css, String url, String hoverStr) {
		StringBuilder sb = setStyle(str, c, css);
		sb.append(setClickEvent("open_url", url));
		sb.append(setHoverEvent("show_text", hoverStr));
		data.add(sb + "}");
	}

	public String print() {
		return "[\"\"," + String.join(",", data) + "]";
	}

	// ---------------------------------------------------------------------------

	private String setClickEvent(String e, String str) {
		return ",\"clickEvent\":{\"action\":\"" + e + "\",\"value\":\"" + str + "\"}";
	}

	private String setHoverEvent(String e, String str) {
		return ",\"hoverEvent\":{\"action\":\"" + e + "\",\"value\":\"" + str + "\"}";
	}

	private StringBuilder setStyle(String str, String c, String css) {
		StringBuilder sb = new StringBuilder("{\"text\":\"" + str + "\"");
		sb.append(",\"color\":\"" + ChatColor.getByChar(c).name().toLowerCase() + "\"");
		CSS.forEach(e -> sb.append(",\"" + e + "\":" + (css.indexOf(e.charAt(0)) != -1 ? true : false)));
		return sb;
	}
}
