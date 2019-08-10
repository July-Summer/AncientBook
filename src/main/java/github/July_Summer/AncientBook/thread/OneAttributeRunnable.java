package github.July_Summer.AncientBook.thread;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.text.Text;

import github.July_Summer.AncientBook.AncientBook;
import github.July_Summer.AncientBook.util.ColorUtil;
import github.July_Summer.AncientBook.util.ConfigUtil;
import github.July_Summer.AncientBook.util.Expression;

public class OneAttributeRunnable implements Runnable{

    private ResolveCallBack scb;
    private String key = new String();
    private List<Text> loreList = new ArrayList<>();
    
    public OneAttributeRunnable(ResolveCallBack scb, String key, List<Text> loreList)
    {
        this.key = AncientBook.keyMap.get(key);
        this.loreList = loreList;
        this.scb = scb;
    }
    
    @Override
    public void run() 
    {        
        Boolean isMath = ConfigUtil.math;
        double vaule = 0.0;
        
        for(int i = 0; i < loreList.size(); i++)
        {  
            String lore = ColorUtil.stripColor(loreList.get(i).toPlain());
            AncientBook.log(key);
            if(lore.contains(key)) 
            {
                 vaule += Expression.resolveExpression(Expression.getExpression(lore), isMath);
            }
        }
        
        scb.set(String.valueOf(vaule));
        scb.setFlag(true);
    }

 
}
