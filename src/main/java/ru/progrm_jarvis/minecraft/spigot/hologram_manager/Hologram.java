package ru.progrm_jarvis.minecraft.spigot.hologram_manager;

import com.comphenix.packetwrapper.*;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import ru.progrm_jarvis.minecraft.spigot.hologram_manager.util.VectorUtils;

import java.util.*;

@Getter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class Hologram extends ArrayList<HologramLine> {
    private static final long serialVersionUID = -6704502063423313450L;

    @NonNull private final String id;
    @NonNull private final boolean global;
    @NonNull @Setter private World world;
    @Setter private Vector vectorAboveLocation = null;

    @NonNull private final Set<Player> players = new HashSet<>();
    @NonNull private final Set<Player> disabledPlayers = new HashSet<>();

    public Hologram addAllPlayers(final Player... players) {
        this.players.addAll(Arrays.asList(players));
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // DataWatcher
    ///////////////////////////////////////////////////////////////////////////

    private WrappedDataWatcher.Serializer DATA_BOOLEAN_SERIALIZER = WrappedDataWatcher.Registry.get(Boolean.class);
    private WrappedDataWatcher.Serializer DATA_BYTE_SERIALIZER = WrappedDataWatcher.Registry.get(Byte.class);
    private WrappedDataWatcher.Serializer DATA_STRING_SERIALIZER = WrappedDataWatcher.Registry.get(String.class);

    public WrappedDataWatcher getDataWithName(final String name) {
        // Custom name
        return new WrappedDataWatcher() {{
            // Invisibility
            setObject(new WrappedDataWatcherObject(0, DATA_BYTE_SERIALIZER), (byte) 0x20);
            // Custom name
            setObject(new WrappedDataWatcherObject(2, DATA_STRING_SERIALIZER), name);
            // Custom name visibility
            setObject(new WrappedDataWatcherObject(3, DATA_BOOLEAN_SERIALIZER), true);
            // No gravity
            setObject(new WrappedDataWatcherObject(5, DATA_BOOLEAN_SERIALIZER), true);
            // Marker
            setObject(new WrappedDataWatcherObject(11, DATA_BYTE_SERIALIZER), (byte) 0x10);
        }};
    }

    public Hologram updateLines(final String[] lines, final Player... players) {
        val packets = new WrapperPlayServerEntityMetadata[size()];
        int i = 0;
        // Create update Packet
        for (val line : this) {
            final String lineText = lines[i];
            if (i < lines.length) packets[i++] = new WrapperPlayServerEntityMetadata() {{
                setEntityID(line.getId());
                setMetadata(Collections.singletonList(new WrappedWatchableObject(new WrappedDataWatcher
                        .WrappedDataWatcherObject(2, DATA_STRING_SERIALIZER), lineText)));
            }};
        }
        for (val player : players) for (val packet : packets) packet.sendPacket(player);
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Spawning and despawning
    ///////////////////////////////////////////////////////////////////////////

    public Hologram spawn(final boolean add, final Player... players) {
        val packets = new WrapperPlayServerSpawnEntityLiving[size()];
        int i = 0;
        // Create spawning Packet
        for (val line : this) packets[i++] = new WrapperPlayServerSpawnEntityLiving() {{
            setEntityID(line.getId());
            setType(EntityType.ARMOR_STAND);
            setX(line.getLocation().getX());
            setY(line.getLocation().getY());
            setZ(line.getLocation().getZ());
            setMetadata(getDataWithName(line.getText()));
        }};

        for (val player : players) for (val packet : packets) {
            packet.sendPacket(player);
            if (add) this.players.add(player);
        }

        return this;
    }

    public Hologram spawn(final Player... players) {
        return spawn(true, players);
    }

    public Hologram despawn(final boolean remove, final Player... players) {
        val entityIds = new int[size()];
        int i = 0;
        // Create despawning Packet
        for (val line : this) entityIds[i++] = line.getId();
        val packet = new WrapperPlayServerEntityDestroy() {{
            setEntityIds(entityIds);
        }};

        for (val player : players) {
            packet.sendPacket(player);
            if (remove) this.players.remove(player);
        }

        return this;
    }

    public Hologram despawn(final Player... players) {
        return despawn(true, players);
    }

    public Hologram despawnAll() {
        return despawn(true, players.toArray(new Player[players.size()]));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Teleportation
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Moves the hologram relatively according to the {@link Vector} given.
     * @param movement the direction of a movement
     * @param players to which players should the ru.progrm_jarvis.minecraft.spigot.hologram_manager.packet be sent
     */
    public Hologram move( final MovementVector movement, final Player... players) {
        if (movement.isZero()) return this;

        val packets = new AbstractPacket[size()];

        int i = 0;
        // Create spawning Packet
        for (val line : this) {
            // Update Locations of Hologram
            line.getLocation().add(movement);

            // Send teleportation ru.progrm_jarvis.minecraft.spigot.hologram_manager.packet
            packets[i++] = movement.isSmall() ? createLineMovePacket(line, movement) : createLineTeleportPacket(line);
        }

        for (val player : players) for (val packet : packets) packet.sendPacket(player);

        return this;
    }

    private WrapperPlayServerRelEntityMove createLineMovePacket(final HologramLine line,
                                                                final MovementVector movement) {
        return new WrapperPlayServerRelEntityMove() {{
            setEntityID(line.getId());
            setDx((int) (movement.getX() * 32 * 128));
            setDy((int) (movement.getY() * 32 * 128));
            setDz((int) (movement.getZ() * 32 * 128));
        }};
    }

    private WrapperPlayServerEntityTeleport createLineTeleportPacket(final HologramLine line) {
        return new WrapperPlayServerEntityTeleport() {{
            setEntityID(line.getId());
            setX(line.getLocation().getX());
            setY(line.getLocation().getY());
            setZ(line.getLocation().getZ());
        }};
    }

    private Hologram multiplyLocations(final double multiplierX, final double multiplierZ) {
        for (val line : this) {
            val location = line.getLocation();
            location.setX(location.getX() * multiplierX);
            location.setX(location.getZ() * multiplierZ);
        }

        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Worlds logic
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Teleports the hologram to the {@link Location} given.
     * @param location the ru.progrm_jarvis.minecraft.spigot.hologram_manager.util of a hologram base
     * @param players to which players should the ru.progrm_jarvis.minecraft.spigot.hologram_manager.packet be sent
     */
    public Hologram teleport(final Location location, final Player... players) {
        // Array of coordinates where util[i] represents the addition of coordinates to this[i] for this[i+1]
        val deltaVectors = new Vector[size() - 1];

        val packets = new WrapperPlayServerEntityTeleport[size()];

        for (int i = size() - 1; i > 0; i--) deltaVectors[i - 1]
                = VectorUtils.directionFrom2Locations(get(i).getLocation(), get(i - 1).getLocation());


        for (int i = size() - 1; i >= 0; i--) {
            // if first line then it should be set to previous location + vectorAboveLocation
            if (i == size() - 1) get(i).setLocation(vectorAboveLocation == null ? location
                    : location.add(vectorAboveLocation));
            // else calculate relatively
            else get(i).setLocation((get(i + 1).getLocation()).clone().add(deltaVectors[i]));

            packets[i] = createLineTeleportPacket(get(i));
        }


        for (val player : players) for (val packet : packets) packet.sendPacket(player);
        return this;
    }

    /**
     * Updates the hologram's position to the {@link Location} given.
     * @param location the ru.progrm_jarvis.minecraft.spigot.hologram_manager.util of a hologram base
     */
    public Hologram changeLocation(final Location location) {
        // Array of coordinates where util[i] represents the addition of coordinates to this[i] for this[i+1]
        val deltaVectors = new Vector[size() - 1];

        for (int i = 0; i < this.size() - 1; i++) deltaVectors[i]
                    = VectorUtils.directionFrom2Locations(get(i).getLocation(), get(i + 1).getLocation());

        // if first line then it should be set to util + vectorAboveLocation
        for (int i = 0; i < this.size(); i++) if (i == 0) get(i).setLocation(location.add(vectorAboveLocation));
        // else calculate relatively
        else get(i).setLocation((get(i - 1).getLocation()).clone().add(deltaVectors[i - 1]));

        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Worlds logic
    ///////////////////////////////////////////////////////////////////////////

    public Hologram changeWorld(final World world, final boolean remove) {
        this.world = world;

        despawnInWrongWorld(remove).spawnInRightWorld(global);
        return this;
    }

    public Hologram changeWorld(final Location location, final boolean remove) {
        changeLocation(location);
        return changeWorld(location.getWorld(), remove);
    }

    public Hologram changeWorldAndCoordinates(final World world,
                                              final boolean remove) {
        val currentEnvironment = this.world.getEnvironment();
        val targetEnvironment = world.getEnvironment();

        if ((currentEnvironment == World.Environment.NORMAL || currentEnvironment == World.Environment.THE_END)
                && targetEnvironment == World.Environment.NETHER) multiplyLocations(8, 8);
        else if ((targetEnvironment == World.Environment.NORMAL || targetEnvironment == World.Environment.THE_END)
                && currentEnvironment == World.Environment.NETHER) multiplyLocations(0.125, 0.125);

        this.world = world;

        despawnInWrongWorld(remove).spawnInRightWorld(global);
        return this;
    }

    public Hologram spawnInRightWorld(final boolean add) {
        val players = new ArrayList<Player>();
        // if add == true then spawn for all players in target world, else only for those who are in the set of players
        for (val player : add ? world.getPlayers() : this.players) if (isAvailableFor(player)) players.add(player);
        spawn(true, players.toArray(new Player[players.size()]));
        return this;
    }

    public Hologram despawnInWrongWorld(final boolean remove) {
        val players = new ArrayList<Player>();
        // will despawn for all players which's world is not target
        for (val player : this.players) if (world != player.getWorld()) players.add(player);
        despawn(remove, players.toArray(new Player[players.size()]));
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Availability for Player
    ///////////////////////////////////////////////////////////////////////////

    public boolean isAvailableFor(final Player player) {
        return !disabledPlayers.contains(player) && (global || players.contains(player));
    }

    public Player[] getAllAvailablePlayers() {
        val players = new ArrayList<Player>();
        for (val player : Bukkit.getOnlinePlayers()) if (isAvailableFor(player)) players.add(player);
        return players.toArray(new Player[players.size()]);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Equipment
    ///////////////////////////////////////////////////////////////////////////

    public Hologram setItem(final int lineIndex,
                            final EnumWrappers.ItemSlot slot,
                            final ItemStack item,
                            final Player... players) {
        if (lineIndex < size() && lineIndex >= 0) {
            val packet = new WrapperPlayServerEntityEquipment() {{
                setEntityID(get(lineIndex).getId());
                setSlot(slot);
                setItem(item);
            }};

            for (val player : players) packet.sendPacket(player);
        }

        return this;
    }

    public Hologram setItem(final EnumWrappers.ItemSlot slot, final ItemStack item, final Player... players) {
        val packets = new WrapperPlayServerEntityEquipment[size()];

        int i = 0;
        // Create Packet
        for (val line : this) packets[i++] = new WrapperPlayServerEntityEquipment() {{
            setEntityID(line.getId());
            setSlot(slot);
            setItem(item);
        }};

        for (val player : players) for (val packet : packets) packet.sendPacket(player);

        return this;
    }
}
