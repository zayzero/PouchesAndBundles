package net.mcsweatshop.createdecopouches.core;

import net.mcsweatshop.createdecopouches.CoinPouches;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Pouch extends Item {

    public Pouch() {
        super(new Properties().stacksTo(1));
    }

    public static ListTag getListOfItems(ItemStack pouch){
        if(!pouch.getOrCreateTag().contains("items")) pouch.getOrCreateTag().put("items", new ListTag());
        assert pouch.getTag() != null;
        return pouch.getTag().getList("items", Tag.TAG_COMPOUND);
    }
    public static int getCountItemsInside(ItemStack pouch){
        if(!pouch.getOrCreateTag().contains("itemcount")) pouch.getOrCreateTag().putInt("itemcount", 0);
        return pouch.getOrCreateTag().getInt("itemcount");
    }
    public static void decreaseItemCount(ItemStack pouch, CompoundTag item, int Count){
        ListTag list = getListOfItems(pouch);
        CompoundTag compoundTag=item.copy();
        compoundTag.putInt("Count", -Count);
        CompoundTag compoundTagWithoutCount=item.copy();
        compoundTagWithoutCount.remove("Count");
        List<Tag> tagsToRemove=new ArrayList<>();
        for (Tag tag : list) {
            if (!(tag.copy() instanceof CompoundTag ct)) continue;
            if (!ct.contains("Count")) continue;
            int countToBeAdded=ct.getInt("Count");
            ct.remove("Count");
            if (!ct.equals(compoundTagWithoutCount)) continue;
            tagsToRemove.add(tag);
            compoundTag.putInt("Count",compoundTag.getInt("Count")+countToBeAdded);
        }
        for (Tag tag : tagsToRemove) list.remove(tag);
        if (compoundTag.contains("Count")&&compoundTag.getInt("Count")>0) list.add(compoundTag);
        pouch.getOrCreateTag().putInt("itemcount",getCountItemsInside(pouch)-Count);
        pouch.getOrCreateTag().put("items",list);
    }
    public static int getItemCountWithId(ItemStack pouch, CompoundTag itemTag){
        ListTag tag = getListOfItems(pouch);
        CompoundTag itemTagCopy = itemTag.copy();
        itemTagCopy.remove("Count");
        for (Tag listItemTag : tag) {
            CompoundTag listItemTagCopy = (CompoundTag) listItemTag.copy();
            int count=listItemTagCopy.getInt("Count");
            listItemTagCopy.remove("Count");
            if (listItemTagCopy.equals(itemTagCopy)) return count;
        }
        return -1;
    }


    public static boolean isWhitelisted(ItemStack pouch, ItemStack itemStack){
        String id = pouch.getOrCreateTag().getString("pouchid");
        if (id.isEmpty()) return false;
        if (ModObjects.PouchInstanceMap.containsKey(id)) {
            ModObjects.PouchInstance pouchInstance = ModObjects.PouchInstanceMap.get(id);
            int differentItemOut=handleMaxDifferentItems(pouchInstance,pouch,itemStack);
            if (differentItemOut>0) return 2==differentItemOut;
            if (pouchInstance.itemWhiteList.isEmpty()) return true;
            ResourceLocation key = ForgeRegistries.ITEMS.getKey(itemStack.getItem());
            if (key==null) return false;
            if (pouchInstance.itemWhiteList.contains(key.toString())) return true;
            ITagManager<Item> tags = ForgeRegistries.ITEMS.tags();
            if (tags==null) return false;
            for (String itemTag : pouchInstance.itemTags) {
                TagKey<Item> name = TagKey.create(Registries.ITEM, new ResourceLocation(itemTag));
                if(!tags.isKnownTagName(name)) continue;
                if (itemStack.is(name)) return true;
            }
            return false;
        }
        return false;
    }

    //0 - continue 1 - no room for it 2 - already exists and can be added to
    private static int handleMaxDifferentItems(ModObjects.PouchInstance pouchInstance, ItemStack pouch, ItemStack itemStack) {
        if (pouchInstance.maxDifferentItems<=-1) return 0;
        ListTag list = getListOfItems(pouch);

        ResourceLocation resourceLocation = ForgeRegistries.ITEMS.getKey(itemStack.getItem());
        if (resourceLocation==null) return 1;
        String itemStackID=resourceLocation.toString();
        //if we check by id
        List<String> ids=new ArrayList<>();
        if (!pouchInstance.differentiateByNBT){
            for (Tag tag : list) {
                if (!(tag instanceof CompoundTag compoundTag)) continue;
                if (!compoundTag.contains("id"))continue;
                String id = compoundTag.getString("id");
                if (id.equals(itemStackID)) return 2;
                if (!ids.contains(id)) ids.add(id);
            }
            if (ids.size()>=pouchInstance.maxDifferentItems) return 1;
            return 0;
        }

        //checking if has item with same tag
        if (list.size()<pouchInstance.maxDifferentItems) return 0;
        CompoundTag itemStackAsTag = itemStack.serializeNBT().copy();
        itemStackAsTag.remove("Count");
        for (Tag tag : list) {
            if (!(tag instanceof CompoundTag compoundTag)) continue;
            CompoundTag copy = compoundTag.copy();
            if (!copy.contains("Count")) continue;
            if (itemStackAsTag.equals(copy)) return 2;
        }
        return 1;
    }
    public static int GetItemDifferentCount(String id, ItemStack pouch){
        if (!ModObjects.PouchInstanceMap.containsKey(id)) return -1;
        ModObjects.PouchInstance pouchInstance = ModObjects.PouchInstanceMap.get(id);
        ListTag list = getListOfItems(pouch);
        if (pouchInstance.differentiateByNBT) return list.size();
        List<String> ids=new ArrayList<>();
        for (Tag tag : list) {
            if (!(tag instanceof CompoundTag compoundTag)) continue;
            if (!compoundTag.contains("id"))continue;
            String itemid = compoundTag.getString("id");
            if (!ids.contains(itemid)) ids.add(itemid);
        }
        return ids.size();
    }

    public static boolean addItem(ItemStack pouch, ItemStack item){
        try {
            if (!isWhitelisted(pouch,item)) return false;
            if (isPouchFull(pouch, item.getCount())) return  false;
            if(!pouch.getOrCreateTag().contains("items")) pouch.getOrCreateTag().put("items", new ListTag());
            ListTag list = pouch.getTag().getList("items", Tag.TAG_COMPOUND);
            CompoundTag compoundTag=item.serializeNBT().copy();
            CompoundTag compoundTagWithoutCount=item.serializeNBT().copy();
            compoundTagWithoutCount.remove("Count");
            List<Tag> tagsToRemove=new ArrayList<>();
            for (Tag tag : list) {
                if (!(tag.copy() instanceof CompoundTag ct)) continue;
                if (!ct.contains("Count")) continue;
                int Count=ct.getInt("Count");
                ct.remove("Count");
                if (!ct.equals(compoundTagWithoutCount)) continue;
                tagsToRemove.add(tag);
                compoundTag.putInt("Count",compoundTag.getInt("Count")+Count);
            }
            for (Tag tag : tagsToRemove) list.remove(tag);
            list.add(compoundTag);

            pouch.getOrCreateTag().put("items",list);
            pouch.getOrCreateTag().putInt("itemcount",getCountItemsInside(pouch)+item.getCount());

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isPouchFull(ItemStack pouch, int amountToAdd) {
        String id = pouch.getOrCreateTag().getString("pouchid");
        int itemsInside = getCountItemsInside(pouch) + amountToAdd;
        if (id.isEmpty()) return itemsInside > 64;
        if (ModObjects.PouchInstanceMap.containsKey(id)) {
            if (ModObjects.PouchInstanceMap.get(id).maxItems==-1) return false;
            return itemsInside > ModObjects.PouchInstanceMap.get(id).maxItems;

        } else {
            return itemsInside > 64;
        }
    }

    public static ItemStack getItem(ItemStack pouch, int index, boolean removeFromList){
        if (getCountItemsInside(pouch)<=0) return ItemStack.EMPTY.copy();
        try {
            if(!pouch.getOrCreateTag().contains("items")) pouch.getOrCreateTag().put("items", new ListTag());

            ListTag list = pouch.getOrCreateTag().getList("items", Tag.TAG_COMPOUND);
            if (list.size()<=index) return ItemStack.EMPTY.copy();
            CompoundTag item=list.getCompound(index).copy();
            Item baseItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(item.getString("id")));
            if (baseItem==null) {
                CoinPouches.LOGGER.error(new NullPointerException("Item doesn't exist? "+ item).getMessage());
                return ItemStack.EMPTY;
            }
            if (item.contains("Count")&&item.getInt("Count")>baseItem.getDefaultInstance().getMaxStackSize()) item.putInt("Count",baseItem.getDefaultInstance().getMaxStackSize());

            if (removeFromList) pouch.getOrCreateTag().put("items",list);
            if (removeFromList) Pouch.decreaseItemCount(pouch, item,item.getInt("Count"));

            return ItemStack.of(item);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ItemStack.EMPTY.copy();
        }
    }
    public static ItemStack getItem(ItemStack pouch, int index, int Count, boolean removeFromList){
        if (getCountItemsInside(pouch)<=0) return ItemStack.EMPTY.copy();
        try {
            if(!pouch.getOrCreateTag().contains("items")) pouch.getOrCreateTag().put("items", new ListTag());

            ListTag list = pouch.getOrCreateTag().getList("items", Tag.TAG_COMPOUND);
            if (list.size()<=index) return ItemStack.EMPTY.copy();
            CompoundTag item=list.getCompound(index).copy();
            Item baseItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(item.getString("id")));
            if (baseItem==null) {
                CoinPouches.LOGGER.error(new NullPointerException("Item doesn't exist? "+ item).getMessage());
                return ItemStack.EMPTY;
            }
            if (item.contains("Count")&&item.getInt("Count")>baseItem.getDefaultInstance().getMaxStackSize()) item.putInt("Count",baseItem.getDefaultInstance().getMaxStackSize());

            if (removeFromList) pouch.getOrCreateTag().put("items",list);
            if (removeFromList) Pouch.decreaseItemCount(pouch, item,Count);
            item.putInt("Count",Count);
            return ItemStack.of(item);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ItemStack.EMPTY.copy();
        }
    }

    public static ItemStack getNextItem(ItemStack pouch, boolean removeFromList){
        ListTag list = getListOfItems(pouch);
        return getItem(pouch,list.size()-1, removeFromList);
    }
    public static ItemStack getNextItem(ItemStack pouch,int count, boolean removeFromList){
        ListTag list = getListOfItems(pouch);
        return getItem(pouch,list.size()-1, count, removeFromList);
    }


    public static ItemStack ItemStackWithId(String pouchId){
        ItemStack stack = CoinPouches.POUCH.get().getDefaultInstance().copy();
        stack.getOrCreateTag().putString("pouchid",pouchId);
        return stack;
    };

    @OnlyIn(Dist.CLIENT)
    public static int getColor(ItemStack stack, int tintIndex) {
        if (!stack.is(CoinPouches.POUCH.get())) return 0xFFFFFF;
        if (tintIndex == 1) return getPouchBandColor(stack);
        return 0xFFFFFF;
    }

    private static int getPouchBandColor(ItemStack s) {
        String id = s.getOrCreateTag().getString("pouchid");
        if (id.isEmpty()) return DyeColor.WHITE.getTextColor();
        if (ModObjects.PouchInstanceMap.containsKey(id)) return ModObjects.PouchInstanceMap.get(id).color;
        return DyeColor.RED.getTextColor();
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        Player player = ctx.getPlayer();
        InteractionHand hand = ctx.getHand();
        return InteractionUseOnServer(level, player, hand, ctx);
//        if (FMLEnvironment.dist==Dist.CLIENT&&Minecraft.getInstance().isSingleplayer()) return InteractionOnServer(level, player, hand);
//        return InteractionResult.SUCCESS;
    }

    private InteractionResult InteractionUseOnServer(Level level, Player player, InteractionHand hand, UseOnContext ctx) {
        ItemStack pouch= player.getItemInHand(hand).copy();
        if(!pouch.is(CoinPouches.POUCH.get())) return InteractionResult.PASS;
        player.awardStat(Stats.ITEM_USED.get(this));
        ItemStack itemStack=getNextItem(pouch, 1, false);
        if (itemStack.isEdible()) return InteractionResult.PASS;
        CompoundTag itemNBT=itemStack.serializeNBT().copy();
        player.setItemInHand(hand,itemStack);
        InteractionResult use = itemStack.useOn(new UseOnContext(level,player,hand, itemStack, new BlockHitResult(ctx.getClickLocation(),ctx.getClickedFace(), ctx.getClickedPos(), ctx.isInside() )));
        player.setItemInHand(hand,pouch);
        if (use.shouldAwardStats()) decreaseItemCount(pouch, itemNBT, 1);
        else if(ModObjects.PouchInstance.getInstance(pouch).dropItems)player.drop(getNextItem(pouch, true), true);

        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide||player.isCreative()) return InteractionOnServer(level, player, hand);
//        if (FMLEnvironment.dist==Dist.CLIENT&&Minecraft.getInstance().isSingleplayer()) return InteractionOnServer(level, player, hand);
        return InteractionOnClient(level, player, hand);

    }

    @NotNull
    private InteractionResultHolder<ItemStack> InteractionOnServer(Level level, Player player, InteractionHand hand) {
        ItemStack pouch= player.getItemInHand(hand);
        if(!pouch.is(CoinPouches.POUCH.get())) return InteractionResultHolder.pass(pouch);
        player.awardStat(Stats.ITEM_USED.get(this));
        ItemStack itemStack=getNextItem(pouch, 1, false);
        if (itemStack.isEdible()) return InteractionResultHolder.sidedSuccess(pouch, level.isClientSide());
        CompoundTag itemNBT=itemStack.serializeNBT().copy();
        player.setItemInHand(hand,itemStack);
        InteractionResultHolder<ItemStack> use = itemStack.use(level, player, hand);
        player.setItemInHand(hand,pouch);
        if (use.getResult().shouldAwardStats()) decreaseItemCount(pouch, itemNBT, 1);
        else if(ModObjects.PouchInstance.getInstance(pouch).dropItems) player.drop(getNextItem(pouch, true), true);

        return InteractionResultHolder.sidedSuccess(pouch, level.isClientSide());
    }
    @NotNull
    private InteractionResultHolder<ItemStack> InteractionOnClient(Level level, Player player, InteractionHand hand) {
        ItemStack pouch= player.getItemInHand(hand);
        if(!pouch.is(CoinPouches.POUCH.get())) return InteractionResultHolder.pass(pouch);
        ItemStack itemStack=getNextItem(pouch, 1, false);
        if (itemStack.isEdible()) return InteractionResultHolder.pass(pouch);
        return InteractionResultHolder.success(pouch);

    }

    //    @Override
//    public void onUseTick(Level p_41428_, LivingEntity entity, ItemStack pouch, int p_41431_) {
//        if (!(entity instanceof Player player)) return;
//
//    }
//    @Override
//    public int getUseDuration(ItemStack p_41454_) {
//        return 5;
//    }
    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> texts, TooltipFlag tooltipFlag) {
        int countItemsInside = getCountItemsInside(itemStack);
        if (countItemsInside<1) return;
        String id = itemStack.getOrCreateTag().getString("pouchid");
        boolean b = ModObjects.PouchInstanceMap.containsKey(id);
        String maxSlots = id.isEmpty() || !b ? "?" : ifNegOneInf(ModObjects.PouchInstanceMap.get(id).maxItems);
        String maxDifferentItems = id.isEmpty() || !b ? "?" : ifNegOneInf(ModObjects.PouchInstanceMap.get(id).maxDifferentItems);
        texts.add(Component.literal(String.valueOf(Pouch.getCountItemsInside(itemStack))).append("/").append(maxSlots).append(Component.translatable("coinpouches.itemstext")));
        texts.add(Component.literal(String.valueOf(getDifferentItemsCount(itemStack))).append("/").append(maxDifferentItems).append(Component.translatable("coinpouches.items-different-text")));
        texts.add(Component.empty());
        if (countItemsInside>3) countItemsInside=3;
        String s="    ";
        for (int i = 1; i < countItemsInside; i++) {
            s+="    ";
        }
        texts.add(Component.literal(s));
    }

    @NotNull
    private static int getDifferentItemsCount(ItemStack pouch) {
        ListTag listOfItems = getListOfItems(pouch);
        int size = listOfItems.size();
        String id = pouch.getOrCreateTag().getString("pouchid");
        if (id.isEmpty()) return size;
        if (ModObjects.PouchInstanceMap.containsKey(id)) {
            ModObjects.PouchInstance pouchInstance = ModObjects.PouchInstanceMap.get(id);
            if (pouchInstance.differentiateByNBT) return size;
            List<String> ids=new ArrayList<>();
            for (Tag tag : listOfItems) if (!ids.contains(id)) ids.add(id);
            return ids.size();
        }
        return size;
    }



    //if negative one return infinity
    private String ifNegOneInf(int count) {
        if (count==-1) return "∞";
        return String.valueOf(count);
    }


}
