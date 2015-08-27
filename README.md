# Java Routing Machine implemented on JGraphT

![Build status](https://travis-ci.org/mapkiwiz/jgrapht-routing.svg)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.mapkiwiz/jgrapht-routing/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.github.mapkiwiz/jgrapht-routing)

## Build and run

This will start a Tomcat container at http://localhost:8080/routing with some test data.

The test data cover the [Rhône-Alpes](https://fr.wikipedia.org/wiki/Rh%C3%B4ne-Alpes) region in France 

```
mvn clean install tomcat7:run
```

## Example queries

Test data are in geographic coordinates (WGS84, epsg:4326) with time(cost) = distance (meters).

Using [HTTPie](https://github.com/jkbrzt/httpie) client :

### Locate nearest network node around Lyon (4.834413,45.767304) :

```
http GET :8080/routing/api/v1/distance lon==4.834413 lat==45.767304
```
Returns a GeoJSON Point :
```json
{
    "geometry": {
        "coordinates": [
            4.834413, 
            45.767304
        ], 
        "type": "Point"
    }, 
    "properties": {
        "id": 24951, 
        "request_lat": 45.767304, 
        "request_lon": 4.834413
    }, 
    "type": "Feature"
}
```

### Distance from Lyon (4.834413,45.767304) to Valence (4.890021,44.930435) :

```
http GET :8080/routing/api/v1/distance source==4.834413,45.767304 target==4.890021,44.930435
```
Returns :
```json
{
    "distance": 100497.0, 
    "distance_unit": "meters", 
    "source": {
        "coordinates": [
            4.834413, 
            45.767304
        ], 
        "type": "Point"
    }, 
    "target": {
        "coordinates": [
            4.890021, 
            44.930435
        ], 
        "type": "Point"
    }, 
    "time": 100497.0, 
    "time_unit": "minutes"
}
```

### Route from Lyon to Valence :

```
http GET :8080/routing/api/v1/route source==4.834413,45.767304 target==4.890021,44.930435
```
Returns GeoJSON LineString.

### 30 km isochrone around Valence :
```
http GET :8080/routing/api/v1/isochrone lon==4.890021 lat==44.930435 distance==30000 concave==true
```
Returns GeoJSON Polygon.

## Using as a library

Add the following dependency to your project pom.xml :

```xml
<dependency>
  <groupId>com.github.mapkiwiz</groupId>
  <artifactId>jgrapht-routing-core</artifactId>
  <version>0.4</version>
</dependency>
```

## Feeding in your own data

TODO

## Getting data from OpenStreetMap

TODO
