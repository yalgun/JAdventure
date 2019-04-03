package com.jadventure.game.menus;

import com.jadventure.game.DeathException;
import com.jadventure.game.entities.Entity;
import com.jadventure.game.entities.Player;
import com.jadventure.game.entities.NPC;
import com.jadventure.game.monsters.Monster;
import com.jadventure.game.QueueProvider;
import com.jadventure.game.CharacterChange;
import com.jadventure.game.items.ItemStack;
import com.jadventure.game.items.Item;
import com.jadventure.game.GameBeans;
import java.util.Scanner;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class BattleMenu extends Menus {

    private NPC opponent;
    private Player player;
    private Random random;
    private int armour;
    private double damage;
    private int escapeSuccessfulAttempts = 0;
    private int counter = 0;

    public BattleMenu(NPC opponent, Player player) throws DeathException {
        this.random = new Random();
        this.opponent = opponent;
        this.player = player;
        this.armour = player.getArmour();
        this.damage = player.getDamage();
        buildMenu();
        while (opponent.getHealth() > 0 &&
                player.getHealth() > 0 &&
                (escapeSuccessfulAttempts <= 0)) {
            QueueProvider.offer("\nWhat is your choice?");
            MenuItem selectedItem = displayMenu(this.menuItems);
            testSelected(selectedItem);
        }
        if (player.getHealth() == 0) {
            QueueProvider.offer("You died... Start again? (y/n)");
            String reply = QueueProvider.take().toLowerCase();
            while (!reply.startsWith("y") && !reply.startsWith("n")) {
                QueueProvider.offer("You died... Start again? (y/n)");
                reply = QueueProvider.take().toLowerCase();
            }
            if (reply.startsWith("y")) {
                throw new DeathException("restart");
            } else if (reply.startsWith("n")) {
                throw new DeathException("close");
            }
        }  else if (opponent.getHealth() == 0) {
            int xp = opponent.getXPGain();
            this.player.setXP(this.player.getXP() + xp);
            int oldLevel = this.player.getLevel();
            int newLevel = (int) (0.075 * Math.sqrt(this.player.getXP()) + 1);
            this.player.setLevel(newLevel);
            this.player.getPet().setPetEnergy(this.player.getPet().getPetEnergy()+1);
            
            if( this.player.getPet().getHasPet()) {
              int tempPetH = this.player.getPet().getPetHealth();	
              this.player.getPet().setPetHealth(this.player.getPet().getPetHealth()+5);
              if( ( (this.player.getPet().getPetHealth()) - tempPetH ) < 5 && ( (this.player.getPet().getPetHealth()) - tempPetH ) > 0 ) {
            	QueueProvider.offer(this.player.getPet().getPetName() + " recovered " + ( (this.player.getPet().getPetHealth()) - tempPetH )  + " health!");
            
              }else if (( (this.player.getPet().getPetHealth()) - tempPetH ) == 0) {
            	QueueProvider.offer(this.player.getPet().getPetName() + " already has maximum health!");
              }else {// your pet recover 5 health
             	QueueProvider.offer(this.player.getPet().getPetName() + " recovered 5 health!");
              }
            }
            this.player.setMana(player.getMana()+10);

            // Iterates over the opponent's items and if there are any, drops them.
            // There are two loops due to a ConcurrentModification Exception that occurs
            // if you try to remove the item while looping through the npc's items.
            List<ItemStack> itemStacks = opponent.getStorage().getItemStack();
            List<String> itemIds = new ArrayList<>();
            for (ItemStack itemStack : itemStacks) {
                String itemId = itemStack.getItem().getId();
                itemIds.add(itemId);
            }
            for (String itemId : itemIds) {
                Item item = GameBeans.getItemRepository().getItem(itemId);
                opponent.removeItemFromStorage(item);
                this.player.getLocation().addItem(item);
                QueueProvider.offer("Your opponent dropped a " +
                        item.getName());
            }

            this.player.getLocation().remove(opponent);
            this.player.setGold(this.player.getGold() + opponent.getGold());
            QueueProvider.offer("You killed a " + opponent.getName() +
                    "\nYou have gained " + xp + " XP and " +
                    opponent.getGold() + " gold");

            if (oldLevel < newLevel) {
            	this.player.getPet().setPetDamage(this.player.getPet().getPetDamage() + newLevel);
                QueueProvider.offer("You've are now level " + newLevel + "!");
            }
            CharacterChange cc = new CharacterChange();
            cc.trigger(this.player, "kill", opponent.getName());
        }
    }

    private void buildMenu() {
        this.menuItems.add(new MenuItem("Attack",
                    "Attack " + opponent.getName() + "."));
        if(player.getRace().equalsIgnoreCase("Wizard"))
        this.menuItems.add(new MenuItem("Skill",
                "Choose a Skill against " + opponent.getName() + "."));
        if(player.getPet().getPetEnergy()>0) {
        this.menuItems.add(new MenuItem("Attack With Pet",
                "Choose a Style against " + opponent.getName() + "."));
        }
        this.menuItems.add(new MenuItem("Defend",
                    "Defend against " + opponent.getName() + "'s attack."));
        this.menuItems.add(new MenuItem("Escape",
                    "Try and escape from " + opponent.getName()));
        this.menuItems.add(new MenuItem("Equip", "Equip an item"));
        this.menuItems.add(new MenuItem("Unequip", "Unequip an item"));
        this.menuItems.add(new MenuItem("View",
                    "View details about your character"));
    }

    private void testSelected(MenuItem m) {
        switch (m.getKey()) {
            case "attack": {
                   mutateStats(1, 0.5);
                   attack(player, opponent);
                   attack(opponent, player);
                   resetStats();
                   break;
            }
            case "skill": {
                mutateStats(1, 0.5);
                skill(player, opponent);
                attack(opponent, player);
                resetStats();
                break;
         }
            case "attack with pet":{
            	if(player.getPet().getPetEnergy()>0) {
                mutateStats(1, 0.5);
                attackWithPet(player, opponent);
                attack(opponent, player);
                resetStats();
                break;  
                }else {
                	if(player.getPet().getPetHealth() < 1) {
                	System.out.println("Your pet is already dead! so you will attack alone!");	
                	}else {
                	System.out.println("Your pet has not enough energy, so you will attack alone!");
                	}
                	 mutateStats(1, 0.5);
                     attack(player, opponent);
                     attack(opponent, player);
                     resetStats();
                     break;
                }
                
            }
            case "defend": {
                   mutateStats(0.5, 1);
                   QueueProvider.offer("\nYou get ready to defend against " +
                           "the " + opponent.getName() + ".");
                   attack(player, opponent);
                   attack(opponent, player);
                   resetStats();
                   break;
            }
            case "escape": {
                       escapeSuccessfulAttempts = escapeAttempt(player,
                               opponent, escapeSuccessfulAttempts);
                   break;
            }
            case "equip": {
                   equip();
                   break;
            }
            case "unequip": {
                  unequip();
                  break;
            }
            case "view": {
                  viewStats();
                  break;
            }
            default: {
                  break;
            }
        }
    }

    private int escapeAttempt(Player player, NPC attacker,
            int escapeAttempts) {
        if (escapeAttempts == -10) {
            escapeAttempts = 0;
        }
        double playerEscapeLevel = player.getIntelligence() +
            player.getStealth() + player.getDexterity();
        double attackerEscapeLevel = attacker.getIntelligence() +
            attacker.getStealth() + attacker.getDexterity() +
            (attacker.getDamage() / playerEscapeLevel);
        double escapeLevel = playerEscapeLevel / attackerEscapeLevel;

        Random rand = new Random();
        int rawLuck = rand.nextInt(player.getLuck()*2) + 1;
        int lowerBound = 60 - rawLuck;
        int upperBound = 80 - rawLuck;
        double minEscapeLevel = (rand.nextInt((upperBound - lowerBound) + 1) +
                lowerBound) / 100.0;
        if (escapeLevel > minEscapeLevel && (escapeAttempts == 0)) {
            QueueProvider.offer("You have managed to escape the: " +
                    attacker.getName());
            return 1;
        } else if (escapeAttempts < 0) {
            QueueProvider.offer("You have tried to escape too many times!");
            return escapeAttempts - 1;
        } else {
            QueueProvider.offer("You failed to escape the: " +
                    attacker.getName());
            return escapeAttempts-1;
        }
    }
    
    private void skill(Entity attacker, Entity defender) {
    	boolean k = false;
    	boolean mana = true;
    	String skill = "";
    	Scanner s = new Scanner(System.in);
    	int bonus = 0;
    	QueueProvider.offer("Please choose a skill (1-5)");
    	if(counter<4)
    	QueueProvider.offer("\nWarning you can only use 3 times in one combat.\nIf you use more than 3 times you will get more damage from the enemy.\n");
    	while(k == false) {   	
    		System.out.println("[1] - Fire Ball (+9 Fire damage) (-5 mana)");
    		System.out.println("[2] - Lightning Bolt (+10 Lightning damage) (-7 mana)");
    		System.out.println("[3] - Darkness (+7 Dark damage) (-4 mana)");
    		System.out.println("[4] - Holy Light (+10 Holy damage) (-7 mana)");
    		System.out.println("[5] - Heal (+15-25 Heal) (-10 mana)");
		 String pp = s.nextLine();
		 if(pp.trim().equals("1")) {
			 if(attacker.getMana() >= 5) {
			 skill = "Fire Ball";
			 k = true;
			 bonus = 9;
			 attacker.setMana(attacker.getMana()-5);
			 }
			 else {
				 System.out.println("Not enought Mana");
				mana = false; 
				k= true;
			 }
		 }
		 else if(pp.trim().equals("2")) {
			 if(attacker.getMana() >= 7) {
			 skill = "Lightning Bolt";
			 k = true;
			 bonus = 10;
			 attacker.setMana(attacker.getMana()-7);
			 }
			 else {
				mana = false;
				k= true;
				 System.out.println("Not enought Mana");
			 }
		 }
			 else if(pp.trim().equals("3")) {
				 if(attacker.getMana() >= 4) {		 
				 skill = "Darkness";
				 k = true;
				 bonus = 7;
				 attacker.setMana(attacker.getMana()-4);
				 }
				 else {
					 System.out.println("Not enought Mana");
					mana = false; 
					k= true;
				 }
			 }
				 else if(pp.trim().equals("4")) {
					 if(attacker.getMana() >= 7) {				 
					 skill = "Holy Light";
					 k = true;
					 bonus = 10;
					 attacker.setMana(attacker.getMana()-7);
					 }
					 else {
						 System.out.println("Not enought Mana");
						mana = false;
						k= true;
					 }
				 }
				 else if(pp.trim().equals("5")) {
					 if(attacker.getMana() >= 10) {			 
					 skill = "Heal";
					 k = true;
					 bonus = 15+(int)(Math.random()*10);
					 attacker.setMana(attacker.getMana()-10);
					 }
					 else {
						 System.out.println("Not enought Mana");
					 mana = false;
					 k= true;
					 
					 }
				 }
				 else {
					 System.out.println("Wrong input.  Please write a number between 1 and 4 \n");
				 }
    	
    	}
        if (attacker.getHealth() == 0) {
            return;
        }
        if(!skill.equals("Heal") && mana == true) {
        	counter++;
        double damage = attacker.getDamage();
        double critCalc = random.nextDouble();
        if (critCalc < attacker.getCritChance()) {
            damage += damage;
            QueueProvider.offer("Crit hit! Damage has been doubled!");
        }
        int healthReduction = (int) ((((4 * attacker.getLevel() / 50 + 2) *
                damage * damage / (defender.getArmour() + 1)/ 100) + 2) *
                (random.nextDouble() + 1)) + bonus;
        defender.setHealth((defender.getHealth() - healthReduction));
        if (defender.getHealth() < 0) {
            defender.setHealth(0);
        }
        QueueProvider.offer(healthReduction + " damage dealt! by " + skill);
        
   
        if (attacker instanceof Player) {
            QueueProvider.offer("The " + defender.getName() + "'s health is " +
                    defender.getHealth());
        } else {
            QueueProvider.offer("Your health is " + defender.getHealth());
        }
        }
        else if (mana == true) {
        	counter++;
            QueueProvider.offer("You healed yourself with " + skill + " +" + bonus + " Health");
            attacker.setHealth(attacker.getHealth()+bonus);
            }
        else {
        	
        	QueueProvider.offer("You attack normally");
        	attack(attacker,defender);
        }
    }
    //attack with pet starts
    private void attackWithPet(Entity attacker, Entity defender) {
    	//System.out.println("attackers name and his pets energy: "+ attacker.getName() + attacker.getPetEnergy());
    	counter++;
    	boolean b = false;
    	String style = "";
    	Scanner s = new Scanner(System.in);
    	int bonusDamage = 0;
    	QueueProvider.offer("Please choose a Style (1-3)");
    	//if(counter<4)
    	//QueueProvider.offer("\nWarning you can only use 3 times in one combat.\nIf you use more than 3 times you will get more damage from the enemy.\n");
    	while(b == false) {   	
    		System.out.println("[1] - Fast Attack (cost 1 Energy bar)");
    		System.out.println("[2] - Confusion Attack (cost 1 Energy bar)");
    		if(attacker.getPet().getPetEnergy()>1) {
    		System.out.println("[3] - Charge Attack (It uses 2 Energy bar from your pet)");
    		}else {
    		System.out.println("Your pet has not enough energy to use Charge Attack");
    		}
		 String r = s.nextLine();
		 if(attacker.getPet().getPetEnergy() == 0) {
	    	 bonusDamage = 0;
	    	 System.out.println("Your pet has not enough energy to attack with you, you will attack alone\n");
	    	 b = true;
	    	 bonusDamage = 0;
	     }
		 else if(r.trim().equals("1") && attacker.getPet().getPetEnergy() > 0) {
			 style = "Fast Attack";
			 b = true;
			 bonusDamage = attacker.getPet().getPetDamage();
			 attacker.getPet().setPetEnergy(attacker.getPet().getPetEnergy()-1);
		 }
	     else if(r.trim().equals("2") && attacker.getPet().getPetEnergy() > 0) {
			 style = "Confusion Attack";
			 b = true;
			 bonusDamage = attacker.getPet().getPetDamage();
			 attacker.getPet().setPetEnergy(attacker.getPet().getPetEnergy()-1);
		 }
		 else if(r.trim().equals("3") && attacker.getPet().getPetEnergy() > 1) {
				 style = "Charge Attack";
				 b = true;
				 bonusDamage = (int)(attacker.getPet().getPetDamage()*3/2);
				 attacker.getPet().setPetEnergy(attacker.getPet().getPetEnergy()-2);
	     }
		 else {
			if(attacker.getPet().getPetEnergy() > 1) {
			System.out.println("Wrong input.  Please write a number between 1 and 3 \n");
			}else {
		    System.out.println("Wrong input.  Please write a number between 1 and 2 \n");	
			}
	     }
    	
    	}
        if (attacker.getHealth() == 0) {
            return;
        }
        double damage = attacker.getDamage();
        double critCalc = random.nextDouble();
        if (critCalc < attacker.getCritChance()) {
            damage += damage;
            QueueProvider.offer("Crit hit! Damage has been doubled!");
        }
        int healthReduction = (int) ((((4 * attacker.getLevel() / 50 + 2) *
                damage * damage / (defender.getArmour() + 1)/ 100) + 2) *
                (random.nextDouble() + 1)) + bonusDamage;
       
        
        if (defender.getHealth() < 0) {
            defender.setHealth(0);
        }
        
               
        QueueProvider.offer("Total " + healthReduction + " damage dealt! by " + style);
        if (attacker instanceof Player) {
            QueueProvider.offer("The " + defender.getName() + "'s health is " +
                    defender.getHealth());
        } else {
        	
            QueueProvider.offer("Your health is " + defender.getHealth());
           
        }
    }
    //attack with pet ends
    

    private void attack(Entity attacker, Entity defender) {
        if (attacker.getHealth() == 0) {
            return;
        }
        double damage = attacker.getDamage();
        double critCalc = random.nextDouble();
        if (critCalc < attacker.getCritChance()) {
            damage += damage;
            QueueProvider.offer("Crit hit! Damage has been doubled!");
        }
        int healthReduction;
        if (attacker instanceof Monster  && counter > 3) {
        	QueueProvider.offer("You weakened!");
        healthReduction = (int) ((((3 * attacker.getLevel() / 50 + 2) *
                damage * damage / (defender.getArmour() + 1)/ 100) + 2) *
                (random.nextDouble() + 1))*(1+((counter-3)*2/10));
        }
        else {
        	healthReduction = (int) ((((3 * attacker.getLevel() / 50 + 2) *
                    damage * damage / (defender.getArmour() + 1)/ 100) + 2) *
                    (random.nextDouble() + 1));
        }
        
        
        int fromPlayer=0;
		int fromPet=0;
        boolean damageToPet=false;
        if(defender instanceof Player) {
       	 if(defender.getPet().getHasPet()) {
       		 int randomPercentage =(int)(Math.random()*4);
       		
       		
       		 switch (randomPercentage) {
       		 case 0:{
       			 double tempHealthReduction = 0;
       			 tempHealthReduction = healthReduction;
       			 fromPlayer = (int)(tempHealthReduction*90/100);
       			 fromPet = (int)(tempHealthReduction*10/100);        			 
       			 break;
       		 }
       		 case 1:{
       			 double tempHealthReduction = 0;
       			 tempHealthReduction = healthReduction;
       			 fromPlayer = (int)(tempHealthReduction*85/100);
       			 fromPet = (int)(tempHealthReduction*15/100);        			 
       			 break;
       		 }
       		 case 2:{
       			 double tempHealthReduction = 0;
       			 tempHealthReduction = healthReduction;
       			 fromPlayer = (int)(tempHealthReduction*80/100);
       			 fromPet = (int)(tempHealthReduction*20/100);        			 
       			 break;
       		 }
       		 case 3:{
       			 double tempHealthReduction = 0;
       			 tempHealthReduction = healthReduction;
       			 fromPlayer = (int)(tempHealthReduction*75/100);
       			 fromPet = (int)(tempHealthReduction*25/100);        			 
       			 break;
       		 }
       		 default:
       			 fromPlayer = healthReduction;
       	         break;
       		 }//switch ends
       		 defender.setHealth((defender.getHealth() - fromPlayer));
       		 defender.getPet().setPetHealth((defender.getPet().getPetHealth() - fromPet));
       		 damageToPet = true;
       	 }else {// has not have a pet
       	  defender.setHealth((defender.getHealth() - healthReduction));
       	  damageToPet = false;
       	 }
        }else {
       	  defender.setHealth((defender.getHealth() - healthReduction));
        }

        //defender.setHealth((defender.getHealth() - healthReduction));
                       
        if (defender.getHealth() < 0) {
            defender.setHealth(0);
        }
      
        QueueProvider.offer(healthReduction + " damage dealt!");
        
        //when pet Dies
        if (defender instanceof Player) {        	
        	if(defender.getPet().getPetHealth()<1) {
        		defender.getPet().setPetHealth(0);
        		if((fromPet >= 0) && damageToPet) {
        			QueueProvider.offer(defender.getPet().getPetName() + " is take "+ fromPet + " damage!");
        			QueueProvider.offer(defender.getPet().getPetName() + " is died! ");
        			QueueProvider.offer("you are take "+ fromPlayer + " damage!");
        		}        		
        	}else if(damageToPet) {
        		QueueProvider.offer(defender.getPet().getPetName() + " is take "+ fromPet + " damage!");
        		QueueProvider.offer("You are take "+ fromPlayer + " damage!");
        	}
        }       
        //when pet Dies       
       
        if (attacker instanceof Player) {
            QueueProvider.offer("The " + defender.getName() + "'s health is " +
                    defender.getHealth());
        } else {
            QueueProvider.offer("Your health is " + defender.getHealth());
            
            if(defender instanceof Player) {
            	if(defender.getPet().getHasPet()) {
                 QueueProvider.offer("Your pet's health is " + defender.getPet().getPetHealth());
            	}
            }
           
        }
    }

    private void mutateStats(double damageMult, double armourMult) {
        armour = player.getArmour();
        damage = player.getDamage();
        player.setArmour((int) (armour * armourMult));
        player.setDamage(damage * damageMult);
    }

    private void resetStats() {
        player.setArmour(armour);
        player.setDamage(damage);
    }

    private void equip() {
        player.printStorage();
        QueueProvider.offer("What item do you want to use?");
        String itemName = QueueProvider.take();
        if (!itemName.equalsIgnoreCase("back")) {
            player.equipItem(itemName);
        }
    }

    private void unequip() {
        player.printEquipment();
        QueueProvider.offer("What item do you want to unequip?");
        String itemName = QueueProvider.take();
        if (!itemName.equalsIgnoreCase("back")) {
            player.dequipItem(itemName);
        }
    }

    private void viewStats() {
        QueueProvider.offer("\nWhat is your command? ex. View stats(vs), " +
                "View Backpack(vb), View Equipment(ve) , View petstats(ps)");
        String input = QueueProvider.take();
        switch (input) {
            case "vs":
            case "viewstats":
                player.getStats();
                break;
            case "ve":
            case "viewequipped":
                player.printEquipment();
                break;
            case "vb":
            case "viewbackpack":
                player.printStorage();
                break;
            case "back":
            case "exit":
                break;
            case "petstats":
            case "ps":
            	player.printPetStats();
            	break;
            default:
                viewStats();
                break;
        }
    }
}
