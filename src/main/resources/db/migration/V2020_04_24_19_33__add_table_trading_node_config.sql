CREATE TABLE trader_config
(
    id   bigserial not null,
    figi text      not null
);
CREATE INDEX ON trader_config (figi);