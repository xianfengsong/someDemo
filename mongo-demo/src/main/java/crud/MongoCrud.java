package crud;

import com.mongodb.WriteConcern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

public class MongoCrud {

    private static MongoTemplate mongoTemplate;


    public static void main(String []args){
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        MongoOperations mongoOps= (MongoOperations) context.getBean("mongoTemplate");
        //WriteConcern : 自定义写入操作的返回行为
        ((MongoTemplate) mongoOps).setWriteConcern(WriteConcern.NORMAL);

        Person person=new Person("Jack",20);

        //create
        mongoOps.insert(person);

        //read
        Person result =  mongoOps.findOne(new Query(Criteria.where("name").is("Jack")),Person.class);
        System.out.println("已插入："+result);

        //update
        mongoOps.updateFirst(new Query(Criteria.where("name").is("Jack")),
                Update.update("age",25),
                Person.class);
        System.out.println("更新为："+mongoOps.findById(person.getId(),Person.class));

        //delete
        mongoOps.remove(person);
        System.out.println("删除后人数："+mongoOps.findAll(Person.class).size());

        mongoOps.dropCollection(Person.class);
    }
}
