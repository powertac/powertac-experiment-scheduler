-- phpMyAdmin SQL Dump
-- version 4.5.4.1deb2ubuntu2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jul 13, 2017 at 02:39 PM
-- Server version: 5.7.18-0ubuntu0.16.04.1
-- PHP Version: 7.0.18-0ubuntu0.16.04.1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `powertac_experiment`
--

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `CleanDb` ()  BEGIN
	SET FOREIGN_KEY_CHECKS=0;
	TRUNCATE TABLE  `agents`;
	TRUNCATE TABLE  `games`;
	TRUNCATE TABLE  `parameters`;
	TRUNCATE TABLE  `experiments`;
	TRUNCATE TABLE  `studies`;
	SET FOREIGN_KEY_CHECKS=1;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `RestGames` ()  NO SQL
BEGIN
  UPDATE agents SET state = 'pending' WHERE NOT state = 'pending';
  UPDATE games SET state = 'boot_complete' WHERE NOT state =
    'boot_complete';
  UPDATE experiments SET state = 'pending' WHERE NOT state = 'pending';
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `agents`
--

CREATE TABLE `agents` (
  `agentId` int(11) NOT NULL,
  `gameId` int(11) NULL,
  `brokerId` int(11) NOT NULL,
  `brokerQueue` varchar(64) DEFAULT NULL,
  `state` enum('pending','in_progress','complete') NOT NULL,
  `balance` double NOT NULL DEFAULT '0',
  `machineId` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `brokers`
--

CREATE TABLE `brokers` (
  `brokerId` int(11) NOT NULL,
  `brokerName` varchar(45) NOT NULL,
  `brokerAuth` varchar(32) NOT NULL,
  `brokerShort` varchar(256) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `brokers`
--

INSERT INTO `brokers` (`brokerId`, `brokerName`, `brokerAuth`, `brokerShort`) VALUES
(1, 'sample1', '7b57b79f2d1467ecc14b59fd00a5fa4b', 'Crocodile_2014'),
(2, 'sample2', '2af9bac5775fba653e067fbac6464dad', 'sample2');

-- --------------------------------------------------------

--
-- Table structure for table `config`
--

CREATE TABLE `config` (
  `configId` int(11) NOT NULL,
  `configKey` varchar(256) NOT NULL,
  `configValue` longtext
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `config`
--

INSERT INTO `config` (`configId`, `configKey`, `configValue`) VALUES
(1, 'index_content', '<h2>Welcome to the PowerTac Experiment Scheduler!</h2>\r\n\r\n<p>\r\n</p>\r\n\r\n<br/><br/>');

-- --------------------------------------------------------

--
-- Table structure for table `experiments`
--

CREATE TABLE `experiments` (
  `experimentId` int(11) NOT NULL,
  `studyId` int(11) NOT NULL,
  `state` enum('pending','in_progress','complete') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `games`
--

CREATE TABLE `games` (
  `gameId` int(11) NOT NULL,
  `gameName` varchar(255) NOT NULL,
  `experimentId` int(11) NULL,
  `machineId` int(11) DEFAULT NULL,
  `state` enum('boot_pending','boot_in_progress','boot_complete','boot_failed','game_pending','game_ready','game_in_progress','game_complete','game_failed') NOT NULL,
  `serverQueue` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `locations`
--

CREATE TABLE `locations` (
  `locationId` int(11) NOT NULL,
  `location` varchar(256) NOT NULL,
  `timezone` int(11) NOT NULL,
  `fromDate` datetime NOT NULL,
  `toDate` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `locations`
--

INSERT INTO `locations` (`locationId`, `location`, `timezone`, `fromDate`, `toDate`) VALUES
(1, 'rotterdam', 1, '2009-01-01 00:00:00', '2011-06-01 00:00:00');

-- --------------------------------------------------------

--
-- Table structure for table `machines`
--

CREATE TABLE `machines` (
  `machineId` int(11) NOT NULL,
  `machineName` varchar(32) NOT NULL,
  `machineUrl` varchar(256) NOT NULL,
  `state` enum('idle','running') NOT NULL,
  `available` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `machines`
--

INSERT INTO `machines` (`machineId`, `machineName`, `machineUrl`, `state`, `available`) VALUES
(10, 'ubuntu10', '10.42.0.110', 'idle', 0),
(11, 'ubuntu11', '10.42.0.111', 'idle', 0),
(12, 'ubuntu12', '10.42.0.112', 'idle', 0),
(13, 'ubuntu13', '10.42.0.113', 'idle', 0),
(14, 'ubuntu14', '10.42.0.114', 'idle', 0),
(15, 'ubuntu15', '10.42.0.115', 'idle', 0),
(16, 'ubuntu16', '10.42.0.116', 'idle', 0),
(17, 'ubuntu17', '10.42.0.117', 'idle', 0),
(18, 'ubuntu18', '10.42.0.118', 'idle', 0),
(19, 'ubuntu19', '10.42.0.119', 'idle', 0);

-- --------------------------------------------------------

--
-- Table structure for table `parameters`
--

CREATE TABLE `parameters` (
  `parameterId` int(11) NOT NULL,
  `studyId` int(11) DEFAULT NULL,
  `experimentId` int(11) DEFAULT NULL,
  `gameId` int(11) DEFAULT NULL,
  `type` varchar(255) NOT NULL,
  `value` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `poms`
--

CREATE TABLE `poms` (
  `pomId` int(11) NOT NULL,
  `pomName` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `poms`
--

INSERT INTO `poms` (`pomId`, `pomName`) VALUES
(1, '1.5.0-SNAPSHOT');

-- --------------------------------------------------------

--
-- Table structure for table `studies`
--

CREATE TABLE `studies` (
  `studyId` int(11) NOT NULL,
  `userId` int(11) NOT NULL,
  `name` varchar(254) NOT NULL,
  `state` enum('pending','in_progress','paused','complete') NOT NULL,
  `variableName` varchar(255) NOT NULL,
  `variableValue` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `userId` int(11) NOT NULL,
  `userName` varchar(45) NOT NULL,
  `institution` varchar(256) DEFAULT NULL,
  `contactName` varchar(256) DEFAULT NULL,
  `contactEmail` varchar(256) DEFAULT NULL,
  `contactPhone` varchar(256) DEFAULT NULL,
  `salt` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `permission` enum('admin','researcher','broker','guest') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`userId`, `userName`, `institution`, `contactName`, `contactEmail`, `contactPhone`, `salt`, `password`, `permission`) VALUES
(1, 'gbuijs', 'RSM-BIT', 'Govert Buijs', 'buijs@rsm.nl', '', '7d1345fe4a270bb6b62fab267f8b913e', 'aa71e6bb2e7005e5342fa361f1f0dd27', 'admin'),
(2, 'jcollins', 'Minnesota', 'John Collins', 'jcollins@cs.umn.edu', '', 'df5421ecae80a8e3a95aab9a906f0ea1', '0f8a24db7dca35163d61ecc728881fe4', 'admin'),
(3, 'test', '', '', '', '', '7d1345fe4a270bb6b62fab267f8b913e', 'aa71e6bb2e7005e5342fa361f1f0dd27', 'researcher'),
(4, 'Mohammad', '', '', '', '', 'db47529b587faf0bc4e33ca850c76e50', '857e3c5ca24a2b92c7d992a2179e01c9', 'broker');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `agents`
--
ALTER TABLE `agents`
  ADD PRIMARY KEY (`agentId`),
  ADD KEY `agent_refs1` (`brokerId`),
  ADD KEY `agent_refs2` (`gameId`);

--
-- Indexes for table `brokers`
--
ALTER TABLE `brokers`
  ADD PRIMARY KEY (`brokerId`),
  ADD UNIQUE KEY `brokerName` (`brokerName`),
  ADD UNIQUE KEY `brokerAuth` (`brokerAuth`);

--
-- Indexes for table `config`
--
ALTER TABLE `config`
  ADD PRIMARY KEY (`configId`),
  ADD UNIQUE KEY `configKey` (`configKey`);

--
-- Indexes for table `experiments`
--
ALTER TABLE `experiments`
  ADD PRIMARY KEY (`experimentId`);

--
-- Indexes for table `games`
--
ALTER TABLE `games`
  ADD PRIMARY KEY (`gameId`),
  ADD KEY `game_refs1` (`experimentId`),
  ADD KEY `game_refs2` (`machineId`);

--
-- Indexes for table `locations`
--
ALTER TABLE `locations`
  ADD PRIMARY KEY (`locationId`);

--
-- Indexes for table `machines`
--
ALTER TABLE `machines`
  ADD PRIMARY KEY (`machineId`);

--
-- Indexes for table `parameters`
--
ALTER TABLE `parameters`
  ADD PRIMARY KEY (`parameterId`),
  ADD KEY `studyId` (`studyId`),
  ADD KEY `experimentId` (`experimentId`),
  ADD KEY `gameId` (`gameId`);

--
-- Indexes for table `poms`
--
ALTER TABLE `poms`
  ADD PRIMARY KEY (`pomId`);

--
-- Indexes for table `studies`
--
ALTER TABLE `studies`
  ADD PRIMARY KEY (`studyId`),
  ADD UNIQUE KEY `name` (`name`),
  ADD KEY `experimentset_refs` (`userId`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`userId`),
  ADD UNIQUE KEY `userName` (`userName`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `agents`
--
ALTER TABLE `agents`
  MODIFY `agentId` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `brokers`
--
ALTER TABLE `brokers`
  MODIFY `brokerId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT for table `config`
--
ALTER TABLE `config`
  MODIFY `configId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;
--
-- AUTO_INCREMENT for table `experiments`
--
ALTER TABLE `experiments`
  MODIFY `experimentId` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `games`
--
ALTER TABLE `games`
  MODIFY `gameId` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `locations`
--
ALTER TABLE `locations`
  MODIFY `locationId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT for table `machines`
--
ALTER TABLE `machines`
  MODIFY `machineId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;
--
-- AUTO_INCREMENT for table `parameters`
--
ALTER TABLE `parameters`
  MODIFY `parameterId` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `poms`
--
ALTER TABLE `poms`
  MODIFY `pomId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT for table `studies`
--
ALTER TABLE `studies`
  MODIFY `studyId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `userId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
