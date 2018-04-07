la classe User ora contiene una lista di oggetti di tipo Position

C'è un PositionServlet che valida i dati inseriti della posizione e li aggiunge alla lsita dell'utente corrispettivo facendo accesso su una cuncurrenthashmap username-User

La validazione lancia eccezioni sui controlli del range ma non riesce a vedere quando una marca temporale è strettamente crescente o se la velocità è minore di 100 perchè la mappa non si aggiorna bene