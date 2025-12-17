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
        displayNames.put("REDSTONE_BLOCK", "Block of Redstone");
        displayNames.put("REDSTONE_TORCH", "Redstone Torch");
        displayNames.put("REDSTONE_LAMP", "Redstone Lamp");
        displayNames.put("REPEATER", "Repeaters");
        displayNames.put("COMPARATOR", "Comparators");
        displayNames.put("OBSERVER", "Observers");
        try {
                Material crafter = Material.matchMaterial("CRAFTER");
                if (crafter != null) {
                    displayNames.put(crafter.name(), "Crafter");
             }
          } catch (Exception ignored) {
           }
        displayNames.put("PISTON", "Pistons");
        displayNames.put("STICKY_PISTON", "Sticky Pistons");
        displayNames.put("SLIME_BLOCK", "Slime Block");
        displayNames.put("HONEY_BLOCK", "Honey Block");
        displayNames.put("HOPPER", "Hoppers");
        displayNames.put("DROPPER", "Droppers");
        displayNames.put("DISPENSER", "Dispensers");
        displayNames.put("CHISELED_BOOKSHELF", "Chiseled Bookshelf");
        displayNames.put("RAIL", "Normal Rail");
        displayNames.put("POWERED_RAIL", "Powered Rail");
        displayNames.put("DETECTOR_RAIL", "Detector Rail");
        displayNames.put("ACTIVATOR_RAIL", "Activator Rail");
        displayNames.put("LEVER", "Lever");
        displayNames.put("TARGET", "Target Block");
        displayNames.put("DAYLIGHT_DETECTOR", "Daylight Detector");
        displayNames.put("SCULK_SENSOR", "Sculk Sensor");
        displayNames.put("CALIBRATED_SCULK_SENSOR", "Calibrated Sculk Sensor");
        displayNames.put("TRIPWIRE_HOOK", "Tripwire Hook");
        displayNames.put("TRAPPED_CHEST", "Trapped Chest");
        displayNames.put("LECTERN", "Lectern");
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
//chat output with normalized display names
        player.sendMessage(ChatColor.GRAY + "---------------------");
        player.sendMessage(ChatColor.GOLD + "Redstone in this chunk:");
        player.sendMessage("");

        for (String blockName : blockTypes) {
            int current = counts.get(blockName);
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

        player.sendMessage(ChatColor.GRAY + "---------------------");
        return true;
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