package ru.progrm_jarvis.minecraft.spigot.hologram_manager.util.nms;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.Vector3F;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("unused")
public abstract class AbstractDataWatcherBuilder {

    public abstract Builder builder();
    public abstract Builder builder(WrappedDataWatcher watcher);

    // Types available:
    // Byte, Integer, Float, String, IChatBaseComponent, ItemStack, Optional<IBlockData>, Boolean, Vector3f,
    // BlockPosition, Optional<BlockPosition>, EnumDirection, Optional<UUID>, NBTTagCompound

    public WrappedWatchableObject createWatchable(final int id, final Byte value) {
        return new WrappedWatchableObject(id, value);
    }

    public WrappedWatchableObject createWatchable(final int id, final Integer value) {
        return new WrappedWatchableObject(id, value);
    }

    public WrappedWatchableObject createWatchable(final int id, final Float value) {
        return new WrappedWatchableObject(id, value);
    }

    public WrappedWatchableObject createWatchable(final int id, final String value) {
        return new WrappedWatchableObject(id, value);
    }

    public WrappedWatchableObject createWatchableIChatBaseComponent(final int id, final Object value) {
        return new WrappedWatchableObject(id, value);
    }

    public WrappedWatchableObject createWatchableItemStack(final int id, final Object value) {
        return new WrappedWatchableObject(id, value);
    }

    public WrappedWatchableObject createWatchable(final int id, final ItemStack value) {
        return createWatchableItemStack(id, MinecraftReflection.getMinecraftItemStack(value));
    }

    public WrappedWatchableObject createWatchableOptionalIBlockData(final int id, final Optional<Object> value) {
        return new WrappedWatchableObject(id, value);
    }

    public WrappedWatchableObject createWatchable(final int id, final Boolean value) {
        return new WrappedWatchableObject(id, value);
    }

    public WrappedWatchableObject createWatchable(final int id, final Vector3F value) {
        return createWatchableVector3f(id, vector3FToNms(value));
    }

    public WrappedWatchableObject createWatchableVector3f(final int id, final Object value) {
        return new WrappedWatchableObject(id, value);
    }

    public WrappedWatchableObject createWatchable(final int id, final BlockPosition value) {
        return new WrappedWatchableObject(id, value);
    }

    public WrappedWatchableObject createWatchable(final int id, final Optional<BlockPosition> value) {
        return new WrappedWatchableObject(id, value);
    }

    public WrappedWatchableObject createWatchableEnumDirection(final int id, final Object value) {
        return new WrappedWatchableObject(id, value);
    }

    public WrappedWatchableObject createWatchableOptionalUUID(final int id, final Optional<UUID> value) {
        return new WrappedWatchableObject(id, value);
    }

    public WrappedWatchableObject createWatchableNBTTagCompound(final int id, final Object value) {
        return new WrappedWatchableObject(id, value);
    }

    public WrappedWatchableObject createWatchable(final int id, final Object value) {
        return new WrappedWatchableObject(id, value);
    }

    public Object vector3FFromNms(final Object vector3f) {
        return Vector3F.getConverter().getSpecific(vector3f);
    }

    public Object vector3FToNms(final Vector3F vector3F) {
        return Vector3F.getConverter().getGeneric(Vector3F.getMinecraftClass(), vector3F);
    }

    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    public abstract class Builder {
        @NonNull protected final WrappedDataWatcher dataWatcher;

        protected Builder() {
            this(new WrappedDataWatcher());
        }

        public Builder set(final int id, final Byte value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        public Builder set(final int id, final Integer value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        public Builder set(final int id, final Float value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        public Builder set(final int id, final String value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        public Builder setIChatBaseComponent(final int id, final Object value) {
            dataWatcher.setObject(id, value);

            return this;
        }


        public Builder setItemStack(final int id, final Object value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        public Builder set(final int id, final ItemStack value) {
            dataWatcher.setObject(id, MinecraftReflection.getMinecraftItemStack(value));

            return this;
        }

        public Builder setOptionalIBlockData(final int id, final Optional<Object> value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        public Builder set(final int id, final Boolean value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        public Builder set(final int id, final Vector3F value) {
            return setVector3f(id, vector3FToNms(value));
        }

        public Builder setVector3f(final int id, final Object value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        public Builder set(final int id, final BlockPosition value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        public Builder set(final int id, final Optional<BlockPosition> value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        public Builder setEnumDirection(final int id, final Object value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        public Builder setNBTTagCompound(final int id, final Object value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        public Builder setOptionalUUID(final int id, final Object value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        // should be used if and only if none of default #set(id, value) methods don't provide type given
        public Builder set(final int id, final Object value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        public WrappedDataWatcher build() {
            return dataWatcher;
        }
    }
}
