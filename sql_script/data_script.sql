create database productspringdb;

# WAIT TO RUN SCRIPT UNTIL AFTER YOU INITIALIZE PROJECT
use productspringdb;

insert into book(title, author, isbn, price, quantity)
	values('To Kill a Mockingbird', 'Harper Lee', '9780446310789', 3.79, 15),
('1984', 'George Orwell', '9780451524935', 7.48, 21),
('Harry Potter and the Philosopher’s Stone', 'J.K. Rowling', '9780747532743', 4.91, 16),
('The Lord of the Rings', 'J.R.R. Tolkien', '9780007136582', 13.57, 8),
('The Great Gatsby', 'F. Scott Fitzgerald', '9780743273565', 6.99, 20),
('Pride and Prejudice', 'Jane Austen', '9780141439518', 34.95, 15),
('The Diary Of A Young Girl', 'Anne Frank', '9780553296983', 12.10, 12),
('The Book Thief', 'Markus Zusak', '9780375842207', 6.99, 23),
('The Hobbit', 'J.R.R. Tolkien', '9780547928227', 11.64, 13),
('Little Women', 'Louisa May Alcott', '9781984898852', 10.00, 40),
('Fahrenheit 451', 'Ray Bradbury', '9780345342966', 8.29, 15),
('Jane Eyre', 'Charlotte Bronte', '9781785996320', 5.95, 4),
('Animal Farm', 'George Orwell', '9788129116123', 7.84, 30),
('Gone with the Wind', 'Margaret Mitchell', '9781784876111', 9.00, 22),
('The Catcher in the Rye', 'J.D. Salinger', '9780316769488', 15.99, 11),
('Charlotte’s Web', 'E.B. White', '9780590302715', 8.99, 61),
('The Lion, the Witch, and the Wardrobe', 'C.S. Lewis', '9780060276362', 8.99, 2),
('The Grapes of Wrath', 'John Steinbeck', '9780606001748', 12.29, 18),
('Lord of the Flies', 'William Golding', '9789381529614', 6.50, 19),
('The Kite Runner', 'Khaled Hosseini', '9783596522682', 16.59, 25),
('Of Mice and Men', 'John Steinbeck', '9780140177398', 10.62, 66),
('A Tale of Two Cities', 'Charles Dickens', '9780486406510', 12.64, 20),
('Romeo and Juliet', 'William Shakespeare', '9781505259568', 9.16, 21),
('The Hitchhiker’s Guide to the Galaxy', 'Douglas Adams', '9780345453747', 13.99, 12),
('Wuthering Heights', 'Emily Bronte', '9781853260018', 5.99, 13),
('The Color Purple', 'Alice Walker', '9781474612944', 17.00, 34),
('Alice in Wonderland', 'Lewis Carroll', '9781503222687', 29.95, 10),
('Frankenstein', 'Mary Shelley', '9780486282114', 3.60, 26),
('The Adventures of Huckleberry Finn', 'Mark Twain', '9780142437179', 2.99, 31),
('Slaughterhouse-Five', 'Kurt Vonnegut', '9780812988529', 13.99, 11);

# PRE-LOADED ADMIN USER 
# username = admin2
# password = pass123
insert into user(username, password, role, enabled)
	values('admin2', '$2a$10$vBWN7BBq4Dcj1ZX781sDpeiPd1IjIruin70u8S2shC9dSPQOt1xSO', 'ROLE_ADMIN', 1);