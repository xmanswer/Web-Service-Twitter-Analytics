mysql --local-infile -u root -ppassword q4_db
drop database q2_db;

use q2_db;
drop table q2_table1;
drop table q2_table2;
use q4_db;
drop table q4_table;


use q4_db;
set character_set_connection = 'utf8mb4';
set character_set_results = 'utf8mb4';
set character_set_client = 'utf8mb4';
set character_set_server = 'utf8mb4';
SET NAMES 'utf8mb4';
alter database q4_db default character set utf8mb4 COLLATE = utf8mb4_unicode_ci;

drop table q4_table;
drop table q3_table_pos;

########create q2_table###############
CREATE TABLE `q2_table` (
  `k` VARCHAR(140) NOT NULL,
  `r` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `hashvalue` INT NOT NULL
) ROW_FORMAT=COMPRESSED 
 KEY_BLOCK_SIZE=8
 ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOAD DATA LOCAL INFILE 'part-00005' INTO TABLE q2_table FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';
LOAD DATA LOCAL INFILE 'part-00006' INTO TABLE q2_table FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';
LOAD DATA LOCAL INFILE 'part-00007' INTO TABLE q2_table FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';
LOAD DATA LOCAL INFILE 'part-00008' INTO TABLE q2_table FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';
LOAD DATA LOCAL INFILE 'part-00009' INTO TABLE q2_table FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';
LOAD DATA LOCAL INFILE 'part-00010' INTO TABLE q2_table FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';
LOAD DATA LOCAL INFILE 'part-00011' INTO TABLE q2_table FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';
LOAD DATA LOCAL INFILE 'part-00012' INTO TABLE q2_table FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';

DROP INDEX `k` ON q2_table; 

ALTER TABLE `q2_table` ADD INDEX `k` (`k`);
commit;

########create q3_table###############
CREATE TABLE `q3_table_pos` (
  `k` BIGINT NOT NULL,
  `r` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `hashvalue` INT NOT NULL
) ROW_FORMAT=COMPRESSED 
 KEY_BLOCK_SIZE=8
 ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOAD DATA LOCAL INFILE '~/q4/part-00010-hashkey1' INTO TABLE q3_table_pos FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';

LOAD DATA LOCAL INFILE '~/q4/part-00011-hashkey2' INTO TABLE q3_table_pos FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';

LOAD DATA LOCAL INFILE '~/q4/part-00012-hashkey3' INTO TABLE q3_table_pos FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';

LOAD DATA LOCAL INFILE '~/q4/part-00013-hashkey4' INTO TABLE q3_table_pos FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';

LOAD DATA LOCAL INFILE '~/q4/part-00014-hashkey5' INTO TABLE q3_table_pos FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';

LOAD DATA LOCAL INFILE '~/q4/part-00015' INTO TABLE q3_table_pos FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';

LOAD DATA LOCAL INFILE '~/q4/part-00016' INTO TABLE q3_table_pos FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';
  
LOAD DATA LOCAL INFILE '~/q4/part-00017' INTO TABLE q3_table_pos FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';

ALTER TABLE `q3_table_pos` ADD INDEX `k` (`k`);
commit;

CREATE TABLE `q3_table_neg` (
  `k` BIGINT NOT NULL,
  `r` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `hashvalue` INT NOT NULL
) ROW_FORMAT=COMPRESSED 
 KEY_BLOCK_SIZE=8
 ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOAD DATA LOCAL INFILE '~/q4/part-00018' INTO TABLE q3_table_neg FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';

LOAD DATA LOCAL INFILE '~/q4/part-00008' INTO TABLE q3_table_neg FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';

LOAD DATA LOCAL INFILE '~/q4/part-00009' INTO TABLE q3_table_neg FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';

LOAD DATA LOCAL INFILE '~/q4/part-00010-hashkey12' INTO TABLE q3_table_neg FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';

LOAD DATA LOCAL INFILE '~/q4/part-00011-hashkey13' INTO TABLE q3_table_neg FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';

LOAD DATA LOCAL INFILE '~/q4/part-00012-hashkey14' INTO TABLE q3_table_neg FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';

LOAD DATA LOCAL INFILE '~/q4/part-00013-hashkey15' INTO TABLE q3_table_neg FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';
  
LOAD DATA LOCAL INFILE '~/q4/part-00014-hashkey16' INTO TABLE q3_table_neg FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';

ALTER TABLE `q3_table_neg` ADD INDEX `k` (`k`);
commit;

########create q4_table###############
CREATE TABLE `q4_table` (
  `k` VARCHAR(140) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `r` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `hashvalue` INT NOT NULL
) ROW_FORMAT=COMPRESSED 
 KEY_BLOCK_SIZE=8
 ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOAD DATA LOCAL INFILE '~/q4/part-00003-hashkey1' INTO TABLE q4_table FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';

LOAD DATA LOCAL INFILE '~/q4/part-00004' INTO TABLE q4_table FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';

LOAD DATA LOCAL INFILE '~/q4/part-00005' INTO TABLE q4_table FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';

LOAD DATA LOCAL INFILE '~/q4/part-00006' INTO TABLE q4_table FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';

LOAD DATA LOCAL INFILE '~/q4/part-00000' INTO TABLE q4_table FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';

LOAD DATA LOCAL INFILE '~/q4/part-00001' INTO TABLE q4_table FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';

LOAD DATA LOCAL INFILE '~/q4/part-00002' INTO TABLE q4_table FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';
  
LOAD DATA LOCAL INFILE '~/q4/part-00003-hashkey8' INTO TABLE q4_table FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';

ALTER TABLE `q4_table` ADD INDEX `k` (`k`);
commit;

########create q6_table###############
CREATE TABLE `q6_table` (
  `k` BIGINT NOT NULL,
  `r` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `hashvalue` INT NOT NULL
) ROW_FORMAT=COMPRESSED 
 KEY_BLOCK_SIZE=8
 ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
 LOAD DATA LOCAL INFILE '~/q4/part-00010' INTO TABLE q6_table FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';
 
 LOAD DATA LOCAL INFILE '~/q4/part-00011' INTO TABLE q6_table FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';
 
 LOAD DATA LOCAL INFILE '~/q4/part-00012' INTO TABLE q6_table FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';
 
 LOAD DATA LOCAL INFILE '~/q4/part-00013' INTO TABLE q6_table FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';
 
 LOAD DATA LOCAL INFILE '~/q4/part-00014' INTO TABLE q6_table FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';
 
 LOAD DATA LOCAL INFILE '~/q4/part-00015' INTO TABLE q6_table FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';
 
 LOAD DATA LOCAL INFILE '~/q4/part-00016' INTO TABLE q6_table FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';
 
 LOAD DATA LOCAL INFILE '~/q4/part-00017' INTO TABLE q6_table FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';

ALTER TABLE `q6_table` ADD INDEX `k` (`k`);
commit;

########create q2_table###############
use q2_db;
GRANT ALL ON q2_db.* TO 'user'@'localhost';
set character_set_connection = 'utf8mb4';
set character_set_results = 'utf8mb4';
set character_set_client = 'utf8mb4';
set character_set_server = 'utf8mb4';
SET NAMES 'utf8mb4';
alter database q2_db default character set utf8mb4 COLLATE = utf8mb4_unicode_ci;







INSERT INTO q4_table (k, r) values('×Ö', '½}');
INSERT INTO q4_table (k, r) values('SSS', '×Ö');




