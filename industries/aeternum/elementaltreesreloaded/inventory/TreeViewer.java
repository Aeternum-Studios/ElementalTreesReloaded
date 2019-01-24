package industries.aeternum.elementaltreesreloaded.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import industries.aeternum.elementaltreesreloaded.ElementalTrees;
import industries.aeternum.elementaltreesreloaded.TreeManager;
import industries.aeternum.elementaltreesreloaded.ngui.inventory.BananaHolder;
import industries.aeternum.elementaltreesreloaded.ngui.items.ItemBuilder;
import industries.aeternum.elementaltreesreloaded.ngui.util.NBTEditor;
import industries.aeternum.elementaltreesreloaded.objects.ElementalTree;

public class TreeViewer extends BananaHolder {
	private static final Object[] CUSTOM = new String[] { "industries", "aeternum", "network", "elementaltreesreloaded", "inventory", "custom" };
	private static final Object[] META1 = new String[] { "industries", "aeternum", "network", "elementaltreesreloaded", "inventory", "meta-1" };
	private static final Object[] META2 = new String[] { "industries", "aeternum", "network", "elementaltreesreloaded", "inventory", "meta-2" };
	
	private Inventory inventory;
	private TreeManager manager;
	private int page = 0;
	
	public TreeViewer( TreeManager manager ) {
		this.manager = manager;
		
		inventory = Bukkit.createInventory( this, 27, "Viewing all trees" );
	}
	
	@Override
	public Inventory getInventory() {
		inventory.clear();
		Map< UUID, Set< ElementalTree > > trees = manager.getUserTrees();
		List< ElementalTree > items = new ArrayList< ElementalTree >();
		for ( Set< ElementalTree > treeList : trees.values() ) {
			items.addAll( treeList );
		}
		
		int maxPages = ( int ) Math.ceil( items.size() / ( double ) ( inventory.getSize() - 9 ) );
		for ( int i = 0; i < 9; i++ ) {
			ItemStack item;
			if ( i == 0 && page > 0 ) {
				item = new ItemBuilder( Material.ARROW, 1, ( byte ) 0, ChatColor.WHITE + "Previous page" ).addFlags( ItemFlag.values() ).getItem();
				item = NBTEditor.setItemTag( item, "prev page", META1 );
			} else if ( i == 8 && page < maxPages - 1 ) {
				item = new ItemBuilder( Material.ARROW, 1, ( byte ) 0, ChatColor.WHITE + "Next page" ).addFlags( ItemFlag.values() ).getItem();
				item = NBTEditor.setItemTag( item, "next page", META1 );
			} else {
				item  = new ItemBuilder( Material.BLACK_STAINED_GLASS_PANE, 1, ( byte ) 7, " ", false ).addFlags( ItemFlag.values() ).getItem();
			}
			item = NBTEditor.setItemTag( item, ( byte ) 1, CUSTOM );
			inventory.setItem( inventory.getSize() - ( 9 - i ), item );
		}
		int startIndex = page * ( inventory.getSize() - 9 );
		for ( int i = 0; i < inventory.getSize() - 9; i++ ) {
			if ( i + startIndex >= items.size() ) {
				break;
			}
			ElementalTree tree = items.get( i + startIndex );
			ItemStack item = tree.getTemplate().getItem();

			ItemMeta meta = item.getItemMeta();
			
			List< String > lore = meta.hasLore() ? meta.getLore() : new ArrayList< String >();
			
			lore.add( "" );
			lore.add( ChatColor.WHITE + "Owner: " + ChatColor.YELLOW + Bukkit.getOfflinePlayer( tree.getOwner() ).getName() );
			lore.add( "" );
			lore.add( ChatColor.GRAY + ChatColor.BOLD.toString() + "Left click" + ChatColor.GRAY + " to teleport" );
			lore.add( ChatColor.GRAY + ChatColor.BOLD.toString() + "Right click" + ChatColor.GRAY + " to delete" );
			
			meta.setLore( lore );
			
			item.setItemMeta( meta );
			
			item = NBTEditor.setItemTag( item, "edit tree", META1 );
			item = NBTEditor.setItemTag( item, tree.getUUID().toString(), META2 );
			item = NBTEditor.setItemTag( item, ( byte ) 1, CUSTOM );
			inventory.addItem( item );
		}
		
		return inventory;
	}

	@Override
	public void onInventoryClick( InventoryClickEvent event ) {
		ClickType click = event.getClick();
		if ( event.getRawSlot() != event.getSlot() ) {
			if ( click.isKeyboardClick() || click.isShiftClick() ) {
				event.setCancelled( true );
			}
			return;
		}
		event.setCancelled( true );
		ItemStack item = event.getCurrentItem();
		
		if ( item != null && NBTEditor.getItemTag( item, CUSTOM ) != null ) {
			String meta = ( String ) NBTEditor.getItemTag( item, META1 );
			if ( meta == null ) {
				return;
			}
			if ( meta.equalsIgnoreCase( "next page" ) ) {
				page++;
			} else if ( meta.equalsIgnoreCase( "prev page" ) ) {
				page = Math.max( 0, page - 1 );
			} else if ( meta.equalsIgnoreCase( "edit tree" ) ) {
				UUID uuid = UUID.fromString( ( String ) NBTEditor.getItemTag( item, META2 ) );
				for ( ElementalTree tree : manager.getTrees() ) {
					if ( tree.getUUID().equals( uuid ) ) {
						if ( click == ClickType.RIGHT ) {
							tree.remove();
						} else if ( click == ClickType.LEFT ) {
							event.getWhoClicked().teleport( tree.getBase() );
						}
						break;
					}
				}
			}
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask( ElementalTrees.getInstance(), this::getInventory, 2 );
	}

}
