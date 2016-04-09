import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import nl.tudelft.jpacman.Launcher;
import nl.tudelft.jpacman.PacmanConfigurationException;
import nl.tudelft.jpacman.board.Board;
import nl.tudelft.jpacman.board.BoardFactory;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.game.GameFactory;
import nl.tudelft.jpacman.level.Level;
import nl.tudelft.jpacman.level.LevelFactory;
import nl.tudelft.jpacman.level.MapParser;
import nl.tudelft.jpacman.level.Pellet;
import nl.tudelft.jpacman.level.Player;
import nl.tudelft.jpacman.npc.ghost.Ghost;
import nl.tudelft.jpacman.npc.ghost.GhostFactory;
import nl.tudelft.jpacman.npc.ghost.Navigation;
import nl.tudelft.jpacman.sprite.PacManSprites;

public class HW1 {

private Launcher launcher;
private Launcher1 launcher1;
private MapParser parser;
	
	/**
	 * Launch the user interface.
	 */
	@Before
	public void setUpPacman() {
		
		launcher = new Launcher();
		launcher.launch();
	}
	
	/**
	 * Quit the user interface when we're done.
	 */
	@After // called after every test case method, disposes off
	public void tearDown() {
		launcher.dispose();
	}
	
	//-------------------------------------------Scenario 1---------------------------------------------------------
	//scenario s1.1
	@Test
	public void starts1(){
		Game game = launcher.getGame(); 
		assertFalse(game.isInProgress()); //test if game has not already started
		game.start(); //start the game
		assertTrue(game.isInProgress()); //test if it has started
	}
	
	
	
	//---------------------------------------------Scenario 2-------------------------------------------------------------
	

	//scenario s2.1
	@Test
	public void consume_pellet(){
		Game game = launcher.getGame(); 
		Player player = game.getPlayers().get(0);
		game.start(); //start the game
		assertTrue(game.isInProgress()); //test if game is in progress
		assertEquals(0, player.getScore()); // test if initially score is 0
		//move the player 2 squares east
		game.move(player, Direction.EAST);
		game.move(player,Direction.EAST);
		//test if score is 20 as 2 pellets consumed
		assertEquals(20,player.getScore());
		//move the player back one square to test if pellet has disappeared and ensure score remains the same
		game.move(player, Direction.WEST);
		assertEquals(20,player.getScore());

	}
	
	@Test
	//scenario s2.2
	public void empty_square(){
		
		Game game = launcher.getGame(); 
		Player player = game.getPlayers().get(0);
		
		game.start(); //start the game
		assertTrue(game.isInProgress()); //test if game is in progress
		assertEquals(0, player.getScore()); // test if initially score is 0	
		//moved player by one square and test if score is updated
		game.move(player, Direction.EAST);
		assertEquals(10,player.getScore());
		//now the player has a blank square behind him
		//moving him to that square
		game.move(player, Direction.WEST);
		//ensuring score remains the same
		assertEquals(10,player.getScore());			
	}
	
	@Test
	//scenario s2.3
	public void player_dies() throws InterruptedException{
		Game game = launcher.getGame();        
        Player player = game.getPlayers().get(0);
 
        // start cleanly.
        assertFalse(game.isInProgress());
        game.start();
        assertTrue(game.isInProgress());
        assertEquals(0, player.getScore());

        // get points
        game.move(player, Direction.EAST);
        assertEquals(10, player.getScore());

        // now moving back does not change the score
        game.move(player, Direction.WEST);
        assertEquals(10, player.getScore());

        // try to move as far as we can
        move(game, Direction.EAST, 7);
        assertEquals(60, player.getScore());

        // move towards the monsters
        move(game, Direction.NORTH, 6);
        assertEquals(120, player.getScore());

        // no more points to earn here.
        move(game, Direction.WEST, 2);
        assertEquals(120, player.getScore());

        move(game, Direction.NORTH, 2);
        
        // Sleeping in tests is generally a bad idea.
        // Here we do it just to let the monsters move.
        Thread.sleep(500L);
      
        // we're close to monsters, this will get us killed.
        move(game, Direction.WEST, 10);
        move(game, Direction.EAST, 10);
        assertFalse(player.isAlive());

        game.stop();
        assertFalse(game.isInProgress());
	}
	
	public static void move(Game game, Direction dir, int numSteps) {
        Player player = game.getPlayers().get(0);
        for (int i = 0; i < numSteps; i++) {
            game.move(player,dir);
        }
	}
	
	@Test
	//scenario s2.4
	public void wall_block(){
		Game game = launcher.getGame();        
        Player player = game.getPlayers().get(0);
        //test if game is not already in progress
        assertFalse(game.isInProgress());
        game.start();
        //ensure the game is in progress
        assertTrue(game.isInProgress());
        //move the player north as there is a wall
        game.move(player, Direction.NORTH);
        //get the player's location
        Square s1 = player.getSquare();
        //move it again in the same direction
        game.move(player, Direction.NORTH);
        //get the location
        Square s2 = player.getSquare();
        //test if both locations are same to ensure no movement of player
        assertEquals(s1,s2);	
	}

	@Test
	//scenario s2.5
	public void player_wins(){
		launcher1 = new Launcher1();
		launcher1.launch(Lists.newArrayList("#####", "#P..#","## G#","#####"));
		//launcher1 launches a new board of size 4x5 with 2 pellets,a player and a ghost
		Game game = launcher1.getGame(); 
		Player player = game.getPlayers().get(0);
		assertFalse(game.isInProgress()); //test if game has not already started
		assertEquals(0, player.getScore()); //check if initial score is 0
		game.start(); //start the game
		assertTrue(game.isInProgress()); //test if it has started
		//since the board has only 2 pellets, to win he has to finish all the pellets
		game.move(player, Direction.EAST);
		game.move(player, Direction.EAST);
		//Player wins if his total score is 20 
		assertEquals(20,player.getScore());
		// Also ensure that player is alive even though the game has ended to indicate that he won
		assertTrue(player.isAlive());
		assertFalse(game.isInProgress());
	}
	
	
	//------------------------------------------------------Scenario 3------------------------------------------------------------
	
	@Test
	//scenario s3.1
	public void ghost_moves() throws InterruptedException{
		launcher1 = new Launcher1();
		launcher1.launch(Lists.newArrayList("#####", "#P.##","## G#", "#####"));
		//launcher1 launches a new board of size 4x5 with 1 pellets,a player, a ghost and empty space
		Game game = launcher1.getGame();
		//Got the board
		Board b = game.getLevel().getBoard();
		//Initial position of ghost on the board
		Square s3 = b.squareAt(2, 2);
		Square s2 = Navigation.findNearest(Ghost.class,s3).getSquare();
		Unit ghost = s2.getOccupants().get(0);
		//ghost.setDirection(Direction.WEST);
		//Ensure game is not on and start the game
		assertFalse(game.isInProgress());
		game.start();
		assertTrue(game.isInProgress());
		//Allows the ghost to move
		Thread.sleep(250L);
		//current position of ghost
		Square s4 = ghost.getSquare();
		//Test if both current position of ghost is the empty space 
		assertEquals(s3,s4);
	}
	
	@Test
	//scenario s3.2
	public void pellet_invisible() throws InterruptedException{
		launcher1 = new Launcher1();
		launcher1.launch(Lists.newArrayList("#####", "#P.G#", "#####"));
		//launcher1 launches a new board of size 3x5 with 1 pellets,a player and a ghost
		Game game = launcher1.getGame();
		//Ensure game is not on and start the game
		assertFalse(game.isInProgress());
		game.start();
		assertTrue(game.isInProgress());
		//Got the board
		Board b = game.getLevel().getBoard();
		Square s1 = b.squareAt(0,0);
		Unit unit = Navigation.findNearest(Ghost.class, s1);
		Ghost g = (Ghost) unit;
		//make ghost travel 1 step to the next square containing pellet
		Thread.sleep(250L);
		//location of pellet
		Square s2 = b.squareAt(2,1);
		//Get the number of occupants of the square containing both pellet and ghost
		int n = s2.getOccupants().size();
		//Test if the size = 2 indicating that both ghost and pellet exist together
		assertEquals(2,n);	
		//index of last occupant is visible index, hence ghost should be visible and pellet invisible
		int x=s2.getOccupants().indexOf(g);
		assertEquals(1,x);
	}
	
	//scenario s3.3
	@Test
	public void pellet_visible() throws InterruptedException{
		launcher1 = new Launcher1();
		launcher1.launch(Lists.newArrayList("#####", "#P.G#", "#####"));
		//launcher1 launches a new board of size 3x5 with 1 pellets,a player and a ghost
		Game game = launcher1.getGame();
		//Ensure game is not on and start the game
		assertFalse(game.isInProgress());
		game.start();
		assertTrue(game.isInProgress());
		//make ghost travel 1 step to the next square containing pellet
		Thread.sleep(250L);
		//Got the board
		Board b = game.getLevel().getBoard();
		//location of pellet
		Square s1 = b.squareAt(2,1);
		Square s2 = Navigation.findNearest(Ghost.class,s1).getSquare();
		Unit ghost = s2.getOccupants().get(0);
		//change direction of ghost and make it traverse back to its location
		ghost.setDirection(Direction.EAST);
		//Let it move at least 1 square
		Thread.sleep(250L);
		//Get the number of occupants of the square containing both pellet and ghost
		int n = s1.getOccupants().size();
		//Test if the size = 1 indicating that ghost leaves and makes pellet visible
		assertEquals(1,n);	
	}
	
	//scenario s3.4
	@Test
	public void player_ghost_dies() throws InterruptedException{
		
		launcher1 = new Launcher1();
		launcher1.launch(Lists.newArrayList("#####", "#.PG#", "#####"));
		//launcher1 launches a new board of size 3x5 with 1 pellets,a player and a ghost
		Game game = launcher1.getGame();
		Player player = game.getPlayers().get(0);
		//Ensure game is not on and start the game
		assertFalse(game.isInProgress());
		game.start();
		assertTrue(game.isInProgress());
		
		//Allows the ghost to move at least 1 square ahead to kill pacman
		Thread.sleep(250L);
		//Test that the player is not alive and the game has ended
		assertFalse(player.isAlive());
		assertFalse(game.isInProgress());	
	}
	
	//-----------------------------------------------------Scenario 4--------------------------------------------------------------------------
	
	@Test
	//scenario s4.1
	public void suspend(){
		Game game = launcher.getGame();
		Player player = game.getPlayers().get(0);
		//start the game
		game.start();
		//test if it is in progress
		assertTrue(game.isInProgress());
		//pause the game
		game.stop();
		//test if it has suspended
		assertFalse(game.isInProgress());	
		//restart the game
		game.start();
		//test if it has resumed
		assertTrue(game.isInProgress());	
	}
	
	@Test
	//scenario s4.2
	public void resume(){
		Game game = launcher.getGame();
		Player player = game.getPlayers().get(0);
		//ensure game is not already in progress
		assertFalse(game.isInProgress());
		game.start(); //start the same
		game.move(player, Direction.EAST);
		//stop the game
		game.stop();
		//resume the game
		game.start();
		//check if the game has been resumed
		assertTrue(game.isInProgress());
	}
	
}
