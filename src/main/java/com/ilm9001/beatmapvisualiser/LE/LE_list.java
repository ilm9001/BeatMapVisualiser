package com.ilm9001.beatmapvisualiser.LE;

import org.json.simple.JSONObject;
import java.util.ArrayList;

// This class is for storing a list of LE objects
// to be iterated over later in a for loop.

// Here we have to duplicate the size() and len() methods of ArrayList,
// and there is probably a more eloquent way of doing this.
public class LE_list {
    private final ArrayList<LE> lst;

    // constructor
    public LE_list() {
        lst = new ArrayList<>();
    }

    public void add(JSONObject eve, int bpm) {
        LE new_ev = new LE(eve, bpm);
        lst.add(new_ev);
    }
    public int size() { return lst.size(); }
    public LE get(int index) {
        return lst.get(index);
    }
}