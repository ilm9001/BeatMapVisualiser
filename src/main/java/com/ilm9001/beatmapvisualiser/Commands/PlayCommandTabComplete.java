package com.ilm9001.beatmapvisualiser.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
//Autocomplete class for /playbeatmap
public class PlayCommandTabComplete implements TabCompleter {
   
   @Nullable
   @Override
   public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
      if(commandSender.hasPermission("beatmapvisualiser.playbeatmap")) {
         String df = Bukkit.getPluginManager().getPlugin("BeatMapVisualiser").getDataFolder().getAbsolutePath();
         File[] directories = new File(df).listFiles(File::isDirectory); //adds all subdirectories in plugin datafolder path to a File array
         List<String> list = new ArrayList<>();
         for (File file : directories) {
            list.add(file.getName());
         }
         return list;
      }
      return null;
   }
}
