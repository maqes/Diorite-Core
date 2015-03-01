package diorite.impl.connection.packets.play.out;

import java.io.IOException;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import diorite.impl.connection.EnumProtocol;
import diorite.impl.connection.EnumProtocolDirection;
import diorite.impl.connection.packets.PacketClass;
import diorite.impl.connection.packets.PacketDataSerializer;
import diorite.impl.connection.packets.play.PacketPlayOutListener;
import diorite.impl.map.chunk.ChunkImpl;

@PacketClass(id = 0x21, protocol = EnumProtocol.PLAY, direction = EnumProtocolDirection.CLIENTBOUND)
public class PacketPlayOutMapChunk implements PacketPlayOut
{
    private int       x;
    private int       z;
    private boolean   groundUpContinuous;
    private ChunkImpl chunk;

    private boolean includeSkyLight;
    private int     mask;

    private int selectedPart; // only if groundUpContinuous

    public PacketPlayOutMapChunk()
    {
    }

    public PacketPlayOutMapChunk(final boolean groundUpContinuous, final ChunkImpl chunk)
    {
        this.x = chunk.getPos().getX();
        this.z = chunk.getPos().getZ();
        this.groundUpContinuous = groundUpContinuous;
        this.chunk = chunk;
        this.mask = chunk.getMask();
    }

    public PacketPlayOutMapChunk(final boolean groundUpContinuous, final ChunkImpl chunk, final boolean includeSkyLight)
    {
        this.x = chunk.getPos().getX();
        this.z = chunk.getPos().getZ();
        this.groundUpContinuous = groundUpContinuous;
        this.chunk = chunk;
        this.includeSkyLight = includeSkyLight;
        this.mask = chunk.getMask();
    }

    public PacketPlayOutMapChunk(final boolean groundUpContinuous, final ChunkImpl chunk, final boolean includeSkyLight, final int mask)
    {
        this.x = chunk.getPos().getX();
        this.z = chunk.getPos().getZ();
        this.groundUpContinuous = groundUpContinuous;
        this.chunk = chunk;
        this.includeSkyLight = includeSkyLight;
        this.mask = mask;
    }

    @Override
    public void readPacket(final PacketDataSerializer data) throws IOException
    {
        this.x = data.readInt();
        this.z = data.readInt();
        // TODO: this
    }

    @Override
    public void writePacket(final PacketDataSerializer data) throws IOException
    {
        data.writeInt(this.x);
        data.writeInt(this.z);

        data.writeBoolean(this.groundUpContinuous);
        data.writeChunk(this.chunk, this.mask, this.includeSkyLight, this.groundUpContinuous);

    }

    @Override
    public void handle(final PacketPlayOutListener listener)
    {

    }

    public int getX()
    {
        return this.x;
    }

    public void setX(final int x)
    {
        this.x = x;
    }

    public int getZ()
    {
        return this.z;
    }

    public void setZ(final int z)
    {
        this.z = z;
    }

    public boolean isGroundUpContinuous()
    {
        return this.groundUpContinuous;
    }

    public void setGroundUpContinuous(final boolean groundUpContinuous)
    {
        this.groundUpContinuous = groundUpContinuous;
    }

    public ChunkImpl getChunk()
    {
        return this.chunk;
    }

    public void setChunk(final ChunkImpl chunk)
    {
        this.chunk = chunk;
    }

    public boolean isIncludeSkyLight()
    {
        return this.includeSkyLight;
    }

    public void setIncludeSkyLight(final boolean includeSkyLight)
    {
        this.includeSkyLight = includeSkyLight;
    }

    public int getMask()
    {
        return this.mask;
    }

    public void setMask(final int mask)
    {
        this.mask = mask;
    }

    public int getSelectedPart()
    {
        return this.selectedPart;
    }

    public void setSelectedPart(final int selectedPart)
    {
        this.selectedPart = selectedPart;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("x", this.x).append("z", this.z).append("groundUpContinuous", this.groundUpContinuous).append("chunk", this.chunk).append("includeSkyLight", this.includeSkyLight).append("mask", this.mask).toString();
    }
}
