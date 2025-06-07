package net.mcsweatshop.createdecopouches.api;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.mcsweatshop.createdecopouches.core.ModObjects;
import net.mcsweatshop.createdecopouches.core.Pouch;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

public class GivePouchCommand{

    public GivePouchCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(giveCommand());
    }

    private static final SuggestionProvider<CommandSourceStack> UNSELECTED_BAGS = (p_248113_, p_248114_) -> {
        String[] pouchIDs=new String[ModObjects.PouchInstanceMap.size()];
        int count=0;
        for (String s : ModObjects.PouchInstanceMap.keySet()) {
            pouchIDs[count]="\""+s+"\"";
            count++;
        }
        return SharedSuggestionProvider.suggest(pouchIDs, p_248114_);
    };

    private LiteralArgumentBuilder<CommandSourceStack> giveCommand() {
        return Commands.literal("pouch")
                .then(Commands.literal("give")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("pouchid",StringArgumentType.string()).suggests(UNSELECTED_BAGS).executes(this::givePouch)))
                );
    }

    private int givePouch(CommandContext<CommandSourceStack> commandSourceStackCommandContext) {
        ServerPlayer player;
        String pouchId;
        try {
            player = EntityArgument.getPlayer(commandSourceStackCommandContext, "player");
            pouchId=StringArgumentType.getString(commandSourceStackCommandContext, "pouchid");
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
        if (!ModObjects.PouchInstanceMap.containsKey(pouchId)) return 0;
        player.getInventory().add(Pouch.ItemStackWithId(pouchId));
        return 1;
    }

}
