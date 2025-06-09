DROP VIEW IF EXISTS chapter_progress_view;

CREATE VIEW chapter_progress_view AS
SELECT
    '11111111-1111-1111-1111-111111111111' AS chapter_id,
    0.75 AS progress,
    '00000000-0000-0000-0000-000000000001' AS account_id;
