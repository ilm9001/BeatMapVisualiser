package com.ilm9001.beatmapvisualiser.ShowElements;

import com.ilm9001.beatmapvisualiser.BeatMapVisualiser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.EulerAngle;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ShowScreens {
    private final BeatMapVisualiser bmv;
    private final ScheduledExecutorService sch;

    private UUID scrn_l;
    private UUID scrn_c; // store a UUID reference to entities as Entity instances can "disappear" when chunks unload, but UUID's cant.
    private UUID scrn_r;
    private ItemStack scrn_stack;
    private ItemMeta scrn_meta;
    private boolean is_on;

    public ShowScreens(BeatMapVisualiser bmv)
    {
        this.bmv = bmv;
        sch = Executors.newScheduledThreadPool(1);
    }

    @SuppressWarnings("CommentedOutCode")
    public void Build(Location loc) {
        Location loc_l = loc.clone().add(0, 0.0, +10.5);
        Location loc_c = loc.clone();
        Location loc_r = loc.clone().add(0, 0.0, -10.5);

        /*
         bmv.getLogger().info(String.format("Screen loc_l: %s", loc_l.toString()));
         bmv.getLogger().info(String.format("Screen loc_c: %s", loc_c.toString()));
         bmv.getLogger().info(String.format("Screen loc_r: %s", loc_r.toString()));
        */


        scrn_l = bmv.main_world.spawnEntity(loc_l, EntityType.ARMOR_STAND).getUniqueId();
        ((ArmorStand)Bukkit.getEntity(scrn_l)).setRightArmPose(new EulerAngle(0,0,0)); // necessary so screens arent "facing towards the sky" in a weird way
        scrn_c = bmv.main_world.spawnEntity(loc_c, EntityType.ARMOR_STAND).getUniqueId();
        ((ArmorStand)Bukkit.getEntity(scrn_c)).setRightArmPose(new EulerAngle(0,0,0));
        scrn_r = bmv.main_world.spawnEntity(loc_r, EntityType.ARMOR_STAND).getUniqueId();
        ((ArmorStand)Bukkit.getEntity(scrn_r)).setRightArmPose(new EulerAngle(0,0,0));


        Entity entscrn_l = Bukkit.getEntity(scrn_l);
        Entity entscrn_c = Bukkit.getEntity(scrn_c);
        Entity entscrn_r = Bukkit.getEntity(scrn_r);

        ((ArmorStand) entscrn_l).setInvisible(true);
        entscrn_l.setInvulnerable(true);
        entscrn_l.setGravity(false);
        entscrn_l.setRotation(-90,-90);
        ((ArmorStand) entscrn_c).setInvisible(true);
        entscrn_c.setInvulnerable(true);
        entscrn_c.setGravity(false);
        entscrn_c.setRotation(-90,-90);
        ((ArmorStand) entscrn_r).setInvisible(true);
        entscrn_r.setInvulnerable(true);
        entscrn_r.setGravity(false);
        entscrn_r.setRotation(-90,-90);

        scrn_stack = new ItemStack(Material.CARROT_ON_A_STICK);
        scrn_meta = scrn_stack.getItemMeta();
        set_item(entscrn_l, scrn_stack);
        set_item(entscrn_c, scrn_stack);
        set_item(entscrn_r, scrn_stack);
        // bmv.getLogger().info("Screens built:");
    }
    public void Dismantle() {
        // bmv.getLogger().info("Screens dismantling");
        Bukkit.getEntity(scrn_l).remove(); scrn_l = null;
        Bukkit.getEntity(scrn_c).remove(); scrn_c = null;
        Bukkit.getEntity(scrn_r).remove(); scrn_r = null;
    }
    public void On() {
        // bmv.getLogger().info("Screens ON");
        Entity entscrn_l = Bukkit.getEntity(scrn_l);
        Entity entscrn_c = Bukkit.getEntity(scrn_c);
        Entity entscrn_r = Bukkit.getEntity(scrn_r);

        scrn_meta.setCustomModelData(4);
        scrn_stack.setItemMeta(scrn_meta);
        set_item(entscrn_l, scrn_stack);
        scrn_meta.setCustomModelData(5);
        scrn_stack.setItemMeta(scrn_meta);
        set_item(entscrn_c, scrn_stack);
        scrn_meta.setCustomModelData(6);
        scrn_stack.setItemMeta(scrn_meta);
        set_item(entscrn_r, scrn_stack);
        is_on = true;
    }
    public void Off() {
        // bmv.getLogger().info("Screens OFF");
        Entity entscrn_l = Bukkit.getEntity(scrn_l);
        Entity entscrn_c = Bukkit.getEntity(scrn_c);
        Entity entscrn_r = Bukkit.getEntity(scrn_r);

        scrn_meta.setCustomModelData(7);
        scrn_stack.setItemMeta(scrn_meta);
        set_item(entscrn_l, scrn_stack);
        scrn_meta.setCustomModelData(8);
        scrn_stack.setItemMeta(scrn_meta);
        set_item(entscrn_c, scrn_stack);
        scrn_meta.setCustomModelData(9);
        scrn_stack.setItemMeta(scrn_meta);
        set_item(entscrn_r, scrn_stack);
        is_on = false;
    }
    public void Fade() {
        Entity entscrn_l = Bukkit.getEntity(scrn_l);
        Entity entscrn_c = Bukkit.getEntity(scrn_c);
        Entity entscrn_r = Bukkit.getEntity(scrn_r);
        // bmv.getLogger().info("Screens FADE");
        scrn_meta.setCustomModelData(1);
        scrn_stack.setItemMeta(scrn_meta);
        set_item(entscrn_l, scrn_stack);
        scrn_meta.setCustomModelData(2);
        scrn_stack.setItemMeta(scrn_meta);
        set_item(entscrn_c, scrn_stack);
        scrn_meta.setCustomModelData(3);
        scrn_stack.setItemMeta(scrn_meta);
        set_item(entscrn_r, scrn_stack);
        
        is_on = true;
    }
    public void FadeOff(long delay) {
        Fade();
        sch.schedule(new Scrn_set_off(), delay, TimeUnit.MILLISECONDS);
    }

    public void Run() {
        // no-op for now
    }
    public void Stop() {
        // no-op for now
    }
    public void Flash() {
        if(is_on) {
            On();
        } else {
            FadeOff(2000);
        }
        // no-op for now
    }
    public void FlashOff() {
        // no-op for now
    }

    private class Scrn_set_off implements Runnable {
        @Override
        public void run()
        {
            //bmv.getLogger().info("Screens delayed OFF");
            Off();
            is_on = false;
        }
    }

    private void set_item(Entity ent, ItemStack stack) {
        ((LivingEntity) ent).getEquipment().setItemInMainHand(stack);
    }
}
// EOF
