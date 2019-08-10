package github.July_Summer.AncientBook.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;

public class InventoryUtil {

    public static List<ItemStack> getEquipmentAndHand(Player p)
    {
         List<ItemStack> items = new ArrayList<>();
         ItemStack mainHand = getMainInHand(p);
         ItemStack offHand = getOffInHand(p);
         List<ItemStack> equipment = getEquipment(p);
         if(mainHand != null)
             items.add(mainHand);
         if(offHand != null)
             items.add(offHand);
         if(equipment.size() != 0)
             equipment.forEach(item -> items.add(item));
         
         return items;
    }
    
    public static ItemStack getMainInHand(Player p) 
    {
        Optional<ItemStack> opt = p.getItemInHand(HandTypes.MAIN_HAND);
        return opt.isPresent() ? opt.get() : null;
    }
    
    public static ItemStack getOffInHand(Player p)
    {
        Optional<ItemStack> opt = p.getItemInHand(HandTypes.OFF_HAND);
        return opt.isPresent() ? opt.get() : null;
    }
    
    public static List<ItemStack> getEquipment(Player p)
    {
        PlayerInventory inv = (PlayerInventory) p.getInventory();
        ArmorEquipable equipment = inv.getEquipment().getCarrier().get();
        List<ItemStack> item = new ArrayList<>();
        Optional<ItemStack> boots = equipment.getBoots();
        Optional<ItemStack> chestPlate = equipment.getChestplate();
        Optional<ItemStack> headWear = equipment.getHelmet();
        Optional<ItemStack> leggings = equipment.getLeggings();
        
        if(boots.isPresent())
            item.add(boots.get());
        if(chestPlate.isPresent())
            item.add(chestPlate.get());
        if(headWear.isPresent())
            item.add(headWear.get());
        if(leggings.isPresent())
            item.add(leggings.get());

        return item;
    }
    
}
