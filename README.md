# OpsisDesktopUploader

Application Java desktop compatible Mac et PC pour permettre aux clients d'upload leurs médias directement depuis leurs machines.

## Installation

Voir les **releases** dans le panneau de droite (.dmg pour macOS et installer .exe pour Windows).

## Fonctionnement

L'app est construite en **Java** suivant le **modèle MVC**.
L'interface graphique repose sur la library **Swing**.
La gestion des dépendences ainsi que le packaging sont assurés par **Maven**.

##### Démarrage

Au démarrage, l'app va lire le fichier JSON qui contient les infos de connection du client : [connection-info.json](src/main/resources/connection-info.json)

S'il est vide, on sera redirigé vers l'écran de connexion où on demandera à l'utilisateur d'entrer l'url de la médiathèque ainsi que sa clé API.
Sinon, l'appli se lance directement sur l'écran d'upload.

##### Ajout de fichiers

L'ajout de fichiers peut se faire de 2 manières :
1. Via un bouton en haut à gauche qui lance un explorateur de fichiers classique.
2. Par **drag and drop** directement dans la fenêtre d'upload.

L'app va ensuite générer des miniatures pour chacuns des fichiers dans un autre thread afin de ne pas bloquer l'execution.

Le client peut pendant ce temps vérifier les informations de ses fichiers et modifier leur titre.

##### Upload

L'upload est également géré dans un autre thread et se passe sous forme de requète **HTTP POST** vers l'**API Opsis**.

L'app va récupérer chacuns des fichiers et les mettre dans un buffer pour préparer la requète.
La progression de ses transferts est récupérée au fûr et à mesure et permet l'affichage du progrès sur chaque fichier au niveau de l'interface.
Une fois que tous les fichiers ont été transférés, la requète part et on demande au client d'attendre la finalisation de l'upload

Une fois l'upload terminé, le client peut préparer un nouvel upload, se déconnecter, ou simplement quitter l'appli qui se souviendra de ses infos de connection.

##### Gestion du Threading

La library Java Swing n'étant pas thread safe, les implémentations classiques du threading en Java ne fonctionne pas correctement (les operations s'effectuent bien dans des threads différents mais l'interface graphique reste bloqué pendant ce temps).

On utilise donc l'implémentation propre à cette library : les **SwingWorker**s ([Tutoriel](https://www.geeksforgeeks.org/swingworker-in-java/)).

##### Packaging

Le code Java compilé produit un .jar executable en ligne de commande.
Pour en faire une application classique Mac / PC, on utilise le plugin Maven [**JavaPackager**](https://github.com/fvarrui/JavaPackager).
Ce plugin permet de packager l'application avec ses dépendances, ses ressources ainsi qu'une JVM et de générer un installer spécifique à la plateforme souhaitée.

La configuration de ce plugin se fait dans le [**pom.xml**](pom.xml).
La commande pour lancer la packaging est : ```mvn clean package```.

Il est nécessaire d'utiliser le système correspondant pour générer l'application.
Sur Mac, on obtient .dmg qui contient l'app et un alias vers le dossier Applications.
Sur PC, on obtient un installer .exe

---

> Le reste des informations se trouve directement en commentaires dans le code. 
