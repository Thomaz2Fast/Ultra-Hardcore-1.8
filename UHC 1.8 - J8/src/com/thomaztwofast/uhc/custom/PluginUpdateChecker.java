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

import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.thomaztwofast.uhc.Main;

public class PluginUpdateChecker {
	private Main pA;
	public String pBa;
	public boolean pBb = false;
	private String pBc;
	public Jc pBd = new Jc();

	public PluginUpdateChecker(Main a) {
		pA = a;
	}

	public void updateCheck() {
		d("Checking version, please wait...", false);
		try {
			URLConnection a = new URL("http://dev.bukkit.org/bukkit-plugins/ultra-hardcore-1-8/files.rss").openConnection();
			a.setConnectTimeout(1500);
			Document b = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(a.getInputStream());
			NodeList c = b.getElementsByTagName("item").item(0).getChildNodes();
			pBa = c.item(1).getTextContent().replaceAll("[^0-9.]", "");
			if (b(pA.getDescription().getVersion(), pBa)) {
				pBc = c.item(3).getTextContent();
				pBd.add("--------------------------------------------", new int[] { 3 }, 8, null, null);
				pBd.add("\n NEW UPDATE AVAILEBLE ", new int[] { 0 }, 15, null, null);
				pBd.add("[", new int[] { 0 }, 8, null, null);
				pBd.add(pBa, new int[] { 0 }, 14, null, null);
				pBd.add("]\n \n", new int[] { 0 }, 8, null, null);
				pBd.add(" Change Log\n", null, 6, null, null);
				a(c.item(5).getTextContent().replaceAll("\\<.*?>", ""));
				pBd.add(" Link\n", null, 6, null, null);
				pBd.add(" " + pBc + "\n", null, 10, "0|" + pBc, "\u00a76\u00a7lGo to this webpage?\u00a7r");
				pBd.add("--------------------------------------------", new int[] { 3 }, 8, null, null);
				d("New version is available. Type '/uhc' for more info or click the link below.", false);
				d(pBc, false);
			}
			a.getInputStream().close();
		} catch (Exception e) {
			d("Error! Something went wrong here :(", true);
			d("Error message: " + e.getMessage(), true);
		}
	}

	// ------:- PRIVATE -:--------------------------------------------------------------------------

	private void a(String a) {
		int b = 0;
		for (String c : a.split("\n")) {
			if (c.equals("Requirements")) {
				return;
			} else if (b == 9) {
				pBd.add(" \n Read more about this update? Click the link below.\n \n", null, 7, null, null);
				return;
			}
			if (c.startsWith("Added:")) {
				pBd.add(" +", new int[] { 0 }, 10, null, null);
				c = c.substring(7);
			} else if (c.startsWith("Fixed:")) {
				pBd.add(" #", new int[] { 0 }, 14, null, null);
				c = c.substring(7);
			} else if (c.startsWith("Remove:")) {
				pBd.add(" -", new int[] { 0 }, 12, null, null);
				c = c.substring(8);
			}
			pBd.add(" " + c(c) + "\n", null, 7, null, null);
			b++;
		}
	}

	private boolean b(String a, String b) {
		if (Integer.parseInt(b.replace(".", "")) > Integer.parseInt(a.replace(".", ""))) {
			pBb = true;
			return true;
		}
		return false;
	}

	private String c(String a) {
		a = a.replace("(", "\u00a78(").replace(")", ")\u00a77");
		if (a.length() > 55) {
			return a.substring(0, 53) + "...";
		}
		return a;
	}

	private void d(String a, boolean b) {
		pA.log((b ? 1 : 0), "[UPDATE] " + a);
	}
}
