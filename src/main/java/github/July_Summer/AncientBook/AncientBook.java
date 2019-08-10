package github.July_Summer.AncientBook;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

import edu.umd.cs.findbugs.annotations.Nullable;
import github.July_Summer.AncientBook.attribute.AttributeManager;
import github.July_Summer.AncientBook.attribute.BookAttribute;
import github.July_Summer.AncientBook.listener.AttributeListener;
import github.July_Summer.AncientBook.thread.ResolveCallBack;
import github.July_Summer.AncientBook.util.ConfigUtil;
import github.July_Summer.AncientBook.util.InventoryUtil;
import github.July_Summer.AncientBook.util.LoreUtil;
import me.rojo8399.placeholderapi.Placeholder;
import me.rojo8399.placeholderapi.PlaceholderService;
import me.rojo8399.placeholderapi.Source;
import me.rojo8399.placeholderapi.Token;

/*
 * @author July_Summer
 */
@SuppressWarnings("all")
@Plugin(id = "ancientbook", name = "AncientBook", version = "1.0.0-API7", description = "Lore Attributes", dependencies = { @Dependency(id = "placeholderapi") })
public class AncientBook{
    
    public static AncientBook plugin;
    public static ExecutorService es = null;
    public PlaceholderService placeholderService = null;
    public static HashMap<String, String> keyMap = Maps.newHashMap();
    public static HashMap<Integer, Object> task = Maps.newHashMap();
    public static HashMap<Integer, Future> taskManger = Maps.newHashMap();
    public static Logger log =  LoggerFactory.getLogger("AncientBook");

    @Inject
    private void setConfigDir(@ConfigDir(sharedRoot = false) Path configDir) 
    {
        ConfigUtil.configDir = configDir;
        ConfigUtil.config = new File(ConfigUtil.configDir + "\\config.json");
    }
    
    @Listener
    public void onGameIniti(GameInitializationEvent event) 
    {
        plugin = this;
        boolean enable = ConfigUtil.initConfig();
        
        if(enable)
        {
            Sponge.getCommandManager().register(this, new PluginCommands(), "tob");
            Sponge.getEventManager().registerListeners(this, new AttributeListener());

            es = Executors.newFixedThreadPool(ConfigUtil.threads);

            for(int i = 0; i < ConfigUtil.threads; i++)
                es.execute( () ->  { 
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
         
            keyMap = AttributeManager.getKeyMap();
        }
    }
    
    @Listener
    public void onGameStop(GameStoppingEvent event)
    {
        if(es != null)
            es.shutdown();
    }

    @Listener
    public void onStart(GameStartingServerEvent event)
    {
        placeholderService = Sponge.getServiceManager().provideUnchecked(PlaceholderService.class);
        
        this.placeholderService.loadAll(this, this).stream() .map(builder -> builder.author("July_Summer").version("1.0.0-API7").plugin(this)).forEach(builder -> {
            try {
                builder.buildAndRegister(); 
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
        
    /**
     * hook Placeholder
     */
    @Placeholder(id = "ancientbook")
    public String onPlaceholderStats(@Source Player src, @Nullable @Token(fix = true) String token)
    {
        if(keyMap.containsKey(token))
        {
            ResolveCallBack scb = ResolveCallBack.builder().flag(false).taskId(AttributeManager.getNewTaskId());
            
            AttributeManager.addResolveOneThread(scb, token, LoreUtil.getItemLore(InventoryUtil.getMainInHand(src)));
            
            if(AttributeManager.getCallBacks(scb))
            {
                return scb.vaule.toString();
            }
        }
        
        return "";
    }
    
    public static void log(String msg)
    {
        log.info(msg);
    }
       
}

