package fr.dgiproject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
	
    private final UUIDSql plugin;
    Connection dbCon = null;
    Statement stmt = null;
    ResultSet rs = null;
    String[] dbInformation = new String[3];
	
	public PlayerListener(UUIDSql instance, String[] dbInfo) {
        plugin = instance;
        dbInformation = dbInfo;
    }
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		
		Player player = event.getPlayer();
    	plugin.getLogger().info("New player "+event.getPlayer().getName()+ " uuid: "+event.getPlayer().getUniqueId());
 	
        	String query = "SELECT COUNT(*) FROM userUUID WHERE uuid = '"+player.getUniqueId().toString()+"' OR username = '"+player.getName().toString()+"'";
        	try {
        		dbCon = DriverManager.getConnection(dbInformation[0], dbInformation[1], dbInformation[2]);
				stmt = dbCon.prepareStatement(query);
				rs = stmt.executeQuery(query);
				while(rs.next()){
					int count = rs.getInt(1);
					if (count == 0)
					{
						// We have to create the record for the specific user... 
						query = "INSERT INTO userUUID(id,uuid,username) VALUES( NULL ,'"+player.getUniqueId()+"','"+player.getName()+"')";				
						stmt = dbCon.prepareStatement(query);
						stmt.executeUpdate(query);	
						plugin.getLogger().info("Creating new record for user "+player.getName()+" with UUID "+player.getUniqueId());
					}
					else
					{
						// The user already exist and we update information if they change !
						
						query = "SELECT COUNT(*) FROM userUUID WHERE uuid = '"+player.getUniqueId().toString()+"' ";
						stmt = dbCon.prepareStatement(query);
						rs = stmt.executeQuery(query);
						while(rs.next()){
							int count2 = rs.getInt(1);
							if (count2 == 0)
							{
								query = "UPDATE userUUID SET old_uuid = uuid, uuid = '"+player.getUniqueId()+"' WHERE username = '"+player.getName().toString()+"'";				
								stmt = dbCon.prepareStatement(query);
								stmt.executeUpdate(query);	
								plugin.getLogger().info("Updating record for user "+player.getName()+" with UUID "+player.getUniqueId());
							}							
						}
						
						
						
					}
				}
				dbCon.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				plugin.getLogger().warning("An error occured ");
				plugin.getLogger().severe("Cause : "+e.getMessage());
			}
        	
        	
           

	}
}
