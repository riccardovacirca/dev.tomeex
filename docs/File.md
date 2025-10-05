# File

Java file system abstraction layer providing simplified access to essential file operations with support for reading, writing, appending, and copying.

## Classes

[File](#file-1) - File abstraction layer for essential file operations  

## Methods

#### File Lifecycle

void [open](#open)() throws Exception
void [close](#close)()  

#### Read Operations

String [read](#read)() throws Exception
String [readLine](#readline)() throws Exception  

#### Write Operations

void [write](#write)(String content) throws Exception  
void [append](#append)(String content) throws Exception  

#### File Operations

void [copy](#copy)(String destination) throws Exception  
void [move](#move)(String destination) throws Exception  
void [delete](#delete)() throws Exception  

#### File Information

boolean [exists](#exists)()  
long [size](#size)() throws Exception  
long [lastModified](#lastmodified)() throws Exception  
boolean [isFile](#isfile)()  
String [getAbsolutePath](#getabsolutepath)() throws Exception  
String [getName](#getname)()  
String [getExtension](#getextension)()  

# Class Documentation

## File

`String filePath` - Current file path
`BufferedReader bufferedReader` - Buffered reader for line reading
`boolean open` - File open status flag  

Minimalist file abstraction layer providing essential file operations. Requires explicit open() call before operations and close() after use.

**Constructor:**
```java
public File(String path)
```

**Parameters:**
- `path` - File path (immutable, cannot be changed after construction)

**Key Features:**
- **Explicit Lifecycle** - Must open before operations, close after use
- **Flexible Reading** - Read entire content or read line by line
- **Simple Writing** - Write entire content or append
- **File Copy** - Copy files with automatic overwrite

**Dependencies:**
- Java 17+
- java.io and java.nio.file packages

**Example:**
```java
import jtools.File;

// Create File instance with file path
File file = new File("/path/to/document.txt");

// File is not open yet, must call open() before operations
try {
    file.open();

    // Read entire content
    String content = file.read();
    System.out.println("Content: " + content);

} catch (Exception e) {
    e.printStackTrace();
} finally {
    // Always close when done
    file.close();
}
```

[↑ Classes](#classes)

# Method Documentation

## open

```java
public void open() throws Exception
```

**Description:**
Opens the file for operations. Must be called before any file operations. The file path is set in the constructor and cannot be changed.

**Parameters:**
- None

**Return value:**
- `void` - No return value

**Exceptions:**
- `Exception` - If file operation fails

**Example:**
```java
import jtools.File;

// File path is set in constructor
File file = new File("/path/to/document.txt");

try {
    // Open file for operations
    file.open();
    System.out.println("File opened successfully");

    // Perform file operations...
    String content = file.read();

} catch (Exception e) {
    System.err.println("Failed to open file: " + e.getMessage());
    e.printStackTrace();
} finally {
    file.close();
}
```

[↑ Methods](#methods)

## close

```java
public void close()
```

**Description:**
Closes the file and releases all internal resources (BufferedReader). Safe to call multiple times.

**Parameters:**
- None

**Return value:**
- `void` - No return value

**Resource Management:**
- Always call in finally block or use try-with-resources
- Closes any open readers/streams
- Resets internal state

**Example:**
```java
import jtools.File;

File file = new File("/path/to/document.txt");

try {
    file.open();

    // Perform file operations
    String content = file.read();
    System.out.println("Content length: " + content.length());

} catch (Exception e) {
    e.printStackTrace();
} finally {
    // Always close file in finally block
    file.close();
    System.out.println("File closed");
}
```

[↑ Methods](#methods)

## read

```java
public String read() throws Exception
```

**Description:**
Reads the entire file content as a string.

**Parameters:**
- None

**Return value:**
- `String` - Complete file content

**Exceptions:**
- `Exception` - File not open or read error

**Example:**
```java
import jtools.File;

File file = new File("/path/to/document.txt");

try {
    file.open();

    // Read entire file as string
    String content = file.read();
    System.out.println("File content:");
    System.out.println(content);
    System.out.println("Total length: " + content.length() + " characters");

} catch (Exception e) {
    e.printStackTrace();
} finally {
    file.close();
}
```

[↑ Methods](#methods)

## readLine

```java
public String readLine() throws Exception
```

**Description:**
Reads one line from the file. Maintains file position for sequential line reading. Returns null when end of file is reached.

**Parameters:**
- None

**Return value:**
- `String` - Next line from file (without line terminator)
- `null` - End of file reached

**Exceptions:**
- `Exception` - File not open or read error

**Usage Pattern:**
Sequential line reading. File position is maintained between calls.

**Example:**
```java
import jtools.File;

File file = new File("/path/to/document.txt");

try {
    file.open();

    // Read file line by line
    String line;
    int lineNumber = 1;

    while ((line = file.readLine()) != null) {
        System.out.println(lineNumber + ": " + line);
        lineNumber++;
    }

    System.out.println("Total lines: " + (lineNumber - 1));

} catch (Exception e) {
    e.printStackTrace();
} finally {
    file.close();
}
```

[↑ Methods](#methods)

## write

```java
public void write(String content) throws Exception
```

**Description:**
Writes content to the file, overwriting any existing content.

**Parameters:**
- `content` - String content to write

**Return value:**
- `void` - No return value

**Exceptions:**
- `Exception` - File not open or write error

**Example:**
```java
import jtools.File;

File file = new File("/path/to/document.txt");

try {
    file.open();

    // Write content (overwrites existing content)
    file.write("Hello, World!\nThis is a test file.\n");
    System.out.println("Content written successfully");

    // Read back to verify
    String content = file.read();
    System.out.println("Verified content: " + content);

} catch (Exception e) {
    e.printStackTrace();
} finally {
    file.close();
}
```

[↑ Methods](#methods)

## append

```java
public void append(String content) throws Exception
```

**Description:**
Appends content to the end of the file without overwriting existing content.

**Parameters:**
- `content` - String content to append

**Return value:**
- `void` - No return value

**Exceptions:**
- `Exception` - File not open or write error

**Example:**
```java
import jtools.File;

File file = new File("/path/to/document.txt");

try {
    file.open();

    // Write initial content
    file.write("First line\n");

    // Append additional content
    file.append("Second line\n");
    file.append("Third line\n");

    System.out.println("Content appended successfully");

    // Read back to verify
    String content = file.read();
    System.out.println("Final content:");
    System.out.println(content);

} catch (Exception e) {
    e.printStackTrace();
} finally {
    file.close();
}
```

[↑ Methods](#methods)

## copy

```java
public void copy(String destination) throws Exception
```

**Description:**
Copies the file to a destination path, replacing destination if it exists.

**Parameters:**
- `destination` - Destination file path

**Return value:**
- `void` - No return value

**Exceptions:**
- `Exception` - File not open, source doesn't exist, or copy error

**Example:**
```java
import jtools.File;

File file = new File("/path/to/source.txt");

try {
    file.open();

    // Copy file to new location
    file.copy("/path/to/backup/source-backup.txt");
    System.out.println("File copied successfully");

    // Original file remains open and can be used
    String content = file.read();
    System.out.println("Original file content: " + content);

} catch (Exception e) {
    e.printStackTrace();
} finally {
    file.close();
}
```

[↑ Methods](#methods)

## move

```java
public void move(String destination) throws Exception
```

**Description:**
Moves the file to a destination path, replacing destination if it exists. Updates internal file path reference.

**Parameters:**
- `destination` - Destination file path

**Return value:**
- `void` - No return value

**Exceptions:**
- `Exception` - File not open, source doesn't exist, or move error

**Behavior:**
- Updates internal filePath to new location
- File remains open after move

**Example:**
```java
import jtools.File;

File file = new File("/path/to/source.txt");

try {
    file.open();

    // Move file to new location
    file.move("/path/to/destination.txt");
    System.out.println("File moved successfully");

    // File is still open at new location
    String content = file.read();
    System.out.println("Content at new location: " + content);

} catch (Exception e) {
    e.printStackTrace();
} finally {
    file.close();
}
```

[↑ Methods](#methods)

## delete

```java
public void delete() throws Exception
```

**Description:**
Deletes the file from the filesystem.

**Parameters:**
- None

**Return value:**
- `void` - No return value

**Exceptions:**
- `Exception` - File not open, doesn't exist, or delete error

**Example:**
```java
import jtools.File;

File file = new File("/path/to/temporary.txt");

try {
    file.open();

    // Check if file exists before deleting
    if (file.exists()) {
        file.delete();
        System.out.println("File deleted successfully");
    }

} catch (Exception e) {
    e.printStackTrace();
} finally {
    file.close();
}
```

[↑ Methods](#methods)

## exists

```java
public boolean exists()
```

**Description:**
Checks if the file exists on the filesystem.

**Parameters:**
- None

**Return value:**
- `true` - File exists
- `false` - File doesn't exist or not open

**Example:**
```java
import jtools.File;

File file = new File("/path/to/document.txt");

try {
    file.open();

    if (file.exists()) {
        System.out.println("File exists, reading content...");
        String content = file.read();
        System.out.println("Content: " + content);
    } else {
        System.out.println("File does not exist, creating new file...");
        file.write("New file content");
    }

} catch (Exception e) {
    e.printStackTrace();
} finally {
    file.close();
}
```

[↑ Methods](#methods)

## size

```java
public long size() throws Exception
```

**Description:**
Returns the file size in bytes.

**Parameters:**
- None

**Return value:**
- `long` - File size in bytes

**Exceptions:**
- `Exception` - File not open or doesn't exist

**Example:**
```java
import jtools.File;

File file = new File("/path/to/document.txt");

try {
    file.open();

    long fileSize = file.size();
    System.out.println("File size: " + fileSize + " bytes");

    // Decide reading strategy based on size
    if (fileSize > 1024 * 1024) {
        System.out.println("Large file, reading line by line...");
        String line;
        while ((line = file.readLine()) != null) {
            // Process line
        }
    } else {
        System.out.println("Small file, reading all at once...");
        String content = file.read();
    }

} catch (Exception e) {
    e.printStackTrace();
} finally {
    file.close();
}
```

[↑ Methods](#methods)

## lastModified

```java
public long lastModified() throws Exception
```

**Description:**
Returns the last modification timestamp in milliseconds since epoch (January 1, 1970, 00:00:00 GMT).

**Parameters:**
- None

**Return value:**
- `long` - Timestamp in milliseconds

**Exceptions:**
- `Exception` - File not open or doesn't exist

**Example:**
```java
import jtools.File;
import java.util.Date;

File file = new File("/path/to/document.txt");

try {
    file.open();

    long timestamp = file.lastModified();
    Date lastModified = new Date(timestamp);

    System.out.println("Last modified: " + lastModified);

    // Check if file is older than 1 day
    long oneDayAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000);
    if (timestamp < oneDayAgo) {
        System.out.println("File is more than 1 day old");
    }

} catch (Exception e) {
    e.printStackTrace();
} finally {
    file.close();
}
```

[↑ Methods](#methods)

## isFile

```java
public boolean isFile()
```

**Description:**
Checks if the path refers to a regular file (not a directory).

**Parameters:**
- None

**Return value:**
- `true` - Path is a regular file
- `false` - Path is not a file (could be directory) or not open

**Example:**
```java
import jtools.File;

File file = new File("/path/to/something");

try {
    file.open();

    if (file.isFile()) {
        System.out.println("This is a regular file");
        String content = file.read();
        System.out.println("Content: " + content);
    } else {
        System.out.println("This is not a regular file (might be a directory)");
    }

} catch (Exception e) {
    e.printStackTrace();
} finally {
    file.close();
}
```

[↑ Methods](#methods)

## getAbsolutePath

```java
public String getAbsolutePath() throws Exception
```

**Description:**
Returns the absolute path of the file.

**Parameters:**
- None

**Return value:**
- `String` - Absolute file path

**Exceptions:**
- `Exception` - File not open

**Example:**
```java
import jtools.File;

File file = new File("document.txt");

try {
    file.open();

    String absolutePath = file.getAbsolutePath();
    System.out.println("Absolute path: " + absolutePath);

    String relativePath = "document.txt";
    System.out.println("Relative path: " + relativePath);

} catch (Exception e) {
    e.printStackTrace();
} finally {
    file.close();
}
```

[↑ Methods](#methods)

## getName

```java
public String getName()
```

**Description:**
Returns the file name (without directory path).

**Parameters:**
- None

**Return value:**
- `String` - File name
- `null` - File not open

**Example:**
```java
import jtools.File;

File file = new File("/path/to/document.txt");

try {
    file.open();

    String name = file.getName();
    System.out.println("File name: " + name); // Outputs: document.txt

    String extension = file.getExtension();
    System.out.println("Extension: " + extension); // Outputs: txt

} catch (Exception e) {
    e.printStackTrace();
} finally {
    file.close();
}
```

[↑ Methods](#methods)

## getExtension

```java
public String getExtension()
```

**Description:**
Returns the file extension (characters after the last dot in filename).

**Parameters:**
- None

**Return value:**
- `String` - File extension without dot (e.g., "txt", "java", "png")
- `null` - No extension, no filename, or file not open

**Example:**
```java
import jtools.File;

File file = new File("/path/to/document.txt");

try {
    file.open();

    String extension = file.getExtension();
    System.out.println("Extension: " + extension); // Outputs: txt

    // Use extension to determine file type
    if ("txt".equals(extension)) {
        System.out.println("This is a text file");
        String content = file.read();
    } else if ("jpg".equals(extension) || "png".equals(extension)) {
        System.out.println("This is an image file");
    }

} catch (Exception e) {
    e.printStackTrace();
} finally {
    file.close();
}
```

[↑ Methods](#methods)

---

@2020-2025 Riccardo Vacirca - All right reserved.
