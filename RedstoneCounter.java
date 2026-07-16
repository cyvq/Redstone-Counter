package com.cyvertray.redstonecount;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedstoneCounter extends JavaPlugin {

    private List<String> blockTypes;
    private Map<String, Integer> maxAmounts;
    private final Map<String, String> displayNames = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigData();
        setupDisplayNames();
        getLogger().info("RedstoneCounter enabled!");
    }

    private void loadConfigData() {
        reloadConfig();
        blockTypes = getConfig().getStringList("blocks");
        maxAmounts = new HashMap<>();
        if (blockTypes != null) {
            for (String block : blockTypes) {
                int max = getConfig().getInt("max." + block, 0);
                maxAmounts.put(block, max);
            }
        }
    }
//normalize displaynames for redstone components
    private void setupDisplayNames() {
        displayNames.put("REDSTONE_WIRE", "Redstone Dust");
        displayNames.put("REDSTONE_BLOCK", "Blocks of Redstone");
        displayNames.put("REDSTONE_TORCH", "Redstone Torches");
        displayNames.put("REDSTONE_LAMP", "Redstone Lamps");
        displayNames.put("REPEATER", "Repeaters");
        displayNames.put("COMPARATOR", "Comparators");
        displayNames.put("OBSERVER", "Observers");
        try {
                Material crafter = Material.matchMaterial("CRAFTER");
                if (crafter != null) {
                    displayNames.put(crafter.name(), "Crafters");
             }
          } catch (Exception ignored) {
           }
        displayNames.put("COPPER_BULB", "Copper Bulbs");
        displayNames.put("EXPOSED_COPPER_BULB", "Copper Bulbs");
        displayNames.put("WEATHERED_COPPER_BULB", "Copper Bulbs");
        displayNames.put("OXIDIZED_COPPER_BULB", "Copper Bulbs");
        displayNames.put("WAXED_COPPER_BULB", "Copper Bulbs");
        displayNames.put("WAXED_EXPOSED_COPPER_BULB", "Copper Bulbs");
        displayNames.put("WAXED_WEATHERED_COPPER_BULB", "Copper Bulbs");
        displayNames.put("WAXED_OXIDIZED_COPPER_BULB", "Copper Bulbs");

        try {
            Material vault = Material.matchMaterial("VAULT");
            if (vault != null) {
                displayNames.put(vault.name(), "Vaults");
            }
        } catch (Exception ignored) {
        }

        try {
            Material trialSpawner = Material.matchMaterial("TRIAL_SPAWNER");
            if (trialSpawner != null) {
                displayNames.put(trialSpawner.name(), "Trial Spawners");
            }
        } catch (Exception ignored) {
        }
        displayNames.put("PISTON", "Pistons");
        displayNames.put("STICKY_PISTON", "Sticky Pistons");
        displayNames.put("SLIME_BLOCK", "Slime Blocks");
        displayNames.put("HONEY_BLOCK", "Honey Blocks");
        displayNames.put("HOPPER", "Hoppers");
        displayNames.put("DROPPER", "Droppers");
        displayNames.put("DISPENSER", "Dispensers");
        displayNames.put("CHISELED_BOOKSHELF", "Chiseled Bookshelf");
        displayNames.put("RAIL", "Normal Rails");
        displayNames.put("POWERED_RAIL", "Powered Rails");
        displayNames.put("DETECTOR_RAIL", "Detector Rails");
        displayNames.put("ACTIVATOR_RAIL", "Activator Rails");
        displayNames.put("LEVER", "Levers");
        displayNames.put("TARGET", "Target Blocks");
        displayNames.put("DAYLIGHT_DETECTOR", "Daylight Detectors");
        displayNames.put("SCULK_SENSOR", "Sculk Sensors");
        displayNames.put("CALIBRATED_SCULK_SENSOR", "Calibrated Sculk Sensors");
        displayNames.put("TRIPWIRE_HOOK", "Tripwire Hooks");
        displayNames.put("TRAPPED_CHEST", "Trapped Chests");
        displayNames.put("LECTERN", "Lecterns");
        displayNames.put("REDSTONE_ORE", "Redstone Ore");
        displayNames.put("NOTE_BLOCK", "Note Blocks");
        displayNames.put("JUKEBOX", "Jukebox");
    }
//perms, reloading and block count
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("redstonecounter.reload")) {
                sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to reload this plugin.");
                return true;
            }
            loadConfigData();
            sender.sendMessage(ChatColor.DARK_GREEN + "RedstoneCounter configuration reloaded!");
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.DARK_RED + "This command can only be used by a player!");
            return true;
        }

        Chunk chunk = player.getLocation().getChunk();
        Map<String, Integer> counts = new HashMap<>();
        for (String blockName : blockTypes) {
            counts.put(blockName, 0);
        }

        for (int x = 0; x < 16; x++) {
            for (int y = chunk.getWorld().getMinHeight(); y < chunk.getWorld().getMaxHeight(); y++) {
                for (int z = 0; z < 16; z++) {
                    Material material = chunk.getBlock(x, y, z).getType();
                    String matName = material.name();
                    if (counts.containsKey(matName)) {
                        counts.put(matName, counts.get(matName) + 1);
                    }
                }
            }
        }

        combineCounts(counts);
        //chat output with normalized display names
        player.sendMessage(ChatColor.GRAY + "---------------------");
        player.sendMessage(ChatColor.GOLD + "Redstone in this chunk:");
        player.sendMessage("");

        Map<String, Integer> outputCounts = new HashMap<>();

		for (Map.Entry<String, Integer> entry : counts.entrySet()) {
			String name = entry.getKey();

			if (name.contains("COPPER_BULB")) {
				outputCounts.merge("COPPER_BULB", entry.getValue(), Integer::sum);
			} else {
				outputCounts.put(name, entry.getValue());
			}
		}

		for (String blockName : outputCounts.keySet()) {
			int current = outputCounts.getOrDefault(blockName, 0);
			int max = maxAmounts.getOrDefault(blockName, 0);

			String maxDisplay;
			ChatColor countColor;
			ChatColor maxColor;

			if (max <= 0) {
				maxDisplay = "∞";
				countColor = ChatColor.GREEN;
				maxColor = ChatColor.GRAY;
			} else if (current >= max) {
				maxDisplay = String.valueOf(max);
				countColor = ChatColor.RED;
				maxColor = ChatColor.RED;
			} else {
				maxDisplay = String.valueOf(max);
				countColor = ChatColor.GREEN;
				maxColor = ChatColor.GRAY;
			}

			String display = displayNames.getOrDefault(blockName, blockName);
			player.sendMessage(display + ": " + countColor + current + ChatColor.GRAY + "/" + maxColor + maxDisplay);
		}

			return true;
	
	}

private void combineCounts(Map<String, Integer> counts) {

    int copperBulbs = 0;

    String[] copperVariants = {
            "COPPER_BULB",
            "EXPOSED_COPPER_BULB",
            "WEATHERED_COPPER_BULB",
            "OXIDIZED_COPPER_BULB",
            "WAXED_COPPER_BULB",
            "WAXED_EXPOSED_COPPER_BULB",
            "WAXED_WEATHERED_COPPER_BULB",
            "WAXED_OXIDIZED_COPPER_BULB"
    };

    for (String variant : copperVariants) {
        copperBulbs += counts.getOrDefault(variant, 0);
        counts.remove(variant);
    }

    counts.put("COPPER_BULB", copperBulbs);
}

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1 && sender.hasPermission("redstonecounter.reload")) {
            if ("reload".startsWith(args[0].toLowerCase())) {
                completions.add("reload");
            }
        }
        return completions;
    }
}
