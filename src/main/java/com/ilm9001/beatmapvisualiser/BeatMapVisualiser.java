package com.ilm9001.beatmapvisualiser;

import com.ilm9001.beatmapvisualiser.Commands.PlayCommandTabComplete;
import com.ilm9001.beatmapvisualiser.LE.LE_list;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.CustomChart;
import org.bstats.json.JsonObjectBuilder;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import org.jetbrains.annotations.NotNull;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;


public final class BeatMapVisualiser extends JavaPlugin {
    public World main_world;
    public BeatMapVisualiser bmv;
    public Show show;
    private Stage stg;

    @Override
    public void onEnable() {
        // Called when server enables plugin

        // We save the pointer to this main plugin object since it needed basically everywhere downstream:
        // Logging, Lasers, almost everything needs the plugin pointer to work.
        bmv = this;
        this.saveDefaultConfig();
        main_world = this.getServer().getWorld("world");
        assert main_world != null;

        if (stg == null) {
            stg = new Stage(bmv);
            stg.Build(-1);
        }
        if(show == null) {
            show = new Show(bmv, stg);
        }
    
        Metrics metrics = new Metrics(this, 12300);
        
        // create the play command hook + autocompleter
        this.getCommand("playbeatmap").setExecutor(new PlayCommand());
        this.getCommand("playbeatmap").setTabCompleter(new PlayCommandTabComplete());
    }

    @Override
    public void onDisable() {
        // Called when server disables plugin, or shuts down.
        stg.Dismantle();
    }

    public class PlayCommand implements CommandExecutor {
        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
            if(sender.hasPermission("beatmapvisualiser.playbeatmap")) {
                Player pl = (Player) sender;
                final String bfile = args[0];
    
                if (bfile.contains("/")) {
                    pl.sendMessage(ChatColor.RED + "Folder name must not contain /");
                    return false;
                }
    
                // get specified path
                String df = getDataFolder().getAbsolutePath();
                String bfile_a = df + "/" + bfile + "/" + bfile + ".dat";
    
                String info = df + "/" + bfile + "/" + "info.dat";
    
                JSONParser jsonParser = new JSONParser();
                JSONObject json_o;
                JSONObject json_info;
    
                // parse beatmap from path
                try {
                    FileReader reader = new FileReader(bfile_a);
                    FileReader inforeader = new FileReader(info);
        
                    json_o = (JSONObject) jsonParser.parse(reader);
                    json_info = (JSONObject) jsonParser.parse(inforeader);
                } catch (IOException e) {
                    pl.sendMessage(ChatColor.RED + "Could not read beatmap: " + bfile);
                    e.printStackTrace();
                    return false;
                } catch (ParseException e) {
                    pl.sendMessage(ChatColor.RED + "Could not parse beatmap: " + bfile);
                    e.printStackTrace();
                    return false;
                }
                if (json_o == null) {
                    pl.sendMessage(ChatColor.RED + "Invalid beatmap: no json?.");
                    return false;
                }
                JSONArray json_ev_list = (JSONArray) json_o.get("_events");
                Object bpm = json_info.get("_beatsPerMinute");
    
                Object artistName = json_info.get("_songAuthorName");
                Object songName = json_info.get("_songName");
    
                Object mapperName = json_info.get("_levelAuthorName");
    
                double bpmVal;
                if (bpm instanceof Long) {
                    bpmVal = ((Long) bpm).doubleValue();
                } else if (bpm instanceof Double) {
                    bpmVal = (Double) bpm;
                } else {
                    bpmVal = (double) bpm;
                }
    
                if (json_ev_list == null) {
                    pl.sendMessage(ChatColor.RED + "Invalid beatmap: no events.");
                    return false;
                }
                // Iterate over event array, call method parseEvent for each element on list
                LE_list le_list = new LE_list();
                //noinspection unchecked
                json_ev_list.forEach(eve -> le_list.add((JSONObject) eve, (int) bpmVal));
    
                final int le_size = le_list.size();
                long last_time = 0;
                int failcount = 0;
                int n_ev = 0;
                // We check that time of events is always increasing.
                for (int i = 0; i < le_size; ++i) {
                    ++n_ev;
                    long t = le_list.get(i).time_ms;
                    if (t >= last_time) {
                        last_time = t;
                    } else {
                        ++failcount;
                    }
                }
                bmv.getLogger().info(String.format("Beatmap %s bpm %f len %d, failcount: %d, last time: %.1f s",
                        bfile_a, bpmVal, le_size, failcount, last_time / 1000.0));
    
                if (n_ev == 0) {
                    bmv.getLogger().info("Beatmap with zero events?");
                    pl.sendMessage(ChatColor.RED + "beatmap with zero events?");
                    return false;
                }
                if (failcount > 0) {
                    // Somewhere in the beatmap time runs backwards.
                    bmv.getLogger().info("Beatmap is invalid, time runs backwards somewhere in it.");
                    pl.sendMessage(ChatColor.RED + "Backward time flow detected in beatmap: " + bfile);
                    return false;
                }
    
                // New scheduling method!
                if (show.Run(le_list, last_time / 1000.0)) {
                    Player[] players = getServer().getOnlinePlayers().toArray(new Player[0]);
                    for (Player plr : players) {
                        plr.playSound(plr.getLocation(), "minecraft:" + bfile, 1, 1);
                        //theres probably a much better way to send these to the players
                        plr.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "BMVPlayer" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE + "Now playing " + ChatColor.AQUA + artistName + ChatColor.WHITE + " - " + ChatColor.AQUA + songName);
                        plr.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "BMVPlayer" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE + "Mapped by " + ChatColor.AQUA + mapperName);
                    }
                    return true;
                } else {
                    bmv.getLogger().info("Beatmap playback active, cannot start new one.");
                    pl.sendMessage(ChatColor.RED + "Beatmap playback active, cannot start new one.");
                    return false;
                }
            } else {
                return false;
            }
        }
    }
}