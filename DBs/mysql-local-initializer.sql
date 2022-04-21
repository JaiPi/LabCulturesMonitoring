-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema LABMonitoring
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema LABMonitoring
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `LABMonitoring` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin ;
USE `LABMonitoring` ;

-- -----------------------------------------------------
-- Table `LABMonitoring`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `LABMonitoring`.`user` (
  `NomeUtilizador` VARCHAR(50) NOT NULL,
  `email` VARCHAR(50) NOT NULL,
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  `IDUtilizador` INT(11) NOT NULL,
  `Administrador` TINYINT(1) NOT NULL,
  PRIMARY KEY (`IDUtilizador`),
  INDEX `IDUtilizador` (`IDUtilizador` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;


-- -----------------------------------------------------
-- Table `LABMonitoring`.`Cultura`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `LABMonitoring`.`Cultura` (
  `IDCultura` INT(11) NOT NULL AUTO_INCREMENT,
  `NomeCultura` VARCHAR(50) NOT NULL,
  `IDUtilizador` INT(11) NOT NULL,
  `Estado` TINYINT(1) NOT NULL,
  `IDZona` INT(11) NOT NULL,
  `MaxLuz` FLOAT NOT NULL,
  `MinLuz` FLOAT NOT NULL,
  `MaxTemp` FLOAT NOT NULL,
  `MimTemp` FLOAT NOT NULL,
  `MaxHumidade` FLOAT NOT NULL,
  `MinHumidade` FLOAT NOT NULL,
  PRIMARY KEY (`IDCultura`),
  INDEX `USER` (`IDUtilizador` ASC),
  CONSTRAINT `USER`
    FOREIGN KEY (`IDUtilizador`)
    REFERENCES `LABMonitoring`.`user` (`IDUtilizador`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;


-- -----------------------------------------------------
-- Table `LABMonitoring`.`Alerta`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `LABMonitoring`.`Alerta` (
  `IDAlerta` INT(11) NOT NULL,
  `IDZona` INT(11) NOT NULL,
  `IDSensor` VARCHAR(10) NOT NULL,
  `DataHora` VARCHAR(45) NOT NULL,
  `TipoAlerta` VARCHAR(1) NOT NULL,
  `NomeCultura` VARCHAR(50) NOT NULL,
  `Descricao` VARCHAR(150) NOT NULL,
  `IDUtilizador` INT(11) NOT NULL,
  `IDCultura` INT(11) NOT NULL,
  PRIMARY KEY (`IDAlerta`),
  INDEX `Cultura` (`IDCultura` ASC),
  CONSTRAINT `Cultura`
    FOREIGN KEY (`IDCultura`)
    REFERENCES `LABMonitoring`.`Cultura` (`IDCultura`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;


-- -----------------------------------------------------
-- Table `LABMonitoring`.`Medicao`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `LABMonitoring`.`Medicao` (
  `IDMedicao` INT(11) NOT NULL,
  `IDZona` INT(11) NOT NULL,
  `IDSensor` VARCHAR(10) NOT NULL,
  `DataHora` VARCHAR(45) NOT NULL,
  `Leitura` FLOAT NULL DEFAULT NULL,
  `Valido` TINYINT(1) NOT NULL,
  PRIMARY KEY (`IDMedicao`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
