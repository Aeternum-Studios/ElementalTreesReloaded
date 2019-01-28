package industries.aeternum.elementaltreesreloaded.objects;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import industries.aeternum.elementaltreesreloaded.ElementalTrees;
import industries.aeternum.elementaltreesreloaded.TreeCache;
import industries.aeternum.elementaltreesreloaded.ngui.util.NBTEditor;

public class ElementalTreeTemplate {
	protected String name;
	protected final String id;
	protected String model;
	protected Material logData;
	protected Material leafData;
	protected Set< Material > growsOn = EnumSet.noneOf( Material.class );
	protected Map< ItemStack, Integer > items = new HashMap< ItemStack, Integer >();
	
	protected Material item;
	
	protected boolean dropTree = false;
	protected boolean leafDecay = false;
	protected boolean playLightning = false;
	
	protected int growthSpeed = 10;
	protected int itemDropDelay = 200;
	
	protected Particle particle;
	
	protected int xp = 0;
	
	public ElementalTreeTemplate( String id, String name ) {
		this.id = id;
		this.name = name;
	}

	public String getModel() {
		return model;
	}
	
	public void setModel( String model ) {
		this.model = model;
	}
	
	public Material getLogData() {
		return logData;
	}

	public void setLogData( Material logData ) {
		this.logData = logData;
	}

	public Material getLeafData() {
		return leafData;
	}

	public void setLeafData( Material leafData ) {
		this.leafData = leafData;
	}

	public boolean isDropTree() {
		return dropTree;
	}

	public void setDropTree( boolean dropTree ) {
		this.dropTree = dropTree;
	}

	public boolean isLeafDecay() {
		return leafDecay;
	}

	public void setLeafDecay(boolean leafDecay) {
		this.leafDecay = leafDecay;
	}

	public void setItemMaterial( Material material ) {
		item = material;
	}
	
	public int getGrowthSpeed() {
		return growthSpeed;
	}

	public void setGrowthSpeed( int growthSpeed ) {
		this.growthSpeed = growthSpeed;
	}

	public int getItemDropDelay() {
		return itemDropDelay;
	}

	public void setItemDropDelay(int itemDropDelay) {
		this.itemDropDelay = itemDropDelay;
	}
	
	public boolean isPlayLightning() {
		return playLightning;
	}

	public void setPlayLightning( boolean playLightning ) {
		this.playLightning = playLightning;
	}

	public Particle getParticle() {
		return particle;
	}

	public void setParticle( Particle particle ) {
		this.particle = particle;
	}

	public int getXp() {
		return xp;
	}

	public void setXp( int xp ) {
		this.xp = xp;
	}

	public ItemStack getItem() {
		ItemStack itemstack = new ItemStack( item );
		ItemMeta meta = itemstack.getItemMeta();
		meta.setDisplayName( name );
		
		List< String > lore = new ArrayList< String >();
		lore.add( ChatColor.YELLOW + "Can be planted on:" );
		for ( Material material : growsOn ) {
			String name = ( material.name().substring( 0, 1 ).toUpperCase() + material.name().substring( 1 ).toLowerCase() ).replace( '_', ' ' );
			lore.add( ChatColor.GRAY + "- " + ChatColor.LIGHT_PURPLE + name );
		}
		meta.setLore( lore );
		itemstack.setItemMeta( meta );

		itemstack = NBTEditor.setItemTag( itemstack, ( byte ) 1, TreeCache.CUSTOM );
		itemstack = NBTEditor.setItemTag( itemstack, id, TreeCache.ID );
		itemstack = NBTEditor.setItemTag( itemstack, UUID.randomUUID().toString(), "uuid" );
		
		return itemstack;
	}

	public void addGrowsOn( Material material ) {
		growsOn.add( material );
	}
	
	public Set< Material > getGrowsOn() {
		return growsOn;
	}
	
	public Map< ItemStack, Integer > getDrops() {
		return items;
	}
	
	public boolean canGrowAt( Location location ) {
		Location cloned = location.clone().subtract( 0, .1, 0 );
		return growsOn.contains( cloned.getBlock().getType() ) && ElementalTrees.getInstance().canGrowIn( location.getBlock().getType() );
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}
}
