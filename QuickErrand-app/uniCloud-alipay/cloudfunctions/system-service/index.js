'use strict';

exports.main = async (event, context) => {
  const { action, data } = event;
  const db = uniCloud.database();
  const dbCmd = db.command;
  
  try {
    switch (action) {
      case 'getBanners': {
        const { position = 1 } = data;
        
        const result = await db.collection('t_banner')
          .where({
            status: 1,
            position: parseInt(position)
          })
          .orderBy('sort_order', 'asc')
          .get();
        
        return {
          code: 200,
          message: '获取成功',
          data: result.data
        };
      }
      
      case 'getAnnouncements': {
        const { position, page = 1, pageSize = 10 } = data;
        
        let query = db.collection('t_announcement').where({
          status: 1
        });
        
        if (position) {
          query = query.where({ position: parseInt(position) });
        }
        
        const countResult = await query.count();
        const total = countResult.total;
        
        const result = await query
          .orderBy('is_top', 'desc')
          .orderBy('publish_time', 'desc')
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
      
      case 'getAnnouncementDetail': {
        const { announcementId } = data;
        
        const result = await db.collection('t_announcement').doc(announcementId).get();
        
        if (!result.data || result.data.length === 0) {
          return { code: 404, message: '公告不存在' };
        }
        
        await db.collection('t_announcement').doc(announcementId).update({
          read_count: dbCmd.inc(1)
        });
        
        return {
          code: 200,
          message: '获取成功',
          data: result.data[0]
        };
      }
      
      case 'getOrderTypes': {
        const result = await db.collection('t_order_type')
          .where({
            status: 1
          })
          .orderBy('sort_order', 'asc')
          .get();
        
        return {
          code: 200,
          message: '获取成功',
          data: result.data
        };
      }
      
      case 'getAddressList': {
        const { userId } = data;
        
        const result = await db.collection('t_address')
          .where({
            user_id: userId
          })
          .orderBy('is_default', 'desc')
          .orderBy('create_time', 'desc')
          .get();
        
        return {
          code: 200,
          message: '获取成功',
          data: result.data
        };
      }
      
      case 'addAddress': {
        const { userId, contactName, contactPhone, province, city, district, detailAddress, longitude, latitude, isDefault } = data;
        
        if (!userId || !contactName || !contactPhone || !province || !city || !district || !detailAddress) {
          return { code: 400, message: '地址信息不完整' };
        }
        
        if (isDefault === 1) {
          await db.collection('t_address').where({
            user_id: userId
          }).update({
            is_default: 0,
            update_time: new Date()
          });
        }
        
        const result = await db.collection('t_address').add({
          user_id: userId,
          contact_name: contactName,
          contact_phone: contactPhone,
          province,
          city,
          district,
          detail_address: detailAddress,
          longitude,
          latitude,
          is_default: isDefault || 0,
          create_time: new Date(),
          update_time: new Date(),
          _openid: context.OPENID || ''
        });
        
        return {
          code: 200,
          message: '添加成功',
          data: {
            addressId: result.id
          }
        };
      }
      
      case 'updateAddress': {
        const { addressId, userId, updateData } = data;
        
        const addressResult = await db.collection('t_address').doc(addressId).get();
        
        if (!addressResult.data || addressResult.data.length === 0) {
          return { code: 404, message: '地址不存在' };
        }
        
        const address = addressResult.data[0];
        
        if (address.user_id !== userId) {
          return { code: 403, message: '无权操作此地址' };
        }
        
        if (updateData.is_default === 1) {
          await db.collection('t_address').where({
            user_id: userId
          }).update({
            is_default: 0,
            update_time: new Date()
          });
        }
        
        updateData.update_time = new Date();
        
        await db.collection('t_address').doc(addressId).update(updateData);
        
        return {
          code: 200,
          message: '更新成功'
        };
      }
      
      case 'deleteAddress': {
        const { addressId, userId } = data;
        
        const addressResult = await db.collection('t_address').doc(addressId).get();
        
        if (!addressResult.data || addressResult.data.length === 0) {
          return { code: 404, message: '地址不存在' };
        }
        
        const address = addressResult.data[0];
        
        if (address.user_id !== userId) {
          return { code: 403, message: '无权操作此地址' };
        }
        
        await db.collection('t_address').doc(addressId).remove();
        
        return {
          code: 200,
          message: '删除成功'
        };
      }
      
      case 'setDefaultAddress': {
        const { addressId, userId } = data;
        
        const addressResult = await db.collection('t_address').doc(addressId).get();
        
        if (!addressResult.data || addressResult.data.length === 0) {
          return { code: 404, message: '地址不存在' };
        }
        
        const address = addressResult.data[0];
        
        if (address.user_id !== userId) {
          return { code: 403, message: '无权操作此地址' };
        }
        
        await db.collection('t_address').where({
          user_id: userId
        }).update({
          is_default: 0,
          update_time: new Date()
        });
        
        await db.collection('t_address').doc(addressId).update({
          is_default: 1,
          update_time: new Date()
        });
        
        return {
          code: 200,
          message: '设置成功'
        };
      }
      
      case 'createEvaluation': {
        const { orderId, userId, runnerId, serviceScore, attitudeScore, content, images, tags } = data;
        
        if (!orderId || !userId || !runnerId || !serviceScore || !attitudeScore) {
          return { code: 400, message: '评价信息不完整' };
        }
        
        const existEval = await db.collection('t_evaluation').where({
          order_id: orderId
        }).get();
        
        if (existEval.data.length > 0) {
          return { code: 400, message: '该订单已评价' };
        }
        
        const result = await db.collection('t_evaluation').add({
          order_id: orderId,
          user_id: userId,
          runner_id: runnerId,
          service_score: serviceScore,
          attitude_score: attitudeScore,
          content,
          images,
          tags,
          status: 0,
          create_time: new Date(),
          update_time: new Date(),
          _openid: context.OPENID || ''
        });
        
        const evaluations = await db.collection('t_evaluation').where({
          runner_id: runnerId,
          status: 0
        }).get();
        
        let totalScore = 0;
        evaluations.data.forEach(e => {
          totalScore += (e.service_score + e.attitude_score) / 2;
        });
        const avgScore = totalScore / evaluations.data.length;
        const goodRate = (avgScore / 5) * 100;
        
        await db.collection('t_runner_info').where({
          user_id: runnerId
        }).update({
          good_rate: Math.round(goodRate * 100) / 100,
          update_time: new Date()
        });
        
        return {
          code: 200,
          message: '评价成功',
          data: {
            evaluationId: result.id
          }
        };
      }
      
      case 'getRunnerReviews': {
        const { runnerId, page = 1, pageSize = 10 } = data;
        
        const countResult = await db.collection('t_evaluation').where({
          runner_id: runnerId,
          status: 0
        }).count();
        
        const total = countResult.total;
        
        const result = await db.collection('t_evaluation')
          .where({
            runner_id: runnerId,
            status: 0
          })
          .orderBy('create_time', 'desc')
          .skip((page - 1) * pageSize)
          .limit(pageSize)
          .get();
        
        const reviewsWithUser = [];
        for (const review of result.data) {
          const userResult = await db.collection('t_user').doc(review.user_id).get();
          reviewsWithUser.push({
            ...review,
            user: userResult.data[0] ? {
              id: userResult.data[0].id,
              nickname: userResult.data[0].nickname,
              avatar: userResult.data[0].avatar
            } : null
          });
        }
        
        return {
          code: 200,
          message: '获取成功',
          data: {
            list: reviewsWithUser,
            total,
            page,
            pageSize
          }
        };
      }
      
      case 'withdraw': {
        const { userId, amount, accountType, accountInfo } = data;
        
        if (!userId || !amount || !accountType || !accountInfo) {
          return { code: 400, message: '提现信息不完整' };
        }
        
        const userResult = await db.collection('t_user').doc(userId).get();
        const user = userResult.data[0];
        
        const earningsResult = await db.collection('t_earnings_record')
          .where({
            user_id: userId,
            type: 1,
            status: 2
          })
          .get();
        
        let totalEarnings = 0;
        earningsResult.data.forEach(item => {
          totalEarnings += item.amount;
        });
        
        const withdrawResult = await db.collection('t_earnings_record')
          .where({
            user_id: userId,
            type: 3,
            status: 2
          })
          .get();
        
        let totalWithdrawn = 0;
        withdrawResult.data.forEach(item => {
          totalWithdrawn += item.amount;
        });
        
        const availableBalance = totalEarnings - totalWithdrawn;
        
        if (amount > availableBalance) {
          return { code: 400, message: '可提现余额不足' };
        }
        
        const fee = Math.round(amount * 0.01 * 100) / 100;
        const actualAmount = amount - fee;
        
        const result = await db.collection('t_withdrawal_record').add({
          user_id: userId,
          amount,
          fee,
          actual_amount: actualAmount,
          account_type: accountType,
          account_info: accountInfo,
          status: 1,
          create_time: new Date(),
          update_time: new Date(),
          _openid: context.OPENID || ''
        });
        
        await db.collection('t_earnings_record').add({
          user_id: userId,
          amount,
          type: 3,
          status: 2,
          remark: `提现申请，提现金额：${amount}元`,
          create_time: new Date(),
          update_time: new Date(),
          _openid: context.OPENID || ''
        });
        
        return {
          code: 200,
          message: '提现申请已提交',
          data: {
            withdrawalId: result.id
          }
        };
      }
      
      case 'getWithdrawalRecords': {
        const { userId, page = 1, pageSize = 10 } = data;
        
        const countResult = await db.collection('t_withdrawal_record').where({
          user_id: userId
        }).count();
        
        const total = countResult.total;
        
        const result = await db.collection('t_withdrawal_record')
          .where({
            user_id: userId
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
      
      default:
        return { code: 400, message: '无效的操作' };
    }
  } catch (error) {
    console.error('系统服务操作错误:', error);
    return { code: 500, message: '服务器错误', error: error.message };
  }
};
