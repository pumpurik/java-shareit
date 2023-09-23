INSERT INTO public.users (id,name,email) VALUES
	 (1,'updateName','updateName@user.com'),
	 (2,'user','user@user.com'),
	 (3,'other','other@other.com'),
	 (4,'practicum','practicum@yandex.ru');

INSERT INTO public.requests (id,description,requestor_id, created) VALUES
	 (1,'Хотел бы воспользоваться щёткой для обуви',1,'2023-09-20 18:15:01.735633');

INSERT INTO public.items (id,name,description,is_avaliable,owner_id,request_id) VALUES
	 (1,'Аккумуляторная дрель','Аккумуляторная дрель + аккумулятор',true,1,NULL),
	 (2,'Клей Момент','Тюбик суперклея марки Момент',true,2,NULL),
	 (3,'Отвертка','Аккумуляторная отвертка',true,2,NULL),
	 (4,'Кухонный стол','Стол для празднования',true,4,NULL),
	 (5,'Щётка для обуви','Стандартная щётка для обуви',true,2,1);

INSERT INTO public.comments (id,text,item_id,author_id,created) VALUES
	 (1,'Add comment from user1',2,1,'2023-09-20 18:15:00.139078');
INSERT INTO public.bookings (id, start_date,end_date,item_id,booker_id,status) VALUES
	 (1,'2023-09-20 18:14:48','2023-09-20 18:14:49',3,1,'APPROVED'),
	 (2,'2023-09-21 18:14:46','2023-09-22 18:14:46',3,1,'APPROVED'),
	 (3,'2023-09-21 18:14:47','2023-09-21 19:14:47',1,2,'REJECTED'),
	 (4,'2023-09-20 19:14:48','2023-09-20 20:14:48',2,3,'APPROVED'),
	 (5,'2023-09-20 18:14:55','2023-09-21 18:14:52',3,1,'REJECTED'),
	 (6,'2023-09-20 18:14:55','2023-09-20 18:14:56',3,1,'APPROVED'),
	 (7,'2023-09-20 18:14:55','2023-09-20 19:14:53',4,1,'APPROVED'),
	 (8,'2023-09-30 18:14:53','2023-10-01 18:14:53',1,3,'APPROVED');

