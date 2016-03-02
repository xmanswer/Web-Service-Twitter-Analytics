#! /bin/bash

################### 
## this script is used to install mysql on ubuntu vm and to load a small 80M data 

sudo apt-get update
sudo apt-get install mysql-server
sudo apt-get install mysql-client-core-5.5
sudo service mysql restart
# set the password 15619project
mysql -u root -p15619project
describe;
create database tweets;
show databases;
use tweets;

## build a table with 2 columns
DROP TABLE IF EXISTS `tweets_query1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tweets_query1` (
  `user_id_time` varchar(32) NOT NULL,
  `response` varchar(500) NOT NULL,
  PRIMARY KEY (`user_id_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


# scp the etl data that have 2 cols to be in tweets_query1.txt 
# then in the terminal
mysqlimport -u root -p15619project --local tweets tweets_query1.txt --fields-terminated-by='\t' --lines-terminated-by='\n'


## build a table with 5 columns
DROP TABLE IF EXISTS `tweets_query1_old`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tweets_query1_old` (
  `user_id` varchar(20) NOT NULL,
  `time` datetime NOT NULL,
  `tweet_id` varchar(20) NOT NULL,
  `sentiment_score` int NOT NULL, 
  `tweet_content` varchar(180) NOT NULL,
  PRIMARY KEY (`user_id`,`time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

# scp the etl data that have 5 cols to be in tweets_query1_old.txt 
# then in the terminal
mysqlimport -u root -p15619project --local tweets tweets_query1_old.txt --fields-terminated-by='\t' --lines-terminated-by='\n'