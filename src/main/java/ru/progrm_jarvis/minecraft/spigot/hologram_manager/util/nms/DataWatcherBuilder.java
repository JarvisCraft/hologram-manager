package ru.progrm_jarvis.minecraft.spigot.hologram_manager.util.nms;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

public class DataWatcherBuilder extends AbstractDataWatcherBuilder {
    private WrappedDataWatcher.Serializer BOOLEAN_SERIALIZER= WrappedDataWatcher.Registry.get(Boolean.class);
    private WrappedDataWatcher.Serializer BYTE_SERIALIZER = WrappedDataWatcher.Registry.get(Byte.class);
    private WrappedDataWatcher.Serializer STRING_SERIALIZER = WrappedDataWatcher.Registry.get(String.class);

    @Override
    public WrappedWatchableObject createWatchable(int id, boolean value) {
        return new WrappedWatchableObject(watcherObjectBoolean(id), value);
    }

    @Override
    public WrappedWatchableObject createWatchable(int id, byte value) {
        return new WrappedWatchableObject(watcherObjectByte(id), value);
    }

    @Override
    public WrappedWatchableObject createWatchable(int id, String value) {
        return new WrappedWatchableObject(watcherObjectString(id), value);
    }

    @Override
    public Builder builder(WrappedDataWatcher watcher) {
        return new Builder(watcher);
    }

    @Override
    public Builder builder() {
        return new Builder();
    }

    public WrappedDataWatcher.WrappedDataWatcherObject watcherObjectBoolean(int id) {
        return new WrappedDataWatcher.WrappedDataWatcherObject(id, BOOLEAN_SERIALIZER);
    }

    public WrappedDataWatcher.WrappedDataWatcherObject watcherObjectByte(int id) {
        return new WrappedDataWatcher.WrappedDataWatcherObject(id, BYTE_SERIALIZER);
    }

    public WrappedDataWatcher.WrappedDataWatcherObject watcherObjectString(int id) {
        return new WrappedDataWatcher.WrappedDataWatcherObject(id, STRING_SERIALIZER);
    }

    private class Builder extends AbstractDataWatcherBuilder.Builder {
        private Builder(final WrappedDataWatcher watcher) {
            super(watcher);
        }

        private Builder() {
            super();
        }

        @Override
        public Builder set(int id, boolean value) {
            dataWatcher.setObject(watcherObjectBoolean(id), value);
            return this;
        }

        @Override
        public Builder set(int id, byte value) {
            dataWatcher.setObject(watcherObjectByte(id), value);
            return this;
        }

        @Override
        public Builder set(int id, String value) {
            dataWatcher.setObject(watcherObjectString(id), value);
            return this;
        }
    }
}
