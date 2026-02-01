drop table if exists payment cascade;

create table if not exists payment
(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_id INT NULL,
    order_reference varchar(255) NULL,
    amount numeric(38, 2) NULL,
    payment_date timestamp(6) NOT NULL,
    last_modified_date timestamp(6) NULL,
    payment_method varchar(255) NULL,
    CONSTRAINT payment_payment_method_check CHECK (((payment_method)::text = ANY (ARRAY[('PAYPAL'::character varying)::text,('CREDIT_CARD'::character varying)::text,('VISA_CARD'::character varying)::text,('MASTER_CARD'::character varying)::text,('AMERICAN_EXPRESS'::character varying)::text,('DISCOVER_CARD'::character varying)::text,('APPLE_PAY'::character varying)::text,('GOOGLE_PAY'::character varying)::text,('AMAZON_PAY'::character varying)::text,('BITCOIN'::character varying)::text])))
);

