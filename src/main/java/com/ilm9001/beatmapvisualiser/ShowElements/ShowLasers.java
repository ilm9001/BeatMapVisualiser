package com.ilm9001.beatmapvisualiser.ShowElements;

import com.ilm9001.beatmapvisualiser.BeatMapVisualiser;
import com.ilm9001.beatmapvisualiser.Util.Laser;
import com.ilm9001.beatmapvisualiser.Util.Util;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ShowLasers implements ShowControl {
    private final BeatMapVisualiser bmv;
    private final ScheduledExecutorService sch;
    private final int sign;

    // Tunables
    private int NUM_LSR = 40;
    private final double INITIAL_A = 120.0;
    private final double STEP_A = 2.0;
    private double INITIAL_DELTA_A = -1;
    private double DELTA_A = INITIAL_DELTA_A;
    private double INITIAL_LEN = 40.0;
    private final double LEN_MIN = 0.0;
    private double LEN_MAX = INITIAL_LEN+30;

    // Internal runtime variables
    private final ArrayList<Laser> lsrs;
    private boolean is_running;
    private double step;
    private double len;
    private double len_d;
    private double a;
    private double OFFSETX;
    private double OFFSETY;
    private double OFFSETZ;
    private boolean is_on;
    private boolean ringZoomed;
    private boolean isAlt;
    private boolean isVertical;
    private double alpha_a;

    public ShowLasers(BeatMapVisualiser bmv, boolean mirror) {
        this.bmv = bmv;
        sch = Executors.newScheduledThreadPool(1);
        if (mirror) { sign = -1; } else { sign = 1; }

        lsrs = new ArrayList<>();
        is_running = false;
        step = 1;
        len = INITIAL_LEN;
        len_d = 0.0;
        a = sign * INITIAL_A;
    }

    public void Build(Location loc, double offsetX, double offsetY, double offsetZ, boolean isAlt, int num_lsr, double delta, double end_time) {
        OFFSETX = offsetX;
        OFFSETY = offsetY;
        OFFSETZ = offsetZ;
        this.isAlt = isAlt;
        NUM_LSR = num_lsr;
        INITIAL_DELTA_A = delta;
        DELTA_A = INITIAL_DELTA_A;

        try {
            for (int i=0; i < NUM_LSR; ++i) {
                Location lloc = loc.clone().subtract(sign * i*OFFSETX, i*OFFSETY, sign * i*OFFSETZ);
                Laser lsr = new Laser(lloc, lloc, (int) end_time, 512);
                lsr.start(bmv);
                lsrs.add(lsr);
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        if(!isAlt) {
            INITIAL_LEN = 50.0;
            LEN_MAX = INITIAL_LEN+70.0;
        } else {
            INITIAL_LEN = 40.0;
            LEN_MAX = INITIAL_LEN + 30;
        }
        // bmv.getLogger().info(String.format("Built lsrs %s", lsrs.toString()));
    }
    public void Build(Location middlepoint, double offsetY, int num_lsr, double end_time) {
        OFFSETY = offsetY;
        isVertical=true;
        NUM_LSR = num_lsr;
        INITIAL_DELTA_A = -((360*1.3)/NUM_LSR);
        //INITIAL_DELTA_A = 22.5;
        DELTA_A = INITIAL_DELTA_A;

        INITIAL_LEN = 25.0;
        LEN_MAX = INITIAL_LEN+30.0;

        Location loc = middlepoint.clone().add(0,0,sign*23);
        step = 1;

        try {
            for (int i=0; i < NUM_LSR; ++i) {
                Location lloc = loc.clone().add(0, i*OFFSETY, 0);
                Laser lsr = new Laser(lloc, lloc, (int) Math.round(end_time), 512);
                lsr.start(bmv);
                lsrs.add(lsr);
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        // bmv.getLogger().info(String.format("Built lsrs %s", lsrs.toString()));
    }

    public void setRingZoomed(boolean zoom) {
        ringZoomed = zoom;
    }

    public void setColor(boolean isBlue) {
    }

    @Override
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
        sch.schedule(new Lasers_runnable(), 0, TimeUnit.MILLISECONDS);
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
    }
    @Override
    public void Off() {
        len = LEN_MIN;
        len_d = 0.0;
        is_on = false;
        len = 0;
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
        // step is changed!
        if (step == this.step) {
            // if step is the same as before "reset" the lasers
            // https://bsmg.wiki/mapping/map-format.html#events-2
            a += sign * 40;
        }

        this.step = step;
        if (step == 0 && !isVertical) {
            a += sign * 40;
        }
        if(isVertical && step == 0) {
            this.step = 1;
        }
    }



    private class Lasers_runnable implements Runnable {
        @Override
        public void run() {
            while (true) {
                if (!is_running) {
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

                double astep = sign * STEP_A * step;
                a += astep;
                a = a % 360.0;
    
                if (step == 0) {
                    a = 45;
                    DELTA_A = 0;
                    alpha_a = sign * ((2.0 * Math.PI * 45) /360);
                } else {
                    DELTA_A = INITIAL_DELTA_A;
                    alpha_a = 0;
                }
    
                double a_n = a;
                for (Laser lsr : lsrs) {
                    if(!lsr.isStarted() && is_on && is_running && len > 0) {
                        lsr.start(bmv);
                    }

                    double delta_r = (2.0 * Math.PI * a_n) / 360.0;
                    Vector3D v1;
                    
                    // <HELL>
                    if(!isVertical) {
                        v1 = new Vector3D(alpha_a, delta_r).normalize().scalarMultiply(len / 3);
                    } else {
                        v1 = new Vector3D(delta_r, 0).normalize().scalarMultiply(len / 3);
                    }
                    Location end_loc;
                    if(isVertical) {
                        end_loc = lsr.getStart().clone().add(v1.getX(),v1.getZ(),v1.getY());
                    } else {
                        if (!isAlt) {
                            if (!ringZoomed) {
                                end_loc = lsr.getStart().clone().add(v1.getX(), v1.getZ() - (len / 20.0), v1.getY() + len);
                            } else {
                                end_loc = lsr.getStart().clone().add(0, v1.getZ() - (len / 20.0), len);
                            }
                        } else {
                            if (!ringZoomed) {
                                end_loc = lsr.getStart().clone().subtract(v1.getX() + sign * len, v1.getZ() - (len / 32.0), v1.getY() + sign * len);
                            } else {
                                end_loc = lsr.getStart().clone().subtract(0, v1.getZ() - (len / 32.0), sign * len);
                            }
                        }
                    }
                    //</HELL>
                    
                    if((len == 0 || !is_on) && is_running && lsr.isStarted() ) {
                        //lsr.stop(); // Doesn't work, everything seems to break whenever a laser is "restarted"
                        Util.safe_lsr_end(lsr,lsr.getStart());
                    }
                    else {
                        Util.safe_lsr_end(lsr,end_loc);
                    }

                    // separate individual lasers by ANGLE_DELTA degrees
                    a_n += sign * DELTA_A;
                }

                Util.safe_sleep(100);
            }
        }
    }
}
// EOF
