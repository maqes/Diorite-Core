package diorite.impl.connection.packets;

import diorite.chat.BaseComponent;

@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface PacketListener
{
    void disconnect(BaseComponent message);
}