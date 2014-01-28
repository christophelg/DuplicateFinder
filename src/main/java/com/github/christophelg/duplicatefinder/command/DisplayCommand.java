package com.github.christophelg.duplicatefinder.command;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.ReadOptions;

import com.beust.jcommander.Parameter;
import com.github.christophelg.duplicatefinder.leveldb.DbFactory;

public class DisplayCommand implements Command {

  @Parameter(names = {"--database"}, description = "Where to find the database", required = true)
  private String database;

  @Parameter(names = {"--pattern"}, description = "The pattern to use to match filenames", required = true)
  private String pattern;

  private File databaseLocation;
  private Pattern whatToMatch;

  @Override
  public void validate() {
    databaseLocation = new File(database);
    if (!databaseLocation.isDirectory()) {
      throw new IllegalArgumentException("Database location must be a directory");
    }

    whatToMatch = Pattern.compile(pattern);
  }

  public void run() throws IOException {
    DB db = DbFactory.getConnection(databaseLocation, false);
    ReadOptions ro = new ReadOptions();
    ro.snapshot(db.getSnapshot());
    DBIterator iterator = db.iterator(ro);

    while (iterator.hasNext()) {
      Entry<byte[], byte[]> entry = iterator.next();
      String filename = new String(entry.getKey());
      String hash = new String(entry.getValue());
      if (whatToMatch.matcher(filename).find()) {
        System.out.println("For filename:" + filename + ", hash:" + hash);
      }
    }
    iterator.close();
    db.close();
  }
}
