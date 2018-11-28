package resource;

import org.restlet.data.Form;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Xianfeng
 * Date: 18-6-28 下午7:11
 * Desc: 返回所有用户
 */
public class UsersResource extends ServerResource {
    private Map<String,String> users;

    @Override
    protected void doInit() throws ResourceException {
        users=new ConcurrentHashMap<>();
        users.put("Tom","Tom is a cat");
        users.put("Jerry","Jerry is a rat");
        //每次访问返回的时间都不同，说明resource每次请求都创建新实例
        users.put("mark",new Date().toString());
    }
    @Override
    protected Representation get() throws ResourceException {
        return new JacksonRepresentation<Map>(users);
    }

    //POST(除了定义新的resource也可以在这里返回某个user信息)
    @Override
    protected Representation post(Representation entity) throws ResourceException {
        Form form=new Form(entity);
        String name=form.getFirstValue("name");
        return new StringRepresentation(users.get(name));
    }

    //PUT
    @Override
    protected Representation put(Representation entity) throws ResourceException {
        Form form=new Form(entity);
        String name=form.getFirstValue("name");
        String info=form.getFirstValue("info");
        users.put(name,info);
        return new JacksonRepresentation<Map>(users);
    }


}
