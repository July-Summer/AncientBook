package github.July_Summer.AncientBook.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

public class LoreUtil {

    public static List<Text> getItemLore(ItemStack item)
    {
        if(item == null)
            return new ArrayList<Text>();
        
        Optional<List<Text>> loreList = item.get(Keys.ITEM_LORE);
        return loreList.isPresent() ? loreList.get() : new ArrayList<Text>();
    }
    
    public static boolean addLore(Player p, Optional<ItemStack> opt, String lore)
    {
        if(!opt.isPresent())
            return false;
        ItemStack item = opt.get();
        List<Text> list = LoreUtil.getItemLore(item);
        list.add(Text.of(lore.replace("&", "¡ì")));
        item.offer(Keys.ITEM_LORE, list);
        p.setItemInHand(HandTypes.MAIN_HAND, item);
        return true;
    }
    
    public static boolean delLore(Player p, Optional<ItemStack> opt, String index) 
    {
        if(!opt.isPresent())
            return false;

        if(!isNumber(index))
            return false;
        
        ItemStack item = opt.get();
        List<Text> list = LoreUtil.getItemLore(item);
        
        if(Integer.parseInt(index) > list.size())
            return false;
        
        list.remove(Integer.parseInt(index) - 1);
        
        item.offer(Keys.ITEM_LORE, list);
        
        p.setItemInHand(HandTypes.MAIN_HAND, item);
        return true;
    }
    
    public static boolean isNumber(String str) 
    {
        return Pattern.compile("[0-9]").matcher(str).matches();
    }
}
