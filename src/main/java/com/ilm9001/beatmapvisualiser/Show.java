package com.ilm9001.beatmapvisualiser;

import com.ilm9001.beatmapvisualiser.LE.LE;
import com.ilm9001.beatmapvisualiser.LE.LE_list;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Show {
    private final BeatMapVisualiser bmv;
    private final Stage stg;
    private final ScheduledExecutorService sch;
    private boolean is_running;
    public boolean ringZoomed;

    public Show(BeatMapVisualiser jp, Stage stg) {
        bmv = jp;
        this.stg = stg;
        sch = Executors.newScheduledThreadPool(1);
        is_running = false;
    }


    private void ev_handle(int ty, int va) {
        // Switch case hell
        switch (ty) {
            // Back lasers
            case 0:
                switch (va) {
                    case 0:
                        stg.bck_lsrs_r.Off();
                        stg.bck_lsrs_l.Off();
                        break;
                    case 1:
                    case 5:
                        stg.bck_lsrs_r.On();
                        stg.bck_lsrs_l.On();
                        break;
                    case 2:
                    case 6:
                        stg.bck_lsrs_r.Flash();
                        stg.bck_lsrs_l.Flash();
                        break;
                    case 3:
                    case 7:
                        stg.bck_lsrs_r.FlashOff();
                        stg.bck_lsrs_l.FlashOff();
                        break;
                    default:
                        break;
                }
                break;
            case 1:
                switch (va) {
                    case 0:
                        stg.scrns.Off();
                        stg.lsrs_ball.Off();
                        break;
                    case 1:
                    case 5:
                        stg.scrns.On();
                        stg.lsrs_ball.On();
                        break;
                    case 2:
                    case 6:
                        stg.scrns.Flash();
                        stg.lsrs_ball.Flash();
                        break;
                    case 3:
                    case 7:
                        stg.scrns.FadeOff(2000);
                        stg.lsrs_ball.FlashOff();
                        //stg.scrns.FlashOff();
                        break;
                    default:
                        break;
                }
                break;
            // Left rotating lasers
            case 2:
                switch (va) {
                    case 0:
                        stg.lsrLOff();
                        break;
                    case 1:
                        stg.setLsrLColor(true);
                        stg.lsrs_l.On();
                        stg.lsrs_l_alt.Off();
                        break;
                    case 5:
                        stg.setLsrLColor(false);
                        stg.lsrs_l_alt.On();
                        stg.lsrs_l.Off();
                        break;
                    case 2:
                        stg.setLsrLColor(true);
                        stg.lsrs_l.Flash();
                        stg.lsrs_l_alt.Off();
                        break;
                    case 6:
                        stg.setLsrLColor(false);
                        stg.lsrs_l_alt.Flash();
                        stg.lsrs_l.Off();
                        break;
                    case 3:
                        stg.setLsrLColor(true);
                        stg.lsrs_l.FlashOff();
                        stg.lsrs_l_alt.Off();
                        break;
                    case 7:
                        stg.setLsrLColor(false);
                        stg.lsrs_l_alt.FlashOff();
                        stg.lsrs_l.Off();
                        break;
                    default:
                        break;
                }
                break;
            // Right rotating lasers
            case 3:
                switch (va) {
                    case 0:
                        stg.lsrROff();
                        break;
                    case 1:
                        stg.setLsrRColor(true);
                        stg.lsrs_r.On();
                        stg.lsrs_r_alt.Off();
                        break;
                    case 5:
                        stg.setLsrRColor(false);
                        stg.lsrs_r_alt.On();
                        stg.lsrs_r.Off();
                        break;
                    case 2:
                        stg.setLsrRColor(true);
                        stg.lsrs_r.Flash();
                        stg.lsrs_r_alt.Off();
                        break;
                    case 6:
                        stg.setLsrRColor(false);
                        stg.lsrs_r_alt.Flash();
                        stg.lsrs_r.Off();
                        break;
                    case 3:
                        stg.setLsrRColor(true);
                        stg.lsrs_r.FlashOff();
                        stg.lsrs_r_alt.Off();
                        break;
                    case 7:
                        stg.setLsrRColor(false);
                        stg.lsrs_r_alt.FlashOff();
                        stg.lsrs_r.Off();
                        break;
                    default:
                        break;
                }
                break;
            // Center lights
            case 4:
                switch (va) {
                    case 0:
                        stg.bklts.Off();
                        break;
                    case 1:
                        stg.bklts.setColor(true);
                        stg.bklts.On();
                        break;
                    case 5:
                        stg.bklts.setColor(false);
                        stg.bklts.On();
                        break;
                    case 2:
                        stg.bklts.setColor(true);
                        stg.bklts.Flash();
                        break;
                    case 6:
                        stg.bklts.setColor(false);
                        stg.bklts.Flash();
                        break;
                    case 3:
                        stg.bklts.setColor(true);
                        stg.bklts.FlashOff();
                        break;
                    case 7:
                        stg.bklts.setColor(false);
                        stg.bklts.FlashOff();
                        break;
                    default:
                        break;
                }
                break;
            // Creates one ring spin in the environment. Value irrelevant.
            case 8:
                stg.lsrs_ball.addSpin();
                break;
            // Controls zoom for applicable rings. Value irrelevant.
            case 9:
                ringZoomed = !ringZoomed;
                stg.lsrs_l.setRingZoomed(ringZoomed);
                stg.lsrs_r.setRingZoomed(ringZoomed);
                stg.lsrs_l_alt.setRingZoomed(ringZoomed);
                stg.lsrs_r_alt.setRingZoomed(ringZoomed);
                stg.lsrs_ball.setRingZoomed(ringZoomed);
                break;
            // Rotation speed for applicable lights in Left Rotating Lasers
            case 12:
                stg.lsrs_l.SetStep(va);
                stg.lsrs_l_alt.SetStep(va);
                stg.bck_lsrs_l.SetStep(va/2);
                stg.lsrs_ball.SetStep(va);
                break;
            // Rotation speed for applicable lights in Right Rotating Lasers
            case 13:
                stg.lsrs_r.SetStep(va);
                stg.lsrs_r_alt.SetStep(va);
                stg.bck_lsrs_r.SetStep(va/2);
                stg.lsrs_ball.SetStep(va);
                break;
            default:
                // should we complain to log?
                break;
        }
    }

    public boolean Run(LE_list ev_list, double end_time)
    {
        if (is_running) {
            return false;
        }
        is_running = true;
        sch.schedule(new Show_runnable(ev_list, end_time), 0, TimeUnit.MILLISECONDS);
        return true;
    }

    public class Show_runnable implements Runnable {
        private final int le_size;
        private final LE_list ev_list;
        private final double end_time;

        public Show_runnable(LE_list ev_list, double end_time) {
            this.ev_list = ev_list;
            this.le_size = ev_list.size();
            this.end_time = end_time;
        }
        @Override
        public void run() {
            //if(!stg.isBuilt) stg.Build(end_time);
            stg.Run();
            long t_start = System.currentTimeMillis();
            for (int i=0; i < le_size; ++i) {
                LE ev = ev_list.get(i);
                long from_start = System.currentTimeMillis() - t_start;
                long delay = ev.time_ms - from_start;
                if (delay < 0) {
                    // Negative delay, we are late!
                    if (delay < -1) {
                        // do not complain about lateness unless it is more than 1ms!
                        bmv.getLogger().info(String.format("Can't keep up at %.1fs delay=%d",
                            ev.time_ms/1000.0, delay));
                    }
                }
                else if (0 < delay && delay < 20) {
                    bmv.getLogger().info(String.format("Quite busy at %.1f", ev.time_ms/1000.0));
                }
                else if (delay > 1) {
                    // Now sleeping for "delay" milliseconds for exact timing of event
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // now fire the event, after possible delay
                ev_handle(ev.type, ev.value);
            }
            stg.Stop();
            is_running = false;
        }
    }
}