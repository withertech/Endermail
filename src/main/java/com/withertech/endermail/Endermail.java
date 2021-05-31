package com.withertech.endermail;

import com.withertech.endermail.commands.MailCommand;
import com.withertech.endermail.listeners.BoxListener;
import com.withertech.endermail.listeners.RouteListener;
import com.withertech.endermail.managers.BoxManager;
import com.withertech.endermail.managers.RouteManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;

/**
 * The Endermail plugin
 * @author Witherking25
 * @version 1.0.0
 */
public final class Endermail extends JavaPlugin
{
	/**
	 * The chat prefix used throughout the {@link Endermail} plugin
	 */
	public static final String chatPrefix = ChatColor.GRAY + "[" + ChatColor.BOLD + ChatColor.LIGHT_PURPLE + "Endermail" + ChatColor.GRAY + "] ";

	/**
	 * The Vault {@link Economy} obtained from Vault
	 */
	private static Economy econ = null;

	/**
	 * The {@link File} where the {@link Endermail#boxesConfig} is stored
	 */
	private File boxesConfigFile = null;

	/**
	 * The {@link FileConfiguration} where {@link Box Boxes} are stored
	 */
	private FileConfiguration boxesConfig = null;

	/**
	 * The {@link File} where the {@link Endermail#routesConfig} is stored
	 */
	private File routesConfigFile = null;

	/**
	 * The {@link FileConfiguration} where {@link Route Routes} are stored
	 */
	private FileConfiguration routesConfig = null;

	/**
	 * If Vault has been detected
	 */
	private boolean vaultExists = false;

	/**
	 * Gets the {@link Economy} that has been retrieved from Vault during {@link Endermail#setupEconomy()}
	 * @return The {@link Economy}
	 */
	public static Economy getEconomy()
	{
		return econ;
	}

	/**
	 * The initialization code
	 */
	@Override
	public void onEnable()
	{
		if (!getDataFolder().exists())
			getDataFolder().mkdir();
		ConfigurationSerialization.registerClass(Box.class, "Box");
		ConfigurationSerialization.registerClass(Route.class, "Route");
		saveDefaultConfig();
		createBoxesConfig();
		BoxManager.getManager().loadBoxes();
		createRoutesConfig();
		RouteManager.getManager().loadRoutes();
		vaultExists = setupEconomy();

		Objects.requireNonNull(this.getCommand("mail")).setExecutor(new MailCommand());
		this.getServer().getPluginManager().registerEvents(new BoxListener(), this);
		this.getServer().getPluginManager().registerEvents(new RouteListener(), this);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> RouteManager.getManager().sendRoutes(), 0L, 100L);
		getLogger().info(String.format("Enabled %s v%s", getName(), getDescription().getVersion()));
	}

	/**
	 * The deinitialization code
	 */
	@Override
	public void onDisable()
	{
		getLogger().info(String.format("Disabled %s v%s", getName(), getDescription().getVersion()));
	}

	/**
	 * Gets the {@link FileConfiguration} for {@link Box Boxes} created during {@link Endermail#createBoxesConfig()}
	 * @return The {@link FileConfiguration}
	 */
	public FileConfiguration getBoxesConfig()
	{
		return this.boxesConfig;
	}

	/**
	 * Creates the {@link Endermail#boxesConfig}
	 */
	private void createBoxesConfig()
	{
		boxesConfigFile = new File(getDataFolder(), "boxes.yml");
		if (!boxesConfigFile.exists())
		{
			boxesConfigFile.getParentFile().mkdirs();
			saveResource("boxes.yml", false);
		}

		boxesConfig = new YamlConfiguration();
		try
		{
			boxesConfig.load(boxesConfigFile);
		} catch (IOException | InvalidConfigurationException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Saves the {@link Endermail#boxesConfig} created during {@link Endermail#createBoxesConfig()} to disk
	 */
	public void saveBoxesConfig()
	{
		try
		{
			getBoxesConfig().save(boxesConfigFile);
		} catch (IOException ex)
		{
			getLogger().log(Level.SEVERE, "Could not save config to " + boxesConfigFile, ex);
		}
	}

	/**
	 * Gets the {@link FileConfiguration} for {@link Route Routes} created during {@link Endermail#createRoutesConfig()}
	 * @return The {@link FileConfiguration}
	 */
	public FileConfiguration getRoutesConfig()
	{
		return this.routesConfig;
	}

	/**
	 * Creates the {@link Endermail#routesConfig}
	 */
	private void createRoutesConfig()
	{
		routesConfigFile = new File(getDataFolder(), "routes.yml");
		if (!routesConfigFile.exists())
		{
			routesConfigFile.getParentFile().mkdirs();
			saveResource("routes.yml", false);
		}

		routesConfig = new YamlConfiguration();
		try
		{
			routesConfig.load(routesConfigFile);
		} catch (IOException | InvalidConfigurationException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Saves the {@link Endermail#routesConfig} created during {@link Endermail#createRoutesConfig()} to disk
	 */
	public void saveRoutesConfig()
	{
		try
		{
			getRoutesConfig().save(routesConfigFile);
		} catch (IOException ex)
		{
			getLogger().log(Level.SEVERE, "Could not save config to " + routesConfigFile, ex);
		}
	}

	/**
	 * Retrieves the {@link Economy} from Vault
	 * @return If it was successful
	 */
	private boolean setupEconomy()
	{
		if (getServer().getPluginManager().getPlugin("Vault") == null)
		{
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null)
		{
			return false;
		}
		econ = rsp.getProvider();
		if (econ != null)
		{
			Bukkit.getConsoleSender().sendMessage(chatPrefix + ChatColor.AQUA + "Detected vault, enabling integration");
		}
		return econ != null;
	}

	/**
	 * Checks if the Vault plugin has been loaded
	 * @return If Vault has been detected
	 */
	public boolean doesVaultExist()
	{
		return vaultExists;
	}
}
