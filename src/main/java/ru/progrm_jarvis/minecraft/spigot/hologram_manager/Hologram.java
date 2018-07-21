package ru.progrm_jarvis.minecraft.spigot.hologram_manager;

import com.comphenix.packetwrapper.*;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import lombok.*;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import ru.progrm_jarvis.minecraft.spigot.hologram_manager.util.VectorUtils;

import java.util.*;

import static ru.progrm_jarvis.minecraft.nmsutil.NmsManager.DATA_WATCHER_BUILDER;

@Getter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@SuppressWarnings({"unused", "WeakerAccess"})
public class Hologram extends ArrayList<HologramLine> {
    private static final long serialVersionUID = 480275233413221397L;

    @NonNull private final String id;
    @NonNull private final boolean global;
    @NonNull @Setter private World world;
    @Setter private Vector vectorAboveLocation = null;

    @NonNull private final Set<Player> players = new HashSet<>();
    @NonNull private final Set<Player> disabledPlayers = new HashSet<>();

    ///////////////////////////////////////////////////////////////////////////
    // Locks
    ///////////////////////////////////////////////////////////////////////////
    @NonNull private final Object[] $locationLock = new Object[0];

    ///////////////////////////////////////////////////////////////////////////
    // Overloading constructors
    ///////////////////////////////////////////////////////////////////////////

    public Hologram addAllPlayers(final Player... players) {
        this.players.addAll(Arrays.asList(players));
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // DataWatcher
    ///////////////////////////////////////////////////////////////////////////

    public String[] getText() {
        val text = new String[size()];
        for (int i = 0; i < size(); i++) text[i] = get(i).getText();
        return text;
    }

    public Hologram setText(final String[] lines, final Player... players) {
        val packets = new WrapperPlayServerEntityMetadata[size()];
        int i = 0;

        // Create update Packet
        for (val line : this) if (i < lines.length) packets[i] = PacketGenerator
                .metadata(line, MetadataGenerator.getNameMetadata(lines[i++]));

        for (val player : players) for (val packet : packets) packet.sendPacket(player);
        return this;
    }

    public Hologram setText(final Map<Integer, String> lines, final Player... players) {
        val packets = new WrapperPlayServerEntityMetadata[lines.size()];
        int i = 0;

        // Create update Packet
        for (val line : lines.entrySet()) packets[i++]
                = PacketGenerator.metadata(get(line.getKey()), MetadataGenerator.getNameMetadata(line.getValue()));

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
        for (val line : this) packets[i++] = PacketGenerator.spawn(line, MetadataGenerator.getDefault(line.getText(),
                true, MetadataGenerator.ArmorStandTag.MARKER));

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
        val packet = PacketGenerator.destroy(this);

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
        return despawn(true, players.toArray(new Player[0]));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Teleportation
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Moves the hologram relatively according to the {@link Vector} given.
     * @param movement the direction of a movement
     * @param players to which players should packet be sent
     */
    @Synchronized("$locationLock") public Hologram move(final MovementVector movement, final Player... players) {
        if (movement.isZero()) return this;

        val packets = new AbstractPacket[size()];

        int i = 0;
        // Create spawning Packet
        for (val line : this) {
            // Update Locations of Hologram
            line.getLocation().add(movement);

            // Send teleportation ru.progrm_jarvis.minecraft.spigot.hologram_manager.packet
            packets[i++] = PacketGenerator.moveOrTeleport(line, movement);
        }

        for (val player : players) for (val packet : packets) packet.sendPacket(player);

        return this;
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
     * @param location the location of a hologram base
     * @param players to which players should the packet be sent
     */
    @Synchronized("$locationLock") public Hologram teleport(final Location location, final Player... players) {
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

            packets[i] = PacketGenerator.teleport(get(i));
        }


        for (val player : players) for (val packet : packets) packet.sendPacket(player);
        return this;
    }

    /**
     * Updates the hologram's position to the {@link Location} given.
     * @param location the location of a hologram base
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

    public Hologram changeWorldAndCoordinates(final World world, final boolean remove) {
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
        spawn(true, players.toArray(new Player[0]));
        return this;
    }

    public Hologram despawnInWrongWorld(final boolean remove) {
        val players = new ArrayList<Player>();
        // will despawn for all players which's world is not target
        for (val player : this.players) if (world != player.getWorld()) players.add(player);
        despawn(remove, players.toArray(new Player[0]));
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
        return players.toArray(new Player[0]);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Equipment
    ///////////////////////////////////////////////////////////////////////////

    public Hologram setItem(final int lineIndex, final EnumWrappers.ItemSlot slot, final ItemStack item,
                            final Player... players) {
        if (lineIndex < size() && lineIndex >= 0) {
            val packet = PacketGenerator.equipment(get(lineIndex), slot, item);

            for (val player : players) packet.sendPacket(player);
        }

        return this;
    }

    public Hologram setItemAll(final EnumWrappers.ItemSlot slot, final ItemStack item, final Player... players) {
        val packets = new WrapperPlayServerEntityEquipment[size()];

        int i = 0;
        // Create Packet
        for (val line : this) packets[i++] = PacketGenerator.equipment(line, slot, item);

        for (val player : players) for (val packet : packets) packet.sendPacket(player);

        return this;
    }

    @Synchronized("$locationLock") public Hologram add(final HologramLine line, final MovementVector movePrevious,
                                                       final Player... players) {
        for (val previousLine : this) previousLine.getLocation().add(movePrevious);

        add(line);

        if (players.length == 0) return this;

        val packets = new AbstractPacket[size()];
        for (int i = 0; i < size() - 1; i++) packets[i] = PacketGenerator.moveOrTeleport(get(i), movePrevious);
        packets[size() - 1] = PacketGenerator.spawn(line, MetadataGenerator.getDefault(line.getText(), true,
                MetadataGenerator.ArmorStandTag.MARKER));

        for (val player : players) for (val packet : packets) packet.sendPacket(player);

        return this;
    }

    @Synchronized("$locationLock") public Hologram add(final int index, final HologramLine line,
                                                       final MovementVector movePrevious, final MovementVector moveNext,
                                                       final Player... players) {
        add(index, line);

        for (int i = 0; i < index; i++) get(i).getLocation().add(movePrevious);
        for (int i = index + 1; i < this.size(); i++) get(i).getLocation().add(moveNext);

        if (players.length == 0) return this;

        val packets = new AbstractPacket[size()];
        for (int i = 0; i < index; i++) packets[i] = PacketGenerator.moveOrTeleport(get(i), movePrevious);
        packets[index] = PacketGenerator.spawn(line, MetadataGenerator.getDefault(line.getText(), true,
                MetadataGenerator.ArmorStandTag.MARKER));
        for (int i = index + 1; i < this.size(); i++) packets[i] = PacketGenerator.moveOrTeleport(get(i), moveNext);

        for (val player : players) for (val packet : packets) packet.sendPacket(player);

        return this;
    }

    @UtilityClass
    private static class PacketGenerator {
        public WrapperPlayServerSpawnEntityLiving spawn(final HologramLine line, final WrappedDataWatcher dataWatcher) {
            return new WrapperPlayServerSpawnEntityLiving() {{
                setEntityID(line.getId());
                setType(EntityType.ARMOR_STAND);
                setX(line.getLocation().getX());
                setY(line.getLocation().getY());
                setZ(line.getLocation().getZ());
                setMetadata(dataWatcher);
            }};
        }

        public WrapperPlayServerEntityDestroy destroy(final HologramLine... lines) {
            val entityIds = new int[lines.length];
            int i = 0;
            // Create despawning Packet
            for (val line : lines) entityIds[i++] = line.getId();

            return new WrapperPlayServerEntityDestroy() {{
                setEntityIds(entityIds);
            }};
        }

        public WrapperPlayServerEntityDestroy destroy(final List<HologramLine> lines) {
            val entityIds = new int[lines.size()];
            int i = 0;
            // Create despawning Packet
            for (val line : lines) entityIds[i++] = line.getId();

            return new WrapperPlayServerEntityDestroy() {{
                setEntityIds(entityIds);
            }};
        }

        public AbstractPacket moveOrTeleport(final HologramLine line, final MovementVector movement) {
            return movement.isSmall() ? move(line, movement) : teleport(line);
        }

        public WrapperPlayServerRelEntityMove move(final HologramLine line, final MovementVector movement) {
            return new WrapperPlayServerRelEntityMove() {{
                setEntityID(line.getId());
                setDx((int) (movement.getX() * 32 * 128));
                setDy((int) (movement.getY() * 32 * 128));
                setDz((int) (movement.getZ() * 32 * 128));
            }};
        }

        public WrapperPlayServerEntityTeleport teleport(final HologramLine line) {
            return new WrapperPlayServerEntityTeleport() {{
                setEntityID(line.getId());
                setX(line.getLocation().getX());
                setY(line.getLocation().getY());
                setZ(line.getLocation().getZ());
            }};
        }

        public WrapperPlayServerEntityMetadata metadata(final HologramLine line,
                                                        final List<WrappedWatchableObject> watchableObjects) {
            return new WrapperPlayServerEntityMetadata() {{
                setEntityID(line.getId());
                setMetadata(watchableObjects);
            }};
        }

        public WrapperPlayServerEntityEquipment equipment(final HologramLine line, final EnumWrappers.ItemSlot slot,
                                                          final ItemStack item) {
            return new WrapperPlayServerEntityEquipment() {{
                setEntityID(line.getId());
                setSlot(slot);
                if (item != null && item.getType() != Material.AIR) setItem(item);
            }};
        }
    }

    @UtilityClass
    public static class MetadataGenerator {
        public WrappedDataWatcher getDefault(final String name, final boolean invisible, final byte... tags) {
            byte tagsByte = 0;
            for (val tag : tags) tagsByte |= tag;

            val builder = DATA_WATCHER_BUILDER.builder().set(5, true).set(11, tagsByte);
            if (invisible) builder.set(0, (byte) 0x20);

            if (name != null) builder.set(2, name).set(3, true);

            return builder.build();
        }

        public List<WrappedWatchableObject> getNameMetadata(final String name) {
            return name == null
                    ? Collections.singletonList(DATA_WATCHER_BUILDER.createWatchable(3, false))
                    : Arrays.asList(DATA_WATCHER_BUILDER.createWatchable(2, name),
                    DATA_WATCHER_BUILDER.createWatchable(3, true));
        }

        public final class ArmorStandTag {
            public static final byte SMALL = 0x01, ARMS = 0x04, NO_BASE_PLATE = 0x08, MARKER = 0x10;
        }
    }
}
