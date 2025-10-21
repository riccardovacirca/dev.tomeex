
- [NEW]   Verificare che la configurazione git del file .env generale venga
          usata globalmente

- [NEW]   Chiarire meglio la semantica delle operazioni di debloy/installazione.
          L'interfaccia tra app e lib dovrebbe essere uniformata.
          Il comando make senga target dovrebbe eseguire build e deploy
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

- [NEW]   I file di infrastruttura di un progetto come ad esempio il makefile
          del progetto dovrebbero essere sincronizzati con quelli dell'archetipo
          se questi ultimi vengono aggiornati.

- [FIXED] Al termine della creazione di una app bisogna mostrare
          anche l'indirizzo inteno al container Deployed: http://localhost:8080

- [NEW]   Aggiungere al progetto una cartella tomeex-dsl
          per i DSL degli agenti AI

- [FIXED] Quando una webapp viene scaricata da un repo remoto invece di essere
          generata localmente deve esistere un target di installazione che
          esegue tutte le operazioni di deploy

- [FIXED] File PROJECT.md che contiene le specifiche del progetto e dal quale
          generare il file CLAUDE.md mediante un template che incorpora il
          contenuto di PROJECT gestito con install.sh

- [FIXED] Quick deploy non funziona

- [FIXED] La libreria tools non è documentata
