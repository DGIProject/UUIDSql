package fr.dgiproject;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
				} else if (args[0].equalsIgnoreCase("reload")) {

					String dbURL = this.getConfig().getString("host")
							+ this.getConfig().getString("dbName");
					String username = this.getConfig().getString("Username");
					String password = this.getConfig().getString("Password");

					dbInformation[0] = dbURL;
					dbInformation[1] = username;
					dbInformation[2] = password;

					sender.sendMessage("[UUIDSql]Reloaded");

				}
				else if (args[0].equalsIgnoreCase("purge"))
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
				else if (args[0].equalsIgnoreCase("removeUuid"))
				{
					if (args.length == 2)
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
					else {
						sender.sendMessage("[UUIDSql]You myst secify a uuid !");
					}
				}
				else if (args[0].equalsIgnoreCase("removeName"))
				{
					if (args.length == 2)
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
					else
					{
						sender.sendMessage("[UUIDSql]You myst specify a name !");
					}
					
				}
				else {
					sender.sendMessage("[UUIDSql]There is a problem, this command doen't exist !");
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
                                                "`username` text NOT NULL,"+
                                                "PRIMARY KEY (`id`)"+
                                                ") ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=0 ;");
			plugin.getLogger().info("[UUIDSql] MySQL table has been created");
		} catch (SQLException e) {
			plugin.getLogger().severe("[UUIDSql]An error occured while creating MySQL table, please change the config file");
			plugin.getLogger().severe("Cause: " + e.getMessage());
		}
		
	}


}