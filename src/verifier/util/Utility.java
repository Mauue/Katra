package verifier.util;

public class Utility {
    public static void main(String[] args) {
    }
    static long[] POWER2;

    public static long[] getPowers2(){
        if (POWER2 == null){
            POWER2 = new long[63];
            for(int i =0;i<63;i++){
                POWER2[i] = 1L <<i;
//                System.out.println(i + " " + POWER2[i]);
            }
        }
        return POWER2;
    }

    /**
     * return the binary representation of num
     * e.g. num = 10, bits = 4, return an array of {0,1,0,1}
     */
    public static int[] calBinRep(long num, int bits)
    {
        if(bits == 0) return new int[0];

        int [] binrep = new int[bits];
        long numtemp = num;
        for(int i = bits; i >0; i--)
        {
            long abit = numtemp & getPowers2()[i - 1];
            if(abit == 0)
            {
                binrep[i - 1] = 0;
            }else
            {
                binrep[i - 1] = 1;
            }
            numtemp = numtemp - abit;
        }
        return binrep;
    }

    public static int[] calBin(long num, int bits)
    {
        if(bits == 0) return new int[0];

        int [] binrep = new int[bits];
        long numtemp = num;
        for(int i = bits; i >0; i--)
        {
            long abit = numtemp & getPowers2()[i - 1];
            if(abit == 0)
            {
                binrep[i - 1] = 0;
            }else
            {
                binrep[i - 1] = 1;
            }
            numtemp = numtemp - abit;
        }
        return binrep;
    }

    /**
     * 将 ip 字符串转换为 int 类型的数字
     * <p>
     * 思路就是将 ip 的每一段数字转为 8 位二进制数，并将它们放在结果的适当位置上
     *
     * @param ipString ip字符串，如 127.0.0.1
     * @return ip字符串对应的 int 值
     */
    public static long ip2Int(String ipString) {
        // 取 ip 的各段
        String[] ipSlices = ipString.split("\\.");
        long rs = 0;
        for (int i = 0; i < ipSlices.length; i++) {
            // 将 ip 的每一段解析为 int，并根据位置左移 8 位
            int intSlice = Integer.parseInt(ipSlices[i]) << (8 * (ipSlices.length - i -1));
            // 求与
            rs = rs | intSlice;
        }
        return rs;
    }
}
