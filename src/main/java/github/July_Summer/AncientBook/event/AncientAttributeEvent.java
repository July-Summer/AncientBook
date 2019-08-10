package github.July_Summer.AncientBook.event;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

import github.July_Summer.AncientBook.attribute.BookAttribute;

public class AncientAttributeEvent extends AbstractEvent implements Cancellable{

    public enum EntityType 
    {
        damager,entity
    };
    
    private boolean cancel = false;
    private Entity damager;
    private Entity entity;
    private BookAttribute damgerAttribute;
    private BookAttribute entityAttribute;
    
    public AncientAttributeEvent(Entity damager, Entity entity, BookAttribute damgerAttribute, BookAttribute entityAttribute)
    {
        this.damager = damager;
        this.entity = entity;
        this.damgerAttribute = damgerAttribute;
        this.entityAttribute = damgerAttribute;
    }
    /**
     * 生物是否为空 也可以理解为是否为玩家
     */
    public boolean isEmpty(EntityType type)
    {
        return type == EntityType.damager ? damgerAttribute.empty : entityAttribute.empty;
    }
    
    /**
     * 获取属性
     */
    public BookAttribute getAttribute(EntityType type)
    {
        return type == EntityType.damager ? damgerAttribute : entityAttribute;
    }
    
    /**
     * 获取攻击者
     */
    public Entity getDamager()
    {
        return damager;
    }
    
    /**
     * 获取被攻击者
     */
    public Entity getEntity()
    {
        return entity;
    }

    @Override
    public boolean isCancelled() 
    {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) 
    {
        this.cancel = cancel;
    }

    @Override
    public Cause getCause() 
    {
        return null;
    }

}