create table categories
(
    id          int identity
        constraint pk_category
            primary key,
    name        nvarchar(100) not null,
    image       nvarchar(max),
    description nvarchar(255)
)
go

create table colors
(
    id       int identity
        constraint pk_color
            primary key,
    name     nvarchar(50) not null
        constraint uk_color_name
            unique,
    hex_code varchar(7)
)
go

create table roles
(
    id   int identity
        constraint pk_role
            primary key,
    name nvarchar(50) not null
)
go

create table users
(
    id         int identity
        constraint pk_user
            primary key,
    email      nvarchar(255) not null,
    phone      nvarchar(20),
    password   nvarchar(255),
    first_name nvarchar(50),
    last_name  nvarchar(50),
    image      nvarchar(255),
    created_at datetime
        constraint DF_user_created_at default getdate(),
    updated_at datetime,
    enabled    bit default 0,
    birth      date
)
go

create table blogs
(
    id           int identity
        constraint pk_blog
            primary key,
    title        nvarchar(255) not null,
    content      nvarchar(max) not null,
    thumbnail    nvarchar(max),
    author_id    int
        constraint FK_BLOG_ON_AUTHORID
            references users,
    published_at datetime,
    created_at   datetime
        constraint DF_blog_created_at default getdate(),
    updated_at   datetime,
    is_published bit default 0 not null,
    status       nvarchar(50)
)
go

create table features
(
    id          int identity
        constraint pk_feature
            primary key,
    name        nvarchar(100) not null,
    description nvarchar(500),
    image       nvarchar(max),
    created_at  datetime
        constraint DF_feature_created_at default getdate(),
    created_by  int           not null
        constraint FK_FEATURE_ON_CREATEDBY
            references users
)
go

create table instance_properties
(
    id         int identity
        constraint pk_instanceproperties
            primary key,
    name       nvarchar(255) not null
        constraint uk_instance_property_name
            unique,
    created_at datetime
        constraint DF_instance_properties_created_at default getdate(),
    created_by int           not null
        constraint FK_INSTANCEPROPERTIES_ON_CREATEDBY
            references users
)
go

create table products
(
    id          int identity
        constraint pk_product
            primary key,
    name        nvarchar(255) not null,
    description nvarchar(max),
    created_at  datetime
        constraint DF_product_created_at default getdate(),
    created_by  int           not null
        constraint FK_PRODUCT_ON_CREATEDBY
            references users,
    updated_at  datetime
        constraint DF_product_updated_at default getdate(),
    updated_by  int           not null
        constraint FK_PRODUCT_ON_UPDATEDBY
            references users,
    category_id int
        constraint FK_PRODUCT_ON_CATEGORYID
            references categories
            on update cascade on delete set null,
    is_deleted  bit default 0
)
go

create table product_features
(
    feature_id int not null
        constraint fk_profea_on_featureqCSbb5
            references features
            on update cascade on delete cascade,
    product_id int not null
        constraint fk_profea_on_productPrAiak
            references products
            on update cascade,
    constraint pk_productfeature
        primary key (feature_id, product_id)
)
go

create table promotions
(
    id                  int identity
        constraint pk_promotions
            primary key,
    name                nvarchar(255)  not null,
    code                nvarchar(50)   not null,
    promotion_type      varchar(55)    not null,
    value               decimal(18, 2) not null,
    max_discount_amount decimal(18, 2),
    min_order_value     decimal(18, 2),
    usage_limit         int            not null,
    usage_count         int
        constraint DF_promotions_usage_count default 0,
    is_active           bit
        constraint DF_promotions_is_active default 1,
    start_date          datetime       not null,
    end_date            datetime       not null,
    created_at          datetime2 default getdate(),
    created_by          int
        constraint promotions_users_id_fk
            references users
)
go

create table orders
(
    id                       int identity
        constraint pk_order
            primary key,
    created_by               int
        constraint FK_ORDER_ON_CREATEDBY
            references users,
    created_at               datetime
        constraint DF_order_created_at default getdate(),
    payment_type             varchar(55) not null,
    approve_at               datetime
        constraint DF_order_approve_at default getdate(),
    approve_by               int
        constraint FK_ORDER_ON_APPROVEBY
            references users,
    first_name               nvarchar(55),
    last_name                nvarchar(55),
    email                    nvarchar(255),
    phone                    nvarchar(20),
    address                  nvarchar(500),
    ward                     nvarchar(100),
    district                 nvarchar(100),
    province                 nvarchar(100),
    country                  nvarchar(100),
    status                   varchar(55) not null,
    shipping_tracking_code   varchar(255),
    product_promotion_id     int
        constraint orders_product_promotions_id_fk
            references promotions,
    shipping_promotion_id    int
        constraint orders_shipping_promotions_id_fk
            references promotions,
    shipping_discount_amount decimal(18, 2) default 0,
    product_discount_amount  decimal(18, 2) default 0,
    subtotal                 decimal(18, 2),
    shipping_fee             decimal(18, 2) default 0,
    final_total              decimal(18, 2),
    vat                      decimal(18, 2)
)
go

create table refresh_tokens
(
    id          int identity
        primary key,
    user_id     int          not null
        references users,
    token       varchar(500) not null
        unique,
    expiry_date date         not null,
    is_revoked  bit default 0,
    issued_at   date
)
go

create table shipping_infos
(
    id         int identity
        constraint pk_shippinginfo
            primary key,
    user_id    int           not null
        constraint FK_SHIPPINGINFO_ON_USERID
            references users
            on delete cascade,
    first_name nvarchar(55),
    last_name  nvarchar(55)  not null,
    email      nvarchar(255) not null,
    phone      nvarchar(20)  not null,
    address    nvarchar(500),
    ward       nvarchar(100) not null,
    district   nvarchar(100) not null,
    is_default bit
        constraint DF_shipping_info_is_default default 0,
    created_at datetime2 default getdate(),
    updated_at datetime2 default getdate(),
    province   nvarchar(100) not null
)
go

create table stocks
(
    id         int identity
        constraint pk_stock
            primary key,
    product_id int                             not null
        constraint FK_STOCK_ON_PRODUCTID
            references products,
    color_id   int
        constraint FK_STOCK_ON_COLORID
            references colors
            on update cascade,
    quantity   int
        constraint DF_stock_quantity default 0 not null,
    price      decimal(18, 2)                  not null
)
go

create table cart_items
(
    id           int identity
        constraint pk_cartitem
            primary key,
    user_id      int not null
        constraint FK_CARTITEM_ON_USERID
            references users,
    product_id   int not null
        constraint FK_CARTITEM_ON_PRODUCTID
            references products,
    product_name nvarchar(255),
    stock_id     int not null
        constraint FK_CARTITEM_ON_STOCKID
            references stocks,
    quantity     int not null
)
go

create table order_details
(
    id           int identity
        constraint pk_orderdetail
            primary key,
    order_id     int            not null
        constraint FK_ORDERDETAIL_ON_ORDERID
            references orders
            on delete cascade,
    product_id   int
        constraint order_details_products_id_fk
            references products
            on update cascade on delete set null,
    product_name nvarchar(255)  not null,
    quantity     int            not null,
    price        decimal(18, 2) not null,
    note         nvarchar(255),
    color_name   nvarchar(50)   not null,
    version_name nvarchar(550)  not null,
    image_url    nvarchar(max)  not null,
    stock_id     int
        constraint order_details_stocks_id_fk
            references stocks
            on update cascade on delete set null,
    is_reviewed  bit
)
go

create table product_photos
(
    id        int identity
        constraint pk_productphotos
            primary key,
    stock_id  int           not null
        constraint FK_PRODUCTPHOTOS_ON_STOCKID
            references stocks,
    image_url nvarchar(max) not null,
    alt       nvarchar(155)
)
go

create table reviews
(
    id            int identity
        constraint pk_review
            primary key,
    user_id       int            not null
        constraint FK_REVIEW_ON_USERID
            references users,
    order_id      int            not null
        constraint reviews_orders_id_fk
            references orders,
    content       nvarchar(1000) not null,
    rating        int            not null,
    created_at    datetime
        constraint DF_review_created_at default getdate(),
    is_approved   bit
        constraint DF_review_is_approved default 0,
    approved_by   int
        constraint FK_REVIEW_ON_APPROVEDBY
            references users,
    approved_at   datetime,
    reply_content nvarchar(1000),
    replied_by    int
        constraint FK_REVIEW_ON_REPLIEDBY
            references users,
    stock_id      int            not null
        constraint reviews_stocks_id_fk
            references stocks
)
go

create table saved_products
(
    product_id int not null
        constraint FK_SAVEDPRODUCT_ON_PRODUCTID
            references products
            on delete cascade,
    created_at datetime
        constraint DF_saved_product_created_at default getdate(),
    user_id    int not null
        constraint FK_SAVEDPRODUCT_ON_USERID
            references users
            on delete cascade,
    stock_id   int not null
        constraint FK_SAVEDPRODUCT_ON_STOCKID
            references stocks,
    constraint pk_savedproduct
        primary key (user_id, stock_id)
)
go

create table stock_instances
(
    stock_id    int not null
        constraint stock_instances_stocks_id_fk
            references stocks,
    instance_id int not null
        constraint stock_instances_instance_properties_id_fk
            references instance_properties
            on update cascade
)
go

create table user_activity_logs
(
    id                 bigint identity
        constraint pk_useractivitylog
            primary key,
    user_id            int           not null
        constraint user_activity_log_user_id_fk
            references users,
    log_time           datetime
        constraint DF_user_activity_log_log_time default getdate(),
    action_type        nvarchar(max) not null,
    target_entity_type nvarchar(max),
    message            nvarchar(max),
    old_value          nvarchar(max),
    new_value          nvarchar(max)
)
go

create table user_role
(
    user_id int not null
        constraint user_role_users_id_fk
            references users,
    role_id int not null
        constraint user_role_roles_id_fk
            references roles,
    constraint user_role_pk
        primary key (role_id, user_id)
)
go

create unique index user_email_uindex
    on users (email)
go

create unique index IX_phone_Unique_NotNull
    on users (phone)
    where [phone] IS NOT NULL
go