package com.withertech.endermail.commands.mail.route;

import com.withertech.endermail.Endermail;
import com.withertech.endermail.commands.NodeCommand;
import com.withertech.endermail.managers.RouteManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class RouteRemoveCommand extends NodeCommand
{
	@Override
	public String getName()
	{
		return "remove";
	}

	@Override
	public String getDescription()
	{
		return "removes the route with the specified name";
	}

	@Override
	public String getSyntax()
	{
		return "/mail route remove <name>";
	}

	@Override
	public String getPermission()
	{
		return "mail.admin.route.remove";
	}

	@Override
	public boolean perform(Player player, String[] args)
	{
		if (args.length == 3)
		{
			if (RouteManager.getManager().getNames().contains(args[2]))
			{
				RouteManager.getManager().removeRoute(args[2]);
				player.sendMessage(Endermail.chatPrefix + ChatColor.AQUA + "Removed route " + args[2]);
			} else
			{
				player.sendMessage(Endermail.chatPrefix + ChatColor.RED + "ERROR: invalid route");
				return false;
			}
		}
		return true;
	}

	@Override
	public List<String> getSubcommandArguments(Player player, String[] args)
	{
		if (args.length == 3)
		{
			return RouteManager.getManager().getNames();
		}
		return null;
	}
}
