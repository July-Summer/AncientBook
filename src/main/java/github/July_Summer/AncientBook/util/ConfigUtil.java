package github.July_Summer.AncientBook.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.regex.Pattern;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import github.July_Summer.AncientBook.AncientBook;
import github.July_Summer.AncientBook.attribute.AttributeManager;
import github.July_Summer.AncientBook.attribute.BookAttributeType;

@SuppressWarnings("all")
public class ConfigUtil {

    public static Path configDir = null;
    public static File config = null;
    public static int threads = 1;
    public static Pattern pattern = null;
    public static Pattern special_pattern = null;
    public static Boolean math = false;
    public static Integer outTime = 1;
    public static double critDamage = 5;
    public static double healthSteal = 1;
    public static String displayEntity = new String();
    public static String displayDamager = new String();
    public static String[] actionBarMessage = new String[] {};
    public static boolean barEnable = true;
    public static String dodgeEntity = new String();
    public static String hitDamager = new String();
    public static String killDamager = new String();
    public static String killEntity = new String();
    public static String[] siteSetting = new String[] {};
    public static HashMap<String, Double> maxMap = Maps.newHashMap();
    
    public static boolean initConfig() 
    {
        if(!configDir.toFile().exists())
            configDir.toFile().mkdirs();
        
        if(!config.exists()) {
            try {
                config.createNewFile();
                addDefault();
                AncientBook.log("§aSuccessful create config.json to " + config.getPath());
            } catch (IOException e) {
                AncientBook.log("§cError IOException: create config.json to " + config.getPath());
                e.printStackTrace();
            }
        }
        try {
            JsonObject obj = getJson();
            JsonObject message = obj.getAsJsonObject("message");
            threads = obj.get("threads").getAsInt();
            critDamage = obj.get("crit-default-damage").getAsDouble();
            pattern = Pattern.compile(toString(obj.get("attribute-pattern").getAsString()));
            special_pattern = Pattern.compile(toString(obj.get("attribute-special-pattern").getAsString()));
            math = obj.get("attribute-math").getAsBoolean();
            outTime = obj.get("outTime").getAsInt();
            healthSteal = obj.get("heatlh-default-steal").getAsInt();
            displayDamager = message.get("chat-damager-display").getAsString().replace("&", "§");
            displayEntity = message.get("chat-entity-display").getAsString().replace("&", "§");
            actionBarMessage = message.get("bar-message").getAsString().replace("&", "§").split("\\|");
            barEnable = message.get("bar-message-enable").getAsBoolean();
            dodgeEntity = message.get("chat-entity-dodged").getAsString().replace("&", "§");
            hitDamager = message.get("chat-damager-hit").getAsString().replace("&", "§");
            killDamager = message.get("chat-damager-kill").getAsString().replace("&", "§");
            killEntity = message.get("chat-entity-kill").getAsString().replace("&", "§");
            siteSetting = obj.get("site-setting").getAsString().replace(" ", "").split("\\|");
            
            maxMap.clear();
            for(BookAttributeType attributeNode : BookAttributeType.values()) 
            {   
                if(!attributeNode.isSpecial())
                {
                    String key = to3LowerCase(attributeNode.getVaule());
                    maxMap.put(key, obj.get(key + "-max").getAsDouble());
                }
            }
            
            AttributeManager.loadAttribute();
            return true;
        }  
        catch (NullPointerException e) 
        {
            AncientBook.log("§cconfig配置读取错误, 插件无法正常加载! 以下是错误信息");
            e.printStackTrace();
            return false;
        }
    }
        
    public static void addDefault() 
    {
        JsonObject obj = new JsonObject();
        JsonObject vaule = new JsonObject();
        JsonObject message = new JsonObject();
    
        obj.addProperty("site-setting", "Hand | OffHand | Equipment");
        
        for(BookAttributeType attributeNode : BookAttributeType.values()) 
        {    
            if(!attributeNode.isSpecial())
            {
                obj.addProperty(to3LowerCase(attributeNode.getVaule()) + "-max", -1);
            }
            vaule.addProperty(to3LowerCase(attributeNode.getVaule()), attributeNode.getVaule());
        }
        obj.add("attribute", vaule);
         
        message.addProperty("chat-damager-display", "&7>>> &aYou attack &f%damage% damage &ato %entity%");
        message.addProperty("chat-entity-display", "&7>>> &cYou are getting &f%damage% damage &cfrom %damager%");
        message.addProperty("chat-damager-hit", "&7>>> &c%entity% dodged your attack!");
        message.addProperty("chat-entity-dodged", "&7>>> &aYou dodged %damager% attack!");
        message.addProperty("chat-damager-kill", "&7>>> &aYou one hit killed %entity% !");
        message.addProperty("chat-entity-kill", "&7>>> &cYou were killed by %damager% one attack!");
        message.addProperty("bar-message", "&4Crit! | &aSunder! | &6Health! | &cThorns! ");
        message.addProperty("bar-message-enable", true);
        obj.add("message", message);
               
        obj.addProperty("crit-default-damage", 5);
        obj.addProperty("heatlh-default-steal", 1);
               
        obj.addProperty("attribute-pattern", to16("([+-]?[0-9]+\\.?[0-9]+)").toString());
        obj.addProperty("attribute-special-pattern", to16("(?<=<)(.+?)(?=>)").toString());
        obj.addProperty("attribute-math", false);
        obj.addProperty("threads", 10);
        obj.addProperty("outTime", 50); 
       
        try {
            FileOutputStream out = new FileOutputStream(config);
            out.write(new JsonFormatTool().formatJson(obj.toString()).getBytes());
            out.close();
        } catch (IOException e) {
            AncientBook.log("§cError IOException: write JsonObject to config.json");
            e.printStackTrace();
        }
        
    }
    
    public static JsonObject getJson() 
    {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(config),"UTF-8"),50 * 1024 * 1024);
        } catch (UnsupportedEncodingException e) {
            AncientBook.log("§cError: read config.json");
            return null;
        } catch (FileNotFoundException e) {
            AncientBook.log("§cError: read config.json");
            return null;
        }
        
        JsonObject obj = (JsonObject) new JsonParser().parse(in);
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return obj;
    }
    
    public static String to3LowerCase(String str)
    {
       StringBuilder sb = new StringBuilder();
       
       for (int i = 0; i < str.length(); i++) 
       {
           char c = str.charAt(i);
           
           if(i < 3 && Character.isUpperCase(c))
               sb.append(Character.toLowerCase(c));
           else
               sb.append(c);
       }
       return sb.toString();
    }
    
    public static String toLowerCase(String str)
    {
       StringBuilder sb = new StringBuilder();
       
       for (int i = 0; i < str.length(); i++) 
       {
           sb.append(Character.toLowerCase(str.charAt(i)));
       }
       return sb.toString();
    }

    public static String to16(String str) 
    {
        StringBuilder strs = new StringBuilder();
        
        for (int i = 0; i < str.length(); i++) 
        {
            strs.append(Integer.toHexString(str.charAt(i)));
        }
        return strs.toString();
    }
    
    public static String toString(String str16) 
    {
        String str = "0123456789abcdef";
        
        char[] hexs = str16.toCharArray();
        byte[] bytes = new byte[str16.length() / 2];
        
        int n;
        for (int i = 0; i < bytes.length; i++) 
        {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

}
