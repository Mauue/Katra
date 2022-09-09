package parser;

import edu.sysu.pmglab.commandParser.CommandGroup;
import edu.sysu.pmglab.commandParser.CommandOption;
import edu.sysu.pmglab.commandParser.CommandOptions;
import edu.sysu.pmglab.commandParser.CommandParser;
import edu.sysu.pmglab.commandParser.usage.DefaultStyleUsage;
import edu.sysu.pmglab.container.File;
import edu.sysu.pmglab.commandParser.types.*;

import java.io.IOException;
import java.util.*;

import static edu.sysu.pmglab.commandParser.CommandRule.*;
import static edu.sysu.pmglab.commandParser.CommandItem.*;

public class MainParser {
    /**
     * build by: CommandParser-1.1
     * time: 2022-09-09 15:34:58
     */
    private static final CommandParser PARSER = new CommandParser(false);

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                     Parse parameters and initialize variables
     * After calling parser.parse($args) to parse the parameters, the program will return an instance of CommandOptions.
     * CommandOptions has the following three API methods:
     * options.isPassedIn($commandName)          : Whether the command item is passed in (or captured) or not.
     * options.get($commandName)                 : Get the converted value of the passed parameter, please note that the
     *                                             type of the returned value is Object, which needs to be formatted by
     *                                             users.
     * options.getMatchedParameter($commandName) : Get the original string parameter of this command item.
     *
     * CommandOption is a wrapper class for parsing options, it has three properties (isPassedIn, value, matchedParameter),
     * which corresponding to the results of the above three method calls. CommandOption automatically generates variable
     * names with the name of the main command item, and uses the correct format type as a paradigm, and thus no additional
     * format conversion is required by users.
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private final CommandOptions options;
    public final CommandOption<?> help;
    public final CommandOption<String> network;
    public final CommandOption<Integer> times;
    public final CommandOption<Boolean> tunnel;
    public final CommandOption<Integer> tunnelNum;

    MainParser(String... args) {
        this.options = PARSER.parse(args);
        this.help = new CommandOption<>("--help", this.options);
        this.network = new CommandOption<>("--network", this.options);
        this.times = new CommandOption<>("--times", this.options);
        this.tunnel = new CommandOption<>("--tunnel", this.options);
        this.tunnelNum = new CommandOption<>("--tunnel_num", this.options);
    }

    public static MainParser parse(String... args) {
        return new MainParser(args);
    }

    public static MainParser parse(File argsFile) throws IOException {
        return new MainParser(CommandParser.readFromFile(argsFile));
    }

    /**
     * Get CommandParser
     */
    public static CommandParser getParser() {
        return PARSER;
    }

    /**
     * Get the usage of CommandParser
     */
    public static String usage() {
        return PARSER.toString();
    }

    /**
     * Get CommandOptions
     */
    public CommandOptions getOptions() {
        return this.options;
    }

    static {
        /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
         *                                          Initialize Command Parser
         * program name    : Program name shown in the User Guide.
         *                   default: <main class>
         * offset          : When the input parameter list has mandatory fields, the 'offset' can be used to skip these
         *                   fields.
         *                   e.g., when offset=2, "bgzip compress --input ..." will start parsing from '--input ...'
         *                   default: 0
         * debug           : In debug mode, the commandParser's work log will be printed to the terminal and the stack
         *                   ERROR will be output in detail to help developers troubleshoot errors. In addition,
         *                   command items marked with 'DEBUG' will also be parsed.
         *                   Note that in non-debug mode, command items marked with 'DEBUG' are treated as regular
         *                   parameter values, but not parameter keys. Therefore, the parsing results may be different
         *                   in different modes.
         *                   default: false
         * usingAt         : For parameters starting with @, the program will recognize the content after it as a file
         *                   (i.e., @<file>), and these parameters will be replaced by the text inside the file.
         *                   default: true
         * max matched num : Control the maximum number of the matched command items. The remaining parameters exceeding
         *                   this number will be regarded as the parameters of the last matched command item.
         *                   default: -1 (means no limitation)
         * usage style     : User Guide in Unix-style. The parameters of the 'DefaultStyleUsage' are used to assign the
         *                   display style of the User Guide. The IUsage interface can be inherited to implement
         *                   customized styles.
         * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
        PARSER.setProgramName("<main class>");
        PARSER.offset(1);
        PARSER.debug(true);
        PARSER.usingAt(true);
        PARSER.setMaxMatchedNum(-1);
        PARSER.setAutoHelp(false);
        PARSER.setUsageStyle(DefaultStyleUsage.UNIX_TYPE_1);


        /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
         *                                              Add Command Items
         * CommandParser organizes multiple command items by "groups". Command items of the same group have the same
         * purpose (e.g. input, output, functional, complementary) or other customized types.
         * commandParser
         *    -- commandGroup 1
         *       -- commandItem 1
         *       -- commandItem 2
         *       -- ...
         *    -- commandGroup 2
         *       -- commandItem 1
         *       -- commandItem 2
         *       -- ...
         *
         * First, use the 'parser.addCommandGroup($GroupName)' statement to create a command group named $GroupName.
         * Next, use the 'parser.register' statement to add command item(s) to the most recently registered command
         * group. We can also use the returned value of the addCommandGroup to add the command item(s) to a specified
         * command group precisely.
         *
         * group.register(IType type, String... commandNames)
         * type         : Type of the parsed value of the current command item. CommandParser sets 10 basic types of
         *                parameters, including NONE, BOOLEAN, BYTE, SHORT, INTEGER, LONG, FLOAT, DOUBLE, STRING, FILE.
         *                On the basis of these basic types, commandParser has deduced other 16 new types.
         * commandNames : The command name of the corresponding command item. The first name is set as the main name of
         *                the command item, and the subsequent names are used as alias names.
         *
         * The returned value of group.register or parser.register is the command item itself, so users can use the
         * chain call to set the property of the command. For example:
         * group.register(FILE.VALUE, "--build", "-b")
         *      .arity(1)
         *      .addOptions(REQUEST)
         *      .defaultTo("./example/assoc.hg19.vcf.gz")
         *      .validateWith(FILE.validateWith(true, true, true));
         * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
        CommandGroup group1 = PARSER.addCommandGroup("Options");
        group1.register(IType.NONE, "--help", "-help", "-h")
                .addOptions(HELP, HIDDEN);
        group1.register(STRING.VALUE, "--network", "-n")
                .addOptions(REQUEST)
                .setFormat("-n <string>")
                .setDescription("network name");
        group1.register(INTEGER.VALUE, "--times", "-t")
                .defaultTo("1")
                .validateWith(INTEGER.validateWith(1))
                .setFormat("-t <int>")
                .setDescription("the running times of Green start");
        group1.register(BOOLEAN.VALUE, "--tunnel")
                .defaultTo("true")
                .setDescription("read or not read tunnel file");
        group1.register(INTEGER.VALUE, "--tunnel_num")
                .defaultTo("50")
                .setDescription("tunnel number");
    }
}