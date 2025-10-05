/* *****************************************************************************
 * Example16 - Records
 * Copyright: (C)2018-2025 Riccardo Vacirca. All right reserved.  
 * License: GNU GPL Version 2. See LICENSE
 *
 * Compile: javac Example16.java
 * Run: java Example16
 * ---
 * Person: Person[name=Alice, age=30]
 * Point: Point[x=10, y=20]
 * Rectangle area: 200.0
 * Square area: 25.0
 * Traditional vs Record equality: true
 * Book genres: [Fiction, Mystery]
 * Library has 3 books
 * Books by Jane Doe: [Book[title=Mystery Novel, author=Jane Doe, genres=[Mystery, Thriller]]]
 * *****************************************************************************
*/

import java.util.List;
import java.util.Objects;

/** Simple record declaration */
record Person(String name, int age) {}

/** Record with validation using compact constructor */
record Point(int x, int y) {
  public Point {
    if (x < 0 || y < 0) {
      throw new IllegalArgumentException("Coordinates must be non-negative");
    }
  }
}

/** Record with methods */
record Rectangle(double width, double height) {
  public double area() {
    return width * height;
  }
  
  public static Rectangle square(double side) {
    return new Rectangle(side, side);
  }
}

/** Traditional class for comparison */
class TodoTraditional {
  private final Long id;
  private final String title;
  private final boolean completed;
  
  public TodoTraditional(Long id, String title, boolean completed) {
    this.id = id;
    this.title = title;
    this.completed = completed;
  }
  
  public Long getId() { return id; }
  public String getTitle() { return title; }
  public boolean isCompleted() { return completed; }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TodoTraditional that = (TodoTraditional) o;
    return completed == that.completed &&
           Objects.equals(id, that.id) &&
           Objects.equals(title, that.title);
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(id, title, completed);
  }
  
  @Override
  public String toString() {
    return "TodoTraditional{id=" + id + ", title='" + title + "', completed=" + completed + '}';
  }
}

/** Record equivalent */
record Todo(Long id, String title, boolean completed) {}

/** Record with collections */
record Book(String title, String author, List<String> genres) {
  public Book {
    genres = List.copyOf(genres); // Defensive copy for immutability
  }
  
  public boolean hasGenre(String genre) {
    return genres.contains(genre);
  }
}

/** Record with nested records and collections */
record Library(String name, List<Book> books) {
  public long bookCount() {
    return books.size();
  }
  
  public List<Book> booksByAuthor(String author) {
    return books.stream()
               .filter(book -> book.author().equals(author))
               .toList();
  }
}

public class Example16 {
  public static void main(String[] args) {
    System.out.printf("\n");
    
    // Simple record usage
    Person person = new Person("Alice", 30);
    System.out.printf("Person: %s\n", person);
    
    // Record with validation
    Point point = new Point(10, 20);
    System.out.printf("Point: %s\n", point);
    
    // Record with methods
    Rectangle rectangle = new Rectangle(10.0, 20.0);
    System.out.printf("Rectangle area: %.1f\n", rectangle.area());
    
    Rectangle square = Rectangle.square(5.0);
    System.out.printf("Square area: %.1f\n", square.area());
    
    // Compare traditional class vs record
    TodoTraditional traditional = new TodoTraditional(1L, "Buy milk", false);
    Todo record = new Todo(1L, "Buy milk", false);
    System.out.printf("Traditional vs Record equality: %b\n", 
                      traditional.getId().equals(record.id()) &&
                      traditional.getTitle().equals(record.title()) &&
                      traditional.isCompleted() == record.completed());
    
    // Records with collections
    Book book1 = new Book("Great Novel", "John Doe", List.of("Fiction", "Mystery"));
    Book book2 = new Book("Mystery Novel", "Jane Doe", List.of("Mystery", "Thriller"));
    Book book3 = new Book("Science Book", "Dr. Smith", List.of("Science", "Education"));
    
    System.out.printf("Book genres: %s\n", book1.genres());
    
    Library library = new Library("City Library", List.of(book1, book2, book3));
    System.out.printf("Library has %d books\n", library.bookCount());
    System.out.printf("Books by Jane Doe: %s\n", library.booksByAuthor("Jane Doe"));
    
    System.out.printf("\n");
  }
}