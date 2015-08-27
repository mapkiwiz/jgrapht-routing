CREATE OR REPLACE FUNCTION bdtopo.nearest_vertices_lonlat (origin_lon double precision, origin_lat double precision, max integer default 1)
RETURNS TABLE (id integer, lon double precision, lat double precision)
AS
$func$
DECLARE

    geom geometry(Point);

BEGIN

    geom := st_transform(st_setsrid(st_makepoint(origin_lon, origin_lat), 4326), 2154);

    RETURN QUERY
        SELECT s.id::integer, st_x(wgs84_geom) as lon, st_y(wgs84_geom) as lat
        FROM (SELECT v.id, st_transform(v.the_geom, 4326) as wgs84_geom
              FROM bdtopo.routes_vertices_pgr v
              ORDER BY the_geom <-> geom
              LIMIT max) s ;

END
$func$
LANGUAGE plpgsql;