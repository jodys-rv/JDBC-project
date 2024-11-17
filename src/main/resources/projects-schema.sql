DROP TABLE IF EXISTS project_category;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS step;
DROP TABLE IF EXISTS material;
DROP TABLE IF EXISTS project;

CREATE TABLE project(
	project_id INT NOT NULL AUTO_INCREMENT,
	project_name VARCHAR(128) NOT NULL,
	estimated_hours DECIMAL(7,2),
	actual_hours DECIMAL(7,2),
	difficulty INT,
	notes TEXT,
	PRIMARY KEY (project_id)
);

CREATE TABLE material(
	material_id INT NOT NULL AUTO_INCREMENT,
	project_id INT NOT NULL,
	material_name VARCHAR(128),
	num_required INT,
	cost DECIMAL(7,2),
	PRIMARY KEY (material_id),
	FOREIGN KEY (project_id) REFERENCES project (project_id) ON DELETE CASCADE
);

CREATE TABLE step(
	step_id INT NOT NULL AUTO_INCREMENT,
	project_id INT NOT NULL,
	step_text TEXT,
	step_order INT NOT NULL,
	PRIMARY KEY (step_id),
	FOREIGN KEY (project_id) REFERENCES project (project_id) ON DELETE CASCADE
);

CREATE TABLE category(
	category_id INT NOT NULL AUTO_INCREMENT,
	category_name VARCHAR(128),
	PRIMARY KEY (category_id)
);

CREATE TABLE project_category(
	project_id INT NOT NULL,
	category_id INT NOT NULL,
	FOREIGN KEY (project_id) REFERENCES project (project_id) ON DELETE CASCADE,
	FOREIGN KEY (category_id) REFERENCES category (category_id) ON DELETE CASCADE,
	UNIQUE KEY (project_id, category_id)
);

INSERT INTO project (project_name, estimated_hours, actual_hours, difficulty, notes) VALUES ("knit a stripey hat", 4, 6, 1, "use leftover yarn from your stash");
INSERT INTO material (project_id, material_name, num_required, cost) VALUES (1, "ball of yarn", 1, 3.99);
INSERT INTO material (project_id, material_name, num_required, cost) VALUES (1, "leftover yarn", 4, 0);
INSERT INTO step (project_id, step_text, step_order) VALUES (1, "knit a gauge swatch", 1);
INSERT INTO step (project_id, step_text, step_order) VALUES (1, "cast on about 60 stitches on your circular needles, holding your ball and one of the leftover yarns together", 2);
INSERT INTO step (project_id, step_text, step_order) VALUES (1, "knit with the two strands together until you run out or get bored", 3);
INSERT INTO step (project_id, step_text, step_order) VALUES (1, "pick up another leftover yarn and keep going", 4);
INSERT INTO step (project_id, step_text, step_order) VALUES (1, "oh, and don't forget to start decreasing your stitches after 5 or 6 inches", 5);
INSERT INTO category (category_id, category_name) VALUES (1, "fiber arts");
INSERT INTO project_category (project_id, category_id) VALUES (1, 1);

INSERT INTO project (project_name, estimated_hours, actual_hours, difficulty, notes) VALUES ("plant a tree", 2, 3, 2, "find a sunny spot sheltered from the wind");
INSERT INTO material (project_id, material_name, num_required, cost) VALUES (2, "sapling", 1, 50);
INSERT INTO material (project_id, material_name, num_required, cost) VALUES (2, "water", 5, .2);
INSERT INTO material (project_id, material_name, num_required, cost) VALUES (2, "bottle of tree fertilizer", 1, 9.50);
INSERT INTO step (project_id, step_text, step_order) VALUES (2, "dig a big hole", 1);
INSERT INTO step (project_id, step_text, step_order) VALUES (2, "put your sapling in the hole", 2);
INSERT INTO step (project_id, step_text, step_order) VALUES (2, "fill in the hole around the sapling", 3);
INSERT INTO step (project_id, step_text, step_order) VALUES (2, "mix the fertilizer with five gallons of water", 4);
INSERT INTO step (project_id, step_text, step_order) VALUES (2, "water your new tree every day", 5);
INSERT INTO category (category_id, category_name) VALUES (2, "garden improvement");
INSERT INTO project_category (project_id, category_id) VALUES (2, 2);

INSERT INTO project (project_name, estimated_hours, actual_hours, difficulty, notes) VALUES ("build a birdhouse", 5, 4, 2, "birds not included");
INSERT INTO material (project_id, material_name, num_required, cost) VALUES (3, "footlong wood boards", 3, 1.99);
INSERT INTO material (project_id, material_name, num_required, cost) VALUES (3, "wood glue", 1, 3.75);
INSERT INTO material (project_id, material_name, num_required, cost) VALUES (3, "outdoor paint", 1, 3.50);
INSERT INTO step (project_id, step_text, step_order) VALUES (3, "cut all wooden shapes according to pattern", 1);
INSERT INTO step (project_id, step_text, step_order) VALUES (3, "glue the pieces together", 2);
INSERT INTO step (project_id, step_text, step_order) VALUES (3, "let the glue cure overnight", 3);
INSERT INTO step (project_id, step_text, step_order) VALUES (3, "hang birdhouse from your favorite tree", 4);
INSERT INTO category (category_id, category_name) VALUES (3, "carpentry");
INSERT INTO project_category (project_id, category_id) VALUES (3, 2);
INSERT INTO project_category (project_id, category_id) VALUES (3, 3);



