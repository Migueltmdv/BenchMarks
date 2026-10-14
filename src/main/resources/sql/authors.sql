TRUNCATE valoraciones CASCADE;

INSERT INTO valoraciones (review_id, reviewer_id, bench_id, date, mark, note, photo_id) VALUES
(1, 'user', 1, '2026-06-01 10:00:00', 5, 'Great bench to relax!', 1),
(2, 'admin', 2, '2026-06-02 15:30:00', 4, 'Nice view, a bit cold.', 2),
(3, 'user', 3, '2026-06-03 09:15:00', 9, 'Excellent for sunsets.', 3),
(4, 'admin', 4, '2026-06-04 11:45:00', 6, 'Confortable but crowded.', 4),
(5, 'user', 5, '2026-06-05 16:20:00', 10, 'Best bench in town.', 5),
(6, 'admin', 6, '2026-06-06 12:10:00', 3, 'Needs maintenance.', 6),
(7, 'user', 7, '2026-06-07 08:50:00', 7, 'Good for reading.', 7),
(8, 'admin', 8, '2026-06-08 17:30:00', 5, 'Decent location.', 8),
(9, 'user', 9, '2026-06-09 10:30:00', 8, 'Very scenic.', 9),
(10, 'admin', 10, '2026-06-10 14:00:00', 6, 'Shady in summer.', 10),
(11, 'user', 11, '2026-06-11 09:00:00', 7, 'Quiet area.', 11),
(12, 'admin', 12, '2026-06-12 13:15:00', 4, 'A bit windy.', 12),
(13, 'user', 13, '2026-06-13 15:45:00', 9, 'Perfect after work.', 13),
(14, 'admin', 14, '2026-06-14 11:00:00', 6, 'Nice park nearby.', 14),
(15, 'user', 15, '2026-06-15 16:30:00', 8, 'Great view of the bridge.', 15),
(16, 'admin', 16, '2026-06-16 10:15:00', 2, 'Too much noise.', 16),
(17, 'user', 17, '2026-06-17 09:20:00', 7, 'Good for a break.', 17),
(18, 'admin', 18, '2026-06-18 12:45:00', 8, 'Well located.', 18),
(19, 'user', 19, '2026-06-19 14:30:00', 9, 'Clean and comfy.', 19),
(20, 'admin', 20, '2026-06-20 16:00:00', 5, 'Average bench.', 20);
