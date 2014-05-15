package fr.dgiproject;
     
import java.io.File;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
     
    public final class UUIDSql extends JavaPlugin {
    	
        private final PlayerListener playerListener = new PlayerListener(this);
        @Override
        public void onEnable() {

        	PluginManager pm = getServer().getPluginManager();
            pm.registerEvents(playerListener, this);
                        
            
            File config = new File(getDataFolder() + File.separator + "config.yml");
            
            if (!config.exists())
            {
            	getLogger().info("Creating configs file !");   
            	this.getConfig().addDefault("dbName", "userUUID");
            	this.getConfig().addDefault("Username", "root");
            	this.getConfig().addDefault("Password", "root");
            	this.getConfig().addDefault("host", "jdbc:mysql://localhost:3306/");
            	this.getConfig().options().copyDefaults(true);
            	this.saveConfig();

            }           
            
        	getLogger().info("Loaded !");   		
    		
        }
        
        
        @Override
        public void onDisable() {
        	getLogger().info("Unloaded");
        }
        
    }