# $Revision$
# $Date$

INSERT INTO ofVersion (name, version) VALUES ('sso', 0);

CREATE TABLE `ssoServer` (
  `serverID` char(36) NOT NULL,
  `serverName` varchar(255) NOT NULL,
  `key` char(32) NOT NULL,
  `enable` bit(1) NOT NULL,
  PRIMARY KEY (`serverID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
