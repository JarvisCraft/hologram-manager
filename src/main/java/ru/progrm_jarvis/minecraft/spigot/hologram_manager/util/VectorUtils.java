package ru.progrm_jarvis.minecraft.spigot.hologram_manager.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.util.Vector;

@UtilityClass
public class VectorUtils {
    public static Vector directionFrom2Locations(final Location from, final Location to) {
        return new Vector(to.getX() - from.getX(), to.getY() - from.getY(), to.getZ() - from.getZ());
    }
}
