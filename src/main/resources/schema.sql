CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL UNIQUE,
    CONSTRAINT pk_user PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS requests (
                                        id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
                                        description VARCHAR(512) NOT NULL,
                                        created TIMESTAMP NOT NULL,
                                        creator BIGINT NOT NULL REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    owner BIGINT NOT NULL REFERENCES users (id),
    name VARCHAR(255) NOT NULL,
    description VARCHAR(512) NOT NULL,
    available BOOLEAN NOT NULL,
    request_id BIGINT REFERENCES requests (id)
);

CREATE TABLE IF NOT EXISTS booking (
   id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
   item_id BIGINT NOT NULL REFERENCES items (id),
   booker_id BIGINT NOT NULL REFERENCES users (id),
   start_booking TIMESTAMP NOT NULL,
   end_booking TIMESTAMP NOT NULL,
   status VARCHAR(512) NOT NULL
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    text VARCHAR(512) NOT NULL,
    item_id BIGINT NOT NULL REFERENCES items (id),
    author_id BIGINT NOT NULL REFERENCES users (id),
    created TIMESTAMP NOT NULL
);
