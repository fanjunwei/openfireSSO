CREATE  TABLE IF NOT EXISTS `ssoServer` (
  `serverID` CHAR(36) NOT NULL ,
  `serverName` VARCHAR(255) NOT NULL ,
  `key` CHAR(32) NOT NULL ,
  `enable` BIT NOT NULL ,
  PRIMARY KEY (`serverID`) );
