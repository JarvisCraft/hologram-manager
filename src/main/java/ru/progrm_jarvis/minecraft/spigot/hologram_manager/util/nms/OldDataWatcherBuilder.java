package ru.progrm_jarvis.minecraft.spigot.hologram_manager.util.nms;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

public class OldDataWatcherBuilder extends AbstractDataWatcherBuilder {
    @Override
    public WrappedWatchableObject createWatchable(int id, boolean value) {
        return new WrappedWatchableObject(id, value);
    }

    @Override
    public WrappedWatchableObject createWatchable(int id, byte value) {
        return new WrappedWatchableObject(id, value);
    }

    @Override
    public WrappedWatchableObject createWatchable(int id, String value) {
        return new WrappedWatchableObject(id, value);
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
        public Builder set(int id, boolean value) {
            dataWatcher.setObject(id, value);
            return this;
        }

        @Override
        public Builder set(int id, byte value) {
            dataWatcher.setObject(id, value);
            return this;
        }

        @Override
        public Builder set(int id, String value) {
            dataWatcher.setObject(id, value);
            return this;
        }
    }
}
