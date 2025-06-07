package net.mcsweatshop.createdecopouches.events;

import net.mcsweatshop.createdecopouches.CoinPouches;
import net.mcsweatshop.createdecopouches.core.Pouch;
import net.mcsweatshop.createdecopouches.net.PacketHandler;
import net.minecraft.client.color.item.ItemColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = CoinPouches.MODID,bus = Mod.EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void colors(RegisterColorHandlersEvent.Item event){
        ItemColors itemColors=event.getItemColors();
        event.register(Pouch::getColor, CoinPouches.POUCH.get());
    }
}
