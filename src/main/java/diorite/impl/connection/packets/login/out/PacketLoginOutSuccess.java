package diorite.impl.connection.packets.login.out;

import java.io.IOException;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.mojang.authlib.GameProfile;

import diorite.impl.connection.EnumProtocol;
import diorite.impl.connection.EnumProtocolDirection;
import diorite.impl.connection.packets.PacketClass;
import diorite.impl.connection.packets.login.PacketLoginOutListener;
import diorite.impl.connection.packets.PacketDataSerializer;

@PacketClass(id = 0x02, protocol = EnumProtocol.LOGIN, direction = EnumProtocolDirection.CLIENTBOUND)
public class PacketLoginOutSuccess implements PacketLoginOut
{
    private GameProfile profile;

    public PacketLoginOutSuccess()
    {
    }

    public PacketLoginOutSuccess(final GameProfile profile)
    {
        this.profile = profile;
    }

    @Override
    public void readPacket(final PacketDataSerializer data) throws IOException
    {
        this.profile = data.readGameProfile();
    }

    @Override
    public void writePacket(final PacketDataSerializer data) throws IOException
    {
        data.writeGameProfile(this.profile);
    }

    @Override
    public void handle(final PacketLoginOutListener listener)
    {
        listener.handle(this);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("profile", this.profile).toString();
    }
}