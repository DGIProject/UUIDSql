package fr.dgiproject;
     
import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
        
        public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
        {
        	if (commandLabel.equalsIgnoreCase("uuidSql"))
        	{
        		if (args.length != 0)
        		{
        			if (args[0].equalsIgnoreCase("name"))
        			{
        				if (args[1].toString() != null)
        				{
        					sender.sendMessage("the name of "+args[1].toString()+" is Ringotter");
        				}
        				else
        				{
        					sender.sendMessage("You must specify a uuid");
        				}
        			}
        			else if (args[0].equalsIgnoreCase("uuid")) 
        			{        				
        				if (args[1].toString() != null)
        				{
        					sender.sendMessage("the UUID of "+args[1].toString()+" is 589-s6d3");
        				}
        				else
        				{
        					sender.sendMessage("You must specify a name");
        				}
					}
        			else if (args[0].equalsIgnoreCase("reload"))
        			{
        				sender.sendMessage("reloading UUIDSql");
        			}
        			else
        			{
        				sender.sendMessage("There is a problem, this command doen't exist !");
        			}
        		}
        		else
        		{
        			sender.sendMessage("Availbale commands");
            		sender.sendMessage("1. uuidsql reload");
            		sender.sendMessage("2. uuidsql name <uuid>");
            		sender.sendMessage("3. uuidsql uuid <name>");	
        		}
        		
        	}
			return false;
        	
        }
        
    }