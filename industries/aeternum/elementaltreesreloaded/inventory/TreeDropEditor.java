package industries.aeternum.elementaltreesreloaded.inventory;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import industries.aeternum.elementaltreesreloaded.ngui.inventory.BananaHolder;
import industries.aeternum.elementaltreesreloaded.objects.ElementalTreeTemplate;

public class TreeDropEditor extends BananaHolder {
	private ElementalTreeTemplate template;
	private List< ItemStack > items;
	private final Inventory inventory;

	public TreeDropEditor( ElementalTreeTemplate template ) {
		this.template = template;
		items = new ArrayList< ItemStack >( template.getDrops().keySet() );
		inventory = Bukkit.createInventory( this, 27, "Editing tree drops for " + template.getId() );
	}

	@Override
	public Inventory getInventory() {
		inventory.clear();
		for ( int i = 0; i < inventory.getSize(); i++ ) {
			if ( i >= template.getDrops().size() ) {
				break;
			}
			inventory.setItem( i, items.get( i ) );
		}
		return inventory;
	}

	@Override
	public void onInventoryClose( InventoryCloseEvent event ) {
		template.getDrops().clear();
		for ( ItemStack item : inventory.getContents() ) {
			if ( item != null && item.getType() != Material.AIR ) {
				template.getDrops().put( item, 1 );
			}
		}
		event.getPlayer().sendMessage( ChatColor.GREEN + "Saved drops!" );
	}

	@Override
	public void onInventoryClick( InventoryClickEvent event ) {
	}
}
