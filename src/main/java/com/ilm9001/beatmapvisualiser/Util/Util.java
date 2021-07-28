package com.ilm9001.beatmapvisualiser.Util;

import org.bukkit.Location;


public final class Util {
    public static void safe_sleep(long t) {
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void safe_lsr_end(Laser lsr, Location end_loc) {
        try {
            lsr.moveEnd(end_loc);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
    public static void safe_lsr_start(Laser lsr, Location start_loc) {
        try {
            lsr.moveStart(start_loc);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
    public static void safe_lsr_colorchange(Laser lsr) {
        try {
            lsr.callColorChange();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
}
// EOF
