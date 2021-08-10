# BeatMapVisualiser

Music visualiser *plugin* for minecraft which uses beat saber beatmaps for various visuals as well as a resource pack for streaming music for players

Showcase videos: 
https://streamable.com/zljpry
https://streamable.com/c7b636
https://streamable.com/tnhdy5
# KEY CAVEATS

THIS PLUGIN USES A RESOURCE PACK FOR THE SOUND. 
- There are some issues with the backlights groups coordinates, basically theres a issue of a weird offset when spawning them. 
- A LOT of the plugin is hardcoded for my specific usecase, if you want to have less or more lasers for example you have to edit the code and compile another plugin at least right now. (Hardcoded to be "facing" east.)
- Viewing the amount of lasers this plugin produces is probably resource-intensive so this will not run on your grandmas computer from 1998. 
- A lot of guardian beam packets for movement and such are being practically spammed to the player every 100ms per laser, this may cause ping issues in certain cases but ive tested this with others and it should be fine for a good-average internet connection.

Might lag your server when running, though doesnt for me. Wont otherwise. 

# SETUP


IMPORTANT: Server resource packs only support packs up to 100mb in size!! If you want to have a resource pack with more than 100mb of music files, you will have to convince all the players on your server to download and apply a resource pack seperately!

[WIP] but general idea:
Download the provided resource pack which will include the screen models and textures, due to legal reasons however, i *probably* cant put any actual music files on it and distribute it.

Pick your songs that you want to visualise from bsaber.com and download them to your computer, preferrably into a seperate folder on your desktop or something.

Unzip the songs, rename all the files with an ".egg" extension to ".ogg" aswell as rename them to something you will remember, such as the song name (Example, song name is "Rogue - Barbed Wire" and the song file is "song.egg", rename the "song.egg" to "barbedwire.ogg")

Place them into the resource packs "sounds" folder (filepath: pack\assets\minecraft\sounds). You can also create a custom folder in the \sounds\ folder such as \sounds\music\ but it will complicate the next step.

Update the sounds.json file in \pack\assets\minecraft\ to include your songs with the names of the files you renamed them to, for example:

```
{
  "barbedwire": {
    "sounds": [
      {
        "name": "barbedwire",
        "stream": true,
        "attenuation_distance": 16
      }
    ],
    "subtitle": "Rogue - Barbed Wire"
  },
  "whosgotyourlove": {
    "sounds": [
      {
        "name": "whosgotyourlove",
        "stream": true,
        "attenuation_distance": 16
      }
    ],
    "subtitle": "Stonebank - Who's Got Your Love"
  } 
}
```
(further reading: https://minecraft.fandom.com/wiki/Sounds.json)

Then, assuming you havent hit the 100mb limit, you can zip up the resource pack (Remember to zip it so that you dont have an extra folder when zipped, aka no pack.zip\pack\assets\, it should directly be pack.zip\assets\) and upload it to a service like https://mc-packs.net/ and then follow the instructions on what to put in your server.properties

After thats done, move on to the server side. First i recommend stopping the server, putting the plugin in the plugins folder, starting the server and letting it generate the config, then stopping the server.

Now move the coordinates in the config to your desired locations (this is a process of trial and error, but the default locations should give you a indication of the correct spacings between "show elements")

Then make folders for all your songs WITH THE EXACT SAME NAMES as in sounds.json such as \plugins\BeatMapVisualiser\barbedwire\ or \plugins\BeatMapVisualiser\whosgotyourlove\
Place your info.dat file and beatmap file (usually named by difficulty, such as expertplus.dat, expert.dat etc) and rename the beatmap file to the name of your folder (again, barbedwire.dat or whosgotyourlove.dat)
(also rename your info.dat file to ``info.dat`` specifically if it has any capital letters, not sure if this causes issues or not though)

You are now safe to start your server. If you have the permission to use the command, you can now do /playbeatmap <songname> (<songname>, again, is the same as in your sounds.json or BeatMapVisualiser folder) and watch the show!

# Troubleshooting

If you have any issues, please contact me on the discord (https://discord.gg/3tFg7RPvJr) and ill try to help if possible, but read all of this first and ensure youre doing it properly before asking for any help.
  
# Credits
  
This plugins uses SkytAsul's super useful GuardianBeam resource, available here: https://github.com/SkytAsul/GuardianBeam

This plugin also uses bStats: https://bstats.org/plugin/bukkit/BeatMapVisualiser/12300
