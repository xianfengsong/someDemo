import org.restlet.*;
import org.restlet.data.Protocol;
import org.restlet.resource.Finder;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;
import resource.HelloWorldResource;
import resource.UserResource;
import resource.UsersResource;

/**
 * Created by Xianfeng
 * E-Mail: songxianfeng@
 * Date: 18-6-28 下午4:43
 * Desc:
 */
public class FirstApplication extends Application {
    @Override
    public Restlet createInboundRoot() {
        Router router = new Router(getContext());
        router.attach("/hello", HelloWorldResource.class);
        //todo finder怎么用
        Finder finder=new Finder(){
            @Override
            public ServerResource find(Request request, Response response) {
                return super.find(request, response);
            }
        };
        router.attach("/user", UsersResource.class);
        router.attach("/user/{name}", UserResource.class);
        return router;
    }

    public static void main(String[] args) {
        try {

            Component component = new Component();

            component.getServers().add(Protocol.HTTP, 8182);

            component.getDefaultHost().attach("/firstApp", new FirstApplication());

            component.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
