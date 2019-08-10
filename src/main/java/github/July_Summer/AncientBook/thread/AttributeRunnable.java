package github.July_Summer.AncientBook.thread;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.text.Text;

import github.July_Summer.AncientBook.attribute.AttributeManager;
import github.July_Summer.AncientBook.attribute.BookAttribute;
import github.July_Summer.AncientBook.util.ColorUtil;
import github.July_Summer.AncientBook.util.ConfigUtil;
import github.July_Summer.AncientBook.util.Expression;

@SuppressWarnings("all")
public class AttributeRunnable implements Runnable{

    public List<Text> loreList = new ArrayList<Text>();
    public ResolveCallBack callBack = null;
    public BookAttribute attribute = null;
    
    public AttributeRunnable(ResolveCallBack callBack, BookAttribute attribute, List<Text> loreList) 
    {
        this.callBack = callBack;
        this.loreList = loreList; 
        this.attribute = attribute;
    }
    
    public void run() 
    {
        Boolean isMath = ConfigUtil.math;
        for(int i = 0; i < loreList.size(); i++)
        {
         String lore = ColorUtil.stripColor(loreList.get(i).toPlain());
         
         if(lore.contains(AttributeManager.pstDamage)) 
             attribute.pstDamage += Expression.resolveExpression(Expression.getExpression(lore), isMath);
         if(lore.contains(AttributeManager.armor))
             attribute.armor += Expression.resolveExpression(Expression.getExpression(lore), isMath);
         if(lore.contains(AttributeManager.sunderChance))
             attribute.sunderChance += Expression.resolveExpression(Expression.getExpression(lore), isMath);
         if(lore.contains(AttributeManager.realDamage))
             attribute.realDamage += Expression.resolveExpression(Expression.getExpression(lore), isMath);
         if(lore.contains(AttributeManager.pvpDamage))
             attribute.pvpDamage += Expression.resolveExpression(Expression.getExpression(lore), isMath);
         if(lore.contains(AttributeManager.pveDamage))
             attribute.pveDamage += Expression.resolveExpression(Expression.getExpression(lore), isMath);
         if(lore.contains(AttributeManager.shootDamage))
             attribute.shootDamage += Expression.resolveExpression(Expression.getExpression(lore), isMath);
         if(lore.contains(AttributeManager.critChance))
             attribute.critChance += Expression.resolveExpression(Expression.getExpression(lore), isMath);
         if(lore.contains(AttributeManager.healthChance))
             attribute.healthChance += Expression.resolveExpression(Expression.getExpression(lore), isMath);
         if(lore.contains(AttributeManager.healthSteal))
             attribute.healthSteal += Expression.resolveExpression(Expression.getExpression(lore), isMath);
         if(lore.contains(AttributeManager.dodgeChance))
             attribute.dodgeChance += Expression.resolveExpression(Expression.getExpression(lore), isMath);
         if(lore.contains(AttributeManager.hitChance))
             attribute.hitChance += Expression.resolveExpression(Expression.getExpression(lore), isMath);
         if(lore.contains(AttributeManager.killChance))
             attribute.killChance += Expression.resolveExpression(Expression.getExpression(lore), isMath);
         if(lore.contains(AttributeManager.thornsDamage))
             attribute.thornsDamage += Expression.resolveExpression(Expression.getExpression(lore), isMath);
        }
        
        attribute.check();
        attribute.isCrit = AttributeManager.isChance(attribute.critChance);
        attribute.isDodge = AttributeManager.isChance(attribute.dodgeChance);
        attribute.isHealth = AttributeManager.isChance(attribute.healthChance);
        attribute.isHit = AttributeManager.isChance(attribute.hitChance);
        attribute.isKill = AttributeManager.isChance(attribute.killChance);
        attribute.isSunder = AttributeManager.isChance(attribute.sunderChance);

        callBack.setFlag(true);
    }

}
