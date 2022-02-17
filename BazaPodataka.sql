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
  PRIMARY KEY (`idRoka`),
  UNIQUE KEY `idRoka_UNIQUE` (`idRoka`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ispitnirok`
--

LOCK TABLES `ispitnirok` WRITE;
/*!40000 ALTER TABLE `ispitnirok` DISABLE KEYS */;
INSERT INTO `ispitnirok` VALUES (1,'januar','2021-01-20 00:00:00','2021-02-05 00:00:00',1),(2,'februar','2021-02-08 00:00:00','2021-02-23 00:00:00',0),(3,'jun','2021-06-15 00:00:00','2021-06-30 00:00:00',0),(4,'septembar','2021-08-30 00:00:00','2021-09-14 00:00:00',0),(5,'oktobar','2021-09-16 00:00:00','2021-09-30 00:00:00',0);
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
INSERT INTO `izabranipredmeti` VALUES (10001,1,'nrt',2021),(10003,1,'nrt',2021),(18006,1,'nrt',2021),(18007,1,'nrt',2021),(18009,1,'nrt',2021),(19004,1,'nrt',2021),(18008,1,'nrt',2021),(19005,1,'nrt',2021),(19006,1,'nrt',2021),(10004,1,'nrt',2021);
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
  `aktivan` tinyint(4) NOT NULL,
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
INSERT INTO `login` VALUES (2001,'asistentkorac','kvukman9711',NULL,NULL,NULL,0),(NULL,'markonrt121','mngu3266',1,'nrt',2021,0),(NULL,'nikolart121','opsa9822',1,'rt',2021,0),(NULL,'pavleist221','lkwe2144',2,'ist',2021,0),(NULL,'petarnrt221','lswa4755',2,'nrt',2021,0),(1002,'profpejanovic','pmilos5478',NULL,NULL,NULL,0),(1001,'profstrbac','sperica3415',NULL,NULL,NULL,0),(3002,'saradnikborak','bmarko2245',NULL,NULL,NULL,0),(NULL,'sluzba','studentska1sluzba',NULL,NULL,NULL,0),(NULL,'sofijart221','pewa5744',2,'rt',2021,0),(NULL,'teodoraist121','asmw5447',1,'ist',2021,0);
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
  `vidljiv` tinyint(4) NOT NULL,
  PRIMARY KEY (`idPredmeta`),
  UNIQUE KEY `idPredmeta_UNIQUE` (`idPredmeta`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `predmet`
--

LOCK TABLES `predmet` WRITE;
/*!40000 ALTER TABLE `predmet` DISABLE KEYS */;
INSERT INTO `predmet` VALUES (10001,'Engleski jezik',NULL,1,4,0),(10002,'Nemački jezik',NULL,1,4,0),(10003,'Inženjerska matematika',NULL,1,8,0),(10004,'Diskretna matematika',NULL,2,6,0),(10005,'RIP',NULL,3,5,0),(18001,'Baze podataka','nrt',3,6,0),(18002,'Relacione baze podataka','nrt',6,6,0),(18003,'Tehnike vizuelnog programiranja','nrt',4,6,0),(18004,'Projektovanje računarskih igara','nrt',5,6,0),(18005,'Integracija softverskih tehnologija','nrt',6,6,0),(18006,'Aplikativni softver','nrt',1,6,0),(18007,'Osnovi informacionih tehnologija','nrt',1,6,0),(18008,'Uvod u objektno programiranje','nrt',2,6,0),(18009,'Osnovi programiranja','nrt',1,6,0),(18010,'UOP','nrt',2,5,0),(19001,'Objektno programiranje 1','rt',4,6,0),(19002,'Objektno programiranje 2','rt',5,6,0),(19003,'Funkcionalno programiranje','rt',6,6,0),(19004,'Arhitektura i organizacija računara 1','rt',1,6,0),(19005,'Arhitektura i organizacija računara 2','rt',2,6,0),(19006,'Algoritmi i strukture podataka','rt',2,6,0);
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
INSERT INTO `prijaveispita` VALUES (10001,1,'nrt',2021,1),(10003,1,'nrt',2021,1),(18006,1,'nrt',2021,1);
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
  PRIMARY KEY (`idSale`),
  UNIQUE KEY `idSale_UNIQUE` (`idSale`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sala`
--

LOCK TABLES `sala` WRITE;
/*!40000 ALTER TABLE `sala` DISABLE KEYS */;
INSERT INTO `sala` VALUES (1,'donja sala levo',100,'/'),(2,'donja sala desno',100,'/'),(3,'101',60,'/'),(4,'102',60,'/'),(5,'103',15,'računari'),(6,'104',15,'računari'),(7,'gornja sala levo',60,'/'),(8,'gornja sala desno',60,'/'),(9,'201',15,'računari'),(10,'202',15,'računari'),(11,'203',30,'računari'),(12,'204',60,'računari');
/*!40000 ALTER TABLE `sala` ENABLE KEYS */;
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
  `vidljiv` tinyint(4) NOT NULL,
  PRIMARY KEY (`idStudenta`,`smer`,`godinaUpisa`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student`
--

LOCK TABLES `student` WRITE;
/*!40000 ALTER TABLE `student` DISABLE KEYS */;
INSERT INTO `student` VALUES (1,'ist',2021,'Teodora','Tomović','budzet','Takovska 10, Beograd','teodoraist121@gmail.com','0601234759',0),(1,'nrt',2021,'Marko','Ilić','budzet','Jurija Gagarina 120, Beograd','markonrt121@gmail.com','06357846322',0),(1,'nrt',2022,'Bilja','Savic','budzet','','','',0),(1,'rt',2021,'Nikola','Mišović','budzet','Gandijeva 53, Beograd','nikolart121@gmail.com','0616553211',0),(1,'rt',2022,'Stojan','Savic','saf','','stole93@gmail.com','',0),(2,'ist',2021,'Pavle','Pavlović','budzet',NULL,NULL,NULL,0),(2,'nrt',2021,'Petar','Marić','budzet',NULL,NULL,NULL,0),(2,'rt',2021,'Sofija','Todorović','budzet',NULL,NULL,NULL,0),(3,'ist',2021,'Kristina','Krstić','budzet',NULL,NULL,NULL,0),(3,'nrt',2021,'Jovana','Stojanović','budzet',NULL,NULL,NULL,0),(3,'rt',2021,'Miloš','Marković','budzet',NULL,NULL,NULL,0),(4,'ist',2021,'Filip','Filipović','budzet',NULL,NULL,NULL,0),(4,'nrt',2021,'Miloš','Petrović','saf',NULL,NULL,NULL,0),(4,'rt',2021,'Branko','Pavić','budzet',NULL,NULL,NULL,0),(5,'nrt',2021,'Ana','Andrić','saf',NULL,NULL,NULL,0),(6,'nrt',2021,'Stefan','Nikolić','saf',NULL,NULL,NULL,0);
/*!40000 ALTER TABLE `student` ENABLE KEYS */;
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
  `datum` date NOT NULL,
  `vremePocetka` time NOT NULL,
  `vremeKraja` time NOT NULL,
  KEY `sala_fk_idSale` (`idSale`),
  KEY `predmet_idPredmeta_fk_` (`idPredmeta`),
  CONSTRAINT `predmet_idPredmeta_fk_` FOREIGN KEY (`idPredmeta`) REFERENCES `predmet` (`idPredmeta`),
  CONSTRAINT `sala_fk_idSale` FOREIGN KEY (`idSale`) REFERENCES `sala` (`idSale`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `zakazivanjesale`
--

LOCK TABLES `zakazivanjesale` WRITE;
/*!40000 ALTER TABLE `zakazivanjesale` DISABLE KEYS */;
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
INSERT INTO `zapisnik` VALUES (10001,'2022-01-30',10,1,'nrt',2021,1),(18006,'2022-02-01',10,1,'nrt',2021,1);
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
  `vidljiv` tinyint(4) NOT NULL,
  PRIMARY KEY (`idZaposlenog`),
  UNIQUE KEY `idZaposlenog_UNIQUE` (`idZaposlenog`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `zaposleni`
--

LOCK TABLES `zaposleni` WRITE;
/*!40000 ALTER TABLE `zaposleni` DISABLE KEYS */;
INSERT INTO `zaposleni` VALUES (1001,'profesor','Perica','Štrbac','Kapetan Misina 50, Beograd','pericastrbac@gmail.com','0615221452',0),(1002,'profesor','Miloš','Pejanović','Takovska 13, Beograd','milospejanovic@gmail.com','0634845211',0),(1003,'profesor','Zoran','Ćirović','Nehruova 136, Beograd','zokicirovic@gmail.com','0659596555',0),(1004,'profesor','Gabrijela','Dimić',NULL,NULL,NULL,0),(1005,'profesor','Svetlana','Štrbac-Savić',NULL,NULL,NULL,0),(1006,'profesor','Vladimir','Baltić',NULL,NULL,NULL,0),(1007,'profesor','Vesna','Jokanović',NULL,NULL,NULL,0),(1008,'profesor','Jelena','Mitić',NULL,NULL,NULL,0),(1009,'profesor','Milan','Mijalković',NULL,NULL,NULL,0),(1010,'profesor','Slobodanka','Đenić',NULL,NULL,NULL,0),(1011,'profesor','Dragana','Prokin',NULL,NULL,NULL,0),(2001,'asistent','Vukman','Korać',NULL,NULL,NULL,0),(2002,'asistent','Ivo','Ivic','ivina','','',0),(3001,'saradnik','Dušan','Marković',NULL,NULL,NULL,0),(3002,'saradnik','Marko','Borak',NULL,NULL,NULL,0),(3003,'saradnik','Martina','Nikolić',NULL,NULL,NULL,0),(3004,'saradnik','Ana','Savić',NULL,NULL,NULL,0),(3005,'saradnik','Divna','Popović',NULL,NULL,NULL,0),(3006,'saradnik','Boško','Bogojević',NULL,NULL,NULL,0);
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

-- Dump completed on 2022-02-17 23:10:52
