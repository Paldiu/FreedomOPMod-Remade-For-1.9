package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.ArrayList;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.BOTH)
@CommandParameters(
        description = "Manipulate potion effects. Duration is measured in server ticks (~20 ticks per second).",
        usage = "/<command> <list | clear [target name] | add <type> <duration> <amplifier> [target name]>")
public class Command_potion extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 1 || args.length == 2)
        {
            if (args[0].equalsIgnoreCase("list"))
            {
                List<String> potionEffectTypeNames = new ArrayList<String>();
                for (PotionEffectType potion_effect_type : PotionEffectType.values())
                {
                    if (potion_effect_type != null)
                    {
                        potionEffectTypeNames.add(potion_effect_type.getName());
                    }
                }
                sender.sendMessage(ChatColor.AQUA + "Potion effect types: " + StringUtils.join(potionEffectTypeNames, ", "));
            }
            else if (args[0].equalsIgnoreCase("clearall"))
            {
                if (!(TFM_AdminList.isSuperAdmin(sender) || senderIsConsole))
                {
                    sender.sendMessage(ChatColor.RED + TotalFreedomMod.MSG_NO_PERMS);
                    return true;
                }
                TFM_Util.adminAction(sender.getName(), "Cleared all potion effects from all players", true);
                for (Player target : Bukkit.getOnlinePlayers())
                {
                    for (PotionEffect potion_effect : target.getActivePotionEffects())
                    {
                        target.removePotionEffect(potion_effect.getType());
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("clear"))
            {
                Player target = sender_p;

                if (args.length == 2)
                {
                    target = getPlayer(args[1]);

                    if (target == null)
                    {
                        sender.sendMessage(ChatColor.RED + TotalFreedomMod.PLAYER_NOT_FOUND);
                        return true;
                    }
                }

                if (!target.equals(sender_p))
                {
                    if (!TFM_AdminList.isSuperAdmin(sender))
                    {
                        sender.sendMessage(ChatColor.RED + "Only superadmins can clear potion effects from other players.");
                        return true;
                    }
                }
                else if (senderIsConsole)
                {
                    sender.sendMessage(ChatColor.GRAY + "You must specify a target player when using this command from the console.");
                    return true;
                }

                for (PotionEffect potion_effect : target.getActivePotionEffects())
                {
                    target.removePotionEffect(potion_effect.getType());
                }

                sender.sendMessage(ChatColor.AQUA + "Cleared all active potion effects " + (!target.equals(sender_p) ? "from player " + target.getName() + "." : "from yourself."));
            }
            else
            {
                return false;
            }
        }
        else if (args.length == 4 || args.length == 5)
        {
            if (args[0].equalsIgnoreCase("add"))
            {
                Player target = sender_p;

                if (args.length == 5)
                {

                    target = getPlayer(args[4]);

                    if (target == null)
                    {
                        sender.sendMessage(ChatColor.RED + TotalFreedomMod.PLAYER_NOT_FOUND);
                        return true;
                    }
                }

                if (!target.equals(sender_p))
                {
                    if (!TFM_AdminList.isSuperAdmin(sender))
                    {
                        sender.sendMessage("Only superadmins can apply potion effects to other players.");
                        return true;
                    }
                }
                else if (senderIsConsole)
                {
                    sender.sendMessage("You must specify a target player when using this command from the console.");
                    return true;
                }

                PotionEffectType potion_effect_type = PotionEffectType.getByName(args[1]);
                if (potion_effect_type == null)
                {
                    sender.sendMessage(ChatColor.AQUA + "Invalid potion effect type.");
                    return true;
                }

                int duration;
                try
                {
                    duration = Integer.parseInt(args[2]);
                }
                catch (NumberFormatException ex)
                {
                    sender.sendMessage(ChatColor.RED + "Invalid potion duration.");
                    return true;
                }

                int amplifier;
                try
                {
                    amplifier = Integer.parseInt(args[3]);
                }
                catch (NumberFormatException ex)
                {
                    sender.sendMessage(ChatColor.RED + "Invalid potion amplifier.");
                    return true;
                }

                PotionEffect new_effect = potion_effect_type.createEffect(duration, amplifier);
                target.addPotionEffect(new_effect, true);
                sender.sendMessage(ChatColor.AQUA
                        + "Added potion effect: " + new_effect.getType().getName()
                        + ", Duration: " + new_effect.getDuration()
                        + ", Amplifier: " + new_effect.getAmplifier()
                        + (!target.equals(sender_p) ? " to player " + target.getName() + "." : " to yourself."));

                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
        return true;
    }
}
