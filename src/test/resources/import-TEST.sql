delete from WMS_REC_ORDER_POS_TU;
delete from WMS_REC_ORDER_POS_PRODUCT;
delete from WMS_REC_ORDER;
delete from WMS_REC_PRODUCT;
delete from WMS_REC_TRANSPORT_UNIT;

-- TransportUnits
insert into WMS_REC_TRANSPORT_UNIT (c_pk, c_pid, c_ol, c_created, c_actual_location, c_barcode, c_foreign_pid) values (1000, '1000', 0, now(), 'WE01', '00000000000000004712', '2');

-- Product
insert into WMS_REC_PRODUCT (c_pk,c_pid,c_foreign_pid,c_sku,c_description,C_BASE_UNIT_TYPE,C_BASE_UNIT_QTY,c_ol,c_created) values (1000,RANDOM(),RANDOM(),'C1','Skateboard gearings 608ZZ','PC@org.openwms.core.units.api.Piece','1',0,now());
insert into WMS_REC_PRODUCT (c_pk,c_pid,c_foreign_pid,c_sku,c_description,C_BASE_UNIT_TYPE,C_BASE_UNIT_QTY,c_ol,c_created) values (1001,RANDOM(),RANDOM(),'C2','Notch M8','PC@org.openwms.core.units.api.Piece','1',0,now());

-- ReceivingOrder
insert into WMS_REC_ORDER (c_pk,c_pid,c_order_id,c_order_state,c_locked,c_priority,c_start_date,c_ol,c_created) values (1000,'d8099b89-bdb6-40d3-9580-d56aeadd578f','T4711','CREATED',false,0,now(),0,now());

-- ReceivingOrderPosition
insert into WMS_REC_ORDER_POS_PRODUCT (c_pk,c_order_id,c_pos_no,c_state,C_QTY_EXPECTED_TYPE,C_QTY_EXPECTED,C_QTY_RECEIVED_TYPE,C_QTY_RECEIVED,c_sku,c_ol,c_created) values (1000,'T4711','1','CREATED','PC@org.openwms.core.units.api.Piece','1','PC@org.openwms.core.units.api.Piece','0','C1',0,now());
insert into WMS_REC_ORDER_POS_PRODUCT (c_pk,c_order_id,c_pos_no,c_state,C_QTY_EXPECTED_TYPE,C_QTY_EXPECTED,C_QTY_RECEIVED_TYPE,C_QTY_RECEIVED,c_sku,c_ol,c_created) values (1001,'T4711','2','CREATED','DOZ@org.openwms.core.units.api.Piece','2','PC@org.openwms.core.units.api.Piece','0','C2',0,now());
insert into WMS_REC_ORDER_POS_TU (c_pk,c_order_id,c_pos_no,c_state,C_TRANSPORT_UNIT_BK,C_TRANSPORT_UNIT_TYPE_NAME,c_ol,c_created) values (1002,'T4711','3','CREATED','00000000000000004712','EURO',0,now());
