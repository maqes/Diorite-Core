package diorite.impl.connection.packets.status;

import diorite.impl.connection.packets.PacketListener;
import diorite.impl.connection.packets.status.in.PacketStatusInPing;
import diorite.impl.connection.packets.status.in.PacketStatusInStart;

public interface PacketStatusInListener extends PacketListener
{
    void handle(PacketStatusInPing packet);

    void handle(PacketStatusInStart packet);
}
