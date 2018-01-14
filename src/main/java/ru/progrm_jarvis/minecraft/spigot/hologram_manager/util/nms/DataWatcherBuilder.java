package ru.progrm_jarvis.minecraft.spigot.hologram_manager.util.nms;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.UUID;

public class DataWatcherBuilder extends AbstractDataWatcherBuilder {
    public final WrappedDataWatcher.Serializer BYTE_SERIALIZER = WrappedDataWatcher.Registry.get(Byte.class);
    public final WrappedDataWatcher.Serializer INTEGER_SERIALIZER = WrappedDataWatcher.Registry.get(Integer.class);
    public final WrappedDataWatcher.Serializer FLOAT_SERIALIZER = WrappedDataWatcher.Registry.get(Float.class);
    public final WrappedDataWatcher.Serializer STRING_SERIALIZER = WrappedDataWatcher.Registry.get(String.class);
    public final WrappedDataWatcher.Serializer I_CHAT_BASE_COMPONENT_SERIALIZER = WrappedDataWatcher.Registry
            .getChatComponentSerializer();
    public final WrappedDataWatcher.Serializer ITEM_STACK_SERIALIZER = WrappedDataWatcher.Registry
            .getItemStackSerializer(false);
    public final WrappedDataWatcher.Serializer OPTIONAL_I_BLOCK_DATA_SERIALIZER = WrappedDataWatcher.Registry
            .getBlockDataSerializer(true);
    public final WrappedDataWatcher.Serializer BOOLEAN_SERIALIZER= WrappedDataWatcher.Registry.get(Boolean.class);
    public final WrappedDataWatcher.Serializer VECTOR_3F_SERIALIZER = WrappedDataWatcher.Registry.getVectorSerializer();
    public final WrappedDataWatcher.Serializer BLOCK_POSITION_SERIALIZER = WrappedDataWatcher.Registry
            .getBlockPositionSerializer(false);
    public final WrappedDataWatcher.Serializer OPTIONAL_BLOCK_POSITION_SERIALIZER = WrappedDataWatcher.Registry
            .getBlockPositionSerializer(true);
    public final WrappedDataWatcher.Serializer ENUM_DIRECTION_SERIALIZER = WrappedDataWatcher.Registry
            .getDirectionSerializer();
    public final WrappedDataWatcher.Serializer OPTIONAL_UUID_SERIALIZER = WrappedDataWatcher.Registry
            .getUUIDSerializer(true);
    public final WrappedDataWatcher.Serializer NBT_TAG_COMPOUND_SERIALIZER = WrappedDataWatcher.Registry
            .getNBTCompoundSerializer();
    public final WrappedDataWatcher.Serializer OBJECT_SERIALIZER = WrappedDataWatcher.Registry.get(Object.class);

    public WrappedDataWatcher.WrappedDataWatcherObject watcherObjectByte(final int id) {
        return new WrappedDataWatcher.WrappedDataWatcherObject(id, BYTE_SERIALIZER);
    }

    public WrappedDataWatcher.WrappedDataWatcherObject watcherObjectInteger(final int id) {
        return new WrappedDataWatcher.WrappedDataWatcherObject(id, INTEGER_SERIALIZER);
    }

    public WrappedDataWatcher.WrappedDataWatcherObject watcherObjectFloat(final int id) {
        return new WrappedDataWatcher.WrappedDataWatcherObject(id, FLOAT_SERIALIZER);
    }

    public WrappedDataWatcher.WrappedDataWatcherObject watcherObjectString(final int id) {
        return new WrappedDataWatcher.WrappedDataWatcherObject(id, STRING_SERIALIZER);
    }

    public WrappedDataWatcher.WrappedDataWatcherObject watcherObjectIChatBaseComponent(final int id) {
        return new WrappedDataWatcher.WrappedDataWatcherObject(id, I_CHAT_BASE_COMPONENT_SERIALIZER);
    }

    public WrappedDataWatcher.WrappedDataWatcherObject watcherObjectItemStack(final int id) {
        return new WrappedDataWatcher.WrappedDataWatcherObject(id, ITEM_STACK_SERIALIZER);
    }

    public WrappedDataWatcher.WrappedDataWatcherObject watcherObjectOptionalIBlockData(final int id) {
        return new WrappedDataWatcher.WrappedDataWatcherObject(id, OPTIONAL_I_BLOCK_DATA_SERIALIZER);
    }

    public WrappedDataWatcher.WrappedDataWatcherObject watcherObjectBoolean(final int id) {
        return new WrappedDataWatcher.WrappedDataWatcherObject(id, BOOLEAN_SERIALIZER);
    }

    public WrappedDataWatcher.WrappedDataWatcherObject watcherObjectVector3f(final int id) {
        return new WrappedDataWatcher.WrappedDataWatcherObject(id, VECTOR_3F_SERIALIZER);
    }

    public WrappedDataWatcher.WrappedDataWatcherObject watcherObjectBlockPosition(final int id) {
        return new WrappedDataWatcher.WrappedDataWatcherObject(id, BLOCK_POSITION_SERIALIZER);
    }

    public WrappedDataWatcher.WrappedDataWatcherObject watcherObjectOptionalBlockPosition(final int id) {
        return new WrappedDataWatcher.WrappedDataWatcherObject(id, OPTIONAL_BLOCK_POSITION_SERIALIZER);
    }

    public WrappedDataWatcher.WrappedDataWatcherObject watcherObjectEnumDirection(final int id) {
        return new WrappedDataWatcher.WrappedDataWatcherObject(id, ENUM_DIRECTION_SERIALIZER);
    }

    public WrappedDataWatcher.WrappedDataWatcherObject watcherObjectOptionalUUID(final int id) {
        return new WrappedDataWatcher.WrappedDataWatcherObject(id, OPTIONAL_UUID_SERIALIZER);
    }

    public WrappedDataWatcher.WrappedDataWatcherObject watcherObjectNBTTagCompound(final int id) {
        return new WrappedDataWatcher.WrappedDataWatcherObject(id, NBT_TAG_COMPOUND_SERIALIZER);
    }

    public WrappedDataWatcher.WrappedDataWatcherObject watcherObjectObject(final int id) {
        return new WrappedDataWatcher.WrappedDataWatcherObject(id, OBJECT_SERIALIZER);
    }

    ///////////////////////////////////////////////////////////////////////////
    // #createWatchable(id, value)
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public WrappedWatchableObject createWatchable(final int id, final Byte value) {
        return new WrappedWatchableObject(watcherObjectByte(id), value);
    }

    @Override
    public WrappedWatchableObject createWatchable(final int id, final Integer value) {
        return new WrappedWatchableObject(watcherObjectInteger(id), value);
    }

    @Override
    public WrappedWatchableObject createWatchable(final int id, final Float value) {
        return new WrappedWatchableObject(watcherObjectFloat(id), value);
    }

    @Override
    public WrappedWatchableObject createWatchable(final int id, final String value) {
        return new WrappedWatchableObject(watcherObjectString(id), value);
    }

    @Override
    public WrappedWatchableObject createWatchableIChatBaseComponent(final int id, final Object value) {
        return new WrappedWatchableObject(watcherObjectIChatBaseComponent(id), value);
    }

    @Override
    public WrappedWatchableObject createWatchable(final int id, final ItemStack value) {
        return new WrappedWatchableObject(watcherObjectItemStack(id), value);
    }

    @Override
    public WrappedWatchableObject createWatchableOptionalIBlockData(final int id, final Optional<Object> value) {
        return new WrappedWatchableObject(watcherObjectOptionalIBlockData(id), value);
    }

    @Override
    public WrappedWatchableObject createWatchable(final int id, final Boolean value) {
        return new WrappedWatchableObject(watcherObjectBoolean(id), value);
    }

    @Override
    public WrappedWatchableObject createWatchableVector3f(final int id, final Object value) {
        return new WrappedWatchableObject(watcherObjectVector3f(id), value);
    }

    @Override
    public WrappedWatchableObject createWatchable(final int id, final BlockPosition value) {
        return new WrappedWatchableObject(watcherObjectBlockPosition(id), value);
    }

    @Override
    public WrappedWatchableObject createWatchable(final int id, final Optional<BlockPosition> value) {
        return new WrappedWatchableObject(watcherObjectOptionalBlockPosition(id), value);
    }

    @Override
    public WrappedWatchableObject createWatchableEnumDirection(final int id, final Object value) {
        return new WrappedWatchableObject(watcherObjectEnumDirection(id), value);
    }

    @Override
    public WrappedWatchableObject createWatchableOptionalUUID(final int id, final Optional<UUID> value) {
        return new WrappedWatchableObject(watcherObjectOptionalUUID(id), value);
    }

    @Override
    public WrappedWatchableObject createWatchableNBTTagCompound(final int id, final Object value) {
        return new WrappedWatchableObject(watcherObjectNBTTagCompound(id), value);
    }

    @Override
    public WrappedWatchableObject createWatchable(final int id, final Object value) {
        return new WrappedWatchableObject(watcherObjectObject(id), value);
    }

    @Override
    public Builder builder(WrappedDataWatcher watcher) {
        return new Builder(watcher);
    }

    @Override
    public Builder builder() {
        return new Builder();
    }

    private class Builder extends AbstractDataWatcherBuilder.Builder {
        private Builder(final WrappedDataWatcher watcher) {
            super(watcher);
        }

        private Builder() {
            super();
        }

        @Override
        public AbstractDataWatcherBuilder.Builder set(final int id, final Byte value) {
            dataWatcher.setObject(watcherObjectByte(id), value);

            return this;
        }

        @Override
        public AbstractDataWatcherBuilder.Builder set(final int id, final Integer value) {
            dataWatcher.setObject(watcherObjectInteger(id), value);

            return this;
        }

        @Override
        public AbstractDataWatcherBuilder.Builder set(final int id, final Float value) {
            dataWatcher.setObject(watcherObjectFloat(id), value);

            return this;
        }

        @Override
        public AbstractDataWatcherBuilder.Builder set(final int id, final String value) {
            dataWatcher.setObject(watcherObjectString(id), value);

            return this;
        }

        @Override
        public AbstractDataWatcherBuilder.Builder setIChatBaseComponent(final int id, final Object value) {
            dataWatcher.setObject(watcherObjectIChatBaseComponent(id), value);

            return this;
        }

        @Override
        public AbstractDataWatcherBuilder.Builder set(final int id, final ItemStack value) {
            dataWatcher.setObject(watcherObjectItemStack(id), value);

            return this;
        }

        @Override
        public AbstractDataWatcherBuilder.Builder setOptionalIBlockData(final int id, final Optional<Object> value) {
            dataWatcher.setObject(watcherObjectOptionalIBlockData(id), value);

            return this;
        }

        @Override
        public AbstractDataWatcherBuilder.Builder set(final int id, final Boolean value) {
            dataWatcher.setObject(watcherObjectBoolean(id), value);

            return this;
        }

        @Override
        public AbstractDataWatcherBuilder.Builder setVector3f(final int id, final Object value) {
            dataWatcher.setObject(watcherObjectVector3f(id), value);

            return this;
        }

        @Override
        public AbstractDataWatcherBuilder.Builder set(final int id, final BlockPosition value) {
            dataWatcher.setObject(watcherObjectBlockPosition(id), value);

            return this;
        }

        @Override
        public AbstractDataWatcherBuilder.Builder set(final int id, final Optional<BlockPosition> value) {
            dataWatcher.setObject(watcherObjectOptionalBlockPosition(id), value);

            return this;
        }

        @Override
        public AbstractDataWatcherBuilder.Builder setEnumDirection(final int id, final Object value) {
            dataWatcher.setObject(watcherObjectEnumDirection(id), value);

            return this;
        }

        @Override
        public AbstractDataWatcherBuilder.Builder setOptionalUUID(final int id, final Object value) {
            dataWatcher.setObject(watcherObjectOptionalUUID(id), value);

            return this;
        }

        @Override
        public AbstractDataWatcherBuilder.Builder setNBTTagCompound(final int id, final Object value) {
            dataWatcher.setObject(watcherObjectNBTTagCompound(id), value);

            return this;
        }

        @Override
        public AbstractDataWatcherBuilder.Builder set(final int id, final Object value) {
            dataWatcher.setObject(watcherObjectObject(id), value);

            return this;
        }
    }
}
