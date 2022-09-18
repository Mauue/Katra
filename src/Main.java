import edu.sysu.pmglab.commandParser.CommandGroup;
import edu.sysu.pmglab.commandParser.CommandItem;
import edu.sysu.pmglab.commandParser.CommandParser;
import parser.MainParser;
import test.GreenStartTest;
import test.IncrementalTest;

import java.util.Arrays;

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
        System.out.println(Arrays.toString(args));
        switch (runner){
            case "Greenstart":{
                GreenStartTest.instance
                        .setNetwork(options.network.value)
                        .setTimes(options.times.value)
                        .isReadTunnelFile(options.tunnel.value)
                        .setTunnelNumber(options.tunnelNum.value)
                        .greenStart();
                break;
            }
            case "Incremental":{
                IncrementalTest.instance
                        .setNetwork(options.network.value)
                        .isReadTunnelFile(options.tunnel.value)
                        .setTunnelNumber(options.tunnelNum.value)
                        .test();
                break;
            }
        }
    }
}
