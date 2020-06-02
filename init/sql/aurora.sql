drop database if exists shop;
create database shop CHARACTER SET utf8 COLLATE utf8_general_ci;
use shop;

/*
drop user if exists demo;
create user 'demo'@'%' identified by 'demo12345';
alter user 'demo'@'%' identified with mysql_native_password by 'demo12345';
grant all privileges on shop.* to 'demo'@'%';

flush privileges;
*/

drop table if exists shop.product;
create table shop.product 
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

insert into shop.product (name, price, delivery_type) values('청바지1', 100, 'Free');
insert into shop.product (name, price, delivery_type) values('청바지2', 200, 'Free');
insert into shop.product (name, price, delivery_type) values('청바지3', 300, 'Free');
commit;


drop table if exists `shop.order`;
create table `shop.order`
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

insert into `shop.order`(product_id, order_price, pay_status) values(1, 1000, 'Queued');
commit;


/*
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
*/


DROP PROCEDURE if exists shop.load_data;

DELIMITER $$
CREATE OR REPLACE PROCEDURE shop.load_data(IN rowcnt INT)
BEGIN
    DECLARE v_cnt             INT DEFAULT 0;
    DECLARE v_price           INT DEFAULT 0;
    DECLARE v_delivery_cd     INT DEFAULT 0;
    DECLARE v_delivery_type   VARCHAR(10);
    DECLARE v_image_url       VARCHAR(300);
    DECLARE v_random          INT;

    DECLARE M_ERR INT DEFAULT 0;    
    DECLARE CONTINUE HANDLER for SQLEXCEPTION SET M_ERR = -1;

    loop_label:  LOOP
        v_cnt := v_cnt + 1;
        

        BEGIN 
            v_price := (MOD(v_cnt, 10) + 1) * 1000;
            select round(dbms_random.value(1,10)) into v_random;
            
            IF v_random = 1 THEN
                v_delivery_type := 'Free';
            ELSE
                v_delivery_type := 'Charged';
            END IF;
            
            v_image_url := 'https://ocktank-prod-image.s3.ap-northeast-2.amazonaws.com/jeans/jean-' || v_random || '.png';
            
            INSERT INTO shop.product(name, price, description, delivery_type, image_url) 
                VALUES( 
                      'ocktank 청바지' || SHOP.seq_product_product_id.currval,   // autoincrement 값 패치?
                      v_price, 
                      '청바지 전문!!!',
                      v_delivery_type,
                      v_image_url);
      
            IF M_ERR < 0 THEN         
               // console output
               M_ERR := 0                            
            END IF;
                                
        END;
                                           
        IF MOD(v_cnt, 1000) = 0 THEN
            COMMIT;
        END IF;
        
        IF v_cnt >= rowcnt THEN 
			   LEAVE  loop_label;
		  END  IF;
                                           
                                           
    END LOOP;
    COMMIT;
END$$ 
DELIMITER ;

CALL shop.load_data(10)









