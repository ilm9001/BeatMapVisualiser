package com.ilm9001.beatmapvisualiser.ShowElements;

import com.ilm9001.beatmapvisualiser.BeatMapVisualiser;
import com.ilm9001.beatmapvisualiser.Util.Util;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;


public class ShowBacklights implements ShowControl {
    private final BeatMapVisualiser bmv;
    private final ScheduledExecutorService sch;

    // Tunables
    private double OFFSETZ = 6.5;
    private double ANGLE_MIN = 70.0;
    private double ANGLE_MAX = 110.0;
    private final double ANGLE_RANGE = ANGLE_MAX - ANGLE_MIN;
    private final double INITIAL_ANGLE = ANGLE_RANGE / 2.0;
    private double INITIAL_LENGTH = 1800.0;

    // Internal runtime variables
    
    private final ArrayList<Entity> bklts_l;//1.17 broke UUID entity storage (making it that Bukkit.getEntity(UUID) mustnt be async)
    private final ArrayList<Entity> bklts_r;// so now we just store entities
    private boolean is_on;
    private boolean color; // True = blue, False = red
    private boolean is_running;
    private double a;
    private double a_d;
    private int off_counter;
    private double length;
    private double step_a;
    private double bltoffsetX;
    private double bltoffsetY;
    private double bltoffsetZ;
    private Location backlight_center;
    private Location backlight_center_alt;
    private int num_light;

    public ShowBacklights(BeatMapVisualiser bmv) {
        this.bmv = bmv;
        step_a = 0.02;
        bklts_l = new ArrayList<>();
        bklts_r = new ArrayList<>();
        is_on = true;
        a = INITIAL_ANGLE;
        a_d = step_a;
        off_counter = 0;
        length = INITIAL_LENGTH;
        color = true;
        sch = Executors.newScheduledThreadPool(1);
    }

    public void setColor(boolean isBlue) {
        if(isBlue == !color) {
            //color switch check, so we dont do useless movement
            if (isBlue) {
                OFFSETZ = 22.5; //Teleports are the rare case where they *must* be in sync with the server, aka must use bukkitrunnables
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i< num_light; i++) {
                            bklts_l.get(i).teleport(backlight_center.clone().add(0,0,-OFFSETZ).clone().add(i*bltoffsetX,i*bltoffsetY,i*bltoffsetZ));
                        }

                        for (int i = 0; i< num_light; i++) {
                            bklts_r.get(i).teleport(backlight_center.clone().add(0, 0, OFFSETZ).clone().add(i*bltoffsetX,i*bltoffsetY,i*bltoffsetZ));
                        }
                    }
                }.runTask(bmv);
                //blue = go to default location
            } else {
                OFFSETZ = 30.5;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i< num_light; i++) {
                            bklts_l.get(i).teleport(backlight_center_alt.clone().add(0,0,-OFFSETZ).clone().add(i*bltoffsetX,i*bltoffsetY,i*bltoffsetZ));
                        }

                        for (int i = 0; i< num_light; i++) {
                            bklts_r.get(i).teleport(backlight_center_alt.clone().add(0, 0, OFFSETZ).clone().add(i*bltoffsetX,i*bltoffsetY,i*bltoffsetZ));
                        }
                    }
                }.runTask(bmv);
                //red = go to secondary location
            }
        }
        color = isBlue;
    }

    public void Build(Location loc, Location alternate_loc, int NUM_LGHT, double offsetX, double offsetY, double offsetZ) {

        bltoffsetX = offsetX;
        bltoffsetY = offsetY;
        bltoffsetZ = offsetZ;

        this.num_light = NUM_LGHT;
        if(backlight_center == null) {
            backlight_center = loc;
            backlight_center_alt = alternate_loc;
        }

        // Left backlight
        Location loc1 = loc.clone().add(0, 0, -OFFSETZ);
        // bmv.getLogger().info(String.format("Create bklt1 at %s", loc1.toString()));
        for(int i=0; i<NUM_LGHT; i++) {
            Location altloc = loc1.clone().add(i*offsetX, i*offsetY, i*offsetZ);
            Entity ent1 = bmv.main_world.spawnEntity(altloc, EntityType.ENDER_CRYSTAL);
            bklts_l.add(ent1);
            ((EnderCrystal) ent1).setBeamTarget(null);
            ((EnderCrystal) ent1).setShowingBottom(false);
        }

        // Right backlight
        Location loc2 = loc.clone().add(0, 0, OFFSETZ);
        // bmv.getLogger().info(String.format("Create bklt2 at %s", loc2.toString()));
        for(int i=0; i<NUM_LGHT; i++) {
            Location altloc = loc2.clone().add(-i*offsetX, -i*offsetY, -i*offsetZ);
            Entity ent2 = bmv.main_world.spawnEntity(altloc, EntityType.ENDER_CRYSTAL);
            bklts_r.add(ent2);
            ((EnderCrystal) ent2).setBeamTarget(null);
            ((EnderCrystal) ent2).setShowingBottom(false);
        }
        // bmv.getLogger().info(String.format("Built bklts %s", bklts.toString()));
        this.setColor(false);
        Util.safe_sleep(200);
        this.setColor(true);
        Util.safe_sleep(100);
        this.setColor(false);
    }

    @Override
    public void Dismantle() {
        // bmv.getLogger().info(String.format("Dismantling bklts %s", bklts.toString()));
        for (Entity blt: bklts_l) {
            if (blt != null) blt.remove();
        }
        for (Entity blt: bklts_r) {
            if (blt != null) blt.remove();
        }
        bklts_l.clear();
        bklts_r.clear();
    }

    @Override
    public void Run() {
        is_on = false;
        sch.schedule(new Backlights_runnable(),0, TimeUnit.MILLISECONDS);
        is_running = true;
    }

    @Override
    public void Stop() {
        Off();
        Util.safe_sleep(100);
        //turns off lasers and exits bukkitrunnable since show is off
        is_running = false;
    }

    @Override
    public void On() {
        is_on = true;
        off_counter = 0;
        length = INITIAL_LENGTH;
        sch.schedule(new Backlights_runnable(),0, TimeUnit.MILLISECONDS);
        // bmv.getLogger().info("Bklts On");
    }

    @Override
    public void Off() {
        is_on = false;
        off_counter = 0;
        length = INITIAL_LENGTH;
        sch.schedule(new Backlights_runnable(),0, TimeUnit.MILLISECONDS);
        // bmv.getLogger().info("Bklts Off");
    }

    @Override
    public void Flash() {
        if(is_on) {
            On();
            /*off_counter = 0;
            length += INITIAL_LENGTH/5; //similar functionality to lasers but as we are already pushing the limits of end crystals, this would cause overflow of the Y coordinate
            off_counter = 8;
             */
            //a += a_d*5;
        } else {
            FlashOff();
        }
        // bmv.getLogger().info("Bklts Flash");
    }

    @Override
    public void FlashOff() {
        // bmv.getLogger().info("Bklts Flashoff");
        On();
        off_counter = 40; // 2 seconds
    }

    private class Backlights_runnable implements Runnable {
        @Override
        public void run() {
            // bmv.getLogger().info(String.format("Starting bklts %s", bklts.toString()));
            while(true) {
                if(!is_running) {
                    return;
                    //return if Stop() is called
                }
                
                // handle scheduled off
                if (off_counter > 0) {
                    off_counter -= 1;
                    length -= INITIAL_LENGTH / 40;
                    if (off_counter == 0) {
                        is_on = false;
                        //bmv.getLogger().info("Bklts off_counter reached");
                    }
                }
    
                // continue moving the angle, even with "lights off" (is_on = false)
                a += a_d;
                if (a >= ANGLE_RANGE) {
                    a_d = -step_a;
                } else if (a <= 0.0) {
                    a_d = step_a;
                }
    
                if (is_on && length > INITIAL_LENGTH / 120) {
                    // Left backlights
                    double delta_r1 = (2.0 * Math.PI * (ANGLE_MIN + a)) / 360.0;
                    Vector3D v1 = new Vector3D(Math.PI / 2, delta_r1).normalize().scalarMultiply(length);
                    for (int i = 0; i < num_light; i++) {
                        Entity blt = bklts_l.get(i);
                        Location loc = blt.getLocation().clone()
                                .add(v1.getX(), v1.getZ(), v1.getY());
                        ((EnderCrystal) blt).setBeamTarget(loc);
                    }
        
                    // Right backlights
                    double delta_r2 = (2.0 * Math.PI * (ANGLE_MAX - a)) / 360.0;
                    Vector3D v2 = new Vector3D(Math.PI / 2, delta_r2).normalize().scalarMultiply(length);
        
                    for (int i = 0; i < num_light; i++) {
                        Entity blt = bklts_r.get(i);
                        Location loc = blt.getLocation().clone()
                                .add(v2.getX(), v2.getZ(), v2.getY());
                        ((EnderCrystal) blt).setBeamTarget(loc);
                    }
                } else {
                    // set beams off
                    for (Entity blt : bklts_l) {
                        ((EnderCrystal) blt).setBeamTarget(null);
                    }
                    for (Entity blt : bklts_r) {
                        ((EnderCrystal) blt).setBeamTarget(null);
                    }
                }
                Util.safe_sleep(100);
            }
        }
    }
}