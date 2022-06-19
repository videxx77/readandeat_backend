INSERT INTO
    account (Email, Username, First_Name, Last_Name, Password)
VALUES
       ('ramon@gmail.com', 'ramon', 'Ramon', 'Schuchlenz', '$2a$10$XT0FmgTiBQTfxqe/ZSVQQeG.cLEwGP2sR9PdrWUqAiKYUn/m4mRi2'),
       ('simon@gmail.com', 'simon', 'Simon', 'Schönmaier', '$2a$10$lyDwycpaR/Vx.Jvh4JBo2OLnex4IiIWGeD8f44tuEeI3ZIAfrIWEW');

INSERT INTO
    customer (First_Name, Last_Name, Balance, PictureURL, AccountID)
VALUES
       ('Heinz', 'Schiffermüller', 1.00, "customer/customer-photos/1/1.jpg", 1),
       ('Paul', 'Temmel', 5.00, NULL, 1),
       ('Simon', 'Terodde', 420.0, NULL, 2),
       ('Thomas', 'Ouwejan', 69.00, NULL, 2);

INSERT INTO
    product (Name, Price, PictureURL, AccountID)
VALUES
       ('Sprite', 1.50, NULL, 1),
       ('Red BUll', 2.00, NULL, 1),
       ('Cola', 3.50, NULL, 1),
       ('Semmel', 4.00, NULL, 2),
       ('Große Semmel', 5.50, NULL, 2),
       ('Schnitzel', 6.00, NULL, 2);

INSERT INTO
    `role` (Name)
VALUES
       ('ROLE_USER');

INSERT INTO
    account_role (AccountID, RoleID)
VALUES
       (1, 1),
       (2, 1);