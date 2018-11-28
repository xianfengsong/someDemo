package crud;

import com.mongodb.Mongo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

@Configuration
public class AppConfig {

    public @Bean MongoDbFactory mongoDbFactory() throws Exception {
        Mongo mongo=new Mongo("localhost",9002);
        //认证信息
        UserCredentials userCredentials = new UserCredentials("admin", "admin");
        return new SimpleMongoDbFactory(mongo,
                "test",
                userCredentials);
    }

    public @Bean
    MongoTemplate mongoTemplate() throws Exception {
        //线程安全
        return new MongoTemplate(mongoDbFactory());
    }
}
