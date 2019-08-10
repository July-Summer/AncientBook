package github.July_Summer.AncientBook.attribute;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.arrow.Arrow;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;

import github.July_Summer.AncientBook.AncientBook;
import github.July_Summer.AncientBook.thread.AttributeRunnable;
import github.July_Summer.AncientBook.thread.OneAttributeRunnable;
import github.July_Summer.AncientBook.thread.ResolveAttributeRunnable;
import github.July_Summer.AncientBook.thread.ResolveCallBack;
import github.July_Summer.AncientBook.util.ColorUtil;
import github.July_Summer.AncientBook.util.ConfigUtil;
import github.July_Summer.AncientBook.util.InventoryUtil;
import github.July_Summer.AncientBook.util.LoreUtil;

@SuppressWarnings("all")
public class AttributeManager {

    public static String pstDamage;
    public static String armor;
    public static String sunderChance;
    public static String realDamage;
    public static String pvpDamage;
    public static String pveDamage;
    public static String shootDamage;
    public static String critChance;
    public static String critDamage;
    public static String healthChance;
    public static String healthSteal;
    public static String dodgeChance;
    public static String hitChance;
    public static String killChance;
    public static String thornsDamage;
    
    public static void loadAttribute() 
    {
        JsonObject obj = ConfigUtil.getJson().getAsJsonObject("attribute");
        pstDamage = obj.get("pstDamage").getAsString();
        armor = obj.get("armor").getAsString();
        sunderChance = obj.get("sunderChance").getAsString();
        realDamage = obj.get("realDamage").getAsString();
        pvpDamage = obj.get("pvpDamage").getAsString();
        pveDamage = obj.get("pveDamage").getAsString();
        shootDamage = obj.get("shootDamage").getAsString();
        critChance = obj.get("critChance").getAsString();
        critDamage = obj.get("critDamage").getAsString();
        healthChance = obj.get("healthChance").getAsString();
        healthSteal =  obj.get("healthSteal").getAsString();
        dodgeChance = obj.get("dodgeChance").getAsString();
        hitChance = obj.get("hitChance").getAsString();
        killChance = obj.get("killChance").getAsString();
        thornsDamage = obj.get("thornsDamage").getAsString();
        SpecialAttribute.load(obj);
    }
        
    public static double resolve(BookAttribute damagerAttribute, BookAttribute entityAttribute)
    {
        double damage = 0.0;
        
        if(!damagerAttribute.empty && !entityAttribute.empty)
        {
            if(damagerAttribute.isSunder)
                entityAttribute.armor = 0.0;
            
            if(entityAttribute.isDodge)
            {
                if(damagerAttribute.isHit && damagerAttribute.hitChance > entityAttribute.dodgeChance)
                {
                    entityAttribute.isDodge = false;
                }
                else
                {
                    damage = 0.0;
                    damagerAttribute.isHit = false;
                }
            }
            
             damagerAttribute.isHit = true;
                
            damage = (damagerAttribute.pstDamage + damagerAttribute.pvpDamage);
            damage = (damage - ((entityAttribute.armor / 100.0) * damage)) + damagerAttribute.realDamage;
            double maxHealth = damagerAttribute.entity.get(Keys.MAX_HEALTH).get();
            
            if(damagerAttribute.source.getSource() instanceof Arrow)
                damage += damagerAttribute.shootDamage;
            if(damagerAttribute.isCrit)
                damage += damagerAttribute.pstDamage * (damagerAttribute.critDamage / 100);
            if(damagerAttribute.isKill && damage < maxHealth)
                damage += maxHealth - damage;
        }
        
        if(!damagerAttribute.empty && entityAttribute.empty)
        {
            damage = damagerAttribute.pstDamage;
                    
             damagerAttribute.isHit = true;
             
            damage = damagerAttribute.pstDamage + damagerAttribute.realDamage + damagerAttribute.pveDamage;
            
            double health = damagerAttribute.entity.get(Keys.HEALTH).get();
            
            if(damagerAttribute.source.getSource() instanceof Arrow)
                damage += damagerAttribute.shootDamage;
            if(damagerAttribute.isCrit)
                damage += damagerAttribute.pstDamage * damagerAttribute.critDamage;
            if(damagerAttribute.isKill)
                damage += health;
        }
        
        if(damagerAttribute.empty && !entityAttribute.empty)
        {
            damage = damagerAttribute.pstDamage;
            
            if(entityAttribute.isDodge)
                damage = 0.0;
            
            damage = (damage - ((entityAttribute.armor / 100.0) * damage));
        }
        
        damage = getTowDigits(damage);
        
        return damage > 0.0 ? damage : 0.0;
    }
    
    public static boolean getCallBacks(ResolveCallBack scb)
    {
        int time = 0;
        while(true) 
        {
            time++;
            
             if(scb.flag) 
              {
                   AncientBook.taskManger.remove(scb.taskId);
                return true;
              }
            
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) 
            {
                e.printStackTrace();
            }
                 
            if(time >= ConfigUtil.outTime && !scb.flag) 
            {
                AncientBook.taskManger.get(scb.taskId).cancel(true);
                AncientBook.taskManger.remove(scb.taskId);
                 AncientBook.log("§clore解析/计算超时, 请尝试设置outTime的值为: " + (ConfigUtil.outTime + 1));
                return false;
            }
        }
    }
    
    public static int getNewTaskId() 
    {
        return AncientBook.task.size() + 1;
    }
    
    public static void addThread(ResolveCallBack scb, BookAttribute bookAttribute, List<Text> lore) 
    {
        AncientBook.taskManger.put(scb.taskId, AncientBook.es.submit(new AttributeRunnable(scb, bookAttribute, lore)));
    }
    
    public static void addResolveThread(ResolveCallBack scb, BookAttribute damagerAttribute, BookAttribute entityAttribute) 
    {
        AncientBook.taskManger.put(scb.taskId, AncientBook.es.submit(new ResolveAttributeRunnable(scb, damagerAttribute, entityAttribute)));
    }
    
    public static void addResolveOneThread(ResolveCallBack scb, String key, List<Text> list) 
    {
        AncientBook.taskManger.put(scb.taskId, AncientBook.es.submit(new OneAttributeRunnable(scb, key, list)));
    }
    
    public static BookAttribute createEmpty(Entity entity, double d)
    {
        BookAttribute attribute = BookAttribute.builder().entity(entity).empty(true);
        attribute.pstDamage = d;
        return attribute;
    }
    
    public static double checkMax(double vaule, double max)
    {
        if(max != -1)
           vaule = vaule > max ? max : vaule;
           
        return vaule;
    }
    
    public static boolean isChance(Double chance)
    {
        return random(0.0, 100.0) < chance;
    }
    
    public static double getTowDigits(Double number)
    {
        return Double.parseDouble(new DecimalFormat(".##").format(number));
    }
    
    public static double random(Double min, Double max)
    {
        return getTowDigits((double)(min + Math.random() * (max - min + 1)));
    }
    
    public static boolean isDouble(String str)
    {
        return ConfigUtil.pattern.matcher(str).matches();
    }
    
    public static Entity toEntity(Entity entity) 
    {
        return (entity instanceof Arrow) ? (Entity) ((Arrow)entity).getShooter() : entity;
    }
    
    public static double vauleOfHealth(double maxHealth, double health)
    {
        return maxHealth <= health ? maxHealth : health; 
    }
    
    public static String getEntityName(Entity entity)
    {
        if(entity instanceof Player)
            return ((Player)entity).getName();
        
        Optional<Text> disyplayName = entity.get(Keys.DISPLAY_NAME);
        return disyplayName.isPresent() ? disyplayName.get().toPlain() : entity.getType().getName();
    }
    
    public static HashMap<String, String> getKeyMap()
    {
        HashMap<String, String> map = Maps.newHashMap();
        Field[] fields = AttributeManager.class.getDeclaredFields();
        for(Field field : fields)
        {
            if(field.getType().getName().toString().equals("java.lang.String"))
                try {
                    map.put(ConfigUtil.toLowerCase(field.getName()), field.get(null).toString());
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
        }
        
        return map;
    }
    
    public static HashMap<String, Double> vauleOfMap(BookAttribute attribute)
    {
        HashMap<String, Double> map = Maps.newHashMap();
        Field[] fields = attribute.getClass().getDeclaredFields();
        for(Field field : fields)
        {
            if(field.getType().getName().toString().equals("double"))
                try {
                    map.put(field.getName(), Double.parseDouble(field.get(attribute).toString()));
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
        }
        
        return map;
    }
    
public static class SpecialAttribute{
        
        public static String sparkSite;
        public static String usePermission;
        
        public static void load(JsonObject obj)
        {
            sparkSite = obj.get("sparkSite").getAsString();
            usePermission = obj.get("usePermission").getAsString();
        }

        public static boolean haveUsePermission(Player p, ItemStack item)
        {
            List<String> usePermissions = getUsePermission(item);
            for(String permission : usePermissions)
            {
                if(!p.hasPermission(permission))
                    return false;

            }
            
            return true;
        }
        
        public static List<String> getUsePermission(ItemStack item)
        {
            List<Text> loreList = LoreUtil.getItemLore(item);
            List<String> usePermissions = new ArrayList<>();
            for(Text text : loreList) 
            {
                String lore = ColorUtil.stripColor((text.toPlain())).replace(" ", "");
                String str = getExpression(lore);
                if(lore.contains(usePermission) && str != null)
                {
                    if(!str.contains(","))
                    {
                        usePermissions.add(str);
                    } 
                    else 
                    {
                        String[] strSplit = str.split(",");
                        for(String split : strSplit)
                        {
                            usePermissions.add(split);
                        }
                    }
                }
            }
            
            return usePermissions;
        }
 
        
        public static List<ItemStack> getSparkSiteItems(Player p)
        {
            List<ItemStack> list = new ArrayList<>();
            ItemStack mainHand = InventoryUtil.getMainInHand(p);
            ItemStack offHand = InventoryUtil.getOffInHand(p);
            List<ItemStack> equipment = InventoryUtil.getEquipment(p);
            
            if(mainHand != null && isAtSite(mainHand, ConfigUtil.siteSetting[0]))
                list.add(mainHand);
            if(offHand != null && isAtSite(offHand, ConfigUtil.siteSetting[1]))
                list.add(offHand);
            
            equipment.forEach(item -> {
                if(isAtSite(item, ConfigUtil.siteSetting[2]))
                    list.add(item);
            });
            
            return list;
        }
        
        public static boolean isAtSite(ItemStack item, String site)
        {
            List<Text> loreList = LoreUtil.getItemLore(item);
            for(Text text : loreList) 
            {
                String lore = ColorUtil.stripColor((text.toPlain())).replace(" ", "");
                String str = getExpression(lore);
                if(lore.contains(sparkSite) && str != null)
                {
                    if(!str.contains(","))
                    {
                        if(str.equals(site))
                        {
                            return true;
                        }
                    } 
                    else 
                    {
                        String[] strSplit = str.split(",");
                        for(String split : strSplit)
                        {
                            if(split.equals(site))
                            {
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }
        
        public static String getExpression(String lore)
        {
            Matcher matcher = ConfigUtil.special_pattern.matcher(lore);
            String str = null;
            if(matcher.find())
            {
                str = matcher.group();
            }
            
            if(str == null)
            {
                return null;
            }
            
            return str;
        }
        
    }
    
}
