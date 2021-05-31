package com.withertech.endermail.commands.mail.box;

import com.withertech.endermail.Endermail;
import com.withertech.endermail.commands.NodeCommand;
import com.withertech.endermail.managers.BoxManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BoxCreateCommand extends NodeCommand
{
	@Override
	public String getName()
	{
		return "create";
	}

	@Override
	public String getDescription()
	{
		return "creates a mailbox at the player's target block";
	}

	@Override
	public String getSyntax()
	{
		return "/mail box create <name>";
	}

	@Override
	public String getPermission()
	{
		return "mail.admin.box.create";
	}

	@Override
	public boolean perform(Player player, String[] args)
	{
		if (args.length == 3)
		{
			if (!BoxManager.getManager().getNames().contains(args[2]))
			{
				Location pos = Objects.requireNonNull(player.getTargetBlockExact(5)).getLocation();
				if (pos.getBlock().getType() == Material.CHEST)
				{
					BoxManager.getManager().createBox(args[2], pos, player.getName());
					player.sendMessage(Endermail.chatPrefix + ChatColor.AQUA + "Created box " + args[2] + " at \nWorld: " + Objects.requireNonNull(pos.getWorld()).getName() + "\nX: " + pos.getBlockX() + "\nY: " + pos.getBlockY() + "\nZ: " + pos.getBlockZ());
				}
				else
				{
					player.sendMessage(Endermail.chatPrefix + ChatColor.RED + "ERROR: Not a chest");
					return false;
				}
			} else
			{
				player.sendMessage(Endermail.chatPrefix + ChatColor.RED + "ERROR: Box already exists");
				return false;
			}
		}
		if (args.length == 4)
		{
			if (!BoxManager.getManager().getNames().contains(args[2]))
			{
				Location pos = Objects.requireNonNull(player.getTargetBlockExact(5)).getLocation();
				if (pos.getBlock().getType() == Material.CHEST)
				{
					BoxManager.getManager().createBox(args[2], pos, args[3]);
					player.sendMessage(Endermail.chatPrefix + ChatColor.AQUA + "Created box " + args[2] + " at \nWorld: " + Objects.requireNonNull(pos.getWorld()).getName() + "\nX: " + pos.getBlockX() + "\nY: " + pos.getBlockY() + "\nZ: " + pos.getBlockZ());
				}
				else
				{
					player.sendMessage(Endermail.chatPrefix + ChatColor.RED + "ERROR: Not a chest");
					return false;
				}
			} else
			{
				player.sendMessage(Endermail.chatPrefix + ChatColor.RED + "ERROR: box already exists");
				return false;
			}
		}
		return true;
	}

	@Override
	public List<String> getSubcommandArguments(Player player, String[] args)
	{
		if (args.length == 4)
		{
			List<String> ret = Endermail.getPlugin(Endermail.class).getServer().getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
			ret.add("server");
			return ret;
		}
		return null;
	}
}
