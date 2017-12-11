package ru.progrm_jarvis.minecraft.spigot.hologram_manager.util.nms;

import lombok.*;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;

@NoArgsConstructor(access = AccessLevel.NONE)
public class NmsManager {
    ///////////////////////////////////////////////////////////////////////////
    // EntityIDs
    ///////////////////////////////////////////////////////////////////////////

    private static final Field entityCountField;
    @Getter private static final NmsVersion nmsVersion;

    static {
        nmsVersion = NmsVersion.get();

        try {
            entityCountField = Class.forName("net.minecraft.server." + nmsVersion.name + ".Entity")
                    .getDeclaredField("entityCount");
        } catch (NoSuchFieldException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static int nextEntityId() {
        val accessible = entityCountField.isAccessible();
        entityCountField.setAccessible(true);

        try {
            val nextId = (int) entityCountField.get(null);
            entityCountField.set(null, nextId + 1);
            entityCountField.setAccessible(accessible);

            return nextId;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Value
    @RequiredArgsConstructor(access = AccessLevel.NONE)
    public static final class NmsVersion {
        @NonNull
        private final String name;
        private final short generation;

        private NmsVersion(final String name) {
            if (name == null) throw new NullPointerException("name");

            this.name = name;
            generation = Short.parseShort(name.substring(3, name.indexOf('_', 4)));
        }

        public static NmsVersion get() {
            val craftbukkitPackage = Bukkit.getServer().getClass().getPackage().getName();
            return new NmsVersion(craftbukkitPackage.substring(craftbukkitPackage.lastIndexOf(".") + 1));
        }
    }
}
