package industries.aeternum.elementaltreesreloaded.objects.animation;

import org.bukkit.Location;

import industries.aeternum.elementaltreesreloaded.objects.ElementalTree;

public interface BlockAnimation {
	void startAnimation( ElementalTree tree );
	boolean update();
	void stopAnimation();
}
