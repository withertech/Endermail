package com.withertech.endermail.managers;

import com.withertech.endermail.Box;
import com.withertech.endermail.Endermail;
import com.withertech.endermail.Route;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class RouteManager
{
	/**
	 * The {@link Endermail} plugin
	 */
	private static final Endermail plugin = Endermail.getPlugin(Endermail.class);

	/**
	 * The {@link RouteManager} singleton instance
	 */
	private static final RouteManager manager = new RouteManager();

	/**
	 * The database of {@link Route Routes}
	 */
	private final Map<String, Route> routes = new HashMap<>();

	/**
	 * Gets the {@link Endermail} plugin
	 * @return The {@link Endermail} plugin
	 */
	private static Endermail getPlugin()
	{
		return plugin;
	}

	/**
	 * Gets the {@link RouteManager} singleton instance
	 * @return The {@link RouteManager} singleton instance
	 */
	public static RouteManager getManager()
	{
		return manager;
	}

	/**
	 * Gets a {@link List} of the {@link Route Routes} names
	 * @return A {@link List} of the {@link Route Routes} names
	 */
	public List<String> getNames()
	{
		return new ArrayList<>(routes.keySet());
	}

	/**
	 * Constructs a {@link Route} from parameters and registers it in the {@link RouteManager#routes} database
	 * @param name The name of the {@link Route}
	 * @param src The source {@link Box} of the {@link Route}
	 * @param dest The destination {@link Box} of the {@link Route}
	 * @param temp If the {@link Route} should be temporary
	 * @return The constructed {@link Route}
	 */
	public Route createRoute(String name, Box src, Box dest, boolean temp)
	{
		Route route = new Route(name, src, dest, temp);
		routes.put(name, route);
		if (!temp)
		{
			Map<String, Object> map;
			if (getPlugin().getRoutesConfig().isConfigurationSection("Routes"))
				map = getPlugin().getRoutesConfig().getConfigurationSection("Routes").getValues(false);
			else
				map = new HashMap<>();
			map.put(name, route);
			plugin.getRoutesConfig().createSection("Routes", map);
			plugin.saveRoutesConfig();
		}
		return route;
	}

	/**
	 * Removes a {@link Route} from the {@link RouteManager#routes} database
	 * @param name The name of the {@link Route} to remove
	 */
	public void removeRoute(String name)
	{
		Route route = routes.get(name);
		if (route == null)
			return;
		routes.remove(name);
		if (!route.isTemp())
		{
			Map<String, Object> map;
			if (getPlugin().getRoutesConfig().isConfigurationSection("Routes"))
				map = getPlugin().getRoutesConfig().getConfigurationSection("Routes").getValues(false);
			else
				return;
			map.remove(name);
			plugin.getRoutesConfig().createSection("Routes", map);
			plugin.saveRoutesConfig();
		}
	}

	/**
	 * Updates a {@link Route} in the {@link RouteManager#routes} database with a new value
	 * @param name The name of the {@link Route} to update
	 * @param route The {@link Route} to update with
	 */
	public void updateRoute(String name, Route route)
	{
		routes.put(name, route);
		if (!route.isTemp())
		{
			Map<String, Object> map;
			if (getPlugin().getRoutesConfig().isConfigurationSection("Routes"))
				map = getPlugin().getRoutesConfig().getConfigurationSection("Routes").getValues(false);
			else
				map = new HashMap<>();
			map.put(name, route);
			plugin.getRoutesConfig().createSection("Routes", map);
			plugin.saveRoutesConfig();
		}
	}

	/**
	 * Gets a {@link Route} from the {@link RouteManager#routes} database
	 * @param name The name of the {@link Route} to get
	 * @return The {@link Route}
	 */
	public Route get(String name)
	{
		return routes.get(name);
	}

	/**
	 * Runs the {@link BiConsumer} on every entry in the {@link RouteManager#routes} database
	 * @param func The {@link BiConsumer} to forEach
	 */
	public void forEach(BiConsumer<String, Route> func)
	{
		if (!routes.isEmpty())
			routes.forEach(func);
	}

	@Nullable
	public Route getForBox(Box box)
	{
		for (Map.Entry<String, Route> entry : routes.entrySet())
		{
			Route route = entry.getValue();

			if (route.getSrc().equals(box) || route.getDest().equals(box))
			{
				return route;
			}
		}
		return null;
	}

	public void loadRoutes()
	{
		if (getPlugin().getRoutesConfig().isConfigurationSection("Routes"))
		{
			Map<String, Object> map = getPlugin().getRoutesConfig().getConfigurationSection("Routes").getValues(false);
			map.forEach((name, object) ->
			{
				Route route = (Route) object;
				routes.put(name, route);
			});
		}
	}

	public void sendRoutes()
	{
		if(getPlugin().getServer().getWorlds().get(0).getTime() >= 23925 || getPlugin().getServer().getWorlds().get(0).getTime() <= 75)
		{
			forEach((name, route) -> route.send());
		}
	}


}
