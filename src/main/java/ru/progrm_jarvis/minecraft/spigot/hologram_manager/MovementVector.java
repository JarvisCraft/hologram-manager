package ru.progrm_jarvis.minecraft.spigot.hologram_manager;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.Vector;

@NoArgsConstructor
@SerializableAs("MovementVector")
@SuppressWarnings("unused")
public class MovementVector extends Vector {
    @Getter private boolean small;

    public MovementVector(final int x, final int y, final int z) {
        super(x, y, z);
        checkIfSmall();
    }

    public MovementVector(final double x, final double y, final double z) {
        super(x, y, z);
        checkIfSmall();
    }

    public MovementVector(final float x, final float y, final float z) {
        super(x, y, z);
        checkIfSmall();
    }

    public static MovementVector fromVector(final Vector vector) {
        return new MovementVector(vector.getX(), vector.getY(), vector.getZ());
    }

    public static MovementVector from2Locations(final Location from, final Location to) {
        return new MovementVector(to.getX() - from.getX(), to.getY() - from.getY(), to.getZ() - from.getZ());
    }

    public boolean isZero() {
        return x == 0 && y == 0 && z == 0;
    }

    public MovementVector checkIfSmall() {
        small = x <= 8 && y <= 8 && z <= 8;
        return this;
    }
}
