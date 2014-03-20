

Intelligence Articifielle pour un jeu de Minesweeper.

Cette version procède qu'avec CSP(Constraint satisfaction problem). Il calcul tout les possible position de flag qui satisfont les indice puis essai tout les cases qui n'ont aucune chance d'avoir une mine. Sinon il procède par le hasard. Le taux de succès reste quand meme très élevé.

Aussi l'algorithme est très lent. Dans les pire cas il y a N! opperation. Donc 10! = 3 628 800 operation.
Une possible optimisation serait de segmenter le probleme. 10! > 5! + 5!  = 240 operation.


![Alt text](https://dl.dropboxusercontent.com/u/14828537/MineSweeper%20Ai.gif)
