DROP SCHEMA PUBLIC CASCADE;

CREATE TABLE Customer
(
   id INT GENERATED BY DEFAULT AS IDENTITY,
   firstname VARCHAR(50),
   lastname VARCHAR(50),
   email VARCHAR(200)
);


insert into Customer (id, email, firstname, lastname) values (1, 'beta@cht.com.tw', 'Beta', 'Huang');
insert into Customer (id, email, firstname, lastname) values (2, 'hyshan@cht.com.tw', 'Hs', 'Shan');
insert into Customer (id, email, firstname, lastname) values (3, 'angusan@cht.com.tw', 'Angus', 'An');


create table AccItem
(
  dataYr VARCHAR(3),
  itemCd VARCHAR(8),
  itemNm VARCHAR(50),
  constraint PK_AccItem primary key (dataYr, itemCd)
);

insert into AccItem (dataYr, itemCd, itemNm) values ('103', '0001', '營業收入');
insert into AccItem (dataYr, itemCd, itemNm) values ('103', '0002', '營業成本');

create table PaynumDiff
(
  fiscalYr VARCHAR(3),
  dataYr  VARCHAR(3),
  dataMonth VARCHAR(2),
  diffType INT,
  itemCd VARCHAR(8),
  certType INT,
  serialNo VARCHAR(8),
  paymentDate VARCHAR(7),
  description VARCHAR(50),
  diffAmt NUMERIC(13),
  constraint PK_PaynumDiff primary key (fiscalYr, dataYr, dataMonth, diffType, itemCd, certType, serialNo)
);

insert into PaynumDiff (fiscalYr, dataYr, dataMonth, diffType, itemCd, certType, serialNo, paymentDate, description, diffAmt)
values ('103', '103', '08', 0, '00012345', 1, '0000001', '1030805', '列入', '-300000');

CREATE TABLE Book
(
   isbn VARCHAR(4),
   author VARCHAR(50),
   name VARCHAR(50),
   publishDate Date,
   constraint PK_Book primary key (isbn)
);

insert into Book (isbn, author, name, publishDate) values ('0001', 'Beta', 'Spring Web Application', '2010-08-08');
insert into Book (isbn, author, name, publishDate) values ('0002', 'Beta', 'Spring Web Application', '2010-08-08');
insert into Book (isbn, author, name, publishDate) values ('0003', 'Beta', 'Spring Web Application', '2010-08-08');
insert into Book (isbn, author, name, publishDate) values ('0004', 'Beta', 'Spring Web Application', '2010-08-08');
insert into Book (isbn, author, name, publishDate) values ('0005', 'Beta', 'Spring Web Application', '2010-08-08');
insert into Book (isbn, author, name, publishDate) values ('0006', 'Beta', 'Spring Web Application', '2010-08-08');
insert into Book (isbn, author, name, publishDate) values ('0007', 'Beta', 'Spring Web Application', '2010-08-08');

CREATE TABLE ETS202FA
(
  ACCYY       VARCHAR(3),
  PROGCD      VARCHAR(8),
  SEQNO       VARCHAR(4),
  SEQNM       VARCHAR(40),
  STRITEMCHK  VARCHAR(23),
  ENDITEMCHK  VARCHAR(23),
  MTNLOGIN    VARCHAR(10),
  MTNUSRCD    VARCHAR(10),
  MTNDT       VARCHAR(7),
  MTNTM       VARCHAR(6)
);


CREATE TABLE ETS100FA
(
  PDATE     VARCHAR(7),
  ACCYY     VARCHAR(3),
  PROGCD    VARCHAR(8),
  SEQNO     VARCHAR(4),
  AMT1      NUMERIC(17),
  AMT2      NUMERIC(17),
  AMT3      NUMERIC(17),
  MTNLOGIN  VARCHAR(10),
  MTNUSRCD  VARCHAR(10),
  MTNDT     VARCHAR(7),
  MTNTM     VARCHAR(6)
);

insert into ETS202FA (ACCYY, PROGCD, SEQNO, SEQNM, STRITEMCHK, ENDITEMCHK, MTNLOGIN, MTNUSRCD, MTNDT, MTNTM) values ('100', 'ETS401R', '0001', '一、上日(月、年)結存', 'test1',  'test1', 'testUser', 'testUser', '1000202', '191013');
insert into ETS202FA (ACCYY, PROGCD, SEQNO, SEQNM, STRITEMCHK, ENDITEMCHK, MTNLOGIN, MTNUSRCD, MTNDT, MTNTM) values ('100', 'ETS401R', '0002', '二、本日(月、年)收入', 'test2',  'test2', 'testUser', 'testUser', '1000202', '191013');
insert into ETS202FA (ACCYY, PROGCD, SEQNO, SEQNM, STRITEMCHK, ENDITEMCHK, MTNLOGIN, MTNUSRCD, MTNDT, MTNTM) values ('100', 'ETS401R', '0003', '三、本日(月、年)兌付國庫支票', 'test3',  'test3', 'testUser', 'testUser', '1000202', '191013');

insert into ETS100FA (PDATE, ACCYY, PROGCD, SEQNO, AMT1, AMT2, AMT3, MTNLOGIN, MTNUSRCD, MTNDT, MTNTM) values ('1000203', '100', 'ETS401R', '0001', 100, 123, 144, 'testUser', 'testUser', '1000202', '191020');
insert into ETS100FA (PDATE, ACCYY, PROGCD, SEQNO, AMT1, AMT2, AMT3, MTNLOGIN, MTNUSRCD, MTNDT, MTNTM) values ('1000203', '100', 'ETS401R', '0002', 1002, 1343, 166, 'testUser', 'testUser', '1000202', '191020');
insert into ETS100FA (PDATE, ACCYY, PROGCD, SEQNO, AMT1, AMT2, AMT3, MTNLOGIN, MTNUSRCD, MTNDT, MTNTM) values ('1000203', '100', 'ETS401R', '0003', 1022, 1323, 1334, 'testUser', 'testUser', '1000202', '191020');
    
CREATE TABLE SYS111FA
(
   SCODE VARCHAR(2),
   NOWORKDATE VARCHAR(7),
   DATEDESC VARCHAR(30),
   USERID VARCHAR(8),
   MDATE VARCHAR(7),
   MTIME VARCHAR(6)
);

Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1021229','SUNDAY','001129','1011113','142058');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1021228','SATURDAY','001129','1011113','142058');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1021222','SUNDAY','001129','1011113','142058');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1021221','SATURDAY','001129','1011113','142058');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1021215','SUNDAY','001129','1011113','142058');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1021214','SATURDAY','001129','1011113','142058');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1021208','SUNDAY','001129','1011113','142058');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1021207','SATURDAY','001129','1011113','142058');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1021201','SUNDAY','001129','1011113','142058');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1021130','SATURDAY','001129','1011113','142058');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1021124','SUNDAY','001129','1011113','142058');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1021123','SATURDAY','001129','1011113','142058');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1021117','SUNDAY','001129','1011113','142058');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1021116','SATURDAY','001129','1011113','142058');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1021110','SUNDAY','001129','1011113','142058');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1021109','SATURDAY','001129','1011113','142058');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1021103','SUNDAY','001129','1011113','142058');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1021102','SATURDAY','001129','1011113','142058');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1021027','SUNDAY','001129','1011113','142058');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1021026','SATURDAY','001129','1011113','142058');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1021020','SUNDAY','001129','1011113','142058');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1021019','SATURDAY','001129','1011113','142058');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1021013','SUNDAY','001129','1011113','142058');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1021012','SATURDAY','001129','1011113','142058');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1021010','','001129','1011113','142742');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1021006','SUNDAY','001129','1011113','142058');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1021005','SATURDAY','001129','1011113','142058');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1020929','SUNDAY','001129','1011113','142058');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1020928','SATURDAY','001129','1011113','142058');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1020922','SUNDAY','001129','1011113','142058');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1020921','SATURDAY','001129','1011113','142058');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1020920','','001129','1011113','142731');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1020919','','001129','1011113','142703');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1020915','SUNDAY','001129','1011113','142058');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1020908','SUNDAY','001129','1011113','142058');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1020907','SATURDAY','001129','1011113','142058');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1020901','SUNDAY','001129','1011113','142058');
Insert into SYS111FA (SCODE,NOWORKDATE,DATEDESC,USERID,MDATE,MTIME) values ('01','1020831','SATURDAY','001129','1011113','142058');

CREATE TABLE AVE011FA
(
   SCODE VARCHAR(2), 
   FYR VARCHAR(3), 
   AGE VARCHAR(8), 
   AGENCY VARCHAR(80), 
   AANM VARCHAR(20), 
   KIND VARCHAR(1) 
);

Insert into AVE011FA (SCODE,FYR,AGE,AGENCY,AANM,KIND) values ('01','100','12345678','財政部中區國稅局','', '1');
Insert into AVE011FA (SCODE,FYR,AGE,AGENCY,AANM,KIND) values ('02','100','12345678','交通部電子收費部','', '5');

CREATE TABLE SYS111FC
(
   CALENDAR_DATE DATE,
   CALENDAR_YM DATE,
   CALENDAR_YR DATE,
   CALENDAR_YM_E_DATE DATE,
   MON VARCHAR(2),
   MMDD VARCHAR(4),
   ROC_DATE VARCHAR(7),
   ROC_MMDD VARCHAR(4),
   ROC_YM VARCHAR(4),
   ROC_YR VARCHAR(3),
   ROC_YM_E_DATE VARCHAR(7),
   WEEK_CD INTEGER,
   ROC_WEEK_DATE VARCHAR(2),
   NO_WORK_IND INTEGER,
   TM_END_ROC_WORK_DATE VARCHAR(7),
   LAST_ROC_WORK_DATE VARCHAR(7),
   SCODE VARCHAR(2)
);

Insert into SYS111FC (CALENDAR_DATE, SCODE, ROC_DATE, NO_WORK_IND, WEEK_CD) values ('2013-10-27', '01', '1021027', 1, 0);
Insert into SYS111FC (CALENDAR_DATE, SCODE, ROC_DATE, NO_WORK_IND, WEEK_CD) values ('2013-10-28', '01', '1021028', 0, 0);
Insert into SYS111FC (CALENDAR_DATE, SCODE, ROC_DATE, NO_WORK_IND, WEEK_CD) values ('2013-10-29', '01', '1021029', 0, 0);
Insert into SYS111FC (CALENDAR_DATE, SCODE, ROC_DATE, NO_WORK_IND, WEEK_CD) values ('2013-10-30', '01', '1021030', 0, 0);
Insert into SYS111FC (CALENDAR_DATE, SCODE, ROC_DATE, NO_WORK_IND, WEEK_CD) values ('2013-10-31', '01', '1021031', 0, 0);
Insert into SYS111FC (CALENDAR_DATE, SCODE, ROC_DATE, NO_WORK_IND, WEEK_CD) values ('2013-11-01', '01', '1021101', 0, 0);
Insert into SYS111FC (CALENDAR_DATE, SCODE, ROC_DATE, NO_WORK_IND, WEEK_CD) values ('2013-11-02', '01', '1021102', 1, 0);
Insert into SYS111FC (CALENDAR_DATE, SCODE, ROC_DATE, NO_WORK_IND, WEEK_CD) values ('2013-11-03', '01', '1021103', 1, 0);
Insert into SYS111FC (CALENDAR_DATE, SCODE, ROC_DATE, NO_WORK_IND, WEEK_CD) values ('2013-11-04', '01', '1021104', 0, 0);
Insert into SYS111FC (CALENDAR_DATE, SCODE, ROC_DATE, NO_WORK_IND, WEEK_CD) values ('2013-11-05', '01', '1021105', 0, 0);
Insert into SYS111FC (CALENDAR_DATE, SCODE, ROC_DATE, NO_WORK_IND, WEEK_CD) values ('2013-11-06', '01', '1021106', 0, 0);
Insert into SYS111FC (CALENDAR_DATE, SCODE, ROC_DATE, NO_WORK_IND, WEEK_CD) values ('2013-11-07', '01', '1021107', 0, 0);
Insert into SYS111FC (CALENDAR_DATE, SCODE, ROC_DATE, NO_WORK_IND, WEEK_CD) values ('2013-11-08', '01', '1021108', 0, 0);
Insert into SYS111FC (CALENDAR_DATE, SCODE, ROC_DATE, NO_WORK_IND, WEEK_CD) values ('2013-11-09', '01', '1021109', 0, 0);

Insert into SYS111FC (CALENDAR_DATE, SCODE, ROC_DATE, NO_WORK_IND, WEEK_CD) values ('2013-10-27', '02', '1021027', 1, 0);
Insert into SYS111FC (CALENDAR_DATE, SCODE, ROC_DATE, NO_WORK_IND, WEEK_CD) values ('2013-10-28', '02', '1021028', 0, 0);
Insert into SYS111FC (CALENDAR_DATE, SCODE, ROC_DATE, NO_WORK_IND, WEEK_CD) values ('2013-10-29', '02', '1021029', 0, 0);
Insert into SYS111FC (CALENDAR_DATE, SCODE, ROC_DATE, NO_WORK_IND, WEEK_CD) values ('2013-10-30', '02', '1021030', 0, 0);
Insert into SYS111FC (CALENDAR_DATE, SCODE, ROC_DATE, NO_WORK_IND, WEEK_CD) values ('2013-10-31', '02', '1021031', 0, 0);
Insert into SYS111FC (CALENDAR_DATE, SCODE, ROC_DATE, NO_WORK_IND, WEEK_CD) values ('2013-11-01', '02', '1021101', 0, 0);
Insert into SYS111FC (CALENDAR_DATE, SCODE, ROC_DATE, NO_WORK_IND, WEEK_CD) values ('2013-11-02', '02', '1021102', 1, 0);
Insert into SYS111FC (CALENDAR_DATE, SCODE, ROC_DATE, NO_WORK_IND, WEEK_CD) values ('2013-11-03', '02', '1021103', 1, 0);
Insert into SYS111FC (CALENDAR_DATE, SCODE, ROC_DATE, NO_WORK_IND, WEEK_CD) values ('2013-11-04', '02', '1021104', 0, 0);
Insert into SYS111FC (CALENDAR_DATE, SCODE, ROC_DATE, NO_WORK_IND, WEEK_CD) values ('2013-11-05', '02', '1021105', 0, 0);
Insert into SYS111FC (CALENDAR_DATE, SCODE, ROC_DATE, NO_WORK_IND, WEEK_CD) values ('2013-11-06', '02', '1021106', 0, 0);
Insert into SYS111FC (CALENDAR_DATE, SCODE, ROC_DATE, NO_WORK_IND, WEEK_CD) values ('2013-11-07', '02', '1021107', 0, 0);
Insert into SYS111FC (CALENDAR_DATE, SCODE, ROC_DATE, NO_WORK_IND, WEEK_CD) values ('2013-11-08', '02', '1021108', 0, 0);
Insert into SYS111FC (CALENDAR_DATE, SCODE, ROC_DATE, NO_WORK_IND, WEEK_CD) values ('2013-11-09', '02', '1021109', 0, 0);
