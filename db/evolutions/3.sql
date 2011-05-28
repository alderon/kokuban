# FragmentTag schema
 
# --- !Ups
 
CREATE TABLE FragmentTag (
    fragment_id bigint(20) NOT NULL,
    tag_id bigint(20) NOT NULL,
    created_at datetime NOT NULL,
    updated_at datetime NOT NULL,
    PRIMARY KEY (fragment_id, tag_id)
);
 
# --- !Downs
 
DROP TABLE FragmentTag IF EXISTS;
