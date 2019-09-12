import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.PluginConfiguration;
import tools.ConfigHelper;
import tools.ContextBuilder;

/**
 * Created by Xianfeng <br/>
 * Date: 18-6-22 上午10:10 <br/>
 * Desc: 使用MybatisGenerator生成mapper和DO的工具
 */
public class Application {

    public static void main(String[] args) {

        ConfigHelper configHelper = new ConfigHelper();
        //对应mybatis-config.xml文件中的<plugin>标签
        PluginConfiguration pluginConfig = new PluginConfiguration();
        //支持分页，会增加selectByExampleWithRowbounds方法
        pluginConfig.setConfigurationType("org.mybatis.generator.plugins.RowBoundsPlugin");
        Context context = new ContextBuilder()
                .setId("id")
                .setJdbcConnectionConfiguration(configHelper.getJDBCConnectionConfiguration())
                .setJavaModelGeneratorConfiguration(
                        configHelper.getJavaModelGeneratorConfiguration())
                .setJavaClientGeneratorConfiguration(
                        configHelper.getJavaClientGeneratorConfiguration())
                .setSqlMapGeneratorConfiguration(configHelper.getSqlMapGeneratorConfiguration())
                .addTableConfigurations(configHelper.getTableConfiguration())
                .setTargetRuntime(configHelper.getRuntime())
                .addPluginConfiguration(pluginConfig)
                .build();
        Configuration config = new Configuration();
        config.addContext(context);

        try {
            List<String> warnings = new ArrayList<String>();
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, null, warnings);
            myBatisGenerator.generate(null);
            if (warnings.size() > 0) {
                System.out.println("waring:" + warnings.stream().map(Object::toString)
                        .collect(Collectors.joining("\r\n")));
            }
            System.out.println("---生成成功---");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
