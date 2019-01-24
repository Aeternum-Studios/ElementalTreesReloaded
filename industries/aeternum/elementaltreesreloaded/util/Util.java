package industries.aeternum.elementaltreesreloaded.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockCanBuildEvent;

public class Util {

	public static < T > T getRandom( Map< T, Integer > objects ) {
		int sum = 0;
		for ( int i : objects.values() ) {
			sum = sum + i;
		}
		List< T > items = new ArrayList< T >( objects.keySet() );
		int randomIndex = -1;
		double random = Math.random() * sum;
		for ( int i = 0; i < items.size(); ++i ) {
		    random -= objects.get( items.get( i ) );
		    if (random <= 0.0d )  {
		        randomIndex = i;
		        break;
		    }
		}
		return items.get( randomIndex );
	}
	
	public static boolean canBuild( Location location, Material material, Player player ) {
		BlockCanBuildEvent event = new BlockCanBuildEvent( location.getBlock(), player, material.createBlockData(), true );
		
		Bukkit.getPluginManager().callEvent( event );
		
		return event.isBuildable();
	}
	
	public static < T > T getEnum( Class< T > clazz, String value ) {
		if ( !clazz.isEnum() ) return null;
		if ( value == null ) return clazz.getEnumConstants()[ 0 ];
		for ( Object object : clazz.getEnumConstants() ) {
			if ( object.toString().equals( value ) ) {
				return ( T ) object;
			}
		}
		return clazz.getEnumConstants()[ 0 ];
	}
}
