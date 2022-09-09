import edu.sysu.pmglab.commandParser.CommandGroup;
import edu.sysu.pmglab.commandParser.CommandItem;
import edu.sysu.pmglab.commandParser.CommandParser;
import parser.MainParser;
import test.GreenStartTest;

public class Main {
    public static void main(String[] args) {
        if(args.length < 1){
            System.out.println(MainParser.usage());
            return;
        }
        String runner = args[0];
        MainParser options = MainParser.parse(args);

        if (options.help.isPassedIn) {
            System.out.println(MainParser.getParser());
            return;
        }

        switch (runner){
            case "greenstart":{
                GreenStartTest.instance
                        .setNetwork(options.network.value)
                        .setTimes(options.times.value)
                        .isReadTunnelFile(options.tunnel.value)
                        .setTunnelNumber(options.tunnelNum.value)
                        .greenStart();
            };
        }
    }
}
