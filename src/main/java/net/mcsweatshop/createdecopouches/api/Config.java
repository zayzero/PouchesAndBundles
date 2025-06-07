package net.mcsweatshop.createdecopouches.api;

import net.mcsweatshop.createdecopouches.CoinPouches;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = CoinPouches.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

//    private static final ForgeConfigSpec.IntValue BASIC_POUCH_SLOT_COUNT = BUILDER
//            .comment("Basic Pouch Number Of Slots")
//            .defineInRange("AmountOfSlots", 8, 0, Integer.MAX_VALUE);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

//    public static int BasicPouchAmountOfSlots;

//
//    @SubscribeEvent
//    static void onLoad(final ModConfigEvent event)
//    {
//        BasicPouchAmountOfSlots = BASIC_POUCH_SLOT_COUNT.get();
//    }
}
