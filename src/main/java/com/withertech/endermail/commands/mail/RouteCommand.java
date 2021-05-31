package com.withertech.endermail.commands.mail;

import com.withertech.endermail.commands.BranchCommand;
import com.withertech.endermail.commands.HelpCommand;
import com.withertech.endermail.commands.NodeCommand;
import com.withertech.endermail.commands.mail.route.RouteCreateCommand;
import com.withertech.endermail.commands.mail.route.RouteHelpCommand;
import com.withertech.endermail.commands.mail.route.RouteRemoveCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RouteCommand extends BranchCommand
{
	public RouteCommand(int depth)
	{
		super(depth);
		commands.add(new RouteHelpCommand());
		commands.add(new RouteCreateCommand());
		commands.add(new RouteRemoveCommand());
	}

	@Override
	public String getName()
	{
		return "route";
	}

	@Override
	public String getDescription()
	{
		return "create/remove routes";
	}

	@Override
	public String getSyntax()
	{
		return "/mail route <subcommand>";
	}

	@Override
	public String getPermission()
	{
		return "mail.admin.route";
	}

	@Override
	protected HelpCommand getHelpCommand()
	{
		return new RouteHelpCommand();
	}
}
