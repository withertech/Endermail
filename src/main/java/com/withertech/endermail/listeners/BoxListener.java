package com.withertech.endermail.listeners;

import com.withertech.endermail.Box;
import com.withertech.endermail.Endermail;
import com.withertech.endermail.guis.BoxGui;
import com.withertech.endermail.managers.BoxManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BoxListener implements Listener
{
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.CHEST)
		{
			Box box = BoxManager.getManager().getForLocation(event.getClickedBlock().getLocation());
			if (box != null)
			{
				if (!box.isGlobal())
				{
					if (box.getOwner().getUniqueId().equals(event.getPlayer().getUniqueId()))
					{
						Chest chest = (Chest) event.getClickedBlock().getState();
						BoxGui root = new BoxGui(box, chest.getBlockInventory(), event.getPlayer());
						root.show(event.getPlayer());
					}
					else
					{
						event.getPlayer().sendMessage(Endermail.chatPrefix + ChatColor.RED + "ERROR: This is not your box!");
					}
				}
				else
				{
					if (event.getPlayer().isOp())
					{
						Chest chest = (Chest) event.getClickedBlock().getState();
						BoxGui root = new BoxGui(box, chest.getBlockInventory(), event.getPlayer());
						root.show(event.getPlayer());
					}
					else
					{
						event.getPlayer().sendMessage(Endermail.chatPrefix + ChatColor.RED + "ERROR: This is not your box! You must be OP to open a global box!");
					}
				}
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		Box box = BoxManager.getManager().getForLocation(event.getBlock().getLocation());
		if(box != null)
		{
			String name = box.getName();
			BoxManager.getManager().removeBox(name);
		}
	}

	@EventHandler
	public void onBlockExplode(BlockExplodeEvent event)
	{
		if(BoxManager.getManager().getForLocation(event.getBlock().getLocation()) != null)
		{
			BoxManager.getManager().removeBox(BoxManager.getManager().getForLocation(event.getBlock().getLocation()).getName());
		}
	}
}
