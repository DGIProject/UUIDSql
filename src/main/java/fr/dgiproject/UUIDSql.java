package fr.dgiproject;
     
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
        				if (args.length == 2)
        				{
        					sender.sendMessage("[UUIDSql]The name of "+args[1].toString()+" is Ringotter");
        					
        					Connection dbCon = null;
        				    Statement stmt = null;
        				    ResultSet rs = null;
        					
        					try { 
        						
        						String query = "SELECT COUNT(*) AS exist, username FROM userUUID WHERE uuid = '"+args[1].toString()+"' ";
        						dbCon = DriverManager.getConnection(dbInformation[0], dbInformation[1], dbInformation[2]);
        						        								
        						stmt = dbCon.prepareStatement(query);
        						rs = stmt.executeQuery(query);
        						while(rs.next()){
        							int count = rs.getInt(1);
        							if (count != 0)
        							{
        	        					sender.sendMessage("[UUIDSql]The username of uuid "+args[1].toString()+" is "+rs.getString(2)+"");
        							}
        							else
        							{
        								sender.sendMessage("This uuid was not found in the database");
        							}
        						}        				
        						dbCon.close();
        					} catch (SQLException e) {
        						// TODO Auto-generated catch block
        						this.getLogger().warning("an error occured while connecting to the db, please change the config file."+e.getMessage());
        					}
        					
        				}
        				else
        				{
        					sender.sendMessage("[UUIDSql]You must specify a uuid");
        				}
        			}
        			else if (args[0].equalsIgnoreCase("uuid")) 
        			{        				
        				if (args.length == 2)
        				{        				 	
        					Connection dbCon = null;
        				    Statement stmt = null;
        				    ResultSet rs = null;
        					
        					try { 
        						
        						String query = "SELECT COUNT(*) AS exist, uuid FROM userUUID WHERE username = '"+args[1].toString()+"' ";
        						dbCon = DriverManager.getConnection(dbInformation[0], dbInformation[1], dbInformation[2]);
        						        								
        						stmt = dbCon.prepareStatement(query);
        						rs = stmt.executeQuery(query);
        						while(rs.next()){
        							int count = rs.getInt(1);
        							if (count != 0)
        							{
        	        					sender.sendMessage("[UUIDSql]The UUID of "+args[1].toString()+" is "+rs.getString(2)+"");
        							}
        							else
        							{
        								sender.sendMessage("This player was not found in the database");
        							}
        						}        				
        						dbCon.close();
        					} catch (SQLException e) {
        						// TODO Auto-generated catch block
        						this.getLogger().warning("an error occured while connecting to the db, please change the config file."+e.getMessage());
        					}
        				}
        				else
        				{
        					sender.sendMessage("[UUIDSql]You must specify a name");
        				}
					}
        			else if (args[0].equalsIgnoreCase("reload"))
        			{
        				sender.sendMessage("[UUIDSql]Reloading UUIDSql");
        				
        				String dbURL = this.getConfig().getString("host")+this.getConfig().getString("dbName");
                        String username = this.getConfig().getString("Username");
                        String password = this.getConfig().getString("Password");
                        
                        dbInformation[0] = dbURL;
                        dbInformation[1] = username;
                        dbInformation[2] = password;
                        
                        sender.sendMessage("[UUIDSql]Reload Ended !");
        				
        				
        			}
        			else
        			{
        				sender.sendMessage("[UUIDSql]There is a problem, this command doen't exist !");
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