package com.withertech.endermail.commands.mail;

import com.withertech.endermail.commands.BranchCommand;
import com.withertech.endermail.commands.HelpCommand;
import com.withertech.endermail.commands.NodeCommand;
import com.withertech.endermail.commands.mail.box.BoxCreateCommand;
import com.withertech.endermail.commands.mail.box.BoxHelpCommand;
import com.withertech.endermail.commands.mail.box.BoxRemoveCommand;

public class BoxCommand extends BranchCommand
{
	public BoxCommand(int depth)
	{
		super(depth);
		commands.add(new BoxHelpCommand());
		commands.add(new BoxCreateCommand());
		commands.add(new BoxRemoveCommand());
	}

	@Override
	public String getName()
	{
		return "box";
	}

	@Override
	public String getDescription()
	{
		return "create/remove mailboxes";
	}

	@Override
	public String getSyntax()
	{
		return "/mail box <subcommand>";
	}

	@Override
	public String getPermission()
	{
		return "mail.admin.box";
	}

	@Override
	protected HelpCommand getHelpCommand()
	{
		return new BoxHelpCommand();
	}
}
