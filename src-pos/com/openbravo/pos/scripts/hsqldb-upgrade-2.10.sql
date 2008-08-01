--    Openbravo POS is a point of sales application designed for touch screens.
--    Copyright (C) 2007-2008 Openbravo, S.L.
--    http://sourceforge.net/projects/openbravopos
--
--    This program is free software; you can redistribute it and/or modify
--    it under the terms of the GNU General Public License as published by
--    the Free Software Foundation; either version 2 of the License, or
--    (at your option) any later version.
--
--    This program is distributed in the hope that it will be useful,
--    but WITHOUT ANY WARRANTY; without even the implied warranty of
--    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
--    GNU General Public License for more details.
--
--    You should have received a copy of the GNU General Public License
--    along with this program; if not, write to the Free Software
--    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

-- Database upgrade script for HSQLDB
-- v2.10 - v2.20

CREATE TABLE TAXCUSTCATEGORIES (
    ID VARCHAR NOT NULL,
    NAME VARCHAR NOT NULL,
    PRIMARY KEY (ID)
);
CREATE UNIQUE INDEX TAXCUSTCAT_NAME_INX ON TAXCUSTCATEGORIES(NAME);


CREATE TABLE TAXCATEGORIES (
    ID VARCHAR NOT NULL,
    NAME VARCHAR NOT NULL,
    PRIMARY KEY (ID)
);
CREATE UNIQUE INDEX TAXCAT_NAME_INX ON TAXCATEGORIES(NAME);
INSERT INTO TAXCATEGORIES(ID, NAME) VALUES ('0', 'Exempt');
INSERT INTO TAXCATEGORIES(ID, NAME) VALUES ('1', 'Standard');

ALTER TABLE TAXES ADD COLUMN CATEGORY VARCHAR;
ALTER TABLE TAXES ADD COLUMN CUSTCATEGORY VARCHAR;
ALTER TABLE TAXES ADD COLUMN PARENTID VARCHAR;
ALTER TABLE TAXES ADD COLUMN CASCADE BOOLEAN;
ALTER TABLE TAXES ADD CONSTRAINT TAXES_CAT_FK FOREIGN KEY (CATEGORY) REFERENCES TAXCATEGORIES(ID);
ALTER TABLE TAXES ADD CONSTRAINT TAXES_CUSTCAT_FK FOREIGN KEY (CUSTCATEGORY) REFERENCES TAXCUSTCATEGORIES(ID);
ALTER TABLE TAXES ADD CONSTRAINT TAXES_TAXES_FK FOREIGN KEY (PARENTID) REFERENCES TAXES(ID);
UPDATE TAXES SET CATEGORY='1', CASCADE=FALSE;
ALTER TABLE TAXES ALTER COLUMN CATEGORY SET NOT NULL;
ALTER TABLE TAXES ALTER COLUMN CASCADE SET NOT NULL;

ALTER TABLE CUSTOMERS ADD COLUMN SEARCHKEY VARCHAR;
UPDATE CUSTOMERS SET SEARCHKEY = ID;
ALTER TABLE CUSTOMERS ALTER COLUMN SEARCHKEY SET NOT NULL;
CREATE UNIQUE INDEX CUSTOMERS_SKEY_INX ON CUSTOMERS(SEARCHKEY);

ALTER TABLE CUSTOMERS ADD COLUMN ADDRESS2 VARCHAR;
ALTER TABLE CUSTOMERS ADD COLUMN POSTAL VARCHAR;
ALTER TABLE CUSTOMERS ADD COLUMN CITY VARCHAR;
ALTER TABLE CUSTOMERS ADD COLUMN REGION VARCHAR;
ALTER TABLE CUSTOMERS ADD COLUMN COUNTRY VARCHAR;
ALTER TABLE CUSTOMERS ADD COLUMN FIRSTNAME VARCHAR;
ALTER TABLE CUSTOMERS ADD COLUMN LASTNAME VARCHAR;
ALTER TABLE CUSTOMERS ADD COLUMN EMAIL VARCHAR;
ALTER TABLE CUSTOMERS ADD COLUMN PHONE VARCHAR;
ALTER TABLE CUSTOMERS ADD COLUMN PHONE2 VARCHAR;
ALTER TABLE CUSTOMERS ADD COLUMN FAX VARCHAR;
ALTER TABLE CUSTOMERS ADD COLUMN TAXCATEGORY VARCHAR;
ALTER TABLE CUSTOMERS ADD CONSTRAINT CUSTOMERS_TAXCAT FOREIGN KEY (TAXCATEGORY) REFERENCES TAXCUSTCATEGORIES(ID);

ALTER TABLE CLOSEDCASH ADD COLUMN HOSTSEQUENCE INTEGER;  
CREATE INDEX CLOSEDCASH_SEQINX ON CLOSEDCASH(HOST, HOSTSEQUENCE);

ALTER TABLE RECEIPTS ADD COLUMN ATTRIBUTES VARBINARY;

UPDATE PEOPLE SET CARD = NULL WHERE CARD = '';
DELETE FROM SHAREDTICKETS;

UPDATE APPLICATIONS SET NAME = $APP_NAME{}, VERSION = $APP_VERSION{} WHERE ID = $APP_ID{};
