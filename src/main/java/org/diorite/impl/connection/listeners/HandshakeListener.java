package org.diorite.impl.connection.listeners;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.LogManager;

import org.diorite.impl.ServerImpl;
import org.diorite.impl.connection.EnumProtocol;
import org.diorite.impl.connection.NetworkManager;
import org.diorite.impl.connection.packets.handshake.PacketHandshakingInListener;
import org.diorite.impl.connection.packets.handshake.in.PacketHandshakingInSetProtocol;
import org.diorite.impl.connection.packets.login.out.PacketLoginOutDisconnect;
import org.diorite.chat.component.BaseComponent;
import org.diorite.chat.component.TextComponent;

public class HandshakeListener implements PacketHandshakingInListener
{
    public static final  int                    CLEANUP_THROTTLE = 200;
    public static final  int                    CURRENT_PROTOCOL = 47;
    private static final Map<InetAddress, Long> throttleTracker  = new ConcurrentHashMap<>(100, 0.2f, 8);
    private static       int                    throttleCounter  = 0;
    private final ServerImpl     server;
    private final NetworkManager networkManager;

    public HandshakeListener(final ServerImpl server, final NetworkManager networkManager)
    {
        this.server = server;
        this.networkManager = networkManager;
    }

    @Override
    public void handle(final PacketHandshakingInSetProtocol packet)
    {

        switch (packet.getRequestType())
        {
            case LOGIN:
                this.networkManager.setProtocol(EnumProtocol.LOGIN);
                try
                {
                    final long currentTime = System.currentTimeMillis();
                    final long connectionThrottle = this.server.getConnectionThrottle();
                    final InetAddress address = ((InetSocketAddress) this.networkManager.getSocketAddress()).getAddress();

                    if ((throttleTracker.containsKey(address)) && (! "127.0.0.1".equals(address.getHostAddress())) && ((currentTime - throttleTracker.get(address)) < connectionThrottle))
                    {
                        throttleTracker.put(address, currentTime);
                        this.disconnect(TextComponent.fromLegacyText("Connection throttled! Please wait before reconnecting."));
                        return;
                    }
                    throttleTracker.put(address, currentTime);
                    throttleCounter += 1;
                    if (throttleCounter > CLEANUP_THROTTLE)
                    {
                        throttleCounter = 0;

                        final Iterator<Map.Entry<InetAddress, Long>> it = throttleTracker.entrySet().iterator();
                        while (it.hasNext())
                        {
                            final Map.Entry<InetAddress, Long> entry = it.next();
                            if (entry.getValue() > connectionThrottle)
                            {
                                it.remove();
                            }
                        }
                    }
                } catch (final Throwable t)
                {
                    LogManager.getLogger().debug("Failed to check connection throttle", t);
                }
                if (packet.getProtocolVersion() > CURRENT_PROTOCOL)
                {
                    this.disconnect(TextComponent.fromLegacyText("Outdated server, we are still on 1.8"));
                }
                else if (packet.getProtocolVersion() < CURRENT_PROTOCOL)
                {
                    this.disconnect(TextComponent.fromLegacyText("Outdated client, we are on 1.8"));
                }
                else
                {
                    this.networkManager.setPacketListener(new LoginListener(this.server, this.networkManager, packet.getServerAddress() + ":" + packet.getServerPort()));
                }
                break;
            case STATUS:
                this.networkManager.setProtocol(EnumProtocol.STATUS);
                this.networkManager.setPacketListener(new StatusListener(this.server, this.networkManager));
                break;
            default:
                throw new UnsupportedOperationException("Invalid intention " + packet.getRequestType());
        }
    }

    @Override
    public void disconnect(final BaseComponent baseComponent)
    {
        this.networkManager.sendPacket(new PacketLoginOutDisconnect(baseComponent));
        this.networkManager.close(baseComponent, true);
        this.server.getServerConnection().remove(this.networkManager);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("server", this.server).append("networkManager", this.networkManager).toString();
    }
}