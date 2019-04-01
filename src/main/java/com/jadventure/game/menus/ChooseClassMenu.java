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
        String petString;
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
        this.menuItems.add(new MenuItem("Elf", "Additional intelligence"));
        this.menuItems.add(new MenuItem("Wizard", "Spell ability"));
        while(true) {
            QueueProvider.offer("Choose a race to get started with:");
            MenuItem selectedItem = displayMenu(this.menuItems);
            if(selectedItem.getKey().equals("dwarf")||selectedItem.getKey().equals("human")||selectedItem.getKey().equals("elf")||selectedItem.getKey().equals("wizard")) {
                raceString=selectedItem.getKey();
            	break;
            }
        }
        
        //petStart
        this.menuItems.clear();
        this.menuItems.add(new MenuItem("Hawk", "The Protector of the sky"));
        this.menuItems.add(new MenuItem("Wolf", "The Leader of the herd"));
        this.menuItems.add(new MenuItem("Leopard", "The Legend of the Earth"));
        while(true) {
            QueueProvider.offer("Choose a pet to get started with:");
            MenuItem selectedItem = displayMenu(this.menuItems);
            //System.out.println("the key is " +selectedItem.getKey());
            if(selectedItem.getKey().equals("hawk")||selectedItem.getKey().equals("wolf")||selectedItem.getKey().equals("leopard")) {
                petString=selectedItem.getKey();
            	break;
            }
        }
        //pet end
        
        //System.out.println(classString+" "+ raceString);
        build(classString,raceString,petString);
    }
    
    private static void build(String sinif,String irk, String pet) throws DeathException{
        Player player = Player.getInstance(sinif, irk, pet);
        new Game(player, "new");
    }
}
