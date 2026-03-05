'use strict';
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');

exports.main = async (event, context) => {
  const { action, data } = event;
  const db = uniCloud.database();
  const dbCmd = db.command;
  
  const JWT_SECRET = 'quickerrand-secret-key-2026';
  
  try {
    switch (action) {
      case 'register': {
        const { username, password, phone, nickname } = data;
        
        if (!username || !password || !phone) {
          return { code: 400, message: '用户名、密码和手机号不能为空' };
        }
        
        const existUser = await db.collection('t_user').where(dbCmd.or([
          { username: username },
          { phone: phone }
        ])).get();
        
        if (existUser.data.length > 0) {
          return { code: 400, message: '用户名或手机号已存在' };
        }
        
        const hashedPassword = await bcrypt.hash(password, 10);
        
        const result = await db.collection('t_user').add({
          username,
          password: hashedPassword,
          phone,
          nickname: nickname || username,
          user_type: 1,
          status: 1,
          balance: 0,
          gender: 0,
          create_time: new Date(),
          update_time: new Date(),
          is_deleted: 0,
          pickup_code_enabled: 1,
          _openid: context.OPENID || ''
        });
        
        const token = jwt.sign({ userId: result.id, username }, JWT_SECRET, { expiresIn: '7d' });
        
        return {
          code: 200,
          message: '注册成功',
          data: {
            token,
            userId: result.id,
            username,
            nickname: nickname || username
          }
        };
      }
      
      case 'login': {
        const { username, password } = data;
        
        if (!username || !password) {
          return { code: 400, message: '用户名和密码不能为空' };
        }
        
        const userResult = await db.collection('t_user').where({
          username: username
        }).get();
        
        if (userResult.data.length === 0) {
          return { code: 404, message: '用户不存在' };
        }
        
        const user = userResult.data[0];
        
        if (user.status === 0) {
          return { code: 403, message: '账号已被禁用' };
        }
        
        const isMatch = await bcrypt.compare(password, user.password);
        
        if (!isMatch) {
          return { code: 401, message: '密码错误' };
        }
        
        const token = jwt.sign({ userId: user.id, username: user.username }, JWT_SECRET, { expiresIn: '7d' });
        
        return {
          code: 200,
          message: '登录成功',
          data: {
            token,
            userId: user.id,
            username: user.username,
            nickname: user.nickname,
            avatar: user.avatar,
            user_type: user.user_type,
            balance: user.balance
          }
        };
      }
      
      case 'getUserInfo': {
        const { userId } = data;
        
        if (!userId) {
          return { code: 400, message: '用户ID不能为空' };
        }
        
        const userResult = await db.collection('t_user').doc(userId).get();
        
        if (!userResult.data || userResult.data.length === 0) {
          return { code: 404, message: '用户不存在' };
        }
        
        const user = userResult.data[0];
        delete user.password;
        
        return {
          code: 200,
          message: '获取成功',
          data: user
        };
      }
      
      case 'updateUserInfo': {
        const { userId, updateData } = data;
        
        if (!userId) {
          return { code: 400, message: '用户ID不能为空' };
        }
        
        updateData.update_time = new Date();
        
        await db.collection('t_user').doc(userId).update(updateData);
        
        return {
          code: 200,
          message: '更新成功'
        };
      }
      
      case 'changePassword': {
        const { userId, oldPassword, newPassword } = data;
        
        if (!userId || !oldPassword || !newPassword) {
          return { code: 400, message: '参数不完整' };
        }
        
        const userResult = await db.collection('t_user').doc(userId).get();
        
        if (!userResult.data || userResult.data.length === 0) {
          return { code: 404, message: '用户不存在' };
        }
        
        const user = userResult.data[0];
        const isMatch = await bcrypt.compare(oldPassword, user.password);
        
        if (!isMatch) {
          return { code: 401, message: '原密码错误' };
        }
        
        const hashedPassword = await bcrypt.hash(newPassword, 10);
        
        await db.collection('t_user').doc(userId).update({
          password: hashedPassword,
          update_time: new Date()
        });
        
        return {
          code: 200,
          message: '密码修改成功'
        };
      }
      
      default:
        return { code: 400, message: '无效的操作' };
    }
  } catch (error) {
    console.error('用户操作错误:', error);
    return { code: 500, message: '服务器错误', error: error.message };
  }
};
