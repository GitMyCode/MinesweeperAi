<h3>Intelligence Articifielle pour un jeu de Minesweeper.</h3>

![Alt text](MineSweeper%20Ai.gif)


Intelligence Articifiel pour un jeu de Minesweeper
=======
Cette version procède qu'avec CSP(Constraint satisfaction problem). Il calcul toutes les possibles positions de flags qui satisfont les indices, puis essai toutes les cases qui n'ont aucune chance d'avoir une mine. Sinon il procède par le hasard. Le taux de succès reste quand meme très élevé.

Aussi l'algorithme est très lent. Dans les pire cas il y a N! opperation. Donc 10! = 3 628 800 operation.
Une possible optimisation serait de segmenter le probleme. 10! > 5! + 5!  = 240 operation.


