drop database if exists shop;
create database shop CHARACTER SET utf8 COLLATE utf8_general_ci;
use shop;

drop user if exists demo;
create user 'demo'@'%' identified by 'demo';
alter user 'demo'@'%' identified with mysql_native_password by 'demo';
grant all privileges on shop.* to 'demo'@'%';
GRANT EXECUTE ON PROCEDURE mysql.lambda_async TO 'demo'@'%';

flush privileges;

drop table if exists product;
create table product 
(
   product_id         int not null auto_increment,
   name               varchar(100) not null,
   price              int not null,
   description        text,
   thumb_image_url    varchar(300),
   image_url          varchar(300),
   delivery_type      enum('Free', 'Charged') not null,
   comment_cnt        int not null default 0,
   buy_cnt            int not null default 0,
   primary key(product_id)
);

insert into product (name, price, delivery_type) values('청바지1', 100, 'Free');
insert into product (name, price, delivery_type) values('청바지2', 200, 'Free');
insert into product (name, price, delivery_type) values('청바지3', 300, 'Free');
commit;


drop table if exists `order`;
create table `order`
(
   order_id                int not null auto_increment,
   product_id              int not null,
   order_price             int not null,
   pay_status              enum('Queued', 'Processing', 'error', 'Completed') not null,
   order_ymdt              datetime default CURRENT_TIMESTAMP,
   pay_ymdt                datetime,
   error_ymdt              datetime,
   error_message           varchar(300),
   primary key(order_id)
);

insert into `order`(product_id, order_price, pay_status) values(1, 1000, 'Queued');
commit;


DROP TRIGGER if exists after_order_insert;

DELIMITER $$
CREATE TRIGGER after_order_insert
AFTER INSERT
ON `order` FOR EACH ROW
BEGIN

   SELECT JSON_OBJECT('order_id', new.order_id, 
      'product_id', new.product_id, 
      'order_price', new.order_price,
      'pay_status', new.pay_status, 
      'order_ymdt', new.order_ymdt) into @payload;
   
   CALL mysql.lambda_async('arn:aws:lambda:ap-northeast-2:509076023497:function:OrderEventQueuing', @payload);

END$$ 
DELIMITER ;



DROP PROCEDURE SHOP.LOAD_DATA;
CREATE OR REPLACE PROCEDURE SHOP.LOAD_DATA(rowcnt IN NUMBER)
IS
    v_cnt NUMBER := 0;
    v_price NUMBER := 0;
    v_delivery_cd NUMBER := 0;
    v_delivery_type VARCHAR2(10);
    v_image_url VARCHAR2(300);
    v_random int;
BEGIN
    
    LOOP
        v_cnt := v_cnt + 1;
        
        BEGIN 
            v_price := (MOD(v_cnt, 10) + 1) * 1000;
            select round(dbms_random.value(1,10)) into v_random from dual;
            
            IF v_random = 1 THEN
                v_delivery_type := 'Free';
            ELSE
                v_delivery_type := 'Charged';
            END IF;
            
            v_image_url := 'https://ocktank-prod-image.s3.ap-northeast-2.amazonaws.com/jeans/jean-' || v_random || '.png';
            
            INSERT INTO SHOP.PRODUCT(product_id, name, price, description, delivery_type, image_url) 
                VALUES(SHOP.seq_product_product_id.nextval, 
                      'ocktank 청바지' || SHOP.seq_product_product_id.currval, 
                      v_price, 
                      '청바지 전문!!!',
                      v_delivery_type,
                      v_image_url);
        EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Exception ..'); 
        END;
           
        IF MOD(v_cnt, 1000) = 0 THEN
            COMMIT;
        END IF;
        
        EXIT WHEN v_cnt >= rowcnt;
        
    END LOOP;
    COMMIT;
END;
/

exec SHOP.LOAD_DATA(10)









