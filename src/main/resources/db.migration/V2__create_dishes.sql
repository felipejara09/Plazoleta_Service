CREATE TABLE dishes (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    price INT NOT NULL,
    description VARCHAR(255) NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    category VARCHAR(255) NOT NULL,
    restaurant_id INT NOT NULL REFERENCES restaurants(id),
    active BOOLEAN NOT NULL DEFAULT TRUE
);
