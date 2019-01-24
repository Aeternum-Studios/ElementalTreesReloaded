package industries.aeternum.elementaltreesreloaded;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import industries.aeternum.elementaltreesreloaded.ngui.ClickListener;
import industries.aeternum.elementaltreesreloaded.ngui.NGui;
import industries.aeternum.elementaltreesreloaded.objects.ElementalTreeTemplate;
import industries.aeternum.elementaltreesreloaded.util.FileUtil;

public class ElementalTrees extends JavaPlugin {
	private static ElementalTrees INSTANCE;

	private TreeManager treeManager;
	
	private Set< Material > materials = EnumSet.noneOf( Material.class );
	
	private int limit;
	
	@Override
	public void onEnable() {
		INSTANCE = this;
		
		saveDefaultConfig();
		loadConfig();
		
		File readme = new File( getDataFolder() + "/" + "README.md" );
		if ( !readme.exists() ) {
			FileUtil.saveToFile( getResource( "README.md" ), readme, true );
			
			FileUtil.saveToFile( getResource( "data/models/default-model.yml" ), new File( getDataFolder() + "/models/" + "default-model.yml" ), false );
			FileUtil.saveToFile( getResource( "data/models/pine-model.yml" ), new File( getDataFolder() + "/models/" + "pine-model.yml" ), false );

			FileUtil.saveToFile( getResource( "data/trees/apple-tree.yml" ), new File( getDataFolder() + "/templates/" + "apple-tree.yml" ), false );
			FileUtil.saveToFile( getResource( "data/trees/gold-tree.yml" ), new File( getDataFolder() + "/templates/" + "gold-tree.yml" ), false );
		}
		
		loadModels( new File( getDataFolder() + "/models/" ) );
		loadTreeTemplates( new File( getDataFolder() + "/templates/" ) );
		
		treeManager = new TreeManager( this );
		treeManager.loadTrees( new File( getDataFolder() + "/" + "saves" ) );
		
		Bukkit.getPluginManager().registerEvents( new CancelInteractionListener(), this );
		Bukkit.getPluginManager().registerEvents( treeManager, this );
		Bukkit.getPluginManager().registerEvents( new ClickListener(), this );
		
		ETreeCommand command = new ETreeCommand( this );
		getCommand( "etree" ).setExecutor( command );
		getCommand( "etree" ).setTabCompleter( command );
	}
	
	@Override
	public void onDisable() {
		NGui.disable();
		saveToConfig();
	}
	
	private void loadConfig() {
		FileConfiguration config = YamlConfiguration.loadConfiguration( new File( getDataFolder() + "/" + "config.yml" ) );
		for ( String material : config.getStringList( "grows-through" ) ) {
			materials.add( Material.valueOf( material ) );
		}
		limit = config.getInt( "tree-limit" );
	}
	
	public void reload() {
		TreeCache.INSTANCE.clearCache();
		loadConfig();
		
		loadModels( new File( getDataFolder() + "/models/" ) );
		loadTreeTemplates( new File( getDataFolder() + "/templates/" ) );
	}
	
	private void loadModels( File directory ) {
		if ( directory.exists() ) {
			for ( File file : directory.listFiles() ) {
				if ( file.isFile() ) {
					FileConfiguration config = YamlConfiguration.loadConfiguration( file );
					
					TreeCache.INSTANCE.registerModel( file.getName().replaceFirst( "\\.y[a]?ml$", "" ), TreeCache.loadTreeModel( config ) );
				}
			}
		}
	}
	
	private void saveToConfig() {
		for ( ElementalTreeTemplate template : TreeCache.INSTANCE.getTemplates() ) {
			File saveFile = new File( getDataFolder() + "/templates/" + template.getId() + ".yml" );
			
			if ( !saveFile.exists() ) {
				try {
					saveFile.createNewFile();
				} catch ( IOException e ) {
					e.printStackTrace();
				}
			}
			
			FileConfiguration config = YamlConfiguration.loadConfiguration( saveFile );
			TreeCache.saveTemplateTo( template, config );
			
			try {
				config.save( saveFile );
			} catch ( IOException e ) {
				e.printStackTrace();
			}
		}
		
		treeManager.saveTrees( new File( getDataFolder() + "/" + "saves" ) );
	}
	
	private void loadTreeTemplates( File directory ) {
		if ( directory.exists() ) {
			for ( File file : directory.listFiles() ) {
				if ( file.isFile() ) {
					FileConfiguration config = YamlConfiguration.loadConfiguration( file );
					String id = file.getName().replaceFirst( "\\.y[a]?ml$", "" );
					
					TreeCache.INSTANCE.registerTemplate( TreeCache.loadTreeTemplate( id, config ) );
				}
			}
		}
	}
	
	public File getUserFile( UUID uuid ) {
		String subFolder = uuid.toString().substring( 0, 2 );
		
		return new File( getDataFolder() + "/" + "trees" + "/" + subFolder + "/" + uuid.toString() );
	}
	
	public TreeManager getTreeManager() {
		return treeManager;
	}
	
	protected int getMaxTreesPerPlayer() {
		return limit;
	}
	
	public boolean canGrowIn( Material material ) {
		return materials.contains( material );
	}
	
	public static ElementalTrees getInstance() {
		return INSTANCE;
	}
}
