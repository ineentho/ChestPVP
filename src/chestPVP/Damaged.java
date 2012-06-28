package chestPVP;

import java.util.Date;

import org.bukkit.entity.Player;

public class Damaged {
	public Date time;
	public Player player;
	public Damaged(Player player){
		this.time = new Date();
		this.player = player;
	}
}
