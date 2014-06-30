package fr.dgiproject;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class UUIDSql extends JavaPlugin {

	String[] dbInformation = new String[3];
	private final PlayerListener playerListener = new PlayerListener(this,
			dbInformation);

	@Override
	public void onEnable() {

		PluginManager pm = getServer().getPluginManager();

		File config = new File(getDataFolder() + File.separator + "config.yml");
                
                
                

		if (!config.exists()) {
			getLogger().info("Creating configs file !");
			this.getConfig().addDefault("dbName", "userUUID");
			this.getConfig().addDefault("Username", "root");
			this.getConfig().addDefault("Password", "root");
			this.getConfig().addDefault("host", "jdbc:mysql://localhost:3306/");
			this.getConfig().options().copyDefaults(true);
			this.saveConfig();

		} else {
			String dbURL = this.getConfig().getString("host")
					+ this.getConfig().getString("dbName");
			String username = this.getConfig().getString("Username");
			String password = this.getConfig().getString("Password");

			dbInformation[0] = dbURL;
			dbInformation[1] = username;
			dbInformation[2] = password;
                        Connection dbCon = null;
                        try {
                            dbCon = DriverManager.getConnection(dbInformation[0], dbInformation[1], dbInformation[2]);
                            createTables(dbCon, this);
                        } catch (SQLException e){
                            this.getLogger().severe("An error occured while connecting to the db, please change the config file."+e.getMessage());
                        }

		}

		pm.registerEvents(playerListener, this);
		pm.addPermission(new Permission("uuidsql.remove"));
		pm.addPermission(new Permission("uuidsql.reload"));
		getLogger().info("[UUIDSql] Loaded !");

	}

	@Override
	public void onDisable() {
		getLogger().info("[UUIDSql] Unloaded");
	}

	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		if (commandLabel.equalsIgnoreCase("uuidSql")) {
			if (args.length != 0) {
				if (args[0].equalsIgnoreCase("getname")) {
					if (args.length == 2) {
						Connection dbCon = null;
						Statement stmt = null;
						ResultSet rs = null;

						try {

							String query = "SELECT COUNT(*) AS exist, username FROM userUUID WHERE uuid = '"
									+ args[1].toString() + "' ";
							dbCon = DriverManager.getConnection(
									dbInformation[0], dbInformation[1],
									dbInformation[2]);

							stmt = dbCon.prepareStatement(query);
							rs = stmt.executeQuery(query);
							while (rs.next()) {
								int count = rs.getInt(1);
								if (count != 0) {
									sender.sendMessage("[UUIDSql]The username of uuid "
											+ args[1].toString()
											+ " is "
											+ rs.getString(2) + "");
								} else {
									sender.sendMessage("[UUIDSql]This uuid was not found in the database");
								}
							}
							dbCon.close();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							this.getLogger().warning("[UUIDSql]An error occured while connecting to the db, please change the config file.");
							this.getLogger().severe("Cause: " + e.getMessage());
						}

					} else {
						sender.sendMessage("[UUIDSql]You must specify a uuid");
					}
				} else if (args[0].equalsIgnoreCase("getuuid")) {
					if (args.length == 2) {
						Connection dbCon = null;
						Statement stmt = null;
						ResultSet rs = null;

						try {

							String query = "SELECT COUNT(*) AS exist, uuid FROM userUUID WHERE username = '"
									+ args[1].toString() + "' ";
							dbCon = DriverManager.getConnection(
									dbInformation[0], dbInformation[1],
									dbInformation[2]);

							stmt = dbCon.prepareStatement(query);
							rs = stmt.executeQuery(query);
							while (rs.next()) {
								int count = rs.getInt(1);
								if (count != 0) {
									sender.sendMessage("[UUIDSql]The UUID of "
											+ args[1].toString() + " is "
											+ rs.getString(2) + "");
								} else {
									sender.sendMessage("[UUIDSql]This player was not found in the database");
								}
							}
							dbCon.close();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							this.getLogger().warning("[UUIDSql]An error occured while connecting to the db, please change the config file.");
							this.getLogger().severe("Cause: " + e.getMessage());

						}
					} else {
						sender.sendMessage("[UUIDSql]You must specify a name");
					}
				} else if (args[0].equalsIgnoreCase("reload") && sender.hasPermission(new Permission("uuidsql.reload"))) {

					String dbURL = this.getConfig().getString("host")
							+ this.getConfig().getString("dbName");
					String username = this.getConfig().getString("Username");
					String password = this.getConfig().getString("Password");

					dbInformation[0] = dbURL;
					dbInformation[1] = username;
					dbInformation[2] = password;

					sender.sendMessage("[UUIDSql]Reloaded");

				}
				else if (args[0].equalsIgnoreCase("purge") && sender.hasPermission(new Permission("uuidsql.remove")))
				{
					if (args.length == 2)
					{
						
						if (args[1].equalsIgnoreCase("1"))
						{
							Connection dbCon = null;
							Statement stmt = null;
		
							try {
								String query = "TRUNCATE userUUID";
								dbCon = DriverManager.getConnection(
										dbInformation[0], dbInformation[1],
										dbInformation[2]);
		
								stmt = dbCon.prepareStatement(query);
								stmt.executeUpdate(query);
								
								sender.sendMessage("[UUIDSql]dataBase is now empty !");
								
								dbCon.close();
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								this.getLogger().warning("[UUIDSql]An error occured .");
								this.getLogger().severe("Cause: " + e.getMessage());
		
							}
						}
						else if(args[1].equalsIgnoreCase("2"))
						{
							try{      
						         File f = new File("world"+File.separator+"playerdata");
						         this.getLogger().info("world"+File.separator+"playerdata");
						         String[] paths = f.list();
						         for(String path:paths)
						         {
						        	 File tmp = new File("world"+File.separator+"playerdata"+File.separator+path);
						        	 tmp.delete();
						         }
						      }catch(Exception e){
						         e.printStackTrace();
						      }
							sender.sendMessage("[UUIDSql] User data were succesfully deleted");
							
						}
						else if (args[1].equalsIgnoreCase("3")) // Tabe & userlib
						{
							Connection dbCon = null;
							Statement stmt = null;
		
							try {
								String query = "TRUNCATE userUUID";
								dbCon = DriverManager.getConnection(
										dbInformation[0], dbInformation[1],
										dbInformation[2]);
		
								stmt = dbCon.prepareStatement(query);
								stmt.executeUpdate(query);
								
								sender.sendMessage("[UUIDSql]dataBase is now empty !");
								
								dbCon.close();
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								this.getLogger().warning("[UUIDSql]An error occured .");
								this.getLogger().severe("Cause: " + e.getMessage());
		
							}
							
							try{      
						         File f = new File("world"+File.separator+"playerdata");
						         this.getLogger().info("world"+File.separator+"playerdata");
						         String[] paths = f.list();
						         for(String path:paths)
						         {
						        	 File tmp = new File("world"+File.separator+"playerdata"+File.separator+path);
						        	 tmp.delete();
						         }
						      }catch(Exception e){
						         e.printStackTrace();
						      }
							sender.sendMessage("[UUIDSql] User data were succesfully deleted");
							
						}
						
					}					
					else {
						sender.sendMessage("[UUIDSql] What do you wan't to purge ?");
						sender.sendMessage("[UUIDSql] 1. Table");
						sender.sendMessage("[UUIDSql] 2. User Library");
						sender.sendMessage("[UUIDSql] 3. Table & User Library");
						sender.sendMessage("[UUIDSql] To perform the action, type : /uuidsql purge <1,2,3>");

					}
					
				}
				else if (args[0].equalsIgnoreCase("removeUuid") && sender.hasPermission(new Permission("uuidsql.remove")))
				{
					if (args.length >= 2)
					{
						if (args.length == 3)
						{
							
							if (args[2].equalsIgnoreCase("1"))
							{
								Connection dbCon = null;
								Statement stmt = null;

								try {
									String query = "DELETE FROM userUUID WHERE uuid = '"+args[1].toString()+"'";
									dbCon = DriverManager.getConnection(
											dbInformation[0], dbInformation[1],
											dbInformation[2]);

									stmt = dbCon.prepareStatement(query);
									stmt.executeUpdate(query);
									sender.sendMessage("[UUIDSql]Row deleted !");
									dbCon.close();
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									this.getLogger().warning("[UUIDSql]An error occured .");
									this.getLogger().severe("Cause: " + e.getMessage());

								}
							}
							else if(args[2].equalsIgnoreCase("2"))
							{
								File f = new File("world"+File.separator+"playerdata"+File.separator+args[1].toString()+".dat");
								if (f.delete())
									{
										sender.sendMessage("[UUIDSql] File for "+args[1].toString()+" has been deleted");
									}
								else {
									sender.sendMessage("[UUIDSql] An error occured");
								}
							}
							else if (args[2].equalsIgnoreCase("3")) // Tabe & userlib
							{
								Connection dbCon = null;
								Statement stmt = null;

								try {
									String query = "DELETE FROM userUUID WHERE uuid = '"+args[1].toString()+"'";
									dbCon = DriverManager.getConnection(
											dbInformation[0], dbInformation[1],
											dbInformation[2]);

									stmt = dbCon.prepareStatement(query);
									stmt.executeUpdate(query);
									sender.sendMessage("[UUIDSql]Row deleted !");
									dbCon.close();
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									this.getLogger().warning("[UUIDSql]An error occured .");
									this.getLogger().severe("Cause: " + e.getMessage());

								}
								File f = new File("world"+File.separator+"playerdata"+File.separator+args[1].toString()+".dat");
								if (f.delete())
									{
										sender.sendMessage("[UUIDSql] File for "+args[1].toString()+" has been deleted");
									}
								else {
									sender.sendMessage("[UUIDSql] An error occured");
								}
							}
							
						}					
						else {
							sender.sendMessage("[UUIDSql] What do you wan't to remove for "+args[1].toString()+" ?");
							sender.sendMessage("[UUIDSql] 1. Table");
							sender.sendMessage("[UUIDSql] 2. User Library");
							sender.sendMessage("[UUIDSql] 3. Table & User Library");
							sender.sendMessage("[UUIDSql] To perform the action, type : /uuidsql removeUuid "+args[1].toString()+" <1,2,3>");

						}
						
					}
					else {
						sender.sendMessage("[UUIDSql]You myst secify a uuid !");
					}
				}
				else if (args[0].equalsIgnoreCase("removeName") && sender.hasPermission(new Permission("uuidsql.remove")))
				{
					if (args.length >= 2)
					{
						if (args.length == 3)
						{
							
							if (args[2].equalsIgnoreCase("1"))
							{
								Connection dbCon = null;
								Statement stmt = null;

								try {
									String query = "DELETE FROM userUUID WHERE username = '"+args[1].toString()+"'";
									dbCon = DriverManager.getConnection(
											dbInformation[0], dbInformation[1],
											dbInformation[2]);

									stmt = dbCon.prepareStatement(query);
									stmt.executeUpdate(query);
									sender.sendMessage("[UUIDSql]Row deleted !");
									dbCon.close();
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									this.getLogger().warning("[UUIDSql]An error occured .");
									this.getLogger().severe("Cause: " + e.getMessage());

								}
							}
							else if(args[2].equalsIgnoreCase("2"))
							{
								String uuidFound = "";
								try {

									String query = "SELECT uuid FROM userUUID WHERE username = '"
											+ args[1].toString() + "' ";
									Connection dbCon = DriverManager.getConnection(
											dbInformation[0], dbInformation[1],
											dbInformation[2]);

									PreparedStatement stmt = dbCon.prepareStatement(query);
									ResultSet rs = stmt.executeQuery(query);
									while (rs.next()) {
										uuidFound = rs.getString(0);
									}
									dbCon.close();
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									this.getLogger().warning("[UUIDSql]An error occured");
									this.getLogger().severe("Cause: " + e.getMessage());
								}
								File f = new File("world"+File.separator+"playerdata"+File.separator+uuidFound+".dat");
								if (f.delete())
									{
										sender.sendMessage("[UUIDSql] File for "+args[1].toString()+" has been deleted");
									}
								else {
									sender.sendMessage("[UUIDSql] An error occured");
								}
							}
							else if (args[2].equalsIgnoreCase("3")) // Tabe & userlib
							{
								String uuidFound = "";
								try {

									String query = "SELECT uuid FROM userUUID WHERE username = '"
											+ args[1].toString() + "' ";
									Connection dbCon = DriverManager.getConnection(
											dbInformation[0], dbInformation[1],
											dbInformation[2]);

									PreparedStatement stmt = dbCon.prepareStatement(query);
									ResultSet rs = stmt.executeQuery(query);
									while (rs.next()) {
										uuidFound = rs.getString(1);
									}
									dbCon.close();
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									this.getLogger().warning("[UUIDSql]An error occured");
									this.getLogger().severe("Cause: " + e.getMessage());
								}
								File f = new File("world"+File.separator+"playerdata"+File.separator+uuidFound+".dat");
								if (f.delete())
									{
										sender.sendMessage("[UUIDSql] File for "+args[1].toString()+" has been deleted");
										
										try {
											String query = "DELETE FROM userUUID WHERE username = '"+args[1].toString()+"'";
											Connection dbCon = DriverManager.getConnection(
													dbInformation[0], dbInformation[1],
													dbInformation[2]);

											PreparedStatement stmt = dbCon.prepareStatement(query);
											stmt.executeUpdate(query);
											sender.sendMessage("[UUIDSql]Row deleted !");
											dbCon.close();
										} catch (SQLException e) {
											// TODO Auto-generated catch block
											this.getLogger().warning("[UUIDSql]An error occured .");
											this.getLogger().severe("Cause: " + e.getMessage());

										}
									}
								else {
									sender.sendMessage("[UUIDSql] An error occured");
								}
							}
							
						}					
						else {
							sender.sendMessage("[UUIDSql] What do you wan't to remove for "+args[1].toString()+" ?");
							sender.sendMessage("[UUIDSql] 1. Table");
							sender.sendMessage("[UUIDSql] 2. User Library");
							sender.sendMessage("[UUIDSql] 3. Table & User Library");
							sender.sendMessage("[UUIDSql] To perform the action, type : /uuidsql removename "+args[1].toString()+" <1,2,3>");

						}
						
					}
					else {
						sender.sendMessage("[UUIDSql]You myst secify a username !");
					}
					
				}
				else {
					sender.sendMessage("[UUIDSql]This command does not exist or you didn't have the permission to perform it");
				}
			} else {
				sender.sendMessage("Availbale commands");
				sender.sendMessage("1. uuidsql reload");
				sender.sendMessage("2. uuidsql getName <uuid>");
				sender.sendMessage("3. uuidsql getUuid <name>");
				sender.sendMessage("4. uuidsql purge");
				sender.sendMessage("5. uuidsql removeUuid <uuid>");
				sender.sendMessage("6. uuidsql removeName <name>");
			}

		}
		return false;

	}
        private static void createTables(Connection connection, Plugin plugin) {
		
		Statement statement;
		try {
			statement = connection.createStatement();
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS `userUUID` ("+
                                                "`id` int(11) NOT NULL AUTO_INCREMENT,"+
                                                "`uuid` text NOT NULL,"+
                                                "`old_uuid` text ,"+
                                                "`username` text NOT NULL,"+
                                                "PRIMARY KEY (`id`)"+
                                                ");");
			plugin.getLogger().info("[UUIDSql] MySQL table has been created");
		} catch (SQLException e) {
			plugin.getLogger().severe("[UUIDSql]An error occured while creating MySQL table, please change the config file");
			plugin.getLogger().severe("Cause: " + e.getMessage());
		}
		
	}


}