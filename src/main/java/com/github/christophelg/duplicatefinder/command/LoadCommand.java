package com.github.christophelg.duplicatefinder.command;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.iq80.leveldb.DB;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.github.christophelg.duplicatefinder.codec.HashFileCodec;
import com.github.christophelg.duplicatefinder.codec.HashFileEntry;
import com.github.christophelg.duplicatefinder.leveldb.DbFactory;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

@Parameters(separators = "=", commandDescription = "Load a directory full of cksum files into leveldb")
public class LoadCommand implements Command {

  @Parameter(names = {"--database"}, description = "Where to find the database", required = true)
  private String database;

  private File databaseLocation;

  @Parameter(names = {"--hashes"}, description = "Where to find the hashes files", required = true)
  private String hashesDirectory;

  private File hashesLocation;

  public void validate() {
    databaseLocation = new File(database);
    if (databaseLocation.exists() && !databaseLocation.isDirectory()) {
      throw new IllegalArgumentException("Database location must be a directory");
    }

    hashesLocation = new File(hashesDirectory);
    if (!hashesLocation.isDirectory()) {
      throw new IllegalArgumentException("Hashes location must be a directory");
    }
  }

  public void run() throws IOException {
    DB db = DbFactory.getConnection(databaseLocation, true);

    System.out.println("Reading hashes files from:" + hashesLocation);
    for (File oneHashFile : hashesLocation.listFiles(hashFiles)) {
      System.out.println("now processing:" + oneHashFile);
      Files.readLines(oneHashFile, Charsets.US_ASCII, new LoadHashFileProcessor(db));
    }

    db.close();
  }

  private FilenameFilter hashFiles = new FilenameFilter() {

    @Override
    public boolean accept(File arg0, String pathname) {
      return pathname.endsWith(".cksum");
    }

  };

  private static class LoadHashFileProcessor implements LineProcessor<Boolean> {
    private DB hashDb;
    private HashFileCodec codec = new HashFileCodec();

    public LoadHashFileProcessor(DB hDb) {
      hashDb = hDb;
    }

    @Override
    public Boolean getResult() {
      return true;
    }

    @Override
    public boolean processLine(String s) throws IOException {
      HashFileEntry entry = codec.decode(s);
      hashDb.put(entry.getFile().getBytes(), entry.getHash().getBytes());
      return true;
    }

  };
}
