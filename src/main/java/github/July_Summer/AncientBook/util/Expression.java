package github.July_Summer.AncientBook.util;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;

import github.July_Summer.AncientBook.attribute.AttributeManager;

public class Expression {

    public static double resolveExpression(String expression, Boolean isMath)
    {
        if(expression == null)
            return 0.0;
            
        if(isMath)
        {
            try {
                return Expression.operation(Expression.sweap(expression));
            } catch (EmptyStackException e) {
                return 0.0;
            }
        } 
        else 
        {
            if(!expression.contains("-"))
                try {
                    return Expression.operation(Expression.sweap(expression));
                } catch (EmptyStackException e) {
                    return 0.0;
                }
            else {
                String[] str = expression.split("-");
                Boolean flag = false;
                for(int i = 0; i < str.length; i++)
                {
                    if(flag && AttributeManager.isDouble(str[i])) {
                        return AttributeManager.random(Double.parseDouble(str[i-1]), Double.parseDouble(str[i]));
                    }
                    if(AttributeManager.isDouble(str[i])) {
                        flag = true;
                    }
                }
            }
        }
        return 0.0;
    }
    
    public static String getExpression(String lore)
    {
        lore = lore.replace("+", "").replace(" ", "");
        Matcher matcher = ConfigUtil.pattern.matcher(lore);
        StringBuilder str = new StringBuilder();
        while(matcher.find())
        {
            str.append(matcher.group());
        }
        if(str.length() == 0)
        {
            return null;
        }
        return str.toString();
    }
    
    /**
     * 版权声明：本方法为CSDN博主「lunaticCode1」的原创代码，遵循CC 4.0 
     */
     public static float operation(List<String> list) 
     {
            Stack<Float> stack = new Stack<>();
            float result = 0;

            int len = list.size();
            for (int i = 0; i < len; i++) 
            {
                String ch = list.get(i);
                try {
                    float f = Float.parseFloat(ch);
                    stack.push(f);
                } catch (Exception e) 
                {
                    result = count(ch, stack.pop(), stack.pop());
                    stack.push(result);
                }
            }
            return stack.pop();
        }

    /**
     * 版权声明：本方法为CSDN博主「lunaticCode1」的原创代码，遵循CC 4.0 
     */
     public static float count(String eq, float eq1, float eq2) 
     {
            if (eq.equals("+")) 
                return eq2 + eq1;
            
            if (eq.equals("-")) 
                return eq2 - eq1;

            if (eq.equals("*")) 
                return eq2 * eq1;

            if (eq.equals("/")) 
                return eq2 * eq1;
            
        return 0;
      }
     
     /**
      * 版权声明：本方法为CSDN博主「lunaticCode1」的原创代码，遵循CC 4.0 
      */
     public static List<String> sweap(String equation) 
     {
            Stack<Character> stack = new Stack<>();
            equation = equation + "?";
            char[] equ = equation.toCharArray();
            List<String> list = new ArrayList<>();
            int len = equ.length;
            String out = "";
            for (int i = 0; i < len; i++) 
            {
                if (equ[i] == '(') 
                {
                    stack.push('(');
                    continue;
                }
                 if (equ[i] >='0' && equ[i] <='9')
                 {
                    out += equ[i];

                    while (equ[i + 1] >= '0' && equ[i + 1] <= '9'|| equ[i + 1] == '.') 
                    {
                        out += equ[i + 1];
                        i++;
                    }
                    list.add(out);
                    out = "";
                    continue;
                }

                if (equ[i] == '+' || equ[i] == '-') 
                {
                    while (!stack.empty() && stack.peek() != '(') 
                    {
                        list.add(stack.pop().toString());
                    }
                    stack.push(equ[i]);
                    continue;
                }

                if (equ[i] == '*' || equ[i] == '/')
                {
                    while (!stack.empty() && (stack.peek() == '*' || stack.peek() == '/')) 
                    {
                        list.add(stack.pop().toString());
                    }
                    stack.push(equ[i]);
                    continue;
                }

                if (equ[i] == ')') 
                {
                    while (!stack.empty() && stack.peek() != '(')
                    {
                        list.add(stack.pop().toString());
                    }
                    stack.pop();
                    continue;
                }

            }

            while (!stack.empty()) 
            {
                list.add(stack.pop().toString());
            }
            
            return list;
        }
    
}
