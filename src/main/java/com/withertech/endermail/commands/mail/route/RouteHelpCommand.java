package com.withertech.endermail.commands.mail.route;

import com.withertech.endermail.Endermail;
import com.withertech.endermail.commands.CommandTree;
import com.withertech.endermail.commands.HelpCommand;
import com.withertech.endermail.commands.NodeCommand;
import com.withertech.endermail.commands.mail.BoxCommand;
import com.withertech.endermail.commands.mail.RouteCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class RouteHelpCommand extends HelpCommand
{
	@Override
	public String getName()
	{
		return "help";
	}

	@Override
	public String getDescription()
	{
		return "Shows information about all of Endermail's route commands";
	}

	@Override
	public String getSyntax()
	{
		return "/mail route help";
	}

	@Override
	public String getPermission()
	{
		return null;
	}

	@Override
	protected String getPrefix()
	{
		return ChatColor.GREEN + "========== " + Endermail.chatPrefix + ChatColor.YELLOW + "Route Commands " + ChatColor.GREEN + "==========";
	}

	@Override
	protected String getSuffix()
	{
		return ChatColor.GREEN + "====================================";
	}

	@Override
	protected CommandTree getCommandTree()
	{
		return new RouteCommand(1);
	}
}
