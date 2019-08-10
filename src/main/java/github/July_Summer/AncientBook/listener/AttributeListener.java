package github.July_Summer.AncientBook.listener;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Text.Builder;
import org.spongepowered.api.text.chat.ChatTypes;
import github.July_Summer.AncientBook.attribute.AttributeManager;
import github.July_Summer.AncientBook.attribute.AttributeManager.SpecialAttribute;
import github.July_Summer.AncientBook.attribute.BookAttribute;
import github.July_Summer.AncientBook.event.AncientAttributeEvent;
import github.July_Summer.AncientBook.thread.ResolveCallBack;
import github.July_Summer.AncientBook.util.ConfigUtil;
import github.July_Summer.AncientBook.util.LoreUtil;

@SuppressWarnings("all")
public class AttributeListener {
    
    @Listener
    public void onDamageEntity(DamageEntityEvent event, @Root EntityDamageSource source) 
    {
    /*    String causeType = event.getCause().toString();
        if(!causeType.equalsIgnoreCase("attack") || !causeType.equalsIgnoreCase("arrow"))
            return; 
        AncientBook.log(causeType);
        */
        
        Entity damager = AttributeManager.toEntity(source.getSource());
        Entity entity = event.getTargetEntity();
        
        if(!(damager instanceof Player) && !(entity instanceof Player))
            return;
        
        BookAttribute damagerAttribute = AttributeManager.createEmpty(damager, event.getBaseDamage());
        BookAttribute entityAttribute = AttributeManager.createEmpty(entity, event.getBaseDamage());
     
        if(damager instanceof Player)
        {
            Player p = (Player)damager;
            
            damagerAttribute = BookAttribute.builder()
                    .empty(false)
                    .source(source)
                    .entity(damager);
            damagerAttribute.pstDamage = event.getBaseDamage();
            
            List<Text> loreList = new ArrayList<>();
            List<ItemStack> itemList = SpecialAttribute.getSparkSiteItems(p);
            itemList.forEach(item -> 
            {
                if(SpecialAttribute.haveUsePermission(p, item))
                    loreList.addAll(LoreUtil.getItemLore(item));
            });
          
            if(loreList.size() != 0)
            {
              ResolveCallBack scb = ResolveCallBack.builder().flag(false).taskId(AttributeManager.getNewTaskId());  
              AttributeManager.addThread(scb, damagerAttribute, loreList);
                
              if(!AttributeManager.getCallBacks(scb))
                  return;
            }
        }
        
        if(entity instanceof Player)
        {
            Player p = (Player) entity;
            
               entityAttribute = BookAttribute.builder()
                    .empty(false)
                    .source(source)
                    .entity(entity);
            entityAttribute.pstDamage = event.getBaseDamage();
            
            List<Text> loreList = new ArrayList<>();
            List<ItemStack> itemList = SpecialAttribute.getSparkSiteItems(p);
              itemList.forEach(item -> 
                {
                    if(SpecialAttribute.haveUsePermission(p, item))
                        loreList.addAll(LoreUtil.getItemLore(item));
                });
          
            if(loreList.size() != 0)
            {
              ResolveCallBack scb = ResolveCallBack.builder().flag(false).taskId(AttributeManager.getNewTaskId());  
              AttributeManager.addThread(scb, entityAttribute, loreList);
                
              if(!AttributeManager.getCallBacks(scb))
                  return;
            }
        }

        AncientAttributeEvent attributeEvent = new AncientAttributeEvent(damager, entity, damagerAttribute, entityAttribute);
           Sponge.getEventManager().post(attributeEvent);
           
           if(attributeEvent.isCancelled())
               return;
           
           double damage = 0.0;
           
           ResolveCallBack call = ResolveCallBack.builder().flag(false).taskId(AttributeManager.getNewTaskId()).builder();
           
           AttributeManager.addResolveThread(call, damagerAttribute, entityAttribute);
           
           if(!AttributeManager.getCallBacks(call)) 
               return;
           
           damage = (double) call.get(Double.class);
           
        event.setBaseDamage(damage);
        
        if(!damagerAttribute.empty)
        {
            Player p = (Player) damager;
            String entityName = AttributeManager.getEntityName(entity);
            
            if(!damagerAttribute.isHit)
            {
                p.sendMessage(Text.of(ConfigUtil.hitDamager.replace("%entity%", entityName)));
                event.setCancelled(true);
                return;
            }
            
            Builder bar = Text.builder();
        
            if(ConfigUtil.barEnable)
            {
                if(damagerAttribute.isCrit)
                    bar.append(Text.of(ConfigUtil.actionBarMessage[0])).append(Text.of(" "));
                if(damagerAttribute.isSunder && !entityAttribute.empty)
                    bar.append(Text.of(ConfigUtil.actionBarMessage[1])).append(Text.of(" "));

                if(damagerAttribute.isHealth)
                {
                    double health = AttributeManager.vauleOfHealth(p.get(Keys.MAX_HEALTH).get(), (p.get(Keys.HEALTH).get() + (damagerAttribute.healthSteal / 100 * damage)));
                    p.offer(Keys.HEALTH, health);
                    bar.append(Text.of(ConfigUtil.actionBarMessage[2])).append(Text.of(" "));
                }
                
                p.sendMessage(ChatTypes.ACTION_BAR, bar.build());
            }            
            p.sendMessage(Text.of(ConfigUtil.displayDamager.replace("%damage%", String.valueOf(damage)).replace("%entity%", entityName)));
            
            if(damagerAttribute.isKill)
            {
                entity.offer(Keys.HEALTH, 0.0);
                p.sendMessage(Text.of(ConfigUtil.killDamager.replace("%entity%", entityName)));
            }
            
        }
        
        
        if(!entityAttribute.empty)
        {
              Player p = (Player) entity;
            String damagerName = AttributeManager.getEntityName(damager);
            
            if(entityAttribute.isDodge)
            {
                p.sendMessage(Text.of(ConfigUtil.dodgeEntity.replace("%damager%", damagerName)));
                event.setCancelled(true);
                return;
            }
                        
            Builder bar = Text.builder();
        
            if(ConfigUtil.barEnable)
            {
                if(!entityAttribute.isEmptyAttribute(entityAttribute.thornsDamage))
                {
                    damager.damage(entityAttribute.thornsDamage / 100 * damage, DamageSource.builder().type(DamageTypes.CUSTOM).build());
                    bar.append(Text.of(ConfigUtil.actionBarMessage[3])).append(Text.of(" "));
                }
                
                p.sendMessage(ChatTypes.ACTION_BAR, bar.build());
            }
            
            if(damagerAttribute.isKill)
            {
                p.sendMessage(Text.of(ConfigUtil.killEntity.replace("%damager%", damagerName)));
            }
       
             p.sendMessage(Text.of(ConfigUtil.displayEntity.replace("%damage%", String.valueOf(damage)).replace("%damager%", damagerName)));
        }
      
    }
}
