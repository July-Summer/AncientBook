package github.July_Summer.AncientBook.util;


/**
 * 作者：The_Number_One 
         来源：CSDN 
          原文：https://blog.csdn.net/yanghaitaohhh/article/details/39672783 
 * @author  yanghaitao
 * @version  [版本号, 2014年9月29日]
 */
public class JsonFormatTool
{
    /**
     * 单位缩进字符串。
     */
    private static String SPACE = "   ";
    
    /**
     * 返回格式化JSON字符串。
     * 
     * @param json 未格式化的JSON字符串。
     * @return 格式化的JSON字符串。
     */
    public String formatJson(String json)
    {
        StringBuffer result = new StringBuffer();
        
        int length = json.length();
        int number = 0;
        char key = 0;
       
        for (int i = 0; i < length; i++)
        {
            key = json.charAt(i);
         
            //这东西能把文本内的括号也序列化了 所以正则采用十六进制进行储存
            if((key == '[') || (key == '{') )
            {
                if((i - 1 > 0) && (json.charAt(i - 1) == ':'))
                {
                    result.append('\n');
                    result.append(indent(number));
                }
                
                result.append(key);

                result.append('\n');
                
                number++;
                result.append(indent(number));
                
                continue;
            }
            
            if((key == ']') || (key == '}') )
            {
                result.append('\n');
                
                number--;
                result.append(indent(number));
                
                result.append(key);
                
                if(((i + 1) < length) && (json.charAt(i + 1) != ','))
                {
                    result.append('\n');
                }
                
                continue;
            }
            
            if((key == ','))
            {
                result.append(key);
                result.append('\n');
                result.append(indent(number));
                continue;
            }
            
            result.append(key);
        }
        
        return result.toString();
    }
    
    /**
     * 返回指定次数的缩进字符串。每一次缩进三个空格，即SPACE。
     * 
     * @param number 缩进次数。
     * @return 指定缩进次数的字符串。
     */
    private String indent(int number)
    {
        StringBuffer result = new StringBuffer();
        for(int i = 0; i < number; i++)
        {
            result.append(SPACE);
        }
        return result.toString();
    }
}
