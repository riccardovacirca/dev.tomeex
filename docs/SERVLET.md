
# Stile di codifica di una servlet

### Import della clsse

```java
import dev.tomeex.tools.ApiResponse;
import dev.tomeex.tools.Database;
import dev.tomeex.tools.HttpRequest;
import dev.tomeex.tools.JSON;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
```

### Ordine di definizione dei metodi

La definizione dei metodi deve seguire il seguente ordine:

- doPost
- doPut
- doDelete
- doGet

### Denominazione request, response

HttpServletRequest req, HttpServletResponse res

### Struttura di un metodo della servlet

Tutti metodi della servlet di tipo doGet, doPost, ..., iniziano con
  
```java
res.setContentType("application/json");
res.setCharacterEncoding("UTF-8");
```  

seguito dal blocco try/catch principale.

### Lettura del body JSON della request

Eseguito con la classe dev.tomeex.tools.HttpRequest

```java
StringBuilder jsonBuilder = new StringBuilder();
BufferedReader reader = req.getReader();
String line;
while ((line = reader.readLine()) != null) {
  jsonBuilder.append(line);
}
String jsonBody = jsonBuilder.toString();
```

### Policy descriptor-based (solo web.xml)

- Configurazione servlet senza annotazioni.
- Nessun import jakarta.servlet.annotation.WebServlet
- Deve essere usato solo web.xml

### Parentesi di blocco

- La parentesi graffa di apertura di un metodo deve stare su una sola riga
- L'indentazione è a 2 spazi
- La dichiarazione throws è indentata rispetto alla parentesi di apertura