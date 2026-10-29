TRUNCATE benches CASCADE;

INSERT INTO benches(bench_id, cords, type, likes) VALUES
(1, '40.4168,-3.7038', 'Wooden', 12),
(2, '41.3851,2.1734', 'Stone', 9),
(3, '43.3623,-8.4115', 'Metal', 15),
(4, '37.3891,-5.9845', 'Wooden', 6),
(5, '39.4699,-0.3763', 'Concrete', 20),
(6, '42.8805,-8.5460', 'Wooden', 3),
(7, '41.6488,-0.8891', 'Stone', 11),
(8, '38.3452,-0.4815', 'Metal', 8),
(9, '43.2630,-2.9350', 'Wooden', 17),
(10, '36.7213,-4.4214', 'Concrete', 5),
(11, '40.9650,-5.6640', 'Wooden', 14),
(12, '39.9864,-0.0518', 'Stone', 7),
(13, '41.1189,1.2445', 'Metal', 19),
(14, '37.1773,-3.5986', 'Wooden', 4),
(15, '42.2406,-8.7207', 'Concrete', 16),
(16, '40.3047,-3.7319', 'Wooden', 2),
(17, '43.3000,-1.9812', 'Stone', 13),
(18, '39.5696,2.6502', 'Metal', 10),
(19, '38.9944,-1.8572', 'Wooden', 18),
(20, '40.6350,-3.1670', 'Concrete', 1);


-- Avanza la secuencia para que los POST no choquen con los IDs insertados a mano
SELECT setval(pg_get_serial_sequence('benches', 'bench_id'), (SELECT MAX(bench_id) FROM benches));
