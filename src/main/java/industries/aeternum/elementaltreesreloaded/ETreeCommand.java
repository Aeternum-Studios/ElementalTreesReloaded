package industries.aeternum.elementaltreesreloaded;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import industries.aeternum.elementaltreesreloaded.inventory.TreeDropEditor;
import industries.aeternum.elementaltreesreloaded.inventory.TreeViewer;
import industries.aeternum.elementaltreesreloaded.objects.ElementalTreeTemplate;

public class ETreeCommand implements CommandExecutor, TabCompleter {
	private ElementalTrees plugin;
	
	public ETreeCommand( ElementalTrees plugin ) {
		this.plugin = plugin;
	}
	
	@Override
	public List< String > onTabComplete( CommandSender sender, Command arg1, String arg2, String[] args ) {
		List< String > completions = new ArrayList< String >();
		if ( !sender.hasPermission( "etree.admin" ) ) {
			return completions;
		}
		List< String > suggestions = new ArrayList< String >();
		boolean isPlayer = sender instanceof Player;
		if ( args.length == 1 ) {
			suggestions.add( "reload" );
			suggestions.add( "give" );
			suggestions.add( "list" );
			if ( isPlayer ) {
				suggestions.add( "edit" );
				suggestions.add( "get" );
				suggestions.add( "view" );
			}
		} else if ( args.length == 2 ) {
			if ( args[ 0 ].equalsIgnoreCase( "give" ) ) {
				for ( Player player : Bukkit.getOnlinePlayers() ) {
					suggestions.add( player.getName() );
				}
			} else if ( isPlayer && args[ 0 ].equalsIgnoreCase( "get" ) || args[ 0 ].equalsIgnoreCase( "edit" ) ) {
				for ( ElementalTreeTemplate template : TreeCache.INSTANCE.getTemplates() ) {
					suggestions.add( template.getId() );
				}
			}
		} else if ( args.length == 3 && args[ 0 ].equalsIgnoreCase( "give" ) ) {
			for ( ElementalTreeTemplate template : TreeCache.INSTANCE.getTemplates() ) {
				suggestions.add( template.getId() );
			}
		}
		StringUtil.copyPartialMatches( args[ args.length - 1 ], suggestions, completions);
		Collections.sort( completions );
		return completions;
	}

	@Override
	public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
		try {
			if ( args.length == 0 ) {
				help( sender );
			} else if ( args.length > 0 ) {
				String option = args[ 0 ];
				args = pop( args );
				if ( option.equalsIgnoreCase( "edit" ) ) {
					edit( sender, args );
				} else if ( option.equalsIgnoreCase( "list" ) ) {
					list( sender, args );
				} else if ( option.equalsIgnoreCase( "get" ) ) {
					getTree( sender, args );
				} else if ( option.equalsIgnoreCase( "give" ) ) {
					giveTree( sender, args );
				} else if ( option.equalsIgnoreCase( "reload" ) ) {
					reload( sender, args );
				} else if ( option.equalsIgnoreCase( "view" ) ) {
					viewTrees( sender, args );
				} else {
					help( sender );
				}
			}
		} catch ( IllegalArgumentException exception ) {
			sender.sendMessage( exception.getMessage() );
		}
		return false;
	}

	private void help( CommandSender sender ) {
		Validate.isTrue( sender.hasPermission( "etree.admin" ), ChatColor.RED + "You do not have permission to run this command!" );
		sender.sendMessage( ChatColor.RED + "Incorrect usage! '/etree <edit|get|give|view|reload> ..." );
	}
	
	private void list( CommandSender sender, String[] args ) {
		Validate.isTrue( sender.hasPermission( "etree.admin" ), ChatColor.RED + "You do not have permission to run this command!" );
		
		if ( TreeCache.INSTANCE.templates.isEmpty() ) {
			sender.sendMessage( ChatColor.AQUA + "There are no available trees!" );
		} else {
			sender.sendMessage( ChatColor.AQUA + "Available trees:" );
			for ( String template : TreeCache.INSTANCE.templates.keySet() ) {
				sender.sendMessage( ChatColor.WHITE + "- " + ChatColor.YELLOW + template );
			}
		}

	}
	
	private void edit( CommandSender sender, String[] args ) {
		Validate.isTrue( sender instanceof Player, "You must be a player to run this command!" );
		Validate.isTrue( sender.hasPermission( "etree.admin" ), ChatColor.RED + "You do not have permission to run this command!" );
		Validate.isTrue( args.length == 1, ChatColor.RED + "Please provide a template to edit!" );
		Validate.isTrue( TreeCache.INSTANCE.getTemplate( args[ 0 ] ) != null, ChatColor.RED + "Invalid template provided!" );
		
		ElementalTreeTemplate template = TreeCache.INSTANCE.getTemplate( args[ 0 ] );
		
		Player player = ( Player ) sender;
		
		player.openInventory( new TreeDropEditor( template ).getInventory() );
	}
	
	private void getTree( CommandSender sender, String[] args ) { 
		Validate.isTrue( sender instanceof Player, "You must be a player to run this command!" );
		Validate.isTrue( sender.hasPermission( "etree.admin" ), ChatColor.RED + "You do not have permission to run this command!" );
		Validate.isTrue( args.length == 1, ChatColor.RED + "Please provide a tree to recieve!" );
		Validate.isTrue( TreeCache.INSTANCE.getTemplate( args[ 0 ] ) != null, ChatColor.RED + "Invalid template provided!" );
		
		ElementalTreeTemplate template = TreeCache.INSTANCE.getTemplate( args[ 0 ] );
		
		Player player = ( Player ) sender;
		
		player.sendMessage( ChatColor.AQUA + "You have been given a tree!" );
		player.getInventory().addItem( template.getItem() );
	}
	
	private void giveTree( CommandSender sender, String[] args ) { 
		Validate.isTrue( sender.hasPermission( "etree.admin" ), ChatColor.RED + "You do not have permission to run this command!" );
		Validate.isTrue( args.length == 2, ChatColor.RED + "Incorrect usage: /etree give <player> <tree>" );
		Validate.isTrue( Bukkit.getPlayer( args[ 0 ] ) != null, ChatColor.RED + args[ 0 ] + " is not online right now!" );
		Validate.isTrue( TreeCache.INSTANCE.getTemplate( args[ 1 ] ) != null, ChatColor.RED + "Invalid template provided!" );
		
		ElementalTreeTemplate template = TreeCache.INSTANCE.getTemplate( args[ 1 ] );
		
		Player player = Bukkit.getPlayer( args[ 0 ] );
		
		player.getInventory().addItem( template.getItem() );
	}	
	private void viewTrees( CommandSender sender, String[] args ) {
		Validate.isTrue( sender instanceof Player, "You must be a player to run this command!" );
		Validate.isTrue( sender.hasPermission( "etree.admin" ), ChatColor.RED + "You do not have permission to run this command!" );

		Player player = ( Player ) sender;
		
		player.openInventory( new TreeViewer( ElementalTrees.getInstance().getTreeManager() ).getInventory() );
	}
	
	private void reload( CommandSender sender, String[] args ) {
		Validate.isTrue( sender.hasPermission( "etree.admin" ), ChatColor.RED + "You do not have permission to run this command!" );
		
		sender.sendMessage( ChatColor.AQUA + "Reloading the config..." );
		plugin.reload();
		sender.sendMessage( ChatColor.AQUA + "Done!" );
	}
	
	private String[] pop( String[] array ) {
		String[] array2 = new String[ Math.max( 0, array.length - 1 ) ];
		for ( int i = 1; i < array.length; i++ ) {
			array2[ i - 1 ] = array[ i ];
		}
		return array2;
	}
}
