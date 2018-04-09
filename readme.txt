Inserito codice per la gestione di richieste di tipo jsono in PositionServlet.
Il casting da oggetto json a oggetto Position non va: probabilmente è
necessario fare singolarmente i casting a Double e a Date dei valori prelevati
dagli oggetti jason e creare poi gli oggetti Position...