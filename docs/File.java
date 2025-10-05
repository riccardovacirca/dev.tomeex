package jtools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class File
{
  private String filePath;
  private BufferedReader bufferedReader;
  private boolean open;

  public File(String path) {
    this.filePath = path;
    this.bufferedReader = null;
    this.open = false;
  }

  public void open() throws Exception
  {
    this.open = true;
  }

  public void close()
  {
    try {
      if (this.bufferedReader != null) {
        this.bufferedReader.close();
        this.bufferedReader = null;
      }
    } catch (IOException e) {}
    this.open = false;
  }

  public String read() throws Exception
  {
    if (!this.open) {
      throw new Exception("File not open");
    }

    return new String(Files.readAllBytes(Paths.get(this.filePath)));
  }

  public String readLine() throws Exception
  {
    if (!this.open) {
      throw new Exception("File not open");
    }

    if (this.bufferedReader == null) {
      this.bufferedReader = new BufferedReader(new FileReader(this.filePath));
    }

    return this.bufferedReader.readLine();
  }

  public void write(String content) throws Exception
  {
    if (!this.open) {
      throw new Exception("File not open");
    }

    Files.write(Paths.get(this.filePath), content.getBytes());
  }

  public void append(String content) throws Exception
  {
    if (!this.open) {
      throw new Exception("File not open");
    }

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.filePath, true))) {
      writer.write(content);
    }
  }

  public void copy(String destination) throws Exception
  {
    if (!this.open) {
      throw new Exception("File not open");
    }

    java.nio.file.Path source = Paths.get(this.filePath);
    java.nio.file.Path dest = Paths.get(destination);

    if (!Files.exists(source)) {
      throw new Exception("Source file does not exist: " + this.filePath);
    }

    Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
  }

  public void move(String destination) throws Exception
  {
    if (!this.open) {
      throw new Exception("File not open");
    }

    java.nio.file.Path source = Paths.get(this.filePath);
    java.nio.file.Path dest = Paths.get(destination);

    if (!Files.exists(source)) {
      throw new Exception("Source file does not exist: " + this.filePath);
    }

    Files.move(source, dest, StandardCopyOption.REPLACE_EXISTING);

    // Update internal references to new location
    this.filePath = destination;
  }

  public void delete() throws Exception
  {
    if (!this.open) {
      throw new Exception("File not open");
    }

    java.nio.file.Path p = Paths.get(this.filePath);

    if (!Files.exists(p)) {
      throw new Exception("File does not exist: " + this.filePath);
    }

    Files.delete(p);
  }

  public boolean exists()
  {
    if (!this.open) {
      return false;
    }

    return Files.exists(Paths.get(this.filePath));
  }

  public long size() throws Exception
  {
    if (!this.open) {
      throw new Exception("File not open");
    }

    java.nio.file.Path p = Paths.get(this.filePath);

    if (!Files.exists(p)) {
      throw new Exception("File does not exist: " + this.filePath);
    }

    return Files.size(p);
  }

  public long lastModified() throws Exception
  {
    if (!this.open) {
      throw new Exception("File not open");
    }

    java.io.File f = new java.io.File(this.filePath);

    if (!f.exists()) {
      throw new Exception("File does not exist: " + this.filePath);
    }

    return f.lastModified();
  }

  public boolean isFile()
  {
    if (!this.open) {
      return false;
    }

    return Files.isRegularFile(Paths.get(this.filePath));
  }

  public String getAbsolutePath() throws Exception
  {
    if (!this.open) {
      throw new Exception("File not open");
    }

    return Paths.get(this.filePath).toAbsolutePath().toString();
  }

  public String getName()
  {
    if (!this.open) {
      return null;
    }

    java.nio.file.Path p = Paths.get(this.filePath);
    return p.getFileName() != null ? p.getFileName().toString() : null;
  }

  public String getExtension()
  {
    String name = this.getName();
    if (name == null) {
      return null;
    }

    int lastDot = name.lastIndexOf('.');
    if (lastDot > 0 && lastDot < name.length() - 1) {
      return name.substring(lastDot + 1);
    }

    return null;
  }
}
