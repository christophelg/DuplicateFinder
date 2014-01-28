package com.github.christophelg.duplicatefinder.command;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.github.christophelg.duplicatefinder.leveldb.DbFactory;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;

@Parameters(separators = "=", commandDescription = "Find all the duplicates in the database")
public class DuplicateCommand implements Command {

  @Parameter(names = {"--database"}, description = "Where to find the database", required = true)
  private String database;

  private File databaseLocation;

  public void validate() {
    databaseLocation = new File(database);
    if (!databaseLocation.isDirectory()) {
      throw new IllegalArgumentException("Database location must be a directory");
    }
  }

  public void run() throws IOException {
    DB db = DbFactory.getConnection(databaseLocation, false);

    // load all the entries in the map
    DBIterator iterator = db.iterator();
    ImmutableListMultimap<String, Map.Entry<byte[], byte[]>> duplications =
        Multimaps.index(iterator, new Function<Map.Entry<byte[], byte[]>, String>() {

          @Override
          public String apply(Map.Entry<byte[], byte[]> paramF) {
            return new String(paramF.getValue(), Charsets.US_ASCII);
          }

        });
    iterator.close();

    // for each map with multiple entries, dump
    for (String key : duplications.keySet()) {
      ImmutableList<Map.Entry<byte[], byte[]>> entries = duplications.get(key);
      if (entries.size() > 1) {
        System.out.println("For key:" + key);
        for (Map.Entry<byte[], byte[]> entry : entries) {
          System.out.println("\tFilename:" + new String(entry.getKey(), Charsets.ISO_8859_1));
        }
      }
    }

    db.close();
  }
}
