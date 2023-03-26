delete from WMS_REC_ORDER_ID;
delete from WMS_REC_ORDER_DETAIL;
delete from WMS_REC_ORDER_POSITION_DETAIL;
delete from WMS_REC_ORDER_POS_TU;
delete from WMS_REC_ORDER_POS_PRODUCT;
delete from WMS_REC_ORDER;
delete from WMS_REC_PRODUCT;
delete from WMS_REC_TRANSPORT_UNIT;

-- Product
insert into WMS_REC_PRODUCT (c_pk,c_pid,c_foreign_pid,c_sku,c_label,c_description,c_base_unit_type,c_base_unit_qty,c_overbooking_allowed,c_ol,c_created,c_created_by) values (1000,RANDOM(),RANDOM(),'C1', 'L_C1','Skateboard gearings 608ZZ','PC@org.openwms.core.units.api.Piece','1',false,0,now(),'SYSTEM');
insert into WMS_REC_PRODUCT (c_pk,c_pid,c_foreign_pid,c_sku,c_label,c_description,c_base_unit_type,c_base_unit_qty,c_overbooking_allowed,c_ol,c_created,c_created_by) values (1001,RANDOM(),RANDOM(),'C2', 'L_C2','Notch M8','PC@org.openwms.core.units.api.Piece','1',true,0,now(),'SYSTEM');

-- ReceivingOrder
insert into WMS_REC_ORDER (c_pk,c_pid,c_order_id,c_order_state,c_locked,c_priority,c_start_earliest_at,c_ol,c_created,c_created_by) values (1000,'d8099b89-bdb6-40d3-9580-d56aeadd578f','T4711','CREATED',false,0,now(),0,now(),'SYSTEM');

insert into WMS_REC_ORDER_DETAIL (c_order_pk, c_key, c_value) values (1000, 'Supplier name', 'Bangee Ltd. Hongkong');
insert into WMS_REC_ORDER_DETAIL (c_order_pk, c_key, c_value) values (1000, 'Supplier no', '182');

insert into WMS_REC_ORDER_ID (c_pk,c_created,c_ol,c_current,c_name,c_prefix) values (1,now(),  0,1000,'DEFAULT','RO');

-- ReceivingOrderPosition
insert into WMS_REC_ORDER_POS_PRODUCT (c_pk,c_order_id,c_pos_no,c_state,c_qty_expected_type,c_qty_expected,c_qty_received_type,c_qty_received,c_sku,c_ol,c_created,c_created_by) values (1000,'T4711','1','CREATED','PC@org.openwms.core.units.api.Piece','1','PC@org.openwms.core.units.api.Piece','0','C1',0,now(), 'SYSTEM');
insert into WMS_REC_ORDER_POS_PRODUCT (c_pk,c_order_id,c_pos_no,c_state,c_qty_expected_type,c_qty_expected,c_qty_received_type,c_qty_received,c_sku,c_ol,c_created,c_created_by) values (1001,'T4711','2','CREATED','DOZ@org.openwms.core.units.api.Piece','2','PC@org.openwms.core.units.api.Piece','0','C2',0,now(), 'SYSTEM');
insert into WMS_REC_ORDER_POS_TU      (c_pk,c_order_id,c_pos_no,c_state,c_latest_due,c_transport_unit_bk,c_transport_unit_type_name,c_ol,c_created,c_created_by) values (1002,'T4711','3','CREATED', now(), '00000000000000004712','EURO',0,now(), 'SYSTEM');

--insert into WMS_REC_ORDER_POSITION_DETAIL (c_order_pos_pk, c_key, c_value) values (1000, 'Unload', 'Unload carefully');
--insert into WMS_REC_ORDER_POSITION_DETAIL (c_order_pos_pk, c_key, c_value) values (1000, 'Unload cond.', 'Put to freezer zone');

-- TransportUnits
insert into WMS_REC_TRANSPORT_UNIT (c_pk, c_pid, c_ol, c_created, c_actual_location, c_barcode, c_foreign_pid) values (1000, '1000', 0, now(), 'WE01', '00000000000000004712', '2');

