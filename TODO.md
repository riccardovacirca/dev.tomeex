
- [FIXED] Verificare che la configurazione git del file .env generale venga
          usata globalmente
          Fix: La configurazione git è già implementata correttamente in
          install.sh e nei Makefile dei progetti.

- [FIXED] La generazione di una nuva app con database mediante il makefile
          principale attualmente genera dei file sql compatibili con tutti i
          database e conteneti il codice pee creare tabelle di gestione dei log.
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
