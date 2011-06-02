# Fragments schema
 
# --- !Ups
 
CREATE TABLE Fragment (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    title varchar(255) NOT NULL,
    body text NOT NULL,
    style varchar(255) NOT NULL,
    created_at datetime NOT NULL,
    PRIMARY KEY (id)
);
 
# --- !Downs
 
DROP TABLE Fragment;
