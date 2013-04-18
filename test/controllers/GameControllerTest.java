package controllers;

import models.Game;
import models.Player;

import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GameControllerTest extends BaseControllerTest {
    
	@Test
	public void gamesForANewPlayerIsEmpty(){
		Player player = new Player("NewUser", "email");
		response = GET("/games?player1Id=" + player.getId());

    	JsonParser parser = new JsonParser();
        JsonArray games = parser.parse(response.out.toString()).getAsJsonObject().getAsJsonArray("games");

        assertEquals(0, games.size());
	}
	
	@Test
	public void OneGameReturnsOneGame(){
		Player localPlayer = new Player("local", "localmail");
		localPlayer.save();
		Player visitorPlayer = new Player("visitor", "visitormail");
		visitorPlayer.save();
		
		Game game = new Game(localPlayer, visitorPlayer, 2, 3);
		game.save();
		
		response = GET("/games");

    	JsonParser parser = new JsonParser();
        JsonArray games = parser.parse(response.out.toString()).getAsJsonObject().getAsJsonArray("games");
        
        assertEquals(1, games.size());
	}
	
	@Test
	public void gamesBetweenTwoPlayers(){
		Player localPlayer = new Player("local", "localmail");
		localPlayer.save();
		Player visitorPlayer = new Player("visitor", "visitormail");
		visitorPlayer.save();
		Player otherPlayer = new Player("other", "othermail");
		otherPlayer.save();
		
		Game game = new Game(localPlayer, visitorPlayer, 2, 3);
		game.save();
		sleep(1);
		Game game2 = new Game(visitorPlayer, localPlayer, 2, 0);
		game2.save();
		sleep(1);
		Game otherGame = new Game(otherPlayer, visitorPlayer, 2, 3);
		otherGame.save();
		
		response = GET("/games?player1Id=" + localPlayer.getId() + "&player2Id=" + visitorPlayer.getId());
    	JsonParser parser = new JsonParser();
        JsonArray games = parser.parse(response.out.toString()).getAsJsonObject().getAsJsonArray("games");

        assertEquals(2, games.size());
        
        // Ordered correctly ?
        JsonObject firstGame = games.get(0).getAsJsonObject();        
        JsonObject secondGame = games.get(1).getAsJsonObject();
        
        long firstGamePlayedOn = firstGame.getAsJsonPrimitive("playedOn").getAsLong();
        long secondGamePlayedOn = secondGame.getAsJsonPrimitive("playedOn").getAsLong();
        assertTrue(firstGamePlayedOn > secondGamePlayedOn);
	}

	@Test
	public void pagingGames(){
		Player localPlayer = new Player("local", "localmail");
		localPlayer.save();
		Player visitorPlayer = new Player("visitor", "visitormail");
		visitorPlayer.save();
		Player otherPlayer = new Player("other", "othermail");
		otherPlayer.save();
		
		Game game = new Game(localPlayer, visitorPlayer, 2, 3);
		game.save();
		Game game2 = new Game(visitorPlayer, localPlayer, 2, 0);
		game2.save();
		Game otherGame = new Game(otherPlayer, visitorPlayer, 2, 3);
		otherGame.save();
		Game anotherGame = new Game(visitorPlayer, otherPlayer, 2, 3);
		anotherGame.save();

		response = GET("/games?player1Id=" + visitorPlayer.getId() + "&page=1&recsPerPage=1");				
		JsonParser parser = new JsonParser();
        JsonArray games = parser.parse(response.out.toString()).getAsJsonObject().getAsJsonArray("games");
        
        assertEquals(1, games.size());
	}
	
	/*
	 * - añadir un game sin algun parametro basico lanza excepcion
	 * - añadir un game con uno mismo
	 * - añadir un game ok
	 * - añadir un game, asegurarse que el score no es el default 	
	 * 
	 */
}