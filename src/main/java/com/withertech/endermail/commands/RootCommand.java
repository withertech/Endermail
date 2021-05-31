package com.withertech.endermail.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class RootCommand implements TabExecutor, CommandTree
{
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args)
	{
		if (sender instanceof Player)
		{
			Player p = (Player) sender;

			if (args.length > 0)
			{
				for (int i = 0; i < getSubCommands().size(); i++)
				{
					if (args[0].equalsIgnoreCase(getSubCommands().get(i).getName()))
					{
						if (getSubCommands().get(i).getPermission() == null || p.hasPermission(getSubCommands().get(i).getPermission()))
						{
							if (getSubCommands().get(i).perform(p, args))
							{
								return true;
							} else
							{
								getHelpCommand().perform(p, args);
							}
						} else
						{
							p.sendMessage(ChatColor.RED + "You do not have permission to use this command");
						}
					}
				}
			} else
			{
				getHelpCommand().perform(p, args);
			}
		}
		return true;
	}

	protected abstract NodeCommand getHelpCommand();

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
	{

		if (args.length == 1)
		{ //mail <subcommand> <args>
			ArrayList<String> subcommandsArguments = new ArrayList<>();

			for (int i = 0; i < getSubCommands().size(); i++)
			{
				subcommandsArguments.add(getSubCommands().get(i).getName());
			}

			return subcommandsArguments;
		} else if (args.length >= 2)
		{
			for (int i = 0; i < getSubCommands().size(); i++)
			{
				if (args[0].equalsIgnoreCase(getSubCommands().get(i).getName()))
				{
					return getSubCommands().get(i).getSubcommandArguments((Player) sender, args);
				}
			}
		}

		return null;
	}
}
