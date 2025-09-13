-- Table for Users
CREATE TABLE users (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(100) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       full_name VARCHAR(255),
                       email VARCHAR(255) UNIQUE,
                       phone VARCHAR(20),
                       city VARCHAR(100),
                       role VARCHAR(50)   -- e.g. "USER", "ADMIN"
);

-- Table for Categories (e.g., Dogs, Cats, etc.)
CREATE TABLE categories (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL UNIQUE
);

-- Table for Animals
CREATE TABLE animals (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         category_id INT,                -- Foreign key to categories table
                         breed VARCHAR(100),             -- You can later decide to use a separate 'breeds' table
                         color VARCHAR(50),
                         age INT,
                         size VARCHAR(50),
                         description_short VARCHAR(255),
                         description_long TEXT,
                         image VARCHAR(255),             -- URL or path to the image
                         owner_id INT,                   -- Foreign key to users table representing the owner
                         is_adopted BOOLEAN DEFAULT FALSE,
                         CONSTRAINT fk_animals_category FOREIGN KEY (category_id)
                             REFERENCES categories(id),
                         CONSTRAINT fk_animals_owner FOREIGN KEY (owner_id)
                             REFERENCES users(id)
);

-- Table for Adoption Requests
CREATE TABLE adoption_requests (
                                   id INT AUTO_INCREMENT PRIMARY KEY,
                                   animal_id INT,                  -- Foreign key to animals table
                                   requester_id INT,               -- Foreign key to users table representing the candidate
                                   message TEXT,
                                   status ENUM('pending', 'approved', 'rejected', 'cancelled') NOT NULL DEFAULT 'pending',
                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   decision_at TIMESTAMP NULL,
                                   CONSTRAINT fk_request_animal FOREIGN KEY (animal_id)
                                       REFERENCES animals(id),
                                   CONSTRAINT fk_request_requester FOREIGN KEY (requester_id)
                                       REFERENCES users(id)
);
