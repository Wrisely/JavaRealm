package trial2dgame;

import entity.Entity;
import object.OBJ_Chest;
import object.OBJ_Coin;
import object.OBJ_Deep_Breath_Book;
import object.OBJ_Diary;
import object.OBJ_Door;
import object.OBJ_Door_Purple;
import object.OBJ_Equation_Of_Ruin;
import object.OBJ_Exponential_Book;
import object.OBJ_Formula_Book;
import object.OBJ_Fractal_Book;
import object.OBJ_Key;
import object.OBJ_Key_Purple;
import object.OBJ_Lantern;
import object.OBJ_Multiplication_Book;
import object.OBJ_Mysterious_Staff;
import object.OBJ_Old_Staff;
import object.OBJ_Potion_Red;
import object.OBJ_Prime_Book;
import object.OBJ_Scroll_Violet;
import object.OBJ_Shield_Blue;
import object.OBJ_Shield_Wood;
import object.OBJ_Wooden_Staff;

public class EntityGenerator {

	GamePanel gp;
	
	public EntityGenerator(GamePanel gp) {
		this.gp = gp;
	}
	
	public Entity getObject(String itemName){
		
		Entity obj = null;
		
		switch(itemName) {
        case OBJ_Chest.objName: obj = new OBJ_Chest(gp); break;
        case OBJ_Coin.objName: obj = new OBJ_Coin(gp); break;
        case OBJ_Deep_Breath_Book.objName: obj = new OBJ_Deep_Breath_Book(gp); break;
        case OBJ_Diary.objName: obj = new OBJ_Diary(gp); break;
        case OBJ_Door.objName: obj = new OBJ_Door(gp); break;
        case OBJ_Door_Purple.objName: obj = new OBJ_Door_Purple(gp); break;
        case OBJ_Equation_Of_Ruin.objName: obj = new OBJ_Equation_Of_Ruin(gp); break;
        case OBJ_Exponential_Book.objName: obj = new OBJ_Exponential_Book(gp); break;
        case OBJ_Formula_Book.objName: obj = new OBJ_Formula_Book(gp); break;
        case OBJ_Fractal_Book.objName: obj = new OBJ_Fractal_Book(gp); break;
        case OBJ_Key.objName: obj = new OBJ_Key(gp); break;
        case OBJ_Key_Purple.objName: obj = new OBJ_Key_Purple(gp); break;
        case OBJ_Lantern.objName: obj = new OBJ_Lantern(gp); break;
        case OBJ_Multiplication_Book.objName: obj = new OBJ_Multiplication_Book(gp); break;
        case OBJ_Mysterious_Staff.objName: obj = new OBJ_Mysterious_Staff(gp); break;
        case OBJ_Old_Staff.objName: obj = new OBJ_Old_Staff(gp); break;
        case OBJ_Potion_Red.objName: obj = new OBJ_Potion_Red(gp); break;
        case OBJ_Prime_Book.objName: obj = new OBJ_Prime_Book(gp); break;
        case OBJ_Scroll_Violet.objName: obj = new OBJ_Scroll_Violet(gp); break;
        case OBJ_Shield_Blue.objName: obj = new OBJ_Shield_Blue(gp); break;
        case OBJ_Shield_Wood.objName: obj = new OBJ_Shield_Wood(gp); break;
        case OBJ_Wooden_Staff.objName: obj = new OBJ_Wooden_Staff(gp); break;
        default: System.out.println("Unknown item: " + itemName); break;

		}
		return obj;
	}
	
}
