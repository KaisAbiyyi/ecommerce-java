# E-Commerce Java Application

An open-source e-commerce platform built with JavaFX, providing a user-friendly interface for buyers, sellers, and admins. This application allows users to manage products, orders, and seller applications efficiently. 


---

## Table of Contents

- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Technologies Used](#technologies-used)
- [Contributing](#contributing)
- [License](#license)

---

## Features

- **Admin Role**: Manage users and seller applications.
- **Seller Role**: Manage products and orders.
- **Customer Role**: Browse products, add items to cart, and complete purchases.
- Secure login and authentication for all roles.
- Dynamic dashboards based on user roles.

---

## Installation

1.  **Clone the repository**;
    ```bash
    git clone https://github.com/KaisAbiyyi/ecommerce-java.git
    ```
2. **Navigate to the project directory:**
    ``bash
    cd ecommerce-java
    ```
3. **Build and run the application using Maven:**
    ```bash
    mvn clean javafx:run
    ```

---

## Usage

1.  **Login** as an `admin`, `customer`, or `seller` using default credentials
    - **Admin:** `username: admin`, `passwword: adminpass`
    - **Customer:** `username: customer`, `passwword: customerpass`
    - **Seller:** `username: seller`, `passwword: sellerpass`
2.  **Navigate** based on role:
    - Admins can approve seller aplications and manage users.
    - Seller can add, update, and view product listings,
    - Customers can browse products and complete orders.

---

## Technologies Used
- **Java**: Core programming language
- **JavaFX**: For building the graphical user interface
- **Maven**: For project build and dependency management

---

## License
This project is license under the GNU General Public License. See the [License](https://github.com/KaisAbiyyi/ecommerce-java/blob/main/LICENSE) file for more details.