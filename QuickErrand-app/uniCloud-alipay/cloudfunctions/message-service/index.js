'use strict';

exports.main = async (event, context) => {
  const { action, data } = event;
  const db = uniCloud.database();
  const dbCmd = db.command;
  
  try {
    switch (action) {
      case 'sendMessage': {
        const { orderId, fromUserId, toUserId, content, msgType = 1 } = data;
        
        if (!orderId || !fromUserId || !toUserId || !content) {
          return { code: 400, message: '消息信息不完整' };
        }
        
        const sendTime = Date.now();
        
        const result = await db.collection('t_chat_message').add({
          order_id: orderId,
          from_user_id: fromUserId,
          to_user_id: toUserId,
          content,
          msg_type: msgType,
          read_status: 0,
          send_time: sendTime,
          create_time: new Date(),
          update_time: new Date(),
          deleted: 0,
          _openid: context.OPENID || ''
        });
        
        const fromUserResult = await db.collection('t_user').doc(fromUserId).get();
        const fromUser = fromUserResult.data[0];
        
        await db.collection('t_message').add({
          user_id: toUserId,
          title: '新消息',
          content: `您有一条来自 ${fromUser.nickname} 的新消息，点击查看`,
          type: 3,
          related_id: orderId,
          is_read: 0,
          create_time: new Date(),
          update_time: new Date(),
          _openid: context.OPENID || ''
        });
        
        return {
          code: 200,
          message: '发送成功',
          data: {
            messageId: result.id,
            sendTime
          }
        };
      }
      
      case 'getChatMessages': {
        const { orderId, page = 1, pageSize = 50 } = data;
        
        const countResult = await db.collection('t_chat_message').where({
          order_id: orderId,
          deleted: 0
        }).count();
        
        const total = countResult.total;
        
        const result = await db.collection('t_chat_message')
          .where({
            order_id: orderId,
            deleted: 0
          })
          .orderBy('send_time', 'asc')
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
      
      case 'markAsRead': {
        const { orderId, userId } = data;
        
        await db.collection('t_chat_message').where({
          order_id: orderId,
          to_user_id: userId,
          read_status: 0
        }).update({
          read_status: 1,
          read_time: Date.now(),
          update_time: new Date()
        });
        
        return {
          code: 200,
          message: '已标记为已读'
        };
      }
      
      case 'getChatList': {
        const { userId } = data;
        
        const chatRelResult = await db.collection('t_chat_order_rel').where(dbCmd.or([
          { user_id: userId },
          { runner_id: userId }
        ])).get();
        
        const chatList = [];
        
        for (const rel of chatRelResult.data) {
          const orderResult = await db.collection('t_order').doc(rel.order_id).get();
          const order = orderResult.data[0];
          
          const otherUserId = rel.user_id === userId ? rel.runner_id : rel.user_id;
          const otherUserResult = await db.collection('t_user').doc(otherUserId).get();
          const otherUser = otherUserResult.data[0];
          
          const lastMsgResult = await db.collection('t_chat_message')
            .where({
              order_id: rel.order_id,
              deleted: 0
            })
            .orderBy('send_time', 'desc')
            .limit(1)
            .get();
          
          const unreadCountResult = await db.collection('t_chat_message').where({
            order_id: rel.order_id,
            to_user_id: userId,
            read_status: 0
          }).count();
          
          chatList.push({
            orderId: rel.order_id,
            orderNo: order.order_no,
            orderStatus: order.status,
            otherUser: {
              id: otherUser.id,
              nickname: otherUser.nickname,
              avatar: otherUser.avatar
            },
            lastMessage: lastMsgResult.data[0] || null,
            unreadCount: unreadCountResult.total
          });
        }
        
        chatList.sort((a, b) => {
          if (!a.lastMessage || !b.lastMessage) return 0;
          return b.lastMessage.send_time - a.lastMessage.send_time;
        });
        
        return {
          code: 200,
          message: '获取成功',
          data: chatList
        };
      }
      
      case 'getMessages': {
        const { userId, type, page = 1, pageSize = 10 } = data;
        
        let query = db.collection('t_message').where({
          user_id: userId
        });
        
        if (type) {
          query = query.where({ type: parseInt(type) });
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
      
      case 'markMessageAsRead': {
        const { messageId } = data;
        
        await db.collection('t_message').doc(messageId).update({
          is_read: 1,
          update_time: new Date()
        });
        
        return {
          code: 200,
          message: '已标记为已读'
        };
      }
      
      case 'getUnreadCount': {
        const { userId } = data;
        
        const result = await db.collection('t_message').where({
          user_id: userId,
          is_read: 0
        }).count();
        
        return {
          code: 200,
          message: '获取成功',
          data: {
            unreadCount: result.total
          }
        };
      }
      
      default:
        return { code: 400, message: '无效的操作' };
    }
  } catch (error) {
    console.error('消息操作错误:', error);
    return { code: 500, message: '服务器错误', error: error.message };
  }
};
