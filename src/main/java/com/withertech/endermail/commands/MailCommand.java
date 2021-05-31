package com.withertech.endermail.commands;

import com.withertech.endermail.commands.mail.BoxCommand;
import com.withertech.endermail.commands.mail.MailHelpCommand;
import com.withertech.endermail.commands.mail.RouteCommand;

public class MailCommand extends RootCommand
{
	public MailCommand()
	{
		commands.add(new MailHelpCommand());
		commands.add(new BoxCommand(1));
		commands.add(new RouteCommand(1));
	}

	@Override
	protected NodeCommand getHelpCommand()
	{
		return new MailHelpCommand();
	}
}
