CREATE DATABASE  IF NOT EXISTS `studentskasluzba` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `studentskasluzba`;
-- MariaDB dump 10.19  Distrib 10.4.19-MariaDB, for Win64 (AMD64)
--
-- Host: 127.0.0.1    Database: studentskasluzba
-- ------------------------------------------------------
-- Server version	10.4.19-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `ispitnirok`
--

DROP TABLE IF EXISTS `ispitnirok`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ispitnirok` (
  `idRoka` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `naziv` varchar(45) CHARACTER SET utf8mb4 NOT NULL,
  `datumPocetka` datetime DEFAULT NULL,
  `datumKraja` datetime DEFAULT NULL,
  `aktivnost` tinyint(4) NOT NULL,
  `aktivnaPrijava` tinyint(4) NOT NULL,
  PRIMARY KEY (`idRoka`),
  UNIQUE KEY `idRoka_UNIQUE` (`idRoka`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ispitnirok`
--

LOCK TABLES `ispitnirok` WRITE;
/*!40000 ALTER TABLE `ispitnirok` DISABLE KEYS */;
INSERT INTO `ispitnirok` VALUES (1,'januar','2022-01-20 00:00:00','2022-02-05 00:00:00',0,0),(2,'februar','2022-02-10 00:00:00','2022-02-24 00:00:00',0,0),(3,'jun','2022-06-15 00:00:00','2022-06-30 00:00:00',0,0),(4,'septembar','2022-08-30 00:00:00','2022-09-14 00:00:00',0,0),(5,'oktobar','2022-09-16 00:00:00','2022-09-30 00:00:00',0,0),(6,'oktobar 2','2022-10-08 00:00:00','2022-10-09 00:00:00',0,0),(7,'oktobar 3','2022-10-25 00:00:00','2022-10-27 00:00:00',0,1);
/*!40000 ALTER TABLE `ispitnirok` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `izabranipredmeti`
--

DROP TABLE IF EXISTS `izabranipredmeti`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `izabranipredmeti` (
  `idPredmeta` int(11) unsigned NOT NULL,
  `idStudenta` int(11) unsigned NOT NULL,
  `smer` varchar(45) NOT NULL,
  `godinaUpisa` int(11) unsigned NOT NULL,
  KEY `predmet_fk_idPredmeta` (`idPredmeta`),
  KEY `student_brojIndeksa_fk_` (`idStudenta`,`smer`,`godinaUpisa`),
  CONSTRAINT `predmet_fk_idPredmeta` FOREIGN KEY (`idPredmeta`) REFERENCES `predmet` (`idPredmeta`),
  CONSTRAINT `student_brojIndeksa_fk_` FOREIGN KEY (`idStudenta`, `smer`, `godinaUpisa`) REFERENCES `student` (`idStudenta`, `smer`, `godinaUpisa`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `izabranipredmeti`
--

LOCK TABLES `izabranipredmeti` WRITE;
/*!40000 ALTER TABLE `izabranipredmeti` DISABLE KEYS */;
INSERT INTO `izabranipredmeti` VALUES (10001,1,'nrt',2021),(10003,1,'nrt',2021),(18006,1,'nrt',2021),(18007,1,'nrt',2021),(18009,1,'nrt',2021),(19004,1,'nrt',2021),(18008,1,'nrt',2021),(19001,1,'nrt',2021),(18001,1,'nrt',2021),(10004,1,'nrt',2021);
/*!40000 ALTER TABLE `izabranipredmeti` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `login`
--

DROP TABLE IF EXISTS `login`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `login` (
  `idZaposlenog` int(11) unsigned DEFAULT NULL,
  `korisnickoIme` varchar(45) NOT NULL,
  `lozinka` varchar(45) NOT NULL,
  `idStudenta` int(11) unsigned DEFAULT NULL,
  `smer` varchar(45) DEFAULT NULL,
  `godinaUpisa` int(11) unsigned DEFAULT NULL,
  `aktivan` tinyint(1) NOT NULL,
  PRIMARY KEY (`korisnickoIme`,`lozinka`),
  UNIQUE KEY `korisnickoIme_UNIQUE` (`korisnickoIme`),
  UNIQUE KEY `lozinka_UNIQUE` (`lozinka`),
  KEY `zaposleni_fk_idZaposlenog` (`idZaposlenog`),
  KEY `student_fk_brojIndeksa_` (`idStudenta`,`smer`,`godinaUpisa`),
  CONSTRAINT `student_fk_brojIndeksa_` FOREIGN KEY (`idStudenta`, `smer`, `godinaUpisa`) REFERENCES `student` (`idStudenta`, `smer`, `godinaUpisa`),
  CONSTRAINT `zaposleni_fk_idZaposlenog` FOREIGN KEY (`idZaposlenog`) REFERENCES `zaposleni` (`idZaposlenog`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `login`
--

LOCK TABLES `login` WRITE;
/*!40000 ALTER TABLE `login` DISABLE KEYS */;
INSERT INTO `login` VALUES (2001,'asistentkorac','kvukman9711',NULL,NULL,NULL,1),(NULL,'markonrt121','mngu3266',1,'nrt',2021,1),(NULL,'nikolart121','opsa9822',1,'rt',2021,1),(NULL,'pavleist221','lkwe2144',2,'ist',2021,1),(NULL,'petarnrt221','lswa4755',2,'nrt',2021,1),(1002,'profpejanovic','pmilos5478',NULL,NULL,NULL,1),(1001,'profstrbac','sperica3415',NULL,NULL,NULL,1),(3002,'saradnikborak','bmarko2245',NULL,NULL,NULL,1),(NULL,'sluzba','studentska1sluzba',NULL,NULL,NULL,1),(NULL,'sofijart221','pewa5744',2,'rt',2021,1),(NULL,'teodoraist121','asmw5447',1,'ist',2021,1);
/*!40000 ALTER TABLE `login` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `predmet`
--

DROP TABLE IF EXISTS `predmet`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `predmet` (
  `idPredmeta` int(11) unsigned NOT NULL,
  `naziv` varchar(45) CHARACTER SET utf8mb4 NOT NULL,
  `studijskiSmer` varchar(45) CHARACTER SET utf8mb4 DEFAULT NULL,
  `semestar` int(11) DEFAULT NULL,
  `Espb` int(11) DEFAULT NULL,
  `vidljiv` tinyint(1) NOT NULL,
  PRIMARY KEY (`idPredmeta`),
  UNIQUE KEY `idPredmeta_UNIQUE` (`idPredmeta`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `predmet`
--

LOCK TABLES `predmet` WRITE;
/*!40000 ALTER TABLE `predmet` DISABLE KEYS */;
INSERT INTO `predmet` VALUES (10001,'Engleski jezik',NULL,1,4,1),(10002,'Nemački jezik',NULL,1,4,1),(10003,'Inženjerska matematika',NULL,1,8,1),(10004,'Diskretna matematika',NULL,2,6,1),(18001,'Baze podataka','nrt',1,6,1),(18002,'Relacione baze podataka','nrt',5,6,1),(18003,'Tehnike vizuelnog programiranja','nrt',3,6,1),(18004,'Projektovanje računarskih igara','nrt',4,6,1),(18005,'Integracija softverskih tehnologija','nrt',6,6,1),(18006,'Aplikativni softver','nrt',1,6,1),(18007,'Osnovi informacionih tehnologija','nrt',1,6,1),(18008,'Uvod u objektno programiranje','nrt',2,6,1),(18009,'Osnovi programiranja','nrt',2,6,1),(19001,'Objektno programiranje 1','rt',2,6,1),(19002,'Objektno programiranje 2','rt',5,6,1),(19003,'Funkcionalno programiranje','rt',6,6,1),(19004,'Arhitektura i organizacija računara 1','rt',2,6,1),(19005,'Arhitektura i organizacija računara 2','rt',3,6,1),(19006,'Algoritmi i strukture podataka','rt',4,6,1);
/*!40000 ALTER TABLE `predmet` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prijaveispita`
--

DROP TABLE IF EXISTS `prijaveispita`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `prijaveispita` (
  `idPredmeta` int(11) unsigned NOT NULL,
  `idStudenta` int(11) unsigned NOT NULL,
  `smer` varchar(45) NOT NULL,
  `godinaUpisa` int(11) unsigned NOT NULL,
  `idRoka` int(11) unsigned NOT NULL,
  `datum` date NOT NULL,
  KEY `predmet_fk_idPredmeta_` (`idPredmeta`),
  KEY `ispitnirok_fk_idRoka` (`idRoka`),
  KEY `student_brojIndeksa_fk` (`idStudenta`,`smer`,`godinaUpisa`),
  CONSTRAINT `ispitnirok_fk_idRoka` FOREIGN KEY (`idRoka`) REFERENCES `ispitnirok` (`idRoka`),
  CONSTRAINT `predmet_fk_idPredmeta_` FOREIGN KEY (`idPredmeta`) REFERENCES `predmet` (`idPredmeta`),
  CONSTRAINT `student_brojIndeksa_fk` FOREIGN KEY (`idStudenta`, `smer`, `godinaUpisa`) REFERENCES `student` (`idStudenta`, `smer`, `godinaUpisa`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prijaveispita`
--

LOCK TABLES `prijaveispita` WRITE;
/*!40000 ALTER TABLE `prijaveispita` DISABLE KEYS */;
INSERT INTO `prijaveispita` VALUES (10001,1,'nrt',2021,1,'2022-01-05'),(10003,1,'nrt',2021,1,'2022-01-05'),(18006,1,'nrt',2021,1,'2022-01-05'),(18007,1,'nrt',2021,1,'2022-01-05'),(18007,1,'nrt',2021,2,'2022-01-05'),(18008,1,'nrt',2021,3,'2022-06-08'),(18009,1,'nrt',2021,3,'2022-06-08'),(19004,1,'nrt',2021,3,'2022-06-08'),(19001,1,'nrt',2021,7,'2022-10-19');
/*!40000 ALTER TABLE `prijaveispita` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `raspodelapredmeta`
--

DROP TABLE IF EXISTS `raspodelapredmeta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `raspodelapredmeta` (
  `idPredmeta` int(11) unsigned NOT NULL,
  `idZaposlenog` int(11) unsigned NOT NULL,
  KEY `predmet_idPredmeta_fk` (`idPredmeta`),
  KEY `zaposleni_idZaposlenog_fk` (`idZaposlenog`),
  CONSTRAINT `predmet_idPredmeta_fk` FOREIGN KEY (`idPredmeta`) REFERENCES `predmet` (`idPredmeta`),
  CONSTRAINT `zaposleni_idZaposlenog_fk` FOREIGN KEY (`idZaposlenog`) REFERENCES `zaposleni` (`idZaposlenog`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `raspodelapredmeta`
--

LOCK TABLES `raspodelapredmeta` WRITE;
/*!40000 ALTER TABLE `raspodelapredmeta` DISABLE KEYS */;
INSERT INTO `raspodelapredmeta` VALUES (10001,1007),(10002,1007),(10003,1006),(10003,3004),(10004,1006),(10004,3004),(18001,1002),(18001,1004),(18001,3005),(18002,1002),(18002,3005),(18003,1003),(18003,3002),(18003,3003),(18004,1003),(18004,3003),(18005,1003),(18005,3002),(18006,1008),(18006,3005),(18007,1010),(18007,3003),(18008,1005),(18008,3002),(18008,3003),(18009,1010),(18009,3006),(19001,1001),(19001,2001),(19001,3001),(19002,1001),(19002,2001),(19002,3001),(19003,1001),(19003,3001),(19004,1011),(19004,1009),(19004,3005),(19005,1009),(19005,3005),(19006,1006),(19006,3004);
/*!40000 ALTER TABLE `raspodelapredmeta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sala`
--

DROP TABLE IF EXISTS `sala`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sala` (
  `idSale` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `naziv` varchar(45) CHARACTER SET utf8mb4 NOT NULL,
  `brojMesta` int(11) NOT NULL,
  `oprema` varchar(45) CHARACTER SET utf8mb4 DEFAULT NULL,
  `vidljiv` tinyint(1) NOT NULL,
  PRIMARY KEY (`idSale`),
  UNIQUE KEY `idSale_UNIQUE` (`idSale`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sala`
--

LOCK TABLES `sala` WRITE;
/*!40000 ALTER TABLE `sala` DISABLE KEYS */;
INSERT INTO `sala` VALUES (1,'donja sala levo',100,'/',1),(2,'donja sala desno',100,'/',1),(3,'101',60,'/',1),(4,'102',60,'/',1),(5,'103',15,'računari',1),(6,'104',15,'računari',1),(7,'gornja sala levo',60,'/',1),(8,'gornja sala desno',60,'/',1),(9,'201',15,'računari',1),(10,'202',15,'računari',1),(11,'203',30,'računari',1),(12,'204',60,'računari',1),(15,'nova sala',50,'računari',0);
/*!40000 ALTER TABLE `sala` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `skolarina`
--

DROP TABLE IF EXISTS `skolarina`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `skolarina` (
  `smer` varchar(45) NOT NULL,
  `iznos` double NOT NULL,
  PRIMARY KEY (`smer`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `skolarina`
--

LOCK TABLES `skolarina` WRITE;
/*!40000 ALTER TABLE `skolarina` DISABLE KEYS */;
INSERT INTO `skolarina` VALUES ('asuv',130000),('avt',130000),('eko',130000),('elite',130000),('epo',130000),('ist',130000),('net',130000),('nrt',150000),('rt',150000);
/*!40000 ALTER TABLE `skolarina` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student`
--

DROP TABLE IF EXISTS `student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `student` (
  `idStudenta` int(11) unsigned NOT NULL,
  `smer` varchar(45) NOT NULL,
  `godinaUpisa` int(11) unsigned NOT NULL,
  `ime` varchar(45) CHARACTER SET utf8mb4 NOT NULL,
  `prezime` varchar(45) CHARACTER SET utf8mb4 NOT NULL,
  `finansiranje` varchar(45) CHARACTER SET utf8mb4 NOT NULL,
  `adresa` varchar(45) CHARACTER SET utf8mb4 DEFAULT NULL,
  `email` varchar(45) CHARACTER SET utf8mb4 DEFAULT NULL,
  `telefon` varchar(45) CHARACTER SET utf8mb4 DEFAULT NULL,
  `vidljiv` tinyint(1) NOT NULL,
  PRIMARY KEY (`idStudenta`,`smer`,`godinaUpisa`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student`
--

LOCK TABLES `student` WRITE;
/*!40000 ALTER TABLE `student` DISABLE KEYS */;
INSERT INTO `student` VALUES (1,'avt',2022,'Ilija','Ilic','budzet',NULL,NULL,NULL,1),(1,'ist',2021,'Teodora','Tomović','budzet','Takovska 10, Beograd','teodoraist121@gmail.com','06012347592',1),(1,'net',2022,'Pavle','Marković','budzet',NULL,NULL,NULL,1),(1,'nrt',2021,'Marko','Ilić','budzet','Jurija Gagarina 120, Beograd','markonrt121@gmail.com','06357846322',1),(1,'nrt',2022,'Bilja','Savic','budzet',NULL,NULL,NULL,1),(1,'rt',2021,'Nikola','Mišović','budzet','Gandijeva 53, Beograd','nikolart121@gmail.com','0616553211',1),(1,'rt',2022,'Stojan','Savic','saf',NULL,'stole93@gmail.com',NULL,1),(2,'ist',2021,'Pavle','Pavlović','budzet',NULL,NULL,NULL,1),(2,'nrt',2021,'Petar','Marić','saf',NULL,NULL,NULL,1),(2,'rt',2021,'Sofija','Todorović','budzet',NULL,NULL,NULL,1);
/*!40000 ALTER TABLE `student` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `uplatailizaduzenje`
--

DROP TABLE IF EXISTS `uplatailizaduzenje`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `uplatailizaduzenje` (
  `idUplate` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `opis` varchar(45) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `idStudenta` int(11) unsigned NOT NULL,
  `smer` varchar(45) NOT NULL,
  `godinaUpisa` int(11) unsigned NOT NULL,
  `datum` date NOT NULL,
  `iznos` double unsigned NOT NULL,
  PRIMARY KEY (`idUplate`),
  KEY `student_fk_brIndeksa_` (`idStudenta`,`smer`,`godinaUpisa`),
  CONSTRAINT `student_fk_brIndeksa_` FOREIGN KEY (`idStudenta`, `smer`, `godinaUpisa`) REFERENCES `student` (`idStudenta`, `smer`, `godinaUpisa`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `uplatailizaduzenje`
--

LOCK TABLES `uplatailizaduzenje` WRITE;
/*!40000 ALTER TABLE `uplatailizaduzenje` DISABLE KEYS */;
INSERT INTO `uplatailizaduzenje` VALUES (1,'upis',1,'nrt',2021,'2021-10-01',0),(10,'upis',1,'rt',2021,'2021-10-01',0),(11,'upis',1,'ist',2021,'2021-10-01',0),(12,'upis',2,'nrt',2021,'2021-10-20',150000),(13,'školarina',2,'nrt',2021,'2021-10-20',30000),(14,'upis',2,'rt',2021,'2021-10-20',150000),(15,'školarina',2,'rt',2021,'2021-10-20',30000),(16,'upis',2,'ist',2021,'2021-10-21',130000),(17,'školarina',2,'ist',2021,'2021-10-21',25000);
/*!40000 ALTER TABLE `uplatailizaduzenje` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `zakazivanjesale`
--

DROP TABLE IF EXISTS `zakazivanjesale`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `zakazivanjesale` (
  `idSale` int(11) unsigned NOT NULL,
  `idPredmeta` int(11) unsigned NOT NULL,
  `idZaposlenog` int(11) unsigned NOT NULL,
  `datum` date NOT NULL,
  `vremePocetka` time NOT NULL,
  `vremeKraja` time NOT NULL,
  PRIMARY KEY (`datum`,`vremePocetka`),
  KEY `sala_fk_idSale` (`idSale`),
  KEY `predmet_idPredmeta_fk_` (`idPredmeta`),
  KEY `predmet_fk_id_zaposlenog` (`idZaposlenog`),
  CONSTRAINT `predmet_fk_id_zaposlenog` FOREIGN KEY (`idZaposlenog`) REFERENCES `zaposleni` (`idZaposlenog`),
  CONSTRAINT `predmet_idPredmeta_fk_` FOREIGN KEY (`idPredmeta`) REFERENCES `predmet` (`idPredmeta`),
  CONSTRAINT `sala_fk_idSale` FOREIGN KEY (`idSale`) REFERENCES `sala` (`idSale`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `zakazivanjesale`
--

LOCK TABLES `zakazivanjesale` WRITE;
/*!40000 ALTER TABLE `zakazivanjesale` DISABLE KEYS */;
INSERT INTO `zakazivanjesale` VALUES (8,19004,3005,'2022-04-28','08:00:00','09:00:00'),(9,10004,3004,'2022-04-30','08:00:00','09:00:00'),(7,18003,1003,'2022-06-24','08:00:00','09:00:00'),(9,19002,3001,'2022-06-25','10:00:00','11:00:00'),(2,19002,1001,'2022-10-16','15:00:00','15:30:00'),(6,19001,1001,'2022-10-16','17:00:00','18:00:00'),(12,19003,1001,'2022-10-25','13:00:00','14:00:00'),(12,19001,1001,'2022-10-26','13:00:00','15:00:00'),(12,19002,1001,'2022-10-27','13:00:00','15:00:00');
/*!40000 ALTER TABLE `zakazivanjesale` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `zapisnik`
--

DROP TABLE IF EXISTS `zapisnik`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `zapisnik` (
  `idPredmeta` int(11) unsigned NOT NULL,
  `datum` date NOT NULL,
  `ocena` int(11) NOT NULL,
  `idStudenta` int(11) unsigned NOT NULL,
  `smer` varchar(45) NOT NULL,
  `godinaUpisa` int(11) unsigned NOT NULL,
  `idRoka` int(11) unsigned NOT NULL,
  KEY `predmet_idPredmeta_fk__` (`idPredmeta`),
  KEY `ispitnirok_idRoka_fk` (`idRoka`),
  KEY `student_fk_brojIndeksa` (`idStudenta`,`smer`,`godinaUpisa`),
  CONSTRAINT `ispitnirok_idRoka_fk` FOREIGN KEY (`idRoka`) REFERENCES `ispitnirok` (`idRoka`),
  CONSTRAINT `predmet_idPredmeta_fk__` FOREIGN KEY (`idPredmeta`) REFERENCES `predmet` (`idPredmeta`),
  CONSTRAINT `student_fk_brojIndeksa` FOREIGN KEY (`idStudenta`, `smer`, `godinaUpisa`) REFERENCES `student` (`idStudenta`, `smer`, `godinaUpisa`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `zapisnik`
--

LOCK TABLES `zapisnik` WRITE;
/*!40000 ALTER TABLE `zapisnik` DISABLE KEYS */;
INSERT INTO `zapisnik` VALUES (10001,'2022-01-22',10,1,'nrt',2021,1),(10003,'2022-01-24',10,1,'nrt',2021,1),(18006,'2022-01-30',10,1,'nrt',2021,1),(18007,'2022-02-01',5,1,'nrt',2021,1),(18007,'2022-02-22',9,1,'nrt',2021,2),(19004,'2022-06-15',9,1,'nrt',2021,3),(18009,'2022-06-18',9,1,'nrt',2021,3),(18008,'2022-06-20',10,1,'nrt',2021,3);
/*!40000 ALTER TABLE `zapisnik` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `zaposleni`
--

DROP TABLE IF EXISTS `zaposleni`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `zaposleni` (
  `idZaposlenog` int(11) unsigned NOT NULL,
  `pozicija` varchar(45) CHARACTER SET utf8mb4 NOT NULL,
  `ime` varchar(45) CHARACTER SET utf8mb4 NOT NULL,
  `prezime` varchar(45) CHARACTER SET utf8mb4 NOT NULL,
  `adresa` varchar(45) CHARACTER SET utf8mb4 DEFAULT NULL,
  `email` varchar(45) CHARACTER SET utf8mb4 DEFAULT NULL,
  `telefon` varchar(45) CHARACTER SET utf8mb4 DEFAULT NULL,
  `vidljiv` tinyint(1) NOT NULL,
  PRIMARY KEY (`idZaposlenog`),
  UNIQUE KEY `idZaposlenog_UNIQUE` (`idZaposlenog`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `zaposleni`
--

LOCK TABLES `zaposleni` WRITE;
/*!40000 ALTER TABLE `zaposleni` DISABLE KEYS */;
INSERT INTO `zaposleni` VALUES (1001,'profesor','Perica','Štrbac','Kapetan Misina 50, Beograd','pericastrbac@gmail.com','0615221452',1),(1002,'profesor','Miloš','Pejanović','Takovska 13, Beograd','milospejanovic@gmail.com','0634845211',1),(1003,'profesor','Zoran','Ćirović','Nehruova 136, Beograd','zokicirovic@gmail.com','0659596555',1),(1004,'profesor','Gabrijela','Dimić',NULL,NULL,NULL,1),(1005,'profesor','Svetlana','Štrbac-Savić',NULL,NULL,NULL,1),(1006,'profesor','Vladimir','Baltić',NULL,NULL,NULL,1),(1007,'profesor','Vesna','Jokanović',NULL,NULL,NULL,1),(1008,'profesor','Jelena','Mitić',NULL,NULL,'063455784',1),(1009,'profesor','Milan','Mijalković',NULL,NULL,NULL,1),(1010,'profesor','Slobodanka','Đenić',NULL,NULL,NULL,1),(1011,'profesor','Dragana','Prokin',NULL,NULL,NULL,1),(2001,'asistent','Vukman','Korać',NULL,NULL,NULL,1),(3001,'saradnik','Dušan','Marković',NULL,NULL,NULL,1),(3002,'saradnik','Marko','Borak',NULL,NULL,NULL,1),(3003,'saradnik','Martina','Nikolić',NULL,NULL,NULL,1),(3004,'saradnik','Ana','Savić',NULL,NULL,NULL,1),(3005,'saradnik','Divna','Popović',NULL,NULL,NULL,1),(3006,'saradnik','Boško','Bogojević',NULL,NULL,NULL,1);
/*!40000 ALTER TABLE `zaposleni` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-10-25 23:57:07
