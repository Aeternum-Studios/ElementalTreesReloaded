package industries.aeternum.elementaltreesreloaded.objects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;

public class TreeModel {
	protected List< String[] > model;
	protected Map< Character, Material > materials = new HashMap< Character, Material >();
	protected char log;
	protected char leaf;
	
	protected int width;
	protected int height;
	
	public TreeModel( List< String[] > model, char log, char leaf ) {
		this.model = model;
		this.log = log;
		this.leaf = leaf;
		
		if ( model.isEmpty() ) {
			throw new IllegalArgumentException( "Tree model cannot be empty!" );
		}
		
		String[] layer1 = model.get( 0 );
		width = layer1.length;
		height = layer1[ 0 ].length();
	}
	
	public TreeModel setMaterial( char character, Material material ) {
		materials.put( character, material );
		return this;
	}
	
	public Map< Location, Material >[] getSpawnAt( Location center, Material logMat, Material leafMat ) {
		center = center.getBlock().getLocation();
		Map< Location, Material >[] data = new Map[ model.size() ];
		
		int y = 0;
		for ( String[] layer : model ) {
			Location left = center.clone().subtract( layer.length / 2, - y, 0 );
			Map< Location, Material > layerMap = new HashMap< Location, Material >();
			for ( int rowIndex = 0; rowIndex < layer.length; rowIndex++ ) {
				Location relativeRow = left.clone().subtract( 0, 0, layer.length / 2 - rowIndex );
				
				String row = layer[ rowIndex ];
				for ( int index = 0; index < row.length(); index++ ) {
					Location finalRow = relativeRow.clone().add( index, 0, 0 );
					char character = row.charAt( index );
					
					if ( character == log ) {
						layerMap.put( finalRow, logMat );
					} else if ( character == leaf ) {
						layerMap.put( finalRow, leafMat );
					} else if ( materials.containsKey( character ) ) {
						layerMap.put( finalRow, materials.get( character ) );
					}
				}
			}
			data[ y++ ] = layerMap;
		}
		
		return data;
	}
}
