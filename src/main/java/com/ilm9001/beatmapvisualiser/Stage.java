package com.ilm9001.beatmapvisualiser;

import com.ilm9001.beatmapvisualiser.ShowElements.*;
import com.ilm9001.beatmapvisualiser.Util.Util;

import org.bukkit.Location;

import java.util.List;


public class Stage {
    private final BeatMapVisualiser bmv;
    public ShowLasers lsrs_l;
    public ShowLasers lsrs_r;
    public ShowLasers lsrs_l_alt;
    public ShowLasers lsrs_r_alt;
    public ShowLasers bck_lsrs_r;
    public ShowLasers bck_lsrs_l;
    public ShowBacklights bklts;
    public ShowScreens scrns;
    public ShowBall lsrs_ball;
    public boolean isBuilt;
    public boolean isRunning;

    public Stage(BeatMapVisualiser bmv) {
        this.bmv = bmv;
        lsrs_l = new ShowLasers(bmv, true);
        lsrs_r = new ShowLasers(bmv, false);
        lsrs_l_alt = new ShowLasers(bmv, true);
        lsrs_r_alt = new ShowLasers(bmv, false);

        bck_lsrs_r = new ShowLasers(bmv,false);
        bck_lsrs_l = new ShowLasers(bmv,true);
        
        lsrs_ball = new ShowBall(bmv);

        bklts = new ShowBacklights(bmv);
        //scrns = new ShowScreens(bmv);
        scrns = new ShowScreens(bmv);
    }
    public void Build(double end_time) {
        lsrs_l.Build(get_loc("left_lasers"),0,0,0.615,false,17,-1.2,end_time);
        lsrs_r.Build(get_loc("right_lasers"),0,0,0.615,false,17,-1.2,end_time);
        lsrs_l_alt.Build(get_loc("left_lasers_alt"),2.0,0,0,true,50,-1.2,end_time);
        lsrs_r_alt.Build(get_loc("right_lasers_alt"),-2.0,0,0,true,50,-1.2,end_time);
        bck_lsrs_r.Build(get_loc("back_lasers"),1.75,32,end_time);
        bck_lsrs_l.Build(get_loc("back_lasers"),1.75,32,end_time);
        bklts.Build(get_loc("backlights"),get_loc("backlights_alt"),6,0,0,5);
        scrns.Build(get_loc("screen_center"));
        
        lsrs_ball.Build(get_loc("laser_ball"),32,end_time);
        //scrns.Build(get_loc("screen_center"));
        //scrns.Off();
        isBuilt = true;
        //sch.schedule(new Stage.Dismantle_runnable(), Math.round(end_time*1000.0), TimeUnit.MILLISECONDS);
    }
    public void Run() {
        lsrs_l.Run();
        lsrs_r.Run();
        lsrs_l_alt.Run();
        lsrs_r_alt.Run();
        lsrs_ball.Run();
        bck_lsrs_r.Run();
        bck_lsrs_l.Run();
        bklts.Run();
        scrns.Run();
        scrns.Off();
        isRunning =true;
    }
    public void Stop() {
        scrns.Stop();
        bklts.Stop();
        lsrs_r.Stop();
        lsrs_l.Stop();
        lsrs_ball.Stop();
        bck_lsrs_r.Stop();
        bck_lsrs_l.Stop();
        lsrs_r_alt.Stop();
        lsrs_l_alt.Stop();
        isRunning = false;
    }
    public void Dismantle() {
        Stop();
        Util.safe_sleep(100);
        scrns.Dismantle();
        bklts.Dismantle();
        lsrs_r.Dismantle();
        lsrs_l.Dismantle();
        lsrs_ball.Dismantle();
        bck_lsrs_r.Dismantle();
        bck_lsrs_l.Dismantle();
        lsrs_r_alt.Dismantle();
        lsrs_l_alt.Dismantle();
        isBuilt = false;
    }
    public void setLsrLColor(boolean isBlue) {
        lsrs_l.setColor(isBlue);
        lsrs_l_alt.setColor(isBlue);
    }
    public void setLsrRColor(boolean isBlue) {
        lsrs_r.setColor(isBlue);
        lsrs_r_alt.setColor(isBlue);
    }
    public void lsrLOff() {
        lsrs_l.Off();
        lsrs_l_alt.Off();
    }
    public void lsrROff() {
        lsrs_r.Off();
        lsrs_r_alt.Off();
    }

    // Not moving this to util class since we need bmv here
    // -- and this is not called from any other class
    private Location get_loc(String name) {
        List<Double> coords = bmv.getConfig().getDoubleList(name);
        return new Location(bmv.main_world, coords.get(0), coords.get(1), coords.get(2));
    }
}