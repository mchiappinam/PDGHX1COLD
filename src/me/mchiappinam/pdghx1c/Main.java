package me.mchiappinam.pdghx1c;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.milkbowl.vault.economy.Economy;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

import me.mchiappinam.pdghx1c.Comando;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.p000ison.dev.simpleclans2.api.SCCore;
import com.p000ison.dev.simpleclans2.api.clanplayer.ClanPlayerManager;

public class Main extends JavaPlugin {
	protected static Economy econ=null;
	protected static List<String> lista=new ArrayList<String>();
	protected SCCore core;
	protected SimpleClans core2;
	protected int version = 0;
	int tteleportarForaArena;
	int ttempoFim;
	int ttempoParaLiberarNaArena;
	int ttempoResposta;
	public boolean andamento=false;
	public boolean contagemFim=false;
	public Player desafiador=null;
	public Player desafiado=null;
	public Player vencedor=null;
	public Player perdedor=null;
	public int posTele=0;
	
	public void onEnable() {
		File file=new File(getDataFolder(),"config.yml");
		if(!file.exists()) {
			try {
				saveResource("config_template.yml",false);
				File file2=new File(getDataFolder(),"config_template.yml");
				file2.renameTo(new File(getDataFolder(),"config.yml"));
			}catch(Exception e) {}
		}
		getServer().getPluginCommand("x1").setExecutor(new Comando(this));
		getServer().getPluginManager().registerEvents(new Listeners(this), this);
		if(!setupEconomy()) {
			getLogger().warning("ERRO: Vault (Economia) nao encontrado!");
			getLogger().warning("Desativando o plugin...");
			getServer().getPluginManager().disablePlugin(this);
        }

		if (hookSimpleClans()) {
			getLogger().info("Hooked to SimpleClans2!");
			version = 2;
		}else if (getServer().getPluginManager().getPlugin("SimpleClans") != null) {
			getLogger().info("Hooked to SimpleClans!");
			core2 = ((SimpleClans)getServer().getPluginManager().getPlugin("SimpleClans"));
			version = 1;
		}else{
			getServer().getPluginManager().disablePlugin(this);
		}
		
		getServer().getConsoleSender().sendMessage("§3[PDGHX1C] §2ativado - Plugin by: mchiappinam");
		getServer().getConsoleSender().sendMessage("§3[PDGHX1C] §2Acesse: http://pdgh.com.br/");
	}

	public void onDisable() {
		getServer().getConsoleSender().sendMessage("§3[PDGHX1C] §2desativando...");
		cAll();
		getServer().getConsoleSender().sendMessage("§3[PDGHX1C] §2desativado - Plugin by: mchiappinam");
		getServer().getConsoleSender().sendMessage("§3[PDGHX1C] §2Acesse: http://pdgh.com.br/");
	}
	
	private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp=getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ=rsp.getProvider();
        return econ != null;
	}

	private boolean hookSimpleClans() {
		try {
			for(Plugin plugin : getServer().getPluginManager().getPlugins()) {
				if ((plugin instanceof SCCore)) {
					core = ((SCCore)plugin);
					return true;
				}
			}
		}catch (NoClassDefFoundError e) {
			return false;
		}
		return false;
	}

	public ClanPlayerManager getClanPlayerManager() {
		return core.getClanPlayerManager();
	}
	
	public void help(Player p) {
		double taxa = getConfig().getDouble("TaxaMoney")+getConfig().getDouble("TaxaConsole");
		p.sendMessage("§3§lPDGH X1 - Comandos:");
		p.sendMessage("§2/x1 desafiar <nick> -§a- Desafia alguém para o x1.");
		p.sendMessage("§2/x1 aceitar -§a- Aceita o x1 desafiado.");
		p.sendMessage("§2/x1 negar -§a- Nega o x1 desafiado.");
		p.sendMessage("§2/x1 info -§a- Status do x1.");
		p.sendMessage("§cTaxa de §a§l$"+taxa+"§c.");
		p.sendMessage("§cPrêmio de §a§l$"+getConfig().getDouble("TaxaMoney")*2+" §cpara o vencedor.");
		p.sendMessage("§cLimite de 1 x1 por vez.");
	}
	
	public void cAll() {
		if(andamento) {
			cAllTasks();
			if(lista.size()==2) {
		        for(String p : Main.lista) {
		        	Main.econ.depositPlayer(p, getConfig().getDouble("TaxaMoney")+getConfig().getDouble("TaxaConsole"));
		        }
				cAllTasks();
	        	andamento=false;
	        	vencedor=null;
	        	perdedor=null;
	        	lista.clear();
				getServer().broadcastMessage("§3[Ⓧ①] §d§lX1 cancelado e taxa devolvida!");
			}else{
				cAllTasks();
	        	andamento=false;
	        	vencedor=null;
	        	perdedor=null;
	        	lista.clear();
				getServer().broadcastMessage("§3[Ⓧ①] §d§lX1 cancelado!");
			}
		}
	}
	
	public void tempoResposta() {
    	ttempoResposta = getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
    		public void run() {
                getServer().broadcastMessage("§3[Ⓧ①] §a"+desafiado.getName()+" §fnegou o x1 de §a"+desafiador.getName()+" §fpois passou o tempo limite de resposta.");
                andamento=false;
                desafiador=null;
                desafiado=null;
        		Main.lista.clear();
    		}
    	}, 60*20L);
	}
	
	public void tempoParaLiberarNaArena() {
        final Location loc1 = new Location(getServer().getWorld("world_x1"), 4.5, 53, 7.5);
        loc1.setPitch(0);
        loc1.setYaw(180);
        final Location loc2 = new Location(getServer().getWorld("world_x1"), -3.5, 53, -6.5);
        loc2.setPitch(0);
        loc2.setYaw(0);
    	ttempoParaLiberarNaArena = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
    		int timer = 40;
    		public void run() {
    			if(timer <0) {
					ctempoParaLiberarNaArena();
				}
                if(posTele==1) {
                	desafiador.teleport(loc1);
                	desafiado.teleport(loc2);
                }else{
                	desafiador.teleport(loc2);
                	desafiado.teleport(loc1);
                }
                desafiador.closeInventory();
                desafiado.closeInventory();
                if((timer == 40) || (timer == 36) || (timer == 32) || (timer == 28) || (timer == 24) || (timer == 20) || (timer == 16) || (timer == 12) || (timer == 8) || (timer == 4)) {
		        	desafiador.playSound(desafiador.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
		        	desafiado.playSound(desafiado.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
                }
		        if(timer == 40) {
		        	desafiador.sendMessage("§3[Ⓧ①]"+ChatColor.WHITE+"⋙O x1 começa em §a10 §fsegundos.");
		        	desafiado.sendMessage("§3[Ⓧ①]"+ChatColor.WHITE+"⋙O x1 começa em §a10 §fsegundos.");
	                limpar(desafiador);
	                limpar(desafiado);
		        }else if((timer == 20) || (timer == 16) || (timer == 12) || (timer == 8)) {
		        	if(timer==16) {
		                limpar(desafiador);
		                limpar(desafiado);
		        	}else if(timer==16) {
			        	kit(desafiador);
			        	kit(desafiado);
		        	}
		        	desafiador.sendMessage("§3[Ⓧ①]"+ChatColor.WHITE+"⋙O x1 começa em §a"+timer/4+" §fsegundos.");
		        	desafiado.sendMessage("§3[Ⓧ①]"+ChatColor.WHITE+"⋙O x1 começa em §a"+timer/4+" §fsegundos.");
		        }else if(timer == 4) {
		        	desafiador.sendMessage("§3[Ⓧ①]"+ChatColor.WHITE+"⋙O x1 começa em §a1 §fsegundo.");
		        	desafiado.sendMessage("§3[Ⓧ①]"+ChatColor.WHITE+"⋙O x1 começa em §a1 §fsegundo.");
		        }else if(timer==0) {
		        	desafiador.playSound(desafiador.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
		        	desafiado.playSound(desafiado.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
		        	desafiador.sendMessage("§3[Ⓧ①]"+ChatColor.WHITE+"⋙VALENDO!");
		        	desafiado.sendMessage("§3[Ⓧ①]"+ChatColor.WHITE+"⋙VALENDO!");
		        	tempoFim();
		        }
    		timer--;
	      }
		}, 0, 5);
	}
	
	public void teleportarForaArena(final Player p) {
        cAllTasks();
    	andamento=false;
        contagemFim=true;
    	tteleportarForaArena = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
    		int timer = 20;
    		public void run() {
    			if(timer <0) {
					cteleportarForaArena();
				}
		        if((timer>=1) && (timer<=20)) {
		        	p.playSound(p.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
		        }
		        if(timer == 20) {
		        	p.sendMessage("§3[Ⓧ①]"+ChatColor.WHITE+"⋙Você será teleportado para o spawn em §a"+timer+" §fsegundos.");
		        }else if(timer == 15) {
		        	p.sendMessage("§3[Ⓧ①]"+ChatColor.WHITE+"⋙Você será teleportado para o spawn em §a"+timer+" §fsegundos.");
		        }else if(timer == 10) {
		        	p.sendMessage("§3[Ⓧ①]"+ChatColor.WHITE+"⋙Você será teleportado para o spawn em §a"+timer+" §fsegundos.");
		        }else if((timer>=1) && (timer<=5)) {
		        	if(timer==1) {
			        	p.sendMessage("§3[Ⓧ①]"+ChatColor.WHITE+"⋙Você será teleportado para o spawn em §a"+timer+" §fsegundo.");
		        	}else{
			        	p.sendMessage("§3[Ⓧ①]"+ChatColor.WHITE+"⋙Você será teleportado para o spawn em §a"+timer+" §fsegundos.");
		        	}
		        }else if(timer==0) {
		        	p.playSound(p.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
		        	
			        World w = getServer().getWorld(getConfig().getString("MundoPrincipal"));
			        if (w != null)
			        	p.teleport(w.getSpawnLocation());
			        else
			        	p.sendMessage("§cOcorreu um erro. Notifique alguém da STAFF.");

	                p.closeInventory();
	                limpar(p);
		        	p.sendMessage("§3[Ⓧ①]"+ChatColor.WHITE+"⋙Teleportado. Parabéns por vencer o x1!");

	                contagemFim=false;
		        	vencedor=null;
		        	perdedor=null;
		        	lista.clear();
		        }
    		timer--;
	      }
		}, 0, 20);
	}
	
	public void tempoFim() {
	  	ttempoFim = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
	  		int timer = 600;
	  		public void run() {
	  			if(timer <0) {
	      			ctempoFim();
	  			}
    		    if ((timer == 5) || (timer == 10) || (timer == 30)) {
    		        getServer().broadcastMessage("§3[Ⓧ①] §a"+timer+" §fsegundos para o x1 finalizar automaticamente.");
    		        getServer().broadcastMessage("§3[Ⓧ①] §fCaso empatar apenas metade da taxa será devolvida.");
    		    }
    		    if ((timer == 60) || (timer == 120) || (timer == 180) || (timer == 240) || (timer == 300) || (timer == 360) || (timer == 420) || (timer == 480) || (timer == 540) || (timer == 600)) {
    		        if(timer/60 == 1) {
    		        	desafiador.sendMessage("§3[Ⓧ①] §a"+timer/60+" §fminuto para o x1 finalizar automaticamente.");
    		        	desafiado.sendMessage("§3[Ⓧ①] §a"+timer/60+" §fminuto para o x1 finalizar automaticamente.");
    		        }else{
    		        	desafiador.sendMessage("§3[Ⓧ①] §a"+timer/60+" §fminutos para o x1 finalizar automaticamente.");
    		        	desafiado.sendMessage("§3[Ⓧ①] §a"+timer/60+" §fminutos para o x1 finalizar automaticamente.");
    		        }
    		        desafiador.sendMessage("§3[Ⓧ①] §fCaso empatar apenas metade da taxa será devolvida.");
    		        desafiado.sendMessage("§3[Ⓧ①] §fCaso empatar apenas metade da taxa será devolvida.");
    		    }
    		    if (timer == 0) {
			        for(String p : Main.lista) {
	    				Main.econ.depositPlayer(p, getConfig().getDouble("TaxaMoney")/2);
				        World w = getServer().getWorld(getConfig().getString("MundoPrincipal"));
			        	
				        if (w != null)
				        	getServer().getPlayer(p).teleport(w.getSpawnLocation());
				        else
				        	getServer().getPlayer(p).sendMessage("§cOcorreu um erro. Notifique alguém da STAFF.");
				        
			        }
    				getServer().broadcastMessage("§3[Ⓧ①] §6Empate entre §a"+desafiador.getName()+" §6vs §a"+desafiado.getName());
    				getServer().broadcastMessage("§3[Ⓧ①] §6Apenas metade da taxa foi devolvida.");
			        andamento=false;
			        vencedor=null;
			        perdedor=null;
	                desafiador=null;
	                desafiado=null;
			        Main.lista.clear();
    		    }
	    			timer--;
	    		
	  		}
	  	  }, 0, 20*1);
	}

	public void cAllTasks() {
		getServer().getScheduler().cancelTask(ttempoResposta);
		getServer().getScheduler().cancelTask(ttempoParaLiberarNaArena);
		getServer().getScheduler().cancelTask(tteleportarForaArena);
		getServer().getScheduler().cancelTask(ttempoFim);
	}

	public void ctempoResposta() {
		getServer().getScheduler().cancelTask(ttempoResposta);
	}

	public void ctempoParaLiberarNaArena() {
		getServer().getScheduler().cancelTask(ttempoParaLiberarNaArena);
	}

	public void cteleportarForaArena() {
		getServer().getScheduler().cancelTask(tteleportarForaArena);
	}

	public void ctempoFim() {
		getServer().getScheduler().cancelTask(ttempoFim);
	}

	public void limpar(Player p) {
		p.getInventory().setHelmet(null);
		p.getInventory().setChestplate(null);
		p.getInventory().setLeggings(null);
		p.getInventory().setBoots(null);
		p.getInventory().clear();
	}

	public void kit(Player p) {
	    ItemStack espada = new ItemStack(Material.DIAMOND_SWORD, 1);
	    espada.addEnchantment(Enchantment.DAMAGE_ALL, 5);
	    ItemStack arco = new ItemStack(Material.BOW, 1);
	    arco.addEnchantment(Enchantment.ARROW_DAMAGE , 5);
	    arco.addEnchantment(Enchantment.ARROW_FIRE, 1);
	    arco.addEnchantment(Enchantment.ARROW_INFINITE, 1);
	    ItemStack elmo = new ItemStack(Material.DIAMOND_HELMET, 1);
	    elmo.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL , 4);
	    elmo.addEnchantment(Enchantment.DURABILITY, 3);
	    ItemStack peito = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);
	    peito.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL , 4);
	    peito.addEnchantment(Enchantment.DURABILITY, 3);
	    ItemStack calca = new ItemStack(Material.DIAMOND_LEGGINGS, 1);
	    calca.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL , 4);
	    calca.addEnchantment(Enchantment.DURABILITY, 3);
	    ItemStack bota = new ItemStack(Material.DIAMOND_BOOTS, 1);
	    bota.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL , 4);
	    bota.addEnchantment(Enchantment.DURABILITY, 3);
		p.getInventory().setHelmet(elmo);
		p.getInventory().setChestplate(peito);
		p.getInventory().setLeggings(calca);
		p.getInventory().setBoots(bota);
	    p.getInventory().addItem(espada);
	    p.getInventory().addItem(arco);
	    p.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 10, (short) 1));
	    p.getInventory().addItem(new ItemStack(Material.POTION, 1, (short) 8226));
	    p.getInventory().addItem(new ItemStack(Material.POTION, 1, (short) 8225));
	    p.getInventory().addItem(new ItemStack(Material.POTION, 1, (short) 8226));
	    p.getInventory().addItem(new ItemStack(Material.POTION, 1, (short) 8225));
	    p.getInventory().addItem(new ItemStack(Material.POTION, 1, (short) 8258));
	    p.getInventory().addItem(new ItemStack(Material.POTION, 1, (short) 8257));
	    p.getInventory().addItem(new ItemStack(Material.ARROW, 1));
}
	
	
	
	
	
	
}