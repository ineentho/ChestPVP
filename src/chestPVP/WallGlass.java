package chestPVP;

import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.block.design.GenericCubeBlockDesign;
import org.getspout.spoutapi.block.design.Texture;
import org.getspout.spoutapi.material.block.GenericCubeCustomBlock;

public class WallGlass extends GenericCubeCustomBlock {
	public WallGlass(Plugin plugin) {
		super(plugin, "WallGlass", 20, getDesign(plugin, 0));
		this.setOpaque(false);
		this.setHardness(Float.MAX_VALUE);
		this.setRotate(true);
		this.setBlockDesign(getDesign(plugin, 1),1);
		this.setBlockDesign(getDesign(plugin, 2),2);
		this.setBlockDesign(getDesign(plugin, 3),3);
	}

	static GenericCubeBlockDesign getDesign(Plugin plugin, int tNr) {
		Texture texture = getTexture(plugin);
		int[] idMap = new int[6];
		switch(tNr){
		case 0:
			idMap[0]=0;
			idMap[1]=0;
			idMap[2]=2;
			idMap[3]=0;
			idMap[4]=0;
			idMap[5]=0;
			break;
		case 1:
			idMap[0]=0;
			idMap[1]=2;
			idMap[2]=0;
			idMap[3]=0;
			idMap[4]=0;
			idMap[5]=0;
			break;
		case 2:
			idMap[0]=0;
			idMap[1]=0;
			idMap[2]=0;
			idMap[3]=0;
			idMap[4]=2;
			idMap[5]=0;
			break;
		case 3:
			idMap[0]=0;
			idMap[1]=0;
			idMap[2]=0;
			idMap[3]=2;
			idMap[4]=0;
			idMap[5]=0;
			break;
		}
		GenericCubeBlockDesign bd = new GenericCubeBlockDesign(plugin, texture,
				idMap);
		return bd;
	}
	static Texture getTexture(Plugin plugin){
		return new Texture(plugin,
				"http://chestpvp.tk/spout/line.png", 16 * 8, 16, 16);
	}
}
