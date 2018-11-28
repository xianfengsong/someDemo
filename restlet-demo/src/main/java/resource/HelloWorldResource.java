package resource;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import java.io.Serializable;

/**
 * Created by Xianfeng
 * E-Mail: songxianfeng@
 * Date: 18-6-28 下午4:53
 * Desc:
 */
public class HelloWorldResource extends ServerResource {
    //设置media类型为文档
    @Get("txt")
    public String represent(){
        return "hello world";
    }
}
