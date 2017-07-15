package com.boothousegaming.minespawner;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class MineSpawner extends JavaPlugin implements Listener {
	
	boolean requirePickaxe;
	boolean requireSilkTouch;
	
	@Override
	public void onEnable() {
		FileConfiguration config;
		
		config = getConfig();
		requirePickaxe = config.getBoolean("require-pickaxe");
		requireSilkTouch = config.getBoolean("require-silk-touch");
		
		saveDefaultConfig();
		
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	@Override
	public void onDisable() {
		
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		ItemStack item;
		Block block;
		ItemMeta itemMeta;
		World world;
		Location location;
		
		block = event.getBlock();
		if (block.getType().equals(Material.MOB_SPAWNER)) {
			item = event.getPlayer().getInventory().getItemInMainHand();
			if (!requirePickaxe || item.getType().name().endsWith("_PICKAXE")) {
				if (!requireSilkTouch || item.containsEnchantment(Enchantment.SILK_TOUCH)) {
					item = new ItemStack(Material.MOB_SPAWNER);
					itemMeta = item.getItemMeta();
					itemMeta.setLore(Arrays.asList(((CreatureSpawner)block.getState()).getSpawnedType().name()));
					item.setItemMeta(itemMeta);
					world = block.getWorld();
					location = block.getLocation();
					if (!block.getDrops().isEmpty() || !block.breakNaturally())
						block.setType(Material.AIR);
					world.dropItemNaturally(location, item);
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		Block block;
		ItemStack item;
		ItemMeta itemMeta;
		
		block = event.getBlockPlaced();
		if (block.getType().equals(Material.MOB_SPAWNER)) {
			item = event.getItemInHand();
			if (item.getType().equals(Material.MOB_SPAWNER)) {
				itemMeta = item.getItemMeta();
				if (itemMeta.hasLore()) {
					((CreatureSpawner)block.getState()).setSpawnedType(EntityType.valueOf(itemMeta.getLore().get(0).toUpperCase()));
				}
			}
		}
	}

}
