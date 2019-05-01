
create table invoiceHeader
(
	 id integer not null,
	 fiscalYear  int  not null,
	 numberInvoice int not null,
	 customerId int not null,
	 active char(1),
	 primary key(id),
	 UNIQUE  (fiscalYear,numberInvoice)
);