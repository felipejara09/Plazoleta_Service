CREATE TABLE restaurants (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    nit VARCHAR(20) NOT NULL,
    address VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    url_logo VARCHAR(255) NOT NULL,
    owner_id INTEGER NOT NULL
);
