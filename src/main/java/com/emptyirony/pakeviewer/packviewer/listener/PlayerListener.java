package com.emptyirony.pakeviewer.packviewer.listener;

import com.emptyirony.pakeviewer.packviewer.PackViewer;
import com.emptyirony.pakeviewer.packviewer.object.Armor;
import com.emptyirony.pakeviewer.packviewer.object.ArmorNpc;
import net.jitse.npclib.api.NPC;
import net.jitse.npclib.api.events.NPCInteractEvent;
import net.jitse.npclib.api.skin.Skin;
import net.jitse.npclib.api.state.NPCSlot;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import team.shiva.core.profile.Profile;
import team.shiva.core.util.Cooldown;
import team.shiva.core.util.ItemBuilder;
import team.shiva.core.util.CC;

import java.util.*;

/**
 * 2 * @Author: EmptyIrony
 * 3 * @Date: 2020/1/17 17:24
 * 4
 */
public class PlayerListener implements Listener {
    private final PackViewer pl;
    private final Map<UUID, ArmorNpc> npcMap;
    private final Inventory inventory;

    public PlayerListener(PackViewer ins) {
        pl = ins;
        npcMap = new HashMap<>();
        inventory = Bukkit.createInventory(null,9*6);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {


        List<String> text = new ArrayList<>();
        text.add("&cRight click to change armor!");
        text.add(Profile.getByUuid(event.getPlayer().getUniqueId()).getColoredUsername());

        NPC npc = pl.getNpcLib().createNPC(text)
                .setSkin(new Skin(event.getPlayer().getName(), "eyJ0aW1lc3RhbXAiOjE1NzkzNzY4Njc2OTAsInByb2ZpbGVJZCI6ImI3MzY3YzA2MjYxYzRlYjBiN2Y3OGY3YzUxNzBiNzQ4IiwicHJvZmlsZU5hbWUiOiJFbXB0eUlyb255IiwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzk2YWQwYmZhM2RkOTkzMTFmZDk0NjFlYzg2OTVlYzlhMTQxNzA4NzE0OWM3MzUyYjgxZjY5OWRkZjI0YzgyMDQifX19"))
                .setLocation(PackViewer.getIns().getNpcLocation())
                .setItem(NPCSlot.HELMET, new ItemBuilder(Material.DIAMOND_HELMET).enchantment(Enchantment.DURABILITY, 1).build())
                .setItem(NPCSlot.CHESTPLATE, new ItemBuilder(Material.DIAMOND_CHESTPLATE).enchantment(Enchantment.DURABILITY, 1).build())
                .setItem(NPCSlot.LEGGINGS, new ItemBuilder(Material.DIAMOND_LEGGINGS).enchantment(Enchantment.DURABILITY, 1).build())
                .setItem(NPCSlot.BOOTS, new ItemBuilder(Material.DIAMOND_BOOTS).enchantment(Enchantment.DURABILITY, 1).build())
                .setItem(NPCSlot.MAINHAND, new ItemBuilder(Material.DIAMOND_SWORD).enchantment(Enchantment.DURABILITY).build());
        Cooldown cooldown = new Cooldown(PackViewer.getIns().getCooldown());
        ArmorNpc armorNpc = new ArmorNpc(npc, Armor.DIAMOND,cooldown);

        npc.create();
        npcMap.put(event.getPlayer().getUniqueId(), armorNpc);
        npc.show(event.getPlayer());
        pl.getHologram().show(event.getPlayer());

    }

    @EventHandler
    public void onInteract(NPCInteractEvent event) {
        if (event.getClickType() != NPCInteractEvent.ClickType.RIGHT_CLICK) {
            Location location = event.getNPC().getLocation();
            float x = (float) location.getX();
            float y = (float) location.getY() + 1;
            float z = (float) location.getZ();
            PacketPlayOutWorldParticles crit1 = new PacketPlayOutWorldParticles(EnumParticle.CRIT,false,x,y,z,0,0,0,0.5F,70);
            PacketPlayOutWorldParticles crit2 = new PacketPlayOutWorldParticles(EnumParticle.CRIT_MAGIC,false,x,y,z,0,0,0,0.5F,70);

            PlayerConnection playerConnection = ((CraftPlayer) event.getWhoClicked()).getHandle().playerConnection;
            playerConnection.sendPacket(crit1);
            playerConnection.sendPacket(crit2);


            return;
        }
        ArmorNpc armorNpc = npcMap.get(event.getWhoClicked().getUniqueId());
        if (armorNpc == null || !event.getNPC().getId().equals(armorNpc.getNpc().getId())) {
            return;
        }
        if (!armorNpc.getCooldown().hasExpired()){
            event.getWhoClicked().sendMessage(CC.translate("&cSlowdown!"));
            return;
        }

        NPC npc = armorNpc.getNpc();

        switch (armorNpc.getArmor()) {
            case DIAMOND:
                npc.setItem(NPCSlot.HELMET, new ItemBuilder(Material.GOLD_HELMET).enchantment(Enchantment.DURABILITY, 1).build())
                        .setItem(NPCSlot.CHESTPLATE, new ItemBuilder(Material.GOLD_CHESTPLATE).enchantment(Enchantment.DURABILITY, 1).build())
                        .setItem(NPCSlot.LEGGINGS, new ItemBuilder(Material.GOLD_LEGGINGS).enchantment(Enchantment.DURABILITY, 1).build())
                        .setItem(NPCSlot.BOOTS, new ItemBuilder(Material.GOLD_BOOTS).enchantment(Enchantment.DURABILITY, 1).build())
                        .setItem(NPCSlot.MAINHAND, new ItemBuilder(Material.GOLD_SWORD).enchantment(Enchantment.DURABILITY).build());

                armorNpc.setArmor(Armor.GOLDEN);
                break;
            case GOLDEN:
                npc.setItem(NPCSlot.HELMET, new ItemBuilder(Material.IRON_HELMET).enchantment(Enchantment.DURABILITY, 1).build())
                        .setItem(NPCSlot.CHESTPLATE, new ItemBuilder(Material.IRON_CHESTPLATE).enchantment(Enchantment.DURABILITY, 1).build())
                        .setItem(NPCSlot.LEGGINGS, new ItemBuilder(Material.IRON_LEGGINGS).enchantment(Enchantment.DURABILITY, 1).build())
                        .setItem(NPCSlot.BOOTS, new ItemBuilder(Material.IRON_BOOTS).enchantment(Enchantment.DURABILITY, 1).build())
                        .setItem(NPCSlot.MAINHAND, new ItemBuilder(Material.IRON_SWORD).enchantment(Enchantment.DURABILITY).build());

                armorNpc.setArmor(Armor.IRON);
                break;
            case IRON:
                npc.setItem(NPCSlot.HELMET, new ItemBuilder(Material.CHAINMAIL_HELMET).enchantment(Enchantment.DURABILITY, 1).build())
                        .setItem(NPCSlot.CHESTPLATE, new ItemBuilder(Material.CHAINMAIL_CHESTPLATE).enchantment(Enchantment.DURABILITY, 1).build())
                        .setItem(NPCSlot.LEGGINGS, new ItemBuilder(Material.CHAINMAIL_LEGGINGS).enchantment(Enchantment.DURABILITY, 1).build())
                        .setItem(NPCSlot.BOOTS, new ItemBuilder(Material.CHAINMAIL_BOOTS).enchantment(Enchantment.DURABILITY, 1).build())
                        .setItem(NPCSlot.MAINHAND, new ItemBuilder(Material.BOW).enchantment(Enchantment.DURABILITY).build());

                armorNpc.setArmor(Armor.CHAIN);
                break;
            case CHAIN:
                npc.setItem(NPCSlot.HELMET, new ItemBuilder(Material.LEATHER_HELMET).enchantment(Enchantment.DURABILITY, 1).build())
                        .setItem(NPCSlot.CHESTPLATE, new ItemBuilder(Material.LEATHER_CHESTPLATE).enchantment(Enchantment.DURABILITY, 1).build())
                        .setItem(NPCSlot.LEGGINGS, new ItemBuilder(Material.LEATHER_LEGGINGS).enchantment(Enchantment.DURABILITY, 1).build())
                        .setItem(NPCSlot.BOOTS, new ItemBuilder(Material.LEATHER_BOOTS).enchantment(Enchantment.DURABILITY, 1).build())
                        .setItem(NPCSlot.MAINHAND, new ItemBuilder(Material.WOOD_SWORD).enchantment(Enchantment.DURABILITY).build());

                armorNpc.setArmor(Armor.LEATHER);
                break;
            case LEATHER:
                npc.setItem(NPCSlot.HELMET, new ItemBuilder(Material.DIAMOND_HELMET).enchantment(Enchantment.DURABILITY, 1).build())
                        .setItem(NPCSlot.CHESTPLATE, new ItemBuilder(Material.DIAMOND_CHESTPLATE).enchantment(Enchantment.DURABILITY, 1).build())
                        .setItem(NPCSlot.LEGGINGS, new ItemBuilder(Material.DIAMOND_LEGGINGS).enchantment(Enchantment.DURABILITY, 1).build())
                        .setItem(NPCSlot.BOOTS, new ItemBuilder(Material.DIAMOND_BOOTS).enchantment(Enchantment.DURABILITY, 1).build())
                        .setItem(NPCSlot.MAINHAND, new ItemBuilder(Material.DIAMOND_SWORD).enchantment(Enchantment.DURABILITY).build());

                armorNpc.setArmor(Armor.DIAMOND);
                break;
        }
        armorNpc.setCooldown(new Cooldown(PackViewer.getIns().getCooldown()));
        event.getWhoClicked().playSound(event.getWhoClicked().getLocation(), Sound.ITEM_BREAK, 15, 2);

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        ArmorNpc armorNpc = npcMap.get(event.getPlayer().getUniqueId());
        if (armorNpc == null) {
            return;
        }

        armorNpc.getNpc().destroy();
        npcMap.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onRightClick(PlayerInteractEntityEvent event){
        if (event.getRightClicked() != null && event.getRightClicked() instanceof ItemFrame){
            ItemFrame frame = (ItemFrame) event.getRightClicked();
            event.setCancelled(true);
            Player player = event.getPlayer();
            if (frame.getItem() != null && frame.getItem().hasItemMeta() && frame.getItem().getItemMeta().hasDisplayName()){
                ItemStack item = frame.getItem();

                if (item.getType() == Material.GOLDEN_APPLE && item.getItemMeta().getDisplayName().equalsIgnoreCase(CC.translate("&cClick to view particles."))){
                    player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION,20*10,1,false));
                    return;
                }

                if (item.getType() == Material.POTION && item.getItemMeta().getDisplayName().equalsIgnoreCase(CC.translate("&cClick to view particles."))){
                    if (item.getDurability() == 8226){
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,20*10,1,false));
                        return;
                    }
                    if (item.getDurability() == 8259){
                        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,20*10,0,false));
                        return;
                    }

                    float x = (float) frame.getX();
                    float y = (float) frame.getY();
                    float z = (float) frame.getZ();

                    PacketPlayOutWorldEvent packet = new PacketPlayOutWorldEvent(2002, new BlockPosition(x, y, z), item.getDurability(), false);
                    ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
                }
            }
        }
    }

    private boolean isInRange(Location location){
        return (location.getX() - PackViewer.getIns().getMinX()) * (location.getX() - PackViewer.getIns().getMaxX()) <= 0 &&
                (location.getY() - PackViewer.getIns().getMinY()) * (location.getY() - PackViewer.getIns().getMaxY()) <= 0 &&
                (location.getZ() - PackViewer.getIns().getMinZ()) * (location.getZ() - PackViewer.getIns().getMinZ()) <= 0;
    }

    @EventHandler
    public void onLeftClick(PlayerInteractEvent event){
        if (event.getAction() == Action.LEFT_CLICK_BLOCK){
            Location location = event.getClickedBlock().getLocation();
            if (isInRange(location)) {
                event.setCancelled(true);
            }
        }else if (event.getAction() == Action.RIGHT_CLICK_BLOCK){
            if (event.getClickedBlock().getType()==Material.CHEST){
                Location location = event.getClickedBlock().getLocation();
                if (isInRange(location)) {
                    event.getPlayer().openInventory(inventory);
                    event.getPlayer().playSound(event.getPlayer().getLocation(),Sound.CHEST_OPEN,1,1);
                }
            }
        }
    }

    @EventHandler
    public void onLeftClickItemFrame(EntityDamageByEntityEvent event){
        if (event.getEntity().getType() == EntityType.ITEM_FRAME){
            event.setCancelled(true);
        }
    }

}
