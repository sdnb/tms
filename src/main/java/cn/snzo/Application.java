package cn.snzo;

import cn.snzo.ipsc.Main2;
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


    @Bean
    public RedisTemplate<String, Object> objectRedisTemplate(){
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Override
    public void run(String... strings) throws Exception {
//        Main2.init();
//        Main2.makeCommander();

//        Main2.init();
//        Main2.makeCommander();
//        IpscServiceImpl.init();
//        IpscServiceImpl.createCommander();
        Main2.excute();
    }
}
