package tools;

import java.sql.Types;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

/**
 * Created by Xianfeng
 * E-Mail: null
 * Date: 18-6-22 下午2:51
 * Desc: 自定义类型转换处理类
 */
public class CustomTypeResolver extends JavaTypeResolverDefaultImpl {
    public CustomTypeResolver(){
        super();
        //对TINYINT类型，就直接用Int表示，代替Byte
        typeMap.put(Types.TINYINT, new JdbcTypeInformation("TINYINT",
                new FullyQualifiedJavaType(Integer.class.getName())));
        //不强制转换成BigDecimal for DECIMAL and NUMERIC fields,使用Int,Long 等代替
        properties.setProperty("forceBigDecimals","false");
    }
}
