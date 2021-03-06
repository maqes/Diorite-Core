package org.diorite.impl.pipelines;

import java.util.Set;

import org.diorite.impl.Main;
import org.diorite.impl.world.generator.ChunkBuilderImpl;
import org.diorite.event.chunk.ChunkGenerateEvent;
import org.diorite.event.pipelines.ChunkGeneratePipeline;
import org.diorite.utils.collections.sets.ConcurrentSet;
import org.diorite.utils.pipeline.SimpleEventPipeline;
import org.diorite.world.chunk.ChunkPos;
import org.diorite.world.generator.ChunkBuilder;

public class ChunkGeneratePipelineImpl extends SimpleEventPipeline<ChunkGenerateEvent> implements ChunkGeneratePipeline
{
    static Set<ChunkPos> gens = new ConcurrentSet<>();
    static Set<ChunkPos> pops = new ConcurrentSet<>();

    public static void addGen(final ChunkPos pos)
    {
        if (Main.isEnabledDebug() && ! gens.add(pos))
        {
            Main.debug("[CHUNK_ERROR] Chunk generated second time: " + pos);
        }
    }

    public static void addPops(final ChunkPos pos)
    {
        if (Main.isEnabledDebug() && ! pops.add(pos))
        {
            Main.debug("[CHUNK_ERROR] Chunk populated second time: " + pos);
        }
    }

    @Override
    public void reset_()
    {
        this.addFirst("Diorite|Gen", (evt, pipeline) -> {
            if (evt.isCancelled())
            {
                return;
            }
            final ChunkBuilder chunkBuilder = evt.getWorld().getGenerator().generate(new ChunkBuilderImpl(), evt.getChunkPos());
            evt.setGeneratedChunk(chunkBuilder.createChunk(evt.getChunkPos()));
        });
    }
}
