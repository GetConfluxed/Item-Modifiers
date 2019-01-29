package com.getconfluxed.itemmodifiers.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;

import com.getconfluxed.itemmodifiers.ItemModifiersMod;

import net.minecraftforge.fml.common.ModContainer;

public class DataLoader {

    public static void findFiles (ModContainer mod, String base, BiConsumer<Path, Path> processor) {

        final File source = mod.getSource();
        FileSystem fs = null;

        try {

            Path root = null;

            // If the mod is a file, try to load it as a file system.
            if (source.isFile()) {

                try {
                    fs = FileSystems.newFileSystem(source.toPath(), null);
                    root = fs.getPath("/" + base);
                }
                catch (final IOException e) {
                    return;
                }
            }

            // Otherwise load the file as a director.
            else if (source.isDirectory()) {
                root = source.toPath().resolve(base);
            }

            // Check to see if the path exists.
            if (root != null && Files.exists(root)) {

                // Stream through the files in the directory
                try (Stream<Path> stream = Files.walk(root)) {

                    final Iterator<Path> itr = stream.iterator();

                    // Iterate the files, and process them.
                    while (itr != null && itr.hasNext()) {

                        processor.accept(root, itr.next());
                    }
                }

                catch (final IOException e) {

                    ItemModifiersMod.LOG.info("Unable to load from {}", mod.getModId(), e);
                }
            }
        }

        // Make sure the file system is closed if it was opened.
        // This is needed here, because we can't use the path if the file system is closed.
        finally {

            IOUtils.closeQuietly(fs);
        }
    }
}