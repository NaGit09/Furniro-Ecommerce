CREATE DATABASE IF NOT EXISTS furniro_db;
USE furniro_db;

-- 1. USER & AUTH MODULE

CREATE TABLE IF NOT EXISTS Account (
    AccountID       INT AUTO_INCREMENT PRIMARY KEY,
    UserName        VARCHAR(50)  UNIQUE NOT NULL,
    Email           VARCHAR(150) UNIQUE NOT NULL,
    Phone           VARCHAR(20),
    PasswordHash    VARCHAR(255),
    ProviderID      VARCHAR(255),

    LoginType ENUM('NORMAL','GOOGLE','FACEBOOK') DEFAULT 'NORMAL',
    Role      ENUM('CUSTOMER','STAFF','ADMIN')   DEFAULT 'CUSTOMER',

    Active BOOLEAN DEFAULT FALSE,
    Banned BOOLEAN DEFAULT FALSE,

    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE IF NOT EXISTS User (
    UserID      INT AUTO_INCREMENT PRIMARY KEY,
    AccountID   INT NOT NULL UNIQUE,

    FirstName   NVARCHAR(100),
    LastName    NVARCHAR(100),
    Gender      ENUM('MALE','FEMALE','OTHER'),

    Avatar      VARCHAR(255) DEFAULT 'default_avatar.png',
    DateOfBirth DATE,

    FOREIGN KEY (AccountID)
        REFERENCES Account(AccountID)
        ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS Address (
    AddressID       INT AUTO_INCREMENT PRIMARY KEY,
    UserID          INT NOT NULL,

    ReceiverName    NVARCHAR(150),
    ReceiverPhone   VARCHAR(20),

    Province        NVARCHAR(100),
    District        NVARCHAR(100),
    Ward            NVARCHAR(100),
    Street          NVARCHAR(255),

    IsDefault   BOOLEAN DEFAULT FALSE,
    AddressType ENUM('HOME','OFFICE') DEFAULT 'HOME',

    FOREIGN KEY (UserID)
        REFERENCES User(UserID)
        ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS VerificationToken (
    Id          INT AUTO_INCREMENT PRIMARY KEY,
    Token       VARCHAR(255) UNIQUE NOT NULL,
    AccountID   INT NOT NULL,

    FOREIGN KEY (AccountID)
        REFERENCES Account(AccountID)
        ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS ExistingTokens (
    Id          INT AUTO_INCREMENT PRIMARY KEY,
    Token       VARCHAR(500) UNIQUE NOT NULL,
    TokenType   VARCHAR(20) DEFAULT 'REFRESH',
    ExpireDate  DATETIME NOT NULL,
    AccountID   INT NOT NULL,

    FOREIGN KEY (AccountID)
        REFERENCES Account(AccountID)
        ON DELETE CASCADE
);

CREATE INDEX idx_token_value
    ON ExistingTokens(Token);

-- 2. PRODUCT MODULE

CREATE TABLE IF NOT EXISTS Category (
    CategoryID       INT AUTO_INCREMENT PRIMARY KEY,
    CategoryName     NVARCHAR(150) NOT NULL,
    ParentCategoryID INT,

    FOREIGN KEY (ParentCategoryID)
        REFERENCES Category(CategoryID)
);


CREATE TABLE IF NOT EXISTS Product (
    ProductID   INT AUTO_INCREMENT PRIMARY KEY,
    CategoryID  INT NOT NULL,

    Name        NVARCHAR(255) NOT NULL,
    Description TEXT,

    BasePrice   INT NOT NULL,
    Brand       NVARCHAR(255),

    Status ENUM('ACTIVE','INACTIVE','OUT_OF_STOCK') DEFAULT 'ACTIVE',

    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (CategoryID)
        REFERENCES Category(CategoryID)
);


CREATE TABLE IF NOT EXISTS ColorMaster (
    ColorID     INT AUTO_INCREMENT PRIMARY KEY,
    ColorName   NVARCHAR(100) NOT NULL
);


CREATE TABLE IF NOT EXISTS SizeMaster (
    SizeID      INT AUTO_INCREMENT PRIMARY KEY,
    SizeName    NVARCHAR(50) NOT NULL
);


CREATE TABLE IF NOT EXISTS ProductVariant (
    VariantID       INT AUTO_INCREMENT PRIMARY KEY,
    ProductID       INT NOT NULL,

    ColorID         INT,
    SizeID          INT,

    SKU             VARCHAR(100) UNIQUE,
    Price           INT NOT NULL,
    StockQuantity   INT DEFAULT 0,

    FOREIGN KEY (ProductID) REFERENCES Product(ProductID) ON DELETE CASCADE,
    FOREIGN KEY (ColorID)   REFERENCES ColorMaster(ColorID),
    FOREIGN KEY (SizeID)    REFERENCES SizeMaster(SizeID)
);


CREATE TABLE IF NOT EXISTS ProductImage (
    ImageID     INT AUTO_INCREMENT PRIMARY KEY,
    ProductID   INT NOT NULL,

    Url         VARCHAR(255) NOT NULL,
    SortOrder   TINYINT DEFAULT 0,

    IsThumbnail BOOLEAN GENERATED ALWAYS AS (SortOrder = 0) VIRTUAL,

    FOREIGN KEY (ProductID)
        REFERENCES Product(ProductID)
        ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS ProductSpecification (
    SpecID      INT AUTO_INCREMENT PRIMARY KEY,
    ProductID   INT NOT NULL UNIQUE,

    Width       INT,
    Height      INT,
    Depth       INT,
    Weight      INT,

    Material        NVARCHAR(255),
    Configuration   NVARCHAR(255),

    FOREIGN KEY (ProductID)
        REFERENCES Product(ProductID)
        ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS ProductLicense (
    LicenseID   INT AUTO_INCREMENT PRIMARY KEY,
    ProductID   INT NOT NULL,

    LicenseName NVARCHAR(255) NOT NULL,
    LicenseType ENUM('ORIGIN','QUALITY','ENVIRONMENT','OTHER'),

    DocumentUrl VARCHAR(255),
    IssueDate   DATE,
    ExpiryDate  DATE,

    FOREIGN KEY (ProductID)
        REFERENCES Product(ProductID)
        ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS Warranty (
    WarrantyID  INT AUTO_INCREMENT PRIMARY KEY,
    ProductID   INT NOT NULL UNIQUE,

    Type        NVARCHAR(100),
    Duration    NVARCHAR(100),
    Summary     TEXT,

    FOREIGN KEY (ProductID)
        REFERENCES Product(ProductID)
        ON DELETE CASCADE
);

-- 3. CART & ORDER MODULE

CREATE TABLE IF NOT EXISTS Cart (
    CartID      INT AUTO_INCREMENT PRIMARY KEY,
    UserID      INT NOT NULL UNIQUE,

    UpdatedAt   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (UserID)
        REFERENCES User(UserID)
        ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS CartItem (
    CartItemID  INT AUTO_INCREMENT PRIMARY KEY,
    CartID      INT NOT NULL,
    VariantID   INT NOT NULL,

    Quantity INT NOT NULL CHECK (Quantity > 0),

    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (CartID)    REFERENCES Cart(CartID) ON DELETE CASCADE,
    FOREIGN KEY (VariantID) REFERENCES ProductVariant(VariantID) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS OrderTable (
    OrderID     INT AUTO_INCREMENT PRIMARY KEY,
    UserID      INT NOT NULL,
    AddressID   INT NOT NULL,

    Status ENUM(
        'PENDING',
        'CONFIRMED',
        'SHIPPING',
        'DELIVERED',
        'CANCELLED',
        'REFUNDED'
    ) DEFAULT 'PENDING',

    TotalAmount INT NOT NULL,
    ShippingFee INT DEFAULT 0,

    OrderNote TEXT,

    OrderedAt   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PaidAt      TIMESTAMP NULL,
    CompletedAt TIMESTAMP NULL,

    FOREIGN KEY (UserID) REFERENCES User(UserID),
    FOREIGN KEY (AddressID) REFERENCES Address(AddressID)
);


CREATE TABLE IF NOT EXISTS OrderItem (
    OrderItemID     INT AUTO_INCREMENT PRIMARY KEY,
    OrderID         INT NOT NULL,
    VariantID       INT NOT NULL,

    Quantity        INT NOT NULL,
    PriceAtPurchase INT NOT NULL,

    FOREIGN KEY (OrderID)
        REFERENCES OrderTable(OrderID)
        ON DELETE CASCADE,

    FOREIGN KEY (VariantID)
        REFERENCES ProductVariant(VariantID)
);

CREATE TABLE IF NOT EXISTS Payment (
    PaymentID       INT AUTO_INCREMENT PRIMARY KEY,
    OrderID         INT NOT NULL,

    PaymentMethod ENUM('COD','PAYPAL','VNPAY','MOMO') NOT NULL,

    PaymentStatus ENUM(
        'PENDING',
        'SUCCESS',
        'FAILED',
        'REFUNDED'
    ) DEFAULT 'PENDING',

    TransactionID   VARCHAR(100),
    Amount          INT NOT NULL,
    Currency        VARCHAR(10) DEFAULT 'VND',

    ProviderResponse TEXT,

    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PaidAt    TIMESTAMP NULL
);

CREATE INDEX idx_payment_order
    ON Payment(OrderID);

-- 4. REVIEW

CREATE TABLE IF NOT EXISTS Review (
    ReviewID    INT AUTO_INCREMENT PRIMARY KEY,
    ProductID   INT NOT NULL,
    UserID      INT NOT NULL,

    Rating TINYINT CHECK (Rating BETWEEN 1 AND 5),
    Comment TEXT,

    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (ProductID) REFERENCES Product(ProductID) ON DELETE CASCADE,
    FOREIGN KEY (UserID) REFERENCES User(UserID) ON DELETE CASCADE
);

-- 5. WAREHOUSE & INVENTORY

CREATE TABLE IF NOT EXISTS Warehouse (
    WarehouseID INT AUTO_INCREMENT PRIMARY KEY,
    Name        NVARCHAR(150) NOT NULL,
    Address     NVARCHAR(255),
    IsActive    BOOLEAN DEFAULT TRUE
);


CREATE TABLE IF NOT EXISTS Inventory (
    InventoryID INT AUTO_INCREMENT PRIMARY KEY,
    VariantID   INT NOT NULL,
    WarehouseID INT NOT NULL,

    Quantity INT NOT NULL DEFAULT 0,
    ReservedQuantity INT DEFAULT 0,

    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
               ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (VariantID)
        REFERENCES ProductVariant(VariantID)
        ON DELETE CASCADE,

    FOREIGN KEY (WarehouseID)
        REFERENCES Warehouse(WarehouseID)
);


CREATE TABLE IF NOT EXISTS StockMovement (
    MovementID  INT AUTO_INCREMENT PRIMARY KEY,
    VariantID   INT NOT NULL,
    WarehouseID INT NOT NULL,

    Quantity INT NOT NULL,

    Type ENUM('IMPORT','EXPORT','ADJUST','RETURN') NOT NULL,

    ReferenceID INT,
    Note TEXT,

    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CreatedBy INT,

    FOREIGN KEY (VariantID) REFERENCES ProductVariant(VariantID),
    FOREIGN KEY (WarehouseID) REFERENCES Warehouse(WarehouseID)
);


CREATE TABLE IF NOT EXISTS StockReservation (
    ReserveID INT AUTO_INCREMENT PRIMARY KEY,

    VariantID INT NOT NULL,
    OrderID   INT NOT NULL,

    Quantity INT NOT NULL,
    ExpiresAt TIMESTAMP NOT NULL,

    Status ENUM(
        'ACTIVE',
        'EXPIRED',
        'COMPLETED',
        'CANCELLED'
    ) DEFAULT 'ACTIVE',

    FOREIGN KEY (VariantID) REFERENCES ProductVariant(VariantID),
    FOREIGN KEY (OrderID) REFERENCES OrderTable(OrderID) ON DELETE CASCADE
);