
- [NEW]   I file di infrastruttura di un progetto come ad esempio il makefile
          del progetto dovrebbero essere sincronizzati con quelli dell'archetipo
          se questi ultimi vengono aggiornati.

- [FIXED] Al termine della creazione di una app bisogna mostrare
          anche l'indirizzo inteno al container Deployed: http://localhost:8080

- [NEW]   Aggiungere al progetto una cartella tomeex-dsl per i DSL degli agenti AI

- [NEW]   Implementare un semplice sistema di DB migration gestite tramite
          Makefile
          File: sql_20251005103000_create_db.sql
          Requisiti:
            - Il file contiene semplice SQL
            - Il codie SQL del file è idempotente.
              Es. CREATE TABLE IF NOT EXISTS... oppure INSERT... ON DUPLICATE KEY...

- [FIXED] Quando una webapp viene scaricata da un repo remoto invece di essere
          generata localmente deve esistere un target di installazione che
          esegue tutte le operazioni di deploy

- [FIXED] File PROJECT.md che contiene le specifiche del progetto e dal quale
          generare il file CLAUDE.md mediante un template che incorpora il
          contenuto di PROJECT gestito con install.sh

- [FIXED] Quick deploy non funziona

- [FIXED] La libreria tools non è documentata
