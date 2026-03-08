package com.quickerrand.service;

import com.quickerrand.vo.BlacklistedRunnerVO;
import java.util.List;

public interface RunnerBlacklistService {

    void addToBlacklist(Long userId, Long runnerId, String reason);

    void removeFromBlacklist(Long userId, Long runnerId);

    boolean isBlacklisted(Long userId, Long runnerId);

    List<BlacklistedRunnerVO> getBlacklist(Long userId);

    List<Long> getBlacklistedRunnerIds(Long userId);
}
