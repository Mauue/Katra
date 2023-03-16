import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import test.GreenStartTest;
import test.IncrementalTest;

public class Main {
    public static void main(String[] args){
        ArgumentParser parser = ArgumentParsers
                .newFor("Katra").build()
                .defaultHelp(true)
                .description("Test contains: GreenStart, Incremental");

        parser.addArgument("test").required(true).type(String.class).help("test name");
        if (args.length < 2){
            parser.printHelp();
            return;
        }
        String [] _args = new String[args.length-1];
        System.arraycopy(args, 1, _args, 0, _args.length);
        try {
            Namespace namespace = parser.parseArgs(new String[]{args[0]});
            String test = namespace.getString("test");
            if (test.equals("GreenStart")){
                GreenStartTest.greenStartEntry(_args);
            }else if( test.equals("Incremental")){
                IncrementalTest.incrementalEntry(_args);
            }
        }catch (Exception e){
            System.out.println("Error args!");
        }
    }
}
