package diorite.impl;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import diorite.EntityManager;
import diorite.entity.Entity;
import diorite.impl.entity.EntityImpl;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class EntityManagerImpl implements EntityManager
{
    private final ServerImpl server;
    private final AtomicInteger             entityCount = new AtomicInteger();
    private final TIntObjectMap<EntityImpl> map         = new TIntObjectHashMap<>(1000, .4f);

    public EntityManagerImpl(final ServerImpl server)
    {
        this.server = server;
    }

    @Override
    public EntityImpl getEntity(final int id)
    {
        return null;
    }

    @Override
    public EntityImpl getEntity(final UUID id)
    {
        for (final EntityImpl entity : this.map.valueCollection())
        {
            if (entity.getUniqueID().equals(id))
            {
                return entity;
            }
        }
        return null;
    }

    public void addEntity(final EntityImpl entity)
    {
        this.map.put(entity.getId(), entity);
    }

    @Override
    public EntityImpl removeEntity(final Entity entity)
    {
        return this.removeEntity(entity.getId());
    }

    @Override
    public EntityImpl removeEntity(final int id)
    {
        return this.map.remove(id);
    }

    public int getNextID()
    {
        return this.entityCount.getAndIncrement();
    }

    @Override
    public int getCurrentID()
    {
        return this.entityCount.get();
    }

    @Override
    public ServerImpl getServer()
    {
        return this.server;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("server", this.server).append("entityCount", this.entityCount).toString();
    }
}