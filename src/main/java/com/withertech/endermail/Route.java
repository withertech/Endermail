package com.withertech.endermail;

import com.withertech.endermail.managers.BoxManager;
import com.withertech.endermail.managers.RouteManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Chest;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.ServerOperator;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * An object that represents a route between two {@link Box Boxes}
 */
@SerializableAs("Route")
public class Route implements ConfigurationSerializable
{
	/**
	 * The name of this {@link Route}
	 */
	private final String name;

	/**
	 * The source {@link Box} of this {@link Route}
	 */
	private final Box src;

	/**
	 * The destination {@link Box} of this {@link Route}
	 */
	private final Box dest;

	/**
	 * If this {@link Route} is temporary and will be removed when used
	 */
	private final boolean temp;

	/**
	 *
	 * @param name The name of the {@link Route}
	 * @param src The source {@link Box} of the {@link Route}
	 * @param dest The destination {@link Box} of the {@link Route}
	 * @param temp If the {@link Route} should be temporary
	 */
	public Route(String name, Box src, Box dest, boolean temp)
	{
		this.name = name;
		this.src = src;
		this.dest = dest;
		this.temp = temp;
	}

	/**
	 * Deserialize a {@link Route} from bukkit yaml storage
	 * @param map The serialized data
	 * @return The deserialized {@link Route}
	 */
	public static Route deserialize(Map<String, Object> map)
	{
		return new Route((String) map.get("name"), BoxManager.getManager().get((String) map.get("src")), BoxManager.getManager().get((String) map.get("dest")), false);
	}

	/**
	 * Serialize this {@link Route} for bukkit yaml storage
	 * @return The serialized data
	 */
	@NotNull
	@Override
	public Map<String, Object> serialize()
	{
		HashMap<String, Object> ret = new HashMap<>();
		ret.put("name", this.getName());
		ret.put("src", this.src.getName());
		ret.put("dest", this.dest.getName());
		return ret;
	}

	/**
	 * Gets the transfer cost of this {@link Route}
	 * @return The transfer cost
	 */
	public double getCost()
	{
		if (src.getWorld().getName().equals(dest.getWorld().getName()))
			return dest.getPos().distance(src.getPos()) * Endermail.getPlugin(Endermail.class).getConfig().getDouble("Cost", 1.0);
		else
			return 1000 * Endermail.getPlugin(Endermail.class).getConfig().getDouble("Cost", 1.0);
	}

	/**
	 * A static version of {@link Route#getCost()}
	 * @param src The source {@link Box}
	 * @param dest The destination {@link Box}
	 * @return The transfer cost
	 */
	public static double getCost(Box src, Box dest)
	{
		if (src.getWorld().getName().equals(dest.getWorld().getName()))
			return dest.getPos().distance(src.getPos()) * Endermail.getPlugin(Endermail.class).getConfig().getDouble("Cost", 1.0);
		else
			return 1000 * Endermail.getPlugin(Endermail.class).getConfig().getDouble("Cost", 1.0);
	}

	/**
	 * Checks if this {@link Route} is temporary and will be removed when used
	 * @return if the {@link Route} is temporary
	 */
	public boolean isTemp()
	{
		return temp;
	}

	/**
	 * Gets the name of this {@link Route}
	 * @return The name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Gets the source {@link Box} of this {@link Route}
	 * @return The source {@link Box}
	 */
	public Box getSrc()
	{
		return src;
	}

	/**
	 * Gets the destination {@link Box} of this {@link Route}
	 * @return The destination {@link Box}
	 */
	public Box getDest()
	{
		return dest;
	}

	/**
	 * Sends items from {@link Route#src} to {@link Route#dest}
	 */
	public void send()
	{
		Chest srcChest = (Chest) src.getPos().getBlock().getState();
		Chest destChest = (Chest) dest.getPos().getBlock().getState();
		if (!srcChest.getBlockInventory().isEmpty())
		{
			if (Endermail.getPlugin(Endermail.class).doesVaultExist() && !src.isGlobal())
			{
				if (Endermail.getEconomy().getBalance(src.getOwner()) >= getCost())
				{
					Endermail.getEconomy().withdrawPlayer(src.getOwner(), getCost());
					transfer(srcChest.getBlockInventory(), destChest.getBlockInventory());
					src.getOwner().sendMessage(Endermail.chatPrefix + ChatColor.AQUA + "Mail sent!\n" + ChatColor.GREEN + "Cost: " + ChatColor.YELLOW + Endermail.getEconomy().format(getCost()));
					if (!dest.isGlobal())
					{
						dest.getOwner().sendMessage(Endermail.chatPrefix + ChatColor.AQUA + "You got mail!");
					} else
					{
						Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).forEach(p -> p.sendMessage(Endermail.chatPrefix + ChatColor.AQUA + "Server got mail!"));
					}
				} else
				{
					src.getOwner().sendMessage(Endermail.chatPrefix + ChatColor.RED + "ERROR: insufficient funds");
					;
				}
			} else
			{
				transfer(srcChest.getBlockInventory(), destChest.getBlockInventory());
				if (!src.isGlobal())
				{
					src.getOwner().sendMessage(Endermail.chatPrefix + ChatColor.AQUA + "Mail sent!");
				} else
				{
					Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).forEach(p -> p.sendMessage(Endermail.chatPrefix + ChatColor.AQUA + "Server Mail sent!"));
				}
				if (!dest.isGlobal())
				{
					dest.getOwner().sendMessage(Endermail.chatPrefix + ChatColor.AQUA + "You got mail!");
				} else
				{
					Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).forEach(p -> p.sendMessage(Endermail.chatPrefix + ChatColor.AQUA + "Server got mail!"));
				}
			}

			if (isTemp())
			{
				RouteManager.getManager().removeRoute(this.getName());
			}
		}
	}

	/**
	 * Actually preforms the transfer
	 * @param src The source {@link org.bukkit.inventory.Inventory}
	 * @param dest The destination {@link org.bukkit.inventory.Inventory}
	 */
	private void transfer(Inventory src, Inventory dest)
	{
		if (!src.isEmpty())
		{
			ItemStack[] inv = Arrays.stream(src.getContents()).filter(Objects::nonNull).toArray(ItemStack[]::new);
			Map<Integer, ItemStack> overflow = dest.addItem(inv);
			src.clear();
			for (Map.Entry<Integer, ItemStack> entry : overflow.entrySet())
			{
				src.setItem(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * Indicates whether some other object is "equal to" this one.
	 * <p>
	 * The {@code equals} method implements an equivalence relation
	 * on non-null object references:
	 * <ul>
	 * <li>It is <i>reflexive</i>: for any non-null reference value
	 *     {@code x}, {@code x.equals(x)} should return
	 *     {@code true}.
	 * <li>It is <i>symmetric</i>: for any non-null reference values
	 *     {@code x} and {@code y}, {@code x.equals(y)}
	 *     should return {@code true} if and only if
	 *     {@code y.equals(x)} returns {@code true}.
	 * <li>It is <i>transitive</i>: for any non-null reference values
	 *     {@code x}, {@code y}, and {@code z}, if
	 *     {@code x.equals(y)} returns {@code true} and
	 *     {@code y.equals(z)} returns {@code true}, then
	 *     {@code x.equals(z)} should return {@code true}.
	 * <li>It is <i>consistent</i>: for any non-null reference values
	 *     {@code x} and {@code y}, multiple invocations of
	 *     {@code x.equals(y)} consistently return {@code true}
	 *     or consistently return {@code false}, provided no
	 *     information used in {@code equals} comparisons on the
	 *     objects is modified.
	 * <li>For any non-null reference value {@code x},
	 *     {@code x.equals(null)} should return {@code false}.
	 * </ul>
	 * <p>
	 * The {@code equals} method for class {@code Object} implements
	 * the most discriminating possible equivalence relation on objects;
	 * that is, for any non-null reference values {@code x} and
	 * {@code y}, this method returns {@code true} if and only
	 * if {@code x} and {@code y} refer to the same object
	 * ({@code x == y} has the value {@code true}).
	 * <p>
	 * Note that it is generally necessary to override the {@code hashCode}
	 * method whenever this method is overridden, so as to maintain the
	 * general contract for the {@code hashCode} method, which states
	 * that equal objects must have equal hash codes.
	 *
	 * @param   o   the reference object with which to compare.
	 * @return  {@code true} if this object is the same as the obj
	 *          argument; {@code false} otherwise.
	 * @see     #hashCode()
	 * @see     java.util.HashMap
	 */
	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Route route = (Route) o;
		return isTemp() == route.isTemp() && getName().equals(route.getName()) && getSrc().equals(route.getSrc()) && getDest().equals(route.getDest());
	}

	/**
	 * Returns a hash code value for the object. This method is
	 * supported for the benefit of hash tables such as those provided by
	 * {@link java.util.HashMap}.
	 * <p>
	 * The general contract of {@code hashCode} is:
	 * <ul>
	 * <li>Whenever it is invoked on the same object more than once during
	 *     an execution of a Java application, the {@code hashCode} method
	 *     must consistently return the same integer, provided no information
	 *     used in {@code equals} comparisons on the object is modified.
	 *     This integer need not remain consistent from one execution of an
	 *     application to another execution of the same application.
	 * <li>If two objects are equal according to the {@code equals(Object)}
	 *     method, then calling the {@code hashCode} method on each of
	 *     the two objects must produce the same integer result.
	 * <li>It is <em>not</em> required that if two objects are unequal
	 *     according to the {@link java.lang.Object#equals(java.lang.Object)}
	 *     method, then calling the {@code hashCode} method on each of the
	 *     two objects must produce distinct integer results.  However, the
	 *     programmer should be aware that producing distinct integer results
	 *     for unequal objects may improve the performance of hash tables.
	 * </ul>
	 * <p>
	 * As much as is reasonably practical, the hashCode method defined by
	 * class {@code Object} does return distinct integers for distinct
	 * objects. (This is typically implemented by converting the internal
	 * address of the object into an integer, but this implementation
	 * technique is not required by the
	 * Java&trade; programming language.)
	 *
	 * @return  a hash code value for this object.
	 * @see     java.lang.Object#equals(java.lang.Object)
	 * @see     java.lang.System#identityHashCode
	 */
	@Override
	public int hashCode()
	{
		return Objects.hash(getName(), getSrc(), getDest(), isTemp());
	}

	/**
	 * Returns a string representation of the object. In general, the
	 * {@code toString} method returns a string that
	 * "textually represents" this object. The result should
	 * be a concise but informative representation that is easy for a
	 * person to read.
	 * It is recommended that all subclasses override this method.
	 * <p>
	 * The {@code toString} method for class {@code Object}
	 * returns a string consisting of the name of the class of which the
	 * object is an instance, the at-sign character `{@code @}', and
	 * the unsigned hexadecimal representation of the hash code of the
	 * object. In other words, this method returns a string equal to the
	 * value of:
	 * <blockquote>
	 * <pre>
	 * getClass().getName() + '@' + Integer.toHexString(hashCode())
	 * </pre></blockquote>
	 *
	 * @return  a string representation of the object.
	 */
	@Override
	public String toString()
	{
		return "Route{" +
				"name='" + name + '\'' +
				", src=" + src +
				", dest=" + dest +
				", temp=" + temp +
				'}';
	}
}
