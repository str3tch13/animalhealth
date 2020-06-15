-- DROP TABLE IF EXISTS pet;
-- DROP TABLE IF EXISTS owner;
-- DROP TABLE IF EXISTS invoice;
-- DROP TABLE IF EXISTS procedure;
-- DROP TABLE IF EXISTS procedure_invoice;



START TRANSACTION;

-- Owner
--    owner_ID (pk)
--    first_name
--    last_name
--    phone_number
--    owner_address


CREATE TABLE owner (
    owner_ID serial,
    first_name varchar(64) not null,
    last_name varchar(64) not null,
    phone_number varchar(11) not null,
    owner_address varchar(64) not null,

    constraint pk_owner primary key (owner_ID)
);
-- Pet
--   pet_name 
--   owner_id (fk)
--   pet_ID (pk)
CREATE TABLE pet (
    pet_ID serial,
    name varchar(64) not null,
    owner_ID int not null,
    constraint pk_pet primary key (pet_ID),
    constraint fk_pet_owner foreign key (owner_ID) references owner (owner_ID)
);
--
-- Invoice table
--   invoice_ID (pk)
--   owner
--   date
--   hospital name
CREATE TABLE INVOICE (
    invoice_ID serial,
    owner_ID int not null,
    invoice_date date not null,
    hospital_name varchar(64) not null,

    constraint pk_invoice primary key (invoice_ID),
    constraint fk_invoice_owner foreign key (owner_ID) references owner (owner_ID)
);
-- Procedure
--       procedure_ID (pk)
--       procedure_name
--       procedure_price
CREATE TABLE procedure (
procedure_ID serial,
invoice_ID int not null,
procedure_amount money not null,
constraint pk_procedure primary key (procedure_ID) 
);
-- Procedure_Invoice
--     invoice_ID (fk)
--     procedure_ID (fk)
CREATE TABLE procedure_invoice (
    invoice_ID int not null,
    procedure_ID int not null,
    constraint fk_procedure_invoice_procedure foreign key (procedure_ID) references procedure(procedure_ID),
        constraint fk_procedure_invoice_invoice foreign key (procedure_ID) references invoice(invoice_ID)
);

--ROLLBACK;

COMMIT TRANSACTION;

