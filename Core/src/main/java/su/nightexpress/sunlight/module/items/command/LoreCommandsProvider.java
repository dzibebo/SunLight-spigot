package su.nightexpress.sunlight.module.items.command;

import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.SuggestionsProvider;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.items.ItemsLang;
import su.nightexpress.sunlight.module.items.ItemsModule;
import su.nightexpress.sunlight.module.items.ItemsPerms;
import su.nightexpress.sunlight.module.items.ItemsSettings;

import java.util.*;
import java.util.stream.IntStream;

import static su.nightexpress.sunlight.SLPlaceholders.*;

public class LoreCommandsProvider extends AbstractCommandProvider {

    private static final Permission PERMISSION_ROOT   = ItemsPerms.COMMAND_LORE_ROOT;
    private static final Permission PERMISSION_ADD    = ItemsPerms.COMMAND_LORE_ADD;
    private static final Permission PERMISSION_INSERT = ItemsPerms.COMMAND_LORE_INSERT;
    private static final Permission PERMISSION_CLEAR  = ItemsPerms.COMMAND_LORE_CLEAR;
    private static final Permission PERMISSION_REMOVE = ItemsPerms.COMMAND_LORE_REMOVE;

    private static final String COMMAND_ADD    = "add";
    private static final String COMMAND_INSERT = "insert";
    private static final String COMMAND_CLEAR  = "clear";
    private static final String COMMAND_REMOVE = "remove";

    private final ItemsModule   module;
    private final ItemsSettings settings;

    public LoreCommandsProvider(@NotNull SunLightPlugin plugin, @NotNull ItemsModule module, @NotNull ItemsSettings settings) {
        super(plugin);
        this.module = module;
        this.settings = settings;
    }

    public LoreCommandsProvider(@NotNull SunLightPlugin plugin, @NotNull ItemsModule module) {
        this(plugin, module, module.getSettings());
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral(COMMAND_ADD, false, new String[]{"addlore"}, builder -> builder
            .description(ItemsLang.COMMAND_ITEM_LORE_ADD_DESC)
            .permission(PERMISSION_ADD)
            .withArguments(Arguments.greedyString(CommandArguments.TEXT).localized(Lang.COMMAND_ARGUMENT_NAME_TEXT))
            .executes(this::addOrInsertLoreLine)
        );

        this.registerLiteral(COMMAND_INSERT, false, new String[]{"inslore"}, builder -> builder
            .description(ItemsLang.COMMAND_ITEM_LORE_INSERT_DESC)
            .permission(PERMISSION_INSERT)
            .withArguments(
                Arguments.integer(CommandArguments.POSITION, 0, 99)
                    .localized(Lang.COMMAND_ARGUMENT_NAME_POSITION)
                    .suggestions(loreIndexesSuggestions()),
                Arguments.greedyString(CommandArguments.TEXT).localized(Lang.COMMAND_ARGUMENT_NAME_TEXT)
            )
            .executes(this::addOrInsertLoreLine)
        );

        this.registerLiteral(COMMAND_REMOVE, false, new String[]{"dellore"}, builder -> builder
            .description(ItemsLang.COMMAND_ITEM_LORE_REMOVE_DESC)
            .permission(PERMISSION_REMOVE)
            .withArguments(
                Arguments.integer(CommandArguments.POSITION, 0, 99)
                    .optional()
                    .localized(Lang.COMMAND_ARGUMENT_NAME_POSITION)
                    .suggestions(loreIndexesSuggestions())
            )
            .executes(this::removeLoreLine)
        );

        this.registerLiteral(COMMAND_CLEAR, false, new String[]{"clearlore"}, builder -> builder
            .description(ItemsLang.COMMAND_ITEM_LORE_CLEAR_DESC)
            .permission(PERMISSION_CLEAR)
            .executes(this::clearLore)
        );

        this.registerRoot("lore", true, new String[]{"itemlore", "lore", "il"},
            Map.of(
                COMMAND_ADD, "add",
                COMMAND_INSERT, "insert",
                COMMAND_REMOVE, "remove",
                COMMAND_CLEAR, "clear"
            ),
            builder -> builder.description(ItemsLang.COMMAND_ITEM_LORE_ROOT_DESC).permission(PERMISSION_ROOT)
        );
    }

    @NotNull
    private static SuggestionsProvider loreIndexesSuggestions() {
        return (reader, context) -> {
            return Optional.ofNullable(context.getPlayer())
                .map(player -> player.getInventory().getItemInMainHand())
                .filter(itemStack -> !itemStack.getType().isAir())
                .map(ItemUtil::getLoreSerialized)
                .map(List::size)
                .map(size -> IntStream.range(0, size).boxed().map(String::valueOf).toList())
                .orElse(Collections.emptyList());
        };
    }

    private boolean addOrInsertLoreLine(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return CommandArguments.handleItemInHandOrError(context, (player, itemStack) -> {
            String text = arguments.getString(CommandArguments.TEXT);
            int pos = arguments.contains(CommandArguments.POSITION) ? arguments.getInt(CommandArguments.POSITION) : -1;

            List<String> lore = ItemUtil.getLoreSerialized(itemStack);

            if (!ItemsPerms.canBypassLoreLimits(player)) {
                int maxLineLength = this.settings.loreMaxLineLength.get();
                if (maxLineLength > 0 && text.length() > maxLineLength) {
                    this.module.sendPrefixed(ItemsLang.ERROR_ITEM_LORE_LINE_TOO_LONG, context.getSender(), builder -> builder
                        .with(GENERIC_MAX, () -> String.valueOf(maxLineLength))
                        .with(GENERIC_CURRENT, () -> String.valueOf(text.length()))
                        .with(GENERIC_AMOUNT, () -> String.valueOf(text.length()))
                    );
                    return false;
                }

                int maxLines = this.settings.loreMaxLines.get();
                if (maxLines > 0 && lore.size() >= maxLines) {
                    this.module.sendPrefixed(ItemsLang.ERROR_ITEM_LORE_MAX_LINES, context.getSender(), builder -> builder
                        .with(GENERIC_MAX, () -> String.valueOf(maxLines))
                        .with(GENERIC_CURRENT, () -> String.valueOf(lore.size()))
                        .with(GENERIC_AMOUNT, () -> String.valueOf(lore.size()))
                    );
                    return false;
                }
            }

            if (pos >= 0 && pos < lore.size()) {
                lore.add(pos, text);
            }
            else {
                lore.add(text);
            }

            ItemUtil.setLore(itemStack, lore);

            this.module.sendPrefixed(ItemsLang.ITEM_LORE_ADD_FEEDBACK, context.getSender(), builder -> builder
                .with(GENERIC_ITEM, () -> ItemUtil.getNameSerialized(itemStack))
                .with(GENERIC_TEXT, () -> text)
            );
            return true;
        });
    }

    private boolean removeLoreLine(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return CommandArguments.handleItemInHandOrError(context, (player, itemStack) -> {
            int index = arguments.getInt(CommandArguments.POSITION);

            List<String> lore = ItemUtil.getLoreSerialized(itemStack);
            if (lore.isEmpty()) {
                this.module.sendPrefixed(ItemsLang.ERROR_ITEM_HAS_NO_LORE, context.getSender(), builder -> builder.with(GENERIC_ITEM, () -> ItemUtil.getNameSerialized(itemStack)));
                return false;
            }

            String result;
            if (index >= 0 && index < lore.size()) {
                result = lore.remove(index);
            }
            else {
                result = lore.removeLast();
            }

            ItemUtil.setLore(itemStack, lore);

            this.module.sendPrefixed(ItemsLang.ITEM_LORE_REMOVE_FEEDBACK, context.getSender(), builder -> builder
                .with(GENERIC_ITEM, () -> ItemUtil.getNameSerialized(itemStack))
                .with(GENERIC_TEXT, () -> result)
            );
            return true;
        });
    }

    private boolean clearLore(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return CommandArguments.handleItemInHandOrError(context, (player, itemStack) -> {
            List<String> lore = ItemUtil.getLoreSerialized(itemStack);
            if (lore.isEmpty()) {
                this.module.sendPrefixed(ItemsLang.ERROR_ITEM_HAS_NO_LORE, context.getSender(), builder -> builder
                    .with(GENERIC_ITEM, () -> ItemUtil.getNameSerialized(itemStack))
                );
                return false;
            }

            ItemUtil.setLore(itemStack, new ArrayList<>());

            this.module.sendPrefixed(ItemsLang.ITEM_LORE_CLEAR_FEEDBACK, context.getSender(), builder -> builder
                .with(GENERIC_ITEM, () -> ItemUtil.getNameSerialized(itemStack))
            );
            return true;
        });
    }
}
