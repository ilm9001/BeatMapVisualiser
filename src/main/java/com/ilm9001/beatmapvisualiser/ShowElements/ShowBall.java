package com.ilm9001.beatmapvisualiser.ShowElements;

import com.ilm9001.beatmapvisualiser.BeatMapVisualiser;
import com.ilm9001.beatmapvisualiser.Util.Laser;
import com.ilm9001.beatmapvisualiser.Util.Util;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
This class creates a ball at the center of the show, which is created with tens of lasers using Vector3D pointing at
random delta and alpha locations, but with the same length, cleverly creating a "ball" when looking at the end points of
all the lasers.
 */

public class ShowBall implements ShowControl {
   private final BeatMapVisualiser bmv;
   private final ScheduledExecutorService sch;
   private int NUM_LSR;
   private final ArrayList<Laser> lsrs;
   private Location ballloc;
   private HashMap<Laser,Double> uniqueAlpha;
   private HashMap<Laser,Double> uniqueDelta;
   
   private final double STEP_A = 1.2;
   private double INITIAL_LEN = 25.0;
   private final double LEN_MIN = 0.0;
   private double LEN_MAX = INITIAL_LEN+20;
   private double INITIAL_A = 40.0;
   
   private boolean ringZoomed;
   private boolean is_running;
   private boolean is_on;
   private double len_d;
   private double len;
   private double a = INITIAL_A;
   private double step = STEP_A;
   private int spin;
   
   public ShowBall(BeatMapVisualiser bmv) {
      this.bmv = bmv;
      sch = Executors.newScheduledThreadPool(1);
      lsrs = new ArrayList<>();
      uniqueDelta = new HashMap<>();
      uniqueAlpha = new HashMap<>();
      spin = 1;
   }
   
   public void Build(Location loc, int num_lsr, double end_time) {
      
      NUM_LSR = num_lsr;
      
      try {
         for (int i=0; i < NUM_LSR; ++i) {
            Laser lsr = new Laser(loc,loc, (int) end_time, 512);
            lsr.start(bmv);
            lsrs.add(lsr);
            double alpha = i*360;
            double delta = i*15;
            uniqueAlpha.put(lsr,alpha);
            uniqueDelta.put(lsr,delta);
         }
      } catch (ReflectiveOperationException e) {
         e.printStackTrace();
      }
      ballloc = loc;
      // bmv.getLogger().info(String.format("Built lsr ball %s", lsrs.toString()));
   }
   
   public void setRingZoomed(boolean zoom) {
      ringZoomed = zoom;
      // Reverse laser rotation if true
   }
   
   public void Dismantle() {
      // bmv.getLogger().info(String.format("Dismantling lsrs %s", lsrs.toString()));
      for (Laser lsr: lsrs) {
         if (lsr != null && lsr.isStarted()) lsr.stop();
      }
      lsrs.clear();
   }
   
   @Override
   public void Run() {
      is_running = true;
      sch.schedule(new ShowBall.Lasers_runnable(), 0, TimeUnit.MILLISECONDS);
      Off();
   }
   @Override
   public void Stop() {
      Off();
      Util.safe_sleep(300);
      // the Runnable thread will notice this and exit/return.
      is_running = false;
   }
   @Override
   public void On() {
      len = INITIAL_LEN;
      len_d = 0.0;
      is_on = true;
      sch.schedule(new ShowBall.Lasers_runnable(), 0, TimeUnit.MILLISECONDS);
   }
   @Override
   public void Off() {
      len = LEN_MIN;
      len_d = 0.0;
      is_on = false;
      len = 0;
      sch.schedule(new ShowBall.Lasers_runnable(), 0, TimeUnit.MILLISECONDS);
   }
   @Override
   public void Flash() {
      if(!is_on) {
         FlashOff();
      } else {
         On();
         for (Laser lsr : lsrs) {
            Util.safe_lsr_colorchange(lsr);
         }
         len_d = 0.0;
         len += (LEN_MAX-INITIAL_LEN) / 6;
      }
   }
   @Override
   public void FlashOff() {
      On();
      Flash();
      len_d = -1.3;
   }
   
   public void SetStep(int step) {
      this.step = step;
      if (step == 0) {
         this.step = 0.35;
      }
   }
   public void addSpin() {
      spin += 90;
   } // Called as a ring spin https://bsmg.wiki/mapping/map-format.html#events-2
   
   private class Lasers_runnable implements Runnable {
      //private double a_n;
      @Override
      public void run() {
         // bmv.getLogger().info(String.format("Starting lasers %s", lsrs.toString()));
         while (true) {
            if (!is_running) {
               // bmv.getLogger().info(String.format("Stopping lasers %s", lsrs.toString()));
               return; // exit this thread now
            }
            
            len += len_d;
            if (len <= LEN_MIN) {
               len = LEN_MIN;
               len_d = 0.0;
            }
            if (len >= LEN_MAX) {
               len = LEN_MAX;
               len_d = -0.8;
            }
            
            if(spin > 1) {
               spin -= spin/20+1;
               if(spin < 0) {
                  spin=1;
               }
               //a_n *= Math.max(spin, 20); // autogenerated suggestion
            }
            
            double astep = STEP_A * step;
            if(ringZoomed) {
               a -= astep;
            } else {
               a += astep; // TODO: fix, this *could* eventually cause overflow
            }
            //a = a % 360.0; // doesnt work here as we arent rotating in a circle so resetting from 360 to 0 will cause unintended behaviour
            
            
            
            for (Laser lsr : lsrs) {
               //if(a_n < 360) a_n = 360;
               
               double a_n = 360 + a + uniqueDelta.get(lsr);
               double b_n = 90 + (a/5) + spin; //+ uniqueAlpha.get(lsr);
               if (!lsr.isStarted() && is_on && is_running) {
                  lsr.start(bmv);
               }
   
               double delta_r = (2.0 * Math.PI * a_n) / 360.0;
               double alpha_a = (2.0 * Math.PI * b_n) / 360.0;
              
               Vector3D v1 = new Vector3D(alpha_a, delta_r).normalize().scalarMultiply(len / 3);
               
               Location end_loc = ballloc.clone().add(v1.getX(),v1.getZ(),v1.getY());
   
               Vector3D v2 = new Vector3D(-alpha_a,-delta_r).normalize().scalarMultiply(len / 3);
               Location start_loc = ballloc.clone().add(v2.getX(),v2.getZ(),v2.getY());
               
               if((len == 0 || !is_on) && is_running) {
                  Util.safe_lsr_end(lsr, ballloc);
                  Util.safe_lsr_start(lsr,ballloc);
                  //lsr.stop();
               }
               else {
                  Util.safe_lsr_end(lsr,end_loc);
                  Util.safe_lsr_start(lsr,start_loc);
               }
            }
            
            Util.safe_sleep(100);
         }
      }
   }
   
}
