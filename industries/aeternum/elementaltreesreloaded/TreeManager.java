package industries.aeternum.elementaltreesreloaded;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import industries.aeternum.elementaltreesreloaded.ngui.util.NBTEditor;
import industries.aeternum.elementaltreesreloaded.objects.ElementalTree;
import industries.aeternum.elementaltreesreloaded.objects.ElementalTreeTemplate;

public class TreeManager implements Listener {
	private ElementalTrees plugin;

	protected TreeItemTracker itemTracker;
	
	protected Set< ElementalTree > trees = new HashSet< ElementalTree >();

	protected Map< UUID, Set< ElementalTree > > userTrees = new HashMap< UUID, Set< ElementalTree > >(); 
	
	protected TreeManager( ElementalTrees plugin ) {
		this.plugin = plugin;
		itemTracker = new TreeItemTracker( plugin );
		
		Bukkit.getScheduler().runTaskTimer( plugin, this::update, 0, 1 );
	}
	
	private void update() {
		itemTracker.update();
		
		for ( Iterator< ElementalTree > iterator = trees.iterator(); iterator.hasNext(); ) {
			ElementalTree tree = iterator.next();
			if ( tree.update() ) {
				iterator.remove();
				getTrees( tree.getOwner() ).remove( tree );
			}
		}
	}
	
	protected void blockBreak( Block block ) {
		for ( ElementalTree tree : trees ) {
			tree.onBlockBreak( block.getLocation() );
		}
	}
	
	public void registerTree( ElementalTree tree ) {
		getTrees( tree.getOwner() ).add( tree );
		trees.add( tree );
	}
	
	public Map< UUID, Set< ElementalTree > > getUserTrees() {
		return userTrees;
	}
	
	public Set< ElementalTree > getTrees( UUID uuid ) {
		Set< ElementalTree > userTree = userTrees.get( uuid );
		if ( userTree == null ) {
			userTree = new HashSet< ElementalTree >();
			userTrees.put( uuid, userTree );
		}
		return userTree;
	}
	
	public Set< ElementalTree > getTrees() {
		return trees;
	}
	
	protected void saveTrees( File directory ) {
		directory.mkdirs();
		for ( ElementalTree tree : trees ) {
			File saveFile = new File( directory, tree.getUUID().toString() );
			
			if ( !saveFile.exists() ) {
				try {
					saveFile.createNewFile();
				} catch ( IOException e ) {
					System.out.println( "Unable to create a new file! Aborting..." );
					e.printStackTrace();
					continue;
				}
			}
			
			FileConfiguration config = YamlConfiguration.loadConfiguration( saveFile );
			
			TreeCache.saveTree( tree, config );
			
			try {
				config.save( saveFile );
			} catch ( IOException e ) {
				System.out.println( "Something went wrong while trying to save a tree to file!" );
				e.printStackTrace();
			}
		}
	}
	
	protected void loadTrees( File directory ) {
		if ( !directory.exists() ) {
			return;
		}
		for ( File file : directory.listFiles() ) {
			FileConfiguration config = YamlConfiguration.loadConfiguration( file );
			
			ElementalTree tree = TreeCache.loadTree( config );
			
			registerTree( tree );
			
			file.delete();
		}
	}
	
	@EventHandler( priority = EventPriority.LOWEST, ignoreCancelled = true )
	private void onBlockBreakEvent( BlockBreakEvent event ) {
		blockBreak( event.getBlock() );
	}
	
	@EventHandler( priority = EventPriority.LOWEST, ignoreCancelled = true )
	private void onBlockBreakEvent( EntityExplodeEvent event ) {
		for ( Block block : event.blockList() ) {
			blockBreak( block );
		}
	}
	
	@EventHandler( priority = EventPriority.LOWEST, ignoreCancelled = true )
	private void onBlockBreakEvent( BlockExplodeEvent event ) {
		for ( Block block : event.blockList() ) {
			blockBreak( block );
		}
	}
	
	@EventHandler( priority = EventPriority.LOWEST, ignoreCancelled = true )
	private void onItemDrop( PlayerDropItemEvent event ) {
		Item itemEntity = event.getItemDrop();
		ItemStack item = itemEntity.getItemStack();
		if ( NBTEditor.getItemTag( item, TreeCache.CUSTOM ) != null ) {
			ElementalTreeTemplate tree = TreeCache.INSTANCE.getTemplate( ( String ) NBTEditor.getItemTag( item, TreeCache.ID ) );
			if ( tree != null ) {
				item = NBTEditor.setItemTag( item, event.getPlayer().getUniqueId().toString(), TreeCache.OWNER );
				itemEntity.setItemStack( item );
				
				itemTracker.trackItem( itemEntity, event.getPlayer() );
			}
		}
	}
	
	@EventHandler( priority = EventPriority.LOWEST, ignoreCancelled = true )
	private void onItemMergeEvent( ItemMergeEvent event ) {
		if ( NBTEditor.getItemTag( event.getEntity().getItemStack(), TreeCache.CUSTOM ) != null ) {
			event.setCancelled( true );
		}
	}
}