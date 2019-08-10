package github.July_Summer.AncientBook.thread;

import github.July_Summer.AncientBook.attribute.AttributeManager;
import github.July_Summer.AncientBook.attribute.BookAttribute;

@SuppressWarnings("all")
public class ResolveAttributeRunnable implements Runnable{

    private BookAttribute damagerAttribute = null;
    private BookAttribute entityAttribute = null;
    private ResolveCallBack callBack = null;
    
    public ResolveAttributeRunnable(ResolveCallBack callBack, BookAttribute damagerAttribute, BookAttribute entityAttribute )
    {
        this.callBack = callBack;
        this.damagerAttribute = damagerAttribute;
        this.entityAttribute = entityAttribute;
    }
    
    @Override
    public void run() 
    {
        double damage = AttributeManager.resolve(damagerAttribute, entityAttribute);
        this.callBack.set(damage);
        this.callBack.setFlag(true);
    }

}
