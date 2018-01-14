package ru.progrm_jarvis.minecraft.spigot.hologram_manager.util.nms;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;

public class OldDataWatcherBuilder extends AbstractDataWatcherBuilder {

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
    }
}
