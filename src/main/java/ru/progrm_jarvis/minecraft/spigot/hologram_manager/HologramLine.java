package ru.progrm_jarvis.minecraft.spigot.hologram_manager;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Location;

@AllArgsConstructor
@Getter
public class HologramLine {
    @NonNull private final String text;
    @NonNull private final Integer id;
    @NonNull @Setter private Location location;
}
