package com.jadventure.game.menus;

import com.jadventure.game.entities.Player;
import com.jadventure.game.DeathException;
import com.jadventure.game.Game;
import com.jadventure.game.QueueProvider;

/**
 * Called when creating a new Player
 */
public class ChooseClassMenu extends Menus {

    
    public ChooseClassMenu() throws DeathException {
        String classString;
        String raceString;
        this.menuItems.add(new MenuItem("Recruit", "A soldier newly enlisted to guard the city of Silliya"));
        this.menuItems.add(new MenuItem("SewerRat", "A member of the underground of Silliya"));
        
        while(true) {
            QueueProvider.offer("Choose a class to get started with:");
            MenuItem selectedItem = displayMenu(this.menuItems);
            if(selectedItem.getKey().equals("recruit")||selectedItem.getKey().equals("sewerrat")) {
                classString=selectedItem.getKey();
            	break;
            }
        }
        this.menuItems.clear();
        this.menuItems.add(new MenuItem("Dwarf", "%50 additional luck"));
        this.menuItems.add(new MenuItem("Human", "%20 additional health"));
        this.menuItems.add(new MenuItem("Elf", "additional intelligence"));
        while(true) {
            QueueProvider.offer("Choose a race to get started with:");
            MenuItem selectedItem = displayMenu(this.menuItems);
            if(selectedItem.getKey().equals("dwarf")||selectedItem.getKey().equals("human")||selectedItem.getKey().equals("elf")) {
                raceString=selectedItem.getKey();
            	break;
            }
        }
        
        //System.out.println(classString+" "+ raceString);
        build(classString,raceString);
    }
    
    private static void build(String sinif,String irk) throws DeathException{
        Player player = Player.getInstance(sinif, irk);
        new Game(player, "new");
    }
}
