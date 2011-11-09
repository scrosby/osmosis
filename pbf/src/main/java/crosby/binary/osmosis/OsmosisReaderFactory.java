// This software is released into the Public Domain.  See copying.txt for details.
package crosby.binary.osmosis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.InputStream;

import org.openstreetmap.osmosis.core.pipeline.common.TaskConfiguration;
import org.openstreetmap.osmosis.core.pipeline.common.TaskManager;
import org.openstreetmap.osmosis.core.pipeline.common.TaskManagerFactory;
import org.openstreetmap.osmosis.core.pipeline.v0_6.RunnableSourceManager;


/**
 * The task manager factory for a binary (PBF) reader.
 */
public class OsmosisReaderFactory extends TaskManagerFactory {
    private static final String ARG_FILE_NAME = "file";
    private static final String DEFAULT_FILE_NAME = "dump.osm.pbf";

    /**
     * {@inheritDoc}
     */
    @Override
    protected TaskManager createTaskManagerImpl(TaskConfiguration taskConfig) {
        String fileName;
        OsmosisReader task;

        // Get the task arguments.
        fileName = getStringArgument(taskConfig, ARG_FILE_NAME,
                getDefaultStringArgument(taskConfig, DEFAULT_FILE_NAME));

        // Build the task object.
        if (fileName.equals("-")) {
            task = new OsmosisReader(System.in);
        } else {
            File file = new File(fileName);
            try {
                FileInputStream fileStream = new FileInputStream(file);
                // BlockOutputStream assumes FileInputStreams are seekable, but this isn't true with named pipes.
                // So wrap it.
                task = new OsmosisReader(new NoopInputStream(fileStream));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }
        return new RunnableSourceManager(taskConfig.getId(), task, taskConfig
                .getPipeArgs());
    }

    /** Basic wrapper around an InputStream */
    class NoopInputStream extends FilterInputStream {
        NoopInputStream(InputStream i) {
            super(i);
        }
    }
}
