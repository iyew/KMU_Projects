-- phpMyAdmin SQL Dump
-- version 4.6.6deb5
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Jan 25, 2019 at 01:19 AM
-- Server version: 5.7.22-0ubuntu18.04.1
-- PHP Version: 7.2.5-0ubuntu0.18.04.1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `Streaming`
--

-- --------------------------------------------------------

--
-- Table structure for table `account`
--

CREATE TABLE `account` (
  `ID` varchar(10) NOT NULL,
  `Password` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `account`
--

INSERT INTO `account` (`ID`, `Password`) VALUES
('22', '22'),
('5171515', '1q2w3e4r');

-- --------------------------------------------------------

--
-- Table structure for table `music_list`
--

CREATE TABLE `music_list` (
  `music_title` varchar(50) NOT NULL,
  `music_artist` varchar(50) NOT NULL,
  `music_owner` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `music_list`
--

INSERT INTO `music_list` (`music_title`, `music_artist`, `music_owner`) VALUES
('Payday', 'Alesso', '5171515'),
('Start', 'Depapepe', '22'),
('Bass is Kicking (Remixed)', 'DJ Splash', '22'),
('Late Autumn', 'Eddie', '5171515'),
('Don\'t Stop Me Now', 'Queen', '22'),
('Unity', 'TheFatRat', '5171515');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `account`
--
ALTER TABLE `account`
  ADD PRIMARY KEY (`ID`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
