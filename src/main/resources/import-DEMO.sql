-- Product
insert into WMS_REC_PRODUCT (c_pk,c_pid,c_sku,C_BASE_UNIT,c_ol,c_created) values (1,306981811784,'C1','PC@org.openwms.core.units.api.PieceUnit',0,now());


-- ReceivingOrder

insert into WMS_REC_ORDER (c_pk,c_pid,c_order_id,c_order_state,c_locked,c_priority,c_start_date,c_ol,c_created) values (100,326981811784,'4711','CREATED',false,0,now(),0,now());

insert into WMS_REC_ORDER_POSITION (c_pk,c_order_id,c_pos_no,c_state,C_QTY_EXPECTED_TYPE,C_QTY_EXPECTED,c_sku,c_ol,c_created) values (1000,100,'1','CREATED','PC@org.openwms.core.units.api.Piece','1','C1',0,now());
