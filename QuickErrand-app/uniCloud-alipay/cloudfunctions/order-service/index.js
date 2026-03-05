'use strict';

exports.main = async (event, context) => {
  const { action, data } = event;
  const db = uniCloud.database();
  const dbCmd = db.command;
  
  const BASE_PRICE = 5.00;
  const PRICE_PER_KM = 2.00;
  const PLATFORM_RATE = 0.10;
  
  function generateOrderNo() {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const hour = String(now.getHours()).padStart(2, '0');
    const minute = String(now.getMinutes()).padStart(2, '0');
    const second = String(now.getSeconds()).padStart(2, '0');
    const random = String(Math.floor(Math.random() * 1000000)).padStart(6, '0');
    return `ORD${year}${month}${day}${hour}${minute}${second}${random}`;
  }
  
  function calculateFee(distance) {
    const distanceKm = distance / 1000;
    const serviceFee = BASE_PRICE + (distanceKm * PRICE_PER_KM);
    const platformFee = serviceFee * PLATFORM_RATE;
    const runnerFee = serviceFee - platformFee;
    
    return {
      serviceFee: Math.round(serviceFee * 100) / 100,
      platformFee: Math.round(platformFee * 100) / 100,
      runnerFee: Math.round(runnerFee * 100) / 100
    };
  }
  
  try {
    switch (action) {
      case 'createOrder': {
        const {
          userId, orderTypeId, pickupContact, pickupPhone, pickupAddress,
          pickupLongitude, pickupLatitude, deliveryAddress, deliveryContact,
          deliveryPhone, deliveryLongitude, deliveryLatitude, distance,
          itemDescription, itemImages, expectedTime, remark
        } = data;
        
        if (!userId || !orderTypeId || !pickupAddress || !deliveryAddress || !itemDescription) {
          return { code: 400, message: '订单信息不完整' };
        }
        
        const fees = calculateFee(distance);
        const orderNo = generateOrderNo();
        const pickupCode = Math.random().toString().slice(2, 6);
        
        const orderData = {
          order_no: orderNo,
          user_id: userId,
          order_type_id: orderTypeId,
          pickup_contact: pickupContact,
          pickup_phone: pickupPhone,
          pickup_address: pickupAddress,
          pickup_longitude: pickupLongitude,
          pickup_latitude: pickupLatitude,
          delivery_address: deliveryAddress,
          delivery_contact: deliveryContact,
          delivery_phone: deliveryPhone,
          delivery_longitude: deliveryLongitude,
          delivery_latitude: deliveryLatitude,
          distance,
          item_description: itemDescription,
          item_images: itemImages,
          expected_time: expectedTime,
          service_fee: fees.serviceFee,
          platform_fee: fees.platformFee,
          runner_fee: fees.runnerFee,
          remark,
          pickup_code: pickupCode,
          status: 1,
          create_time: new Date(),
          update_time: new Date(),
          _openid: context.OPENID || ''
        };
        
        const result = await db.collection('t_order').add(orderData);
        
        return {
          code: 200,
          message: '订单创建成功',
          data: {
            orderId: result.id,
            orderNo,
            serviceFee: fees.serviceFee,
            pickupCode
          }
        };
      }
      
      case 'payOrder': {
        const { orderId, userId, paymentMethod } = data;
        
        const orderResult = await db.collection('t_order').doc(orderId).get();
        
        if (!orderResult.data || orderResult.data.length === 0) {
          return { code: 404, message: '订单不存在' };
        }
        
        const order = orderResult.data[0];
        
        if (order.user_id !== userId) {
          return { code: 403, message: '无权操作此订单' };
        }
        
        if (order.status !== 1) {
          return { code: 400, message: '订单状态不正确' };
        }
        
        if (paymentMethod === 2) {
          const userResult = await db.collection('t_user').doc(userId).get();
          const user = userResult.data[0];
          
          if (user.balance < order.service_fee) {
            return { code: 400, message: '余额不足' };
          }
          
          await db.collection('t_user').doc(userId).update({
            balance: dbCmd.inc(-order.service_fee)
          });
        }
        
        await db.collection('t_order').doc(orderId).update({
          status: 2,
          payment_method: paymentMethod,
          pay_time: new Date(),
          update_time: new Date()
        });
        
        await db.collection('t_message').add({
          user_id: userId,
          title: '订单支付成功',
          content: `您的订单 ${order.order_no} 已支付成功，等待跑腿员接单`,
          type: 1,
          related_id: orderId,
          is_read: 0,
          create_time: new Date(),
          update_time: new Date(),
          _openid: context.OPENID || ''
        });
        
        return {
          code: 200,
          message: '支付成功'
        };
      }
      
      case 'getOrderList': {
        const { userId, status, page = 1, pageSize = 10 } = data;
        
        let query = db.collection('t_order').where({
          user_id: userId
        });
        
        if (status) {
          query = query.where({ status: parseInt(status) });
        }
        
        const countResult = await query.count();
        const total = countResult.total;
        
        const result = await query
          .orderBy('create_time', 'desc')
          .skip((page - 1) * pageSize)
          .limit(pageSize)
          .get();
        
        return {
          code: 200,
          message: '获取成功',
          data: {
            list: result.data,
            total,
            page,
            pageSize
          }
        };
      }
      
      case 'getOrderDetail': {
        const { orderId } = data;
        
        const orderResult = await db.collection('t_order').doc(orderId).get();
        
        if (!orderResult.data || orderResult.data.length === 0) {
          return { code: 404, message: '订单不存在' };
        }
        
        const order = orderResult.data[0];
        
        const typeResult = await db.collection('t_order_type').doc(order.order_type_id).get();
        order.order_type = typeResult.data[0] || null;
        
        if (order.runner_id) {
          const runnerResult = await db.collection('t_user').doc(order.runner_id).get();
          order.runner = runnerResult.data[0] || null;
        }
        
        return {
          code: 200,
          message: '获取成功',
          data: order
        };
      }
      
      case 'cancelOrder': {
        const { orderId, userId, cancelReason } = data;
        
        const orderResult = await db.collection('t_order').doc(orderId).get();
        
        if (!orderResult.data || orderResult.data.length === 0) {
          return { code: 404, message: '订单不存在' };
        }
        
        const order = orderResult.data[0];
        
        if (order.user_id !== userId) {
          return { code: 403, message: '无权操作此订单' };
        }
        
        if (order.status !== 1 && order.status !== 2) {
          return { code: 400, message: '订单状态不允许取消' };
        }
        
        if (order.payment_method === 2 && order.pay_time) {
          await db.collection('t_user').doc(userId).update({
            balance: dbCmd.inc(order.service_fee)
          });
        }
        
        await db.collection('t_order').doc(orderId).update({
          status: 5,
          cancel_reason: cancelReason,
          cancel_time: new Date(),
          update_time: new Date()
        });
        
        return {
          code: 200,
          message: '订单已取消'
        };
      }
      
      default:
        return { code: 400, message: '无效的操作' };
    }
  } catch (error) {
    console.error('订单操作错误:', error);
    return { code: 500, message: '服务器错误', error: error.message };
  }
};
