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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Husk;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.TippedArrow;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.BlockProjectileSource;

import com.thomaztwofast.uhc.data.UHCPlayer;
import com.thomaztwofast.uhc.lib.F;

public class DamagerLogger {
	private Main pl;
	private HashMap<String, HashMap<Integer, Inventory>> invStorages = new HashMap<>();

	public DamagerLogger(Main pl) {
		this.pl = pl;
	}

	public void click(UHCPlayer u, ClickType click, int slot, boolean b) {
		if (b && click.equals(ClickType.LEFT)) {
			switch (slot) {
			case 45:
				changeInv(u, u.invHistory[1] - 1);
				return;
			case 53:
				changeInv(u, u.invHistory[1] + 1);
				return;
			}
		}
	}

	public String[] getDamager(EntityDamageEvent e) {
		switch (e.getEventName().hashCode()) {
		case -438677650:
			return getEntity((EntityDamageByEntityEvent) e);
		case 238032950:
			return o(uBlockName(e.getCause()));
		default:
			return o(uEnvName(e.getCause()));
		}
	}

	public void giveItem(String name) {
		UHCPlayer u = pl.getRegisterPlayer(name);
		if (u != null) {
			if (new File(pl.getDataFolder(), "data/" + u.player.getUniqueId() + ".yml").exists()) {
				u.sendCmdMessage("DamagerLogger", "Click on the \u00A7E\u00A7ONether Star\u00A77\u00A7O item to see how has damage you.");
				u.player.getInventory().setItem(4, F.item(Material.NETHER_STAR, "&cDamager Logger", "0|&7List of damage you have been", "0|&7taken from this UHC game."));
			}
		}
	}

	public void openInv(UHCPlayer u) {
		if (invStorages.containsKey(u.player.getName())) {
			u.player.openInventory(invStorages.get(u.player.getName()).get(u.invHistory[1] == -1 ? 0 : u.invHistory[1]));
			u.setInvKey(2, u.invHistory[1]);
			u.playSound(Sound.UI_BUTTON_CLICK, 1.2f);
			return;
		}
		File a = new File(pl.getDataFolder(), "data/" + u.player.getUniqueId() + ".yml");
		if (!a.exists())
			return;
		new Thread(new Runnable() {
			public void run() {
				HashMap<Integer, Inventory> inventorys = new HashMap<>();
				FileConfiguration yaml = YamlConfiguration.loadConfiguration(a);
				int[] data = { (int) Math.ceil(yaml.getInt("i") / 45d), 0, 10, 0, 0 };
				double totalDmg = 0.0;
				for (int i = 0; i < data[0]; i++) {
					Inventory inventory = pl.getServer().createInventory(null, 54, "DMG> " + u.player.getName() + "> " + (i + 1));
					for (int j = 0; j < 45; j++) {
						List<String> lore = new ArrayList<>();
						lore.add("0|&7From: &a" + yaml.getString("d." + data[1] + ".f"));
						if (yaml.getString("d." + data[1] + ".i").length() != 0)
							lore.add("0|&7Using: &e" + yaml.getString("d." + data[1] + ".i"));
						lore.add("0|&7Dmg: &c" + String.format("%.1f", yaml.getDouble("d." + data[1] + ".d")));
						totalDmg += yaml.getDouble("d." + data[1] + ".d");
						inventory.setItem(j, F.item(Material.PAPER, "&6&l" + (data[1] + 1) + ": &r&e&l" + F.getTimeLeft(yaml.getLong("d." + data[1] + ".t") / 1000), lore.toArray(new String[0])));
						data[1]++;
						if (!yaml.isConfigurationSection("d." + data[1]))
							break;
					}
					if (i != 0 && i + 1 <= data[0])
						inventory.setItem(45, F.item(Material.RED_STAINED_GLASS_PANE, "&c&lPrevious Page"));
					if (data[0] > (i + 1))
						inventory.setItem(53, F.item(Material.GREEN_STAINED_GLASS_PANE, "&a&lNext page"));
					inventorys.put(i, inventory);
				}
				a.delete();
				for (Inventory e : inventorys.values())
					e.setItem(49, F.item(Material.PURPLE_STAINED_GLASS_PANE, "&e&lTotal Damage", "0|&7Your total damage:", "0|&c " + String.format("%.1f", totalDmg)));
				invStorages.put(u.player.getName(), inventorys);
				changeInv(u, 0);
			}
		}).start();
	}

	// ---------------------------------------------------------------------------

	private void changeInv(UHCPlayer u, int invID) {
		u.player.openInventory(invStorages.get(u.player.getName()).get(invID));
		u.playSound(Sound.UI_BUTTON_CLICK, 1.2f);
		u.setInvKey(2, invID);
	}

	private String[] getEntity(EntityDamageByEntityEvent e) {
		switch (e.getCause()) {
		case ENTITY_ATTACK:
			if (e.getDamager() instanceof Player) {
				Player p = (Player) e.getDamager();
				if (e.getEntity().equals(p) && p.getInventory().getItemInMainHand().getType().equals(Material.AIR))
					if (e.getDamage() == 6d || e.getDamage() == 12d) // TODO A better way to detect / filter drinking potions.
						return o(p.getName(), "Drinking Harm " + (e.getDamage() == 6d ? "I" : "II"));
				return o(p.getName(), uItemName(p.getInventory().getItemInMainHand().getType()));
			}
		case ENTITY_EXPLOSION:
			return o(uEntityName(e.getDamager()));
		case FALL:
			return o("Ender Pearl (Teleport)");
		case FALLING_BLOCK:
			return o("Falling Anvil");
		case LIGHTNING:
			return o("Lightning");
		case MAGIC:
			ThrownPotion potion = (ThrownPotion) e.getDamager();
			if (potion.getShooter() instanceof Player)
				return o(((Player) potion.getShooter()).getName(), uPotions(potion.getEffects()));
			else if (potion.getShooter() instanceof BlockProjectileSource)
				return o(uBlockName((BlockProjectileSource) potion.getShooter()), uPotions(potion.getEffects()));
			return o(uEntityName((Entity) potion.getShooter()), uPotions(potion.getEffects()));
		case PROJECTILE:
			Projectile projectile = (Projectile) e.getDamager();
			if (projectile.getShooter() instanceof Player)
				return o(((Player) projectile.getShooter()).getName(), uEntityName(projectile));
			else if (projectile.getShooter() instanceof BlockProjectileSource)
				return o(uBlockName((BlockProjectileSource) projectile.getShooter()), uEntityName(projectile));
			return o(uEntityName((Entity) projectile.getShooter()), uEntityName(projectile));
		case THORNS:
			if (e.getDamager() instanceof Player)
				return o(((Player) e.getDamager()).getName(), "Thorns");
			return o(uEntityName(e.getDamager()), "Thorns");
		default:
			return o("Unknown Entity Type (" + e.getCause().name() + ")");
		}
	}

	private String[] o(String... a) {
		return a;
	}

	private String uBlockName(DamageCause cause) {
		switch (cause) {
		case CONTACT:
			return "Cactus";
		case HOT_FLOOR:
			return "Lava Floor";
		case LAVA:
			return "Lava";
		case VOID:
			return "Void";
		default:
			return "Unknown Block (" + cause.name() + ")";
		}
	}

	private String uBlockName(BlockProjectileSource source) {
		switch (source.getBlock().getType()) {
		case DISPENSER:
			return "Dispenser";
		default:
			return "Unknown Block Shooter (" + source.getBlock().getType() + ")";
		}
	}

	private String uEffectName(PotionData potion) {
		return WordUtils.capitalizeFully(potion.getType().name().replaceAll("_", " "));
	}

	private String uEntityName(Entity e) {
		switch (e.getType()) {
		case AREA_EFFECT_CLOUD:
			AreaEffectCloud cloud = (AreaEffectCloud) e;
			if (cloud.getCustomEffects().size() != 0) // TODO Better way to detect / filter dragon breath effect.
				return "Dragon Breath (Cloud Effect)";
			return uEffectName(cloud.getBasePotionData()) + " " + (cloud.getBasePotionData().isUpgraded() ? "II" : "I") + " (Cloud Effect)";
		case ARROW:
			return "Archery";
		case BLAZE:
			return "Blaze";
		case CAVE_SPIDER:
			return "Cave Spider";
		case CREEPER:
			return (((Creeper) e).isPowered() ? "Charged " : "") + "Creeper";
		case DOLPHIN:
			return "Dolphin";
		case DRAGON_FIREBALL:
			return "Dragon Fireball";
		case DROWNED:
			return (((Drowned) e).isBaby() ? "Baby " : "") + "Drowned";
		case EGG:
			return "Throwing Egg";
		case ELDER_GUARDIAN:
			return "Elder Guardian";
		case ENDER_CRYSTAL:
			return "Ender Crystal";
		case ENDER_DRAGON:
			return "Ender Dragon";
		case ENDER_PEARL:
			return "Throwing Ender Pearl";
		case ENDERMAN:
			return "Enderman";
		case ENDERMITE:
			return "Endermite";
		case EVOKER_FANGS:
			return "Evoker Fangs";
		case FIREBALL:
			return "Fireball";
		case FIREWORK:
			return "Firework Explosion";
		case LLAMA:
			return "Llama";
		case LLAMA_SPIT:
			return "Spit";
		case HUSK:
			Husk husk = (Husk) e;
			if (husk.isBaby()) {
				if (husk.isInsideVehicle() && husk.getVehicle().getType().equals(EntityType.CHICKEN))
					return "Chicken Jockey (Husk)";
				return "Baby Husk";
			}
			return "Husk";
		case GHAST:
			return "Ghast";
		case GUARDIAN:
			return "Guardian";
		case ILLUSIONER:
			return "Illusioner";
		case IRON_GOLEM:
			return "Iron Golem";
		case MAGMA_CUBE:
			return uSizeName(((MagmaCube) e).getSize()) + "Magma Cube";
		case MINECART_TNT:
			return "Minecraft TNT";
		case PHANTOM:
			return "Phantom";
		case POLAR_BEAR:
			return "Polar Bear";
		case PRIMED_TNT:
			return "TNT";
		case PIG_ZOMBIE:
			PigZombie pigZombie = (PigZombie) e;
			if (pigZombie.isBaby()) {
				if (pigZombie.isInsideVehicle() && pigZombie.getVehicle().getType().equals(EntityType.CHICKEN))
					return "Chicken Jockey (Zombie Pigman)";
				return "Baby Zombie Pigman";
			}
			return "Zombie Pigman";
		case PUFFERFISH:
			return "Pufferfish";
		case RABBIT:
			return "The Killer Rabbit";
		case SHULKER:
			return "Shulker";
		case SHULKER_BULLET:
			return "Shulker Missile";
		case SILVERFISH:
			return "Silverfish";
		case SKELETON:
			Skeleton skeleton = (Skeleton) e;
			if (skeleton.isInsideVehicle() && skeleton.getVehicle().getType().equals(EntityType.SKELETON_HORSE))
				return "Skeleton Trap (4 Horsemen)";
			return "Skeleton";
		case SLIME:
			return uSizeName(((Slime) e).getSize()) + "Slime";
		case SMALL_FIREBALL:
			return "Small Fireball";
		case SNOWMAN:
			return "Snowman";
		case SNOWBALL:
			return "Throwing Snowball";
		case SPECTRAL_ARROW:
			return "Spectral Arrow";
		case SPIDER:
			Spider spider = (Spider) e;
			if (spider.getPassengers().size() != 0 && spider.getPassengers().get(0).getType().equals(EntityType.SKELETON))
				return "Spider Jockey";
			return "Spider";
		case STRAY:
			return "Stray";
		case TIPPED_ARROW:
			TippedArrow tippedArrow = (TippedArrow) e;
			if (tippedArrow.getCustomEffects().size() != 0)
				return uPotions(tippedArrow.getCustomEffects()) + " (Tipped Arrow)";
			return uEffectName(tippedArrow.getBasePotionData()) + " " + (tippedArrow.getBasePotionData().isUpgraded() ? "II" : "I") + " (Tipped Arrow)";
		case TRIDENT:
			return "Trident";
		case VEX:
			return "Vex";
		case VINDICATOR:
			return "Vindicator";
		case WITCH:
			return "Witch";
		case WITHER:
			return "Wither Boss";
		case WITHER_SKELETON:
			return "Wither Skeleton";
		case WITHER_SKULL:
			return "Wither Skull";
		case WOLF:
			return (((Wolf) e).isAdult() ? "" : "Baby ") + "Wolf";
		case ZOMBIE:
			Zombie zombie = (Zombie) e;
			if (zombie.isBaby()) {
				if (zombie.isInsideVehicle() && zombie.getVehicle().getType().equals(EntityType.CHICKEN))
					return "Chicken Jockey (Zombie)";
				return "Baby Zombie";
			}
			return "Zombie";
		case ZOMBIE_VILLAGER:
			ZombieVillager zombieVillager = (ZombieVillager) e;
			if (zombieVillager.isBaby()) {
				if (zombieVillager.isInsideVehicle() && zombieVillager.getVehicle().getType().equals(EntityType.CHICKEN))
					return "Chicken Jockey (Zombie Villager (" + uVillagerName(zombieVillager.getVillagerProfession()) + "))";
				return "Baby Zombie Villager (" + uVillagerName(zombieVillager.getVillagerProfession()) + ")";
			}
			return "Zombie Villager (" + uVillagerName(zombieVillager.getVillagerProfession()) + ")";
		default:
			return "Unknown Entity (" + e.getType() + ")";
		}
	}

	private String uEnvName(DamageCause cause) {
		switch (cause) {
		case CRAMMING:
			return "Cramming Suffocation";
		case DROWNING:
			return "Drowning";
		case FALL:
			return "Fall";
		case FIRE:
			return "Fire";
		case FIRE_TICK:
			return "Burning";
		case FLY_INTO_WALL:
			return "Fly Into Wall";
		case MAGIC:
		case POISON:
			return "Poison";
		case SUFFOCATION:
			return "Suffocation";
		case STARVATION:
			return "Starvation";
		case WITHER:
			return "Wither Poison";
		default:
			return "Unknown Environment (" + cause.name() + ")";
		}
	}

	private String uItemName(Material material) {
		switch (material) {
		case DIAMOND_AXE:
		case DIAMOND_HOE:
		case DIAMOND_PICKAXE:
		case DIAMOND_SHOVEL:
		case DIAMOND_SWORD:
		case GOLDEN_AXE:
		case GOLDEN_HOE:
		case GOLDEN_PICKAXE:
		case GOLDEN_SHOVEL:
		case GOLDEN_SWORD:
		case IRON_AXE:
		case IRON_HOE:
		case IRON_PICKAXE:
		case IRON_SHOVEL:
		case IRON_SWORD:
		case STONE_AXE:
		case STONE_HOE:
		case STONE_PICKAXE:
		case STONE_SHOVEL:
		case STONE_SWORD:
		case WOODEN_AXE:
		case WOODEN_HOE:
		case WOODEN_PICKAXE:
		case WOODEN_SHOVEL:
		case WOODEN_SWORD:
			return WordUtils.capitalizeFully(material.name().replaceAll("_", " "));
		default:
			return "Fist" + (material.equals(Material.AIR) ? "" : " (" + WordUtils.capitalizeFully(material.name().replaceAll("_", " ") + ")"));
		}
	}

	private String uPotions(Collection<PotionEffect> potions) {
		List<String> list = new ArrayList<>();
		potions.forEach(b -> list.add(uPotionName(b.getType()) + " " + (b.getAmplifier() == 0 ? "I" : "II")));
		return String.join(", ", list);
	}

	private String uPotionName(PotionEffectType type) {
		switch (type.getName().hashCode()) {
		case 2210036:
			return "Harm";
		case 2548225:
			return "Slowness";
		default:
			return "Unknown Potion (" + type.getName() + ")";
		}
	}

	private String uSizeName(int size) {
		return size == 1 ? "Tiny " : size == 2 ? "Small " : "";
	}

	private String uVillagerName(Profession profession) {
		return WordUtils.capitalizeFully(profession.name().replaceAll("_", " "));
	}
}
