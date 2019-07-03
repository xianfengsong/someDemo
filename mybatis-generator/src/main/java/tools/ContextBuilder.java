package tools;

import static org.mybatis.generator.config.PropertyRegistry.CONTEXT_BEGINNING_DELIMITER;
import static org.mybatis.generator.config.PropertyRegistry.CONTEXT_ENDING_DELIMITER;

import java.util.ArrayList;
import java.util.List;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.config.CommentGeneratorConfiguration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JDBCConnectionConfiguration;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.JavaTypeResolverConfiguration;
import org.mybatis.generator.config.ModelType;
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.config.SqlMapGeneratorConfiguration;
import org.mybatis.generator.config.TableConfiguration;

/**
 * Created by Xianfeng
 * E-Mail: null
 * Date: 18-6-22 下午2:10
 * Desc: 生成构建需要的context
 */
public class ContextBuilder {

    private String id;
    private ModelType modelType;
    private JDBCConnectionConfiguration jdbcConnectionConfiguration;
    private SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration;
    private JavaTypeResolverConfiguration javaTypeResolverConfiguration;
    private JavaModelGeneratorConfiguration javaModelGeneratorConfiguration;
    private JavaClientGeneratorConfiguration javaClientGeneratorConfiguration;
    private ArrayList<TableConfiguration> tableConfigurations = new ArrayList<>();
    private CommentGeneratorConfiguration commentGeneratorConfiguration;
    private List<PluginConfiguration> pluginConfigurations = new ArrayList<>();
    private String targetRuntime;
    /**
     * 这几项要通过addProperty添加
     */
    private String beginDelimiter, endDelimiter;
    private String javaFormatter;
    private String xmlFormatter;
    private String introspectedColumnImpl;
    private List<IntrospectedTable> introspectedTables;


    /**
     * 在这里添加默认配置
     */
    public ContextBuilder() {
        //列名的分隔符，防止关键字做列名
        this.beginDelimiter = "`";
        this.endDelimiter = "`";
        //FLAT配置每一张表只生成一个实体类。这个实体类包含表中的所有字段。
        this.modelType = ModelType.FLAT;
        //去掉默认注释
        CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();
        commentGeneratorConfiguration.addProperty("suppressAllComments", "true");
        commentGeneratorConfiguration.addProperty("suppressDate", "true");
        this.commentGeneratorConfiguration = commentGeneratorConfiguration;
        //自定义类型转换处理类
        JavaTypeResolverConfiguration typeResolverConfig = new JavaTypeResolverConfiguration();
        typeResolverConfig.setConfigurationType("tools.CustomTypeResolver");
        this.javaTypeResolverConfiguration = typeResolverConfig;
        //ExampleClass重命名
        PluginConfiguration pluginConfiguration = new PluginConfiguration();
        pluginConfiguration
                .setConfigurationType("org.mybatis.generator.plugins.RenameExampleClassPlugin");
        pluginConfiguration.addProperty("searchString", "Example$");
        pluginConfiguration.addProperty("replaceString", "Criteria");
        this.pluginConfigurations.add(pluginConfiguration);
    }

    public Context build() {
        final Context context = new Context(modelType);
        context.setId(id);
        context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);
        context.setJavaTypeResolverConfiguration(javaTypeResolverConfiguration);
        context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);
        context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);
        context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);
        context.setCommentGeneratorConfiguration(commentGeneratorConfiguration);
        context.setTargetRuntime(targetRuntime);
        context.addProperty(CONTEXT_BEGINNING_DELIMITER, beginDelimiter);
        context.addProperty(CONTEXT_ENDING_DELIMITER, endDelimiter);
        tableConfigurations.forEach(context::addTableConfiguration);
        pluginConfigurations.forEach(context::addPluginConfiguration);
        return context;
    }

    private void addDefaultProperties() {

    }

    public ContextBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public ContextBuilder setModelType(ModelType modelType) {
        this.modelType = modelType;
        return this;
    }

    public ContextBuilder setJdbcConnectionConfiguration(JDBCConnectionConfiguration jdbcConnectionConfiguration) {
        this.jdbcConnectionConfiguration = jdbcConnectionConfiguration;
        return this;
    }

    public ContextBuilder setSqlMapGeneratorConfiguration(SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration) {
        this.sqlMapGeneratorConfiguration = sqlMapGeneratorConfiguration;
        return this;
    }

    public ContextBuilder setJavaTypeResolverConfiguration(
            JavaTypeResolverConfiguration javaTypeResolverConfiguration) {
        this.javaTypeResolverConfiguration = javaTypeResolverConfiguration;
        return this;
    }

    public ContextBuilder setJavaModelGeneratorConfiguration(
            JavaModelGeneratorConfiguration javaModelGeneratorConfiguration) {
        this.javaModelGeneratorConfiguration = javaModelGeneratorConfiguration;
        return this;
    }

    public ContextBuilder setJavaClientGeneratorConfiguration(
            JavaClientGeneratorConfiguration javaClientGeneratorConfiguration) {
        this.javaClientGeneratorConfiguration = javaClientGeneratorConfiguration;
        return this;
    }

    public ContextBuilder addTableConfigurations(TableConfiguration tableConfiguration) {
        this.tableConfigurations.add(tableConfiguration);
        return this;
    }


    public ContextBuilder setCommentGeneratorConfiguration(
            CommentGeneratorConfiguration commentGeneratorConfiguration) {
        this.commentGeneratorConfiguration = commentGeneratorConfiguration;
        return this;
    }

    public ContextBuilder addPluginConfiguration(PluginConfiguration pluginConfiguration) {
        this.pluginConfigurations.add(pluginConfiguration);
        return this;
    }

    public ContextBuilder setIntrospectedColumnImpl(String introspectedColumnImpl) {
        this.introspectedColumnImpl = introspectedColumnImpl;
        return this;
    }

    public ContextBuilder setIntrospectedTables(List<IntrospectedTable> introspectedTables) {
        this.introspectedTables = introspectedTables;
        return this;
    }

    public ContextBuilder setTargetRuntime(String targetRuntime) {
        this.targetRuntime = targetRuntime;
        return this;
    }
}
