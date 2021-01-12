package com.emptyirony.pakeviewer.packviewer;

import com.emptyirony.pakeviewer.packviewer.listener.PlayerListener;
import lombok.Getter;
import net.jitse.npclib.NPCLib;
import net.jitse.npclib.hologram.Hologram;
import net.jitse.npclib.internal.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import team.shiva.core.util.CC;
import team.shiva.core.util.LocationUtil;
import team.shiva.shivalib.phoenix.lang.file.type.BasicConfigurationFile;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class PackViewer extends JavaPlugin {
    @Getter
    private static PackViewer ins;

    private NPCLib npcLib;
    private Hologram hologram;
    private BasicConfigurationFile mainConfig;

    public Location npcLocation;
    double minX;
    double maxX;
    double minY;
    double maxY;
    double minZ;
    double maxZ;
    int cooldown;

    @Override
    public void onEnable() {
        ins = this;
        npcLib = new NPCLib(this);
        this.saveDefaultConfig();
        mainConfig = new BasicConfigurationFile(this, "config");

        this.getServer().getPluginManager().registerEvents(new PlayerListener(this),this);
        // Plugin startup logic

        List<String> text = new ArrayList<>();
        text.add(CC.translate(mainConfig.getString("hologram.name")));
        Location location = LocationUtil.deserialize(mainConfig.getString("hologram.location"));
        if(location == null){
            location = Bukkit.getWorld("world").getSpawnLocation();
        }
        hologram = new Hologram(MinecraftVersion.V1_8_R3, location, text);
        npcLocation = LocationUtil.deserialize(mainConfig.getString("npc.location"));
        if(npcLocation == null){
            npcLocation = Bukkit.getWorld("world").getSpawnLocation();
        }
        minX = mainConfig.getDouble("range.minx");
        maxX = mainConfig.getDouble("range.maxx");
        minY = mainConfig.getDouble("range.miny");
        maxY = mainConfig.getDouble("range.maxy");
        minZ = mainConfig.getDouble("range.minz");
        maxZ = mainConfig.getDouble("range.maxz");
        cooldown = mainConfig.getInteger("cooldown");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
