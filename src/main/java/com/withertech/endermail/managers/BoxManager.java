package com.withertech.endermail.managers;

import com.withertech.endermail.Box;
import com.withertech.endermail.Endermail;
import com.withertech.endermail.Route;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.BiConsumer;

public class BoxManager
{
	private static final Endermail plugin = Endermail.getPlugin(Endermail.class);
	private static final BoxManager manager = new BoxManager();

	private final Map<String, Box> boxes = new HashMap<>();

	public BoxManager()
	{
	}

	private static Endermail getPlugin()
	{
		return plugin;
	}

	public static BoxManager getManager()
	{
		return manager;
	}

	public List<String> getNames()
	{
		return new ArrayList<>(boxes.keySet());
	}

	public Box createBox(String name, Location pos, String owner)
	{
		Box box = new Box(name, Objects.requireNonNull(pos.getWorld()).getName(), owner, Box.BoxScope.PUBLIC, pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
		boxes.put(name, box);
		Map<String, Object> map;
		if (getPlugin().getBoxesConfig().isConfigurationSection("Boxes"))
			map = Objects.requireNonNull(getPlugin().getBoxesConfig().getConfigurationSection("Boxes")).getValues(false);
		else
			map = new HashMap<>();
		map.put(name, box);
		plugin.getBoxesConfig().createSection("Boxes", map);
		plugin.saveBoxesConfig();
		return box;
	}

	public void removeBox(String name)
	{
		Box box = boxes.get(name);
		if (box == null)
			return;
		Route route = RouteManager.getManager().getForBox(box);
		if (route != null)
			RouteManager.getManager().removeRoute(route.getName());
		boxes.remove(name);
		Map<String, Object> map;
		if (getPlugin().getBoxesConfig().isConfigurationSection("Boxes"))
			map = getPlugin().getBoxesConfig().getConfigurationSection("Boxes").getValues(false);
		else
			return;
		map.remove(name);
		plugin.getBoxesConfig().createSection("Boxes", map);
		plugin.saveBoxesConfig();
	}

	public void updateBox(String name, Box box)
	{
		boxes.put(name, box);
		Map<String, Object> map;
		if (getPlugin().getBoxesConfig().isConfigurationSection("Boxes"))
			map = getPlugin().getBoxesConfig().getConfigurationSection("Boxes").getValues(false);
		else
			map = new HashMap<>();
		map.put(name, box);
		plugin.getBoxesConfig().createSection("Boxes", map);
		plugin.saveBoxesConfig();
	}
	public Box get(String name)
	{
		return boxes.get(name);
	}

	public void forEach(BiConsumer<String, Box> func)
	{
		boxes.forEach(func);
	}

	public Box getForLocation(Location loc)
	{
		Box box = null;
		for (Map.Entry<String, Box> entry : boxes.entrySet())
		{
			if (entry.getValue().getPos().equals(loc))
				box = entry.getValue();
		}
		if (box == null || !box.getPos().equals(loc))
			return null;
		return box;
	}

	public void loadBoxes()
	{
		if (getPlugin().getBoxesConfig().isConfigurationSection("Boxes"))
		{
			Map<String, Object> map = getPlugin().getBoxesConfig().getConfigurationSection("Boxes").getValues(false);
			map.forEach((name, object) ->
			{
				Box box = (Box) object;
				boxes.put(name, box);
			});
		}
	}


}
