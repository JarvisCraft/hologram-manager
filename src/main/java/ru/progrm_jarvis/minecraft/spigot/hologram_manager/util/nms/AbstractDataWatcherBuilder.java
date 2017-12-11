package ru.progrm_jarvis.minecraft.spigot.hologram_manager.util.nms;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public abstract class AbstractDataWatcherBuilder {

    public abstract Builder builder();
    public abstract Builder builder(WrappedDataWatcher watcher);

    public abstract WrappedWatchableObject createWatchable(int id, boolean value);
    public abstract WrappedWatchableObject createWatchable(int id, byte value);
    public abstract WrappedWatchableObject createWatchable(int id, String value);

    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    public abstract class Builder {
        @NonNull protected final WrappedDataWatcher dataWatcher;

        protected Builder() {
            this(new WrappedDataWatcher());
        }

        public abstract Builder set(int id, boolean value);
        public abstract Builder set(int id, byte value);
        public abstract Builder set(int id, String value);

        public WrappedDataWatcher get() {
            return dataWatcher;
        }
    }
}
