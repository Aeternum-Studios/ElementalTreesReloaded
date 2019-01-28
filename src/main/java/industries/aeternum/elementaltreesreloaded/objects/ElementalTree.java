package industries.aeternum.elementaltreesreloaded.objects;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import industries.aeternum.elementaltreesreloaded.objects.animation.BlockAnimation;
import industries.aeternum.elementaltreesreloaded.util.Util;

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
	
	public void onBlockBreak( Location location ) {
		if ( animation == null && components.getLogs().contains( location ) ) {
			components.remove( location );
			if ( components.getLogs().isEmpty() ) {
				if ( template.isDropTree() ) {
					location.getWorld().dropItem( location.clone().add( .5, .5, .5 ), template.getItem() );
				}
			}
		} else {
			components.remove( location );
		}
	}
	
	public void remove() {
		remove = true;
	}
}
