use game;

CREATE TABLE `user`(
    ID int AUTO_INCREMENT PRIMARY KEY,
    `username` varchar(255) UNIQUE,
    `password` varchar(255),
    nickname varchar(255),
    avatar varchar(255),
    numberOfGame int DEFAULT 0,
    numberOfWin int DEFAULT 0,
    numberOfDraw int DEFAULT 0,
    IsOnline int DEFAULT 0,
    IsPlaying int DEFAULT 0
);

-- Thêm cột score vào bảng user
ALTER TABLE `user` 
ADD COLUMN score INT DEFAULT 0;


CREATE TABLE friend(
    ID_User1 int NOT NULL,
    ID_User2 int NOT NULL,
    FOREIGN KEY (ID_User1) REFERENCES `user`(ID),
    FOREIGN KEY (ID_User2) REFERENCES `user`(ID),
    CONSTRAINT PK_friend PRIMARY KEY (ID_User1,ID_User2)
);

-- Bảng lưu thông tin trận đấu
CREATE TABLE match_history (
    match_id INT AUTO_INCREMENT PRIMARY KEY,
    player1_id INT NOT NULL,
    player2_id INT NOT NULL,
    winner_id INT,
    start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP,
    match_score_p1 INT DEFAULT 0,
    match_score_p2 INT DEFAULT 0,
    final_score_p1 INT DEFAULT 0,
    final_score_p2 INT DEFAULT 0,
    is_disconnected BOOLEAN DEFAULT FALSE,
    disconnected_player_id INT,
    FOREIGN KEY (player1_id) REFERENCES user(ID),
    FOREIGN KEY (player2_id) REFERENCES user(ID),
    FOREIGN KEY (winner_id) REFERENCES user(ID),
    FOREIGN KEY (disconnected_player_id) REFERENCES user(ID)
);

-- Bảng lưu thông tin về các rounds trong trận đấu
CREATE TABLE match_rounds (
    round_id INT AUTO_INCREMENT PRIMARY KEY,
    match_id INT NOT NULL,
    round_number INT NOT NULL,
    time_limit INT NOT NULL,
    questions_per_round INT NOT NULL,
    FOREIGN KEY (match_id) REFERENCES match_history(match_id)
);

-- Bảng lưu thông tin chi tiết từng câu hỏi
CREATE TABLE match_questions (
    question_id INT AUTO_INCREMENT PRIMARY KEY,
    match_id INT NOT NULL,
    round_id INT NOT NULL,
    question_number INT NOT NULL,
    target_number INT NOT NULL,
    operations VARCHAR(255) NOT NULL,
    FOREIGN KEY (match_id) REFERENCES match_history(match_id),
    FOREIGN KEY (round_id) REFERENCES match_rounds(round_id)
);

-- Bảng lưu câu trả lời của người chơi
CREATE TABLE player_answers (
    answer_id INT AUTO_INCREMENT PRIMARY KEY,
    question_id INT NOT NULL,
    player_id INT NOT NULL,
    is_correct BOOLEAN NOT NULL,
    answer_time BIGINT NOT NULL,
    points_earned INT NOT NULL,
    FOREIGN KEY (question_id) REFERENCES match_questions(question_id),
    FOREIGN KEY (player_id) REFERENCES user(ID)
);








DELIMITER //

CREATE TRIGGER after_match_update 
AFTER UPDATE ON match_history
FOR EACH ROW
BEGIN
    -- Nếu là record mới (chưa có winner_id cũ) thì cập nhật numberOfGame
    IF OLD.winner_id IS NULL AND NEW.winner_id IS NULL THEN
        UPDATE user 
        SET numberOfGame = numberOfGame + 1
        WHERE ID IN (NEW.player1_id, NEW.player2_id);
    END IF;
    
    -- Nếu trận đấu vừa kết thúc (có winner_id mới)
    IF NEW.winner_id IS NOT NULL THEN
        -- Cập nhật số trận thắng cho người thắng
        UPDATE user 
        SET 
            numberOfWin = numberOfWin + 1,
            score = score + CASE 
                -- Người chơi thắng do đối thủ disconnect
                WHEN NEW.is_disconnected = TRUE THEN 40
                -- Người chơi thắng bình thường
                ELSE NEW.final_score_p1 * (NEW.player1_id = NEW.winner_id) + 
                     NEW.final_score_p2 * (NEW.player2_id = NEW.winner_id)
            END
        WHERE ID = NEW.winner_id;
        
        -- Cập nhật điểm cho người thua (nếu không disconnect)
        IF NEW.is_disconnected = FALSE THEN
            UPDATE user 
            SET score = score + CASE 
                    WHEN ID = NEW.player1_id THEN NEW.final_score_p1
                    ELSE NEW.final_score_p2
                END
            WHERE ID IN (NEW.player1_id, NEW.player2_id) 
                AND ID != NEW.winner_id;
        END IF;
        
        -- Nếu có hòa (điểm bằng nhau)
        IF NEW.match_score_p1 = NEW.match_score_p2 AND NEW.final_score_p1 = NEW.final_score_p2 THEN
            UPDATE user 
            SET numberOfDraw = numberOfDraw + 1,
                score = score + NEW.final_score_p1
            WHERE ID IN (NEW.player1_id, NEW.player2_id);
        END IF;
    END IF;
END//

DELIMITER ;


select * from user;
update user set IsOnline = 0 where ID in (1)

