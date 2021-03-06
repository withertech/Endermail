package com.withertech.endermail;

import com.withertech.endermail.managers.BoxManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * An object that represents a postbox
 */
@SerializableAs("Box")
public class Box implements ConfigurationSerializable
{
	/**
	 * The name of this {@link Box}
	 */
	private final String name;

	/**
	 * The World this {@link Box} resides in
	 */
	private final String world;

	/**
	 * The Player that owns this {@link Box}, or "server" if global
	 */
	private String owner;

	/**
	 * The visibility scope of this {@link Box}
	 */
	private BoxScope scope;

	/**
	 * The x coordinate this {@link Box} is located at
	 */
	private final int x;

	/**
	 * The y coordinate this {@link Box} is located at
	 */
	private final int y;

	/**
	 * The z coordinate this {@link Box} is located at
	 */
	private final int z;

	/**
	 *
	 * @param name The name of the {@link Box}
	 * @param world The World this {@link Box} resides in
	 * @param owner The Player that owns this {@link Box}, or "server" if global
	 * @param scope The visibility scope ({@link BoxScope}) of the {@link Box}
	 * @param x The x coordinate the {@link Box} is located at
	 * @param y The y coordinate the {@link Box} is located at
	 * @param z The z coordinate the {@link Box} is located at
	 */
	public Box(String name, String world, String owner, BoxScope scope, int x, int y, int z)
	{
		this.name = name;
		this.world = world;
		this.owner = owner;
		if (owner.equals("server"))
			this.scope = BoxScope.PUBLIC;
		else
			this.scope = scope;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Deserialize a {@link Box} from bukkit yaml storage
	 * @param map The serialized data
	 * @return The deserialized {@link Box}
	 */
	public static Box deserialize(Map<String, Object> map)
	{
		return new Box((String) map.get("name"), (String) map.get("world"), (String) map.get("owner"), BoxScope.valueOf((String) map.get("scope")), (int) map.get("x"), (int) map.get("y"), (int) map.get("z"));
	}

	/**
	 * Serialize this {@link Box} for bukkit yaml storage
	 * @return The serialized data
	 */
	@NotNull
	@Override
	public Map<String, Object> serialize()
	{
		HashMap<String, Object> ret = new HashMap<>();
		ret.put("name", name);
		ret.put("world", world);
		ret.put("owner", owner);
		ret.put("scope", scope.name());
		ret.put("x", x);
		ret.put("y", y);
		ret.put("z", z);
		return ret;
	}

	/**
	 * Gets the name of this {@link Box}
	 * @return The name of this {@link Box}
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Gets the {@link org.bukkit.Location} of this {@link Box}
	 * @return The {@link org.bukkit.Location} of this {@link Box}
	 */
	public Location getPos()
	{
		return new Location(getWorld(), this.x, this.y, this.z);
	}

	/**
	 * Gets the {@link org.bukkit.World} of this {@link Box}
	 * @return The {@link org.bukkit.World} of this {@link Box}
	 */
	public World getWorld()
	{
		return Endermail.getPlugin(Endermail.class).getServer().getWorld(this.world);
	}

	/**
	 * Gets the x coordinate of this {@link Box}
	 * @return The x coordinate of this {@link Box}
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * Gets the y coordinate of this {@link Box}
	 * @return The y coordinate of this {@link Box}
	 */
	public int getY()
	{
		return y;
	}

	/**
	 * Gets the z coordinate of this {@link Box}
	 * @return The z coordinate of this {@link Box}
	 */
	public int getZ()
	{
		return z;
	}

	/**
	 * Checks if this {@link Box} is global and owned by the server
	 * @return If the {@link Box} is global
	 */
	public boolean isGlobal()
	{
		return owner.equals("server");
	}

	/**
	 * Gets the current owner of this {@link Box}
	 * @return The current owner, or null if global
	 */
	@Nullable
	public Player getOwner()
	{
		if (!isGlobal())
			return Endermail.getPlugin(Endermail.class).getServer().getPlayer(owner);
		else
			return null;
	}

	/**
	 * Gets the current visibility scope ({@link BoxScope}) of this {@link Box}
	 * @return The current visibility scope ({@link BoxScope})
	 */
	public BoxScope getScope()
	{
		return scope;
	}

	/**
	 * Makes this {@link Box} global and owned by the server
	 */
	public void makeGlobal()
	{
		this.owner = "server";
		this.setScope(BoxScope.PUBLIC);
		BoxManager.getManager().updateBox(this.getName(), this);
	}

	/**
	 * Sets the owner ({@link org.bukkit.entity.Player}) of this {@link Box}
	 * @param owner The new owner ({@link org.bukkit.entity.Player})
	 */
	public void setOwner(Player owner)
	{
		this.owner = owner.getName();
		BoxManager.getManager().updateBox(this.getName(), this);
	}

	/**
	 * Sets the visibility scope ({@link BoxScope}) of this {@link Box}
	 * @param scope The new visibility scope ({@link BoxScope})
	 */
	public void setScope(BoxScope scope)
	{
		this.scope = scope;
		BoxManager.getManager().updateBox(this.getName(), this);
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
		Box box = (Box) o;
		return getX() == box.getX() && getY() == box.getY() && getZ() == box.getZ() && getName().equals(box.getName()) && getWorld().equals(box.getWorld()) && Objects.equals(getOwner(), box.getOwner()) && getScope() == box.getScope();
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
		return Objects.hash(getName(), getWorld(), getOwner(), getScope(), getX(), getY(), getZ());
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
		return "Box{" +
				"name='" + name + '\'' +
				", world='" + world + '\'' +
				", owner='" + owner + '\'' +
				", scope=" + scope +
				", x=" + x +
				", y=" + y +
				", z=" + z +
				'}';
	}

	/**
	 * An enum that represents the Scope that the {@link Box} is limited to.
	 */
	public enum BoxScope
	{
		PUBLIC(),
		PRIVATE()
	}
}
