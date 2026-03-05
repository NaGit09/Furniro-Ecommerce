CREATE TABLE verificationToken
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    token       VARCHAR(255)          NULL,
    userid      INT                   NOT NULL,
    expiry_date datetime              NULL,
    CONSTRAINT pk_verificationtoken PRIMARY KEY (id)
);

ALTER TABLE verificationToken
    ADD CONSTRAINT uc_verificationtoken_userid UNIQUE (userid);

ALTER TABLE verificationToken
    ADD CONSTRAINT FK_VERIFICATIONTOKEN_ON_USERID FOREIGN KEY (userid) REFERENCES User (userid);