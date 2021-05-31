package com.withertech.endermail.commands.mail.box;

import com.withertech.endermail.commands.NodeCommand;
import com.withertech.endermail.managers.BoxManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class BoxRemoveCommand extends NodeCommand
{
	@Override
	public String getName()
	{
		return "remove";
	}

	@Override
	public String getDescription()
	{
		return "removes the mailbox with the specified name";
	}

	@Override
	public String getSyntax()
	{
		return "/mail box remove <name>";
	}

	@Override
	public String getPermission()
	{
		return "mail.admin.box.remove";
	}

	@Override
	public boolean perform(Player player, String[] args)
	{
		if (args.length == 3)
		{
			if (BoxManager.getManager().getNames().contains(args[2]))
			{
				BoxManager.getManager().removeBox(args[2]);
				player.sendMessage(ChatColor.GRAY + "[" + ChatColor.LIGHT_PURPLE + "Endermail" + ChatColor.GRAY + "] " + ChatColor.AQUA + "Removed box " + args[2]);
			} else
			{
				player.sendMessage(ChatColor.GRAY + "[" + ChatColor.LIGHT_PURPLE + "Endermail" + ChatColor.GRAY + "] " + ChatColor.RED + "ERROR: invalid box");
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
			return BoxManager.getManager().getNames();
		}
		return null;
	}
}
