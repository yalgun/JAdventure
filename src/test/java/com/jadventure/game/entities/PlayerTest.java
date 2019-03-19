package com.jadventure.game.entities;

import com.jadventure.game.entities.Player;

import org.junit.Test;
import org.junit.Before;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class PlayerTest {

    @Test
    public void newRecruit() {
        Player player = Player.getInstance("recruit","Elf");
        int expected = 1;
        int actual = player.getArmour();
        assertEquals("Failure - new recruit not properly created", expected, actual);
    }

    @Test
    public void newSewerRat() {
        Player player = Player.getInstance("sewerrat","Dwarf");
        int expected = 0;
        int actual = player.getArmour();
        assertEquals("Failure - new sewer rat not properly created", expected, actual);
    }

   // @Test
    public void oldPlayer() {
        Player player = Player.load("test");
        String expected = "test";
        String actual = player.getName();
        assertEquals("Failure - old player not properly loaded", expected, actual);
    }
}
