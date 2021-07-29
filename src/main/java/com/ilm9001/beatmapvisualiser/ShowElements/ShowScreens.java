package com.ilm9001.beatmapvisualiser.ShowElements;

import com.ilm9001.beatmapvisualiser.BeatMapVisualiser;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.EulerAngle;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ShowScreens {
    private final BeatMapVisualiser bmv;
    private final ScheduledExecutorService sch;

    private Entity scrn_l;
    private Entity scrn_c; // store a entity reference as 1.17 broke UUID references (Bukkit.getEntity(UUID) doesnt work async anymore!!!!)
    private Entity scrn_r;
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


        scrn_l = bmv.main_world.spawnEntity(loc_l, EntityType.ARMOR_STAND);
        scrn_c = bmv.main_world.spawnEntity(loc_c, EntityType.ARMOR_STAND);
        scrn_r = bmv.main_world.spawnEntity(loc_r, EntityType.ARMOR_STAND);
    
        ((ArmorStand)scrn_l).setRightArmPose(new EulerAngle(0,0,0)); // necessary so screens arent "facing towards the sky" in a weird way
        ((ArmorStand)scrn_c).setRightArmPose(new EulerAngle(0,0,0));
        ((ArmorStand)scrn_r).setRightArmPose(new EulerAngle(0,0,0));
        

        ((ArmorStand) scrn_l).setInvisible(true);
        scrn_l.setInvulnerable(true);
        scrn_l.setGravity(false);
        scrn_l.setRotation(-90,-90);
        ((ArmorStand) scrn_c).setInvisible(true);
        scrn_c.setInvulnerable(true);
        scrn_c.setGravity(false);
        scrn_c.setRotation(-90,-90);
        ((ArmorStand) scrn_r).setInvisible(true);
        scrn_r.setInvulnerable(true);
        scrn_r.setGravity(false);
        scrn_r.setRotation(-90,-90);

        scrn_stack = new ItemStack(Material.CARROT_ON_A_STICK);
        scrn_meta = scrn_stack.getItemMeta();
        set_item(scrn_l, scrn_stack);
        set_item(scrn_c, scrn_stack);
        set_item(scrn_r, scrn_stack);
        // bmv.getLogger().info("Screens built:");
    }
    public void Dismantle() {
        // bmv.getLogger().info("Screens dismantling");
        scrn_l.remove(); scrn_l = null;
        scrn_c.remove(); scrn_c = null;
        scrn_r.remove(); scrn_r = null;
    }
    public void On() {
        // bmv.getLogger().info("Screens ON");

        scrn_meta.setCustomModelData(4);
        scrn_stack.setItemMeta(scrn_meta);
        set_item(scrn_l, scrn_stack);
        scrn_meta.setCustomModelData(5);
        scrn_stack.setItemMeta(scrn_meta);
        set_item(scrn_c, scrn_stack);
        scrn_meta.setCustomModelData(6);
        scrn_stack.setItemMeta(scrn_meta);
        set_item(scrn_r, scrn_stack);
        is_on = true;
    }
    public void Off() {
        // bmv.getLogger().info("Screens OFF");

        scrn_meta.setCustomModelData(7);
        scrn_stack.setItemMeta(scrn_meta);
        set_item(scrn_l, scrn_stack);
        scrn_meta.setCustomModelData(8);
        scrn_stack.setItemMeta(scrn_meta);
        set_item(scrn_c, scrn_stack);
        scrn_meta.setCustomModelData(9);
        scrn_stack.setItemMeta(scrn_meta);
        set_item(scrn_r, scrn_stack);
        is_on = false;
    }
    public void Fade() {
        // bmv.getLogger().info("Screens FADE");
        
        scrn_meta.setCustomModelData(1);
        scrn_stack.setItemMeta(scrn_meta);
        set_item(scrn_l, scrn_stack);
        scrn_meta.setCustomModelData(2);
        scrn_stack.setItemMeta(scrn_meta);
        set_item(scrn_c, scrn_stack);
        scrn_meta.setCustomModelData(3);
        scrn_stack.setItemMeta(scrn_meta);
        set_item(scrn_r, scrn_stack);
        
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
