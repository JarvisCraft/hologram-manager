package ru.progrm_jarvis.minecraft.spigot.hologram_manager;

import com.comphenix.protocol.ProtocolManager;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import lombok.*;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@SuppressWarnings("unused")
public class HologramManager {
    @NonNull @Getter private final Plugin plugin;
    @NonNull @Getter private final ProtocolManager protocolManager;

    @NonNull @Getter private final Map<String, Hologram> holograms = new ConcurrentHashMap<>();


    ///////////////////////////////////////////////////////////////////////////
    // Constants
    ///////////////////////////////////////////////////////////////////////////

    private static final MovementVector EMPTY_VECTOR = new MovementVector();
    private static final MovementVector SNEAK_OFF_VECTOR = new MovementVector(0, 0.8, 0);
    private static final MovementVector SNEAK_ON_VECTOR = new MovementVector(0, -0.8, 0);

    ///////////////////////////////////////////////////////////////////////////
    // Basic systems
    ///////////////////////////////////////////////////////////////////////////

    public HologramManager initAttaching() {
        Bukkit.getPluginManager().registerEvents(new PluginDisableEventListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerMoveEventListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerToggleSneakEventListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerTeleportEventListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinEventListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitEventListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerChangedWorldEventListener(), plugin);
        return this;
    }

    @Getter @Setter private BukkitTask attachingSyncTask;

    public HologramManager initAttachingSync(final long frequencyTicks) {
        if (attachingSyncTask != null && !attachingSyncTask.isCancelled()) attachingSyncTask.cancel();
        attachingSyncTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::syncAllAttached,
                frequencyTicks, frequencyTicks);

        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Creation and Removing
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Creates a custom hologram stored by given id
     * @param id custom id for storing hologram
     * @param location location at which the lowest line of hologram is
     * @param linesInterval interval between nearest hologram lines
     * @param text text content of lines
     * @param global whether or not the hologram should be marked as global (seen by everyone)
     * @return hologram created
     */
    public Hologram create(@NonNull final String id, @NonNull final Location location, final Vector linesInterval,
                           @NonNull final String[] text, final boolean global) {
        val lineLocations = new Location[text.length]; // array of locations to spawn lines
        for (int i = 0; i < text.length; i++) {
            if (i == 0) {
                lineLocations[i] = location;
                continue;
            }
            lineLocations[i] = lineLocations[i-1].clone();
            if (linesInterval != null) lineLocations[i].add(linesInterval);
        }
        ArrayUtils.reverse(lineLocations);

        val hologram = new Hologram(id, global, location.getWorld());

        // Add line to Hologram object
        for (int i = 0; i < lineLocations.length; i++) hologram
                .add(new HologramLine(text[i], lineLocations[i]));

        holograms.put(id, hologram);

        return hologram;
    }

    /**
     * Creates a custom hologram stored by given id
     * @param id custom id for storing hologram
     * @param location ru.progrm_jarvis.minecraft.spigot.hologram_manager.util at which the lowest line of hologram is
     * @param linesInterval interval between nearest hologram lines
     * @param text text content of lines
     * @param players players to which to show hologram
     * @return hologram created
     */
    public Hologram create(@NonNull final String id, @NonNull final Location location, final Vector linesInterval,
                           @NonNull final String[] text, final boolean global, final boolean add,
                           final Player... players) {
        val hologram = create(id, location, linesInterval, text, global).addAllPlayers(players);

        // Spawn hologram to all players given (if any)
        if (players.length > 0) hologram.spawn(add, players);

        return hologram;
    }

    public HologramManager remove(@NonNull final Hologram hologram) {
        detachAll(hologram);

        hologram.despawnAll();

        holograms.remove(hologram.getId());

        return this;
    }

    public HologramManager removeAll() {
        for (val hologram : holograms.values()) hologram.despawnAll();

        synchronized (attachments) {
            attachments.clear();
        }
        holograms.clear();

        return this;
    }


    public HologramManager remove(@NonNull final String id) {
        remove(getOrThrow(id));
        return this;
    }

    private Hologram getOrThrow(final String id) {
        val hologram = holograms.get(id);
        if (hologram == null) throw new NullPointerException(String.format("No hologram registered by id %s", id));
        return hologram;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Spawning and Despawning
    ///////////////////////////////////////////////////////////////////////////

    public HologramManager spawn(final String id, final boolean add, final Player... players) {
        getOrThrow(id).spawn(add, players);
        return this;
    }

    public HologramManager spawn(final String id, final Player... players) {
        getOrThrow(id).spawn(players);
        return this;
    }

    public HologramManager despawn(final String id, final boolean remove, final Player... players) {
        getOrThrow(id).despawn(remove, players);
        return this;
    }

    public HologramManager despawn(final String id, final Player... players) {
        getOrThrow(id).despawn(players);
        return this;
    }

    public HologramManager teleport(final String id, final Location location, final Player... players) {
        getOrThrow(id).teleport(location, players);
        return this;
    }

    public HologramManager move(final String id, final MovementVector movement, final Player... players) {
        getOrThrow(id).move(movement, players);
        return this;
    }

    public HologramManager changeWorld(final String id, final Location location, final boolean remove) {
        getOrThrow(id).changeWorld(location, remove);
        return this;
    }

    public HologramManager changeWorld(final String id, final World world, final boolean remove) {
        getOrThrow(id).changeWorld(world, remove);
        return this;
    }

    public HologramManager changeWorldAndCoordinates(final String id, final World world, final boolean remove) {
        getOrThrow(id).changeWorldAndCoordinates(world, remove);
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Attachments logic
    ///////////////////////////////////////////////////////////////////////////

    @NonNull @Getter private final Multimap<Player, Hologram> attachments
            = Multimaps.synchronizedListMultimap(ArrayListMultimap.create());

    public HologramManager attach(final Hologram hologram, final Player player) {

        attachments.put(player, hologram);

        return this;
    }

    public HologramManager attach(final String id, final Player player) {
        return attach(getOrThrow(id), player);
    }

    public HologramManager detachAll(final Hologram hologram) {
        synchronized (attachments) {
            val attachments = this.attachments.entries().iterator();

            while (attachments.hasNext()) if (attachments.next().getValue() == hologram) attachments.remove();
        }

        return this;
    }

    public HologramManager detach(final Hologram hologram, final Player... players) {
        for (val player : players) attachments.removeAll(player);

        return this;
    }

    public HologramManager removeAllAttached(final Player player) {
        synchronized (attachments) {
            val attachments = this.attachments.entries().iterator();
            while (attachments.hasNext()) {
                val entry = attachments.next();

                if (entry.getKey() != player) continue;

                attachments.remove();
            }
        }

        return this;
    }

    public HologramManager syncAllAttached() {
        synchronized (attachments) {
            for (val entry : attachments.entries()) entry.getValue().teleport(entry.getKey().getLocation().add(entry
                            .getKey().isSneaking() ? SNEAK_ON_VECTOR : EMPTY_VECTOR), entry.getValue()
                    .getAllAvailablePlayers());
        }

        return this;
    }

    public int incrementAmount(FileConfiguration config, String path) {
        return config.isConfigurationSection(path) ? config.getConfigurationSection(path).getKeys(false).size() : 0;
    }

    public HologramManager detach(final String id, final Player... players) {
        return detach(getOrThrow(id), players);
    }

    public HologramManager showAllPossible(final Player... players) {
        for (val hologram : holograms.values()) {
            val playersAble = new ArrayList<Player>();
            for (val player : players) if (hologram.isAvailableFor(player)) playersAble.add(player);
            hologram.spawn(true, playersAble.toArray(new Player[playersAble.size()]));
        }

        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Event Listeners
    ///////////////////////////////////////////////////////////////////////////

    private final class PluginDisableEventListener implements Listener {
        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisableEvent(final PluginDisableEvent event) {
            removeAll();
        }
    }

    private final class PlayerMoveEventListener implements Listener {
        @EventHandler(priority = EventPriority.MONITOR)
        public void onPlayerMove(final PlayerMoveEvent event) {
            moveAttached(event.getPlayer(), event.getFrom(), event.getTo());
        }
    }

    private void moveAttached(final Player player, final MovementVector movement) {
        // Get all attachments of a player
        val holograms = attachments.get(player);

        // return if no attachments to player
        if (holograms == null || holograms.isEmpty()) return;

        //Move all attached holograms
        synchronized (attachments) {
            for (val hologram : holograms) hologram.move(movement, hologram.getAllAvailablePlayers());
        }
    }

    private final class PlayerToggleSneakEventListener implements Listener {
        @EventHandler(priority = EventPriority.MONITOR)
        public void onPlayerToggleSneak(final PlayerToggleSneakEvent event) {
            moveAttached(event.getPlayer(), event.isSneaking() ? SNEAK_ON_VECTOR : SNEAK_OFF_VECTOR);
        }
    }

    private void moveAttached(final Player player, final Location from, final Location to) {
        // Get all attachments of a player
        val holograms = attachments.get(player);

        // return if no attachments to player
        if (holograms == null || holograms.isEmpty()) return;

        //Move all attached holograms
        synchronized (attachments) {
            for (val hologram : holograms) hologram.move(MovementVector
                    .from2Locations(from, to), hologram.getAllAvailablePlayers());
        }
    }

    private final class PlayerTeleportEventListener implements Listener {
        @EventHandler(priority = EventPriority.MONITOR)
        public void onPlayerTeleport(final PlayerTeleportEvent event) {
            // Get all attachments of a player
            val holograms = attachments.get(event.getPlayer());

            // return if no attachments to player
            if (holograms == null || holograms.isEmpty()) return;

            synchronized (attachments) {
                for (Hologram hologram : holograms) hologram.teleport(event.getTo(), hologram.getAllAvailablePlayers());

                //Move all attached holograms
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    for (val hologram : holograms) hologram.teleport(event.getTo(),
                            hologram.getAllAvailablePlayers());
                });
            }
        }
    }

    private final class PlayerJoinEventListener implements Listener {
        @EventHandler(priority = EventPriority.MONITOR)
        public void onPlayerJoin(final PlayerJoinEvent event) {
            showAllPossible(event.getPlayer());
        }
    }

    private final class PlayerQuitEventListener implements Listener {
        @EventHandler(priority = EventPriority.MONITOR)
        public void onPlayerQuit(final PlayerQuitEvent event) {
            removeAllAttached(event.getPlayer());
        }
    }

    private final class PlayerChangedWorldEventListener implements Listener {
        @EventHandler(priority = EventPriority.MONITOR)
        public void onPlayerChangedWorld(final PlayerChangedWorldEvent event) {
            // Get all attachments of a player
            val holograms = attachments.get(event.getPlayer());

            synchronized (attachments) {
                // change world of Hologram to Player's World
                if (holograms != null && !holograms.isEmpty()) for (val hologram
                        : holograms) hologram.changeWorld(event.getPlayer().getLocation(), false);
            }

            // Show all possible holograms for this player
            showAllPossible(event.getPlayer());
        }
    }
}
