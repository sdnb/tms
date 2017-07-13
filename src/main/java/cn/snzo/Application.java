package cn.snzo;

import cn.snzo.ipsc.ConferenceCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Created by ThomasC on 2017/6/28 0028.
 */
@SpringBootApplication
public class Application implements CommandLineRunner{
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }


    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private ConferenceCreator conferenceCreator;

    @Bean
    public RedisTemplate<String, Object> objectRedisTemplate(){
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Override
    public void run(String... strings) throws Exception {
        conferenceCreator.init();
        while (ConferenceCreator.busAddress == null) {
            Thread.sleep(1000);
        }
        conferenceCreator.createCommander();
    }
}
