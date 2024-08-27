USE catalog;
-- Insert movies with names and descriptions in English and Hebrew, and categories in the required format
INSERT INTO movies (movie_name, main_actors, category, description_, time_, year_, director, price, rating, movie_link)
VALUES
('Moana / מואנה', 'Aulii Cravalho, Dwayne Johnson', 'Family - משפחה', 'Disney animated film about a brave Polynesian girl who sets sail on a daring mission to save her people, with the help of the demigod Maui / סרט אנימציה של דיסני על נערה פולינזית אמיצה שיוצאת להציל את עמה בעזרת חצי אל מאווי', '1:47', 2016, 'Ron - רון ', 40, 3, 'https://w27.my-cima.net/watch.php?vid=7c7d539b0'),
('Anyone But You / כל אחד מלבדך', 'Sydney Sweeney, Glen Powell', 'Romance - רומנטיקה', 'Romantic comedy about two ex-lovers who must pretend to be a couple again for a wedding / קומדיה רומנטית על שני אוהבים לשעבר שמעמידים פנים שהם עדיין יחד בחתונה', '1:44', 2023, 'Mias - מיאס', 40, 9, 'https://w27.my-cima.net/watch.php?vid=7c7d539b0'),
('Cruella / קרואלה', 'Emma Stone, Emma Thompson', 'Comedy - קומדיה', 'A stylish and rebellious origin story of Cruella de Vil, set in 1970s London amidst the punk rock revolution / סיפור המקור המסוגנן והמרדני של קרואלה דה ויל, המתרחש בלונדון של שנות ה-70', '2:14', 2021, 'Sami - סאמי', 50, 6, 'https://w27.my-cima.net/watch.php?vid=7c7d539b0'),
('Divergent / מפוצלים', 'Shailene Woodley, Theo James', 'Action - פעולה', 'In a dystopian future, a young woman discovers she is a Divergent and must uncover the secrets behind her society’s facade / בעתיד דיסטופי, נערה מגלה שהיא מפוצלת וצריכה לחשוף את הסודות מאחורי החברה שלה', '2:19', 2014, 'Aisha - עאישה', 30, 7, 'https://w27.my-cima.net/watch.php?vid=7c7d539b0'),
('Spider-Man: No Way Home / ספיידרמן: אין דרך הביתה', 'Tom Holland, Zendaya, Benedict Cumberbatch', 'Action - פעולה', 'Spider-Man seeks Doctor Strange’s help to restore his secret identity, unleashing multiverse chaos / ספיידרמן מבקש את עזרתו של דוקטור סטריינג\' לשחזר את זהותו הסודית, מה שגורם לכאוס ביקומים מקבילים', '2:30', 2021, 'Jan - גאן', 30, 1, 'https://w27.my-cima.net/watch.php?vid=');

-- Truncate the Worker table
TRUNCATE TABLE Worker;

-- Insert workers
INSERT INTO Worker (user_name, password, name, branch, role, is_worker_loggedIn)
VALUES 
    ('MA1', 'MA1', 'John Doe', 'Sakhnin', 'Manager', false),
    ('DM1', 'DM1', 'Alice Smith', 'Haifa', 'DataManager', false),
    ('CS1', 'CS1', 'Bob Thompson', 'Nazareth', 'CustomerService', false),
    ('DM2', 'DM2', 'Catherine Johnson', 'Nhif', 'DataManager', false);
