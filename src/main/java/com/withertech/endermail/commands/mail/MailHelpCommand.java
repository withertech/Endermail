package com.withertech.endermail.commands.mail;

import com.withertech.endermail.Endermail;
import com.withertech.endermail.commands.CommandTree;
import com.withertech.endermail.commands.HelpCommand;
import com.withertech.endermail.commands.MailCommand;
import org.bukkit.ChatColor;

public class MailHelpCommand extends HelpCommand
{
	@Override
	public String getName()
	{
		return "help";
	}

	@Override
	public String getDescription()
	{
		return "Shows information about all of Endermail's \n   commands";
	}

	@Override
	public String getSyntax()
	{
		return "/mail help";
	}

	@Override
	public String getPermission()
	{
		return null;
	}

	@Override
	protected String getPrefix()
	{
		return ChatColor.GREEN + "========== " + Endermail.chatPrefix + ChatColor.YELLOW + "Commands " + ChatColor.GREEN + "==========";
	}

	@Override
	protected String getSuffix()
	{
		return ChatColor.GREEN + "====================================";
	}

	@Override
	protected CommandTree getCommandTree()
	{
		return new MailCommand();
	}
}
