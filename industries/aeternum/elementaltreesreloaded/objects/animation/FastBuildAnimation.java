package industries.aeternum.elementaltreesreloaded.objects.animation;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;

import industries.aeternum.elementaltreesreloaded.ElementalTrees;
import industries.aeternum.elementaltreesreloaded.TreeCache;
import industries.aeternum.elementaltreesreloaded.dependencies.WorldGuardDependency;
import industries.aeternum.elementaltreesreloaded.objects.ElementalTree;
import industries.aeternum.elementaltreesreloaded.objects.ElementalTreeTemplate;
import industries.aeternum.elementaltreesreloaded.objects.TreeModel;

public class FastBuildAnimation implements BlockAnimation {
	private ElementalTree tree;
	private int tick;
	private double blocks = 0;
	private double[] bpsPerLevel;
	private int duration;
	private int heightMax;
	
	private Player player;
	
	private Map< Location, Material >[] layers;
	
	public FastBuildAnimation( int duration ) {
		this.duration = duration;
	}
	
	@Override
	public void startAnimation( ElementalTree tree ) {
		this.tree = tree;
		ElementalTreeTemplate template = tree.getTemplate();
		TreeModel model = TreeCache.INSTANCE.getModel( template.getModel() );
		
		if ( model != null ) {
			layers = model.getSpawnAt( tree.getBase(), template.getLogData(), template.getLeafData() );
			heightMax = layers.length;
			
			bpsPerLevel = new double[ layers.length ];
			for ( int index = 0; index < layers.length; index++ ) {
				bpsPerLevel[ index ] = layers[ index ].size();
			}
			
			player = Bukkit.getPlayer( tree.getOwner() );
		}
	}

	@Override
	public boolean update() {
		if ( ++tick >= duration ) {
			return true;
		}
		int degree = ( 5 * tick ) % 360;
		
		double height = heightMax * tick / ( double ) duration;
		int index = ( int ) Math.ceil( height ) - 1;
		
		Map< Location, Material > remaining = layers[ index ];
		blocks = bpsPerLevel[ index ] * ( 1 - ( height - index ) );
		
		while ( !remaining.isEmpty() && remaining.size() >= blocks ) {
			Entry< Location, Material > entry = remaining.entrySet().iterator().next();
			Location location = entry.getKey();
			remaining.remove( location );
			
			Block block = location.getBlock();
			if ( ElementalTrees.getInstance().canGrowIn( block.getType() ) && WorldGuardDependency.canBuild( player, location ) ) {
				block.setType( entry.getValue() );
				
				BlockData state = block.getState().getBlockData();
				 if ( state instanceof Leaves ) {
					 ( ( Leaves ) state ).setPersistent( true );
				 }
				 block.setBlockData( state );
				 
				 tree.getComponents().getBlocks().add( location );
					if ( entry.getValue() == tree.getTemplate().getLeafData() ) {
						tree.getComponents().getLeaves().add( location );
					} else if ( entry.getValue() == tree.getTemplate().getLogData() ) {
						tree.getComponents().getLogs().add( location );
					}
			}
		}
		
		double lengthMult = height * 180 / heightMax;
		double mult = Math.sin( Math.toRadians( lengthMult ) );
		
		for ( int i = 0; i < 4; i++ ) {
			double arm = degree + i * 90;
			
			double cos = Math.cos( Math.toRadians( arm ) );
			double sin = Math.sin( Math.toRadians( arm ) );
			
			Location base = tree.getBase().clone().add( 0, height, 0 );
			for ( int length = 0; length < 5; length++ ) {
				Location particle = base.clone();
				particle.getWorld().spawnParticle( tree.getTemplate().getParticle(), particle.getBlockX() + .5 + cos * length * mult, particle.getY(), particle.getBlockZ() + .5 + sin * length * mult, 0, 0, 0, 0, 0 );
			}
		}
		
		return false;
	}

	@Override
	public void stopAnimation() {
		if ( tree.getTemplate().isPlayLightning() ) {
			tree.getBase().getWorld().strikeLightningEffect( tree.getBase() );
		}
		if ( tree.getTemplate().getXp() > 0 ) {
			ExperienceOrb orb = tree.getBase().getWorld().spawn( tree.getBase(), ExperienceOrb.class );
			orb.setExperience( tree.getTemplate().getXp() );
		}
	}

}
