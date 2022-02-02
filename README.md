# CarburantiApp

- **Anno Accademico**: 2020-2021
- **Nome del progetto**: Geolocalizzazione di stazioni di servizio tramite applicazione per dispositivi android
- **Autore**: Bernardo Tiezzi
- **CFU**: 9 

## Overviews
L'idea è creare un'applicazione che gestisca una community di condivisione dei prezzi di carburante (benzina, gasolio, GPL, metano), forniti dalle stazioni di servizio, geolocalizzate su una mappa. Tra le possibili funzionalità:
- [x] individuare su una mappa le stazioni di servizio nelle vicinanze dell'utente, se queste risultano presenti all'interno del database utilizzato, assieme alle informazioni relative al prezzo del carburante di interesse.
- [x] avviare la navigazione verso il distributore selezionato
- [x] aggiungere o rimuovere i pin sulla mappa a seconda del gestore dei distributori o della tipologia di carburante scelto

Al termine della fase di sviluppo, saranno eseguiti *usability test* per verificare il livello di apprezzamento da parte di utenti arbitrari nei confronti dell'applicazione. I risultati ottenuti saranno riportati nella relazione finale. 

## Dati necessari
Per realizzare il progetto è necessario disporre di:
- un DataBase gratuito, contenente informazioni concernenti le stazioni di rifornimento  
- un dispositivo di tipo Android (smartphone, tablet, ...)
- API di gestione di mappe gratuite

## MockUp
Di seguito viene presentato il mockup dell'applicazione:
<img src="https://github.com/Tiezzi96/PrezziCarburante/blob/main/app1.PNG?raw=true" width="75%" />
<img src="https://github.com/Tiezzi96/PrezziCarburante/blob/main/app2.PNG?raw=true" width="75%" />

## Personas
Sono state individuate le seguenti **personas**:

**1**
- **Nome**: Susanna
- **Data di Nascita**: 18/04/1962
- **Residenza**: Firenze
- **Professione**: Vicepreside
- **Lavoro**: Susanna è un'insegnante che svolge la funzione di vicario del dirigente scolastico, lavora presso un'istituto comprensivo esterno al proprio comune di residenza. Durante la giornata lavorativa è solita percorrere diversi chilometri per raggiungere l'istituto e predilige percorrere strade a rapido scorrimento. è proprietaria di una utilitaria Fiat Punto a gasolio. 
- **Tempo Libero**: Susanna ha stretto amicizia con sue colleghe di lavoro, che risiedono al di fuori del suo comune. Le piace fare jogging ma predilige le lunghe camminate in compagnia delle amiche.

**2**
- **Nome**: Manuel
- **Data di Nascita**: 13/06/1998
- **Residenza**: Prato
- **Professione**: Dipendente privato
- **Lavoro**: Manuel è un giovane dipendente per una ditta di materiali edili. Tale azienda si trova al di fuori del suo comune di residenza. Il suo lavoro può richiedere di lavorare al banco o di effettuare consegne a clienti o aziende private. Per le consegne può utilizzare furgoni di piccola taglia o camion di medie dimensioni. L'itinerario delle consegne è variabile. L'azienda fornisce mensilmente al dipendente buoni benzina da poter spendere in stazioni di senvizio con gestore Eni.
- **Tempo Libero**: Manuel è solito frequentare amici di infanzia che abitano nel suo stesso comune. Ha una relazione ed esce  abitualmente il sabato sera. Predilige visitare città o centri commerciali, frequentare discoteche.

## Scenari

**1**
- **Personas**: Manuel
- **Descrizione**: Manuel sta effettuando una consegna per la ditta per cui lavora. Durante il percorso necessita di fermarsi per fare rifornimento. Poichè la sua azienda gli fornisce dei buoni carburante, Manuel predilige rifornirsi presso i gestori convenzionati. Utilizzando l'applicazione è in grado di individuare il distributore più vicino che gli consenta di utilizzare il buono.


**2**
- **Personas**: Susanna
- **Descrizione**: Susanna si sta recando da una sua amica la domenica pomeriggio. Ha intenzione di intraprendere una lunga camminata in mezzo alla natura. Entrambe hanno deciso di utilizzare un unico mezzo di trasporto è dividere la spesa del carburante. Scaricando l'applicazione sono in grado di individuare il distributore più vicino con il prezzo più basso. 


## Ambiente di sviluppo
L'applicazione sarà sviluppata in linguaggio Java sfruttando l'IDE per applicazioni android **AndroidStudio**.

## Difficoltà 
L'applicazione può risultare inefficiente nel caso non siano garantiti i permessi per l'accesso alla posizione del dispositivo.
