const { faker } = require('@faker-js/faker');

function escapeSqlString(value) {
    return value.replace(/'/g, "''");
}

const N = 100; // Number of customers
const M = 10_000; // Number of orders
const P = 10_000; // Number of products

// Generate customers
for (let i = 1; i <= N; i++) {
    const name = escapeSqlString(faker.name.fullName());
    console.log(`INSERT INTO customer (id, name) VALUES (${i}, '${name}');`);
}

// Generate orders
for (let i = 1; i <= M; i++) {
    const customerId = Math.ceil(Math.random() * N);
    const description = escapeSqlString(faker.commerce.productName());
    console.log(`INSERT INTO "order" (id, description, customer_id) VALUES (${i}, '${description}', ${customerId});`);
}

// Generate products
for (let i = 1; i <= P; i++) {
    const description = escapeSqlString(faker.commerce.productName());
    console.log(`INSERT INTO "product" (id, description) VALUES (${i}, '${description}');`);
}