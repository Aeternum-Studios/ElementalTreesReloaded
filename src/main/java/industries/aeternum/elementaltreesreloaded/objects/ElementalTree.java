package industries.aeternum.elementaltreesreloaded.objects;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import industries.aeternum.elementaltreesreloaded.objects.animation.BlockAnimation;
import industries.aeternum.elementaltreesreloaded.util.Util;

@SuppressWarnings("Duplicates")
public class ElementalTree {
	protected TreeComponents components;
	protected ElementalTreeTemplate template;
	protected Location base;
	protected UUID owner;
	protected UUID uuid;
	
	private boolean remove = false;
	
	private int tick = 0;
	
	private Random random = ThreadLocalRandom.current();
	
	private BlockAnimation animation;
	
	public ElementalTree( Location base, ElementalTreeTemplate template, BlockAnimation animation, UUID owner ) {
		this.owner = owner;
		this.base = base;
		components = new TreeComponents();
		this.template = template;
		uuid = UUID.randomUUID();
		
		this.animation = animation;
		
		animation.startAnimation( this );
	}
	
	public ElementalTree( UUID id, UUID owner, ElementalTreeTemplate template, TreeComponents components ) {
		this.owner = owner;
		this.template = template;
		this.components = components;
		uuid = id;
	}
	
	public ElementalTreeTemplate getTemplate() {
		return template;
	}
	
	public TreeComponents getComponents() {
		return components;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public UUID getOwner() {
		return owner;
	}
	
	public Location getBase() {
		return base;
	}
	
	public boolean update() {
		if ( remove ) {
			return true;
		}
		if ( animation != null ) {
			if ( animation.update() ) {
				animation.stopAnimation();
				animation = null;
			}
		} else {
			tick = ( tick + 1 ) % template.getItemDropDelay();
			
			if ( components.getBlocks().isEmpty() ) {
				return true;
			}
			
			if ( components.getLogs().isEmpty() ) {
				if ( template.isLeafDecay() ) {
					Location block = components.getBlocks().iterator().next();
					block.getBlock().setType( Material.AIR );
					components.getBlocks().remove( block );
				}
			} else if ( tick == 0 ) {
				if ( !template.getDrops().isEmpty() ) {
					ItemStack item = Util.getRandom( template.getDrops() );
					
					if ( item != null && !components.getLeaves().isEmpty() ) {
						Location location = components.getLeaves().get( random.nextInt( components.getLeaves().size() ) ).clone().add( .5, .5, .5 );
						location.getWorld().dropItem( location, item );
					}
				}
			}
		}
		return false;
	}
	
	public void onBlockBreak( BlockBreakEvent event ) {
		Block b = event.getBlock();

		if ( animation == null && components.getBlocks().contains( b.getLocation() ) ) {
			// There's probably a better way to do this but it works.
			components.getLogs().clear();

			if ( template.isDropTree() ) {
				b.getWorld().dropItem( b.getLocation().clone().add( .5, .5, .5 ), template.getItem() );
			}
		}

		if( animation != null && components.getBlocks().contains( b.getLocation() ) ) {
			event.setCancelled(true);
		}
	}

	public void onBlockBreak( BlockExplodeEvent event ) {
		for( Block b : event.blockList() ) {
			if ( animation == null && components.getBlocks().contains( b.getLocation() ) ) {
				components.getLogs().clear();

				if ( template.isDropTree() ) {
					b.getWorld().dropItem( b.getLocation().clone().add( .5, .5, .5 ), template.getItem() );
				}

				break;
			}

			if( animation != null && components.getBlocks().contains( b.getLocation() ) ) {
				event.setCancelled(true);
			}
		}
	}

	public void onBlockBreak( EntityExplodeEvent event ) {
		for( Block b : event.blockList() ) {
			if ( animation == null && components.getBlocks().contains( b.getLocation() ) ) {
				components.getLogs().clear();

				if ( template.isDropTree() ) {
					b.getWorld().dropItem( b.getLocation().clone().add( .5, .5, .5 ), template.getItem() );
				}

				break;
			}

			if( animation != null && components.getBlocks().contains( b.getLocation() ) ) {
				event.setCancelled(true);
			}
		}
	}
	
	public void remove() {
		remove = true;
	}
}
