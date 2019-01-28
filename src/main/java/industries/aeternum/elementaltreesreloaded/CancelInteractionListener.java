package industries.aeternum.elementaltreesreloaded;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import industries.aeternum.elementaltreesreloaded.ngui.util.NBTEditor;

public class CancelInteractionListener implements Listener {
	@EventHandler( priority = EventPriority.LOWEST, ignoreCancelled = true )
	private void onPlayerConsumeItemEvent( PlayerItemConsumeEvent event ) {
		if ( NBTEditor.getItemTag( event.getItem(), TreeCache.CUSTOM ) != null ) {
			event.setCancelled( true );
		}
	}
	
	@EventHandler( priority = EventPriority.LOWEST, ignoreCancelled = true )
	private void onPlayerInteractEvent( PlayerInteractEvent event ) {
		ItemStack item = event.getItem();
		if ( item != null && NBTEditor.getItemTag( event.getItem(), TreeCache.CUSTOM ) != null ) {
			event.setCancelled( true );
		}
	}
}
