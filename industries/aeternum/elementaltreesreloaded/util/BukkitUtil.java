package industries.aeternum.elementaltreesreloaded.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class BukkitUtil {
	public static Location toLocation( String string ) {
		String[] ll = string.split( "%" );
		return new Location( Bukkit.getWorld( ll[ 0 ] ), Integer.parseInt( ll[ 1 ] ), Integer.parseInt( ll[ 2 ] ), Integer.parseInt( ll[ 3 ] ) );
	}
	
	public static String toString( Location location ) {
		return location.getWorld().getName() + "%" + location.getBlockX() + "%" + location.getBlockY() + "%" + location.getBlockZ();
	}
}
