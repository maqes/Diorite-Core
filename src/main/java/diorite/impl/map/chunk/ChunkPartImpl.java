package diorite.impl.map.chunk;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import diorite.map.chunk.Chunk;
import diorite.utils.collections.NibbleArray;

public class ChunkPartImpl // part of chunk 16x16x16
{
    public static final int CHUNK_DATA_SIZE = Chunk.CHUNK_SIZE * Chunk.CHUNK_PART_HEIGHT * Chunk.CHUNK_SIZE;

    private volatile byte        yPos; // from 0 to 15
    private final    char[]      blocks; // id and sub-id(0-15) of every block
    private final     NibbleArray skyLight;
    private final     NibbleArray blockLight;
    private volatile  int         blocksCount;

    public ChunkPartImpl(final byte yPos)
    {
        this.yPos = yPos;
        this.blocks = new char[CHUNK_DATA_SIZE];
        this.skyLight = new NibbleArray(CHUNK_DATA_SIZE);
        this.blockLight = new NibbleArray(CHUNK_DATA_SIZE);
    }

    public ChunkPartImpl(final char[] blocks, final NibbleArray skyLight, final NibbleArray blockLight, final byte yPos)
    {
        this.blocks = blocks;
        this.skyLight = skyLight;
        this.blockLight = blockLight;
        this.yPos = yPos;
    }

    public void setBlock(final int x, final int y, final int z, final byte id, final byte meta)
    {
        this.blocks[this.toArrayIndex(x, y, z)] = (char) ((id << 4) | meta);
    }

    public char[] getBlocks()
    {
        return this.blocks;
    }

    public int recalculateBlockCount()
    {
        this.blocksCount = 0;
        for (final char type : this.blocks)
        {
            if (type != 0)
            {
                this.blocksCount++;
            }
        }
        return this.blocksCount;
    }

    public int getBlocksCount()
    {
        return this.blocksCount;
    }

    public NibbleArray getBlockLight()
    {
        return this.blockLight;
    }

    public NibbleArray getSkyLight()
    {
        return this.skyLight;
    }

    public byte getYPos()
    {
        return this.yPos;
    }

    public void setYPos(final byte yPos)
    {
        this.yPos = yPos;
    }

    public boolean isEmpty()
    {
        return this.blocksCount == 0;
    }

    @SuppressWarnings("MagicNumber")
    public int toArrayIndex(final int x, final int y, final int z)
    {
        return ((y & 0xf) << 8) | (z << 4) | x;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("yPos", this.yPos).append("blocks", this.blocks).append("skyLight", this.skyLight).append("blockLight", this.blockLight).append("blocksCount", this.blocksCount).toString();
    }
}