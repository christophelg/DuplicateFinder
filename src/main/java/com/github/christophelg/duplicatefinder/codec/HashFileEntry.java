package com.github.christophelg.duplicatefinder.codec;

public class HashFileEntry {

  private String hash;
  private String file;

  public HashFileEntry(String h, String f) {
    hash = h;
    file = f;
  }

  public String getHash() {
    return hash;
  }

  public String getFile() {
    return file;
  }

}
