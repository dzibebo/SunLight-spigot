package su.nightexpress.sunlight.module.items;

import su.nightexpress.nightcore.configuration.AbstractConfig;
import su.nightexpress.nightcore.configuration.ConfigProperty;
import su.nightexpress.nightcore.configuration.ConfigTypes;

public class ItemsSettings extends AbstractConfig {

    public final ConfigProperty<Integer> defaultAmountValue = this.addProperty(ConfigTypes.INT, "Commands.StackSize.DefaultValue",
        64,
        "Sets the default stack size value for the 'stack size' command if no value argument was provided.",
        "[Default is 64]"
    );

    public final ConfigProperty<Integer> maxItemStacksAmount = this.addProperty(ConfigTypes.INT, "Commands.Max-Item-Stacks-Amount",
        36,
        "Sets the max. allowed item amount (in item stacks) for the 'get', 'give' and 'spawn' commands.",
        "[Default is 36]"
    );

    public final ConfigProperty<Integer> loreMaxLines = this.addProperty(ConfigTypes.INT, "Commands.Lore.Max-Lines",
        20,
        "Sets the maximum allowed amount of lore lines on an item.",
        "Set to -1 to disable.",
        "[*] To bypass this limit, give '%s' permission.".formatted(ItemsPerms.BYPASS_LORE_LIMITS.getName()),
        "[Default is 20]"
    );

    public final ConfigProperty<Integer> loreMaxLineLength = this.addProperty(ConfigTypes.INT, "Commands.Lore.Max-Line-Length",
        80,
        "Sets the maximum allowed character length for a single lore line.",
        "Set to -1 to disable.",
        "[*] To bypass this limit, give '%s' permission.".formatted(ItemsPerms.BYPASS_LORE_LIMITS.getName()),
        "[Default is 80]"
    );

    public ItemsSettings() {

    }
}
