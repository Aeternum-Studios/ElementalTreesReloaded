package industries.aeternum.elementaltreesreloaded;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import industries.aeternum.elementaltreesreloaded.dependencies.WorldGuardDependency;
import industries.aeternum.elementaltreesreloaded.ngui.util.NBTEditor;
import industries.aeternum.elementaltreesreloaded.objects.ElementalTree;
import industries.aeternum.elementaltreesreloaded.objects.ElementalTreeTemplate;
import industries.aeternum.elementaltreesreloaded.objects.animation.BlockAnimation;
import industries.aeternum.elementaltreesreloaded.objects.animation.FastBuildAnimation;

public class TreeItemTracker {
	private ElementalTrees plugin;
	
	private Map< Item, UUID > tracking = new WeakHashMap< Item, UUID >();
	
	public TreeItemTracker( ElementalTrees plugin ) {
		this.plugin = plugin;	
	}
	
	protected void update() {
		for ( Item item : tracking.keySet() ) {
			if ( item.isValid() ) {
				ItemStack itemStack = item.getItemStack();
				ElementalTreeTemplate tree = TreeCache.INSTANCE.getTemplate( ( String ) NBTEditor.getItemTag( itemStack, TreeCache.ID ) );
				
				if ( tree != null ) {
					UUID uuid = tracking.get( item );
					Player player = Bukkit.getPlayer( uuid );
					if ( !player.hasPermission( "etree.grow." + tree.getId() ) ) {
						continue;
					}
					if ( !player.hasPermission( "etree.bypass" ) && plugin.getTreeManager().getTrees( uuid ).size() >= plugin.getMaxTreesPerPlayer() ) {
						continue;
					}
					if ( player != null && tree.canGrowAt( item.getLocation() ) && WorldGuardDependency.canBuild( player, item.getLocation() ) ) {
						BlockAnimation animation = new FastBuildAnimation( 100 );
						ElementalTree newTree = new ElementalTree( item.getLocation().getBlock().getLocation(), tree, animation, tracking.get( item ) );
						
						plugin.getTreeManager().registerTree( newTree );
						
						item.remove();
					}
				}
			}
		}
	}
	
	protected void trackItem( Item item, Player owner ) {
		tracking.put( item, owner.getUniqueId() );
	}
}
