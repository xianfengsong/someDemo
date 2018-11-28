package resource;

import org.restlet.representation.Representation;
import org.restlet.resource.*;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Xianfeng
 * Date: 18-6-29 上午10:44
 * Desc: 返回某个用户
 * 访问用户还需要user/user两个resource
 * 一类资源就是url不同就要创建多个resource？有点二
 */
public class UserResource extends ServerResource {
    private Map<String, String> users;
    //url标记的请求参数，可以在doInit方法获得
    private String userName;

    @Override
    protected void doInit() throws ResourceException {
        users = new ConcurrentHashMap<>();
        users.put("Tom", "Tom is a cat");
        users.put("Jerry", "Jerry is a rat");
        //每次访问返回的时间都不同，说明resource每次请求都创建新实例
        users.put("mark", new Date().toString());

        userName = this.getAttribute("name");
    }

    @Get("txt")
    public String getUser() {
        return " user \"" + this.userName + "\" :" + users.get(userName);
    }

    @Delete
    public String deleteUser(){
        users.remove(userName);
        return "success";
    }



}
