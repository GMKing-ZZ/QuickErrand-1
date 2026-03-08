package com.quickerrand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.quickerrand.entity.RunnerBlacklist;
import com.quickerrand.entity.RunnerInfo;
import com.quickerrand.entity.User;
import com.quickerrand.exception.BusinessException;
import com.quickerrand.mapper.RunnerBlacklistMapper;
import com.quickerrand.mapper.RunnerInfoMapper;
import com.quickerrand.service.RunnerBlacklistService;
import com.quickerrand.service.UserService;
import com.quickerrand.vo.BlacklistedRunnerVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RunnerBlacklistServiceImpl implements RunnerBlacklistService {

    @Autowired
    private RunnerBlacklistMapper runnerBlacklistMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private RunnerInfoMapper runnerInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addToBlacklist(Long userId, Long runnerId, String reason) {
        User runner = userService.getById(runnerId);
        if (runner == null) {
            throw new BusinessException("跑腿员不存在");
        }
        if (runner.getUserType() == null || runner.getUserType() != 2) {
            throw new BusinessException("该用户不是跑腿员");
        }
        if (userId.equals(runnerId)) {
            throw new BusinessException("不能拉黑自己");
        }

        LambdaQueryWrapper<RunnerBlacklist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RunnerBlacklist::getUserId, userId)
               .eq(RunnerBlacklist::getRunnerId, runnerId);
        Long count = runnerBlacklistMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("该跑腿员已在黑名单中");
        }

        RunnerBlacklist blacklist = new RunnerBlacklist();
        blacklist.setUserId(userId);
        blacklist.setRunnerId(runnerId);
        blacklist.setReason(reason);
        runnerBlacklistMapper.insert(blacklist);

        log.info("用户拉黑跑腿员成功，用户ID：{}，跑腿员ID：{}", userId, runnerId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFromBlacklist(Long userId, Long runnerId) {
        LambdaQueryWrapper<RunnerBlacklist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RunnerBlacklist::getUserId, userId)
               .eq(RunnerBlacklist::getRunnerId, runnerId);
        int deleted = runnerBlacklistMapper.delete(wrapper);
        if (deleted == 0) {
            throw new BusinessException("该跑腿员不在黑名单中");
        }

        log.info("用户取消拉黑跑腿员成功，用户ID：{}，跑腿员ID：{}", userId, runnerId);
    }

    @Override
    public boolean isBlacklisted(Long userId, Long runnerId) {
        LambdaQueryWrapper<RunnerBlacklist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RunnerBlacklist::getUserId, userId)
               .eq(RunnerBlacklist::getRunnerId, runnerId);
        Long count = runnerBlacklistMapper.selectCount(wrapper);
        return count != null && count > 0;
    }

    @Override
    public List<BlacklistedRunnerVO> getBlacklist(Long userId) {
        LambdaQueryWrapper<RunnerBlacklist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RunnerBlacklist::getUserId, userId)
               .orderByDesc(RunnerBlacklist::getCreateTime);
        List<RunnerBlacklist> blacklists = runnerBlacklistMapper.selectList(wrapper);

        List<BlacklistedRunnerVO> result = new ArrayList<>();
        for (RunnerBlacklist blacklist : blacklists) {
            BlacklistedRunnerVO vo = new BlacklistedRunnerVO();
            vo.setId(blacklist.getId());
            vo.setRunnerId(blacklist.getRunnerId());
            vo.setReason(blacklist.getReason());
            vo.setCreateTime(blacklist.getCreateTime());

            User runner = userService.getById(blacklist.getRunnerId());
            if (runner != null) {
                vo.setRunnerName(runner.getNickname());
                vo.setRunnerAvatar(runner.getAvatar());
                vo.setRunnerPhone(runner.getPhone());
            }

            RunnerInfo runnerInfo = runnerInfoMapper.selectOne(
                new LambdaQueryWrapper<RunnerInfo>().eq(RunnerInfo::getUserId, blacklist.getRunnerId())
            );
            if (runnerInfo != null) {
                vo.setCreditLevel(runnerInfo.getCreditLevel());
                vo.setTotalOrders(runnerInfo.getTotalOrders());
                vo.setGoodRate(runnerInfo.getGoodRate());
            }

            result.add(vo);
        }

        return result;
    }

    @Override
    public List<Long> getBlacklistedRunnerIds(Long userId) {
        LambdaQueryWrapper<RunnerBlacklist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RunnerBlacklist::getUserId, userId)
               .select(RunnerBlacklist::getRunnerId);
        List<RunnerBlacklist> blacklists = runnerBlacklistMapper.selectList(wrapper);
        return blacklists.stream()
                .map(RunnerBlacklist::getRunnerId)
                .collect(Collectors.toList());
    }
}
