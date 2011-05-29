# Tag schema
 
# --- !Ups
 
CREATE TABLE Tag (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    name varchar(255) NOT NULL,
    created_at datetime NOT NULL,
    PRIMARY KEY (id)
);
 
# --- !Downs
 
DROP TABLE Tag;
