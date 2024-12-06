package me.mchiappinam.pdghx1c;

import java.util.Random;

import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class Listeners implements Listener {
	private Main plugin;
	
	public Listeners(Main main) {
		plugin=main;
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	private void onDeath(PlayerDeathEvent e) {
		if ((e.getEntity().getKiller() instanceof Player)) {
	    	Player p=e.getEntity();
	    	Player killer=e.getEntity().getKiller();
	    	if((Main.lista.contains(p.getName())) && (Main.lista.contains(killer.getName()))) {
				if(plugin.vencedor!=null) {
					return;
				}
	    		for (Player all : plugin.getServer().getOnlinePlayers()) {
	            	all.playSound(all.getLocation(), Sound.WOLF_HOWL, 1.0F, 1.0F);
	    		}
	        	Random randomgen=new Random();
	        	int i=randomgen.nextInt(10) + 1;
	        	if(i == 1) {
					plugin.getServer().broadcastMessage("§3[Ⓧ①] §a§l"+killer.getName()+" §fexterminou §a§l§m"+p.getName()+"§f e venceu o x1.");
	        	}else if(i == 2) {
					plugin.getServer().broadcastMessage("§3[Ⓧ①] §a§l"+killer.getName()+" §fmassacrou §a§l§m"+p.getName()+"§f e venceu o x1.");
	        	}else if(i == 3) {
					plugin.getServer().broadcastMessage("§3[Ⓧ①] §a§l"+killer.getName()+" §fassediou §a§l§m"+p.getName()+"§f e venceu o x1.");
	        	}else if(i == 4) {
					plugin.getServer().broadcastMessage("§3[Ⓧ①] §a§l"+killer.getName()+" §fencheu de porrada §a§l§m"+p.getName()+"§f e venceu o x1.");
	        	}else if(i == 5) {
					plugin.getServer().broadcastMessage("§3[Ⓧ①] §a§l"+killer.getName()+" §fchutou §a§l§m"+p.getName()+"§f e venceu o x1.");
	        	}else if(i == 6) {
					plugin.getServer().broadcastMessage("§3[Ⓧ①] §a§l"+killer.getName()+" §fenforcou §a§l§m"+p.getName()+"§f e venceu o x1.");
	        	}else if(i == 7) {
					plugin.getServer().broadcastMessage("§3[Ⓧ①] §a§l"+killer.getName()+" §fjogou de um penhasco §a§l§m"+p.getName()+"§f e venceu o x1.");
	        	}else if(i == 8) {
					plugin.getServer().broadcastMessage("§3[Ⓧ①] §a§l"+killer.getName()+" §ffez §a§l§m"+p.getName()+"§f visitar Jesus mais cedo e venceu o x1.");
	        	}else if(i == 9) {
					plugin.getServer().broadcastMessage("§3[Ⓧ①] §a§l"+killer.getName()+" §fassassinou §a§l§m"+p.getName()+"§f e venceu o x1.");
	        	}else if(i == 10) {
					plugin.getServer().broadcastMessage("§3[Ⓧ①] §a§l"+killer.getName()+" §fcortou §a§l§m"+p.getName()+"§f para colocar no pão e venceu o x1.");
	        	}
	        	Main.econ.depositPlayer(killer.getName(), plugin.getConfig().getDouble("TaxaMoney")*2);
				plugin.cAllTasks();
				plugin.teleportarForaArena(killer);
	        	plugin.andamento=false;
	        	plugin.vencedor=killer;
	        	plugin.perdedor=p;
	    	}
		}else{
	    	Player p=e.getEntity();
			if(Main.lista.size()==2) {
				if(plugin.vencedor!=null) {
					return;
				}
				if(Main.lista.contains(p.getName())) {
					if(plugin.desafiador==p) {
						plugin.vencedor=plugin.desafiado;
						plugin.perdedor=plugin.desafiador;
					}else{
						plugin.vencedor=plugin.desafiador;
						plugin.perdedor=plugin.desafiado;
					}
		    		for (Player all : plugin.getServer().getOnlinePlayers()) {
		            	all.playSound(all.getLocation(), Sound.WOLF_HOWL, 1.0F, 1.0F);
		    		}
					plugin.vencedor=plugin.vencedor;
					plugin.perdedor=p;
					plugin.getServer().broadcastMessage("§3[Ⓧ①] §a§l"+p.getName()+" §fmorreu e §a§l"+plugin.vencedor.getName()+"§f venceu o x1.");
		        	Main.econ.depositPlayer(plugin.vencedor.getName(), plugin.getConfig().getDouble("TaxaMoney")*2);
					plugin.cAllTasks();
					plugin.teleportarForaArena(plugin.vencedor);
		        	plugin.andamento=false;
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	private void onQuit(PlayerQuitEvent e) {
		if(plugin.andamento) {
			if(Main.lista.size()==2) {
				if(Main.lista.contains(e.getPlayer().getName())) {
					if(plugin.desafiador==e.getPlayer()) {
						plugin.vencedor=plugin.desafiado;
						plugin.perdedor=plugin.desafiador;
					}else{
						plugin.vencedor=plugin.desafiador;
						plugin.perdedor=plugin.desafiado;
					}
					for (Player all : plugin.getServer().getOnlinePlayers()) {
						all.playSound(all.getLocation(), Sound.WOLF_HOWL, 1.0F, 1.0F);
	    			}
	    			e.getPlayer().setHealth(0);
	    			plugin.teleportarForaArena(plugin.vencedor);
	    			Main.econ.depositPlayer(plugin.vencedor.getName(), plugin.getConfig().getDouble("TaxaMoney")*2);
	    			plugin.getServer().broadcastMessage("§3[Ⓧ①] §a§l§m"+plugin.perdedor.getName()+"§c desconectou-se no meio do x1.");
	    			plugin.getServer().broadcastMessage("§3[Ⓧ①] §a§l"+plugin.vencedor.getName()+" §fvenceu o x1.");
				}
			}else if(Main.lista.size()==1) {
				if((Main.lista.contains(e.getPlayer().getName())) || (plugin.desafiado==e.getPlayer())) {
					plugin.cAllTasks();
			        plugin.andamento=false;
			        plugin.vencedor=null;
			        plugin.perdedor=null;
	                plugin.desafiador=null;
	                plugin.desafiado=null;
			        Main.lista.clear();
					plugin.getServer().broadcastMessage("§3[Ⓧ①] §a"+e.getPlayer().getName()+"§c desconectou-se antes do x1 começar.");
					plugin.getServer().broadcastMessage("§3[Ⓧ①] §fX1 cancelado.");
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	private void onKick(PlayerKickEvent e) {
		if(plugin.andamento) {
			if(Main.lista.size()==2) {
				if(Main.lista.contains(e.getPlayer().getName())) {
					if(plugin.desafiador==e.getPlayer()) {
						plugin.vencedor=plugin.desafiado;
						plugin.perdedor=plugin.desafiador;
					}else{
						plugin.vencedor=plugin.desafiador;
						plugin.perdedor=plugin.desafiado;
					}
					for (Player all : plugin.getServer().getOnlinePlayers()) {
						all.playSound(all.getLocation(), Sound.WOLF_HOWL, 1.0F, 1.0F);
	    			}
	    			e.getPlayer().setHealth(0);
	    			plugin.teleportarForaArena(plugin.vencedor);
	    			Main.econ.depositPlayer(plugin.vencedor.getName(), plugin.getConfig().getDouble("TaxaMoney")*2);
	    			plugin.getServer().broadcastMessage("§3[Ⓧ①] §a§l§m"+plugin.perdedor.getName()+"§c desconectou-se no meio do x1.");
	    			plugin.getServer().broadcastMessage("§3[Ⓧ①] §a§l"+plugin.vencedor.getName()+" §fvenceu o x1.");
				}
			}else if(Main.lista.size()==1) {
				if((Main.lista.contains(e.getPlayer().getName())) || (plugin.desafiado==e.getPlayer())) {
					plugin.cAllTasks();
			        plugin.andamento=false;
			        plugin.vencedor=null;
			        plugin.perdedor=null;
	                plugin.desafiador=null;
	                plugin.desafiado=null;
			        Main.lista.clear();
					plugin.getServer().broadcastMessage("§3[Ⓧ①] §a"+e.getPlayer().getName()+"§c desconectou-se antes do x1 começar.");
					plugin.getServer().broadcastMessage("§3[Ⓧ①] §fX1 cancelado.");
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerRespawn(PlayerRespawnEvent e) {
	    if(e.getPlayer().getWorld()==plugin.getServer().getWorld("world_x1")) {
        	
	        World w = plugin.getServer().getWorld(plugin.getConfig().getString("MundoPrincipal"));
	        if (w != null) {
	        	e.getPlayer().sendMessage("§3[Ⓧ①] §cVocê morreu :(");
	        	e.setRespawnLocation(w.getSpawnLocation());
	        }else{
	        	e.getPlayer().sendMessage("§cOcorreu um erro. Notifique alguém da STAFF.");
	        }
	        
	    }
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e) {
	    if(e.getPlayer().getWorld()==plugin.getServer().getWorld("world_x1")) {
        	
	        World w = plugin.getServer().getWorld(plugin.getConfig().getString("MundoPrincipal"));
	        if (w != null) {
	        	e.getPlayer().teleport(w.getSpawnLocation());
	        	e.getPlayer().sendMessage("§3[Ⓧ①] §cVocê desconectou no x1 e foi teleportado para o spawn.");
	        }else{
	        	e.getPlayer().sendMessage("§cOcorreu um erro. Notifique alguém da STAFF.");
	        }
	        
	    }
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent e) {
	    if(e.getBlock().getWorld()==plugin.getServer().getWorld("world_x1")) {
        	e.setCancelled(true);
        	e.getPlayer().sendMessage("§3[Ⓧ①] §cVocê não pode quebrar blocos do x1.");
	    }
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockPlacek(BlockPlaceEvent e) {
	    if(e.getBlock().getWorld()==plugin.getServer().getWorld("world_x1")) {
        	e.setCancelled(true);
        	e.getPlayer().sendMessage("§3[Ⓧ①] §cVocê não pode colocar blocos no x1.");
	    }
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	private void onPCmd(PlayerCommandPreprocessEvent e) {
	    if(e.getPlayer().getWorld()==plugin.getServer().getWorld("world_x1")) {
	    	if(!e.getMessage().toLowerCase().startsWith("/g")) {
	    		e.setCancelled(true);
	    		e.getPlayer().sendMessage("§3[Ⓧ①] §cApenas o comando do chat global é liberado.");
	    	}
    	}
	}

	@EventHandler
	public void onHungerChange(FoodLevelChangeEvent e) {
	    if(e.getEntity().getWorld()==plugin.getServer().getWorld("world_x1"))
			e.setCancelled(true);
	}
	
	
	
	
	
	
	
	
}