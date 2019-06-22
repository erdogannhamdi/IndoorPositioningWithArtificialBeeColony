# IndoorPositioningWithArtificialBeeColony
In this study, position detection is performed by using artificial bee colony algorithm in closed positioning. it is also compared with the trilateration method.


In this study, the location was determined by using artificial bee colony algorithm in closed positioning. 
it is also compared with the trilateration method. 
Beacon devices in the environment are listed with android application. 
The signals from each beacon are filtered with kalman filter. 

the filtered values ​​are converted to distance. 
The three beacon closest to us are selected and sent to the server. 
Using the distance information and beacon locations, the artificial bee colony algorithm finds the best "x and y" coordinates for us. 
the actual walking path and the path that the algorithm finds are shown on the graph. 

xampp is used to display the graph.
