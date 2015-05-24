package org.diorite.impl.world;

import java.io.File;
import java.util.Random;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.impl.world.chunk.ChunkManagerImpl;
import org.diorite.impl.world.io.ChunkIO;
import org.diorite.impl.world.io.WorldFile;
import org.diorite.impl.world.io.mca.McaWorldFile;
import org.diorite.BlockLocation;
import org.diorite.Difficulty;
import org.diorite.GameMode;
import org.diorite.ImmutableLocation;
import org.diorite.Loc;
import org.diorite.Location;
import org.diorite.Particle;
import org.diorite.material.BlockMaterialData;
import org.diorite.nbt.NbtTagCompound;
import org.diorite.world.Block;
import org.diorite.world.Dimension;
import org.diorite.world.HardcoreSettings;
import org.diorite.world.World;
import org.diorite.world.chunk.Chunk;
import org.diorite.world.chunk.ChunkPos;
import org.diorite.world.generator.WorldGenerator;
import org.diorite.world.generator.WorldGenerators;

public class WorldImpl implements World
{
    private final String             name;
    private final ChunkManagerImpl   chunkManager;
    private final WorldFile<?, ?, ?, ?> worldFile;
    private final Dimension          dimension;
    private Difficulty       difficulty        = Difficulty.NORMAL;
    private HardcoreSettings hardcore          = new HardcoreSettings(false);
    private GameMode         defaultGameMode   = GameMode.SURVIVAL;
    private int              maxHeight         = Chunk.CHUNK_FULL_HEIGHT - 1;
    private byte             forceLoadedRadius = 5;
    private long           seed;
    private boolean        raining;
    private boolean        thundering;
    private int            clearWeatherTime;
    private int            rainTime;
    private int            thunderTime;
    private Loc            spawn;
    private WorldGenerator generator;
    private long           time;
    private       boolean noUpdateMode = true;
    private final Random  random       = new Random();

    // TODO: world border impl
    // TODO: add some method allowing to set multiple blocks without calling getChunk so often

    public WorldImpl(final String name, final Dimension dimension, final File worldFile, final String generator)
    {
        this.name = name;
        this.dimension = dimension;
        this.chunkManager = new ChunkManagerImpl(this);
        this.generator = WorldGenerators.getGenerator(generator, this, null);
        this.worldFile = new McaWorldFile(worldFile, this, ChunkIO.getDefault());
    }

    public WorldImpl(final String name, final Dimension dimension, final File worldFile, final String generator, final String generatorOptions)
    {
        this.name = name;
        this.dimension = dimension;
        this.chunkManager = new ChunkManagerImpl(this);
        this.generator = WorldGenerators.getGenerator(generator, this, generatorOptions);
        this.worldFile = new McaWorldFile(worldFile, this, ChunkIO.getDefault());
    }

    public WorldImpl(final WorldFile<?, ?, ?, ?> worldFile, final String name, final Dimension dimension, final String generator, final String generatorOptions)
    {
        this.name = name;
        this.dimension = dimension;
        this.chunkManager = new ChunkManagerImpl(this);
        this.generator = WorldGenerators.getGenerator(generator, this, generatorOptions);
        this.worldFile = worldFile;
    }

    public WorldImpl(final WorldFile<?, ?, ?, ?> worldFile, final String name, final Dimension dimension, final String generator)
    {
        this.name = name;
        this.dimension = dimension;
        this.chunkManager = new ChunkManagerImpl(this);
        this.generator = WorldGenerators.getGenerator(generator, this, null);
        this.worldFile = worldFile;
    }

    public void loadNBT(final NbtTagCompound tag)
    {
        this.clearWeatherTime = tag.getInt("clearWeatherTime", 0);
        this.defaultGameMode = GameMode.getByID(tag.getInt("GameType", 0));
        this.difficulty = Difficulty.getByLevel(tag.getByte("Difficulty", 0));
        this.raining = tag.getBoolean("raining", false);
        this.rainTime = tag.getInt("rainTime", 0);
        this.thundering = tag.getBoolean("thundering", false);
        this.thunderTime = tag.getInt("thunderTime", 0);
        this.seed = tag.getLong("RandomSeed", 0);
        this.random.setSeed(this.seed);
        this.time = tag.getLong("Time", 0);

        final NbtTagCompound diTag = tag.getCompound("Diorite", new NbtTagCompound("Diorite"));
        diTag.setParent(tag);
        this.hardcore = (new HardcoreSettings(tag.getBoolean("hardcore", false), HardcoreSettings.HardcoreAction.values()[diTag.getByte("HardcoreAction", 0)]));
        this.forceLoadedRadius = (diTag.getByte("ForceLoadedRadius", 10));

        if (diTag.containsTag("spawnX"))
        {
            this.spawn = new Location(diTag.getDouble("spawnX"), diTag.getDouble("spawnY"), diTag.getDouble("spawnZ"), diTag.getFloat("spawnYaw"), diTag.getFloat("spawnPitch"), this);
        }
        else
        {
            this.spawn = new Location(tag.getInt("spawnX", 0), tag.getInt("spawnY", Chunk.CHUNK_FULL_HEIGHT / 2), tag.getInt("spawnZ", 0), this);
        }

    }

    @Override
    public void save()
    {
        System.out.println("Saving chunks for world: " + this.name);
        this.chunkManager.saveAll();
        System.out.println("Saved chunks for world: " + this.name);
    }

    @Override
    public Random getRandom()
    {
        return this.random;
    }

    @Override
    public Dimension getDimension()
    {
        return this.dimension;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public ChunkManagerImpl getChunkManager()
    {
        return this.chunkManager;
    }

    @Override
    public long getSeed()
    {
        return this.seed;
    }

    @Override
    public void setSeed(final long seed)
    {
        this.seed = seed;
        this.random.setSeed(this.seed);
    }

    @Override
    public Difficulty getDifficulty()
    {
        return this.difficulty;
    }

    @Override
    public void setDifficulty(final Difficulty difficulty)
    {
        this.difficulty = difficulty;
    }

    @Override
    public HardcoreSettings getHardcore()
    {
        return this.hardcore;
    }

    @Override
    public void setHardcore(final HardcoreSettings hardcore)
    {
        this.hardcore = hardcore;
    }

    @Override
    public GameMode getDefaultGameMode()
    {
        return this.defaultGameMode;
    }

    @Override
    public void setDefaultGameMode(final GameMode defaultGameMode)
    {
        this.defaultGameMode = defaultGameMode;
    }

    @Override
    public boolean isRaining()
    {
        return this.raining;
    }

    @Override
    public void setRaining(final boolean raining)
    {
        this.raining = raining;
    }

    @Override
    public boolean isThundering()
    {
        return this.thundering;
    }

    @Override
    public void setThundering(final boolean thundering)
    {
        this.thundering = thundering;
    }

    @Override
    public int getClearWeatherTime()
    {
        return this.clearWeatherTime;
    }

    @Override
    public void setClearWeatherTime(final int clearWeatherTime)
    {
        this.clearWeatherTime = clearWeatherTime;
    }

    @Override
    public int getRainTime()
    {
        return this.rainTime;
    }

    public void setRainTime(final int rainTime)
    {
        this.rainTime = rainTime;
    }

    @Override
    public int getThunderTime()
    {
        return this.thunderTime;
    }

    public void setThunderTime(final int thunderTime)
    {
        this.thunderTime = thunderTime;
    }

    @Override
    public ImmutableLocation getSpawn()
    {
        return this.spawn.toImmutableLocation();
    }

    @Override
    public void setSpawn(final Loc spawn)
    {
        this.spawn = spawn.toImmutableLocation();
    }

    @Override
    public WorldGenerator getGenerator()
    {
        return this.generator;
    }

    @Override
    public void setGenerator(final WorldGenerator generator)
    {
        this.generator = generator;
    }

    @Override
    public Block getBlock(final int x, final int y, final int z)
    {
        if (y > this.maxHeight)
        {
            return null;
        }
        return this.chunkManager.getChunkAt(ChunkPos.fromWorldPos(x, z, this), true, false).getBlock((x & (Chunk.CHUNK_SIZE - 1)), y, (z & (Chunk.CHUNK_SIZE - 1)));
    }

    @Override
    public int getHighestBlockY(final int x, final int z)
    {
        return this.chunkManager.getChunkAt(ChunkPos.fromWorldPos(x, z, this), true, false).getHighestBlockY((x & (Chunk.CHUNK_SIZE - 1)), (z & (Chunk.CHUNK_SIZE - 1)));
    }

    @Override
    public Block getHighestBlock(final int x, final int z)
    {
        return this.chunkManager.getChunkAt(ChunkPos.fromWorldPos(x, z, this), true, false).getHighestBlock((x & (Chunk.CHUNK_SIZE - 1)), (z & (Chunk.CHUNK_SIZE - 1)));
    }

    @Override
    public void setBlock(final int x, final int y, final int z, final BlockMaterialData material)
    {
        if (y > this.maxHeight)
        {
            return;
        }
        this.chunkManager.getChunkAt(ChunkPos.fromWorldPos(x, z, this), true, false).setBlock((x & (Chunk.CHUNK_SIZE - 1)), y, (z & (Chunk.CHUNK_SIZE - 1)), material.getId(), material.getType());
    }

    @Override
    public void setBlock(final BlockLocation location, final BlockMaterialData material)
    {
        this.setBlock(location.getX(), location.getY(), location.getZ(), material);
    }

    @Override
    public int getMaxHeight()
    {
        return this.maxHeight;
    }

    public void setMaxHeight(final int maxHeight)
    {
        this.maxHeight = maxHeight;
    }

    @Override
    public long getTime()
    {
        return this.time;
    }

    @Override
    public void setTime(final long time)
    {
        this.time = time;
    }

    @Override
    public void showParticle(final Particle particle, final boolean isLongDistance, final int x, final int y, final int z, final int offsetX, final int offsetY, final int offsetZ, final int particleData, final int particleCount, final int... data)
    {
        this.getPlayersInWorld().forEach(player -> player.showParticle(particle, isLongDistance, x, y, x, offsetX, offsetY, offsetZ, particleData, particleCount, data));
    }

    public boolean hasSkyLight()
    {
        return this.dimension.hasSkyLight();
    }

    public boolean isNoUpdateMode()
    {
        return this.noUpdateMode;
    }

    public void setNoUpdateMode(final boolean noUpdateMode)
    {
        this.noUpdateMode = noUpdateMode;
    }

    public byte getForceLoadedRadius()
    {
        return this.forceLoadedRadius;
    }

    public void setForceLoadedRadius(final byte forceLoadedRadius)
    {
        this.forceLoadedRadius = forceLoadedRadius;
    }

    @SuppressWarnings("rawtypes")
    public WorldFile getWorldFile()
    {
        return this.worldFile;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).toString();
    }
}
