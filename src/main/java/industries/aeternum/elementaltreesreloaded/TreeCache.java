package industries.aeternum.elementaltreesreloaded;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Enums;

import industries.aeternum.elementaltreesreloaded.objects.ElementalTree;
import industries.aeternum.elementaltreesreloaded.objects.ElementalTreeTemplate;
import industries.aeternum.elementaltreesreloaded.objects.TreeComponents;
import industries.aeternum.elementaltreesreloaded.objects.TreeModel;
import industries.aeternum.elementaltreesreloaded.util.BukkitUtil;
import industries.aeternum.elementaltreesreloaded.util.Util;

public enum TreeCache {
	INSTANCE;
	public static final Object[] CUSTOM = new String[] { "industries", "aeternum", "elementaltreesreloaded", "item", "custom" };
	public static final Object[] ID = new String[] { "industries", "aeternum", "elementaltreesreloaded", "item", "id" };
	public static final Object[] OWNER = new String[] { "industries", "aeternum", "elementaltreesreloaded", "item", "owner" };
	
	protected Map< String, TreeModel > models = new HashMap< String, TreeModel >();
	protected Map< String, ElementalTreeTemplate > templates = new HashMap< String, ElementalTreeTemplate >();
	
	public TreeModel getModel( String name ) {
		return models.get( name );
	}
	
	public void registerModel( String name, TreeModel model ) {
		models.put( name, model );
	}
	
	public ElementalTreeTemplate getTemplate( String id ) {
		return templates.get( id );
	}
	
	public void registerTemplate( ElementalTreeTemplate template ) {
		templates.put( template.getId(), template );
	}
	
	protected Collection< ElementalTreeTemplate > getTemplates() {
		return templates.values();
	}
	
	public static TreeModel loadTreeModel( FileConfiguration config ) {
		ConfigurationSection section = config.getConfigurationSection( "layers" );
		Validate.notNull( section, "Layers section does not exist!" );
		ConfigurationSection data = config.getConfigurationSection( "data" );
		Validate.notNull( data, "Data section does not exist!" );
		
		List< String[] > layers = new ArrayList< String[] >();
		for ( String key : section.getKeys( false ) ) {
			List< String > values = section.getStringList( key );
			String[] rows = new String[ values.size() ];
			layers.add( values.toArray( rows ) );
		}
		
		char defLog = data.getString( "log" ).charAt( 0 );
		char defLeaves = data.getString( "leaves" ).charAt( 0 );
		
		TreeModel model = new TreeModel( layers, defLog, defLeaves );
		
		ConfigurationSection additional = data.getConfigurationSection( "additional-data" );
		if ( additional != null ) {
			for ( String key : additional.getKeys( false ) ) {
				model.setMaterial( key.charAt( 0 ), Material.valueOf( additional.getString( key ).toUpperCase() ) );
			}
		}
		
		return model;
	}
	
	protected void clearCache() {
		models.clear();
		templates.clear();
	}
	
	public static ElementalTreeTemplate loadTreeTemplate( String id, FileConfiguration config ) {
		String model = config.getString( "model" );
		String name = config.getString( "name" ).replace( '&', '\u00a7' );
		
		Material log = Material.valueOf( config.getString( "log" ).toUpperCase() );
		Material leaves = Material.valueOf( config.getString( "leaves" ).toUpperCase() );
		Material sapling = Material.valueOf( config.getString( "item-material" ).toUpperCase() );
		
		int growthSpeed = config.getInt( "growth-speed" );
		int dropSpeed = config.getInt( "drop-speed" );
		boolean decay = config.getBoolean( "leaf-decay" );
		
		boolean dropSapling = config.getBoolean( "drop-sapling" );

		int xp = config.getInt( "xp-drop-on-finish-growing" );
		boolean lightning = config.getBoolean( "play-lightning-animation" );
		Particle particle = Util.getEnum( Particle.class, config.getString( "particle-type" ) );
		
		ElementalTreeTemplate template = new ElementalTreeTemplate( id, name );

		for ( String mat : config.getStringList( "grows-on" ) ) {
			template.addGrowsOn( Material.valueOf( mat.toUpperCase() ) );
		}
		
		if ( config.getConfigurationSection( "items" ) != null ) {
			for ( String key : config.getConfigurationSection( "items" ).getKeys( false ) ) {
				ItemStack item = config.getItemStack( "items." + key + ".item" );
				int weight = config.getInt( "items." + key + ".weight" );
				template.getDrops().put( item, weight );
			}
		}
	
		template.setModel( model );
		template.setItemMaterial( sapling );
		template.setDropTree( dropSapling );
		template.setLogData( log );
		template.setLeafData( leaves );
		template.setGrowthSpeed( growthSpeed );
		template.setItemDropDelay( dropSpeed );
		template.setLeafDecay( decay );
		template.setXp( xp );
		template.setPlayLightning( lightning );
		template.setParticle( particle );
		
		return template;
	}
	
	public static void saveTemplateTo( ElementalTreeTemplate template, FileConfiguration config ) {
		config.set( "model", template.getModel() );
		config.set( "name", template.getName().replace( "\u00a7", "&" ) );
		
		config.set( "log", template.getLogData().name() );
		config.set( "leaves", template.getLeafData().name() );
		config.set( "item-material", template.getItem().getType().name() );
		
		config.set( "growth-speed", template.getGrowthSpeed() );
		config.set( "drop-speed", template.getItemDropDelay() );
		
		config.set( "drop-sapling", template.isDropTree() );
		
		config.set( "xp-drop-on-finish-growing", template.getXp() );
		config.set( "play-lightning-animation", template.isPlayLightning() );
		config.set( "particle-type", template.getParticle().name() );
		
		int index = 0;
		config.set( "items", null );
		for ( ItemStack item : template.getDrops().keySet() ) {
			int amount = template.getDrops().get( item );
			
			config.set( "items." + index + ".item", item );
			config.set( "items." + index++ + ".weight", amount );
		}
	}
	
	public static ElementalTree loadTree( ConfigurationSection section ) {
		String template = section.getString( "template" );
		UUID uuid = UUID.fromString( section.getString( "owner" ) );
		UUID id = UUID.fromString( section.getString( "uuid" ) );
		
		TreeComponents components = loadTreeComponents( section.getConfigurationSection( "components" ) );
		
		return new ElementalTree( id, uuid, INSTANCE.getTemplate( template ), components );
	}
	
	public static void saveTree( ElementalTree tree, ConfigurationSection section ) {
		section.set( "template", tree.getTemplate().getId() );
		section.set( "owner", tree.getOwner().toString() );
		section.set( "uuid", tree.getUUID().toString() );
		
		saveTreeComponents( tree.getComponents(), section.createSection( "components" ) );
	}
	
	public static TreeComponents loadTreeComponents( ConfigurationSection section ) {
		TreeComponents components = new TreeComponents();
		
		for ( String str : section.getStringList( "blocks" ) ) {
			components.getBlocks().add( BukkitUtil.toLocation( str ) );
		}
		
		for ( String str : section.getStringList( "logs" ) ) {
			components.getLogs().add( BukkitUtil.toLocation( str ) );
		}
		
		for ( String str : section.getStringList( "leaves" ) ) {
			components.getLeaves().add( BukkitUtil.toLocation( str ) );
		}
		
		return components;
	}
	
	public static void saveTreeComponents( TreeComponents components, ConfigurationSection section ) {
		List< String > blocks = new ArrayList< String >();
		for ( Location loc : components.getBlocks() ) {
			blocks.add( BukkitUtil.toString( loc ) );
		}
		section.set( "blocks", blocks );
		
		List< String > logs = new ArrayList< String >();
		for ( Location loc : components.getLogs() ) {
			logs.add( BukkitUtil.toString( loc ) );
		}
		section.set( "logs", logs );
		
		List< String > leaves = new ArrayList< String >();
		for ( Location loc : components.getLeaves() ) {
			leaves.add( BukkitUtil.toString( loc ) );
		}
		section.set( "leaves", leaves );
	}
}
