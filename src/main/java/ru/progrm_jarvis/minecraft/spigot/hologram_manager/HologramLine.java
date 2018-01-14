package ru.progrm_jarvis.minecraft.spigot.hologram_manager;

import lombok.*;
import org.bukkit.Location;
import ru.progrm_jarvis.minecraft.spigot.hologram_manager.util.nms.NmsManager;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class HologramLine {
    private final int id;
    private final String text;
    @NonNull @Setter private Location location;

    public HologramLine(final String text, final Location location) {
        this(NmsManager.nextEntityId(), text, location);
    }
}
