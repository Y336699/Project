package com.qian.usercenter.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qian.usercenter.model.domain.User;
import com.qian.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class PreUserInfoRedis {
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    UserService userService;
    @Resource
    RedissonClient redissonClient;
    private List<Long> ImportantUserId = Arrays.asList(1L);
    @Scheduled(cron = "0 3 0 * * *")
    public void preUserInfoRedis() {
        RLock lock = redissonClient.getLock("redisson:precahe:lock");
        try {
            lock.tryLock(0,30000,TimeUnit.MILLISECONDS);
            for (Long id: ImportantUserId) {
                String userKey = String.format("yupao:usercenter:recommend:%s", id);
                ValueOperations<String, Object> stringObjectValueOperations = redisTemplate.opsForValue();
                QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
                Page<User> page = userService.page(new Page<>(1, 8), userQueryWrapper);
                try{
                    stringObjectValueOperations.set(userKey,page,30000, TimeUnit.MILLISECONDS);
                }catch (Exception e) {
                    log.error("预热缓存存储错误",e);
                }
            }
        } catch (InterruptedException e) {
           log.error("error",e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
