package github.July_Summer.AncientBook.thread;

@SuppressWarnings("rawtypes")
public class ResolveCallBack<T> {

    public boolean flag;
    public int taskId;
    public T vaule;
    
    public static ResolveCallBack builder()
    {
        return new ResolveCallBack();
    }
    
    public void set(T vaule)
    {
        this.vaule = vaule;
    }
    
    //这个T完全没有什么屁用
    public T get(Class<T> tClass)
    {
        return tClass.cast(vaule);
    }
    
    public ResolveCallBack<T> setFlag(boolean flag)
    {
        this.flag = flag;
        return this;
    }
    
    public ResolveCallBack<T> taskId(int taskId)
    {
        this.taskId = taskId;
        return this;
    }
    
    public ResolveCallBack flag(boolean flag)
    {
        this.flag = flag;
        return this;
    }
    
}
