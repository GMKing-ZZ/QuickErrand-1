'use strict';

exports.main = async (event, context) => {
  const { action, data } = event;
  const db = uniCloud.database();
  const dbCmd = db.command;
  
  try {
    switch (action) {
      case 'applyCertification': {
        const { userId, realName, idCard, idCardFront, idCardBack, serviceTime, serviceRange } = data;
        
        if (!userId || !realName || !idCard || !idCardFront || !idCardBack) {
          return { code: 400, message: '认证信息不完整' };
        }
        
        const existRunner = await db.collection('t_runner_info').where({
          user_id: userId
        }).get();
        
        if (existRunner.data.length > 0) {
          await db.collection('t_runner_info').where({
            user_id: userId
          }).update({
            real_name: realName,
            id_card: idCard,
            id_card_front: idCardFront,
            id_card_back: idCardBack,
            cert_status: 1,
            service_time: serviceTime,
            service_range: serviceRange || 5000,
            update_time: new Date()
          });
        } else {
          await db.collection('t_runner_info').add({
            user_id: userId,
            real_name: realName,
            id_card: idCard,
            id_card_front: idCardFront,
            id_card_back: idCardBack,
            cert_status: 1,
            credit_level: 5,
            total_orders: 0,
            good_rate: 100.00,
            service_time: serviceTime,
            service_range: serviceRange || 5000,
            create_time: new Date(),
            update_time: new Date(),
            _openid: context.OPENID || ''
          });
        }
        
        await db.collection('t_user').doc(userId).update({
          user_type: 2,
          update_time: new Date()
        });
        
        return {
          code: 200,
          message: '认证申请已提交，请等待审核'
        };
      }
      
      case 'getRunnerInfo': {
        const { userId } = data;
        
        const runnerResult = await db.collection('t_runner_info').where({
          user_id: userId
        }).get();
        
        if (runnerResult.data.length === 0) {
          return {
            code: 200,
            message: '跑腿员信息不存在',
            data: null
          };
        }
        
        return {
          code: 200,
          message: '获取成功',
          data: runnerResult.data[0]
        };
      }
      
      case 'getPendingOrders': {
        const { page = 1, pageSize = 10 } = data;
        
        const countResult = await db.collection('t_order').where({
          status: 2
        }).count();
        
        const total = countResult.total;
        
        const result = await db.collection('t_order')
          .where({
            status: 2
          })
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
      
      case 'acceptOrder': {
        const { orderId, runnerId } = data;
        
        const orderResult = await db.collection('t_order').doc(orderId).get();
        
        if (!orderResult.data || orderResult.data.length === 0) {
          return { code: 404, message: '订单不存在' };
        }
        
        const order = orderResult.data[0];
        
        if (order.status !== 2) {
          return { code: 400, message: '订单已被接单或状态不正确' };
        }
        
        await db.collection('t_order').doc(orderId).update({
          runner_id: runnerId,
          status: 3,
          accept_time: new Date(),
          update_time: new Date()
        });
        
        await db.collection('t_chat_order_rel').add({
          order_id: orderId,
          user_id: order.user_id,
          runner_id: runnerId,
          create_time: new Date(),
          update_time: new Date(),
          deleted: 0,
          _openid: context.OPENID || ''
        });
        
        const runnerResult = await db.collection('t_user').doc(runnerId).get();
        const runner = runnerResult.data[0];
        
        await db.collection('t_message').add({
          user_id: order.user_id,
          title: '跑腿员已接单',
          content: `您的订单 ${order.order_no} 已被 ${runner.nickname} 接单，请耐心等待`,
          type: 1,
          related_id: orderId,
          is_read: 0,
          create_time: new Date(),
          update_time: new Date(),
          _openid: context.OPENID || ''
        });
        
        return {
          code: 200,
          message: '接单成功'
        };
      }
      
      case 'getRunnerOrders': {
        const { runnerId, status, page = 1, pageSize = 10 } = data;
        
        let query = db.collection('t_order').where({
          runner_id: runnerId
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
      
      case 'completeOrder': {
        const { orderId, runnerId } = data;
        
        const orderResult = await db.collection('t_order').doc(orderId).get();
        
        if (!orderResult.data || orderResult.data.length === 0) {
          return { code: 404, message: '订单不存在' };
        }
        
        const order = orderResult.data[0];
        
        if (order.runner_id !== runnerId) {
          return { code: 403, message: '无权操作此订单' };
        }
        
        if (order.status !== 3) {
          return { code: 400, message: '订单状态不正确' };
        }
        
        await db.collection('t_order').doc(orderId).update({
          status: 4,
          complete_time: new Date(),
          update_time: new Date()
        });
        
        await db.collection('t_earnings_record').add({
          user_id: runnerId,
          order_id: orderId,
          amount: order.runner_fee,
          type: 1,
          status: 2,
          remark: `订单完成，订单号：${order.order_no}`,
          create_time: new Date(),
          update_time: new Date(),
          _openid: context.OPENID || ''
        });
        
        await db.collection('t_runner_info').where({
          user_id: runnerId
        }).update({
          total_orders: dbCmd.inc(1),
          update_time: new Date()
        });
        
        await db.collection('t_message').add({
          user_id: order.user_id,
          title: '订单已完成',
          content: `您的订单 ${order.order_no} 已完成，请对跑腿员进行评价`,
          type: 1,
          related_id: orderId,
          is_read: 0,
          create_time: new Date(),
          update_time: new Date(),
          _openid: context.OPENID || ''
        });
        
        await db.collection('t_message').add({
          user_id: runnerId,
          title: '订单已完成',
          content: `您服务的订单 ${order.order_no} 已完成，收益 ${order.runner_fee} 元`,
          type: 1,
          related_id: orderId,
          is_read: 0,
          create_time: new Date(),
          update_time: new Date(),
          _openid: context.OPENID || ''
        });
        
        return {
          code: 200,
          message: '订单已完成'
        };
      }
      
      case 'getEarnings': {
        const { userId, page = 1, pageSize = 10 } = data;
        
        const countResult = await db.collection('t_earnings_record').where({
          user_id: userId
        }).count();
        
        const total = countResult.total;
        
        const result = await db.collection('t_earnings_record')
          .where({
            user_id: userId
          })
          .orderBy('create_time', 'desc')
          .skip((page - 1) * pageSize)
          .limit(pageSize)
          .get();
        
        const totalEarnings = await db.collection('t_earnings_record')
          .where({
            user_id: userId,
            type: 1,
            status: 2
          })
          .get();
        
        let totalAmount = 0;
        totalEarnings.data.forEach(item => {
          totalAmount += item.amount;
        });
        
        return {
          code: 200,
          message: '获取成功',
          data: {
            list: result.data,
            total,
            totalEarnings: Math.round(totalAmount * 100) / 100,
            page,
            pageSize
          }
        };
      }
      
      default:
        return { code: 400, message: '无效的操作' };
    }
  } catch (error) {
    console.error('跑腿员操作错误:', error);
    return { code: 500, message: '服务器错误', error: error.message };
  }
};
