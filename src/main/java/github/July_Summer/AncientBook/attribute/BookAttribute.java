package github.July_Summer.AncientBook.attribute;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;

import github.July_Summer.AncientBook.util.ConfigUtil;

@SuppressWarnings("all")
public class BookAttribute {

    public double pstDamage = 0.0;
    public double armor = 0.0;
    public double sunderChance = 0.0;
    public double realDamage = 0.0;
    public double pvpDamage = 0.0;
    public double pveDamage = 0.0;
    public double shootDamage = 0.0;
    public double critChance = 0.0;
    public double critDamage = ConfigUtil.critDamage;
    public double healthChance = 0.0;
    public double healthSteal = ConfigUtil.healthSteal;
    public double dodgeChance = 0.0;
    public double hitChance = 0.0;
    public double killChance = 0.0;
    public double thornsDamage = 0.0;
    public boolean isCrit = false;
    public boolean isSunder = false;
    public boolean isHealth = false;
    public boolean isDodge = false;
    public boolean isHit = false;
    public boolean isKill = false;
    public EntityDamageSource source = null;
    public Entity entity = null;
    public boolean empty = false;

    public static BookAttribute builder() 
    {        
       return new BookAttribute();
    }
    
    public BookAttribute source(EntityDamageSource source) 
    {
        this.source = source;
        return this;
    }

    public BookAttribute empty(boolean empty)
    {
        this.empty  = empty;
        return this;
    }
    
    public BookAttribute entity(Entity entity)
    {
        this.entity = entity;
        return this;
    }
    
    public boolean isEmptyAttribute(double d)
    {
        return d == 0.0;
    }
    
    public BookAttribute check()
    {
        pstDamage = AttributeManager.checkMax(pstDamage, ConfigUtil.maxMap.get("pstDamage"));
        armor = AttributeManager.checkMax(armor, ConfigUtil.maxMap.get("armor"));
        sunderChance = AttributeManager.checkMax(sunderChance, ConfigUtil.maxMap.get("sunderChance"));
        realDamage = AttributeManager.checkMax(realDamage, ConfigUtil.maxMap.get("realDamage"));
        pvpDamage = AttributeManager.checkMax(pvpDamage, ConfigUtil.maxMap.get("pvpDamage"));
        pveDamage = AttributeManager.checkMax(pveDamage, ConfigUtil.maxMap.get("pveDamage"));
        shootDamage = AttributeManager.checkMax(shootDamage, ConfigUtil.maxMap.get("shootDamage"));
        critChance = AttributeManager.checkMax(critChance, ConfigUtil.maxMap.get("critChance"));
        critDamage = AttributeManager.checkMax(critDamage, ConfigUtil.maxMap.get("critDamage"));
        healthChance = AttributeManager.checkMax(healthChance, ConfigUtil.maxMap.get("healthChance"));
        healthSteal = AttributeManager.checkMax(healthSteal, ConfigUtil.maxMap.get("healthSteal"));
        dodgeChance = AttributeManager.checkMax(dodgeChance, ConfigUtil.maxMap.get("dodgeChance"));
        hitChance = AttributeManager.checkMax(hitChance, ConfigUtil.maxMap.get("hitChance"));
        killChance = AttributeManager.checkMax(killChance, ConfigUtil.maxMap.get("killChance"));
        thornsDamage = AttributeManager.checkMax(thornsDamage, ConfigUtil.maxMap.get("thornsDamage"));
        return this;
    }

}
