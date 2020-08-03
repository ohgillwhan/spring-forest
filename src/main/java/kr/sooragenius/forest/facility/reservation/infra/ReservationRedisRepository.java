package kr.sooragenius.forest.facility.reservation.infra;

import kr.sooragenius.forest.facility.reservation.dto.ReservationRedisDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ReservationRedisRepository{
    private final RedisTemplate redisTemplate;

    @Transactional
    public boolean increaseAndCheckExists(Long facilityId, List<LocalDate> localDates) {
        String execute = "empty";
        try {
            Long begin = System.currentTimeMillis();
            DefaultRedisScript<String> holdScript = new DefaultRedisScript<>();
            holdScript.setLocation(new ClassPathResource("luascript/reservation/increaseReservation.lua"));
            holdScript.setResultType(String.class);

            List<String> keys = Arrays.asList("reserve::" + facilityId);
//
            List<String> dates = localDates.stream().map(item -> item.toString()).collect(Collectors.toList());
//            execute = (String) redisTemplate.execute(holdScript, keys, dates.toArray());

//            System.out.println(redisTemplate);
            redisTemplate.execute(new RedisCallback() {
                @Override
                public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {

                    redisConnection.multi();
                    for(String date : dates) {
                        System.out.println(redisTemplate.opsForHash().getOperations());
                        redisConnection.hashCommands().hIncrBy(("reserve::" + facilityId).getBytes(), date.getBytes() ,1);
                    }
                    return redisConnection.exec();
                }
            });
        }catch(Exception ex) {
            ex.printStackTrace();
        }finally {
        }


        return execute.equals("exists");
    }
}
