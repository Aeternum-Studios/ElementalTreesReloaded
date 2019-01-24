package industries.aeternum.elementaltreesreloaded.objects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;

public class TreeComponents {
	Set< Location > blocks = new HashSet< Location >();
	List< Location > logs = new ArrayList< Location >();
	List< Location > leaves = new ArrayList< Location >();
	
	public Set< Location > getBlocks() {
		return blocks;
	}

	public List< Location > getLogs() {
		return logs;
	}

	public List< Location > getLeaves() {
		return leaves;
	}
	
	public void remove( Location location ) {
		blocks.remove( location );
		logs.remove( location );
		leaves.remove( location );
	}
	
	public boolean isEmpty() {
		return blocks.isEmpty();
	}
}
