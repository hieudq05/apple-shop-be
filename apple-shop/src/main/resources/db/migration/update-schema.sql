CREATE TABLE blog
(
    id           int IDENTITY (1, 1) NOT NULL,
    title        nvarchar(255)       NOT NULL,
    content      nvarchar(MAX)       NOT NULL,
    thumbnail    nvarchar(255),
    author_id    int,
    published_at datetime,
    created_at   datetime
        CONSTRAINT DF_blog_created_at DEFAULT GETDATE(),
    updated_at   datetime,
    status       nvarchar(50),
    CONSTRAINT pk_blog PRIMARY KEY (id)
)
GO

CREATE TABLE cart_item
(
    id           int IDENTITY (1, 1) NOT NULL,
    user_id      int                 NOT NULL,
    product_id   int                 NOT NULL,
    product_name nvarchar(255),
    stock_id     int                 NOT NULL,
    quantity     int                 NOT NULL,
    CONSTRAINT pk_cartitem PRIMARY KEY (id)
)
GO

CREATE TABLE category
(
    id    int IDENTITY (1, 1) NOT NULL,
    name  nvarchar(100)       NOT NULL,
    image nvarchar(255),
    CONSTRAINT pk_category PRIMARY KEY (id)
)
GO

CREATE TABLE color
(
    id         int IDENTITY (1, 1) NOT NULL,
    name       nvarchar(50)        NOT NULL,
    hex_code   varchar(7),
    product_id int,
    CONSTRAINT pk_color PRIMARY KEY (id)
)
GO

CREATE TABLE feature
(
    id          int IDENTITY (1, 1) NOT NULL,
    name        nvarchar(100)       NOT NULL,
    description nvarchar(500),
    image       nvarchar(255),
    created_at  datetime
        CONSTRAINT DF_feature_created_at DEFAULT GETDATE(),
    created_by  int                 NOT NULL,
    CONSTRAINT pk_feature PRIMARY KEY (id)
)
GO

CREATE TABLE instance_properties
(
    id         int IDENTITY (1, 1) NOT NULL,
    name       nvarchar(255)       NOT NULL,
    created_at datetime
        CONSTRAINT DF_instance_properties_created_at DEFAULT GETDATE(),
    created_by int                 NOT NULL,
    CONSTRAINT pk_instanceproperties PRIMARY KEY (id)
)
GO

CREATE TABLE [order]
(
    id           int IDENTITY (1, 1) NOT NULL,
    created_by   int                 NOT NULL,
    created_at   datetime
        CONSTRAINT DF_order_created_at DEFAULT GETDATE(),
    payment_type int                 NOT NULL,
    approve_at   datetime
        CONSTRAINT DF_order_approve_at DEFAULT GETDATE(),
    approve_by   int                 NOT NULL,
    first_name   nvarchar(55),
    last_name    nvarchar(55),
    email        nvarchar(255),
    phone        nvarchar(20),
    address      nvarchar(500),
    ward         nvarchar(100),
    district     nvarchar(100),
    province     nvarchar(100),
    country      nvarchar(100),
    status       int                 NOT NULL,
    CONSTRAINT pk_order PRIMARY KEY (id)
)
GO

CREATE TABLE order_detail
(
    id           int IDENTITY (1, 1) NOT NULL,
    order_id     int                 NOT NULL,
    product_id   int                 NOT NULL,
    product_name nvarchar(255)       NOT NULL,
    stock_id     int                 NOT NULL,
    quantity     int                 NOT NULL,
    price        decimal(18, 2)      NOT NULL,
    note         nvarchar(255),
    CONSTRAINT pk_orderdetail PRIMARY KEY (id)
)
GO

CREATE TABLE order_status
(
    id   int IDENTITY (1, 1) NOT NULL,
    name nvarchar(255),
    CONSTRAINT pk_orderstatus PRIMARY KEY (id)
)
GO

CREATE TABLE payment_type
(
    id   int IDENTITY (1, 1) NOT NULL,
    name nvarchar(255)       NOT NULL,
    CONSTRAINT pk_paymenttype PRIMARY KEY (id)
)
GO

CREATE TABLE product
(
    id          int IDENTITY (1, 1) NOT NULL,
    name        nvarchar(255)       NOT NULL,
    description nvarchar(MAX),
    created_at  datetime
        CONSTRAINT DF_product_created_at DEFAULT GETDATE(),
    created_by  int                 NOT NULL,
    updated_at  datetime
        CONSTRAINT DF_product_updated_at DEFAULT GETDATE(),
    updated_by  int                 NOT NULL,
    category_id int                 NOT NULL,
    CONSTRAINT pk_product PRIMARY KEY (id)
)
GO

CREATE TABLE product_feature
(
    feature_id int NOT NULL,
    product_id int NOT NULL,
    CONSTRAINT pk_productfeature PRIMARY KEY (feature_id, product_id)
)
GO

CREATE TABLE product_features
(
    product_id  int NOT NULL,
    features_id int NOT NULL,
    CONSTRAINT pk_product_features PRIMARY KEY (product_id, features_id)
)
GO

CREATE TABLE product_photos
(
    id         int IDENTITY (1, 1) NOT NULL,
    stock_id   int                 NOT NULL,
    image_url  nvarchar(255)       NOT NULL,
    is_default bit
        CONSTRAINT DF_product_photos_is_default DEFAULT 0,
    CONSTRAINT pk_productphotos PRIMARY KEY (id)
)
GO

CREATE TABLE product_promotions
(
    categories_id int NOT NULL,
    promotions_id int NOT NULL,
    CONSTRAINT pk_product_promotions PRIMARY KEY (categories_id, promotions_id)
)
GO

CREATE TABLE promotion_category
(
    category_id  int NOT NULL,
    promotion_id int NOT NULL,
    CONSTRAINT pk_promotioncategory PRIMARY KEY (category_id, promotion_id)
)
GO

CREATE TABLE promotion_type
(
    id   int IDENTITY (1, 1) NOT NULL,
    name nvarchar(155),
    CONSTRAINT pk_promotiontype PRIMARY KEY (id)
)
GO

CREATE TABLE promotions
(
    id                  int IDENTITY (1, 1) NOT NULL,
    name                nvarchar(255)       NOT NULL,
    code                nvarchar(50)        NOT NULL,
    promotion_type      int                 NOT NULL,
    value               decimal(18, 2)      NOT NULL,
    max_discount_amount decimal(18, 2),
    min_order_value     decimal(18, 2),
    usage_limit         int                 NOT NULL,
    usage_count         int
        CONSTRAINT DF_promotions_usage_count DEFAULT 0,
    is_active           bit
        CONSTRAINT DF_promotions_is_active DEFAULT 1,
    start_date          datetime            NOT NULL,
    end_date            datetime            NOT NULL,
    apply_on            bit                 NOT NULL,
    CONSTRAINT pk_promotions PRIMARY KEY (id)
)
GO

CREATE TABLE review
(
    id            int IDENTITY (1, 1) NOT NULL,
    user_id       int                 NOT NULL,
    product_id    int                 NOT NULL,
    content       nvarchar(1000)      NOT NULL,
    rating        int                 NOT NULL,
    created_at    datetime
        CONSTRAINT DF_review_created_at DEFAULT GETDATE(),
    is_approved   bit
        CONSTRAINT DF_review_is_approved DEFAULT 0,
    approved_by   int,
    approved_at   datetime
        CONSTRAINT DF_review_approved_at DEFAULT GETDATE(),
    reply_content nvarchar(1000)      NOT NULL,
    replied_by    int                 NOT NULL,
    CONSTRAINT pk_review PRIMARY KEY (id)
)
GO

CREATE TABLE role
(
    id   int IDENTITY (1, 1) NOT NULL,
    name nvarchar(50)        NOT NULL,
    CONSTRAINT pk_role PRIMARY KEY (id)
)
GO

CREATE TABLE saved_product
(
    product_id int NOT NULL,
    created_at datetime
        CONSTRAINT DF_saved_product_created_at DEFAULT GETDATE(),
    user_id    int NOT NULL,
    stock_id   int NOT NULL,
    CONSTRAINT pk_savedproduct PRIMARY KEY (user_id, stock_id)
)
GO

CREATE TABLE shipping_info
(
    id         int IDENTITY (1, 1) NOT NULL,
    user_id    int                 NOT NULL,
    first_name nvarchar(55),
    last_name  nvarchar(55),
    email      nvarchar(255),
    phone      nvarchar(20),
    address    nvarchar(500),
    ward       nvarchar(100),
    district   nvarchar(100),
    province   nvarchar(100),
    country    nvarchar(100),
    is_default bit
        CONSTRAINT DF_shipping_info_is_default DEFAULT 0,
    CONSTRAINT pk_shippinginfo PRIMARY KEY (id)
)
GO

CREATE TABLE stock
(
    id         int IDENTITY (1, 1)             NOT NULL,
    product_id int                             NOT NULL,
    color_id   int,
    quantity   int
        CONSTRAINT DF_stock_quantity DEFAULT 0 NOT NULL,
    CONSTRAINT pk_stock PRIMARY KEY (id)
)
GO

CREATE TABLE stock_instance
(
    instance_id int NOT NULL,
    stock_id    int NOT NULL,
    CONSTRAINT pk_stockinstance PRIMARY KEY (instance_id, stock_id)
)
GO

CREATE TABLE stock_instance_properties
(
    stock_id               int NOT NULL,
    instance_properties_id int NOT NULL,
    CONSTRAINT pk_stock_instanceproperties PRIMARY KEY (stock_id, instance_properties_id)
)
GO

CREATE TABLE [user]
(
    id            int IDENTITY (1, 1) NOT NULL,
    email         nvarchar(255)       NOT NULL,
    phone         nvarchar(20),
    password_hash nvarchar(255)       NOT NULL,
    first_name    nvarchar(50),
    last_name     nvarchar(50),
    address       nvarchar(500),
    ward          nvarchar(100),
    district      nvarchar(100),
    province      nvarchar(100),
    country       nvarchar(100),
    image         nvarchar(255),
    created_at    datetime
        CONSTRAINT DF_user_created_at DEFAULT GETDATE(),
    updated_at    datetime,
    role_id       int,
    is_active     bit
        CONSTRAINT DF_user_is_active DEFAULT 1,
    CONSTRAINT pk_user PRIMARY KEY (id)
)
GO

CREATE TABLE user_activity_log
(
    id                 bigint IDENTITY (1, 1) NOT NULL,
    user_id            int                    NOT NULL,
    log_time           datetime
        CONSTRAINT DF_user_activity_log_log_time DEFAULT GETDATE(),
    action_type        nvarchar(MAX)          NOT NULL,
    target_entity_type nvarchar(MAX),
    message            nvarchar(MAX),
    old_value          nvarchar(MAX),
    CONSTRAINT pk_useractivitylog PRIMARY KEY (id)
)
GO

ALTER TABLE blog
    ADD CONSTRAINT FK_BLOG_ON_AUTHORID FOREIGN KEY (author_id) REFERENCES [user] (id)
GO

ALTER TABLE cart_item
    ADD CONSTRAINT FK_CARTITEM_ON_PRODUCTID FOREIGN KEY (product_id) REFERENCES product (id)
GO

ALTER TABLE cart_item
    ADD CONSTRAINT FK_CARTITEM_ON_STOCKID FOREIGN KEY (stock_id) REFERENCES stock (id)
GO

ALTER TABLE cart_item
    ADD CONSTRAINT FK_CARTITEM_ON_USERID FOREIGN KEY (user_id) REFERENCES [user] (id)
GO

ALTER TABLE color
    ADD CONSTRAINT FK_COLOR_ON_PRODUCTID FOREIGN KEY (product_id) REFERENCES product (id)
GO

ALTER TABLE feature
    ADD CONSTRAINT FK_FEATURE_ON_CREATEDBY FOREIGN KEY (created_by) REFERENCES [user] (id)
GO

ALTER TABLE feature
    ADD CONSTRAINT FK_FEATURE_ON_ID FOREIGN KEY (id) REFERENCES [user] (id)
GO

ALTER TABLE instance_properties
    ADD CONSTRAINT FK_INSTANCEPROPERTIES_ON_CREATEDBY FOREIGN KEY (created_by) REFERENCES [user] (id)
GO

ALTER TABLE order_detail
    ADD CONSTRAINT FK_ORDERDETAIL_ON_ORDERID FOREIGN KEY (order_id) REFERENCES [order] (id) ON DELETE CASCADE
GO

ALTER TABLE order_detail
    ADD CONSTRAINT FK_ORDERDETAIL_ON_PRODUCTID FOREIGN KEY (product_id) REFERENCES product (id)
GO

ALTER TABLE order_detail
    ADD CONSTRAINT FK_ORDERDETAIL_ON_STOCKID FOREIGN KEY (stock_id) REFERENCES stock (id)
GO

ALTER TABLE [order]
    ADD CONSTRAINT FK_ORDER_ON_APPROVEBY FOREIGN KEY (approve_by) REFERENCES [user] (id)
GO

ALTER TABLE [order]
    ADD CONSTRAINT FK_ORDER_ON_CREATEDBY FOREIGN KEY (created_by) REFERENCES [user] (id)
GO

ALTER TABLE [order]
    ADD CONSTRAINT FK_ORDER_ON_PAYMENTTYPE FOREIGN KEY (payment_type) REFERENCES payment_type (id)
GO

ALTER TABLE [order]
    ADD CONSTRAINT FK_ORDER_ON_STATUS FOREIGN KEY (status) REFERENCES order_status (id)
GO

ALTER TABLE product_photos
    ADD CONSTRAINT FK_PRODUCTPHOTOS_ON_STOCKID FOREIGN KEY (stock_id) REFERENCES stock (id)
GO

ALTER TABLE product
    ADD CONSTRAINT FK_PRODUCT_ON_CATEGORYID FOREIGN KEY (category_id) REFERENCES category (id)
GO

ALTER TABLE product
    ADD CONSTRAINT FK_PRODUCT_ON_CREATEDBY FOREIGN KEY (created_by) REFERENCES [user] (id)
GO

ALTER TABLE product
    ADD CONSTRAINT FK_PRODUCT_ON_UPDATEDBY FOREIGN KEY (updated_by) REFERENCES [user] (id)
GO

ALTER TABLE promotions
    ADD CONSTRAINT FK_PROMOTIONS_ON_PROMOTIONTYPE FOREIGN KEY (promotion_type) REFERENCES promotion_type (id)
GO

ALTER TABLE review
    ADD CONSTRAINT FK_REVIEW_ON_APPROVEDBY FOREIGN KEY (approved_by) REFERENCES [user] (id)
GO

ALTER TABLE review
    ADD CONSTRAINT FK_REVIEW_ON_PRODUCTID FOREIGN KEY (product_id) REFERENCES product (id)
GO

ALTER TABLE review
    ADD CONSTRAINT FK_REVIEW_ON_REPLIEDBY FOREIGN KEY (replied_by) REFERENCES [user] (id)
GO

ALTER TABLE review
    ADD CONSTRAINT FK_REVIEW_ON_USERID FOREIGN KEY (user_id) REFERENCES [user] (id)
GO

ALTER TABLE saved_product
    ADD CONSTRAINT FK_SAVEDPRODUCT_ON_PRODUCTID FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE CASCADE
GO

ALTER TABLE saved_product
    ADD CONSTRAINT FK_SAVEDPRODUCT_ON_STOCKID FOREIGN KEY (stock_id) REFERENCES stock (id)
GO

ALTER TABLE saved_product
    ADD CONSTRAINT FK_SAVEDPRODUCT_ON_USERID FOREIGN KEY (user_id) REFERENCES [user] (id) ON DELETE CASCADE
GO

ALTER TABLE shipping_info
    ADD CONSTRAINT FK_SHIPPINGINFO_ON_USERID FOREIGN KEY (user_id) REFERENCES [user] (id) ON DELETE CASCADE
GO

ALTER TABLE stock
    ADD CONSTRAINT FK_STOCK_ON_COLORID FOREIGN KEY (color_id) REFERENCES color (id)
GO

ALTER TABLE stock
    ADD CONSTRAINT FK_STOCK_ON_PRODUCTID FOREIGN KEY (product_id) REFERENCES product (id)
GO

ALTER TABLE user_activity_log
    ADD CONSTRAINT FK_USERACTIVITYLOG_ON_USERID FOREIGN KEY (user_id) REFERENCES [user] (id) ON DELETE SET NULL
GO

ALTER TABLE [user]
    ADD CONSTRAINT FK_USER_ON_ROLEID FOREIGN KEY (role_id) REFERENCES role (id)
GO

ALTER TABLE promotion_category
    ADD CONSTRAINT fk_procat_on_category FOREIGN KEY (category_id) REFERENCES category (id)
GO

ALTER TABLE promotion_category
    ADD CONSTRAINT fk_procat_on_promotion FOREIGN KEY (promotion_id) REFERENCES promotions (id)
GO

ALTER TABLE product_features
    ADD CONSTRAINT fk_profea_on_feature FOREIGN KEY (features_id) REFERENCES feature (id)
GO

ALTER TABLE product_feature
    ADD CONSTRAINT fk_profea_on_featureqCSbb5 FOREIGN KEY (feature_id) REFERENCES feature (id)
GO

ALTER TABLE product_features
    ADD CONSTRAINT fk_profea_on_product FOREIGN KEY (product_id) REFERENCES product (id)
GO

ALTER TABLE product_feature
    ADD CONSTRAINT fk_profea_on_productPrAiak FOREIGN KEY (product_id) REFERENCES product (id)
GO

ALTER TABLE promotion_product
    ADD CONSTRAINT fk_propro_on_product FOREIGN KEY (product_id) REFERENCES product (id)
GO

ALTER TABLE promotion_product
    ADD CONSTRAINT fk_propro_on_promotion FOREIGN KEY (promotions_id) REFERENCES promotions (id)
GO

ALTER TABLE stock_instance
    ADD CONSTRAINT fk_stoins_on_instance_property FOREIGN KEY (instance_id) REFERENCES instance_properties (id)
GO

ALTER TABLE stock_instance
    ADD CONSTRAINT fk_stoins_on_stock FOREIGN KEY (stock_id) REFERENCES stock (id)
GO