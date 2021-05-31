package com.withertech.endermail.commands.mail.route;

import com.withertech.endermail.Box;
import com.withertech.endermail.Endermail;
import com.withertech.endermail.commands.NodeCommand;
import com.withertech.endermail.managers.BoxManager;
import com.withertech.endermail.managers.RouteManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class RouteCreateCommand extends NodeCommand
{
	@Override
	public String getName()
	{
		return "create";
	}

	@Override
	public String getDescription()
	{
		return "creates a route between the specified boxes";
	}

	@Override
	public String getSyntax()
	{
		return "/mail route create <name> <src> <dest>";
	}

	@Override
	public String getPermission()
	{
		return "mail.admin.route.create";
	}

	@Override
	public boolean perform(Player player, String[] args)
	{
		if (args.length == 5)
		{
			if (!RouteManager.getManager().getNames().contains(args[2]))
			{
				Box src = BoxManager.getManager().get(args[3]);
				Box dest = BoxManager.getManager().get(args[4]);
				RouteManager.getManager().createRoute(args[2], src, dest, false);
				player.sendMessage(Endermail.chatPrefix + ChatColor.AQUA + "Created route " + args[2] + " with \nSrc: " + src.getName() + "\nDest: " + dest.getName());
			} else
			{
				player.sendMessage(Endermail.chatPrefix + ChatColor.RED + "ERROR: route already exists");
				return false;
			}
		}
		return true;
	}

	@Override
	public List<String> getSubcommandArguments(Player player, String[] args)
	{
		if (args.length == 4 || args.length == 5)
		{
			return BoxManager.getManager().getNames();
		}
		return null;
	}
}
