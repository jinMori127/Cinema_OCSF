

Use catalog;
insert INTO movies ( movie_name, main_actors, category, description_, time_, year_,director,price)
VALUES
( 'Moana', 'Aulii Cravalho, Dwayne Johnson', 'Family', 'Disney animated film about a brave Polynesian girl who sets sail on a daring mission to save her people, with the help of the demigod Maui', '1:47', 2016,'Ron',40),
('Anyone But You', 'Sydney Sweeney, Glen Powell', 'Romance', 'romantic comedy about two ex-lovers who must pretend to be a couple again for a wedding', '1:44', 2023,'Mias',40),
('Cruella', 'Emma Stone, Emma Thompson', 'Comedy', 'A stylish and rebellious origin story of Cruella de Vil, set in 1970s London amidst the punk rock revolution', '2:14', 2021,'Sami',50),
('Divergent', 'Shailene Woodley, Theo James', 'Action', 'In a dystopian future, a young woman discovers she is a Divergent and must uncover the secrets behind her societys facade', '2:19', 2014,'Aisha',30),
('Spider-Man: No Way Home', 'Tom Holland, Zendaya, Benedict Cumberbatch', 'Action', 'Spider-Man seeks Doctor Stranges help to restore his secret identity, unleashing multiverse chaos', '2:30', 2021,'Jan',10);



INSERT INTO screening (branch,date_time,room_number,theater_map,movie_id)
VALUES
('Haifa','2021/11/11 15:00',1,"0, 0, 0, 0, 0",1);


