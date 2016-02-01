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

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.thomaztwofast.uhc.Main;

public class UpdateNotification {
	private boolean b = false;
	private JChat d = new JChat();
	private String v = "";
	private String l = "";

	public UpdateNotification(Main pl) {
		pl.getPlLog().info("[UPDATE CHECKING] Connecting...");
		try {
			URLConnection con = new URL("http://dev.bukkit.org/bukkit-plugins/ultra-hardcore-1-8/files.rss").openConnection();
			con.setConnectTimeout(1500);
			InputStream i = con.getInputStream();
			Document dc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(i);
			Node n = dc.getElementsByTagName("item").item(0);
			NodeList cl = n.getChildNodes();
			v = cl.item(1).getTextContent().replaceAll("[^0-9.]", "");
			if (isNewVersion(pl.getDescription().getVersion(), v)) {
				l = cl.item(3).getTextContent();
				d.add("--------------------------------------------", new int[] { 3 }, 8, null, null);
				d.add("\n New Update Available [", new int[] { 0 }, 15, null, null);
				d.add(v, new int[] { 0 }, 14, null, null);
				d.add("]\n \n", new int[] { 0 }, 15, null, null);
				d.add(" Change Log\n", null, 6, null, null);
				d.add(dm(cl.item(5).getTextContent().replaceAll("\\<.*?>", "")), null, 7, null, null);
				d.add(" Link\n", null, 6, null, null);
				d.add(" " + l, null, 10, "0|" + l, "§7Go to this webpage?\n§a" + l);
				d.add("--------------------------------------------", new int[] { 3 }, 8, null, null);
				b = true;
			}
			i.close();
		} catch (Exception e) {
			pl.getPlLog().warn("[UHC] [UPDATE CHECKING] Could not reach the web page...");
			pl.getPlLog().warn("[UHC] [UPDATE CHECKING] " + e.getMessage());
		}
	}

	/**
	 * Get > New Version
	 */
	public String getVersion() {
		return v;
	}

	/**
	 * Get > Change log message
	 */
	public String getMessage() {
		return d.a();
	}

	/**
	 * Is > New update?
	 */
	public boolean isNewUpdate() {
		return b;
	}

	// :: PRIVATE :: //

	/**
	 * Change Log layout
	 */
	private String dm(String s) {
		String o = "";
		int i = 0;
		for (String l : s.split("\n")) {
			if (l.equals("Requirements")) {
				break;
			} else if (i == 10) {
				o += "\n §7Read more about this update? Click the link below.\n \n";
				break;
			}
			l = l.replaceFirst("Added:", "§a§l+§7");
			l = l.replaceFirst("Fixed:", "§e§l#§7");
			l = l.replaceFirst("Remove:", "§c§l-§7");
			l = l.replace("(", "§8(").replace(")", ")§7");
			if (l.length() > 55) {
				o += " " + l.substring(0, 53) + "...\n";
			} else {
				o += " " + l + "\n";
			}
			i++;
		}
		return o;
	}

	/**
	 * Check > Is there new version?
	 */
	private boolean isNewVersion(String ov, String nv) {
		if (Integer.parseInt(nv.replace(".", "")) > Integer.parseInt(ov.replace(".", ""))) {
			return true;
		}
		return false;
	}
}