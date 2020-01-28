-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema learndeck
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `learndeck` DEFAULT CHARACTER SET utf8 ;
USE `learndeck` ;

-- -----------------------------------------------------
-- Table `learndeck`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `learndeck`.`user` (
  `user_id` INT(11) NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(45) NOT NULL,
  `last_name` VARCHAR(45) NOT NULL,
  `user_name` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) NOT NULL,
  `user_type` ENUM('teacher', 'student') NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX `user_name_UNIQUE` (`user_name` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 309
DEFAULT CHARACTER SET = utf8;




-- -----------------------------------------------------
-- Table `learndeck`.`teacher`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `learndeck`.`teacher` (
  `teacher_id` INT(11) NOT NULL,
  PRIMARY KEY (`teacher_id`),
  CONSTRAINT `fk_teacher_user`
    FOREIGN KEY (`teacher_id`)
    REFERENCES `learndeck`.`user` (`user_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;




-- -----------------------------------------------------
-- Table `learndeck`.`course`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `learndeck`.`course` (
  `course_id` INT(11) NOT NULL AUTO_INCREMENT,
  `course_name` VARCHAR(45) NOT NULL,
  `made_by_teacher` INT(11) NOT NULL,
  PRIMARY KEY (`course_id`),
  UNIQUE INDEX `course_name_UNIQUE` (`course_name` ASC) VISIBLE,
  INDEX `fk_teacher_idx` (`made_by_teacher` ASC) VISIBLE,
  CONSTRAINT `fk_teacker`
    FOREIGN KEY (`made_by_teacher`)
    REFERENCES `learndeck`.`teacher` (`teacher_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 55
DEFAULT CHARACTER SET = utf8;




-- -----------------------------------------------------
-- Table `learndeck`.`card`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `learndeck`.`card` (
  `card_id` INT(11) NOT NULL AUTO_INCREMENT,
  `course_id` INT(11) NOT NULL,
  `general_difficulty` DECIMAL(10,3) NOT NULL DEFAULT '1.000',
  PRIMARY KEY (`card_id`, `course_id`),
  INDEX `fk_card_course1_idx` (`course_id` ASC) VISIBLE,
  CONSTRAINT `fk_card_course1`
    FOREIGN KEY (`course_id`)
    REFERENCES `learndeck`.`course` (`course_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 180
DEFAULT CHARACTER SET = utf8;




-- -----------------------------------------------------
-- Table `learndeck`.`student`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `learndeck`.`student` (
  `student_id` INT(11) NOT NULL,
  `birth_date` DATE NOT NULL,
  `e_mail` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`student_id`),
  UNIQUE INDEX `e_mail_UNIQUE` (`e_mail` ASC) VISIBLE,
  CONSTRAINT `fk_teacher_user0`
    FOREIGN KEY (`student_id`)
    REFERENCES `learndeck`.`user` (`user_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;




-- -----------------------------------------------------
-- Table `learndeck`.`student_has_course`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `learndeck`.`student_has_course` (
  `student_id` INT(11) NOT NULL,
  `course_id` INT(11) NOT NULL,
  PRIMARY KEY (`student_id`, `course_id`),
  INDEX `fk_course_idx` (`course_id` ASC) VISIBLE,
  CONSTRAINT `fk_course`
    FOREIGN KEY (`course_id`)
    REFERENCES `learndeck`.`course` (`course_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_student`
    FOREIGN KEY (`student_id`)
    REFERENCES `learndeck`.`student` (`student_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;




-- -----------------------------------------------------
-- Table `learndeck`.`card_review`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `learndeck`.`card_review` (
  `card_id` INT(11) NOT NULL,
  `course_id` INT(11) NOT NULL,
  `student_id` INT(11) NOT NULL,
  `next_review_date` DATE NULL DEFAULT '1111-11-11',
  `hard_pushed` INT(11) NULL DEFAULT '0',
  `medium_pushed` INT(11) NULL DEFAULT '0',
  `easy_pushed` INT(11) NULL DEFAULT '0',
  `very_easy_pushed` INT(11) NULL DEFAULT '0',
  PRIMARY KEY (`course_id`, `card_id`, `student_id`),
  INDEX `fk_card_review_card1_idx` (`card_id` ASC, `course_id` ASC) VISIBLE,
  INDEX `fk_card_review_student1_idx` (`student_id` ASC, `course_id` ASC) VISIBLE,
  CONSTRAINT `fk_card`
    FOREIGN KEY (`card_id` , `course_id`)
    REFERENCES `learndeck`.`card` (`card_id` , `course_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_student_id`
    FOREIGN KEY (`student_id` , `course_id`)
    REFERENCES `learndeck`.`student_has_course` (`student_id` , `course_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

USE `learndeck` ;



-- -----------------------------------------------------
-- procedure Card_Add
-- -----------------------------------------------------

DELIMITER $$
USE `learndeck`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `Card_Add`(
	IN i_course_id INT,
	OUT o_card_id INT
)
BEGIN
	
    DECLARE last_id INT;
    DECLARE done INT DEFAULT FALSE;
    DECLARE current_student_id INT;
    DECLARE curs CURSOR FOR SELECT student_id FROM student_has_course WHERE course_id = i_course_id;
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
	DECLARE EXIT HANDLER FOR SQLEXCEPTION, SQLWARNING
	BEGIN
		ROLLBACK;
		RESIGNAL;
	END;
    
START TRANSACTION;
	
	INSERT INTO card(card_id, course_id)
		VALUES(card_id, i_course_id); 
	
    SELECT LAST_INSERT_ID() INTO last_id;

    OPEN curs;
    
    #Add new card reviews to all students enrolled to the course
    read_loop: LOOP
    
		FETCH curs INTO current_student_id;
        
        IF done THEN
			LEAVE read_loop;
		ELSE 
        
        INSERT INTO card_review(card_id, course_id, student_id)
			VALUES(last_id, i_course_id, current_student_id);
        
        END IF;
	END LOOP;
	CLOSE curs;
    
    SELECT LAST_INSERT_ID() INTO o_card_id;
    
    COMMIT;
END$$

DELIMITER ;



-- -----------------------------------------------------
-- procedure Card_Difficulty_Update
-- -----------------------------------------------------

DELIMITER $$
USE `learndeck`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `Card_Difficulty_Update`(
	IN i_course_id INT,
    IN i_card_id INT
)
BEGIN

DECLARE total INT;
DECLARE h_modifier DOUBLE DEFAULT -0.9;
DECLARE m_modifier DOUBLE DEFAULT -0.2;
DECLARE e_modifier DOUBLE DEFAULT 0.5;
DECLARE ve_modifier DOUBLE DEFAULT 1.0;

CREATE TEMPORARY TABLE card_stat 
	AS(
		SELECT
			SUM(hard_pushed) AS h,
			SUM(medium_pushed) AS m,
			SUM(easy_pushed) AS e,
			SUM(very_easy_pushed) AS ve
				FROM card_review
					WHERE course_id = i_course_id AND card_id = i_card_id
	);

            
	SELECT (SUM(h) + SUM(m) + SUM(e) + SUM(ve)) FROM card_stat INTO total;
    SET h_modifier = (SELECT h FROM card_stat)/total * h_modifier;
    SET m_modifier = (SELECT m FROM card_stat)/total * m_modifier;
    SET e_modifier = (SELECT e FROM card_stat)/total * e_modifier;
    SET ve_modifier = (SELECT ve FROM card_stat)/total * ve_modifier;
	
    DROP TEMPORARY TABLE card_stat;     
    
	UPDATE card
		SET general_difficulty = FORMAT((h_modifier + m_modifier + e_modifier + ve_modifier + 1), 3)
			WHERE course_id = i_course_id AND card_id = i_card_id;
	
END$$

DELIMITER ;



-- -----------------------------------------------------
-- procedure Card_Difficulty_Update_General
-- -----------------------------------------------------

DELIMITER $$
USE `learndeck`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `Card_Difficulty_Update_General`(
	IN i_course_id INT,
    IN i_difficulty DOUBLE
)
BEGIN

	DECLARE done INT DEFAULT FALSE;
    DECLARE current_card_id INT;
    DECLARE new_difficulty DOUBLE;
    DECLARE curs CURSOR FOR SELECT card_id FROM card WHERE card.course_id = i_course_id;
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
	OPEN curs;
    
    read_loop: LOOP
    
		FETCH curs INTO current_card_id;
        
        IF done THEN
			LEAVE read_loop;
		ELSE 
			
			UPDATE card C
				SET C.general_difficulty = C.general_difficulty + i_difficulty
					WHERE C.card_id = current_card_id;
        
        END IF;
	END LOOP;
    
CLOSE curs;

END$$

DELIMITER ;



-- -----------------------------------------------------
-- procedure Course_Add
-- -----------------------------------------------------

DELIMITER $$
USE `learndeck`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `Course_Add`(
	IN i_course_name varchar(45),
    IN i_made_by_teacher INT,
    OUT o_course_id INT
)
BEGIN

    DECLARE EXIT HANDLER FOR SQLEXCEPTION, SQLWARNING
	BEGIN
		ROLLBACK;
		RESIGNAL;
	END;

START TRANSACTION;

	INSERT INTO course(course_id ,course_name, made_by_teacher)
		VALUES(course_id, i_course_name, i_made_by_teacher);
    
    SELECT LAST_INSERT_ID() INTO o_course_id; 

COMMIT;
    
END$$

DELIMITER ;



-- -----------------------------------------------------
-- procedure Course_Add_Student
-- -----------------------------------------------------

DELIMITER $$
USE `learndeck`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `Course_Add_Student`(
	IN i_student_id INT,
    IN i_course_id INT
)
BEGIN

	DECLARE done INT DEFAULT FALSE;
    DECLARE current_card_id INT;
    DECLARE curs CURSOR FOR SELECT card_id FROM card WHERE card.course_id = i_course_id;
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
	DECLARE EXIT HANDLER FOR SQLEXCEPTION, SQLWARNING
	BEGIN
		ROLLBACK;
		RESIGNAL;
	END;
    
START TRANSACTION;
    
    INSERT INTO student_has_course(student_id, course_id)
    VALUES(i_student_id, i_course_id);
    
    OPEN curs;
    
    #Add new reviews for all the cards of the course
    read_loop: LOOP
    
		FETCH curs INTO current_card_id;
        
        IF done THEN
			LEAVE read_loop;
		ELSE 
        
        INSERT INTO card_review(card_id, course_id, student_id)
			VALUES(current_card_id, i_course_id, i_student_id);
        
        END IF;
	END LOOP;
    CLOSE curs;
    
	COMMIT;
END$$

DELIMITER ;



-- -----------------------------------------------------
-- procedure Student_Course_Info_Get
-- -----------------------------------------------------

DELIMITER $$
USE `learndeck`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `Student_Course_Info_Get`(
	IN i_student_id INT
)
BEGIN
	
    SELECT 
	course_name AS "COURSE", 
    COUNT(card_id) AS "CARDS",

	(SELECT COUNT(*) FROM card_review CR 
		INNER JOIN student_has_course SHC 
			ON CR.course_id = SHC.course_id
		WHERE CR.course_id = CO.course_id AND next_review_date IS NULL
	) AS "NEW_CARDS",

	(SELECT COUNT(DISTINCT card_id) FROM card_review CR
		LEFT JOIN student_has_course SHC 
			ON SHC.student_id = CR.student_id
		WHERE (CURDATE() >= next_review_date OR next_review_date IS NULL) AND CR.course_id = CO.course_Id
	) AS "DUE_CARDS"

 FROM course CO
 
	INNER JOIN card C 
		ON CO.course_id = C.course_id
	INNER JOIN student_has_course SHC 
		ON SHC.course_id = CO.course_id
        
	WHERE SHC.student_id = i_student_id
GROUP BY CO.course_name;

END$$

DELIMITER ;



-- -----------------------------------------------------
-- procedure User_Login
-- -----------------------------------------------------

DELIMITER $$
USE `learndeck`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `User_Login`(
	IN i_user_name varchar(45), 
	IN i_password varchar(45),
    OUT o_user_id INT,
    OUT o_user_type VARCHAR(45)
)
BEGIN

	SELECT user_id, user_type FROM user U
		WHERE U.user_name = i_user_name AND U.password = i_password
			INTO o_user_id, o_user_type;
    
END$$

DELIMITER ;



-- -----------------------------------------------------
-- procedure User_Name_Create
-- -----------------------------------------------------

DELIMITER $$
USE `learndeck`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `User_Name_Create`(
	IN i_user_name VARCHAR(45),
    OUT o_user_name VARCHAR(45)
)
BEGIN

	DECLARE num INT;
	DECLARE return_name varchar(45);
    DECLARE start_name varchar(45);
    
	SET num = 1;
    SET start_name = CONCAT(i_user_name, num);
    SET return_name = i_user_name;
    
    WHILE(SELECT MAX(user_id) FROM user U WHERE U.user_name = return_name > 0) DO
		
        SET return_name = CONCAT(i_user_name, num);
		SET num = num + 1;
       
	END WHILE;
    
	SELECT return_name INTO o_user_name;
    
END$$

DELIMITER ;



-- -----------------------------------------------------
-- procedure User_Student_Add
-- -----------------------------------------------------

DELIMITER $$
USE `learndeck`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `User_Student_Add`(
	IN i_first_name VARCHAR(45), 
	IN i_last_name VARCHAR(45), 
	IN i_user_name VARCHAR(45), 
	IN i_password VARCHAR(45), 
	IN i_birth_date VARCHAR(10), 
	IN i_e_mail VARCHAR(45)
)
BEGIN

	DECLARE EXIT HANDLER FOR SQLEXCEPTION, SQLWARNING
	BEGIN
		ROLLBACK;
		RESIGNAL;
	END;

START TRANSACTION;

	INSERT INTO user(user_id, first_name, last_name, user_name, password, user_type)
		VALUES(NULL, i_first_name, i_last_name, i_user_name, i_password, "student");
	
    INSERT INTO student(student_id, birth_date, e_mail)
		VALUES(LAST_INSERT_ID(), i_birth_date, i_e_mail);
    
COMMIT;
END$$

DELIMITER ;



-- -----------------------------------------------------
-- procedure User_Teacher_Add
-- -----------------------------------------------------

DELIMITER $$
USE `learndeck`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `User_Teacher_Add`(
    IN i_first_name varchar(45),
    IN i_last_name varchar(45),
    IN i_user_name varchar(45),
    IN i_password varchar(45)
)
BEGIN
	DECLARE EXIT HANDLER FOR SQLEXCEPTION, SQLWARNING
	BEGIN
		ROLLBACK;
		RESIGNAL;
	END;

START TRANSACTION;

	INSERT INTO user(user_id, first_name, last_name, user_name, password, user_type)
		VALUES(NULL, i_first_name, i_last_name, i_user_name, i_password, "teacher");
	
    INSERT INTO teacher(teacher_id)
		VALUES(LAST_INSERT_ID());
    
COMMIT;
END$$

DELIMITER ;
USE `learndeck`;

DELIMITER $$
USE `learndeck`$$
CREATE
DEFINER=`root`@`localhost`
TRIGGER `learndeck`.`card_BEFORE_UPDATE`
BEFORE UPDATE ON `learndeck`.`card`
FOR EACH ROW
BEGIN

	IF(NEW.general_difficulty > 2.0) THEN
			SET NEW.general_difficulty = 2.0;
    ELSEIF(NEW.general_difficulty < 0.1) THEN
			SET NEW.general_difficulty = 0.1;
	END IF;
    
END$$


DELIMITER ;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
