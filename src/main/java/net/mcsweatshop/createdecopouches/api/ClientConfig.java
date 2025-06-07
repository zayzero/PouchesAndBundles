package net.mcsweatshop.createdecopouches.api;

import net.mcsweatshop.createdecopouches.CoinPouches;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;


@Mod.EventBusSubscriber(modid = CoinPouches.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientConfig {
        private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

        private static final ForgeConfigSpec.IntValue DelayToStart = BUILDER
                .comment("How many ticks takes before rapid taking from pouch")
                .defineInRange("DelayToStart", 15, 0, Integer.MAX_VALUE);

        private static final ForgeConfigSpec.IntValue DelayBetween = BUILDER
                .comment("How many ticks between each item take")
                .defineInRange("DelayBetween", 5, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec SPEC = BUILDER.build();

        public static int DelayToStartInt;
        public static int DelayBetweenInt;


        @SubscribeEvent
        static void onLoad(final ModConfigEvent event)
        {
            DelayToStartInt = DelayToStart.get();
            DelayBetweenInt = DelayBetween.get();
        }
}
