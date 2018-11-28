package tools;

import static org.mybatis.generator.config.PropertyRegistry.TABLE_USE_COLUMN_INDEXES;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.config.JDBCConnectionConfiguration;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.ModelType;
import org.mybatis.generator.config.SqlMapGeneratorConfiguration;
import org.mybatis.generator.config.TableConfiguration;
import org.springframework.util.StringUtils;

/**
 * author Xianfeng <br/>
 * date 18-9-27 上午11:12 <br/>
 * Desc: 加载用户自定义配置
 */
public class ConfigHelper {

    private static Properties appProps = new Properties();

    public ConfigHelper() {
        String appConfigPath = "";
        try {
            appConfigPath =
                    System.getProperty("user.dir") + "/mybatis/src/main/resources/app.properties";
            appProps.load(new FileInputStream(appConfigPath));
        } catch (IOException e) {
            System.out.println("配置加载失败,configPath:" + appConfigPath);
            e.printStackTrace();
        }
    }

    /**
     * 获得数据库连接配置
     */
    public JDBCConnectionConfiguration getJDBCConnectionConfiguration() {
        JDBCConnectionConfiguration jdbcConn = new JDBCConnectionConfiguration();
        jdbcConn.setConnectionURL(appProps.getProperty("connStr"));
        jdbcConn.setDriverClass(appProps.getProperty("driverClass"));
        jdbcConn.setUserId(appProps.getProperty("dbUser"));
        jdbcConn.setPassword(appProps.getProperty("dbPwd"));
        return jdbcConn;
    }

    /**
     * 获得DO model的配置
     */
    public JavaModelGeneratorConfiguration getJavaModelGeneratorConfiguration() {
        JavaModelGeneratorConfiguration modelConfig = new JavaModelGeneratorConfiguration();
        modelConfig.setTargetPackage(appProps.getProperty("modelPackageName"));
        modelConfig.setTargetProject(appProps.getProperty("defaultProjectPath"));
        modelConfig.addProperty("enableSubPackages", "true");
        modelConfig.addProperty("trimStrings", "true");
        return modelConfig;
    }

    /**
     * 关于Mapper类的配置
     */
    public JavaClientGeneratorConfiguration getJavaClientGeneratorConfiguration() {
        JavaClientGeneratorConfiguration clientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
        clientGeneratorConfiguration.setTargetPackage(appProps.getProperty("mapperPackageName"));
        clientGeneratorConfiguration.setTargetProject(appProps.getProperty("defaultProjectPath"));
        clientGeneratorConfiguration.addProperty("enableSubPackages", "false");
        clientGeneratorConfiguration.setConfigurationType("XMLMAPPER");
        return clientGeneratorConfiguration;
    }

    /**
     * 关于mapper.xml文件的配置
     */
    public SqlMapGeneratorConfiguration getSqlMapGeneratorConfiguration() {
        SqlMapGeneratorConfiguration sqlConfig = new SqlMapGeneratorConfiguration();
        sqlConfig.setTargetPackage(appProps.getProperty("xmlPackageName"));
        sqlConfig.setTargetProject(appProps.getProperty("xmlProjectPath"));
        sqlConfig.addProperty("enableSubPackages", "true");
        return sqlConfig;
    }

    /**
     * 数据库table配置
     */
    public TableConfiguration getTableConfiguration() {
        String dbName = appProps.getProperty("dbName");
        String tableName = appProps.getProperty("tableName");
        String domainObjectName = appProps.getProperty("domainObjectName");
        String onlySelect = appProps.getProperty("onlySelect");
        String mapperName = appProps.getProperty("mapperName");

        Context tableContext = new Context(ModelType.FLAT);

        TableConfiguration tableConfiguration = new TableConfiguration(tableContext);
        tableConfiguration.setSchema(dbName);
        tableConfiguration.setTableName(tableName);
        tableConfiguration.setCountByExampleStatementEnabled(false);
        tableConfiguration.setDeleteByExampleStatementEnabled(false);
        if ("true".equals(onlySelect)) {
            tableConfiguration.setInsertStatementEnabled(false);
            tableConfiguration.setUpdateByExampleStatementEnabled(false);
            tableConfiguration.setUpdateByPrimaryKeyStatementEnabled(false);
            tableConfiguration.setDeleteByPrimaryKeyStatementEnabled(false);
        }
        tableConfiguration.setDomainObjectName(domainObjectName);
        tableConfiguration.setMapperName(mapperName);
        //主键
        GeneratedKey generatedKey = new GeneratedKey("id", "JDBC", true, "");
        tableConfiguration.setGeneratedKey(generatedKey);
        //列名用分隔符包裹
        tableConfiguration.setAllColumnDelimitingEnabled(true);
        tableConfiguration.addProperty(TABLE_USE_COLUMN_INDEXES, "true");
        //处理update_time列(暂时不用)
//        ColumnOverride updateDateCol=new ColumnOverride("update_time");
//        updateDateCol.setTypeHandler("tools.UpdateTimeTypeHandler");
//        tableConfiguration.addColumnOverride(updateDateCol);
        return tableConfiguration;
    }

    /*
     * MyBatis3 	这是默认值
     * 使用这值的时候，MBG会生成兼容MyBatis 3.0或更高版本， 兼容JSE 5.0或更高版本的对象（例如Java模型类和Mapper接口会使用泛型）。 这些生成对象中的"by example"方法将支持几乎不受限制的动态的where子句。 另外，这些生成器生成的Java对象支持JSE 5.0特性，包含泛型和注解。
     * MyBatis3Simple 	这是默认值
     * 使用这值的时候，和上面的MyBatis3类似，但是不会有"by example"一类的方法，只有少量的动态SQL。
     * Ibatis2Java2 	使用这值的时候，MBG会生成兼容iBATIS 2.2.0或更高版本（除了iBATIS 3），还有Java2的所有层次。 这些生成对象中的"by example"方法将支持几乎不受限制的动态的where子句。 这些生成的对象不能100%和原生的Abator或其他的代码生成器兼容。
     * Ibatis2Java5 	使用这值的时候，MBG会生成兼容iBATIS 2.2.0或更高版本（除了iBATIS 3）， 兼容JSE 5.0或更高版本的对象（例如Java模型类和Dao类会使用泛型）。 这些生成对象中的"by example"方法将支持几乎不受限制的动态的where子句。 另外，这些生成器生成的Java对象支持JSE 5.0特性，包含泛型和注解。 这些生成的对象不能100%和原生的Abator或其他的代码生成器兼容。
     */
    public String getRuntime() {
        String targetRuntime = appProps.getProperty("targetRuntime");
        if (StringUtils.isEmpty(targetRuntime)) {
            targetRuntime = "MyBatis3";
        }
        return targetRuntime;
    }
}
