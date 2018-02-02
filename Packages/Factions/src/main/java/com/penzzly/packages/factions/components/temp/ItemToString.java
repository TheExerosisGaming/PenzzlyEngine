/*package com.penzzly.packages.factions.components.temp;

import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//TODO REDO THIS CRAP!!!
public class ItemToString {
	public static String convertItem(@NotNull ItemStack item) {
		ChatColor itemcolor = ChatColor.WHITE;
		
		JSONObject itemJson = new JSONObject();
		
		String icon = "";
		String name = "";
		List<String> translateWith = new ArrayList<String>();
		
		JSONObject tagJson = new JSONObject();
		
		boolean hideEnchants = false;
		boolean hideAttributes = false;
		boolean hideUnbreakable = false;
		boolean hideDestroys = false;
		boolean hidePlacedOn = false;
		boolean hideVarious = false;
		
		if (item.hasItemMeta()) {
			ItemMeta meta = item.getItemMeta();
			
			JSONObject displayJson = new JSONObject();
			
			if (meta.getLore() != null && meta.getLore().size() > 0) {
				List<String> loreList = new ArrayList<String>();
				for (String l : meta.getLore()) {
					loreList.add(l);
				}
				displayJson.put("Lore", loreList);
			}
			
			try {
				if (meta.getItemFlags().size() > 0) {
					int flagBits = 0;
					for (ItemFlag flag : meta.getItemFlags())
						switch (flag) {
							case HIDE_ENCHANTS:
								hideEnchants = true;
								flagBits += 1;
								break;
							case HIDE_ATTRIBUTES:
								hideAttributes = true;
								flagBits += 2;
								break;
							case HIDE_UNBREAKABLE:
								hideUnbreakable = true;
								flagBits += 4;
								break;
							case HIDE_DESTROYS:
								hideDestroys = true;
								flagBits += 8;
								break;
							case HIDE_PLACED_ON:
								hidePlacedOn = true;
								flagBits += 16;
								break;
							case HIDE_POTION_EFFECTS:
								hideVarious = true;
								flagBits += 32;
								break;
						}
					displayJson.put("HideFlags", flagBits);
				}
			} catch (NoSuchMethodError e) {
				// Catch for 1.7.3-1.7.10 servers, there are no item flags previous to 1.8!
			}
			
			if (meta instanceof LeatherArmorMeta) {
				displayJson.put("color", ((LeatherArmorMeta) meta).getColor().asRGB());
			}
			
			if (item.getType().isRecord()) {
				itemcolor = ChatColor.AQUA;
			}
			
			if (meta.spigot().isUnbreakable() && !hideUnbreakable) {
				tagJson.put("Unbreakable", 1);
			}
			
			if (meta.getEnchants() != null && meta.getEnchants().size() > 0) {
				if (!hideEnchants) {
					List<JSONObject> enchList = new ArrayList<JSONObject>();
					
					for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
						JSONObject enchJson = new JSONObject();
						enchJson.put("id", entry.getKey().hashCode());
						enchJson.put("lvl", entry.getValue());
						enchList.add(enchJson);
					}
					tagJson.put("ench", enchList);
				}
				itemcolor = ChatColor.AQUA;
			}
			
			if (meta instanceof EnchantmentStorageMeta) {
				EnchantmentStorageMeta esm = (EnchantmentStorageMeta) meta;
				
				if (esm.getStoredEnchants() != null) {
					if (!hideVarious) {
						List<JSONObject> enchList = new ArrayList<JSONObject>();
						
						for (Map.Entry<Enchantment, Integer> entry : esm.getStoredEnchants().entrySet()) {
							JSONObject enchJson = new JSONObject();
							enchJson.put("id", entry.getKey().hashCode());
							enchJson.put("lvl", entry.getValue());
							enchList.add(enchJson);
						}
						tagJson.put("StoredEnchantments", enchList);
					}
					itemcolor = ChatColor.YELLOW;
				}
			}
			
			if (meta instanceof PotionMeta) {
				if (!hideVarious) {
					PotionMeta pm = (PotionMeta) meta;
					List<JSONObject> potionList = new ArrayList<>();
					for (PotionEffect potion : pm.getCustomEffects()) {
						JSONObject potionJson = new JSONObject();
						potionJson.put("Id", potion.getType().hashCode());
						potionJson.put("Amplifier", potion.getAmplifier());
						potionJson.put("Duration", potion.getDuration());
						if (potion.isAmbient()) {
							potionJson.put("Ambient", (byte) 1);
						}
						potionList.add(potionJson);
					}
					tagJson.put("CustomPotionEffects", potionList);
				}
				itemcolor = ChatColor.YELLOW;
			}
			
			if (meta instanceof BookMeta) {
				if (!hideVarious) {
					BookMeta bm = (BookMeta) meta;
					name = "Book";
					if (bm.getTitle() != null) {
						tagJson.put("title", bm.getTitle());
						name += ": " + bm.getTitle();
					}
					if (bm.getAuthor() != null) {
						tagJson.put("author", bm.getAuthor());
						if (bm.getTitle() == null) {
							name += " by " + bm.getAuthor();
						}
					}
				}
			}
			
			if (meta instanceof SkullMeta) {
				SkullMeta sm = (SkullMeta) meta;
				if (sm.hasOwner()) {
					OfflinePlayer owner = Bukkit.getOfflinePlayer(sm.getOwner());
					if (owner != null) {
						translateWith.add(0, owner.getName());
					//taglist.add("SkullOwner:{Name:\\\\\"" + owner + "\\\\\",},");
						JSONObject ownerJson = new JSONObject();
						ownerJson.put("Name", owner);
						tagJson.put("SkullOwner", ownerJson);
					}
	                //name = owner + "'";
                    //if(!(owner.substring(owner.length() -1).equalsIgnoreCase("s") || owner.substring(owner.length() -1).equalsIgnoreCase("x") || owner.substring(owner.length() -1).equalsIgnoreCase("z")))
                    //    name += "s";
                    //name += " Head";
                    //meta.setDisplayName(name);
				}
			}
			
			if (meta instanceof FireworkMeta) {
				if (!hideVarious) {
					FireworkMeta fm = (FireworkMeta) meta;
					
					JSONObject fireworkJson = new JSONObject();
					
					fireworkJson.put("Flight", fm.getPower());
					
					List<JSONObject> explList = new ArrayList<JSONObject>();
					for (FireworkEffect fe : fm.getEffects()) {
						JSONObject explJson = new JSONObject();
						
						if (fe.hasFlicker()) {
							explJson.put("Flicker", (byte) 1);
						}
						if (fe.hasTrail()) {
							explJson.put("Trail", (byte) 1);
						}
						byte type = 42;
						switch (fe.getType()) {
							case BALL:
								type = 0;
								break;
							case BALL_LARGE:
								type = 1;
								break;
							case STAR:
								type = 2;
								break;
							case CREEPER:
								type = 3;
								break;
							case BURST:
								type = 4;
								break;
						}
						explJson.put("Type", type);
						JSONArray colorArray = new JSONArray();
						for (Color c : fe.getColors()) {
							colorArray.add(c.asRGB());
						}
						explJson.put("Colors", colorArray);
						JSONArray fadeArray = new JSONArray();
						for (Color c : fe.getFadeColors()) {
							fadeArray.add(c.asRGB());
						}
						explJson.put("FadeColors", fadeArray);
						explList.add(explJson);
					}
					fireworkJson.put("Explosions", explList);
					
					tagJson.put("Fireworks", fireworkJson);
				}
			}
			
			if (meta instanceof FireworkEffectMeta) {
				if (!hideVarious) {
					FireworkEffect fe = ((FireworkEffectMeta) meta).getEffect();
					JSONObject explJson = new JSONObject();
					
					if (fe.hasFlicker()) {
						explJson.put("Flicker", (byte) 1);
					}
					if (fe.hasTrail()) {
						explJson.put("Trail", (byte) 1);
					}
					byte type = 42;
					switch (fe.getType()) {
						case BALL:
							type = 0;
							break;
						case BALL_LARGE:
							type = 1;
							break;
						case STAR:
							type = 2;
							break;
						case CREEPER:
							type = 3;
							break;
						case BURST:
							type = 4;
							break;
					}
					explJson.put("Type", type);
					JSONArray colorArray = new JSONArray();
					for (Color c : fe.getColors()) {
						colorArray.add(c.asRGB());
					}
					explJson.put("Colors", colorArray);
					JSONArray fadeArray = new JSONArray();
					for (Color c : fe.getFadeColors()) {
						fadeArray.add(c.asRGB());
					}
					explJson.put("FadeColors", fadeArray);
					tagJson.put("Explosion", explJson);
				}
			}
			
			if (meta instanceof MapMeta && ((MapMeta) meta).isScaling() && !hideVarious) {
				tagJson.put("map_is_scaling", (byte) 1);
			}
			
			if (meta instanceof BannerMeta) {
				BannerMeta bm = (BannerMeta) meta;
				JSONObject blockEntityJson = new JSONObject();
				DyeColor baseColor = bm.getBaseColor();
				int base;
				if (baseColor != null) {
					base = baseColor.getDyeData();
				} else {
					base = (int) item.getDurability();
				}
				blockEntityJson.put("Base", base);
				
				List<JSONObject> patternList = new ArrayList<JSONObject>();
				for (Pattern p : bm.getPatterns()) {
					JSONObject patternJson = new JSONObject();
					patternJson.put("Pattern", p.getPattern().getIdentifier());
					patternJson.put("Color", p.getColor().getDyeData());
					patternList.add(patternJson);
				}
				blockEntityJson.put("Patterns", patternList);
				
				tagJson.put("BlockEntityTag", blockEntityJson);
			}
			
			if (meta.getDisplayName() != null) {
				name = ChatColor.ITALIC + meta.getDisplayName();
				displayJson.put("Name", itemcolor + name);
			}
			
			if (!displayJson.isEmpty()) {
				tagJson.put("display", displayJson);
			}
		}
		
		if (!tagJson.isEmpty()) {
			itemJson.put("tag", tagJson);
		}
		
		
		JSONObject hoverJson = new JSONObject();
		hoverJson.put("action", "show_item");
		String mojangItemJson = toMojangJsonString(itemJson.toJSONString());
		
		hoverJson.put("value", mojangItemJson);
		
		JSONObject nameJson = new JSONObject();
		if (!name.isEmpty()) {
			String resultname = itemcolor + name + ChatColor.RESET;
			nameJson.put("text", resultname);
		}
		nameJson.put("hoverEvent", hoverJson);
		nameJson.put("color", itemcolor.name().toLowerCase());
		
		String lbracket = itemcolor + "[";
		
		JSONObject lbracketJson = new JSONObject();
		lbracketJson.put("text", lbracket);
		lbracketJson.put("hoverEvent", hoverJson);
		
		JSONObject rbracketJson = new JSONObject();
		rbracketJson.put("text", itemcolor + "]");
		rbracketJson.put("hoverEvent", hoverJson);
		
		return lbracketJson.toJSONString() + "," + nameJson.toJSONString() + "," + rbracketJson.toJSONString();
	}
	
	private static String toMojangJsonString(String json) {
		json = json.replace("\\\"", "{ESCAPED_QUOTE}");
		json = json.replaceAll("\"([a-zA-Z]*)\":", "$1:");
		json = json.replace("{ESCAPED_QUOTE}", "\\\"");
		json = StringEscapeUtils.unescapeJava(json);
		return json;
	}
}*/
