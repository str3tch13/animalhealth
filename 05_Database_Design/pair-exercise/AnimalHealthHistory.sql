-- DROP TABLE IF EXISTS pet;
-- DROP TABLE IF EXISTS owner;
-- DROP TABLE IF EXISTS visit;
-- DROP TABLE IF EXISTS procedure;




START TRANSACTION;

CREATE TABLE owner (
    owner_ID serial,
    first_name varchar(64) not null,
    last_name varchar(64) not null,
    phone_number varchar(11) not null,
    owner_address varchar(64) not null,


        constraint pk_owner primary key (owner_ID)
    );

CREATE TABLE pet (
    pet_ID serial,
    name varchar(64) not null,
    owner_ID int not null,
    pet_type varchar(64) not null,
    age int not null,

        constraint pk_pet primary key (pet_ID),
        constraint fk_pet_owner foreign key (owner_ID) references owner (owner_ID)
);

CREATE TABLE procedure (
    procedure_ID serial,
    procedure_type varchar(64) not null,

        constraint pk_procedure primary key (procedure_ID)

);


CREATE TABLE visit (
    visit_ID serial,
    owner_ID int not null,
    visit_date timestamp not null,
    procedure_ID int not null,

        constraint pk_visit primary key (visit_ID),
        constraint fk_visit_owner foreign key (owner_ID) references owner (owner_ID),
        constraint fk_visit_procedure foreign key (procedure_ID) references procedure (procedure_ID)
    );


COMMIT TRANSACTION;

