package su.nightexpress.sunlight.module.items;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.config.Perms;

public class ItemsPerms {

    public static final PermissionTree MODULE  = Perms.detached("items");
    public static final PermissionTree COMMAND = MODULE.branch("command");
    public static final PermissionTree BYPASS  = MODULE.branch("bypass");

    public static final Permission COMMAND_ITEM_ROOT              = COMMAND.permission("item.root");
    public static final Permission COMMAND_ITEM_AMOUNT            = COMMAND.permission("item.amount");
    public static final Permission COMMAND_ITEM_DAMAGE            = COMMAND.permission("item.damage");
    public static final Permission COMMAND_ITEM_UNBREAKABLE       = COMMAND.permission("item.unbreakable");
    public static final Permission COMMAND_ITEM_ENCHANT           = COMMAND.permission("item.enchant");
    public static final Permission COMMAND_ITEM_ENCHANT_UNLIMITED = COMMAND.permission("item.enchant.unlimited");
    public static final Permission COMMAND_ITEM_DISENCHANT        = COMMAND.permission("item.disenchant");
    public static final Permission COMMAND_ITEM_GET               = COMMAND.permission("item.get");
    public static final Permission COMMAND_ITEM_GIVE              = COMMAND.permission("item.give");
    public static final Permission COMMAND_ITEM_MODEL             = COMMAND.permission("item.model");
    public static final Permission COMMAND_ITEM_NAME              = COMMAND.permission("item.name");
    public static final Permission COMMAND_ITEM_POTION            = COMMAND.permission("item.potion");
    public static final Permission COMMAND_ITEM_REPAIR            = COMMAND.permission("item.repair");
    public static final Permission COMMAND_ITEM_SPAWN             = COMMAND.permission("item.spawn");

    public static final Permission COMMAND_LORE_ROOT              = COMMAND.permission("lore.root");
    public static final Permission COMMAND_LORE_ADD               = COMMAND.permission("lore.add");
    public static final Permission COMMAND_LORE_INSERT            = COMMAND.permission("lore.insert");
    public static final Permission COMMAND_LORE_CLEAR             = COMMAND.permission("lore.clear");
    public static final Permission COMMAND_LORE_REMOVE            = COMMAND.permission("lore.remove");
    public static final Permission COMMAND_LORE_UNLIMITED         = COMMAND.permission("lore.unlimited");

    public static final Permission BYPASS_LORE_LIMITS             = BYPASS.permission("lore.limits");
    public static final Permission BYPASS_LORE_LENGTH             = BYPASS.permission("lore.length");

    public static boolean canBypassLoreLimits(@NotNull CommandSender sender) {
        return sender.isOp()
            || sender.hasPermission(BYPASS_LORE_LIMITS)
            || sender.hasPermission(BYPASS_LORE_LENGTH)
            || sender.hasPermission(COMMAND_LORE_UNLIMITED);
    }
}
