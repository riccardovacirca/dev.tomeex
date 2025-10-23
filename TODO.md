
- [FIXED] La posizione del file di database sqlite nel sistema sia in sviluppo
          che in produzione dovrebbe essere:
          /var/lib/tomeex/data/<artifact_name>.db.
          La cartella /var/lib/tomeex/data se non esiste dovrebbe essere creata
          al momento della installazione principale dopo aver runnato il
          container. Questa cartella dovrebbe essere anche montata in un volume
          mappato sull'host per mantenere la persistenza dei dati.
          Questa strategia va usata anche per la release della applicazione.
          Fix: Implementato il nuovo path standard /var/lib/tomeex/data/ con:
          - Aggiornato .env generale (SQLITE_DATA_DIR=/var/lib/tomeex/data)
          - Aggiornato install.sh (template .env e funzioni database)
          - Aggiornati archetipi context-dev.xml e context-prod.xml
          - Cambiata estensione da .sqlite a .db per uniformità
          - Aggiornato install.sh.template per release produzione (VOLUME, mount path)
          - Migrato database esistente da /var/lib/tomee/sqlite/ al nuovo path
          - Aggiornato progetto dev.tomeex.qd (.env e tutti i context.xml)
          - Aggiornata documentazione docs/PROJECT.md (sincronizzato con CLAUDE.md)
          - Rebuild di tutti gli archetipi completato (2x)
          - Test creazione nuovo database: PASSED
          - Rimossa directory obsoleta /var/lib/tomee/sqlite/
          Risultato: Tutti i database SQLite ora in /var/lib/tomeex/data/ con
          estensione .db, pronti per mount in produzione con Docker volume

- [FIXED] Verificare che la configurazione git del file .env generale venga
          usata globalmente
          Fix: La configurazione git è già implementata correttamente in
          install.sh e nei Makefile dei progetti.

- [FIXED] La generazione di una nuva app con database mediante il makefile
          principale attualmente genera dei file sql compatibili con tutti i
          database e contenenti il codice per creare tabelle di gestione dei log.
          Dal momento che la generazione di una app con db prevede la specifica
          del tipo di server di database (postgres, mariadb, sqlite) generare un
          solo file sql per il server selezionato e denominare il file con il
          nome dell'artefatto.
          Fix: Implementata funzione cleanup_database_sql_files() in install.sh
          che rimuove i file SQL non necessari e rinomina quello corretto in
          {artifactId}.sql

- [FIXED] Tutti i makefile degli artefatti devono essere uguali anche se in
          quelli senza database i target relativi al db sono senza effetto.
          Il target build compila. Il target deploy esegue il deploy delle app
          nella cartella webapp e il deploy delle librerie nella cartella lib
          e nel repo maven locale.
          Il target clean rimuove le app da webapp e la cartella target e le
          librerie da lib e dal repo maven
          Il comando make senza target dovrebbe eseguire build e deploy
          I target dovrebbero essere:
          - help,   restituisce l'help
          - build,  esegue la compilazione
          - deploy, esegue il deploy in base al tipo di artefatto
          - clean,  rimuove l'artefatto da webapp e la cartella target
          - dbcli,  senza argomenti accede al server, con argomento f esegue
                    il file sql o csv
          - db,     esegue make dbcli f=<artefatto>.sql
                    può essere customizzato aggiungendo installazioni
                    il comportamento di db dipende dal contenuto dei file sql
                    eseguiti nel target
          - install esegue clean, db, build, deploy
          - push,   mantenere nello stato attuale
          - pull,   mantenere nello stato attuale
          Fix: Implementati target db e dbcli in tutti i Makefile degli archetipi.
          Le webapp con database eseguono database/${artifactId}.sql.
          Le librerie mostrano un messaggio informativo che il target non è
          applicabile.

- [FIXED] Al termine della creazione di una app bisogna mostrare
          anche l'indirizzo inteno al container Deployed:
          http://localhost:8080/...
          Fix: Implementato in install.sh (linee 1214-1215) e nei Makefile
          degli archetipi (target deploy).

- [FIXED] Aggiungere al progetto una cartella dsl
          per i DSL degli agenti AI
          Fix: Cartella /workspace/dsl creata.

- [FIXED] Quando una webapp viene scaricata da un repo remoto invece di essere
          generata localmente deve esistere un target di installazione che
          esegue tutte le operazioni di deploy

- [FIXED] File PROJECT.md che contiene le specifiche del progetto e dal quale
          generare il file CLAUDE.md mediante un template che incorpora il
          contenuto di PROJECT gestito con install.sh

- [FIXED] Quick deploy non funziona

- [FIXED] La libreria tools non è documentata

- [FIXED] MariaDB startup timeout durante install.sh --mariadb
          Il comando mysqladmin ping falliva senza credenziali causando timeout
          di 60 secondi prima di mostrare errore spurio.
          Fix: Aggiunta autenticazione root al comando mysqladmin ping in
          wait_for_mariadb() (install.sh linee 490-502). Ora usa:
          mysqladmin ping -uroot -p"${MARIADB_ROOT_PASSWORD}" --silent

- [FIXED] SQLite database location non portabile e non compatibile con production
          I database SQLite venivano creati in /workspace/sqlite-data/ che è
          specifico per sviluppo e non segue gli standard Linux.
          Fix: Cambiata posizione in /var/lib/tomee/sqlite/ che:
          - Segue Linux FHS (Filesystem Hierarchy Standard)
          - È compatibile con tutte le distro (Ubuntu, Alpine, etc.)
          - È isolata dal codice applicativo
          - Può essere montata come volume in produzione
          Modificati: .env, install.sh, archetipi context*.xml, docs/PROJECT.md

- [FIXED] Migrazione database SQLite esistenti e cleanup directory obsoleta
          Dopo il cambio di posizione dei database SQLite da /workspace/sqlite-data/
          a /var/lib/tomee/sqlite/, i progetti esistenti dovevano essere aggiornati.
          Fix: Operazioni eseguite:
          - Aggiornato dev.tomeex.qd (.env e context*.xml files)
          - Migrato database qd.sqlite alla nuova posizione
          - Aggiornata documentazione (SETUP.md, CLAUDE.md)
          - Verificata procedura di rimozione database con nuova posizione
          - Rimossa directory obsoleta /workspace/sqlite-data/
          Risultato: Tutti i database SQLite ora esclusivamente in /var/lib/tomee/sqlite/

- [FIXED] Directory SQLite non creata automaticamente durante installazione framework
          La directory /var/lib/tomee/sqlite/ veniva creata solo quando si generava
          la prima webapp con database SQLite, causando potenziali errori.
          Fix: Aggiunta chiamata a create_sqlite_directories() nel flusso principale
          di installazione (install.sh linea 1625), subito dopo wait_for_tomee().
          La directory viene ora creata automaticamente all'avvio del container TomEE,
          indipendentemente dalla creazione di webapp SQLite.
          Risultato: Path di sistema sempre disponibile, creazione webapp SQLite
          semplificata senza necessità di gestione directory.
