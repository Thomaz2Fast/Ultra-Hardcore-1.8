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

/**
 * JChat Generator
 * @version 0.1.6
 */
public class JChat {
	private ArrayList<String> ar = new ArrayList<>();
	private boolean sB1 = false;
	private boolean sB2 = false;
	private boolean sI1 = false;
	private boolean sI2 = false;
	private boolean sU1 = false;
	private boolean sU2 = false;
	private boolean sS1 = false;
	private boolean sS2 = false;
	private boolean sO1 = false;
	private boolean sO2 = false;

	/**
	 * JChat > Format
	 * 
	 * @param 	text 		> 	Plain text.
	 * @param 	textStyle	>	Text Style. [0-4]
	 * 						-	0	=	Bold
	 * 						-	1	=	Italic
	 * 						-	2	=	Underlined
	 * 						-	3	=	Strikethrough
	 * 						-	4	=	Obfuscated
	 * 
	 * @param color			>	Controls the color of the text. [0-15]
	 * 						-	0  -> 9		=	0 -> 9
	 * 						-	10 -> 15	=	a -> f
	 * 
	 * @param clickEvent	>	Executes the action once the text is clicked. [0-3]
	 * 						|	Format: Action|Value
	 * 						-	0	=	open_url
	 * 						-	1	=	open_file
	 * 						-	2	=	run_command
	 * 						-	3	=	suggest_command
	 * 
	 * @param hoverText		>	Displays the action upon hovering over the text.
	 */
	public void add(String text, int[] textStyle, int color, String clickEvent, String hoverText) {
		if (text == null) {
			return;
		}
		String s = "{\"text\":\"" + text + "\"";
		if (textStyle != null) {
			for (int ts : textStyle) {
				switch (ts) {
				case 0:
					s += ",\"bold\":true";
					sB1 = true;
					sB2 = false;
					break;
				case 1:
					s += ",\"italic\":true";
					sI1 = true;
					sI2 = false;
					break;
				case 2:
					s += ",\"underlined\":true";
					sU1 = true;
					sU2 = false;
					break;
				case 3:
					s += ",\"strikethrough\":true";
					sS1 = true;
					sS2 = false;
					break;
				case 4:
					s += ",\"obfuscated\":true";
					sO1 = true;
					sO2 = false;
					break;
				}
			}
		}
		if (sB2) {
			s += ",\"bold\":false";
			sB2 = false;
		}
		if (sI2) {
			s += ",\"italic\":false";
			sI2 = false;
		}
		if (sU2) {
			s += ",\"underlined\":false";
			sU2 = false;
		}
		if (sS2) {
			s += ",\"strikethrough\":false";
			sS2 = false;
		}
		if (sO2) {
			s += ",\"obfuscated\":false";
			sO2 = false;
		}
		sB2 = sB1;
		sI2 = sI1;
		sU2 = sU1;
		sS2 = sS1;
		sO2 = sO1;
		sB1 = false;
		sI1 = false;
		sU1 = false;
		sS1 = false;
		sO1 = false;

		s += ",\"color\":";
		switch (color) {
		case 0:
			s += "\"black\"";
			break;
		case 1:
			s += "\"dark_blue\"";
			break;
		case 2:
			s += "\"dark_green\"";
			break;
		case 3:
			s += "\"dark_aqua\"";
			break;
		case 4:
			s += "\"dark_red\"";
			break;
		case 5:
			s += "\"dark_purple\"";
			break;
		case 6:
			s += "\"gold\"";
			break;
		case 7:
			s += "\"gray\"";
			break;
		case 8:
			s += "\"dark_gray\"";
			break;
		case 9:
			s += "\"blue\"";
			break;
		case 10:
			s += "\"green\"";
			break;
		case 11:
			s += "\"aqua\"";
			break;
		case 12:
			s += "\"red\"";
			break;
		case 13:
			s += "\"light_purple\"";
			break;
		case 14:
			s += "\"yellow\"";
			break;
		default:
			s += "\"white\"";
			break;
		}
		if (clickEvent != null) {
			String[] ce = clickEvent.split("\\|");
			s += ",\"clickEvent\":{";
			switch (ce[0]) {
			case "0":
				s += "\"action\":\"open_url\",";
				break;
			case "1":
				s += "\"action\":\"open_file\",";
				break;
			case "2":
				s += "\"action\":\"run_command\",";
				break;
			default:
				s += "\"action\":\"suggest_command\",";
				break;
			}
			s += "\"value\":\"" + ce[1] + "\"}";
		}
		if (hoverText != null) {
			s += ",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"" + hoverText + "\"}";
		}
		ar.add(s + "}");
	}

	/**
	 * JChat > Output
	 */
	public String a() {
		String s = "[\"\",";
		int a = 1;
		for (String t : ar) {
			s += t;
			if (ar.size() != a) {
				s += ",";
				a++;
			}
		}
		return s + "]";
	}
}