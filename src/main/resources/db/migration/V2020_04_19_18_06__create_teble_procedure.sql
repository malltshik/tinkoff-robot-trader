CREATE TABLE IF NOT EXISTS candle
(
    id            bigserial                not null,
    figi          text                     not null,
    date_time     timestamp with time zone not null,
    interval      text                     not null,
    open_price    numeric                  not null,
    closing_price numeric                  not null,
    highest_price numeric                  not null,
    lowest_price  numeric                  not null,
    trading_value numeric                  not null
) PARTITION BY LIST (figi);



CREATE OR REPLACE FUNCTION create_candle_partition(_figi text, _date timestamp with time zone)
    RETURNS void AS
$func$
DECLARE
    figi_part           text := 'candle_' || _figi;
    figi_timestamp_part text := figi_part || '_' || to_char(_date, 'yyyymmdd');
    start_day           text := to_char(_date, 'yyyy-mm-dd 00:00:00.000001');
    end_day             text := to_char(_date, 'yyyy-mm-dd 23:59:59.999999');
BEGIN
    EXECUTE format('CREATE TABLE IF NOT EXISTS ' ||
                   '%s PARTITION OF candle ' ||
                   'FOR VALUES IN (%L) ' ||
                   'PARTITION BY RANGE (date_time);',
                   figi_part, _figi);

    EXECUTE format('CREATE INDEX ON %s (figi);', figi_part);

    EXECUTE format('CREATE TABLE IF NOT EXISTS ' ||
                   '%s PARTITION OF %s ' ||
                   'FOR VALUES FROM (%L) TO (%L);',
                   figi_timestamp_part, figi_part, start_day, end_day);

    EXECUTE format('CREATE INDEX ON %s (date_time);', figi_timestamp_part);
END
$func$ LANGUAGE plpgsql;