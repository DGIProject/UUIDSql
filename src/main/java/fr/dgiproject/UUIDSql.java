package fr.dgiproject;
     
import java.io.File;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
     
    public final class UUIDSql extends JavaPlugin {
    	
    	String[] dbInformation = new String[3];
        private final PlayerListener playerListener = new PlayerListener(this,dbInformation);
        @Override
        public void onEnable() {

        	
        	PluginManager pm = getServer().getPluginManager();

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
            else
            {
            	String dbURL = this.getConfig().getString("host")+this.getConfig().getString("dbName");
                String username = this.getConfig().getString("Username");
                String password = this.getConfig().getString("Password");
                
                dbInformation[0] = dbURL;
                dbInformation[1] = username;
                dbInformation[2] = password;

            }
            
            pm.registerEvents(playerListener, this);
        	getLogger().info("Loaded !");   		
    		
        }
        
        
        @Override
        public void onDisable() {
        	getLogger().info("Unloaded");
        }
        
    }