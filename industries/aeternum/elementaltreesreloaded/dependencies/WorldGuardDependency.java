package industries.aeternum.elementaltreesreloaded.dependencies;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionQuery;

public final class WorldGuardDependency {
	public static boolean canBuild( Player p, Location l) {
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(l);
        if (!hasBypass(p, l)) {
            return query.testState(loc, WorldGuardPlugin.inst().wrapPlayer(p), Flags.BUILD);
        } else {
            return true;
        }
    }

    public static boolean hasBypass( Player p, Location l ) {
        return WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass( WorldGuardPlugin.inst().wrapPlayer( p ), BukkitAdapter.adapt( l.getWorld() ) );
    }
}
