package github.July_Summer.AncientBook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import github.July_Summer.AncientBook.attribute.AttributeManager;
import github.July_Summer.AncientBook.attribute.BookAttribute;
import github.July_Summer.AncientBook.thread.ResolveCallBack;
import github.July_Summer.AncientBook.util.ConfigUtil;
import github.July_Summer.AncientBook.util.InventoryUtil;
import github.July_Summer.AncientBook.util.LoreUtil;

@SuppressWarnings("all")
public class PluginCommands implements CommandCallable {

    public Optional<Text> getHelp(CommandSource source) 
    {
        return Optional.of(Text.of());
    }

    public Optional<Text> getShortDescription(CommandSource source) 
    {
        return Optional.of(Text.of());
    }

    public List<String> getSuggestions(CommandSource arg0, String arg1, Location<World> arg2) throws CommandException 
    {
        return new ArrayList<String>();
    }

    public Text getUsage(CommandSource source) 
    {
        return Text.of();
    }

    public CommandResult process(CommandSource source, String arguments) throws CommandException 
    {
        String[] args = arguments.split(" ");
        
        if(arguments.equals("") || arguments.startsWith("help")) 
        {
            source.sendMessage(Text.builder()
                       .color(TextColors.WHITE).append(Text.of("> tob stats - 解析手持物品属性标签"))
                       .build());
            if(source.hasPermission("ancientbook.admin.use")) 
            {
                 source.sendMessage(Text.builder()
                         .color(TextColors.WHITE).append(Text.of("> tob name <name> - 设置手中物品显示名"))
                         .build());
                 source.sendMessage(Text.builder()
                            .color(TextColors.WHITE).append(Text.of("> tob add <lore> - 添加lore到手中物品"))
                            .build());
                 source.sendMessage(Text.builder()
                            .color(TextColors.WHITE).append(Text.of("> tob del <index> - 删除指定行数lore"))
                            .build());
                 source.sendMessage(Text.builder()
                         .color(TextColors.WHITE).append(Text.of("> tob list - 显示所有属性节点"))
                         .build());
                 source.sendMessage(Text.builder()
                            .color(TextColors.WHITE).append(Text.of("> tob reload - 重载配置文件"))
                            .build());
            }
            return CommandResult.success();
        }
        
        if(args[0].equalsIgnoreCase("reload") && args.length == 1)
        {
            if(!source.hasPermission("ancientbook.admin.use")) 
            {
                source.sendMessage(Text.builder()
                        .color(TextColors.RED).append(Text.of("> 你没有权限执行该命令"))
                        .build());
                return CommandResult.empty();
            }
            
            source.sendMessage(Text.builder()
                    .color(TextColors.GREEN).append(Text.of("配置文件已重载"))
                    .build());
            ConfigUtil.initConfig();
            return CommandResult.success();
        }
        
        if(!(source instanceof Player)) 
        {
            source.sendMessage(Text.builder()
                    .color(TextColors.RED).append(Text.of("该命令只能由玩家来执行"))
                    .build());
            return CommandResult.empty();
        }
        
        Player p = (Player) source;
        
        //由于写之前没想到这茬 没写好对应方法 所以只好堆这了
        if(args[0].equalsIgnoreCase("stats") && args.length == 1) 
        {
            ItemStack hand = InventoryUtil.getMainInHand(p);
            if(hand == null)
            {
                source.sendMessage(Text.builder()
                        .color(TextColors.RED).append(Text.of("你的手中没有物品！"))
                        .build());
                return CommandResult.empty();
            }
            
            ResolveCallBack scb = ResolveCallBack.builder().flag(false).taskId(AttributeManager.getNewTaskId());
            
            BookAttribute damagerAttribute = BookAttribute.builder()
                    .empty(false)
                    .source(EntityDamageSource.builder().type(DamageTypes.ATTACK).entity(p).build())
                    .entity(p);
           
            AttributeManager.addThread(scb, damagerAttribute, LoreUtil.getItemLore(hand));
            
            damagerAttribute.pstDamage = 1.0;
            
            if(!AttributeManager.getCallBacks(scb))
            {
                source.sendMessage(Text.builder()
                        .color(TextColors.RED).append(Text.of("解析超时！"))
                        .build());
                
                return CommandResult.empty();
            }
            boolean isSend = false;
            HashMap<String, Double> map = AttributeManager.vauleOfMap(damagerAttribute);
            HashMap<String, String> keyMap = AncientBook.keyMap;
            
            source.sendMessage(Text.builder()
                    .color(TextColors.GREEN).append(Text.of("属性统计: "))
                    .build());
            
            for(Entry<String, Double> key : map.entrySet())
            {
               if(key.getValue() != 0.0)
               {
                    source.sendMessage(Text.builder().append(Text.of("  §7> §f"))
                            .append(Text.of(keyMap.get(ConfigUtil.toLowerCase(key.getKey())) + ": "))
                            .append(Text.of("§c" + map.get(key.getKey())))
                            .build());
                    isSend = true;
               }
            }
            
            if(!isSend)
            {
                source.sendMessage(Text.builder()
                        .append(Text.of("  §7> §f"))
                        .append(Text.of("无标签"))
                        .build()); 
                return CommandResult.success();
            }
            
            source.sendMessage(Text.of());
            
            ResolveCallBack scb1 = ResolveCallBack.builder().flag(false).taskId(AttributeManager.getNewTaskId());
            BookAttribute entityAttribute = AttributeManager.createEmpty(p, 1.0);
            AttributeManager.addResolveThread(scb1, damagerAttribute, entityAttribute);
            
            if(AttributeManager.getCallBacks(scb1))
            {
                source.sendMessage(Text.builder()
                         .append(Text.of("  §7> §f伤害计算值: §c"))
                         .append(Text.of(scb1.vaule.toString()))
                         .build()); 
            }
            
            return CommandResult.success();
        }
        
        if(args[0].equalsIgnoreCase("add") && arguments.contains("add ")) 
        {
            if(!source.hasPermission("ancientbook.admin.use")) 
            {
                source.sendMessage(Text.builder()
                        .color(TextColors.RED).append(Text.of("> 你没有权限执行该命令"))
                        .build());
                return CommandResult.empty();
            }
            
            Optional<ItemStack> opt = p.getItemInHand(HandTypes.MAIN_HAND);
            
            boolean addLore = LoreUtil.addLore(p, opt, arguments.split("add ")[1]);
            
            if(!addLore)
            {
                source.sendMessage(Text.builder().color(TextColors.RED).append(Text.of("> 添加失败")).build());
                return CommandResult.empty();
            }
            
            source.sendMessage(Text.builder().color(TextColors.GREEN).append(Text.of("lore已添加")).build());
            return CommandResult.success();
        }
        
        if(args[0].equalsIgnoreCase("list") && args.length == 1) 
        {
            if(!source.hasPermission("ancientbook.admin.use")) 
            {
                source.sendMessage(Text.builder()
                        .color(TextColors.RED).append(Text.of("> 你没有权限执行该命令"))
                        .build());
                return CommandResult.empty();
            }
            
            source.sendMessage(Text.builder()
                    .color(TextColors.GREEN).append(Text.of("KeyList: "))
                    .build());
            
            for(Entry<String, String> key : AncientBook.keyMap.entrySet())
            {
                source.sendMessage(Text.of("  §7> §f" + key.getKey()));
            }
            
            return CommandResult.success();
        }
        
          if(args[0].equalsIgnoreCase("name") && arguments.contains("name ")) 
            {
                if(!source.hasPermission("ancientbook.admin.use")) 
                {
                    source.sendMessage(Text.builder()
                            .color(TextColors.RED).append(Text.of("> 你没有权限执行该命令"))
                            .build());
                    return CommandResult.empty();
                }
                
                ItemStack hand = InventoryUtil.getMainInHand(p);
                
                if(hand == null)
                {
                    source.sendMessage(Text.builder()
                            .color(TextColors.RED).append(Text.of("你的手中没有物品！"))
                            .build());
                    return CommandResult.empty();
                }
                
                hand.offer(Keys.DISPLAY_NAME, Text.of(arguments.split("name ")[1].replace("&", "§")));
                p.setItemInHand(HandTypes.MAIN_HAND, hand);
                source.sendMessage(Text.builder()
                        .color(TextColors.GREEN).append(Text.of("显示名已设置"))
                        .build());
                return CommandResult.success();
            }
        
        if(args[0].equalsIgnoreCase("del") && args.length == 2) 
        {
            if(!source.hasPermission("ancientbook.admin.use")) 
            {
                source.sendMessage(Text.builder()
                        .color(TextColors.RED).append(Text.of("> 你没有权限执行该命令"))
                        .build());
                return CommandResult.empty();
            }
            
            Optional<ItemStack> opt = p.getItemInHand(HandTypes.MAIN_HAND);
            
            boolean delLore = LoreUtil.delLore(p, opt, args[1]);
            
            if(!delLore)
            {
                source.sendMessage(Text.builder().color(TextColors.RED).append(Text.of("> 删除失败")).build());
                return CommandResult.empty();
            }
            
            source.sendMessage(Text.builder().color(TextColors.GREEN).append(Text.of("lore已删除")).build());
            return CommandResult.success();
        }
        
        source.sendMessage(Text.builder().color(TextColors.RED).append(Text.of("> 并没有该命令 请输入/tob help查看命令帮助！")).build());
        return CommandResult.empty();
    }

    public boolean testPermission(CommandSource source) 
    {
        return true;
    }


}
