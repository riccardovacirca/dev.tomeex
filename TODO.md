- La procedura di installazione di una libreria mediante il target install del
  Makefile del progetto genera un file META-INF/maven/dev.tomeex/tools/pom.properties
  e copia il JAR della libreria nella cartella /workspace/lib.
  Durante una nuova installazione di tomeex l'installer prova ad installare le
  librerie presenti nella cartella /workspace/lib nel repo locale di maven,
  tuttavia sembra non riuscire a rilevare la presenza del file pom.properties.
  In occasione di una nuova installazione di TomEEx verificare il contenuto
  del JAR.