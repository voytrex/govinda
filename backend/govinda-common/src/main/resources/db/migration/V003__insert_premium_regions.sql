/*
 * Govinda ERP - BAG Premium Regions Reference Data
 * Version: V003
 * Description: Inserts BAG premium regions for all cantons
 *
 * Note: Most cantons have 3 regions (urban/suburban/rural),
 * but some smaller cantons have only 1 or 2 regions.
 */

-- Zürich (3 regions)
INSERT INTO premium_region (id, canton_code, region_number, name_de, name_fr) VALUES
    ('10000000-0001-0001-0001-000000000001', 'ZH', 1, 'Zürich Region 1 (Stadt)', 'Zurich Région 1'),
    ('10000000-0001-0001-0002-000000000001', 'ZH', 2, 'Zürich Region 2 (Agglomeration)', 'Zurich Région 2'),
    ('10000000-0001-0001-0003-000000000001', 'ZH', 3, 'Zürich Region 3 (Land)', 'Zurich Région 3');

-- Bern (3 regions)
INSERT INTO premium_region (id, canton_code, region_number, name_de, name_fr) VALUES
    ('10000000-0002-0001-0001-000000000001', 'BE', 1, 'Bern Region 1', 'Berne Région 1'),
    ('10000000-0002-0001-0002-000000000001', 'BE', 2, 'Bern Region 2', 'Berne Région 2'),
    ('10000000-0002-0001-0003-000000000001', 'BE', 3, 'Bern Region 3', 'Berne Région 3');

-- Luzern (3 regions)
INSERT INTO premium_region (id, canton_code, region_number, name_de, name_fr) VALUES
    ('10000000-0003-0001-0001-000000000001', 'LU', 1, 'Luzern Region 1', 'Lucerne Région 1'),
    ('10000000-0003-0001-0002-000000000001', 'LU', 2, 'Luzern Region 2', 'Lucerne Région 2'),
    ('10000000-0003-0001-0003-000000000001', 'LU', 3, 'Luzern Region 3', 'Lucerne Région 3');

-- Uri (1 region - small canton)
INSERT INTO premium_region (id, canton_code, region_number, name_de, name_fr) VALUES
    ('10000000-0004-0001-0001-000000000001', 'UR', 1, 'Uri', 'Uri');

-- Schwyz (2 regions)
INSERT INTO premium_region (id, canton_code, region_number, name_de, name_fr) VALUES
    ('10000000-0005-0001-0001-000000000001', 'SZ', 1, 'Schwyz Region 1', 'Schwytz Région 1'),
    ('10000000-0005-0001-0002-000000000001', 'SZ', 2, 'Schwyz Region 2', 'Schwytz Région 2');

-- Obwalden (1 region)
INSERT INTO premium_region (id, canton_code, region_number, name_de, name_fr) VALUES
    ('10000000-0006-0001-0001-000000000001', 'OW', 1, 'Obwalden', 'Obwald');

-- Nidwalden (1 region)
INSERT INTO premium_region (id, canton_code, region_number, name_de, name_fr) VALUES
    ('10000000-0007-0001-0001-000000000001', 'NW', 1, 'Nidwalden', 'Nidwald');

-- Glarus (1 region)
INSERT INTO premium_region (id, canton_code, region_number, name_de, name_fr) VALUES
    ('10000000-0008-0001-0001-000000000001', 'GL', 1, 'Glarus', 'Glaris');

-- Zug (2 regions)
INSERT INTO premium_region (id, canton_code, region_number, name_de, name_fr) VALUES
    ('10000000-0009-0001-0001-000000000001', 'ZG', 1, 'Zug Region 1', 'Zoug Région 1'),
    ('10000000-0009-0001-0002-000000000001', 'ZG', 2, 'Zug Region 2', 'Zoug Région 2');

-- Freiburg (3 regions)
INSERT INTO premium_region (id, canton_code, region_number, name_de, name_fr) VALUES
    ('10000000-0010-0001-0001-000000000001', 'FR', 1, 'Freiburg Region 1', 'Fribourg Région 1'),
    ('10000000-0010-0001-0002-000000000001', 'FR', 2, 'Freiburg Region 2', 'Fribourg Région 2'),
    ('10000000-0010-0001-0003-000000000001', 'FR', 3, 'Freiburg Region 3', 'Fribourg Région 3');

-- Solothurn (3 regions)
INSERT INTO premium_region (id, canton_code, region_number, name_de, name_fr) VALUES
    ('10000000-0011-0001-0001-000000000001', 'SO', 1, 'Solothurn Region 1', 'Soleure Région 1'),
    ('10000000-0011-0001-0002-000000000001', 'SO', 2, 'Solothurn Region 2', 'Soleure Région 2'),
    ('10000000-0011-0001-0003-000000000001', 'SO', 3, 'Solothurn Region 3', 'Soleure Région 3');

-- Basel-Stadt (1 region - city canton)
INSERT INTO premium_region (id, canton_code, region_number, name_de, name_fr) VALUES
    ('10000000-0012-0001-0001-000000000001', 'BS', 1, 'Basel-Stadt', 'Bâle-Ville');

-- Basel-Landschaft (3 regions)
INSERT INTO premium_region (id, canton_code, region_number, name_de, name_fr) VALUES
    ('10000000-0013-0001-0001-000000000001', 'BL', 1, 'Basel-Land Region 1', 'Bâle-Campagne Région 1'),
    ('10000000-0013-0001-0002-000000000001', 'BL', 2, 'Basel-Land Region 2', 'Bâle-Campagne Région 2'),
    ('10000000-0013-0001-0003-000000000001', 'BL', 3, 'Basel-Land Region 3', 'Bâle-Campagne Région 3');

-- Schaffhausen (2 regions)
INSERT INTO premium_region (id, canton_code, region_number, name_de, name_fr) VALUES
    ('10000000-0014-0001-0001-000000000001', 'SH', 1, 'Schaffhausen Region 1', 'Schaffhouse Région 1'),
    ('10000000-0014-0001-0002-000000000001', 'SH', 2, 'Schaffhausen Region 2', 'Schaffhouse Région 2');

-- Appenzell Ausserrhoden (1 region)
INSERT INTO premium_region (id, canton_code, region_number, name_de, name_fr) VALUES
    ('10000000-0015-0001-0001-000000000001', 'AR', 1, 'Appenzell A.Rh.', 'Appenzell Rh.-Ext.');

-- Appenzell Innerrhoden (1 region)
INSERT INTO premium_region (id, canton_code, region_number, name_de, name_fr) VALUES
    ('10000000-0016-0001-0001-000000000001', 'AI', 1, 'Appenzell I.Rh.', 'Appenzell Rh.-Int.');

-- St. Gallen (3 regions)
INSERT INTO premium_region (id, canton_code, region_number, name_de, name_fr) VALUES
    ('10000000-0017-0001-0001-000000000001', 'SG', 1, 'St. Gallen Region 1', 'Saint-Gall Région 1'),
    ('10000000-0017-0001-0002-000000000001', 'SG', 2, 'St. Gallen Region 2', 'Saint-Gall Région 2'),
    ('10000000-0017-0001-0003-000000000001', 'SG', 3, 'St. Gallen Region 3', 'Saint-Gall Région 3');

-- Graubünden (3 regions)
INSERT INTO premium_region (id, canton_code, region_number, name_de, name_fr) VALUES
    ('10000000-0018-0001-0001-000000000001', 'GR', 1, 'Graubünden Region 1', 'Grisons Région 1'),
    ('10000000-0018-0001-0002-000000000001', 'GR', 2, 'Graubünden Region 2', 'Grisons Région 2'),
    ('10000000-0018-0001-0003-000000000001', 'GR', 3, 'Graubünden Region 3', 'Grisons Région 3');

-- Aargau (3 regions)
INSERT INTO premium_region (id, canton_code, region_number, name_de, name_fr) VALUES
    ('10000000-0019-0001-0001-000000000001', 'AG', 1, 'Aargau Region 1', 'Argovie Région 1'),
    ('10000000-0019-0001-0002-000000000001', 'AG', 2, 'Aargau Region 2', 'Argovie Région 2'),
    ('10000000-0019-0001-0003-000000000001', 'AG', 3, 'Aargau Region 3', 'Argovie Région 3');

-- Thurgau (3 regions)
INSERT INTO premium_region (id, canton_code, region_number, name_de, name_fr) VALUES
    ('10000000-0020-0001-0001-000000000001', 'TG', 1, 'Thurgau Region 1', 'Thurgovie Région 1'),
    ('10000000-0020-0001-0002-000000000001', 'TG', 2, 'Thurgau Region 2', 'Thurgovie Région 2'),
    ('10000000-0020-0001-0003-000000000001', 'TG', 3, 'Thurgau Region 3', 'Thurgovie Région 3');

-- Tessin (3 regions)
INSERT INTO premium_region (id, canton_code, region_number, name_de, name_fr) VALUES
    ('10000000-0021-0001-0001-000000000001', 'TI', 1, 'Tessin Region 1', 'Tessin Région 1'),
    ('10000000-0021-0001-0002-000000000001', 'TI', 2, 'Tessin Region 2', 'Tessin Région 2'),
    ('10000000-0021-0001-0003-000000000001', 'TI', 3, 'Tessin Region 3', 'Tessin Région 3');

-- Waadt (3 regions)
INSERT INTO premium_region (id, canton_code, region_number, name_de, name_fr) VALUES
    ('10000000-0022-0001-0001-000000000001', 'VD', 1, 'Waadt Region 1', 'Vaud Région 1'),
    ('10000000-0022-0001-0002-000000000001', 'VD', 2, 'Waadt Region 2', 'Vaud Région 2'),
    ('10000000-0022-0001-0003-000000000001', 'VD', 3, 'Waadt Region 3', 'Vaud Région 3');

-- Wallis (3 regions)
INSERT INTO premium_region (id, canton_code, region_number, name_de, name_fr) VALUES
    ('10000000-0023-0001-0001-000000000001', 'VS', 1, 'Wallis Region 1', 'Valais Région 1'),
    ('10000000-0023-0001-0002-000000000001', 'VS', 2, 'Wallis Region 2', 'Valais Région 2'),
    ('10000000-0023-0001-0003-000000000001', 'VS', 3, 'Wallis Region 3', 'Valais Région 3');

-- Neuenburg (2 regions)
INSERT INTO premium_region (id, canton_code, region_number, name_de, name_fr) VALUES
    ('10000000-0024-0001-0001-000000000001', 'NE', 1, 'Neuenburg Region 1', 'Neuchâtel Région 1'),
    ('10000000-0024-0001-0002-000000000001', 'NE', 2, 'Neuenburg Region 2', 'Neuchâtel Région 2');

-- Genf (1 region - city canton)
INSERT INTO premium_region (id, canton_code, region_number, name_de, name_fr) VALUES
    ('10000000-0025-0001-0001-000000000001', 'GE', 1, 'Genf', 'Genève');

-- Jura (2 regions)
INSERT INTO premium_region (id, canton_code, region_number, name_de, name_fr) VALUES
    ('10000000-0026-0001-0001-000000000001', 'JU', 1, 'Jura Region 1', 'Jura Région 1'),
    ('10000000-0026-0001-0002-000000000001', 'JU', 2, 'Jura Region 2', 'Jura Région 2');
