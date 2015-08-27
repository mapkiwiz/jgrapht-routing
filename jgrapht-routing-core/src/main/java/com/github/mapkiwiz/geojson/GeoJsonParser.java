package com.github.mapkiwiz.geojson;

import java.util.List;
import java.util.Map;

import com.google.gson.GsonBuilder;

@SuppressWarnings("unchecked")
public class GeoJsonParser {
	
	public GeoJsonObject parse(String json) throws GeoJsonFormatException {
		
		GsonBuilder gson = new GsonBuilder();
		Map<String, Object> map = gson.create().fromJson(json, Map.class);
		if (!map.containsKey("type")) {
			throw new GeoJsonFormatException("Missing type member ");
		}
		
		return parseObject(map);
		
	}
	
	private GeoJsonObject parseObject(Map<String, Object> jsonObj) throws GeoJsonFormatException {
		
		String type = (String) jsonObj.get("type");
		
		if ("FeatureCollection".equals(type)) {
			FeatureCollection<Geometry<?>> obj =
					new FeatureCollection<Geometry<?>>();
			return obj;
		} else if ("Feature".equals(type)) {
			Feature<Geometry<?>> obj = new Feature<Geometry<?>>();
			obj.properties = (Map<String, Object>) jsonObj.get("properties");
			obj.geometry = parseGeometry((Map<String, Object>) jsonObj.get("geometry"));
			return obj;
		} else if ("Point".equals(type)) {
			Point obj = new Point();
			obj.coordinates = (List<Double>) jsonObj.get("coordinates");
			return obj;
		} else if ("LineString".equals(type)) {
			LineString obj = new LineString();
			obj.coordinates = (List<List<Double>>) jsonObj.get("coordinates");
			return obj;
		} else if ("Polygon".equals(type)) {
			Polygon obj = new Polygon();
			obj.coordinates = (List<List<List<Double>>>) jsonObj.get("coordinates");
			return obj;
		} else {
			throw new GeoJsonFormatException("Not yet implemented : " + type);
		}
		
	}
	
	private Geometry<?> parseGeometry(Map<String, Object> jsonObj) throws GeoJsonFormatException {
		
		return (Geometry<?>) parseObject(jsonObj);
		
	}

}
