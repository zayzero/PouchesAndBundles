package net.mcsweatshop.createdecopouches.events;

import net.mcsweatshop.createdecopouches.CoinPouches;

import net.mcsweatshop.createdecopouches.core.Pouch;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;


@Mod.EventBusSubscriber(modid = CoinPouches.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientModForgeEvents {

    @SubscribeEvent
    public static void renderingInventory(RenderTooltipEvent.Pre e){
        if (e==null) return;
        if (!e.getItemStack().is(CoinPouches.POUCH.get())) return;

        int lineHeight=new StringWidget(Component.empty(),Minecraft.getInstance().font).getHeight();
        GuiGraphics graphics = e.getGraphics();
        graphics.pose().translate(0f,0f,1000f);
        ListTag tags=Pouch.getListOfItems(e.getItemStack());
        for (int i = 1; i < 4; i++) {
            graphics.renderItem(Pouch.getItem(e.getItemStack(), tags.size()-i, false), e.getX()+10+(17*(i-1)), e.getY()+(3*lineHeight)-4, 1000);
        }

        graphics.pose().translate(0f,0f,-1000f);
    }

}
