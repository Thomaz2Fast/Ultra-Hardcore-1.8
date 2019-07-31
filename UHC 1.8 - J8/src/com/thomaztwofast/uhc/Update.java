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

package com.thomaztwofast.uhc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Update {
	private final String URL_UPDATE = "https://servermods.forgesvc.net/servermods/files?projectIds=88234";
	private final String URL_FILES = "https://dev.bukkit.org/projects/ultra-hardcore-1-8/files";
	private Main pl;
	private boolean[] bool = { false, false };
	private String version = "";

	public Update(Main pl) {
		this.pl = pl;
	}

	public void checkForUpdate() {
		if (pl.config.pluingUpdate && !bool[0]) {
			bool[0] = true;
			log(0, "Checking for updates...");
			new Thread(new Runnable() {
				public void run() {
					try {
						HttpURLConnection http = (HttpURLConnection) new URL(URL_UPDATE).openConnection();
						if (http.getResponseCode() == 200) {
							BufferedReader buffer = new BufferedReader(new InputStreamReader(http.getInputStream()));
							String bufferData = "", line = "";
							while ((line = buffer.readLine()) != null) {
								bufferData += line;
							}
							buffer.close();
							if (bufferData.length() != 0) {
								JSONArray jsArr = (JSONArray) new JSONParser().parse(bufferData);
								JSONObject jsObj = (JSONObject) jsArr.get(jsArr.size() - 1);
								if (jsObj.containsKey("name")) {
									version = jsObj.get("name").toString().replaceAll("[^0-9]", "");
									if (Integer.parseInt(pl.getDescription().getVersion().replace(".", "")) < Integer.parseInt(version)) {
										bool[1] = true;
										version = jsObj.get("name").toString();
										log(2, "New version is available. (" + version + ")");
										log(2, URL_FILES);
									} else {
										log(0, "You are running the latest version.");
									}
								} else
									log(1, "Response from the web server is missing something...");
							} else
								log(1, "Response from the web server was empty...");
						} else
							log(1, "Was unable to get response from the web server. HTTP (" + http.getResponseCode() + ")");
						http.disconnect();
					} catch (Exception err) {
						log(1, "Was unable to connect to the web server...");
					}
				}
			}).start();
		}
	}

	public String[] getInformation() {
		return new String[] { version.replaceAll("[^0-9.]", ""), URL_FILES };
	}

	public boolean isNew() {
		return bool[1];
	}

	// ---------------------------------------------------------------------------

	private void log(int lvl, String log) {
		pl.log(lvl, "[UPDATE] " + log);
	}
}
