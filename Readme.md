# Katra - Java implementation version

## Usage

`Katra [-h] test`

Test contains: `GreenStart`, `Incremental`

Network contains: `i2` `BtNorthAmerica` `Oteglobe`


## Program directory
```
├── Readme.md                   // help
├── config                      // 配置
│   ├── i2                      // Internet2 config
│   │   ├── rule                // original forwading rules 
│   │   │   └── ...             // rule files
│   │   ├── ruleExp             // the forwarding rules used for test
│   │   │   └── ...             // rule files
│   │   ├── i2.space            // the address space of each device in the i2 network
│   │   ├── i2.topology         // topology file
│   │   └── i2.__.tunnel        // IP tunnel of the i2 network(number indicates the number of tunnels)
│   ├── Oteglobe                // OTEG config
│   │   └── ...                 // similar to above
│   ├── BtNorthAmerica          // BTNA config
│   │   └── ...                 // similar to above
│   └── latency                 // distance and propagation delay between devices
├── main                          
│   └── src
│       ├── jdd                 // java bdd library
│       ├── test                // all tests 
│       ├── verifier            // the core algorithm
│       └── Main.java           // entry
└── .gitignore
```
