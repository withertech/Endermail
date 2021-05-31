package com.withertech.endermail.guis;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.MasonryPane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.component.CycleButton;
import com.github.stefvanschie.inventoryframework.pane.component.ToggleButton;
import com.withertech.endermail.Box;
import com.withertech.endermail.Endermail;
import com.withertech.endermail.Route;
import com.withertech.endermail.managers.BoxManager;
import com.withertech.endermail.managers.RouteManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BoxGui
{
	private ChestGui root = null;

	public BoxGui(Box box, Inventory inv, Player player)
	{
		// Destination select
		ChestGui boxSelect = new ChestGui(4, "Select destination");
		boxSelect.setOnGlobalClick(event -> event.setCancelled(true));
		MasonryPane selectMasonryPane = new MasonryPane(9, 4);
		PaginatedPane selectPaginatedPane = new PaginatedPane(9, 3);
		selectPaginatedPane.populateWithGuiItems(BoxManager.getManager().getNames().stream().filter(s -> (BoxManager.getManager().get(s).isGlobal() || BoxManager.getManager().get(s).getOwner().getUniqueId().equals(player.getUniqueId()) || BoxManager.getManager().get(s).getScope().equals(Box.BoxScope.PUBLIC)) && !BoxManager.getManager().get(s).equals(box)).map(name ->
		{
			Box destBox = BoxManager.getManager().get(name);
			ItemStack itemStack = new ItemStack(Material.END_CRYSTAL)
			{
				@Override
				public ItemMeta getItemMeta()
				{
					ItemMeta meta = super.getItemMeta();
					assert meta != null;
					meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&r&bName: &e" + name));
					List<String> lore = new ArrayList<>();
					lore.add(ChatColor.translateAlternateColorCodes('&', "&r&bWorld: &e" + destBox.getWorld().getName()));
					lore.add(ChatColor.translateAlternateColorCodes('&', "&r&bX: &e" + destBox.getX()));
					lore.add(ChatColor.translateAlternateColorCodes('&', "&r&bY: &e" + destBox.getY()));
					lore.add(ChatColor.translateAlternateColorCodes('&', "&r&bZ: &e" + destBox.getZ()));
					if (!box.isGlobal())
						lore.add(ChatColor.translateAlternateColorCodes('&', "&r&bCost: &e" + Endermail.getEconomy().format(Route.getCost(box, destBox))));
					meta.setLore(lore);
					return meta;
				}
			};
			GuiItem guiItem = new GuiItem(itemStack);
			guiItem.setAction(inventoryClickEvent ->
			{
				Route route = RouteManager.getManager().createRoute(box.getName() + "->" + destBox.getName(), box, destBox, true);
				route.send();
				root.show(inventoryClickEvent.getWhoClicked());
			});
			return guiItem;
		}).collect(Collectors.toList()));
		StaticPane selectStaticPane = new StaticPane(9, 1);
		selectStaticPane.addItem(new GuiItem(new ItemStack(Material.ARROW)
		{
			@Override
			public ItemMeta getItemMeta()
			{
				ItemMeta meta = super.getItemMeta();
				assert meta != null;
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&rPrevious (<-)"));
				return meta;
			}
		}, inventoryClickEvent ->
		{
			if (selectPaginatedPane.getPage() - 1 >= 0)
				selectPaginatedPane.setPage(selectPaginatedPane.getPage() - 1);
			boxSelect.update();
		}), 2, 0);
		selectStaticPane.addItem(new GuiItem(new ItemStack(Material.ARROW)
		{
			@Override
			public ItemMeta getItemMeta()
			{
				ItemMeta meta = super.getItemMeta();
				assert meta != null;
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&rNext (->)"));
				return meta;
			}
		}, inventoryClickEvent ->
		{
			if (selectPaginatedPane.getPage() + 1 < selectPaginatedPane.getPages() - 1)
				selectPaginatedPane.setPage(selectPaginatedPane.getPage() + 1);
			boxSelect.update();
		}), 6, 0);
		selectStaticPane.fillWith(new ItemStack(Material.PURPLE_STAINED_GLASS_PANE)
		{
			@Override
			public ItemMeta getItemMeta()
			{
				ItemMeta meta = super.getItemMeta();
				assert meta != null;
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&5Ender stuff!"));
				return meta;
			}
		});
		selectMasonryPane.addPane(selectPaginatedPane);
		selectMasonryPane.addPane(selectStaticPane);
		boxSelect.addPane(selectMasonryPane);

		// Settings
		ChestGui settings = new ChestGui(1, "Settings");
		settings.setOnGlobalClick(event -> event.setCancelled(true));
		ToggleButton scopeButton = new ToggleButton(4, 0, 1, 1);
		scopeButton.setEnabledItem(new GuiItem(new ItemStack(Material.LIME_DYE)
		{
			@Override
			public ItemMeta getItemMeta()
			{
				ItemMeta meta = super.getItemMeta();
				assert meta != null;
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&bVisibility: &aPUBLIC"));
				return meta;
			}
		}));
		scopeButton.setDisabledItem(new GuiItem(new ItemStack(Material.GRAY_DYE)
		{
			@Override
			public ItemMeta getItemMeta()
			{
				ItemMeta meta = super.getItemMeta();
				assert meta != null;
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&bVisibility: &7PRIVATE"));
				return meta;
			}
		}));
		scopeButton.setOnClick(inventoryClickEvent ->
		{
			if (scopeButton.isEnabled())
			{
				box.setScope(Box.BoxScope.PUBLIC);
			}
			else
			{
				box.setScope(Box.BoxScope.PRIVATE);
			}
		});
		if (box.getScope() == Box.BoxScope.PUBLIC)
			scopeButton.toggle();
		settings.addPane(scopeButton);

		// Root
		root = new ChestGui(1, ChatColor.translateAlternateColorCodes('&', box.getName()));
		root.setOnGlobalClick(event -> event.setCancelled(true));
		StaticPane pane = new StaticPane(9, 1);
		pane.addItem(new GuiItem(new ItemStack(Material.CHEST)
		{
			@Override
			public ItemMeta getItemMeta()
			{
				ItemMeta meta = super.getItemMeta();
				assert meta != null;
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eOpen Box"));
				return meta;
			}
		}, inventoryClickEvent ->
		{
			if (box.isGlobal())
			{
				if (inventoryClickEvent.getWhoClicked().isOp())
				{
					inventoryClickEvent.getWhoClicked().openInventory(inv);
				}
			}
			else if (box.getOwner().getUniqueId().equals(inventoryClickEvent.getWhoClicked().getUniqueId()))
			{
				inventoryClickEvent.getWhoClicked().openInventory(inv);
			}
		}), 2, 0);

		pane.addItem(new GuiItem(new ItemStack(Material.REDSTONE_BLOCK)
		{
			@Override
			public ItemMeta getItemMeta()
			{
				ItemMeta meta = super.getItemMeta();
				assert meta != null;
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aSettings"));
				return meta;
			}
		}, inventoryClickEvent -> settings.show(inventoryClickEvent.getWhoClicked())), 4, 0);

		pane.addItem(new GuiItem(new ItemStack(Material.ENDER_EYE)
		{
			@Override
			public ItemMeta getItemMeta()
			{
				ItemMeta meta = super.getItemMeta();
				assert meta != null;
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&bSend Item"));
				return meta;
			}
		}, inventoryClickEvent -> boxSelect.show(inventoryClickEvent.getWhoClicked())), 6, 0);
		pane.fillWith(new ItemStack(Material.PURPLE_STAINED_GLASS_PANE)
		{
			@Override
			public ItemMeta getItemMeta()
			{
				ItemMeta meta = super.getItemMeta();
				assert meta != null;
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&5Ender stuff!"));
				return meta;
			}
		});
		root.addPane(pane);
	}

	public void show(Player player)
	{
		root.show(player);
	}
}
